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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.view.MenuItem;

import com.bugsense.trace.BugSenseHandler;

import dentex.youtube.downloader.R;
import dentex.youtube.downloader.utils.PopUps;
import dentex.youtube.downloader.utils.Utils;

public class SocialActivity extends Activity {
	
	public static final String DEBUG_TAG = "SocialActivity";
	public static String chooserSummary;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BugSenseHandler.leaveBreadcrumb("SocialActivity_onCreate");
        this.setTitle(R.string.settings_cat_social);
    	
    	// Theme init
    	Utils.themeInit(this);
    	
        // Language init
    	Utils.langInit(this);
        
        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SocialFragment())
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
    
    @SuppressLint("SetJavaScriptEnabled")
	public static class SocialFragment extends PreferenceFragment /*implements OnSharedPreferenceChangeListener*/ {
    	
		private Preference share;
		private Preference tw;
		private Preference loc;
		private Preference xda;
		
		@Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.social);
            
            /*for(int i=0;i<getPreferenceScreen().getPreferenceCount();i++){
                initSummary(getPreferenceScreen().getPreference(i));
            }*/
	        
            share = (Preference) findPreference("share");
	        share.setOnPreferenceClickListener(new OnPreferenceClickListener() {
	        	
	            public boolean onPreferenceClick(Preference preference) {
	                try {
	                	Intent shareIntent =   
	                	new Intent(android.content.Intent.ACTION_SEND);   
	                	shareIntent.setType("text/plain");  
	                	shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "YouTube Downloader");
	                	
	                	String shareMessage = getString(R.string.share_message) + getString(R.string.ytd_sourceforge_home);
	                	
	                	shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareMessage);
	                	startActivity(Intent.createChooser(shareIntent, shareMessage));
	                } catch (final ActivityNotFoundException e) {
	                	Utils.logger("d", "No suitable Apps found.", DEBUG_TAG);
	                	PopUps.showPopUp(getString(R.string.attention), getString(R.string.share_warning), "error", SocialFragment.this.getActivity());
	                }
	            	return true;
	            }
	        });
	        
            xda = (Preference) findPreference("xda");
            xda.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            	
                public boolean onPreferenceClick(Preference preference) {
                	String url = "http://forum.xda-developers.com/showthread.php?p=37708791";
                	Intent i = new Intent(Intent.ACTION_VIEW);
                	i.setData(Uri.parse(url));
                	startActivity(i);
                	return true;
                }
            });
            
            tw = (Preference) findPreference("tweet");
            tw.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            	
                public boolean onPreferenceClick(Preference preference) {
                	
                	/*
                	 * http://www.androidsnippets.com/open-twitter-via-intent
                	 * http://www.androidsnippets.com/users/hyperax
                	 */
                	try {
                		Utils.logger("d", "twitter direct link", DEBUG_TAG);
                		startActivity(new Intent(Intent.ACTION_VIEW, 
                				Uri.parse("twitter://user?screen_name=@twidentex")));
                	} catch (Exception e) {
                		Utils.logger("d", "twitter WEB link", DEBUG_TAG);
                		startActivity(new Intent(Intent.ACTION_VIEW, 
                				Uri.parse("https://twitter.com/#!/@twidentex")));
                	}
                    return true;
                }
            });
            
	        loc = (Preference) findPreference("help_translate");
            loc.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            	
                public boolean onPreferenceClick(Preference preference) {
                	String url = "http://www.getlocalization.com/ytdownloader/";
                	Intent i = new Intent(Intent.ACTION_VIEW);
                	i.setData(Uri.parse(url));
                	startActivity(i);
                	return true;
                }
            });
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
