package edu.uoregon.casls.aris_android.Utilities;

import edu.uoregon.casls.aris_android.GamePlayActivity;
import edu.uoregon.casls.aris_android.data_objects.Game;
import edu.uoregon.casls.aris_android.data_objects.User;

/**
 * Created by smorison on 10/6/15.
 */
public class Services {

	public GamePlayActivity mGamePlayAct;
	public Game mGame;
	public User mPlayer;

	public Services(GamePlayActivity gamePlayActivity) {
		// reference to GamePlayActivity
		mGamePlayAct = gamePlayActivity;
		//convenience references:
		mGame = mGamePlayAct.mGame;
		mPlayer = mGamePlayAct.mPlayer;
	}

	public void fetchInstanceById(long i) {

	}


	public void fetchInstancesForPlayer() {

	}

	public void setQtyForInstanceId(long instance_id, long qty) {

	}

	public void fetchOptionsForPlayerForDialog(long dialog_id, long dialog_script_id) {

	}

	public void fetchRequirementAtoms() {
	}

	public void fetchRequirementAnds() {
	}

	public void fetchRequirementRoots() {

	}

	public void touchItemsForGame() {

	}

	public void dropItem(long item_id, long qty) {

	}

	public void fetchLogsForPlayer() {

	}

	public void logPlayerEnteredGame() {

	}

	public void logPlayerMoved() {

	}

	public void logPlayerViewedTabId(long tab_id) {

	}

	public void logPlayerViewedPlaqueId(long content_id) {
	}

	public void logPlayerViewedItemId(long content_id) {
	}

	public void logPlayerViewedDialogId(long content_id) {
	}

	public void logPlayerViewedDialogScriptId(long content_id) {
	}

	public void logPlayerViewedWebPageId(long content_id) {
	}

	public void logPlayerViewedNoteId(long content_id) {
	}

	public void logPlayerViewedSceneId(long content_id) {
	}

	public void logPlayerViewedInstanceId(long instance_id) {
	}

	public void logPlayerTriggeredTriggerId(long trigger_id) {
	}

	public void logPlayerReceivedItemId(long item_id, long qty) {
	}

	public void logPlayerLostItemId(long item_id, long qty) {
	}

	public void logGameReceivedItemId(long item_id, long qty) {
	}

	public void logGameLostItemId(long item_id, long qty) {
	}

	public void logGroupReceivedItemId(long item_id, long qty) {
	}

	public void logGroupLostItemId(long item_id, long qty) {
	}

	public void logPlayerSetSceneId(long scene_id) {
	}

	public void logPlayerJoinedGroupId(long group_id) {
	}

	public void logPlayerRanEventPackageId(long event_package_id) {
	}

	public void fetchDialogs() {
	}

	public void fetchDialogCharacters() {
	}

	public void fetchDialogScripts() {
	}

	public void fetchDialogOptions() {
	}

	public void fetchQuestsForPlayer() {
	}

	public void fetchQuests() {
	}

	public void touchItemsForGroups() {
	}

	public void fetchTags() {
	}

	public void fetchObjectTags() {
	}
}
