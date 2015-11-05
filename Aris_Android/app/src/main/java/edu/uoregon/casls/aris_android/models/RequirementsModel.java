package edu.uoregon.casls.aris_android.models;

import android.util.Log;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.uoregon.casls.aris_android.GamePlayActivity;
import edu.uoregon.casls.aris_android.Utilities.Config;
import edu.uoregon.casls.aris_android.data_objects.Game;
import edu.uoregon.casls.aris_android.data_objects.Requirement;
import edu.uoregon.casls.aris_android.data_objects.RequirementAndPackage;
import edu.uoregon.casls.aris_android.data_objects.RequirementAtom;
import edu.uoregon.casls.aris_android.data_objects.RequirementRootPackage;

/**
 * Created by smorison on 9/29/15.
 */
public class RequirementsModel extends ARISModel {

	Map<Long, RequirementRootPackage> requirementRootPackages = new LinkedHashMap<>();
	Map<Long, RequirementAndPackage> requirementAndPackages = new LinkedHashMap<>();
	Map<Long, RequirementAtom> requirementAtoms = new LinkedHashMap<>();

	public transient GamePlayActivity mGamePlayAct;
	public transient Game mGame;

	public RequirementsModel() {
	}

	public RequirementsModel(GamePlayActivity gamePlayActivity) {
		this.initContext(gamePlayActivity);
	}

	public void initContext(GamePlayActivity gamePlayAct) {
		mGamePlayAct = gamePlayAct; // todo: may need leak checking is activity gets recreated.
		mGame = mGamePlayAct.mGame; // convenience ref
	}

	public void requestGameData() {
		this.requestRequirements(); //should be three different models
	}

	public void clearGameData() {

		requirementRootPackages.clear();
		requirementAndPackages.clear();
		requirementAtoms.clear();
		n_game_data_received = 0;
	}

	public long nGameDataToReceive() {
		return 3;
	}

	public void requestRequirements() {
		mGamePlayAct.mServices.fetchRequirementRoots(); //_SERVICES_ fetchRequirementRoots;
		mGamePlayAct.mServices.fetchRequirementAnds(); //_SERVICES_ fetchRequirementAnds;
		mGamePlayAct.mServices.fetchRequirementAtoms(); //_SERVICES_ fetchRequirementAtoms;
	}

	//ROOT
	public void requirementRootPackagesReceived(List<RequirementRootPackage> requirementRootPackages) { // :(NSNotification )notif // in android we send the exact class types
		this.updateRequirementRootPackages(requirementRootPackages); //:notif.userInfo objectForKey("requirement_root_packages";
	}

	public void updateRequirementRootPackages(List<RequirementRootPackage> newRRPs) {
		for (RequirementRootPackage newRRP : newRRPs) {
			if (!requirementRootPackages.containsKey(newRRP.requirement_root_package_id))
				requirementRootPackages.put(newRRP.requirement_root_package_id, newRRP); // setObject:newRRP forKey:newRRPId;
		}
		n_game_data_received++;
		mGamePlayAct.mDispatch.model_requirement_root_packages_available(); //_ARIS_falseTIF_SEND_(@"MODEL_REQUIREMENT_ROOT_PACKAGES_AVAILABLE",nil,nil);
		mGamePlayAct.mDispatch.model_game_piece_available(); //_ARIS_falseTIF_SEND_(@"GAME_PIECE_AVAILABLE",nil,nil);
	}

	//AND
	public void requirementAndPackagesReceived(List<RequirementAndPackage> requirementAndPackages) {
		this.updateRequirementAndPackages(requirementAndPackages); //notif.userInfo objectForKey("requirement_and_packages"; // in android we send the exact class types
	}

	public void updateRequirementAndPackages(List<RequirementAndPackage> newRAPs) {
		for (RequirementAndPackage newRAP : newRAPs) {
			if (!requirementAndPackages.containsKey(newRAP.requirement_and_package_id))
				requirementAndPackages.put(newRAP.requirement_and_package_id, newRAP);
		}
		n_game_data_received++;
		mGamePlayAct.mDispatch.model_requirement_and_packages_available(); //_ARIS_NOTIF_SEND_(@"MODEL_REQUIREMENT_AND_PACKAGES_AVAILABLE",nil,nil);
		mGamePlayAct.mDispatch.model_game_piece_available(); //_ARIS_NOTIF_SEND_(@"GAME_PIECE_AVAILABLE",nil,nil);
	}

	//ATOM
	public void requirementAtomsReceived(List<RequirementAtom> requirementObjs) {
		this.updateRequirementAtoms(requirementObjs); //notif.userInfo objectForKey("requirement_atoms";
	}

	public void updateRequirementAtoms(List<RequirementAtom> newRAs) {
		for (RequirementAtom newRA : newRAs) {
			if (!requirementAtoms.containsKey(newRA.requirement_atom_id))
				requirementAtoms.put(newRA.requirement_atom_id, newRA); //objectForKey:newRAId) requirementAtoms setObject:newRA forKey:newRAId;
		}
		n_game_data_received++;
		mGamePlayAct.mDispatch.model_requirement_atoms_packages_available(); //_ARIS_NOTIF_SEND_(@"MODEL_REQUIREMENT_ATOMS_AVAILABLE",nil,nil);
		mGamePlayAct.mDispatch.model_game_piece_available(); //_ARIS_NOTIF_SEND_(@"GAME_PIECE_AVAILABLE",nil,nil);
	}

