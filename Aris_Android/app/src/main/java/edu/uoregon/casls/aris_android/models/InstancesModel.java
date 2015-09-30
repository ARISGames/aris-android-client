package edu.uoregon.casls.aris_android.models;

import java.util.LinkedHashMap;
import java.util.Map;

import edu.uoregon.casls.aris_android.data_objects.Instance;

/**
 * Created by smorison on 8/20/15.
 */
public class InstancesModel extends ARISModel {

	public Map<Long, Instance> instances = new LinkedHashMap<>();

	public void clearGameData() {
		n_game_data_received = 0;
	}

	public void clearPlayerData() {

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
//		if(![_MODEL_GAME_.network_level isEqualToString:@"REMOTE"])
//		{
//			long oldQty = i.qty;
//			i.qty = qty;
//			NSDictionary *deltas;
//			if(qty > oldQty) deltas = @{@"lost":@[],@"added":@[@{@"instance":i,@"delta":[NSNumber numberWithLong:qty-oldQty]}]};
//			if(qty < oldQty) deltas = @{@"added":@[],@"lost":@[@{@"instance":i,@"delta":[NSNumber numberWithLong:qty-oldQty]}]};
//
//			if(deltas)
//			{
//				if([i.owner_type isEqualToString:@"USER"] && i.owner_id == _MODEL_PLAYER_.user_id)
//				[self sendNotifsForGameDeltas:nil playerDeltas:deltas];
//				else if([i.owner_type isEqualToString:@"GAME_CONTENT"])
//				[self sendNotifsForGameDeltas:deltas playerDeltas:nil];
//			}
//		}
//
//		if(![_MODEL_GAME_.network_level isEqualToString:@"LOCAL"])
//		[_SERVICES_ setQtyForInstanceId:instance_id qty:qty];
		return qty;
	}

	private Instance instanceForId(long instance_id) {
		Instance i = null; // todo temp stub

		return i;
	}

	public long nGameDataToReceive () {
		return 1;
	}


}
