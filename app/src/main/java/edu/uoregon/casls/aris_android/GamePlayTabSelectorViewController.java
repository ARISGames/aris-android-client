package edu.uoregon.casls.aris_android;

import android.support.v4.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.uoregon.casls.aris_android.data_objects.Tab;
import edu.uoregon.casls.aris_android.services.TabCompareBySortIndex;
import edu.uoregon.casls.aris_android.tab_controllers.MapViewFragment;
import edu.uoregon.casls.aris_android.tab_controllers.QuestsViewFragment;

/**
 * Created by smorison on 3/23/16.
 */
public class GamePlayTabSelectorViewController {

	public transient GamePlayActivity mGamePlayAct;
	public List<Tab> mPlayerTabs = new ArrayList<>();

	//	- (id) initWithDelegate:(id<GamePlayTabSelectorViewControllerDelegate>)d;
	public void initContext(GamePlayActivity gamePlayActivity) {
		mGamePlayAct = gamePlayActivity;
		this.refreshFromModel();
//			_ARIS_NOTIF_LISTEN_(@"MODEL_TABS_NEW_AVAILABLE",  self, @selector(refreshFromModel), nil); // done in dispatcher.
//			_ARIS_NOTIF_LISTEN_(@"MODEL_TABS_LESS_AVAILABLE", self, @selector(refreshFromModel), nil); // done in dispatcher.

	}

	public void loadView()
	{
//		[super loadView];
//		this.view.backgroundColor = [ARISTemplate ARISColorSideNavigationBackdrop];

//		tableView = [[UITableView alloc] init];
//		tableView.delegate = self;
//		tableView.dataSource = self;
//		tableView.opaque = NO;
//		tableView.backgroundColor = [UIColor clearColor];
//
//		leaveGameButton = [[UIView alloc] init];
//		leaveGameButton.userInteractionEnabled = YES;
//		leaveGameButton.backgroundColor = [ARISTemplate ARISColorTextBackdrop];
//		leaveGameButton.opaque = NO;
//		[leaveGameButton addGestureRecognizer:[[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(leaveGameButtonTouched)]];
//
//		leaveGameLabel = [[UILabel alloc] init];
//		leaveGameLabel.textAlignment = NSTextAlignmentLeft;
//		leaveGameLabel.font = [ARISTemplate ARISButtonFont];
//		leaveGameLabel.text = NSLocalizedString(@"BogusTitleKey", @""); //leave game text
//		leaveGameLabel.textColor = [ARISTemplate ARISColorText];
//		leaveGameLabel.accessibilityLabel = @"Leave Game";
//
//		leaveGameArrow = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"arrowBack"]];
//
//		leaveGameLine = [[UIView alloc] init];
//		leaveGameLine.backgroundColor = [UIColor ARISColorLightGray];
//
//		[leaveGameButton addSubview:leaveGameLine];
//		[leaveGameButton addSubview:leaveGameLabel];
//		[leaveGameButton addSubview:leaveGameArrow];
//
//		long headerHeight = 40;
//
//		CGRect headerFrame = CGRectMake(0, 0, this.view.bounds.size.width, headerHeight);
//		UIView *headerView = [[UIView alloc] init];
//		headerView.frame = headerFrame;
//
//		UILabel *gameName = [[UILabel alloc] init];
//		gameName.frame = CGRectMake(57, (headerHeight/2) - (35/2), 200, 35);
//		gameName.text = _MODEL_GAME_.name;
//		[headerView addSubview:gameName];
//
//		ARISMediaView *gameIcon = [[ARISMediaView alloc] init];
//		[gameIcon setDisplayMode:ARISMediaDisplayModeAspectFit];
//		[gameIcon setFrame:CGRectMake(15, (headerHeight/2) - (35/2), 30, 35)];
//		if(_MODEL_GAME_.icon_media_id == 0) [gameIcon setImage:[UIImage imageNamed:@"logo_icon"]];
//		else [gameIcon setMedia:[_MODEL_MEDIA_ mediaForId:_MODEL_GAME_.icon_media_id]];
//		[headerView addSubview:gameIcon];
//
//		[tableView setTableHeaderView:headerView];
//
//		[this.view addSubview:tableView];
//		if(_MODEL_.leave_game_enabled) [this.view addSubview:leaveGameButton];
	}

