package edu.uoregon.casls.aris_android.models;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.uoregon.casls.aris_android.GamePlayActivity;
import edu.uoregon.casls.aris_android.data_objects.Factory;

/**
 * Created by smorison on 8/20/15.
 */
public class FactoriesModel extends ARISModel {

	public Map<Long, Factory> factories = new LinkedHashMap<>();
	public GamePlayActivity mGamePlayAct;

	public void initContext(GamePlayActivity gamePlayAct) {
		mGamePlayAct = gamePlayAct; // todo: may need leak checking is activity gets recreated.
	}

	public void clearGameData() {
		factories.clear();
		n_game_data_received = 0;
	}

	public long nGameDataToReceive () {
		return 1;
	}

	public void requestGameData()
	{
		this.requestFactories();
	}

	public void factoriesReceived(List<Factory> newFactories)
	{
		this.updateFactories(newFactories);
	}

	public void updateFactories(List<Factory> newFactories)
	{
		long newFactoryId;
		for (Factory newFactory : newFactories)
		{
			newFactoryId = newFactory.factory_id;
			if(!factories.containsKey(newFactoryId)) factories.put(newFactoryId, newFactory);
		}
		mGamePlayAct.mDispatch.model_factories_available(); //_ARIS_NOTIF_SEND_(@"MODEL_FACTORIES_AVAILABLE",nil,nil);
		mGamePlayAct.mDispatch.model_game_piece_available(); //_ARIS_NOTIF_SEND_(@"MODEL_GAME_PIECE_AVAILABLE",nil,nil);
		n_game_data_received = 1;
	}

	public void requestFactories()
	{
		mGamePlayAct.mServices.fetchFactories();
	}

// null factory (id == 0) NOT flyweight!!! (to allow for temporary customization safety)
	public Factory factoryForId(long factory_id)
	{
		if(factory_id != 0) return new Factory();
		return factories.get(factory_id); //NSNumber numberWithLong:factory_id]];
	}


}
