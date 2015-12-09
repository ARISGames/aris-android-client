package edu.uoregon.casls.aris_android.models;

import java.util.LinkedHashMap;
import java.util.Map;

import edu.uoregon.casls.aris_android.GamePlayActivity;
import edu.uoregon.casls.aris_android.data_objects.Game;

/**
 * Created by smorison on 12/9/15.
 */
public class GamesModel {
	public Map<Long, Game> games = new LinkedHashMap<>();

	public transient GamePlayActivity mGamePlayAct;
//	public           Game            playerGame;

	// todo: populate methods as needed instead of using all the stock iOS ones.

	public void initContext(GamePlayActivity gamePlayAct) {
		mGamePlayAct = gamePlayAct; // todo: may need leak checking is activity gets recreated.
	}

	public Game updateGame(Game g) {
		Game existingG = this.gameForId(g.game_id);
		if (existingG != null)
			existingG.mergeDataFromGame(g);
		else
			this.games.put(g.game_id, g);
		// this dispatch may be unnecessary for the Android version
		mGamePlayAct.mDispatch.model_game_available(this.gameForId(g.game_id)); //_ARIS_NOTIF_SEND_(@"MODEL_GAME_AVAILABLE",nil,@{@"game":[self gameForId:g.game_id]});

		return this.gameForId(g.game_id);
	}

	public Game gameForId(long game_id) {
		return this.games.get(game_id);
	}
}
