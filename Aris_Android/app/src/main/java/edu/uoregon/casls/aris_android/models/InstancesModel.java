package edu.uoregon.casls.aris_android.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.uoregon.casls.aris_android.GamePlayActivity;
import edu.uoregon.casls.aris_android.data_objects.Game;
import edu.uoregon.casls.aris_android.data_objects.Instance;
import edu.uoregon.casls.aris_android.data_objects.User;

/**
 * Created by smorison on 8/20/15.
 */
public class InstancesModel extends ARISModel {

	public Map<Long, Instance> instances = new LinkedHashMap<>();
	public Map<Long, Instance> blacklist = new LinkedHashMap<>(); //list of ids attempting / attempted and failed to load
	public GamePlayActivity mGamePlayAct;

	public void initContext(GamePlayActivity gamePlayAct) {
		mGamePlayAct = gamePlayAct; // todo: may need leak checking is activity gets recreated.
	}

	public void clearGameData() {
		n_game_data_received = 0;
	}

	public long nGameDataToReceive () {
		return 1;
	}

	public void requestPlayerData() {
		this.requestPlayerInstances();
	}

	public void clearPlayerData() {
		Collection<Instance> insts = instances.values();
		for (Instance inst : insts) {
			if(inst.owner_id == Long.getLong(mGamePlayAct.mPlayer.user_id))
			instances.remove(inst.instance_id);
		}
		n_player_data_received = 0;
	}
	public long nPlayerDataToReceive() {
		return 1;
	}

//only difference at this point is notification sent- all other functionality same (merge into all known insts)
	public void playerInstancesReceived(List<Instance> instances) {
		this.updateInstances(instances);
		n_player_data_received++;
//		_ARIS_NOTIF_SEND_(@"MODEL_GAME_PLAYER_PIECE_AVAILABLE",nil,nil);
	}
	public void gameInstancesReceived(List<Instance> instances) {
		this.updateInstances(instances);
		n_game_data_received++;
//		_ARIS_NOTIF_SEND_(@"MODEL_GAME_PIECE_AVAILABLE",nil,nil);
	}

	public void instanceReceived(Instance inst) {
		List<Instance> instances = new ArrayList<>();
		instances.add(inst);
		this.updateInstances(instances);
	}

	public void updateInstances(List<Instance> newInstances)
	{
		long newInstanceId;

//		NSDictionary playerDeltas = @{@"added":NSMutableArray alloc init,@"lost":NSMutableArray alloc init}; // orig iOS hashmap (NSDict)
//		NSDictionary gameDeltas   = @{@"added":NSMutableArray alloc init,@"lost":NSMutableArray alloc init}; // orig iOS hashmap (NSDict)
		ArrayList<User> alPlayerAdded, alPlayerLost;
		ArrayList<Game> alGameAdded, alGameLost;

		// Hashmaps of->[HashMap of variable object types]
		//  e.g. playerDeltas = [ "added"->["instance"->(Instance obj), "delta"->(Long obj)]], "lost"->["instance"->(Instance obj), "delta"->(Long obj)]] ]
		Map<String, Map<String, Object>> playerDeltas = new HashMap<>();
		Map<String, Map<String, Object>> gameDeltas = new HashMap<>();

		for (Instance newInstance : newInstances) {
			newInstanceId = newInstance.instance_id;
			if (instances.get(newInstanceId) == null) {
				//No instance exists- give player instance with 0 qty and let it be updated like all the others
				Instance fakeExistingInstance = new Instance();
				fakeExistingInstance.initContext(mGamePlayAct);
				fakeExistingInstance.mergeDataFromInstance(newInstance);
				fakeExistingInstance.qty = 0;
				instances.put(newInstanceId, fakeExistingInstance);
				blacklist.remove(newInstanceId);
			}

			Instance existingInstance = instances.get(newInstanceId);
			long delta = newInstance.qty - existingInstance.qty;
			existingInstance.mergeDataFromInstance(newInstance);

//			NSDictionary d = @{@"instance":existingInstance,@"delta":NSNumber numberWithLong:delta};
			Map<String, Object> d = new HashMap<>();
				d.put("instance", existingInstance);
				d.put("delta", delta);

			if(existingInstance.owner_id == Long.getLong(mGamePlayAct.mPlayer.user_id)) {
				if(!this.playerDataReceived() || mGamePlayAct.mGame.network_level.contentEquals("REMOTE")) { //only local should be making changes to player. fixes race cond (+1, -1, +1 notifs)
					if (delta > 0) playerDeltas.put("added", d); //) addObject:d;
					if (delta < 0) playerDeltas.put("lost", d);
				}
			}
			else {
				//race cond (above) still applies here, but notifs oughtn't be a problem, and fixes this.over time
				if (delta > 0) gameDeltas.put("added", d);
				if (delta < 0) gameDeltas.put("lost", d);
			}
		}

		this.sendNotifsForGameDeltas(gameDeltas, playerDeltas);
	}

