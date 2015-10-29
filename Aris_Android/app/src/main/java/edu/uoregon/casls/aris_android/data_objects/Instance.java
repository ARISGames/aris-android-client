package edu.uoregon.casls.aris_android.data_objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import edu.uoregon.casls.aris_android.GamePlayActivity;
import edu.uoregon.casls.aris_android.models.ARISModel;

/**
 * Created by smorison on 8/19/15.
 */
public class Instance {
	// An Instance is one or more of a given object. The object type can be an Item, a Plaque, Web Page, etc.
	public long instance_id;
	public String object_type;
	public long object_id;
	public String owner_type;
	public long owner_id;
	public long qty;
	public Boolean infinite_qty;
	public long factory_id;
//	public Date created;
	private String created; // use setters and getters. storing as string 'cus gson hates Dates

	public transient GamePlayActivity mGamePlayAct;

	public Instance() {
//		setCreated(new Date());
	}

	public void initContext(GamePlayActivity gamePlayAct) {
		mGamePlayAct = gamePlayAct;
	}

	public void mergeDataFromInstance(Instance i)
	{
		this.instance_id = i.instance_id;
		this.object_type = i.object_type;
		this.object_id = i.object_id;
		this.owner_type = i.owner_type;
		this.owner_id = i.owner_id;
		this.qty = i.qty;
		this.infinite_qty = i.infinite_qty;
		this.factory_id = i.factory_id;
		this.created = i.getCreated().toString();
	}

	public void setCreated(Date date) {
		if (date.toString().contentEquals("")) date = new Date();
		created = date.toString();

		// gson hates dates with default formatting. This setter adds quotes that apeases gson
//		Gson gson=  new GsonBuilder().setDateFormat("yyyy		-MM-dd'T'HH:mm:ssZ").create();
//		String dateStr = "\"" + date.toString() + "\"";
//		created = gson.fromJson(dateStr, Date.class);
	}

	public Date getCreated() {
		try {
			return new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(created);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null; // satisfy default return obligation.
	}

	public Object object() {
		if (this.object_type.contentEquals("ITEM"))     return mGamePlayAct.mGame.itemsModel.itemForId(this.object_id);
		if (this.object_type.contentEquals("PLAQUE"))   return mGamePlayAct.mGame.plaquesModel.plaqueForId(this.object_id);
		if (this.object_type.contentEquals("WEB_PAGE")) return mGamePlayAct.mGame.webPagesModel.webPageForId(this.object_id);
		if (this.object_type.contentEquals("DIALOG"))   return mGamePlayAct.mGame.dialogsModel.dialogForId(this.object_id);
		if (this.object_type.contentEquals("EVENT_PACKAGE")) return mGamePlayAct.mGame.eventsModel.eventPackageForId(this.object_id);
		if (this.object_type.contentEquals("SCENE"))    return mGamePlayAct.mGame.scenesModel.sceneForId(this.object_id);
		if (this.object_type.contentEquals("NOTE"))
		{
			if(mGamePlayAct.mGame.notesModel.noteForId(this.object_id) == null)
			{
				mGamePlayAct.fetchNoteById(this.object_id);
			}
			return mGamePlayAct.mGame.notesModel.noteForId(this.object_id);
		}
		return null;
	}

	public String name() {
		if (this.object_type.contentEquals("ITEM"))     return mGamePlayAct.mGame.itemsModel.itemForId(this.object_id).name;
		if (this.object_type.contentEquals("PLAQUE"))   return mGamePlayAct.mGame.plaquesModel.plaqueForId(this.object_id).name;
		if (this.object_type.contentEquals("WEB_PAGE")) return mGamePlayAct.mGame.webPagesModel.webPageForId(this.object_id).name;
		if (this.object_type.contentEquals("DIALOG"))   return mGamePlayAct.mGame.dialogsModel.dialogForId(this.object_id).name;
		if (this.object_type.contentEquals("EVENT_PACKAGE")) return mGamePlayAct.mGame.eventsModel.eventPackageForId(this.object_id).name;
		if (this.object_type.contentEquals("SCENE"))    return mGamePlayAct.mGame.scenesModel.sceneForId(this.object_id).name;
		if (this.object_type.contentEquals("NOTE"))
		{
			if (mGamePlayAct.mGame.notesModel.noteForId(this.object_id) == null)
			{
				mGamePlayAct.fetchNoteById(this.object_id);
			}
			return mGamePlayAct.mGame.notesModel.noteForId(this.object_id).name;
		}
		return null;
	}

	public long icon_media_id()
	{
		if (this.object_type.contentEquals("ITEM"))     return mGamePlayAct.mGame.itemsModel.itemForId(this.object_id).icon_media_id;
		if (this.object_type.contentEquals("PLAQUE"))   return mGamePlayAct.mGame.plaquesModel.plaqueForId(this.object_id).icon_media_id;
		if (this.object_type.contentEquals("WEB_PAGE")) return mGamePlayAct.mGame.webPagesModel.webPageForId(this.object_id).icon_media_id;
		if (this.object_type.contentEquals("DIALOG"))   return mGamePlayAct.mGame.dialogsModel.dialogForId(this.object_id).icon_media_id;
		if (this.object_type.contentEquals("EVENT_PACKAGE")) return mGamePlayAct.mGame.eventsModel.eventPackageForId(this.object_id).icon_media_id;
		if (this.object_type.contentEquals("SCENE"))    return mGamePlayAct.mGame.scenesModel.sceneForId(this.object_id).icon_media_id;
		if (this.object_type.contentEquals("NOTE"))
		{
			if (mGamePlayAct.mGame.notesModel.noteForId(this.object_id) == null)
			{
				mGamePlayAct.fetchNoteById(this.object_id);
			}
			return mGamePlayAct.mGame.notesModel.noteForId(this.object_id).icon_media_id;
		}
		return 0;
	}


}
