/*
 * Copyright (C) 2011 Jake Wharton
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
package net.basilwang;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.widget.ArrayAdapter;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.ActionBar.TabListener;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;

public class ViewPagerActivity extends SherlockFragmentActivity implements TabListener {
	private String[] viewtypes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //setTheme(SampleList.THEME); //Used for theme switching in samples
        super.onCreate(savedInstanceState);

        /*
         * Most interactions with what would otherwise be the system UI should
         * now be done through this instance. Content, title, action bar, and
         * menu inflation can all be done.
         *
         * All of the base activities use this class to provide the normal
         * action bar functionality so everything that they can do is possible
         * using this static attacFragmentStatePagerSupporthment method.
         *
         * Calling something like setContentView or getActionBar on this
         * instance is required in order to properly set up the wrapped layout
         * and dispatch menu events (if they are needed).
         */
        //getSherlock().setUiOptions(ActivityInfo.UIOPTION_SPLIT_ACTION_BAR_WHEN_NARROW);
 
        
        //((TextView)findViewById(R.id.text)).setText(R.string.static_attach_content);
        viewtypes = getResources().getStringArray(R.array.viewtype);

		Context context = getSupportActionBar().getThemedContext();
		ArrayAdapter<CharSequence> viewTypeList = ArrayAdapter
				.createFromResource(context, R.array.viewtype,
						R.layout.sherlock_spinner_item);
		viewTypeList
				.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);

		//getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		//getSupportActionBar().setListNavigationCallbacks(viewTypeList, this);
		
		
		 getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
	        for (String viewtype : viewtypes) {
	            ActionBar.Tab tab = getSupportActionBar().newTab();
	            tab.setText(viewtype);
	            tab.setTabListener(this);
	            getSupportActionBar().addTab(tab);
	        }
    }


    /*
     * In order to receive these events you need to implement an interface
     * from ActionBarSherlock so it knows to dispatch to this callback.
     * There are three possible interface you can implement, one for each
     * menu event.
     *
     * Remember, there are no superclass implementations of these methods so
     * you must return a value with meaning.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Used to put dark icons on light action bar
        // boolean isLight = SampleList.THEME == R.style.Theme_Sherlock_Light;

        menu.add("Config")
            .setIcon(R.drawable.ic_compose)
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

//        menu.add("Search")
//            .setIcon( R.drawable.ic_search)
//            .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

//        menu.add("Refresh")
//            .setIcon( R.drawable.ic_refresh)
//            .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
//       
        SubMenu sub = menu.addSubMenu("登录");
        sub.add(0, R.style.Theme_Sherlock, 0, "Default");
        sub.add(0, R.style.Theme_Sherlock_Light, 0, "Light");
        sub.add(0, R.style.Theme_Sherlock_Light_DarkActionBar, 0, "Light (Dark Action Bar)");
        sub.getItem().setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

        
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // If this callback does not handle the item click, onPerformDefaultAction
        // of the ActionProvider is invoked. Hence, the provider encapsulates the
        // complete functionality of the menu item.
    	if(item.getTitle()=="Config")
    	{
    		Intent intent=new Intent();
    		intent.setClass(this, MyPreferenceActivity.class);
            startActivity(intent);
    	}
    	 return false;
    }

	@Override
	public void onTabReselected(Tab arg0, FragmentTransaction arg1) {
		// TODO Auto-generated method stub
		
	}

 
	@Override
	public void onTabSelected(Tab tab, FragmentTransaction arg1) {
		// TODO Auto-generated method stub
//		Intent intent=new Intent();
//		//&&this.getClass()!=CurriculumActivity.class
//		if(tab.getText()==viewtypes[0])
//		{
//		intent.setClass(this, CurriculumActivity.class);
//        startActivity(intent);
//		}
	}


	@Override
	public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {
		// TODO Auto-generated method stub
		
	}
}