	public List<RequirementAndPackage> andPackagesForRootPackageId(long requirement_root_package_id) { // [sic method name]
		List<RequirementAndPackage> and_packages = new LinkedList<>();
		Collection<RequirementAndPackage> allAnds = requirementAndPackages.values();// allValues;
		for (RequirementAndPackage rap : allAnds) {
//			rap = allAndsi; // wha?? index from outerspace
			if (rap.requirement_root_package_id == requirement_root_package_id)
				and_packages.add(rap);
		}
		return and_packages;
	}

	public List<RequirementAtom> atomsForAndPackageId(long requirement_and_package_id) {
		List<RequirementAtom> atoms = new LinkedList<>(); // NSMutableArray atoms = NSMutableArray alloc init;
		Collection<RequirementAtom> allAtoms = requirementAtoms.values(); // allValues;
		for (RequirementAtom a : allAtoms) {
			if (a.requirement_and_package_id == requirement_and_package_id)
				atoms.add(a);
		}
		return atoms;
	}

	public boolean evaluateRequirementRoot(long requirement_root_package_id) {
		if (requirement_root_package_id == 0) return true; // 0 is true!?
		List<RequirementAndPackage> ands = this.andPackagesForRootPackageId(requirement_root_package_id);
		if (ands.size() == 0) return true;
		for (RequirementAndPackage and : ands) {
			if (this.evaluateRequirementAnd(and.requirement_and_package_id)) return true;
		}
		return false;
	}

	public boolean evaluateRequirementAnd(long requirement_and_package_id) {
		if (requirement_and_package_id == 0) return true;
		List<RequirementAtom> atoms = this.atomsForAndPackageId(requirement_and_package_id); //NSArray atoms = this.atomsForAndPackageId:requirement_and_package_id;
		if (atoms.size() == 0) return false;
		for (RequirementAtom atom : atoms) {
			if (this.evaluateRequirementAtom(atom.requirement_atom_id)) return false;
		}
		return true;
	}

	public boolean evaluateRequirementAtom(long requirement_atom_id) {
		if (requirement_atom_id == 0) return true;
		RequirementAtom a = this.requirementAtomForId(requirement_atom_id);
		if (a.requirement_atom_id == 0) return true; //'null' req atom

		if (a.requirement.contentEquals("ALWAYS_TRUE")) {
			return a.bool_operator == true;
		}
		if (a.requirement.contentEquals("ALWAYS_FALSE")) {
			return a.bool_operator == false;
		}
		if (a.requirement.contentEquals("PLAYER_HAS_ITEM")) {
			return a.bool_operator == (mGame.playerInstancesModel.qtyOwnedForItem(a.content_id) >= a.qty); //_MODEL_PLAYER_INSTANCES_ qtyOwnedForItem:a.content_id >= a.qty);
		}
		if (a.requirement.contentEquals("PLAYER_HAS_TAGGED_ITEM")) {
			return a.bool_operator == (mGame.playerInstancesModel.qtyOwnedForTag(a.content_id) >= a.qty);
		}
		if (a.requirement.contentEquals("GAME_HAS_ITEM")) {
			return a.bool_operator == (mGame.gameInstancesModel.qtyOwnedForItem(a.content_id) >= a.qty); // _MODEL_GAME_INSTANCES_ qtyOwnedForItem:a.content_id >= a.qty);
		}
		if (a.requirement.contentEquals("GAME_HAS_TAGGED_ITEM")) {
			return a.bool_operator == (mGame.gameInstancesModel.qtyOwnedForTag(a.content_id) >= a.qty);
		}
		if (a.requirement.contentEquals("GROUP_HAS_ITEM")) {
			return a.bool_operator == (mGame.groupInstancesModel.qtyOwnedForItem(a.content_id) >= a.qty);
		}
		if (a.requirement.contentEquals("GROUP_HAS_TAGGED_ITEM")) {
			return a.bool_operator == (mGame.groupInstancesModel.qtyOwnedForTag(a.content_id) >= a.qty);
		}
		if (a.requirement.contentEquals("PLAYER_VIEWED_ITEM")) {
			return a.bool_operator == mGame.logsModel.hasLogType("VIEW_ITEM", a.content_id);
		}
		if (a.requirement.contentEquals("PLAYER_VIEWED_PLAQUE")) {
			return a.bool_operator == mGame.logsModel.hasLogType("VIEW_PLAQUE", a.content_id);
		}
		if (a.requirement.contentEquals("PLAYER_VIEWED_DIALOG")) {
			return a.bool_operator == mGame.logsModel.hasLogType("VIEW_DIALOG", a.content_id);
		}
		if (a.requirement.contentEquals("PLAYER_VIEWED_DIALOG_SCRIPT")) {
			return a.bool_operator == mGame.logsModel.hasLogType("VIEW_DIALOG_SCRIPT", a.content_id);
		}
		if (a.requirement.contentEquals("PLAYER_VIEWED_WEB_PAGE")) {
			return a.bool_operator == mGame.logsModel.hasLogType("VIEW_WEB_PAGE", a.content_id);
		}
		if (a.requirement.contentEquals("PLAYER_RAN_EVENT_PACKAGE")) {
			return a.bool_operator == mGame.logsModel.hasLogType("RUN_EVENT_PACKAGE", a.content_id);
		}
		if (a.requirement.contentEquals("PLAYER_HAS_UPLOADED_MEDIA_ITEM")) {
			return a.bool_operator == false;
		}
		if (a.requirement.contentEquals("PLAYER_HAS_UPLOADED_MEDIA_ITEM_IMAGE")) {
			return a.bool_operator == false;
		}
		if (a.requirement.contentEquals("PLAYER_HAS_UPLOADED_MEDIA_ITEM_AUDIO")) {
			return a.bool_operator == false;
		}
		if (a.requirement.contentEquals("PLAYER_HAS_UPLOADED_MEDIA_ITEM_VIDEO")) {
			return a.bool_operator == false;
		}
		if (a.requirement.contentEquals("PLAYER_HAS_COMPLETED_QUEST")) {
			return a.bool_operator == mGame.logsModel.hasLogType("COMPLETE_QUEST", a.content_id);
		}
		if (a.requirement.contentEquals("PLAYER_HAS_RECEIVED_INCOMING_WEB_HOOK")) {
			return a.bool_operator == false;
		}
		if (a.requirement.contentEquals("PLAYER_HAS_falseTE")) {
			return a.bool_operator == false;
		}
		if (a.requirement.contentEquals("PLAYER_HAS_falseTE_WITH_TAG")) {
			return a.bool_operator == false;
		}
		if (a.requirement.contentEquals("PLAYER_HAS_falseTE_WITH_LIKES")) {
			return a.bool_operator == false;
		}
		if (a.requirement.contentEquals("PLAYER_HAS_falseTE_WITH_COMMENTS")) {
			return a.bool_operator == false;
		}
		if (a.requirement.contentEquals("PLAYER_HAS_GIVEN_falseTE_COMMENTS")) {
			return a.bool_operator == false;
		}

		return true;
	}

