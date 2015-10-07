package edu.uoregon.casls.aris_android.models;

import com.google.android.gms.games.internal.game.GameInstance;

import java.util.Collection;
import java.util.HashMap;
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
public class GameInstancesModel extends ARISModel {

	public Map<Long, Instance> instances;
	public List<GameInstance> gameOwnedInstances;
	public long currentWeight;


	public GamePlayActivity mGamePlayAct;
	public Game mGame;

	public GameInstancesModel(){}


	public GameInstancesModel(GamePlayActivity gamePlayAct){
		this.initContext(gamePlayAct);
	}

	public void initContext(GamePlayActivity gamePlayAct) {
		mGamePlayAct = gamePlayAct; // todo: may need leak checking is activity gets recreated.
		mGame = mGamePlayAct.mGame; // shortcut convenience reference
	}

	public void clearGameData() {
		this.clearPlayerData();
		n_game_data_received = 0;
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
		gameOwnedInstances.clear();
	}

	public void requestGameData()
	{
		this.touchGameInstances();
	}
	

	public void gameInstancesTouched(List<GameInstance> gameInstances)
	{
		n_game_data_received++;
		mGamePlayAct.mDispatch.model_game_instance_touched(); //_ARIS_NOTIF_SEND_(@"MODEL_GAME_INSTANCES_TOUCHED",nil,nil);
		mGamePlayAct.mDispatch.model_game_piece_available(); //_ARIS_NOTIF_SEND_(@"MODEL_GAME_PIECE_AVAILABLE",nil,nil);
	}

	public void touchGameInstances()
	{
		mGamePlayAct.mServices.touchItemsForGame();
	}

	public void gameInstancesAvailable()
	{
		List<Instance> newInstances = mGame.instancesModel.gameOwnedInstances();
		this.clearPlayerData();

		for (Instance newInstance : newInstances) {
			if(!newInstance.object_type.contentEquals("ITEM") || !newInstance.object_type.contentEquals("GAME")) continue;
			instances.put(newInstance.object_id, newInstance); //[[NSNumber numberWithLong:newInstance.object_id]] = newInstance;
		}
		mGamePlayAct.mDispatch.model_game_instances_available(); // _ARIS_NOTIF_SEND_(@"MODEL_GAME_INSTANCES_AVAILABLE",nil,nil);
	}

	public long dropItemFromGame(long item_id, long qty)
	{
		Instance gII = instances.get(item_id);// instances[[NSNumber numberWithLong:item_id]];
		if (gII == null) return 0; //UH OH! NO INSTANCE TO TAKE ITEM FROM! (shouldn't happen if touchItemsForGame was called...)
		if (gII.qty < qty) qty = gII.qty; // uh, ok...whatever.

		if(!mGame.network_level.contentEquals("LOCAL"))
			mGamePlayAct.mServices.dropItem(item_id, qty);
		return this.takeItemFromGame(item_id, qty);
	}

	public long takeItemFromGame(long item_id, long qty)
	{
		Instance gII = instances.get(item_id);
		if (gII == null) return 0; //UH OH! NO INSTANCE TO TAKE ITEM FROM! (shouldn't happen if touchItemsForGame was called...)
		if(gII.qty < qty) qty = gII.qty;

		return this.setItemsForGame(item_id, gII.qty - qty);
	}

	public long giveItemToGame(long item_id, long qty)
	{
		Instance gII = instances.get(item_id);
		if (gII == null) return 0; //UH OH! NO INSTANCE TO GIVE ITEM TO! (shouldn't happen if touchItemsForGame was called...)
		if (qty > this.qtyAllowedToGiveForItem(item_id)) qty = this.qtyAllowedToGiveForItem(item_id);

		return this.setItemsForGame(item_id, gII.qty + qty);
	}

	public long setItemsForGame(long item_id, long qty)
	{
		Instance gII = instances.get(item_id);
		if (gII == null) return 0; //UH OH! NO INSTANCE TO GIVE ITEM TO! (shouldn't happen if touchItemsForGame was called...)

		if (qty < 0) qty = 0;
		if (qty - gII.qty > this.qtyAllowedToGiveForItem(item_id)) qty = gII.qty + this.qtyAllowedToGiveForItem(item_id);

		long oldQty = gII.qty;
		mGame.instancesModel.setQtyForInstanceId(gII.instance_id, qty);
		if (qty > oldQty) mGame.logsModel.gameReceivedItemId(item_id, qty);
		if (qty < oldQty) mGame.logsModel.gameLostItemId(item_id, qty);

		return qty;
	}

	public long qtyOwnedForItem(long item_id) {
		return instances.get(item_id).qty;
	}

	public long qtyOwnedForTag(long tag_id)
	{
		long q = 0;
		List<Long> item_ids = mGame.tagsModel.objectIdsOfType(tag_id); // [_MODEL_TAGS_ objectIdsOfType:@"ITEM" tag:tag_id];
		for (Long item_id: item_ids)
			q += this.qtyOwnedForItem(item_id);
		return q;
	}

	public long qtyAllowedToGiveForItem(long item_id)
	{
		Item i = mGame.itemsModel.itemForId(item_id);
		long amtMoreCanHold = i.max_qty_in_inventory - this.qtyOwnedForItem(item_id);
		while (mGame.inventory_weight_cap > 0 &&
				(amtMoreCanHold * i.weight + currentWeight) > mGame.inventory_weight_cap)
			amtMoreCanHold--;

		return amtMoreCanHold;
	}

//because it's 1 to 1 (unlike player instances to attribs + inventory), very simple
	public List<Instance> gameOwnedInstances()
	{
		if (gameOwnedInstances != null) return gameOwnedInstances;

		List<Instance> gameOwnedInstances = new LinkedList<>(); //[[NSMutableArray alloc] init];
		Collection<Instance> insts = instances.values();
		for (Instance inst : insts)
		gameOwnedInstances.add(inst);
		return gameOwnedInstances;
	}
}
