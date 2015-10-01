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

	public void requestInstances() {
	}

	public void requestPlayerInstances() {

	}


	public long setQtyForInstanceId(long instance_id, long qty) {
		Instance i = this.instanceForId(instance_id);
		if(i == null) return 0;
		if(qty < 0) qty = 0;

		// todo: un iOS-ify everything below
//		if(!_MODEL_GAME_.network_level isEqualToString:@"REMOTE")
//		{
//			long oldQty = i.qty;
//			i.qty = qty;
//			NSDictionary deltas;
//			if(qty > oldQty) deltas = @{@"lost":@,@"added":@@{@"instance":i,@"delta":NSNumber numberWithLong:qty-oldQty}};
//			if(qty < oldQty) deltas = @{@"added":@,@"lost":@@{@"instance":i,@"delta":NSNumber numberWithLong:qty-oldQty}};
//
//			if(deltas)
//			{
//				if(i.owner_type isEqualToString:@"USER" && i.owner_id == _MODEL_PLAYER_.user_id)
//				this.sendNotifsForGameDeltas:nil playerDeltas:deltas;
//				else if(i.owner_type isEqualToString:@"GAME_CONTENT")
//				this.sendNotifsForGameDeltas:deltas playerDeltas:nil;
//			}
//		}
//
//		if(!_MODEL_GAME_.network_level isEqualToString:@"LOCAL")
//		_SERVICES_ setQtyForInstanceId:instance_id qty:qty;
		return qty;
	}

//	private Instance instanceForId(long instance_id) {
//		Instance i = null; // todo temp stub
//
//		return i;
//	}

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

//		NSDictionary playerDeltas = @{@"added":NSMutableArray alloc init,@"lost":NSMutableArray alloc init};
//		NSDictionary gameDeltas   = @{@"added":NSMutableArray alloc init,@"lost":NSMutableArray alloc init};
		ArrayList<User> alPlayerAdded, alPlayerLost;
		ArrayList<Game> alGameAdded, alGameLost;
		Map<String, ArrayList<User>> playerDeltas = new HashMap<>();
		Map<String, ArrayList<Game>> gameDeltas = new HashMap<>();
		playerDeltas.put("added", alPlayerAdded); playerDeltas.put("lost", alPlayerLost);
		gameDeltas.put("added", alGameAdded); gameDeltas.put("lost", alGameLost);

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
			Map<String, Object> d = new HashMap<String, Object>();
				d.put("instance", existingInstance);
				d.put("delta", delta);

			if(existingInstance.owner_id == _MODEL_PLAYER_.user_id) HERE HERE HERE
			{
				if(!this.playerDataReceived || _MODEL_GAME_.network_level isEqualToString:@"REMOTE") //only local should be making changes to player. fixes race cond (+1, -1, +1 notifs)
				{
					if(delta > 0) (playerDeltas.get("added")).add(d); //) addObject:d;
					if(delta < 0) (playerDeltas.get("lost")) addObject:d;
				}
			}
			else
			{
				//race cond (above) still applies here, but notifs oughtn't be a problem, and fixes this.over time
				if(delta > 0) ((NSMutableArray )gameDeltas@"added") addObject:d;
				if(delta < 0) ((NSMutableArray )gameDeltas@"lost" ) addObject:d;
			}
		}

		this.sendNotifsForGameDeltas:gameDeltas playerDeltas:playerDeltas;
	}

	public void sendNotifsForGameDeltas:(NSDictionary )gameDeltas playerDeltas:(NSDictionary )playerDeltas
	{
		if(playerDeltas)
		{
			if(((NSArray )playerDeltas@"added").count > 0) _ARIS_NOTIF_SEND_(@"MODEL_INSTANCES_PLAYER_GAINED",nil,playerDeltas);
			if(((NSArray )playerDeltas@"lost").count  > 0) _ARIS_NOTIF_SEND_(@"MODEL_INSTANCES_PLAYER_LOST",  nil,playerDeltas);
			_ARIS_NOTIF_SEND_(@"MODEL_INSTANCES_PLAYER_AVAILABLE",nil,playerDeltas);
		}

		if(gameDeltas)
		{
			if(((NSArray )gameDeltas@"added").count > 0) _ARIS_NOTIF_SEND_(@"MODEL_INSTANCES_GAINED",nil,gameDeltas);
			if(((NSArray )gameDeltas@"lost").count  > 0) _ARIS_NOTIF_SEND_(@"MODEL_INSTANCES_LOST",  nil,gameDeltas);
			_ARIS_NOTIF_SEND_(@"MODEL_INSTANCES_AVAILABLE",nil,gameDeltas);
		}
	}

	public void requestInstances       { _SERVICES_ fetchInstances;   }
	public void requestInstance:(long)i { _SERVICES_ fetchInstanceById:i;   }
	public void requestPlayerInstances
	{
		if(this.playerDataReceived &&
		!_MODEL_GAME_.network_level isEqualToString:@"REMOTE")
		{
			NSArray *pinsts = instances allValues;
			_ARIS_NOTIF_SEND_(@"SERVICES_PLAYER_INSTANCES_RECEIVED",nil,@{@"instances":pinsts});
		}
		if(!this.playerDataReceived ||
		_MODEL_GAME_.network_level isEqualToString:@"HYBRID" ||
		_MODEL_GAME_.network_level isEqualToString:@"REMOTE")
		_SERVICES_ fetchInstancesForPlayer;
	}

	public long setQtyForInstanceId:(long)instance_id qty:(long)qty
	{
		Instance *i = this.instanceForId:instance_id;
		if(!i) return 0;
		if(qty < 0) qty = 0;

		if(!_MODEL_GAME_.network_level isEqualToString:@"REMOTE")
		{
			long oldQty = i.qty;
			i.qty = qty;
			NSDictionary deltas;
			if(qty > oldQty) deltas = @{@"lost":@,@"added":@@{@"instance":i,@"delta":NSNumber numberWithLong:qty-oldQty}};
			if(qty < oldQty) deltas = @{@"added":@,@"lost":@@{@"instance":i,@"delta":NSNumber numberWithLong:qty-oldQty}};

			if(deltas)
			{
				if(i.owner_type isEqualToString:@"USER" && i.owner_id == _MODEL_PLAYER_.user_id)
				this.sendNotifsForGameDeltas:nil playerDeltas:deltas;
				else if(i.owner_type isEqualToString:@"GAME_CONTENT")
				this.sendNotifsForGameDeltas:deltas playerDeltas:nil;
			}
		}

		if(!_MODEL_GAME_.network_level isEqualToString:@"LOCAL")
		_SERVICES_ setQtyForInstanceId:instance_id qty:qty;
		return qty;
	}

