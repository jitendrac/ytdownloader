/***
 	Copyright (c) 2012-2013 Samuele Rini
 	
	This program is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.
	
	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
	GNU General Public License for more details.
	
	You should have received a copy of the GNU General Public License
	along with this program. If not, see http://www.gnu.org/licenses
	
	***
	
	https://github.com/dentex/ytdownloader/
    https://sourceforge.net/projects/ytdownloader/
	
	***
	
	Different Licenses and Credits where noted in code comments.
*/

package dentex.youtube.downloader.menu;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.Window;

import com.bugsense.trace.BugSenseHandler;

import dentex.youtube.downloader.R;
import dentex.youtube.downloader.utils.PopUps;
import dentex.youtube.downloader.utils.Utils;

public class TutorialsActivity extends Activity {
	
	public static final String DEBUG_TAG = "TutorialsActivity";
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BugSenseHandler.leaveBreadcrumb("TutorialsActivity_onCreate");
        this.setTitle(R.string.title_activity_tutorials);
    	
    	getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        
    	// Theme init
    	Utils.themeInit(this);
    	
        // Language init
    	Utils.langInit(this);
        
        // Load default preferences values
        PreferenceManager.setDefaultValues(this, R.xml.tutorials, false);
        
        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new TutorialsFragment())
                .commit();
        setupActionBar();
	}
	
	private void setupActionBar() {
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			//NavUtils.navigateUpFromSameTask(this);
	        finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

    public static class TutorialsFragment extends PreferenceFragment /*implements OnSharedPreferenceChangeListener */{
    	
    	private Preference tutRead;
    	
    	@Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.tutorials);
            
            /*for(int i=0;i<getPreferenceScreen().getPreferenceCount();i++){
                initSummary(getPreferenceScreen().getPreference(i));
            }*/
            
            tutRead = (Preference) findPreference("tutorials_read");
            tutRead.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            	
                public boolean onPreferenceClick(Preference preference) {
                	AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
                	adb.setTitle(R.string.tutorials_read_title);
                	adb.setItems(R.array.tutorials_read_entries, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							
		    				switch (which) {
	    					case 0: // settings
	    						quickstartsSettings();
	    						break;
			    			case 1: // dashboard
			    				quickstartsDashboard();
			    				break;
			    			case 2: // share
			    				generalAppTutorial();
			    				break;
			    			case 3: // ssh
			    				sshTutorial();
		    				}
		        		}
                	});
                	
                	Utils.secureShowDialog(getActivity(), adb);
                	
                	return true;
            	}
            });

    	}
    	
    	private void quickstartsSettings() {
			PopUps.showPopUp(getString(R.string.quick_start_settings_title), 
					getString(R.string.quick_start_settings_text), 
					"info", 
					getActivity());
		}
        
        private void quickstartsDashboard() {
			PopUps.showPopUp(getString(R.string.quick_start_dashboard_title), 
					getString(R.string.quick_start_dashboard_text), 
					"info", 
					getActivity());
		}
        
        private void generalAppTutorial() { //ShareActivity
        	PopUps.showPopUp(getString(R.string.tutorial_share_title), 
					getString(R.string.tutorial_share_text), 
					"info", 
					getActivity());
        }
        
        private void sshTutorial() { //ShareActivity -> SSH
        	PopUps.showPopUp(getString(R.string.tutorial_ssh_title), 
					getString(R.string.tutorial_ssh_text), 
					"info", 
					getActivity());
        }
        
	    /*public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
	    	updatePrefSummary(findPreference(key));
		}
	
		private void initSummary(Preference p){
	    	if (p instanceof PreferenceCategory){
	    		PreferenceCategory pCat = (PreferenceCategory)p;
	    		for(int i=0;i<pCat.getPreferenceCount();i++){
	    			initSummary(pCat.getPreference(i));
	    	    }
	    	}else{
	    		updatePrefSummary(p);
	    	}
	    }
	    
	    private void updatePrefSummary(Preference p){
	    	if (p instanceof ListPreference) {
	    		ListPreference listPref = (ListPreference) p;
	    	    p.setSummary(listPref.getEntry());
	    	}
	    }*/
    }
}
