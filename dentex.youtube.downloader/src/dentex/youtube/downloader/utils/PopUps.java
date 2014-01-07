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

package dentex.youtube.downloader.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import dentex.youtube.downloader.R;
import dentex.youtube.downloader.YTD;

public class PopUps {
	
	private static String DEBUG_TAG = "PopUps";
	private static int icon;
	private static CheckBox showAgain;
	private static TextView msg;
	
	public static void showPopUp(String title, String message, String type, Activity activity) {
		showPopUp(title, message, type, activity, null);
	}

	public static void showPopUp(String title, String message, String type, Activity activity, final String pref) {
        
		AlertDialog.Builder adb = new AlertDialog.Builder(activity);
		adb.setTitle(title);
		
		if (pref != null) {
			LayoutInflater adbInflater = LayoutInflater.from(activity);
		    View showAgainView = adbInflater.inflate(R.layout.dialog_msg_text_and_checkbox, null);
		    
		    msg = (TextView) showAgainView.findViewById(R.id.msg);
		    msg.setText(message);
		    
		    showAgain = (CheckBox) showAgainView.findViewById(R.id.showAgain);
		    showAgain.setChecked(true);
		    showAgain.setText(activity.getString(R.string.show_again_checkbox));
		    adb.setView(showAgainView);
		} else {
			adb.setMessage(message);
		}
	
	    String theme = YTD.settings.getString("choose_theme", "D");
    	if (theme.equals("D")) {
		    if ( type == "error" ) {
		        icon = R.drawable.ic_dialog_alert_holo_dark;
		    } else if ( type == "info" ) {
		        icon = R.drawable.ic_dialog_info_holo_dark;
		    }
    	} else {
    		if ( type == "error" ) {
		        icon = R.drawable.ic_dialog_alert_holo_light;
		    } else if ( type == "info" ) {
		        icon = R.drawable.ic_dialog_info_holo_light;
		    }
    	}
	
    	adb.setIcon(icon);
    	adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	
	        public void onClick(DialogInterface dialog, int which) {
	            // close the dialog and store (eventually) the "show again" pref
	        	if (pref != null) {
	        		if (!showAgain.isChecked()) {
	        			YTD.settings.edit().putBoolean(pref, false).apply();
	        			Utils.logger("d", pref + "checkbox disabled", DEBUG_TAG);
	        		}
	        	}
	        }
	    });
	
    	if (pref != null) {
    		if (YTD.settings.getBoolean(pref, true)) {
    			Utils.secureShowDialog(activity, adb);
    		}
    	} else {
    		Utils.secureShowDialog(activity, adb);
    	}
	}
}
