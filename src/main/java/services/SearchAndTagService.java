package services;

import entities.File;
import entities.SimpleSyncpTag;
import entities.StorageEndpoint;
import entities.SyncPoint;
import entities.User;
import util.APIGateway;
import util.ConfigurationHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * A service for searching for file and tagging files
 * @author Walid Hajeri
 */
public class SearchAndTagService extends APIGateway {


	protected static String basesearchUrl;

	static {
		basesearchUrl = ConfigurationHelper.getBaseSearchEndpointUrl() + "api/v1.1/search?";
	}

	/**
	 * Retrieves search Result and store most important info in a File
	 * 
	 */
	public static File getSearchResults(String filenameToSearch) {

		String queryappend = "q='" + filenameToSearch + "'";

		String searchResultRaw = httpGet(basesearchUrl + queryappend, String.class, false);

		JSONObject obj = new JSONObject(searchResultRaw);

		JSONArray myJsonArray = obj.getJSONArray("Files");
		List<Object> myArrayList = myJsonArray.toList();
		Map<String, Object> myMap = (Map<String, Object>) myArrayList.get(0); // normally we should iterate

		/*
		 * System.out.println("My file name  is : " + myMap.get("Name"));
		 * 
		 * System.out.println("My file id  is : " + myMap.get("Id"));
		 * 
		 * System.out.println("My Syncpoint id  is : " + myMap.get("SyncpointId"));
		 */
		
		File myfoundfile = new File();
		
		myfoundfile.Filename = (String) myMap.get("Name");
		myfoundfile.FileId = (long) myMap.get("Id");
		myfoundfile.SyncpointId = (int) myMap.get("SyncpointId");

		return myfoundfile;
	}

	
	public static void postTag(File filetoTag, String tag) {
		
		String taggingUrl = "https://api.syncplicity.com/syncpoint/" 
		+ Integer.toString(filetoTag.SyncpointId) 
		+ "/file/" 
		+ Long.toString(filetoTag.FileId) 
		+ "/tags";
		
		
		SimpleSyncpTag mysyncptag = new SimpleSyncpTag();
		mysyncptag.name = tag;
		
		//SimpleSyncpTag[] arrayoftags = new SimpleSyncpTag[1];
		
		ArrayList<SimpleSyncpTag> arrayoftags = new ArrayList<SimpleSyncpTag>();
		arrayoftags.add(mysyncptag);
		
		httpPost(taggingUrl, "application/json", arrayoftags);
		 
	}
	 

}
