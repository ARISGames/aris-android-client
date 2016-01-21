package edu.uoregon.casls.aris_android.data_objects;

/**
 * Created by smorison on 10/13/15.
 */
public class EventPackage {

	public long event_package_id = 0;
	public String name = ""; // dummy var to placate Instance.name() function - Android only
	public long icon_media_id = 0; // dummy var to placate Instance.icon_media_id() function - Android only

	public EventPackage copy() {
		EventPackage o = new EventPackage();
		o.event_package_id = this.event_package_id;
		return o;
	}

	public boolean compareTo(EventPackage ob) {
		if (ob.event_package_id == this.event_package_id)
			return true;
		return false;
	}

	public String name() {
		return "Event";
	}

	public long icon_media_id() {
		return icon_media_id;
	}

	public String description() {
		return "EventPackage- Id:" + this.event_package_id;
	}

}
