package edu.uoregon.casls.aris_android.models;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.uoregon.casls.aris_android.GamePlayActivity;
import edu.uoregon.casls.aris_android.data_objects.Dialog;
import edu.uoregon.casls.aris_android.data_objects.DialogCharacter;
import edu.uoregon.casls.aris_android.data_objects.DialogOption;
import edu.uoregon.casls.aris_android.data_objects.DialogScript;

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

	// to be consistent with iOS these are here to do what the listener declarations did in iOS
	public void dialogsReceived(List<Dialog> dialogs) { this.updateDialogs(dialogs); }
	public void dialogOptionsReceived(List<DialogOption> dialogOptions) { this.updateDialogOptions(dialogOptions); }
	public void dialogCharactersReceived(List<DialogCharacter> dialogCharacters) { this.updateDialogCharacters(dialogCharacters);}
	public void dialogScriptsReceived(List<DialogScript> dialogScripts) { this.updateDialogScripts(dialogScripts);}

	public void updateDialogs(List<Dialog> newDialogs) {
		long newDialogId;
		for(Dialog newDialog : newDialogs) {
			newDialogId = newDialog.dialog_id;
			if(!dialogs.containsKey(newDialogId))
				dialogs.put(newDialogId, newDialog); // setObject:newDialog forKey:newDialogId];
		}
		n_game_data_received++;
		mGamePlayAct.mDispatch.model_dialogs_available(); // a call to no one.		_ARIS_NOTIF_SEND_(@"MODEL_DIALOGS_AVAILABLE",nil,nil);
		mGamePlayAct.mDispatch.model_game_piece_available(); //		_ARIS_NOTIF_SEND_(@"MODEL_GAME_PIECE_AVAILABLE",nil,nil);
	}

	public void updateDialogCharacters(List<DialogCharacter> newDialogCharacters) {
		long newDialogCharacterId;
		for(DialogCharacter newDialogCharacter : newDialogCharacters) {
			newDialogCharacterId = newDialogCharacter.dialog_character_id;
			if(dialogCharacters.get(newDialogCharacterId) == null ) dialogCharacters.put(newDialogCharacterId, newDialogCharacter);
		}
		n_game_data_received++;
		mGamePlayAct.mDispatch.model_dialog_characters_available(); //		_ARIS_NOTIF_SEND_(@"MODEL_DIALOG_CHARACTERS_AVAILABLE",nil,nil);
		mGamePlayAct.mDispatch.model_game_piece_available(); //		_ARIS_NOTIF_SEND_(@"MODEL_GAME_PIECE_AVAILABLE",nil,nil);
	}

	public void updateDialogScripts(List<DialogScript> newDialogScripts) {
		long newDialogScriptId;
		for(DialogScript newDialogScript : newDialogScripts) {
			newDialogScriptId = newDialogScript.dialog_script_id;
			if(dialogScripts.get(newDialogScriptId) == null) dialogScripts.put(newDialogScriptId, newDialogScript); // setObject:newDialogScript forKey:newDialogScriptId];
		}
		n_game_data_received++;
		mGamePlayAct.mDispatch.model_dialog_scripts_available(); //		_ARIS_NOTIF_SEND_(@"MODEL_DIALOG_SCRIPTS_AVAILABLE",nil,nil);
		mGamePlayAct.mDispatch.model_game_piece_available(); //		_ARIS_NOTIF_SEND_(@"MODEL_GAME_PIECE_AVAILABLE",nil,nil);
	}

	public void updateDialogOptions(List<DialogOption> newDialogOptions) {
		long newDialogOptionId;
		for(DialogOption newDialogOption : newDialogOptions) {
			newDialogOptionId = newDialogOption.dialog_option_id;
			if (!dialogOptions.containsKey(newDialogOptionId))
				dialogOptions.put(newDialogOptionId, newDialogOption); // setObject:newDialogOption forKey:newDialogOptionId];
		}
		n_game_data_received++;
		mGamePlayAct.mDispatch.model_dialog_options_available(); //		_ARIS_NOTIF_SEND_(@"MODEL_DIALOG_OPTIONS_AVAILABLE",nil,nil);
		mGamePlayAct.mDispatch.model_game_piece_available(); //		_ARIS_NOTIF_SEND_(@"MODEL_GAME_PIECE_AVAILABLE",nil,nil);
	}

	public void requestDialogs() {
		mGamePlayAct.mServices.fetchDialogs(); //[_SERVICES_ fetchDialogs];
		mGamePlayAct.mServices.fetchDialogCharacters(); //[_SERVICES_ fetchDialogCharacters];
		mGamePlayAct.mServices.fetchDialogScripts(); //[_SERVICES_ fetchDialogScripts];
		mGamePlayAct.mServices.fetchDialogOptions(); //[_SERVICES_ fetchDialogOptions];
	}

	public void requestPlayerOptionsForDialogId(long dialog_id, long dialog_script_id) {
		if(!mGamePlayAct.mGame.network_level.contentEquals("REMOTE")) {
			List<DialogOption> pops = new LinkedList<>();
//			NSDictionary *uInfo = @{@"options":pops,
//			@"dialog_id":[NSNumber numberWithLong:dialog_id],
//			@"dialog_script_id":[NSNumber numberWithLong:dialog_script_id]};
			Map<String, Object> uInfo = new HashMap<>();
				uInfo.put("options", pops);
				uInfo.put("dialog_id", dialog_id);
				uInfo.put("dialog_script_id", dialog_script_id);

			Collection<DialogOption> os = dialogOptions.values();
			for (DialogOption o : os) {
				if (o.dialog_id == dialog_id &&
						o.parent_dialog_script_id == dialog_script_id &&
					mGamePlayAct.mGame.requirementsModel.evaluateRequirementRoot(o.requirement_root_package_id));
				pops.add(o); // addObject:o];
			}
			mGamePlayAct.mDispatch.services_player_script_options_received(uInfo); //_ARIS_NOTIF_SEND_(@"SERVICES_PLAYER_SCRIPT_OPTIONS_RECEIVED", nil, uInfo);
		}
		else  mGamePlayAct.mServices.fetchOptionsForPlayerForDialog(dialog_id, dialog_script_id); //_SERVICES_ fetchOptionsForPlayerForDialog:dialog_id script:dialog_script_id];
	}

// null dialog/character/script (id == 0) NOT flyweight!!! (to allow for temporary customization safety)
	public Dialog dialogForId(long dialog_id)
	{
		if (dialog_id == 0) return new Dialog();
		return dialogs.get(dialog_id);
	}

	public DialogCharacter characterForId(long dialog_character_id)
	{
		if (dialog_character_id == 0) return new DialogCharacter();
		return dialogCharacters.get(dialog_character_id);
	}

	public DialogScript scriptForId(long dialog_script_id)
	{
		if (dialog_script_id == 0) return new DialogScript();
		return dialogScripts.get(dialog_script_id);
	}

	public DialogOption optionForId(long dialog_option_id)
	{
		if (dialog_option_id == 0) return new DialogOption();
		return dialogOptions.get(dialog_option_id);// objectForKey:[NSNumber numberWithLong:dialog_option_id]];
	}

}
