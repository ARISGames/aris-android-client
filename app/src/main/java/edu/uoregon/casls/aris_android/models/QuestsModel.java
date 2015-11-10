package edu.uoregon.casls.aris_android.models;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.uoregon.casls.aris_android.GamePlayActivity;
import edu.uoregon.casls.aris_android.data_objects.Game;
import edu.uoregon.casls.aris_android.data_objects.Quest;

/**
 * Created by smorison on 8/20/15.
 */
public class QuestsModel extends ARISModel {

	public Map<Long, Quest> quests = new LinkedHashMap<>();

	public List<Quest> visibleActiveQuests = new LinkedList<>();
	public List<Quest> visibleCompleteQuests = new LinkedList<>();


	public transient GamePlayActivity mGamePlayAct;
	public transient Game mGame;

	public void initContext(GamePlayActivity gamePlayAct) {
		mGamePlayAct = gamePlayAct; // todo: may need leak checking is activity gets recreated.
		mGame = mGamePlayAct.mGame;
	}

	public void requestPlayerData() {
		this.requestPlayerQuests();
	}

	public void clearPlayerData() {
		visibleActiveQuests.clear();
		visibleCompleteQuests.clear();
		n_player_data_received = 0;
	}

	public long nPlayerDataToReceive() {
		return 1;
	}

	public void requestGameData() {
		this.requestQuests();
	}

	public void clearGameData() {
		this.clearPlayerData();
		quests.clear();
		n_game_data_received = 0;
	}

	public long nGameDataToReceive() {
		return 1;
	}

	public void questsReceived(List<Quest> newQuests) {
		this.updateQuests(newQuests);
	}

	public void updateQuests(List<Quest> newQuests) {
		long newQuestId;
		for (Quest newQuest : newQuests) {
			newQuestId = newQuest.quest_id;
			if (quests.get(newQuestId) != null)
				quests.put(newQuestId, newQuest); // setObject:newQuest forKey:newQuestId;
		}
		n_game_data_received++;
		mGamePlayAct.mDispatch.model_quests_available(); // _ARIS_NOTIF_SEND_(@"MODEL_QUESTS_AVAILABLE",nil,nil);
		mGamePlayAct.mDispatch.model_game_piece_available(); // _ARIS_NOTIF_SEND_(@"GAME_PIECE_AVAILABLE",nil,nil);
	}

	public void requestQuests() {
		mGamePlayAct.mServices.fetchQuests();
	}

	public void requestPlayerQuests() {
		if (this.playerDataReceived() && !mGame.network_level.contentEquals("REMOTE")) {
			this.logAnyNewlyCompletedQuests();
//			NSDictionary *pquests =
//				@{
//				@"active"   : [[NSMutableArray alloc] init],
//				@"complete" : [[NSMutableArray alloc] init]
//			};
			Map<String, List<Quest>> pquests = new HashMap<>();
			List<Quest> qListComplete = new LinkedList<>();
			pquests.put("complete", qListComplete);
			List<Quest> qListActive = new LinkedList<>();
			pquests.put("active", qListActive);
			Collection<Quest> qs = quests.values();// allValues;
			for (Quest q : qs) {
				if (mGame.requirementsModel.evaluateRequirementRoot(q.active_requirement_root_package_id)) {
					if (mGame.logsModel.hasLogType("COMPLETE_QUEST", q.quest_id)) {
						qListComplete.add(q); //[@"complete"] addObject(q;
					}
					else {
						qListActive.add(q); //pquests[@"active"] addObject(q;
					}
				}
			}
			mGamePlayAct.mDispatch.services_player_quests_received(pquests); // _ARIS_NOTIF_SEND_(@"SERVICES_PLAYER_QUESTS_RECEIVED",nil,pquests);
		}
		if (!this.playerDataReceived() ||
				mGame.network_level.contentEquals("HYBRID") ||
				mGame.network_level.contentEquals("REMOTE"))
			mGamePlayAct.mServices.fetchQuestsForPlayer();
	}

	public void logAnyNewlyCompletedQuests() {
		Collection<Quest> qs = quests.values();// allValues;
		for (Quest q : qs) {
			if (!mGame.logsModel.hasLogType("COMPLETE_QUEST", q.quest_id)) {
				if (mGame.requirementsModel.evaluateRequirementRoot(q.complete_requirement_root_package_id)) {
					mGame.requirementsModel.evaluateRequirementRoot(q.complete_requirement_root_package_id);
					mGame.logsModel.playerCompletedQuestId(q.quest_id);
				}
			}
		}
	}

