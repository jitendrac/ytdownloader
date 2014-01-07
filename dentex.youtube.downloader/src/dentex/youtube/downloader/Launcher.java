package dentex.youtube.downloader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class Launcher extends Activity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    
		String launcher = YTD.settings.getString("launcher", "dashboard");
		Class<?> cl = DashboardActivity.class;
		
		if (launcher.equals("settings")) cl = SettingsActivity.class;
		
	    startActivity(new Intent(this, cl));
	    finish();
	}
}
