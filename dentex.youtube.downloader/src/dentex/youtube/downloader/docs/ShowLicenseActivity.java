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


package dentex.youtube.downloader.docs;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import dentex.youtube.downloader.R;
import dentex.youtube.downloader.utils.Utils;

public class ShowLicenseActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Theme init
    	Utils.themeInit(this);
    	
		setContentView(R.layout.activity_show_license);
		
		TextView lt = (TextView) findViewById(R.id.license_text);
		
		if (lt != null) {
			if (getIntent().getStringExtra("license_text").equals("gpl")) {
				lt.setText(R.string.gpl_text);
				setTitle(R.string.gpl_title);
			} else if (getIntent().getStringExtra("license_text").equals("mit")) {
				lt.setText(R.string.mit_text);
				setTitle(R.string.mit_title);
			} else if (getIntent().getStringExtra("license_text").equals("lgpl")) {
				lt.setText(R.string.lgpl_text);
				setTitle(R.string.lgpl_title);
			} else if (getIntent().getStringExtra("license_text").equals("apache")) {
				lt.setText(R.string.apache_text);
				setTitle(R.string.apache_title);
			} else if (getIntent().getStringExtra("license_text").equals("cc")) {
				lt.setText(R.string.cc_text);
				setTitle(R.string.cc_title);
			} else if (getIntent().getStringExtra("license_text").equals("cc_b")) {
				lt.setText(R.string.cc_text_b);
				setTitle(R.string.cc_title_b);
			} else if (getIntent().getStringExtra("license_text").equals("mpl")) {
				lt.setText(R.string.mpl_text);
				setTitle(R.string.mpl_title);
			}
		}
	}
}
