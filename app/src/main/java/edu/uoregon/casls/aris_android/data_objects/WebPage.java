package edu.uoregon.casls.aris_android.data_objects;

import edu.uoregon.casls.aris_android.Utilities.AppConfig;

/**
 * Created by smorison on 8/19/15.
 */
public class WebPage {
	public int    web_page_id   = 0;
	public String name          = "WebPage";
	public String url           = "http://arisgames.org";
	public int    icon_media_id = 0;

	public int media_id = 0;
	public int back_button_enabled;

	public WebPage copy() {
		WebPage c = new WebPage();
		c.web_page_id = this.web_page_id;
		c.name = this.name;
		c.url = this.url;
		c.icon_media_id = this.icon_media_id;
		c.back_button_enabled = this.back_button_enabled;
		return c;
	}

	public boolean compareTo(WebPage ob) {
		return (ob.web_page_id == this.web_page_id);
	}

	public String description() {
		return "WebPage- Id:" + this.web_page_id + "\tName:" + this.name + "\tURL:" + this.url + "\t";
	}

	public int icon_media_id() {
		if (icon_media_id == 0) return Media.DEFAULT_WEB_PAGE_ICON_MEDIA_ID;
		return icon_media_id;
	}


}
