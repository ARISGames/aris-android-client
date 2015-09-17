package edu.uoregon.casls.aris_android.models;

import java.util.LinkedHashMap;
import java.util.Map;

import edu.uoregon.casls.aris_android.data_objects.ObjectTag;
import edu.uoregon.casls.aris_android.data_objects.Tag;

/**
 * Created by smorison on 8/20/15.
 */
public class TagsModel {

	public Map<Long, Tag> tags = new LinkedHashMap<>();
	public Map<Long, ObjectTag> objectTags = new LinkedHashMap<>();

	public void clearGameData() {

	}

	public void requestTags() {

	}
}
