package dentex.youtube.downloader.utils;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;
import dentex.youtube.downloader.R;
import dentex.youtube.downloader.YTD;

public class DashboardClearHelper {
	public static final String DEBUG_TAG = "DashboardClearHelper";
	
	public static void confirmClearDashboard(final Activity act, final ContextThemeWrapper tw, final boolean doReload) {
		String previousJson = Json.readJsonDashboardFile(act);
		boolean smtInProgressOrPaused = (previousJson.contains(YTD.JSON_DATA_STATUS_IN_PROGRESS) || 
										 previousJson.contains(YTD.JSON_DATA_STATUS_PAUSED)) ;
		
		if (YTD.JSON_FILE.exists() && !previousJson.equals("{}\n") && !smtInProgressOrPaused) {
			
			AlertDialog.Builder adb = new AlertDialog.Builder(tw);
			
			LayoutInflater adbInflater = LayoutInflater.from(act);
		    View deleteDataView = adbInflater.inflate(R.layout.dialog_show_again_checkbox, null);
		    final CheckBox deleteData = (CheckBox) deleteDataView.findViewById(R.id.showAgain2);
		    deleteData.setChecked(false);
		    deleteData.setText("delete data also"); //TODO
		    adb.setView(deleteDataView);
		    
		    adb.setIcon(android.R.drawable.ic_dialog_info);
		    adb.setTitle(act.getString(R.string.information));
		    adb.setMessage(act.getString(R.string.clear_dashboard_msg));
		    
		    adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
		    	
		        public void onClick(DialogInterface dialog, int which) {
		        	if (YTD.JSON_FILE.delete()) {
		        		Toast.makeText(act, act.getString(R.string.clear_dashboard_ok), Toast.LENGTH_SHORT).show();
		        		Utils.logger("v", "Dashboard cleared", DEBUG_TAG);
		        		
		        		// clean thumbnails dir
		            	File thFolder = act.getDir(YTD.THUMBS_FOLDER, 0);
		            	for(File file: thFolder.listFiles()) file.delete();
		            	
		            	// clear the videoinfo shared pref
		            	YTD.videoinfo.edit().clear().apply();
		            	
		            	if (deleteData.isChecked()) Utils.logger("i", "delete data checkbox checked", DEBUG_TAG); //TODO
		            	
		            	if (doReload) Utils.reload(act);
		        	} else {
		        		Toast.makeText(act, act.getString(R.string.clear_dashboard_failed), Toast.LENGTH_SHORT).show();
		        		Utils.logger("w", "clear_dashboard_failed", DEBUG_TAG);
		        	}
		        }
		    });
		    
		    adb.setNegativeButton(act.getString(R.string.dialogs_negative), new DialogInterface.OnClickListener() {
		    	
		    	public void onClick(DialogInterface dialog, int which) {
		        	// cancel
		        }
		    });

		    AlertDialog helpDialog = adb.create();
		    if (! act.isFinishing()) {
		    	helpDialog.show();
		    }
		} else {
			Toast.makeText(act, act.getString(R.string.long_press_warning_title) + 
					"\n- " + act.getString(R.string.notification_downloading_pt1) + " (" + 
					act.getString(R.string.json_status_paused) + "/" + act.getString(R.string.json_status_in_progress) + " )" + 
					"\n- " + act.getString(R.string.empty_dashboard), 
					Toast.LENGTH_SHORT).show();
		}
	}
}
