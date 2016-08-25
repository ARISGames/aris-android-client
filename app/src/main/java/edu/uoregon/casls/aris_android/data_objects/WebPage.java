package edu.uoregon.casls.aris_android.data_objects;

/**
 * Created by smorison on 8/19/15.
 */
public class WebPage implements InstantiableProtocol {
	public long web_page_id   = 0;
	public long icon_media_id = 0;
	public long media_id      = 0;

	public String name = "WebPage";
	public String url  = "http://arisgames.org";

	public long back_button_enabled; // Boolean as long

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

	public long icon_media_id() {
		if (icon_media_id == 0) return Media.DEFAULT_WEB_PAGE_ICON_MEDIA_ID;
		return icon_media_id;
	}
}
