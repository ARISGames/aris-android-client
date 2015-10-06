package edu.uoregon.casls.aris_android.models;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.uoregon.casls.aris_android.GamePlayActivity;
import edu.uoregon.casls.aris_android.data_objects.Requirement;

/**
 * Created by smorison on 9/29/15.
 */
public class RequirementsModel extends ARISModel {

	Map<Long, Requirement.RequirementRootPackage> requirementRootPackages;
	Map<Long, Requirement.RequirementAndPackage> requirementAndPackages;
	Map<Long, Requirement.RequirementAtom> requirementAtoms;

	public GamePlayActivity mGamePlayAct;

	public void initContext(GamePlayActivity gamePlayAct) {
		mGamePlayAct = gamePlayAct; // todo: may need leak checking is activity gets recreated.
	}

	public void clearGameData() {

		requirementRootPackages.clear();
		requirementAndPackages.clear();
		requirementAtoms.clear();
		n_game_data_received = 0;
	}

	public long nGameDataToReceive () {
		return 3;
	}


	public void requestRequirements
	{
		mGamePlayAct.mServices.fetchRequirementRoots(); //[_SERVICES_ fetchRequirementRoots];
		mGamePlayAct.mServices.fetchRequirementAnds(); //[_SERVICES_ fetchRequirementAnds];
		mGamePlayAct.mServices.fetchRequirementAtoms(); //[_SERVICES_ fetchRequirementAtoms];
	}

//ROOT
	public void requirementRootPackagesReceived(List<Requirement.RequirementRootPackage> requirementRootPackages) { // :(NSNotification *)notif
		this.updateRequirementRootPackages(requirementRootPackages); //:[notif.userInfo objectForKey:@"requirement_root_packages"]];
	}

