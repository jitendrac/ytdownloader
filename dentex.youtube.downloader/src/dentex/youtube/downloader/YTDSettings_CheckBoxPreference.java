package dentex.youtube.downloader;

import android.content.Context;
import android.preference.CheckBoxPreference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import dentex.youtube.downloader.utils.Utils;

public class YTDSettings_CheckBoxPreference extends CheckBoxPreference {

	public YTDSettings_CheckBoxPreference(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public YTDSettings_CheckBoxPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public YTDSettings_CheckBoxPreference(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

    @Override
    protected View onCreateView(ViewGroup parent) {
    	View newView = super.onCreateView(parent);
    	//Utils.setCustomTitleAndSummaryColors(newView);
	    
	    return newView;
    }
}
