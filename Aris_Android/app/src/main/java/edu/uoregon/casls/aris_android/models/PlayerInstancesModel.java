package edu.uoregon.casls.aris_android.models;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.uoregon.casls.aris_android.GamePlayActivity;
import edu.uoregon.casls.aris_android.data_objects.Game;
import edu.uoregon.casls.aris_android.data_objects.Instance;
import edu.uoregon.casls.aris_android.data_objects.Item;
import edu.uoregon.casls.aris_android.data_objects.User;

/**
 * Created by smorison on 8/20/15.
 */
public class PlayerInstancesModel extends ARISModel {


	public Map<Long, Instance> playerInstances = new LinkedHashMap<>();
	public Map<Long, Instance> inventory;// = new LinkedHashMap<>(); to be instantiated later and parcel to logic.
	public Map<Long, Instance> attributes;// = new LinkedHashMap<>(); // todo: is this an array of "Item" or something else?
	
	public int currentWeight;

	// instead of mPlayer representing the player obj as in iOS we'll just grab a reference to it when instantiated
//	public User mPlayer;
//	public ItemsModel mItemsModel;
//	public InstancesModel mInstancesModel;
//	public Game mGame;
//	private Context mContext;
//	private GamePlayActivity mGamePlayActivity;
//	private LogsModel mLogsModel;
	public GamePlayActivity mGamePlayAct;

	// todo - reform the constructor into the initContext version.
	public void PlayerInstancesModel(Context context, User player, ItemsModel items, Game game, InstancesModel instancesModel) { // todo: does player want to be of type playerModel instead of Player?
//		mContext = context;
//		mGamePlayActivity = (GamePlayActivity) mContext;
//		mPlayer = player; // remember: Java passes objects by reference, so mPlayer IS the same object passed in.
//		mItemsModel = items;
//		mInstancesModel = instancesModel;
//		mGame = game;
	}


	public void initContext(GamePlayActivity gamePlayAct) {
		mGamePlayAct = gamePlayAct; // todo: may need leak checking is activity gets recreated.
	}

	public void clearGameData() {
		n_game_data_received = 0;
	}

	public void clearPlayerData() {

	}

	public void playerInstancesTouched() {
		n_game_data_received++;
		mGamePlayAct.mDispatch.services_player_instances_touched(); //		_ARIS_NOTIF_SEND_(@"MODEL_PLAYER_INSTANCES_TOUCHED",nil,nil);
		mGamePlayAct.mDispatch.model_game_piece_available(); //		_ARIS_NOTIF_SEND_(@"MODEL_GAME_PIECE_AVAILABLE",nil,nil);
	}

	public void touchPlayerInstances() {
		// call (via two messages) Game.gamePieceReceived()

	}

	/*{
  NSArray *newInstances = [_MODEL_INSTANCES_ playerInstances];
  [self clearPlayerData];

  Instance *newInstance;
  for(long i = 0; i < newInstances.count; i++)
  {
    newInstance = newInstances[i];
    if(![newInstance.object_type isEqualToString:@"ITEM"] || newInstance.owner_id != _MODEL_PLAYER_.user_id) continue;

    playerInstances[[NSNumber numberWithLong:newInstance.object_id]] = newInstance;
  }
  _ARIS_NOTIF_SEND_(@"MODEL_PLAYER_INSTANCES_AVAILABLE",nil,nil);
}
*/
	public void playerInstancesAvailable() {
		List<Instance> newInstances = new ArrayList<>();
		newInstances = mGamePlayAct.mGame.instancesModel.playerInstances();
//		NSArray *newInstances = [_MODEL_INSTANCES_ playerInstances];
		this.clearPlayerData();

//		Instance newInstance;
		for (Instance newInstance : newInstances) {
			if(!newInstance.object_type.contentEquals("ITEM") || newInstance.owner_id != Long.getLong(mGamePlayAct.mPlayer.user_id)) continue;

		}
//		_ARIS_NOTIF_SEND_(@"MODEL_PLAYER_INSTANCES_AVAILABLE",nil,nil);
	}

	public void calculateWeight() {
		currentWeight = 0;
		Collection<Instance> insts;
		insts = playerInstances.values();

		for (Instance inst : insts) {
			if (inst.object_type.contentEquals("ITEM")) {
//				Item item = mItemsModel.itemForId(inst.object_id);
				Item item = mGamePlayAct.mGame.itemsModel.itemForId(inst.object_id);
				currentWeight += item.weight * inst.qty;
			}
		}
	}

