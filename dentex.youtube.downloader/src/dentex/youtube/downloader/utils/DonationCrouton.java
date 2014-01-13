package dentex.youtube.downloader.utils;

import com.prashant.custom.widget.crouton.Configuration;
import com.prashant.custom.widget.crouton.Crouton;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import dentex.youtube.downloader.R;
import dentex.youtube.downloader.YTD;
import dentex.youtube.downloader.menu.DonateActivity;

public class DonationCrouton {
	static String DEBUG_TAG = "DonationCrouton";

	private static final Configuration CONFIGURATION_INFINITE = 
		new Configuration.Builder()
			.setDuration(Configuration.DURATION_INFINITE)
			.build();
	
	private static final int CROUTON_TRIGGER_VALUE = 12;
	
	private static Crouton cr;
	
	public static void popIt(final Activity act) {
		int cd = getDownloadsCount();
		int cf = getFfmpegJobsCount();
		
		if (!YTD.settings.getBoolean("crouton_disable", false)) {
			if (YTD.showCrouton) {
				Utils.logger("i", "showing the donation crouton", DEBUG_TAG);
				
				final LayoutInflater inflater = act.getLayoutInflater();
				View view = inflater.inflate(R.layout.crouton_custom_view, null);
				
				TextView title = (TextView) view.findViewById(R.id.crouton_title);
				if (cf != 0) {
					title.setText(String.format(
							act.getString(R.string.crouton_title_a_1) + 
							act.getString(R.string.crouton_title_b), cd, cf));
				} else {
					title.setText(String.format(
							act.getString(R.string.crouton_title_a_2) + 
							act.getString(R.string.crouton_title_b), cd));
				}
				
				View closeButton = (View) view.findViewById(R.id.crouton_close_button);
				closeButton.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						Crouton.hide(cr);
						setShowCrouton(false);
					}
				});
				
				LinearLayout root = (LinearLayout) view.findViewById(R.id.crouton_root);
				root.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						act.startActivity(new Intent(act, DonateActivity.class));
					}
				});
				
				cr = Crouton.make(act, view);
				cr.setConfiguration(dentex.youtube.downloader.utils.DonationCrouton.CONFIGURATION_INFINITE);
				cr.show();
			} 
		} else {
			Utils.logger("v", "crouton always disabled", DEBUG_TAG);
		}
	}

	private static void setShowCrouton(boolean b) {
		YTD.showCrouton = b;
		Utils.logger("v", "showCrouton " + b, DEBUG_TAG);
	}

	public static void increaseFfmpegJobsCount() {
		int cf = YTD.settings.getInt("completed_ffmpeg", 0);
		cf++;
		
		YTD.settings.edit().putInt("completed_ffmpeg", cf).commit();
		Utils.logger("v", "FFmpeg Jobs Count: " + cf, DEBUG_TAG);
		
		if (cf % CROUTON_TRIGGER_VALUE == 0) {
			setShowCrouton(true);
		}
	}
	
	public static int getFfmpegJobsCount() {
		int cf = YTD.settings.getInt("completed_ffmpeg", 0);
		//Utils.logger("i", "FFmpeg Jobs Count: " + cf, DEBUG_TAG);
		return cf;
	}
	
	public static void increaseDownloadsCount() {
		int cd = YTD.settings.getInt("completed_downloads", 0);
		cd++;

		YTD.settings.edit().putInt("completed_downloads", cd).commit();
		Utils.logger("v", "completed Downloads Count: " + cd, DEBUG_TAG);

		if (cd % CROUTON_TRIGGER_VALUE == 0) {
			setShowCrouton(true);
		}
	}
	
	public static int getDownloadsCount() {
		int cd = YTD.settings.getInt("completed_downloads", 0);
		//Utils.logger("i", "Completed Downloads Count: " + cd, DEBUG_TAG);
		return cd;
	}

}
