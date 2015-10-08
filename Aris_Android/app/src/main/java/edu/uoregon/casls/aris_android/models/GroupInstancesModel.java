package edu.uoregon.casls.aris_android.models;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.uoregon.casls.aris_android.GamePlayActivity;
import edu.uoregon.casls.aris_android.data_objects.Game;
import edu.uoregon.casls.aris_android.data_objects.Instance;
import edu.uoregon.casls.aris_android.data_objects.Item;

/**
 * Created by smorison on 9/29/15.
 */
public class GroupInstancesModel extends ARISModel {

	public Map<Long, Instance> instances = new LinkedHashMap<>();
	public List<Instance> groupOwnedInstances = new LinkedList<>();
	public long currentWeight;
	
	public GamePlayActivity mGamePlayAct;
	public Game mGame;

	public void initContext(GamePlayActivity gamePlayAct) {
		mGamePlayAct = gamePlayAct; // todo: may need leak checking is activity gets recreated.
		mGame = mGamePlayAct.mGame;
	}

	public long nGameDataToReceive () {
		return 1;
	}


	public void clearPlayerData()
	{
		instances.clear();
		this.invalidateCaches();
		currentWeight = 0;
	}

	public void invalidateCaches()
	{
		groupOwnedInstances.clear();
	}

	public void requestGameData()
	{
		this.touchGroupInstances();
	}
	public void clearGameData()
	{
		this.clearPlayerData();
		n_game_data_received = 0;
	}

	public void groupInstancesTouched(List<Instance> instances)
	{
		n_game_data_received++;
		mGamePlayAct.mDispatch.model_group_instances_touched(); //mGamePlayAct.mDispatch. //_ARIS_NOTIF_SEND_(@"MODEL_GROUP_INSTANCES_TOUCHED",nil,nil);
		mGamePlayAct.mDispatch.model_game_piece_available(); //mGamePlayAct.mDispatch. //_ARIS_NOTIF_SEND_(@"MODEL_GAME_PIECE_AVAILABLE",nil,nil);
	}

	public void touchGroupInstances() 
	{
		mGamePlayAct.mServices.touchItemsForGroups();
	}

	public void groupInstancesAvailable()
	{ 
		List<Instance> newInstances = mGame.instancesModel.groupOwnedInstances();
		this.clearPlayerData();

		for (Instance newInstance : newInstances)
		{
			if(!newInstance.object_type.contentEquals("ITEM") || !newInstance.owner_type.contentEquals("GROUP")) continue;

//			instances[[NSNumber numberWithLong:newInstance.object_id]] = newInstance;
			instances.put(newInstance.object_id, newInstance);
		}
		mGamePlayAct.mDispatch.model_group_instances_available(); //_ARIS_NOTIF_SEND_(@"MODEL_GROUP_INSTANCES_AVAILABLE",nil,nil);
	}

	public long dropItemFromGroup(long item_id, long qty)
	{
		Instance gII = instances.get(item_id);// instances.get(item_id);
		if (gII == null) return 0; //UH OH! NO INSTANCE TO TAKE ITEM FROM! (shouldn't happen if touchItemsForGroups was called...)
		if(gII.qty < qty) qty = gII.qty;

		if(!mGame.network_level.contentEquals("LOCAL"))
		mGamePlayAct.mServices.dropItem(item_id, qty);
		return this.takeItemFromGroup(item_id, qty);
	}

	public long takeItemFromGroup(long item_id, long qty)
	{
		Instance gII = instances.get(item_id);
		if (gII == null) return 0; //UH OH! NO INSTANCE TO TAKE ITEM FROM! (shouldn't happen if touchItemsForGroups was called...)
		if(gII.qty < qty) qty = gII.qty;

		return this.setItemsForGroup(item_id, gII.qty - qty);
	}

	public long giveItemToGroup(long item_id, long qty)
	{
		Instance gII = instances.get(item_id);
		if (gII == null) return 0; //UH OH! NO INSTANCE TO GIVE ITEM TO! (shouldn't happen if touchItemsForGroups was called...)
		if(qty > this.qtyAllowedToGiveForItem(item_id)) qty = this.qtyAllowedToGiveForItem(item_id);

		return this.setItemsForGroup(item_id, gII.qty + qty);
	}

	public long setItemsForGroup(long item_id, long qty)
	{
		Instance gII = instances.get(item_id);
		if (gII == null) return 0; //UH OH! NO INSTANCE TO GIVE ITEM TO! (shouldn't happen if touchItemsForGroup was called...)

		if (qty < 0) qty = 0;
		if(qty-gII.qty > this.qtyAllowedToGiveForItem(item_id)) qty = gII.qty+this.qtyAllowedToGiveForItem(item_id);

		long oldQty = gII.qty;
		mGame.instancesModel.setQtyForInstanceId(gII.instance_id, qty);
		if(qty > oldQty) mGame.logsModel.groupReceivedItemId(item_id, qty);
		if(qty < oldQty) mGame.logsModel.groupLostItemId(item_id, qty);

		return qty;
	}

	public long qtyOwnedForItem(long item_id)
	{
		return instances.get(item_id).qty;
	}

	public long qtyOwnedForTag(long tag_id)
	{
		long q = 0;
		long[] item_ids = mGame.tagsModel.objectIdsOfType("ITEM", tag_id);
		for(int i = 0; i < item_ids.length; i++)
			q += this.qtyOwnedForItem(item_ids[i]);
		return q;
	}

	public long qtyAllowedToGiveForItem(long item_id)
	{
		Item i = mGame.itemsModel.itemForId(item_id);
		long amtMoreCanHold = i.max_qty_in_inventory-this.qtyOwnedForItem(item_id);
		while (mGame.inventory_weight_cap > 0 &&
				(amtMoreCanHold*i.weight + currentWeight) > mGame.inventory_weight_cap)
			amtMoreCanHold--;

		return amtMoreCanHold;
	}

//because it's 1 to 1 (unlike player instances to attribs + inventory), very simple
	public List<Instance> groupOwnedInstances()
	{
		if(groupOwnedInstances != null) return groupOwnedInstances;

		groupOwnedInstances = new LinkedList<>();
		Collection<Instance> instancearray = instances.values();
		for (Instance inst : instancearray)
		groupOwnedInstances.add(inst); //addObject:[instancearray objectAtIndex:i]];
		return groupOwnedInstances;
	}


}
