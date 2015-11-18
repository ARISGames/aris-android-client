package edu.uoregon.casls.aris_android.models;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.uoregon.casls.aris_android.GamePlayActivity;
import edu.uoregon.casls.aris_android.data_objects.ObjectTag;
import edu.uoregon.casls.aris_android.data_objects.Tag;

/**
 * Created by smorison on 8/20/15.
 */
public class TagsModel extends ARISModel {

	public Map<Long, Tag> tags = new LinkedHashMap<>();
	public Map<Long, ObjectTag> objectTags = new LinkedHashMap<>();
	public transient GamePlayActivity mGamePlayAct;

	public void initContext(GamePlayActivity gamePlayAct) {
		mGamePlayAct = gamePlayAct; // todo: may need leak checking is activity gets recreated.
	}

	public void clearGameData() {
		tags.clear();
		objectTags.clear();
		n_game_data_received = 0;
	}

	public long nGameDataToReceive() {
		return 2;
	}

	public void requestGameData() {
		this.requestTags();
	}

	public void tagsReceived(List<Tag> tags) {
		this.updateTags(tags);
	}

	public void objectTagsReceived(List<ObjectTag> objTags) {
		this.updateObjectTags(objTags);
	}

	public void updateTags(List<Tag> newTags) {
		long newTagId;
		for (Tag newTag : newTags) {
			newTagId = newTag.tag_id;
			if (!tags.containsKey(newTagId))
				tags.put(newTagId, newTag);// setObject:newTag forKey:newTagId];
		}
		n_game_data_received++;
		mGamePlayAct.mDispatch.tags_available(); //_ARIS_NOTIF_SEND_(@"MODEL_TAGS_AVAILABLE",nil,nil);
		mGamePlayAct.mDispatch.game_piece_available(); //_ARIS_NOTIF_SEND_(@"GAME_PIECE_AVAILABLE",nil,nil);
	}

	public void updateObjectTags(List<ObjectTag> newObjectTags) {
		long newObjectTagId;
		for (ObjectTag newObjectTag : newObjectTags) {
			newObjectTagId = newObjectTag.object_tag_id;
			if (!objectTags.containsKey(newObjectTagId))
				objectTags.put(newObjectTagId, newObjectTag);// setObject:newObjectTag forKey:newObjectTagId];
		}
		n_game_data_received++;
		mGamePlayAct.mDispatch.object_tags_available(); //_ARIS_NOTIF_SEND_(@"MODEL_OBJECT_TAGS_AVAILABLE",nil,nil);
		mGamePlayAct.mDispatch.game_piece_available(); //_ARIS_NOTIF_SEND_(@"GAME_PIECE_AVAILABLE",nil,nil);
	}

	public void requestTags() {
		mGamePlayAct.mServices.fetchTags();
		mGamePlayAct.mServices.fetchObjectTags();
	}

	public List<Tag> tagsForObjectType(String t, long object_id) {
		Collection<ObjectTag> otags = objectTags.values();// allValues];
		List<Tag> objects_tags = new LinkedList<>();
		for (ObjectTag otag : otags) {
			if (otag.object_type.contentEquals(t) && otag.object_id == object_id)
				objects_tags.add(this.tagForId(otag.tag_id));
		}
		return objects_tags;
	}

	public List<Long> objectIdsOfType(String t, long tag_id) {
		List<Long> objs = new LinkedList<>();
		Collection<ObjectTag> otags = objectTags.values();// allValues];
		for (ObjectTag ot : otags) {
			if (ot.object_type.contentEquals(t) && ot.tag_id == tag_id)
				objs.add(ot.object_id); // addObject:[long numberWithLong:ot.object_id]];
		}
		return objs;
	}

	public void removeTagsFromObjectType(String t, long object_id) {
		Collection<ObjectTag> otags = objectTags.values();// allValues];
		for (ObjectTag otag : otags) {
			if (otag.object_type.contentEquals(t) && otag.object_id == object_id) {
				objectTags.remove(otag.object_tag_id);// removeObjectForKey: [long numberWithLong:otag.object_tag_id]];
			}
		}
	}

	public Collection<Tag> tags() {
		return tags.values();
	}

	// null tag (id == 0) NOT flyweight!!! (to allow for temporary customization safety)
	public Tag tagForId(long tag_id) {
		if (tag_id == 0) return new Tag();
		return tags.get(tag_id);// objectForKey:[long numberWithLong:tag_id]];
	}

	// null objectTag (id == 0) NOT flyweight!!! (to allow for temporary customization safety)
	public ObjectTag objectTagForId(long object_tag_id) {
		if (object_tag_id == 0) return new ObjectTag();
		return objectTags.get(object_tag_id); // objectForKey:[long numberWithLong:object_tag_id]];
	}

}
