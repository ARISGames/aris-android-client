package edu.uoregon.casls.aris_android.data_objects;

/**
 * Created by smorison on 8/19/15.
 */
public class DialogOption {
	public long   dialog_option_id        = 0;
	public long   dialog_id               = 0;
	public long   parent_dialog_script_id = 0;
	public String prompt                  = "";
	public String link_type               = "EXIT";
	public long   link_id                 = 0;
	public String link_info               = "";
	public long   sort_index              = 0;

	public long requirement_root_package_id = 0;

}
