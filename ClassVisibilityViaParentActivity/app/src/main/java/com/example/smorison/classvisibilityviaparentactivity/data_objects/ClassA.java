package com.example.smorison.classvisibilityviaparentactivity.data_objects;

import android.content.Context;
import android.content.ContextWrapper;

import com.example.smorison.classvisibilityviaparentactivity.MainActivity;

/**
 * Created by smorison on 10/1/15.
 */
public class ClassA {

	public String name = "I'm Class A";
	MainActivity mMainAct;

	public ClassA(MainActivity mainActivity) {
		mMainAct = mainActivity;
	}

	public String getName() {
		return this.name;
	}

	public String getNameOfclassB() {
		return mMainAct.classB.getName(); // could also be return mMainAct.classB.name;
	}
}
