package edu.uoregon.casls.aris_android.models;

import java.util.LinkedHashMap;
import java.util.List;
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
	public GamePlayActivity mGamePlayAct;

	public void initContext(GamePlayActivity gamePlayAct) {
		mGamePlayAct = gamePlayAct; // todo: may need leak checking is activity gets recreated.
	}


	public void clearGameData() {
		dialogs.clear();
		dialogCharacters.clear();
		dialogScripts.clear();
		dialogOptions.clear();
		n_game_data_received = 0;
	}

	public void requestDialogs() {

	}

	public void dialogsReceived()          { this.updateDialogs(); }

	public void updateDialogs(List<Dialog> newDialogs)
	{
//		Dialog *newDialog;
		long newDialogId;
//		for(long i = 0; i < newDialogs.count; i++)
		for(Dialog newDialog : newDialogs)
		{
//			newDialog = [newDialogs objectAtIndex:i];
			newDialogId = newDialog.dialog_id;
			if(dialogs.get(newDialogId) == null) dialogs.put(newDialogId, newDialog); // setObject:newDialog forKey:newDialogId];
		}
		n_game_data_received++;
//		_ARIS_NOTIF_SEND_(@"MODEL_DIALOGS_AVAILABLE",nil,nil);
//		_ARIS_NOTIF_SEND_(@"MODEL_GAME_PIECE_AVAILABLE",nil,nil);
	}
	public void updateDialogCharacters(List<DialogCharacter> newDialogCharacters)
	{
		DialogCharacter *newDialogCharacter;
		NSNumber *newDialogCharacterId;
		for(long i = 0; i < newDialogCharacters.count; i++)
		{
			newDialogCharacter = [newDialogCharacters objectAtIndex:i];
			newDialogCharacterId = [NSNumber numberWithLong:newDialogCharacter.dialog_character_id];
			if(![dialogCharacters objectForKey:newDialogCharacterId]) [dialogCharacters setObject:newDialogCharacter forKey:newDialogCharacterId];
		}
		n_game_data_received++;
//		_ARIS_NOTIF_SEND_(@"MODEL_DIALOG_CHARACTERS_AVAILABLE",nil,nil);
//		_ARIS_NOTIF_SEND_(@"MODEL_GAME_PIECE_AVAILABLE",nil,nil);
	}
	public void updateDialogScripts(List<DialogScript> newDialogScripts)
	{
		DialogScript *newDialogScript;
		NSNumber *newDialogScriptId;
		for(long i = 0; i < newDialogScripts.count; i++)
		{
			newDialogScript = [newDialogScripts objectAtIndex:i];
			newDialogScriptId = [NSNumber numberWithLong:newDialogScript.dialog_script_id];
			if(![dialogScripts objectForKey:newDialogScriptId]) [dialogScripts setObject:newDialogScript forKey:newDialogScriptId];
		}
		n_game_data_received++;
//		_ARIS_NOTIF_SEND_(@"MODEL_DIALOG_SCRIPTS_AVAILABLE",nil,nil);
//		_ARIS_NOTIF_SEND_(@"MODEL_GAME_PIECE_AVAILABLE",nil,nil);
	}
	public void updateDialogOptions(List<DialogOption> newDialogOptions)
	{
//		DialogOption *newDialogOption;
		long newDialogOptionId;
//		for(long i = 0; i < newDialogOptions.count; i++)
		for(DialogOption newDialogOption : newDialogOptions)
		{
//			newDialogOption = [newDialogOptions objectAtIndex:i];
			newDialogOptionId = newDialogOption.dialog_option_id;
			if(dialogOptions.get(newDialogOptionId) == null) dialogOptions.put(newDialogOptionId, newDialogOption); // setObject:newDialogOption forKey:newDialogOptionId];
		}
		n_game_data_received++;
//		_ARIS_NOTIF_SEND_(@"MODEL_DIALOG_OPTIONS_AVAILABLE",nil,nil);
//		_ARIS_NOTIF_SEND_(@"MODEL_GAME_PIECE_AVAILABLE",nil,nil);
	}

	public void requestDialogs
	{
		[_SERVICES_ fetchDialogs];
		[_SERVICES_ fetchDialogCharacters];
		[_SERVICES_ fetchDialogScripts];
		[_SERVICES_ fetchDialogOptions];
	}
	public void requestPlayerOptionsForDialogId:(long)dialog_id scriptId:(long)dialog_script_id
	{
		if(![mGamePlayAct.mGame.network_level.contentEquals("REMOTE"))
		{
			NSMutableArray *pops = [[NSMutableArray alloc] init];
			NSDictionary *uInfo = @{@"options":pops,
			@"dialog_id":[NSNumber numberWithLong:dialog_id],
			@"dialog_script_id":[NSNumber numberWithLong:dialog_script_id]};
			NSArray *os = [dialogOptions allValues];
			for(int i = 0; i < os.count; i++)
			{
				DialogOption *o = os[i];
				if(o.dialog_id == dialog_id &&
						o.parent_dialog_script_id == dialog_script_id &&
				mGamePlayAct.mGame.requirementsModel.evaluateRequirementRoot(o.requirement_root_package_id))
				[pops addObject:o];
			}
//			_ARIS_NOTIF_SEND_(@"SERVICES_PLAYER_SCRIPT_OPTIONS_RECEIVED", nil, uInfo);
		}
		else [_SERVICES_ fetchOptionsForPlayerForDialog:dialog_id script:dialog_script_id];
	}

//	public Dialog dialogForId(long object_id) {
//		return dialogs.get(object_id);
//	}

// null dialog/character/script (id == 0) NOT flyweight!!! (to allow for temporary customization safety)
	public Dialog dialogForId(long dialog_id)
	{
		if (dialog_id == 0) return new Dialog();
		return dialogs.get(dialog_id);
	}
//	- (DialogCharacter *) characterForId:(long)dialog_character_id
	public DialogCharacter characterForId(long dialog_character_id)
	{
		if (dialog_character_id == 0) return new DialogCharacter();
		return dialogCharacters.get(dialog_character_id);
	}
//	- (DialogScript *) scriptForId:(long)dialog_script_id
	public DialogScript scriptForId(long dialog_script_id)
	{
		if (dialog_script_id == 0) return new DialogScript();
		return dialogScripts.get(dialog_script_id);
	}
//	- (DialogOption *) optionForId:(long)dialog_option_id
	public DialogOption optionForId(long dialog_option_id)
	{
		if (dialog_option_id == 0) return new DialogOption();
		return dialogOptions.get(dialog_option_id);// objectForKey:[NSNumber numberWithLong:dialog_option_id]];
	}

}
