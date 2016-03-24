package edu.uoregon.casls.aris_android.services;

import java.util.Comparator;

import edu.uoregon.casls.aris_android.data_objects.Tab;

/**
 * Created by smorison on 3/24/16.
 */
public class TabCompareBySortIndex implements Comparator<Tab> {

	@Override
	public int compare(Tab t1, Tab t2) {
		return t1.sort_index - t2.sort_index;
	}
}