	//admittedly a bit silly, but a great way to rid any risk of deviation from flyweight by catching it at the beginning
	public List<Quest> conformQuestListToFlyweight(List<Quest> newQuests) {
		List<Quest> conformingQuests = new LinkedList<>();
		for (Quest q : newQuests) {
			if (this.questForId(q.quest_id) != null)
				conformingQuests.add(q); // addObject(q;
		}

		return conformingQuests;
	}

	public void playerQuestsReceived(Map<String, List<Quest>> pquests) { //todo: params needed???
		this.updateCompleteQuests(this.conformQuestListToFlyweight(pquests.get("complete"))); //objectForKey:@"complete"]];
		this.updateActiveQuests(this.conformQuestListToFlyweight(pquests.get("active"))); //.userInfo objectForKey:@"active"]];
		n_player_data_received++;
		mGamePlayAct.mDispatch.model_game_player_piece_available(); // _ARIS_NOTIF_SEND_(@"PLAYER_PIECE_AVAILABLE",nil,nil);
	}

	public void updateActiveQuests(List<Quest> newQuests) {
		Map<String, List<Quest>> deltas = this.findDeltasInNew(newQuests, visibleActiveQuests);

		visibleActiveQuests = newQuests; //assumes already conforms to flyweight

		List<Quest> addedDeltas = deltas.get("added");
		if (addedDeltas.size() > 0)
			mGamePlayAct.mDispatch.model_quests_active_new_available(deltas); // _ARIS_NOTIF_SEND_(@"MODEL_QUESTS_ACTIVE_NEW_AVAILABLE",nil,deltas);
		if (this.playerDataReceived()) {
			for (Quest addedDelta : addedDeltas)
				mGame.eventsModel.runEventPackageId(addedDelta.active_event_package_id);
		}

		List<Quest> removedDeltas = deltas.get("removed");
		if (removedDeltas.size() > 0)
			mGamePlayAct.mDispatch.model_quests_active_less_available(deltas); // _ARIS_NOTIF_SEND_(@"MODEL_QUESTS_ACTIVE_LESS_AVAILABLE",nil,deltas);
	}

	public void updateCompleteQuests(List<Quest> newQuests) {
		Map<String, List<Quest>> deltas = this.findDeltasInNew(newQuests, visibleCompleteQuests);
		visibleCompleteQuests = newQuests; //assumes already conforms to flyweight

		List<Quest> addedDeltas = deltas.get("added");
		if (addedDeltas.size() > 0)
			mGamePlayAct.mDispatch.model_quests_complete_new_available(deltas); // _ARIS_NOTIF_SEND_(@"MODEL_QUESTS_COMPLETE_NEW_AVAILABLE",nil,deltas);
		if (this.playerDataReceived()) {
			for (Quest addedDelta : addedDeltas)
				mGame.eventsModel.runEventPackageId(addedDelta.complete_event_package_id);
		}

		List<Quest> removedDeltas = deltas.get("removed");
		if (removedDeltas.size() > 0)
			mGamePlayAct.mDispatch.model_quests_complete_less_available(deltas); // _ARIS_NOTIF_SEND_(@"MODEL_QUESTS_COMPLETE_LESS_AVAILABLE",nil,deltas);
	}

	//finds deltas in quest lists generally, so I can just use same code for complete/active
	public Map<String, List<Quest>> findDeltasInNew(List<Quest> newQuests, List<Quest> oldQuests) {
//		NSDictionary *qDeltas = @{ @"added"([NSMutableArray alloc] init], @"removed"([NSMutableArray alloc] init] };
		Map<String, List<Quest>> qDeltas = new HashMap<>();
		List<Quest> qListAdded = new LinkedList<>();
		qDeltas.put("added", qListAdded);
		List<Quest> qListRemoved = new LinkedList<>();
		qDeltas.put("removed", qListRemoved);

		//find added
		boolean newq;
		for (Quest newQuest : newQuests) {
			newq = true;
			for (Quest oldQuest : oldQuests) {
				if (newQuest.quest_id == oldQuest.quest_id) newq = false;
			}
			if (newq) qListAdded.add(newQuest); //[qDeltas[@"added"] addObject(newQuests[i];
		}

		//find removed
		boolean removed;
		for (Quest oldQuest : oldQuests) {
			removed = true;
			for (Quest newQuest : newQuests) {
				if (newQuest.quest_id == oldQuest.quest_id) removed = false;
			}
			if (removed) qListRemoved.add(oldQuest); // [qDeltas[@"removed"] addObject(oldQuests[i];
		}

		return qDeltas;
	}

	public Quest questForId(long quest_id) {
		if (quest_id == 0) return new Quest();
		return quests.get(quest_id);// objectForKey(NSNumber numberWithLong(quest_id];
	}

	public List<Quest> visibleActiveQuests() {
		return visibleActiveQuests;
	}

	public List<Quest> visibleCompleteQuests() {
		return visibleCompleteQuests;
	}

}