	public void viewWillLayoutSubviews()
	{
//		[super viewWillLayoutSubviews];

//		tableView.frame = this.view.bounds;
//		if(_MODEL_.leave_game_enabled) tableView.contentInset = UIEdgeInsetsMake(20,0,44,0);
//		else                           tableView.contentInset = UIEdgeInsetsMake(20,0,0,0);
//
//		leaveGameButton.frame = CGRectMake(0,this.view.bounds.size.height-44,this.view.bounds.size.width,44);
//		leaveGameLabel.frame = CGRectMake(30,0,this.view.bounds.size.width-30,44);
//		leaveGameArrow.frame = CGRectMake(6,13,19,19);
//		leaveGameLine.frame = CGRectMake(0,0,this.view.bounds.size.width,1);
	}

//	public void viewDidLoad
//	{
//		[super viewDidLoad];
//		[tableView reloadData];
//	}

	// were sort of already doing all this in the NavBar fragment, right?
	public void refreshFromModel()
	{
		// sort playerTabs by "sort_index" field
//		playerTabs = _ARIS_ARRAY_SORTED_ON_(_MODEL_TABS_.playerTabs,@"sort_index");
//		Collections.sort(mGamePlayAct.mGame.tabsModel.playerTabs, new TabCompareBySortIndex());
		// not used in Android:
//		viewControllers = [[NSMutableArray alloc] initWithCapacity:playerTabs.count];

//		this.mPlayerTabs = mGamePlayAct.mGame.tabsModel.playerTabs; // convenience ref
//		Tab tab;
//		for(long i = 0; i < playerTabs.count; i++)
//		for (Tab tab : playerTabs)
//		{
//			if(!viewControllersDict[[NSNumber numberWithLong:tab.tab_id]))
//			{
//				ARISNavigationController *vc;
//				if(tab.type.contentEquals("QUESTS"))
//				{
//					//if uses icon quest view
//					if(tab.info && ![tab.info.contentEquals(""))
//					{
//						IconQuestsViewController *iconQuestsViewController = [[IconQuestsViewController alloc] initWithTab:tab delegate:
//						(id<QuestsViewControllerDelegate>)delegate];
//						vc = [[ARISNavigationController alloc] initWithRootViewController:iconQuestsViewController];
//					}
//					else
//					{
//						QuestsViewController *questsViewController = [[QuestsViewController alloc] initWithTab:tab delegate:
//						(id<QuestsViewControllerDelegate>)delegate];
//						vc = [[ARISNavigationController alloc] initWithRootViewController:questsViewController];
//					}
//				}
//				else if(tab.type.contentEquals("MAP"))
//				{
//					MapViewController *mapViewController = [[MapViewController alloc] initWithTab:tab delegate:
//					(id<MapViewControllerDelegate>)delegate];
//					vc = [[ARISNavigationController alloc] initWithRootViewController:mapViewController];
//				}
//				else if(tab.type.contentEquals("INVENTORY"))
//				{
//					InventoryViewController *inventoryViewController = [[InventoryViewController alloc] initWithTab:tab delegate:
//					(id<InventoryViewControllerDelegate>)delegate];
//					vc = [[ARISNavigationController alloc] initWithRootViewController:inventoryViewController];
//				}
//				else if(tab.type.contentEquals("DECODER")) //text only
//				{
//					DecoderViewController *decoderViewController = [[DecoderViewController alloc] initWithTab:tab delegate:
//					(id<DecoderViewControllerDelegate>)delegate];
//					vc = [[ARISNavigationController alloc] initWithRootViewController:decoderViewController];
//				}
//				else if(tab.type.contentEquals("SCANNER")) //will be scanner only- supports both for legacy
//				{
//					ScannerViewController *scannerViewController = [[ScannerViewController alloc] initWithTab:tab delegate:
//					(id<ScannerViewControllerDelegate>)delegate];
//					vc = [[ARISNavigationController alloc] initWithRootViewController:scannerViewController];
//				}
//				else if(tab.type.contentEquals("PLAYER"))
//				{
//					AttributesViewController *attributesViewController = [[AttributesViewController alloc] initWithTab:tab delegate:
//					(id<AttributesViewControllerDelegate>)delegate];
//					vc = [[ARISNavigationController alloc] initWithRootViewController:attributesViewController];
//				}
//				else if(tab.type.contentEquals("NOTEBOOK"))
//				{
//					NotebookViewController *notesViewController = [[NotebookViewController alloc] initWithTab:tab delegate:
//					(id<NotebookViewControllerDelegate>)delegate];
//					vc = [[ARISNavigationController alloc] initWithRootViewController:notesViewController];
//				}
//				//non-standard
//				else if(tab.type.contentEquals("DIALOG"))
//				{
//					DialogViewController *dialogViewController = [[DialogViewController alloc] initWithTab:tab delegate:
//					(id<DialogViewControllerDelegate>)delegate];
//					vc = [[ARISNavigationController alloc] initWithRootViewController:dialogViewController];
//				}
//				else if(tab.type.contentEquals("ITEM"))
//				{
//					ItemViewController *itemViewController = [[ItemViewController alloc] initWithTab:tab delegate:
//					(id<ItemViewControllerDelegate>)delegate];
//					vc = [[ARISNavigationController alloc] initWithRootViewController:itemViewController];
//				}
//				else if(tab.type.contentEquals("PLAQUE"))
//				{
//					PlaqueViewController *plaqueViewController = [[PlaqueViewController alloc] initWithTab:tab delegate:
//					(id<PlaqueViewControllerDelegate>)delegate];
//					vc = [[ARISNavigationController alloc] initWithRootViewController:plaqueViewController];
//				}
//				else if(tab.type.contentEquals("WEB_PAGE"))
//				{
//					WebPageViewController *webPageViewController = [[WebPageViewController alloc] initWithTab:tab delegate:
//					(id<WebPageViewControllerDelegate>)delegate];
//					vc = [[ARISNavigationController alloc] initWithRootViewController:webPageViewController];
//				}
//				if(vc) [viewControllersDict setObject:vc forKey:[NSNumber numberWithLong:tab.tab_id]];
//			}
//
//			if(viewControllersDict[[NSNumber numberWithLong:tab.tab_id])) [viewControllers addObject:viewControllersDict[[NSNumber numberWithLong:tab.tab_id]]];
//			else _ARIS_LOG_(@"ERROR: Tab from server could not be created. KeyString %ld should exist but does not have a matching VC", tab.tab_id);
//		}
//
//		if(this.view) [tableView reloadData];
	}
	