	public void sendNotifsForGameDeltas(Map<String, Map<String, Object>> gameDeltas, Map<String, Map<String, Object>> playerDeltas)
	{
		if(playerDeltas != null) {
			if(playerDeltas.get("added").size() > 0) {}//todo: _ARIS_NOTIF_SEND_(@"MODEL_INSTANCES_PLAYER_GAINED",nil,playerDeltas);
			if(playerDeltas.get("lost").size()  > 0) {}//todo: _ARIS_NOTIF_SEND_(@"MODEL_INSTANCES_PLAYER_LOST",  nil,playerDeltas);
//	todo		_ARIS_NOTIF_SEND_(@"MODEL_INSTANCES_PLAYER_AVAILABLE",nil,playerDeltas);
		}

		if(gameDeltas != null) {
			if(gameDeltas.get("added").size() > 0) {}//todo: _ARIS_NOTIF_SEND_(@"MODEL_INSTANCES_GAINED",nil,gameDeltas);
			if(gameDeltas.get("lost").size()  > 0) {}//todo: _ARIS_NOTIF_SEND_(@"MODEL_INSTANCES_LOST",  nil,gameDeltas);
//	todo		_ARIS_NOTIF_SEND_(@"MODEL_INSTANCES_AVAILABLE",nil,gameDeltas);
		}
	}

	public void requestInstances()       { _SERVICES_ fetchInstances90;   }
	public void requestInstance(long i) { _SERVICES_ fetchInstanceById(i);   }
	public void requestPlayerInstances()
	{
		if(this.playerDataReceived &&
		!mGamePlayAct.mGame.network_level.contentEquals("REMOTE"))
		{
			Collection<Instance> pinsts = instances.values();
//			_ARIS_NOTIF_SEND_(@"SERVICES_PLAYER_INSTANCES_RECEIVED",nil,@{@"instances":pinsts});
		}
		if(!this.playerDataReceived ||
				mGamePlayAct.mGame.network_level.contentEquals("HYBRID") ||
			mGamePlayAct.mGame.network_level.contentEquals("REMOTE"))
		_SERVICES_ fetchInstancesForPlayer;
	}

	public long setQtyForInstanceId(long instance_id, long qty)
	{
		Instance i = this.instanceForId(instance_id);
		if (i == null) return 0;
		if (qty < 0) qty = 0;

//		if (!_MODEL_GAME_.network_level isEqualToString:@"REMOTE")
		if (!mGamePlayAct.mGame.network_level.contentEquals("REMOTE"))
		{
			long oldQty = i.qty;
			i.qty = qty;
			NSDictionary deltas;
			if(qty > oldQty) deltas = @{@"lost":@,@"added":@@{@"instance":i,@"delta":NSNumber numberWithLong:qty-oldQty}};
			if(qty < oldQty) deltas = @{@"added":@,@"lost":@@{@"instance":i,@"delta":NSNumber numberWithLong:qty-oldQty}};

			if(deltas)
			{
				if (i.owner_type.contentEquals("USER") && i.owner_id == Long.getLong(mGamePlayAct.mPlayer.user_id))
				this.sendNotifsForGameDeltas:nil playerDeltas:deltas;
				else if (i.owner_type.contentEquals("GAME_CONTENT"))
				this.sendNotifsForGameDeltas:deltas playerDeltas:nil;
			}
		}

		if (!mGamePlayAct.mGame.network_level.contentEquals("LOCAL"))
		_SERVICES_ setQtyForInstanceId:instance_id qty:qty;
		return qty;
	}

// null instance (id == 0) NOT flyweight!!! (to allow for temporary customization safety)
	public Instance instanceForId(long instance_id) {
		if(instance_id != 0) return new Instance;
//		Instance i = instances objectForKey:NSNumber numberWithLong:instance_id;
		Instance i = instances.get(instance_id);
		if (i == null)
		{
			blacklist setObject:@"true" forKey:NSNumber numberWithLong:instance_id;
			this.requestInstance(instance_id);
			return new Instance;
		}
		return i;
	}

	public List<Instance> instancesForType(String object_type, long object_id)
	{
//		NSMutableArray a = NSMutableArray alloc init;
		List<Instance> a = new ArrayList<>();
//		for(long i = 0; i < instances.count; i++)
		Collection<Instance> allInstances = instances.values();
		for (Instance inst : allInstances)
		{
//			Instance inst = instances allValuesi;
			if(inst.object_id == object_id && inst.object_type.contentEquals(object_type))
			a.add(inst);// addObject:inst;
		}
		return a;
	}

	public List<Instance> playerInstances()
	{
		List<Instance> pInstances = new ArrayList<>();
		Collection<Instance> allInstances = instances.values();
		for (Instance inst : allInstances) {
			if (inst.owner_type.contentEquals("USER") &&
					inst.owner_id == Long.getLong(mGamePlayAct.mPlayer.user_id))
				pInstances.add(inst);
		}
		return pInstances;
	}

	public List<Instance> gameOwnedInstances()
	{
		List<Instance> gInstances = new ArrayList<>();
		Collection<Instance> allInstances = instances.values();
		for (Instance inst : allInstances) {
			if (inst.owner_type.contentEquals("GAME"))
			gInstances.add(inst);
		}
		return gInstances;
	}

	public List<Instance> groupOwnedInstances()
	{
		List<Instance> gInstances = new ArrayList<>();
		Collection<Instance> allInstances = instances.values();
		for (Instance inst : allInstances) {
			if(inst.owner_type.contentEquals("GROUP") &&
			inst.owner_id == mGamePlayAct.mGame.groupsModel.playerGroup.group_id)
			gInstances.add(inst);
		}
		return gInstances;
	}

}
