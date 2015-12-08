package edu.uoregon.casls.aris_android.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.uoregon.casls.aris_android.GamePlayActivity;
import edu.uoregon.casls.aris_android.data_objects.Game;
import edu.uoregon.casls.aris_android.data_objects.User;

/**
 * Created by smorison on 9/29/15.
 */
public class UsersModel extends ARISModel {

	public transient GamePlayActivity mGamePlayAct;
	public transient Game mGame;

	Map<Long, User> users = new HashMap<>();
	Map<Long, String> blacklist = new HashMap<>();

	public UsersModel(GamePlayActivity gamePlayAct) {
		super();
		initContext(gamePlayAct);
	}

	public void initContext(GamePlayActivity gamePlayAct) {
		mGamePlayAct = gamePlayAct; // todo: may need leak checking is activity gets recreated.
		mGame = mGamePlayAct.mGame;
	}

	public void clearGameData() {
		n_game_data_received = 0;
	}

	public long nGameDataToReceive() {
		return 1;
	}

	public void usersReceived(Map<String, User> mGameUsers) {

	}

	public void clearData() {
		users.clear();
		blacklist.clear();
	}

	public void requestGameData() {
		this.requestUsers();
	}

	public void usersReceived(List<User> newUsers) {
		this.updateUsers(newUsers);
	}

	public void userReceived(User user) {
		List<User> newUsers = new ArrayList<>();
		newUsers.add(user);
		this.updateUsers(newUsers);
	}

	public void updateUsers(List<User> newUsers) {
		long newUserId;
		for (User newUser : newUsers) {
			newUserId = Integer.parseInt(newUser.user_id);
			if (!users.containsKey(newUserId)) {
				users.put(newUserId, newUser); // setObject:newUser forKey:newUserId];
				blacklist.remove(newUserId); // removeObjectForKey:[NSNumber numberWithLong:newUserId]];
			}
			else
				users.get(newUserId).mergeDataFromUser(newUser); // objectForKey:newUserId] mergeDataFromUser:newUser];
		}
		n_game_data_received++;
		mGamePlayAct.mDispatch.model_users_available(); //_ARIS_NOTIF_SEND_(@"MODEL_USERS_AVAILABLE",nil,nil);
		mGamePlayAct.mDispatch.game_piece_available(); //_ARIS_NOTIF_SEND_(@"GAME_PIECE_AVAILABLE",nil,nil); //weird... not "game" piece. whatever.
	}

	public List<User> conformUsersListToFlyweight(List<User> newUsers) {
		List<User> conformingUsers = new ArrayList<>();
		User u;
		for (User newUser : newUsers) {
			if ((u = this.userForId(Long.parseLong(newUser.user_id))) == null)
				conformingUsers.add(u); // addObject:u];
		}
		return conformingUsers;
	}

	public void requestUsers() {
		mGamePlayAct.mAppServices.fetchUsers();
	}

	public void requestUser(long t) {
		mGamePlayAct.mAppServices.fetchUserById(t);
	}

	// null user (id == 0) NOT flyweight!!! (to allow for temporary customization safety)
	public User userForId(long user_id) {
		User t = users.get(user_id);  // objectForKey:[NSNumber numberWithLong:user_id]];
		if (t == null) {
			blacklist.put(user_id, "true");// setObject:@"true" forKey:[NSNumber numberWithLong:user_id]];
			this.requestUser(user_id);
			return new User();
		}
		return t;
	}


}