	public void setupDefaultTab()
	{
		String tag = "";
		String fragViewToDisplay = "";
		// sort playerTabs by "sort_index" field
//		Collections.sort(mGamePlayAct.mGame.tabsModel.playerTabs, new TabCompareBySortIndex());

		this.mPlayerTabs = mGamePlayAct.mGame.tabsModel.playerTabs; // convenience ref
		Collections.sort(mPlayerTabs, new TabCompareBySortIndex());
		Tab tab = mPlayerTabs.get(0); // get the first item on the list (which is sorted now by sort_index)

		if (true) // todo: temp. do we have a need to check stuff here? nulls?
		{
			if (tab.type.equalsIgnoreCase("QUESTS")) {
				//if uses icon quest view
				if (!tab.info.isEmpty() && !tab.info.contentEquals("")) { // todo: Android doesn't have icon view currently
					if (mGamePlayAct.questsViewFragment == null) {
						mGamePlayAct.questsViewFragment = new QuestsViewFragment();
						if (mGamePlayAct.questsViewFragment.isAdded()) return;
						mGamePlayAct.questsViewFragment.initContext(mGamePlayAct);
						//						mGamePlayAct.questsViewFragment.initWithInstance(i);
						tag = mGamePlayAct.questsViewFragment.toString();
						FragmentTransaction ft = mGamePlayAct.getSupportFragmentManager().beginTransaction();
//						ft.add(R.id.fragment_view_container, mGamePlayAct.questsViewFragment, tag); //set tag.
						ft.addToBackStack(tag);
//						ft.attach(mGamePlayAct.questsViewFragment); // was .show()
						ft.replace(R.id.fragment_view_container, mGamePlayAct.questsViewFragment, tag);
						ft.commit();
//						mGamePlayAct.getSupportFragmentManager().executePendingTransactions();
//						mGamePlayAct.setAsFrontmostFragment(tag);
					}
				}
				else {
//						QuestsViewController *questsViewController = [[QuestsViewController alloc] initWithTab:tab delegate:
//						(id<QuestsViewControllerDelegate>)delegate];
//						vc = [[ARISNavigationController alloc] initWithRootViewController:questsViewController];
					if (mGamePlayAct.questsViewFragment == null) {
						mGamePlayAct.questsViewFragment = new QuestsViewFragment();
						if (mGamePlayAct.questsViewFragment.isAdded()) return;
						mGamePlayAct.questsViewFragment.initContext(mGamePlayAct);
						//						mGamePlayAct.questsViewFragment.initWithInstance(i);
						tag = mGamePlayAct.questsViewFragment.toString();
						FragmentTransaction ft = mGamePlayAct.getSupportFragmentManager().beginTransaction();
//						ft.add(R.id.fragment_view_container, mGamePlayAct.questsViewFragment, tag); //set tag.
						ft.addToBackStack(tag);
//						ft.attach(mGamePlayAct.questsViewFragment); // was .show()
						ft.replace(R.id.fragment_view_container, mGamePlayAct.questsViewFragment, tag);
						ft.commit();
						mGamePlayAct.getSupportFragmentManager().executePendingTransactions();
//						mGamePlayAct.setAsFrontmostFragment(tag);
					}

				}
			}
			else if (tab.type.equalsIgnoreCase("MAP")) {
//					MapViewController *mapViewController = [[MapViewController alloc] initWithTab:tab delegate:
//					(id<MapViewControllerDelegate>)delegate];
//					vc = [[ARISNavigationController alloc] initWithRootViewController:mapViewController];
				if (mGamePlayAct.mapViewFragment == null) {
					mGamePlayAct.mapViewFragment = new MapViewFragment();
					mGamePlayAct.mapViewFragment.initContext(mGamePlayAct);
					//						mGamePlayAct.mapViewFragment.initWithInstance(i);
					tag = mGamePlayAct.mapViewFragment.toString();
					FragmentTransaction ft = mGamePlayAct.getSupportFragmentManager().beginTransaction();
					ft.add(R.id.fragment_view_container, mGamePlayAct.mapViewFragment, tag); //set tag.
					ft.addToBackStack(tag);
					ft.attach(mGamePlayAct.mapViewFragment); // was .show()
					ft.commit();
					mGamePlayAct.getSupportFragmentManager().executePendingTransactions();
					mGamePlayAct.setAsFrontmostFragment(tag);
				}

			}
			else if (tab.type.equalsIgnoreCase("INVENTORY")) {
//					InventoryViewController *inventoryViewController = [[InventoryViewController alloc] initWithTab:tab delegate:
//					(id<InventoryViewControllerDelegate>)delegate];
//					vc = [[ARISNavigationController alloc] initWithRootViewController:inventoryViewController];
			}
			else if (tab.type.equalsIgnoreCase("DECODER")) //text only
			{
//					DecoderViewController *decoderViewController = [[DecoderViewController alloc] initWithTab:tab delegate:
//					(id<DecoderViewControllerDelegate>)delegate];
//					vc = [[ARISNavigationController alloc] initWithRootViewController:decoderViewController];
			}
			else if (tab.type.equalsIgnoreCase("SCANNER")) //will be scanner only- supports both for legacy
			{
//					ScannerViewController *scannerViewController = [[ScannerViewController alloc] initWithTab:tab delegate:
//					(id<ScannerViewControllerDelegate>)delegate];
//					vc = [[ARISNavigationController alloc] initWithRootViewController:scannerViewController];
			}
			else if (tab.type.equalsIgnoreCase("PLAYER")) {
//					AttributesViewController *attributesViewController = [[AttributesViewController alloc] initWithTab:tab delegate:
//					(id<AttributesViewControllerDelegate>)delegate];
//					vc = [[ARISNavigationController alloc] initWithRootViewController:attributesViewController];
			}
			else if (tab.type.equalsIgnoreCase("NOTEBOOK")) {
//					NotebookViewController *notesViewController = [[NotebookViewController alloc] initWithTab:tab delegate:
//					(id<NotebookViewControllerDelegate>)delegate];
//					vc = [[ARISNavigationController alloc] initWithRootViewController:notesViewController];
			}
			//non-standard
			else if (tab.type.equalsIgnoreCase("DIALOG")) {
//					DialogViewController *dialogViewController = [[DialogViewController alloc] initWithTab:tab delegate:
//					(id<DialogViewControllerDelegate>)delegate];
//					vc = [[ARISNavigationController alloc] initWithRootViewController:dialogViewController];
			}
			else if (tab.type.equalsIgnoreCase("ITEM")) {
//					ItemViewController *itemViewController = [[ItemViewController alloc] initWithTab:tab delegate:
//					(id<ItemViewControllerDelegate>)delegate];
//					vc = [[ARISNavigationController alloc] initWithRootViewController:itemViewController];
			}
			else if (tab.type.equalsIgnoreCase("PLAQUE")) {
//					PlaqueViewController *plaqueViewController = [[PlaqueViewController alloc] initWithTab:tab delegate:
//					(id<PlaqueViewControllerDelegate>)delegate];
//					vc = [[ARISNavigationController alloc] initWithRootViewController:plaqueViewController];
			}
			else if (tab.type.equalsIgnoreCase("WEB_PAGE")) {
//					WebPageViewController *webPageViewController = [[WebPageViewController alloc] initWithTab:tab delegate:
//					(id<WebPageViewControllerDelegate>)delegate];
//					vc = [[ARISNavigationController alloc] initWithRootViewController:webPageViewController];
			}
//				if(vc) [viewControllersDict setObject:vc forKey:[NSNumber numberWithLong:tab.tab_id]];
		}

//		if(viewControllersDict[[NSNumber numberWithLong:tab.tab_id])) [viewControllers addObject:viewControllersDict[[NSNumber numberWithLong:tab.tab_id]]];
//		else _ARIS_LOG_(@"ERROR: Tab from server could not be created. KeyString %ld should exist but does not have a matching VC", tab.tab_id);
//
//		if(this.view) [tableView reloadData];
	}

//	- (ARISNavigationController *) firstViewController
//	{
//		if(viewControllers.count < 1) return nil;
//		return viewControllers[0];
//	}

