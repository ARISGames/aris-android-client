package edu.uoregon.casls.aris_android.models;

import java.util.LinkedHashMap;
import java.util.Map;

import edu.uoregon.casls.aris_android.GamePlayActivity;
import edu.uoregon.casls.aris_android.data_objects.Dialog;
import edu.uoregon.casls.aris_android.data_objects.DialogCharacter;
import edu.uoregon.casls.aris_android.data_objects.DialogOption;
import edu.uoregon.casls.aris_android.data_objects.DialogScript;
import edu.uoregon.casls.aris_android.data_objects.Item;

/**
 * Created by smorison on 8/20/15.
 */
public class DialogsModel extends ARISModel {

	public Map<Long, Dialog> dialogs = new LinkedHashMap<>();
	public Map<Long, DialogCharacter> dialogCharacters = new LinkedHashMap<>();
	public Map<Long, DialogScript> dialogScripts = new LinkedHashMap<>();
	public Map<Long, DialogOption> dialogOptions = new LinkedHashMap<>();

	public void clearGameData() {
		dialogs.clear();
		dialogCharacters.clear();
		dialogScripts.clear();
		dialogOptions.clear();
		n_game_data_received = 0;
	}

	public void requestDialogs() {

	}

	public Dialog dialogForId(long object_id) {
		return dialogs.get(object_id);
	}
}