	public void updateRequirementRootPackages(List<Requirement.RequirementRootPackage> newRRPs)
	{
		Long newRRPId;
		for (Requirement.RequirementRootPackage newRRP : newRRPs)
		{
			newRRPId = newRRP.requirement_root_package_id;
			if(requirementRootPackages.get(newRRPId) == null) [requirementRootPackages.put(newRRPId, newRRP); // setObject:newRRP forKey:newRRPId];
		}
		n_game_data_received++;
		mGamePlayAct.mDispatch.model_requirement_root_packages_available(); //_ARIS_falseTIF_SEND_(@"MODEL_REQUIREMENT_ROOT_PACKAGES_AVAILABLE",nil,nil);
		mGamePlayAct.mDispatch.model_game_piece_available(); //_ARIS_falseTIF_SEND_(@"MODEL_GAME_PIECE_AVAILABLE",nil,nil);
	}

//AND
	public void requirementAndPackagesReceived:(NSNotification *)notif
	{
		[self updateRequirementAndPackages:[notif.userInfo objectForKey:@"requirement_and_packages"]];
	}
	public void updateRequirementAndPackages:(NSArray *)newRAPs
	{
		RequirementAndPackage *newRAP;
		NSNumber *newRAPId;
		for(long i = 0; i < newRAPs.count; i++)
		{
			newRAP = [newRAPs objectAtIndex:i];
			newRAPId = [NSNumber numberWithLong:newRAP.requirement_and_package_id];
			if(![requirementAndPackages objectForKey:newRAPId]) [requirementAndPackages setObject:newRAP forKey:newRAPId];
		}
		n_game_data_received++;
		_ARIS_falseTIF_SEND_(@"MODEL_REQUIREMENT_AND_PACKAGES_AVAILABLE",nil,nil);
		_ARIS_falseTIF_SEND_(@"MODEL_GAME_PIECE_AVAILABLE",nil,nil);
	}

//ATOM
	public void requirementAtomsReceived:(NSNotification *)notif
	{
		[self updateRequirementAtoms:[notif.userInfo objectForKey:@"requirement_atoms"]];
	}
	public void updateRequirementAtoms:(NSArray *)newRAs
	{
		RequirementAtom *newRA;
		NSNumber *newRAId;
		for(long i = 0; i < newRAs.count; i++)
		{
			newRA = [newRAs objectAtIndex:i];
			newRAId = [NSNumber numberWithLong:newRA.requirement_atom_id];
			if(![requirementAtoms objectForKey:newRAId]) [requirementAtoms setObject:newRA forKey:newRAId];
		}
		n_game_data_received++;
		_ARIS_falseTIF_SEND_(@"MODEL_REQUIREMENT_ATOMS_AVAILABLE",nil,nil);
		_ARIS_falseTIF_SEND_(@"MODEL_GAME_PIECE_AVAILABLE",nil,nil);
	}


	public List<Requirement.RequirementAndPackage> andPackagesForRootPackageId:(long)requirement_root_package_id
	{
		RequirementAndPackage *rap;
		NSMutableArray *and_packages = [[NSMutableArray alloc] init];
		NSArray *allAnds = [requirementAndPackages allValues];
		for(long i = 0; i < allAnds.count; i++)
		{
			rap = allAnds[i];
			if(rap.requirement_root_package_id == requirement_root_package_id)
			[and_packages addObject:rap];
		}
		return and_packages;
	}

	public List<Requirement.RequirementAtom> atomsForAndPackageId(long requirement_and_package_id)
	{
		RequirementAtom *a;
		List<Requirement.RequirementAtom>  atoms = new LinkedList<>(); // NSMutableArray *atoms = [[NSMutableArray alloc] init];
		NSArray *allAtoms = [requirementAtoms allValues];
		for(long i = 0; i < allAtoms.count; i++)
		{
			a = allAtoms[i];
			if(a.requirement_and_package_id == requirement_and_package_id)
			[atoms addObject:a];
		}
		return atoms;
	}


	public boolean evaluateRequirementRoot(long requirement_root_package_id)
	{
		if(requirement_root_package_id == 0) return true;
		NSArray *ands = [self andPackagesForRootPackageId:requirement_root_package_id];
		if(ands.count == 0) return true;
		for(int i = 0; i < ands.count; i++)
		{
			if([self evaluateRequirementAnd:((RequirementAndPackage *)ands[i]).requirement_and_package_id]) return true;
		}
		return false;
	}
	public boolean evaluateRequirementAnd(long requirement_and_package_id)
	{
		if(requirement_and_package_id == 0) return true;
		List<Requirement.RequirementAtom> = this.atomsForAndPackageId(requirement_and_package_id); //NSArray *atoms = [self atomsForAndPackageId:requirement_and_package_id];
		if(atoms.count == 0) return false;
		for(int i = 0; i < atoms.count; i++)
		{
			if(![self evaluateRequirementAtom:((RequirementAtom *)atoms[i]).requirement_atom_id]) return false;
		}
		return true;
	}
	public boolean evaluateRequirementAtom(long requirement_atom_id)
	{
		if(requirement_atom_id == 0) return true;
		Requirement.RequirementAtom a = this.requirementAtomForId(requirement_atom_id);
		if(a.requirement_atom_id == 0) return true; //'null' req atom

		if(a.requirement.contentEquals("ALWAYS_TRUE")
		{
			return a.bool_operator == true;
		}
		if(a.requirement.contentEquals("ALWAYS_FALSE")
		{
			return a.bool_operator == false;
		}
		if(a.requirement.contentEquals("PLAYER_HAS_ITEM")
		{
			return a.bool_operator == ([_MODEL_PLAYER_INSTANCES_ qtyOwnedForItem:a.content_id] >= a.qty);
		}
		if(a.requirement.contentEquals("PLAYER_HAS_TAGGED_ITEM")
		{
			return a.bool_operator == ([_MODEL_PLAYER_INSTANCES_ qtyOwnedForTag:a.content_id] >= a.qty);
		}
		if(a.requirement.contentEquals("GAME_HAS_ITEM")
		{
			return a.bool_operator == ([_MODEL_GAME_INSTANCES_ qtyOwnedForItem:a.content_id] >= a.qty);
		}
		if(a.requirement.contentEquals("GAME_HAS_TAGGED_ITEM")
		{
			return a.bool_operator == ([_MODEL_GAME_INSTANCES_ qtyOwnedForTag:a.content_id] >= a.qty);
		}
		if(a.requirement.contentEquals("GROUP_HAS_ITEM")
		{
			return a.bool_operator == ([_MODEL_GROUP_INSTANCES_ qtyOwnedForItem:a.content_id] >= a.qty);
		}
		if(a.requirement.contentEquals("GROUP_HAS_TAGGED_ITEM")
		{
			return a.bool_operator == ([_MODEL_GROUP_INSTANCES_ qtyOwnedForTag:a.content_id] >= a.qty);
		}
		if(a.requirement.contentEquals("PLAYER_VIEWED_ITEM")
		{
			return a.bool_operator == [_MODEL_LOGS_ hasLogType:@"VIEW_ITEM" content:a.content_id];
		}
		if(a.requirement.contentEquals("PLAYER_VIEWED_PLAQUE")
		{
			return a.bool_operator == [_MODEL_LOGS_ hasLogType:@"VIEW_PLAQUE" content:a.content_id];
		}
		if(a.requirement.contentEquals("PLAYER_VIEWED_DIALOG")
		{
			return a.bool_operator == [_MODEL_LOGS_ hasLogType:@"VIEW_DIALOG" content:a.content_id];
		}
		if(a.requirement.contentEquals("PLAYER_VIEWED_DIALOG_SCRIPT")
		{
			return a.bool_operator == [_MODEL_LOGS_ hasLogType:@"VIEW_DIALOG_SCRIPT" content:a.content_id];
		}
		if(a.requirement.contentEquals("PLAYER_VIEWED_WEB_PAGE")
		{
			return a.bool_operator == [_MODEL_LOGS_ hasLogType:@"VIEW_WEB_PAGE" content:a.content_id];
		}
		if(a.requirement.contentEquals("PLAYER_RAN_EVENT_PACKAGE")
		{
			return a.bool_operator == [_MODEL_LOGS_ hasLogType:@"RUN_EVENT_PACKAGE" content:a.content_id];
		}
		if(a.requirement.contentEquals("PLAYER_HAS_UPLOADED_MEDIA_ITEM")
		{
			return a.bool_operator == false;
		}
		if(a.requirement.contentEquals("PLAYER_HAS_UPLOADED_MEDIA_ITEM_IMAGE")
		{
			return a.bool_operator == false;
		}
		if(a.requirement.contentEquals("PLAYER_HAS_UPLOADED_MEDIA_ITEM_AUDIO")
		{
			return a.bool_operator == false;
		}
		if(a.requirement.contentEquals("PLAYER_HAS_UPLOADED_MEDIA_ITEM_VIDEO")
		{
			return a.bool_operator == false;
		}
		if(a.requirement.contentEquals("PLAYER_HAS_COMPLETED_QUEST")
		{
			return a.bool_operator == [_MODEL_LOGS_ hasLogType:@"COMPLETE_QUEST" content:a.content_id];
		}
		if(a.requirement.contentEquals("PLAYER_HAS_RECEIVED_INCOMING_WEB_HOOK")
		{
			return a.bool_operator == false;
		}
		if(a.requirement.contentEquals("PLAYER_HAS_falseTE")
		{
			return a.bool_operator == false;
		}
		if(a.requirement.contentEquals("PLAYER_HAS_falseTE_WITH_TAG")
		{
			return a.bool_operator == false;
		}
		if(a.requirement.contentEquals("PLAYER_HAS_falseTE_WITH_LIKES")
		{
			return a.bool_operator == false;
		}
		if(a.requirement.contentEquals("PLAYER_HAS_falseTE_WITH_COMMENTS")
		{
			return a.bool_operator == false;
		}
		if(a.requirement.contentEquals("PLAYER_HAS_GIVEN_falseTE_COMMENTS")
		{
			return a.bool_operator == false;
		}

		return true;
	}

// null req (id == 0) falseT flyweight!!! (to allow for temporary customization safety)
	- (RequirementRootPackage *) requirementRootPackageForId:(long)requirement_root_package_id
	{
		if(!requirement_root_package_id) return [[RequirementRootPackage alloc] init];
		return [requirementRootPackages objectForKey:[NSNumber numberWithLong:requirement_root_package_id]];
	}
	- (RequirementAndPackage *) requirementAndPackageForId:(long)requirement_and_package_id
	{
		if(!requirement_and_package_id) return [[RequirementAndPackage alloc] init];
		return [requirementAndPackages objectForKey:[NSNumber numberWithLong:requirement_and_package_id]];
	}
	- (RequirementAtom *) requirementAtomForId:(long)requirement_atom_id
	{
		if(!requirement_atom_id) return [[RequirementAtom alloc] init];
		return [requirementAtoms objectForKey:[NSNumber numberWithLong:requirement_atom_id]];
	}

	public void logRequirementTree:(long)requirement_root_package_id
	{
		_ARIS_LOG_(@"Root: %ld",requirement_root_package_id);
		NSArray *ands = [self andPackagesForRootPackageId:requirement_root_package_id];
		for(int i = 0; i < ands.count; i++)
		[self logRequirementAnd:((RequirementAndPackage *)ands[i]).requirement_and_package_id];
	}
	public void logRequirementAnd:(long)requirement_and_package_id
	{
		_ARIS_LOG_(@"  And: %ld",requirement_and_package_id);
		NSArray *atoms = [self atomsForAndPackageId:requirement_and_package_id];
		for(int i = 0; i < atoms.count; i++)
		[self logRequirementAtom:((RequirementAtom *)atoms[i]).requirement_atom_id];
	}
	public void logRequirementAtom:(long)requirement_atom_id
	{
		_ARIS_LOG_(@"    Atom: %ld",requirement_atom_id);
		RequirementAtom *a = [self requirementAtomForId:requirement_atom_id];
		if(a.bool_operator) _ARIS_LOG_(@"      Req: %@ %ld",a.requirement,a.content_id);
		else _ARIS_LOG_(@"      Req: Not %@ %ld",a.requirement,a.content_id);
	}


}
