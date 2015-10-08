package edu.uoregon.casls.aris_android.models;

import java.util.LinkedHashMap;
import java.util.List;
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

	public void webPagesReceived(List<WebPage> webPages) {
		this.updateWebPages(webPages);
	}


	public void updateWebPages(List<WebPage> newWebPages)
	{
		WebPage *newWebPage;
		NSNumber *newWebPageId;
		for(long i = 0; i < newWebPages.count; i++)
		{
			newWebPage = [newWebPages objectAtIndex:i];
			newWebPageId = [NSNumber numberWithLong:newWebPage.web_page_id];
			if(![webPages objectForKey:newWebPageId]) [webPages setObject:newWebPage forKey:newWebPageId];
		}
		_ARIS_NOTIF_SEND_(@"MODEL_WEB_PAGES_AVAILABLE",nil,nil);
		_ARIS_NOTIF_SEND_(@"MODEL_GAME_PIECE_AVAILABLE",nil,nil);
		n_game_data_received++;
	}

	- (void) requestWebPages
	{
		[_SERVICES_ fetchWebPages];
	}

// null webpage (id == 0) NOT flyweight!!! (to allow for temporary customization safety)
	- (WebPage *) webPageForId:(long)web_page_id
	{
		if(!web_page_id) return [[WebPage alloc] init];
		return [webPages objectForKey:[NSNumber numberWithLong:web_page_id]];
	}

}
