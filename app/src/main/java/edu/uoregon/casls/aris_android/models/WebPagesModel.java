package edu.uoregon.casls.aris_android.models;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.uoregon.casls.aris_android.GamePlayActivity;
import edu.uoregon.casls.aris_android.data_objects.WebPage;

/**
 * Created by smorison on 8/20/15.
 */
public class WebPagesModel extends ARISModel {

	public Map<Long, WebPage> webpages = new LinkedHashMap<>();
	public transient GamePlayActivity mGamePlayAct;

	public void initContext(GamePlayActivity gamePlayAct) {
		mGamePlayAct = gamePlayAct; // todo: may need leak checking is activity gets recreated.
	}

	public void clearGameData() {
		webpages.clear();
		n_game_data_received = 0;
	}

	public long nGameDataToReceive() {
		return 1;
	}

	public void requestGameData() {
		this.requestWebPages();
	}

	public void webPagesReceived(List<WebPage> webPages) {
		this.updateWebPages(webPages);
	}

	public void updateWebPages(List<WebPage> newWebPages) {
		long newWebPageId;

		for (WebPage newWebPage : newWebPages) {
			newWebPageId = newWebPage.web_page_id;
			if (!webpages.containsKey(newWebPageId))
				webpages.put(newWebPageId, newWebPage); // setObject:newWebPage forKey:newWebPageId];
		}
		mGamePlayAct.mDispatch.model_web_pages_available(); //;_ARIS_NOTIF_SEND_(@"MODEL_WEB_PAGES_AVAILABLE",nil,nil);
		mGamePlayAct.mDispatch.game_piece_available(); //_ARIS_NOTIF_SEND_(@"GAME_PIECE_AVAILABLE",nil,nil);
		n_game_data_received++;
	}

	public void requestWebPages() {
		mGamePlayAct.mAppServices.fetchWebPages();
	}

	// null webpage (id == 0) NOT flyweight!!! (to allow for temporary customization safety)
	public WebPage webPageForId(long web_page_id) {
		if (web_page_id == 0) return new WebPage();
		return webpages.get(web_page_id); // objectForKey:[NSNumber numberWithLong:web_page_id]];
	}

}
