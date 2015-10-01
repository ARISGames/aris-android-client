package com.example.smorison.classvisibilityviaparentactivity;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.smorison.classvisibilityviaparentactivity.data_objects.ClassA;
import com.example.smorison.classvisibilityviaparentactivity.data_objects.ClassB;

public class MainActivity extends ActionBarActivity {
	// the intent of this excersize is to show how to allow one object to talk to another object
	//  by referencing the mutual calling object.

	public ClassA classA;
	public ClassB classB;

	TextView tvResult;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		classB = new ClassB();
		classA = new ClassA(this);

		tvResult = (TextView) findViewById(R.id.textViewResult);

	}


	public void onClickButton(View v) {
		String s = classA.getNameOfclassB();
		if (s.length() > 0)
		tvResult.setText(s);
		else
		tvResult.setText("Nothing Yet");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
}
