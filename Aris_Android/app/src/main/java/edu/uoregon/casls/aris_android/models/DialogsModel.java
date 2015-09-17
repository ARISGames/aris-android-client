package edu.uoregon.casls.aris_android.models;

import java.util.LinkedHashMap;
import java.util.Map;

import edu.uoregon.casls.aris_android.data_objects.Dialog;
import edu.uoregon.casls.aris_android.data_objects.DialogCharacter;
import edu.uoregon.casls.aris_android.data_objects.DialogOption;
import edu.uoregon.casls.aris_android.data_objects.DialogScript;

/**
 * Created by smorison on 8/20/15.
 */
public class DialogsModel {

	public Map<Long, Dialog> dialogs = new LinkedHashMap<>();
	public Map<Long, DialogCharacter> dialogCharacters = new LinkedHashMap<>();
	public Map<Long, DialogScript> dialogScript = new LinkedHashMap<>();
	public Map<Long, DialogOption> dialogOptions = new LinkedHashMap<>();

	public void clearGameData() {

	}

	public void requestDialogs() {

	}
}