// null instance (id == 0) NOT flyweight!!! (to allow for temporary customization safety)
	public Instance instanceForId(long instance_id) {
		if(instance_id != 0) return new Instance;
		Instance i = instances objectForKey:NSNumber numberWithLong:instance_id;
		if(!i)
		{
			blacklist setObject:@"true" forKey:NSNumber numberWithLong:instance_id;
			this.requestInstance:instance_id;
			return Instance alloc init;
		}
		return i;
	}

	- (NSArray ) instancesForType:(NSString )object_type id:(long)object_id
	{
		NSMutableArray a = NSMutableArray alloc init;
		for(long i = 0; i < instances.count; i++)
		{
			Instance inst = instances allValuesi;
			if(inst.object_id == object_id && inst.object_type isEqualToString:object_type)
			a addObject:inst;
		}
		return a;
	}

	public List<Instance> playerInstances()
	{
		NSMutableArray pInstances = NSMutableArray alloc init;
		NSArray allInstances = instances allValues;
		for(long i = 0; i < allInstances.count; i++)
		{
			Instance inst = allInstancesi;
			if(inst.owner_type isEqualToString:@"USER" &&
			inst.owner_id == _MODEL_PLAYER_.user_id)
			pInstances addObject:allInstancesi;
		}
		return pInstances;
	}

	- (NSArray ) gameOwnedInstances
	{
		NSMutableArray gInstances = NSMutableArray alloc init;
		NSArray allInstances = instances allValues;
		for(long i = 0; i < allInstances.count; i++)
		{
			Instance inst = allInstancesi;
			if(inst.owner_type isEqualToString:@"GAME")
			gInstances addObject:allInstancesi;
		}
		return gInstances;
	}

	- (NSArray ) groupOwnedInstances
	{
		NSMutableArray gInstances = NSMutableArray alloc init;
		NSArray allInstances = instances allValues;
		for(long i = 0; i < allInstances.count; i++)
		{
			Instance inst = allInstancesi;
			if(inst.owner_type isEqualToString:@"GROUP" &&
			inst.owner_id == _MODEL_GROUPS_.playerGroup.group_id)
			gInstances addObject:allInstancesi;
		}
		return gInstances;
	}

}
