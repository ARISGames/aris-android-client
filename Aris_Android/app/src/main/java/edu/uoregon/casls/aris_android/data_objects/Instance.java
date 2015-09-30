package edu.uoregon.casls.aris_android.data_objects;

/**
 * Created by smorison on 8/19/15.
 */
public class Instance {
	public long instance_id;
	public String object_type;
	public long object_id;
	public String owner_type;
	public long owner_id;
	public long qty;
	public Boolean infinite_qty;
	public long factory_id;


	public Item object() {
		return null; // todo stub
	}
}
