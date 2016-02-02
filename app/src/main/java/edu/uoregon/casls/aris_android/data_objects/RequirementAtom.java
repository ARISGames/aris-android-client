package edu.uoregon.casls.aris_android.data_objects;

/**
 * Created by smorison on 10/7/15.
 */
public class RequirementAtom {
	public long   requirement_atom_id        = 0;
	public long   requirement_and_package_id = 0;
	public long   bool_operator              = 0; // Boolean cast as long for 1|0 logic to conform to server json.
	public String requirement                = "";
	public long   content_id                 = 0;
	public long   distance                   = 0;
	public long   qty                        = 0;
	public double latitude                   = 0;
	public double longitude                  = 0;

}