	public long dropItemFromPlayer (long item_id, long qty) {
		Instance pII = playerInstances.get(item_id);
		if (pII == null) return 0; //UH OH! NO INSTANCE TO TAKE ITEM FROM! (shouldn't happen if touchItemsForPlayer was called...)
		if (pII.qty < qty) qty = pII.qty;

		if (!mGamePlayAct.mGame.network_level.contentEquals("LOCAL")) {
			mGamePlayAct.dropItem(item_id, qty); // todo send message back to GamePlayActivity to make this call. TESTTESTTTEST this..
		}
		return this.takeItemFromPlayer(item_id, qty);
	}

	public long takeItemFromPlayer(long item_id, long qty) {
		Instance pII = playerInstances.get(item_id);
		if (pII == null) return 0; //UH OH! NO INSTANCE TO TAKE ITEM FROM! (shouldn't happen if touchItemsForPlayer was called...)
		if (pII.qty < qty) qty = pII.qty;

		return this.setItemsForPlayer(item_id, pII.qty-qty);
	}

	public long giveItemToPlayer(long item_id, long qty) {
		Instance pII = playerInstances.get(item_id);
		if (pII == null) return 0; //UH OH! NO INSTANCE TO GIVE ITEM TO! (shouldn't happen if touchItemsForPlayer was called...)
		if (qty > this.qtyAllowedToGiveForItem(item_id)) qty = this.qtyAllowedToGiveForItem(item_id);

		return this.setItemsForPlayer(item_id, pII.qty + qty);
	}

	public long setItemsForPlayer(long item_id, long qty) {
		Instance pII = playerInstances.get(item_id);
		if (pII == null) return 0; //UH OH! NO INSTANCE TO GIVE ITEM TO! (shouldn't happen if touchItemsForPlayer was called...)

		if (qty < 0) qty = 0;
		if (qty-pII.qty > this.qtyAllowedToGiveForItem(item_id)) qty = pII.qty + this.qtyAllowedToGiveForItem(item_id);

		long oldQty = pII.qty;
		mGamePlayAct.mGame.instancesModel.setQtyForInstanceId(pII.instance_id, qty);
		if (qty > oldQty) mGamePlayAct.mGame.logsModel.playerReceivedItemId(item_id, qty);
		if (qty < oldQty) mGamePlayAct.mGame.logsModel.playerLostItemId(item_id, qty);

		return qty;
	}

	public long qtyOwnedForItem(long item_id) {
		return playerInstances.get(item_id).qty;
	}

	public long qtyOwnedForTag(long tag_id) {
		long q = 0;
		long[] item_ids = mGamePlayAct.mGame.logsModel.objectIdsOfType("ITEM" , tag_id);
		for (int i = 0; i < item_ids.length; i++)
			q += this.qtyOwnedForItem(item_ids[i]);
		return q;
	}

	public long qtyAllowedToGiveForItem(long item_id) {
		this.calculateWeight();

		Item i = mGamePlayAct.mGame.itemsModel.itemForId(item_id);
		long amtMoreCanHold = i.max_qty_in_inventory-this.qtyOwnedForItem(item_id);
		while(mGamePlayAct.mGame.inventory_weight_cap > 0 &&
				(amtMoreCanHold*i.weight + currentWeight) > mGamePlayAct.mGame.inventory_weight_cap)
			amtMoreCanHold--;

		return amtMoreCanHold;
	}

	public Map<Long, Instance> inventory() {
		if(inventory != null) return inventory;

		inventory = new LinkedHashMap<Long, Instance>();
		Collection<Instance> instancearray = playerInstances.values();
		for (Instance inst : instancearray) {

			Item item = (Item) inst.object();
			if (item.type.contentEquals("NORMAL") || item.type.contentEquals("URL"))
			inventory.put(inst.instance_id, inst);
		}
		return inventory;
	}

	public Map<Long, Instance> attributes() {
		if(attributes != null) return attributes;

		attributes = new LinkedHashMap<>();
		Collection<Instance> instancearray = playerInstances.values();
		for (Instance inst : instancearray) {
			Item item = (Item) inst.object();
			if (item.type.contentEquals("ATTRIB"))
			attributes.put(inst.instance_id, inst);
		}
		return attributes;
	}

}
