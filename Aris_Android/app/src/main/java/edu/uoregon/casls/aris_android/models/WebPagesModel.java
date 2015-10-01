package edu.uoregon.casls.aris_android.models;

import java.util.LinkedHashMap;
import java.util.Map;

import edu.uoregon.casls.aris_android.GamePlayActivity;
import edu.uoregon.casls.aris_android.data_objects.Item;
import edu.uoregon.casls.aris_android.data_objects.WebPage;

/**
 * Created by smorison on 8/20/15.
 */
public class WebPagesModel extends ARISModel {

	public Map<Long, WebPage> webpages = new LinkedHashMap<>();
	public GamePlayActivity mGamePlayAct;

	public void initContext(GamePlayActivity gamePlayAct) {
		mGamePlayAct = gamePlayAct; // todo: may need leak checking is activity gets recreated.
	}

	public void clearGameData() {
		webpages.clear();
		n_game_data_received = 0;
	}

	public void requestWebPages() {

	}

	public long nGameDataToReceive ()
	{
		return 1;
	}

	public WebPage webPageForId(long object_id) {
		return webpages.get(object_id);
	}
}
