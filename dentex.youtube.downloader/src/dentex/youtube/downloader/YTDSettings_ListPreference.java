package dentex.youtube.downloader;

import android.content.Context;
import android.preference.ListPreference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import dentex.youtube.downloader.utils.Utils;

public class YTDSettings_ListPreference extends ListPreference {

	public YTDSettings_ListPreference(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public YTDSettings_ListPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

    @Override
    protected View onCreateView(ViewGroup parent) {
    	View newView = super.onCreateView(parent);
    	//Utils.setCustomTitleAndSummaryColors(newView);
	    
	    return newView;
    }
}
