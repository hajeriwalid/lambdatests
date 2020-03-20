package entities;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@SuppressWarnings("serial")
public class SimpleSyncpTag implements Serializable {
	public UUID Id;

	public String type = "usertag";
	
	public String name;
}