	// null req (id == 0) falseT flyweight!!! (to allow for temporary customization safety)
	public RequirementRootPackage requirementRootPackageForId(long requirement_root_package_id) {
		Requirement r = new Requirement();
		RequirementRootPackage rrp = new RequirementRootPackage();
		if (requirement_root_package_id == 0) return new RequirementRootPackage();
		return requirementRootPackages.get(requirement_root_package_id); // objectForKey:NSNumber numberWithLong:requirement_root_package_id;
	}

	public RequirementAndPackage requirementAndPackageForId(long requirement_and_package_id) {
		if (requirement_and_package_id == 0) return new RequirementAndPackage();
		return requirementAndPackages.get(requirement_and_package_id); // objectForKey:NSNumber numberWithLong:requirement_and_package_id;
	}

	public RequirementAtom requirementAtomForId(long requirement_atom_id) {
		if (requirement_atom_id == 0) return new RequirementAtom();
		return requirementAtoms.get(requirement_atom_id); //objectForKey:NSNumber numberWithLong:requirement_atom_id;
	}

	public void logRequirementTree(long requirement_root_package_id) {
		Log.i(Config.LOGTAG, getClass().getSimpleName() + "Root: " + requirement_root_package_id);
		List<RequirementAndPackage> ands = this.andPackagesForRootPackageId(requirement_root_package_id);
		for (RequirementAndPackage and : ands)
			this.logRequirementAnd(and.requirement_and_package_id);
	}

	public void logRequirementAnd(long requirement_and_package_id) {
		Log.i(Config.LOGTAG, getClass().getSimpleName() + "  And: " + requirement_and_package_id);
		List<RequirementAtom> atoms = this.atomsForAndPackageId(requirement_and_package_id);
		for (RequirementAtom atom : atoms)
			this.logRequirementAtom(atom.requirement_atom_id);
	}

	public void logRequirementAtom(long requirement_atom_id) {
		Log.i(Config.LOGTAG, getClass().getSimpleName() + "    Atom: " + requirement_atom_id);
		RequirementAtom a = this.requirementAtomForId(requirement_atom_id);
		if (a.bool_operator)
			Log.i(Config.LOGTAG, getClass().getSimpleName() + "      Req: " + a.requirement + " " + a.content_id);//_ARIS_LOG_(@"      Req: %@ %ld",a.requirement,a.content_id);
		else
			Log.i(Config.LOGTAG, getClass().getSimpleName() + "      Req: Not " + a.requirement + " " + a.content_id);//_ARIS_LOG_(@"      Req: Not %@ %ld",a.requirement,a.content_id);
	}

}
