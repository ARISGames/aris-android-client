Home
Android
2D Graphics
Animation
Core Class
Database
Date Type
Development
File
Game
Hardware
Media
Network
Security
UI
User Event

Search
Demonstration of hiding and showing fragments. : Fragment « Core Class « Android
AndroidCore ClassFragment




Demonstration of hiding and showing fragments.

  
/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.apis.app;

import com.example.android.apis.R;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * Demonstration of hiding and showing fragments.
 */
public class FragmentHideShow extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_hide_show);

        // The content view embeds two fragments; now retrieve them and attach
        // their "hide" button.
        FragmentManager fm = getFragmentManager();
        addShowHideListener(R.id.frag1hide, fm.findFragmentById(R.id.fragment1));
        addShowHideListener(R.id.frag2hide, fm.findFragmentById(R.id.fragment2));
    }

    void addShowHideListener(int buttonId, final Fragment fragment) {
        final Button button = (Button)findViewById(buttonId);
        button.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.setCustomAnimations(android.R.animator.fade_in,
                        android.R.animator.fade_out);
                if (fragment.isHidden()) {
                    ft.show(fragment);
                    button.setText("Hide");
                } else {
                    ft.hide(fragment);
                    button.setText("Show");
                }
                ft.commit();
            }
        });
    }

    public static class FirstFragment extends Fragment {
        TextView mTextView;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.labeled_text_edit, container, false);
            View tv = v.findViewById(R.id.msg);
            ((TextView)tv).setText("The fragment saves and restores this text.");

            // Retrieve the text editor, and restore the last saved state if needed.
            mTextView = (TextView)v.findViewById(R.id.saved);
            if (savedInstanceState != null) {
                mTextView.setText(savedInstanceState.getCharSequence("text"));
            }
            return v;
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);

            // Remember the current text, to restore if we later restart.
            outState.putCharSequence("text", mTextView.getText());
        }
    }

    public static class SecondFragment extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.labeled_text_edit, container, false);
            View tv = v.findViewById(R.id.msg);
            ((TextView)tv).setText("The TextView saves and restores this text.");

            // Retrieve the text editor and tell it to save and restore its state.
            // Note that you will often set this in the layout XML, but since
            // we are sharing our layout with the other fragment we will customize
            // it here.
            ((TextView)v.findViewById(R.id.saved)).setSaveEnabled(true);
            return v;
        }
    }
}
//fragment_hide_show.xml

<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright (C) 2010 The Android Open Source Project

     Licensed under the Apache License, Version 2.0 (the "License");
     you may not use this file except in compliance with the License.
     You may obtain a copy of the License at

          http://www.apache.org/licenses/LICENSE-2.0

     Unless required by applicable law or agreed to in writing, software
     distributed under the License is distributed on an "AS IS" BASIS,
     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     See the License for the specific language governing permissions and
     limitations under the License.
-->

<!-- Top-level content view for the layout fragment sample. -->

<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:orientation="vertical"
    android:gravity="center_horizontal"
    android:layout_width="match_parent" android:layout_height="match_parent">

    <TextView android:layout_width="match_parent" android:layout_height="wrap_content"
        android:gravity="center_vertical|center_horizontal"
        android:textAppearance="?android:attr/textAppearanceMedium"
        android:text="Demonstration of hiding and showing fragments." />

    <LinearLayout android:orientation="horizontal" android:padding="4dip"
        android:gravity="center_vertical" android:layout_weight="1"
        android:layout_width="match_parent" android:layout_height="wrap_content">

        <Button android:id="@+id/frag1hide"
            android:layout_width="wrap_content" android:layout_height="wrap_content"
            android:text="Hide" />

        <fragment android:name="com.example.android.apis.app.FragmentHideShow$FirstFragment"
                android:id="@+id/fragment1" android:layout_weight="1"
                android:layout_width="0px" android:layout_height="wrap_content" />

    </LinearLayout>

    <LinearLayout android:orientation="horizontal" android:padding="4dip"
        android:gravity="center_vertical" android:layout_weight="1"
        android:layout_width="match_parent" android:layout_height="wrap_content">

        <Button android:id="@+id/frag2hide"
            android:layout_width="wrap_content" android:layout_height="wrap_content"
            android:text="Hide" />

        <fragment android:name="com.example.android.apis.app.FragmentHideShow$SecondFragment"
                android:id="@+id/fragment2" android:layout_weight="1"
                android:layout_width="0px" android:layout_height="wrap_content" />

    </LinearLayout>

</LinearLayout>

   
    
  










Related examples in the same category
1.	extends Fragment		
2.	Demonstration of using ListFragment to show a list of items from a canned array.		
3.	Fragment Stack		
4.	Use Fragment to propagate state across activity instances when an activity needs to be restarted due to a configuration change.		
5.	extends Fragment to display result		
6.	Demonstration of PreferenceFragment, showing a single fragment in an activity.		
7.	Get child element, inner element, outer element, parse Fragment		
java2s.com  | Email:info at java2s.com | © Demo Source and Support. All rights reserved.