	public void leaveGameButtonTouched()
	{
		mGamePlayAct.leaveGame();
	}


	public void requestDisplayTab(Tab t)
	{
//		Tab tab;
//
//		for(long i = 0; i < playerTabs.count; i++)
//		{
//			tab = playerTabs[i];
//			if(tab == t)
//			{
//				if(tab.type.contentEquals("SCANNER"))
//				{
//					ARISNavigationController *navigation = (ARISNavigationController*)viewControllersDict[[NSNumber numberWithLong:tab.tab_id]];
//					[((ScannerViewController *)navigation.topViewController) setPrompt:tab.info];
//					// clean this up later.
//					tab.info = @"";
//
//				}
//				else if(tab.type.contentEquals("DIALOG"))
//				{
//					DialogViewController *dialogViewController = [[DialogViewController alloc] initWithTab:tab delegate:
//					(id<DialogViewControllerDelegate>)delegate];
//					vc = [[ARISNavigationController alloc] initWithRootViewController:dialogViewController];
//				}
//				else if(tab.type.contentEquals("ITEM"))
//				{
//					ItemViewController *itemViewController = [[ItemViewController alloc] initWithTab:tab delegate:
//					(id<ItemViewControllerDelegate>)delegate];
//					vc = [[ARISNavigationController alloc] initWithRootViewController:itemViewController];
//				}
//				else if(tab.type.contentEquals("PLAQUE"))
//				{
//					PlaqueViewController *plaqueViewController = [[PlaqueViewController alloc] initWithTab:tab delegate:
//					(id<PlaqueViewControllerDelegate>)delegate];
//					vc = [[ARISNavigationController alloc] initWithRootViewController:plaqueViewController];
//				}
//				else if(tab.type.contentEquals("WEB_PAGE"))
//				{
//					WebPageViewController *webPageViewController = [[WebPageViewController alloc] initWithTab:tab delegate:
//					(id<WebPageViewControllerDelegate>)delegate];
//					vc = [[ARISNavigationController alloc] initWithRootViewController:webPageViewController];
//				}
//
//				if(vc) //new vc was created- replace old one
//				{
//					for(long i = 0; i < viewControllers.count; i++)
//					{
//						if(viewControllers[i] == viewControllersDict[[NSNumber numberWithLong:tab.tab_id]))
//						{
//							[viewControllersDict setObject:vc forKey:[NSNumber numberWithLong:tab.tab_id]];
//							[viewControllers setObject:vc atIndexedSubscript:(NSUInteger)i];
//						}
//					}
//				}
//
//				[delegate viewControllerRequestedDisplay:viewControllersDict[[NSNumber numberWithLong:tab.tab_id]]];
//				return;
//			}
//		}
		
	}

	public void requestDisplayScannerWithPrompt(String p)
	{
//		Tab *tab;
//		for(long i = 0; i < playerTabs.count; i++)
//		{
//			tab = playerTabs[i];
//			if(tab.type.contentEquals("SCANNER"))
//			{
//				[((ScannerViewController *)viewControllersDict[[NSNumber numberWithLong:tab.tab_id])) setPrompt:p];
//				[delegate viewControllerRequestedDisplay:viewControllersDict[[NSNumber numberWithLong:tab.tab_id]]];
//				return;
//			}
//		}
	}

//	public void tableView(UITableView *)
//	{
//		Tab *tab = playerTabs[indexPath.row];
//		[self requestDisplayTab:tab];
//	}

	public void refreshTabTable()
	{
//		[tableView reloadData];
	}

}
