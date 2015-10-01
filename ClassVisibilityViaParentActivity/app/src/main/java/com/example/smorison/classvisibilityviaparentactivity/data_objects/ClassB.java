package com.example.smorison.classvisibilityviaparentactivity.data_objects;

/**
 * Created by smorison on 10/1/15.
 */
public class ClassB {

	public String name = "I'm Class B";

	public String getName() {
		return this.name;
	}

	// class B has no upward visibility like class A does, because we have not sent in the calling object.
	public ClassB() {
		int j = 0; // dummy sptopping point
	}
}
