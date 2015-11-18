package edu.uoregon.casls.aris_android.media;

/**
 * Created by smorison on 11/18/15.
 * Java equivallent of the class agnostic ARISDelegatHandler class in iOS. Java doesn't know how to
 * deal with such runtime defined class types so I'm going to give it class specific versions.
 * In this case it will be a Delegate Handler for MediaModel objects.
 */
public class ARISDelegateHandle<T> {

	public Object mmDelegate;

	public ARISDelegateHandle(Object o) {
		mmDelegate = o;
	}

	public Object initWithDelegate(Object o) {
		mmDelegate = o;
		return this.mmDelegate;
	}

	public Object delegate() {
		return mmDelegate;
	}

	public void invalidate() {
		mmDelegate = null;
	}

}
