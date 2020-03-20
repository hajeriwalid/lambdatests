package com.amazonaws.lambda.demo;

import java.util.ArrayList;
import java.util.List;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.DetectLabelsRequest;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.services.rekognition.model.Label;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.event.S3EventNotification.S3Entity;
import com.amazonaws.services.s3.event.S3EventNotification.S3EventNotificationRecord;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectTagging;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.SetObjectTaggingRequest;
import com.amazonaws.services.s3.model.Tag;

import entities.File;
import services.SearchAndTagService;

public class PictureRecognizer implements RequestHandler<S3Event, String> {

    private AmazonS3 s3 = AmazonS3ClientBuilder.standard().build();

    public PictureRecognizer() {}

    // Test purpose only.
    PictureRecognizer(AmazonS3 s3) {
        this.s3 = s3;
    }

    @Override
    public String handleRequest(S3Event event, Context context) {
        context.getLogger().log("Hello Walid 2 - Received event: " + event);
        context.getLogger().log(event.toJson());

        // Get the object from the event and show its content type
        // String bucket = event.getRecords().get(0).getS3().getBucket().getName();
        // String key = event.getRecords().get(0).getS3().getObject().getKey();
        
        
        try {
			/*
			 * S3Object response = s3.getObject(new GetObjectRequest(bucket, key)); String
			 * contentType = response.getObjectMetadata().getContentType();
			 * context.getLogger().log("CONTENT TYPE: " + contentType);
			 */
            
            
            AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder.defaultClient();
            
            
            // For every S3 object
            for (S3EventNotificationRecord myevent : event.getRecords()) {
                S3Entity entity = myevent.getS3();
                String bucketName = entity.getBucket().getName();
                String objectKey = entity.getObject().getKey();
                
                
                /*
                 * FILE NAME
                 */
                String filenameToSearch = entity.getObject().getKey();
                
                File foundfilePrincipal = SearchAndTagService.getSearchResults(filenameToSearch);

                context.getLogger().log("Hello Walid 3 - Lambda could communicate with Syncp API and found the file : " + foundfilePrincipal.Filename);
                
                com.amazonaws.services.rekognition.model.S3Object myRekoObj = new com.amazonaws.services.rekognition.model.S3Object().withBucket(bucketName).withName(objectKey);
                
                Image imageToTag = new Image().withS3Object(myRekoObj);

                // Call Rekognition to identify image labels. 
                //Detects instances of real-world entities within an image (JPEG or PNG) provided as input. This includes objects like flower, tree, and table; events like wedding, graduation, and birthday party; and concepts like landscape, evening, and nature.
                
                DetectLabelsRequest request = new DetectLabelsRequest()
                        .withImage(imageToTag)
                        .withMaxLabels(5)
                        .withMinConfidence(77F);

                try {
                    List<Label> labels = rekognitionClient.detectLabels(request).getLabels();
                    // Add the labels tag to the object
                    List<Tag> newTags = new ArrayList<>();

                    if (labels.isEmpty()) {
                    	context.getLogger().log("No label is recognized!");
                    } else {
                    	context.getLogger().log("Detected labels for " + imageToTag.getS3Object().getName());
                    }
                    for (Label label : labels) {
                    	context.getLogger().log(label.getName() + ": " + label.getConfidence().toString());
                        newTags.add(new Tag(label.getName(), label.getConfidence().toString()));
                       
                        //we post the tag to Syncplicity
                        SearchAndTagService.postTag(foundfilePrincipal, label.getName());
                    }

                    //Not needed as we're not tagging the S3 object itself
                    //s3Client.setObjectTagging(new SetObjectTaggingRequest(
                    //        bucketName, objectKey, new ObjectTagging(newTags)));

                } catch (AmazonServiceException e) {
                    e.printStackTrace();
                }
            }
            

            
           // return contentType;
            return null;
        } catch (Exception e) {
            e.printStackTrace();
         
            throw e;
        }
    }
    
    
}