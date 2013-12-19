package dentex.youtube.downloader;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import dentex.youtube.downloader.utils.Utils;

public class YTDSettings_Preference extends Preference {

	public YTDSettings_Preference(Context context) {
		super(context);
	}

	public YTDSettings_Preference(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public YTDSettings_Preference(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

    @Override
    protected View onCreateView(ViewGroup parent) {
    	View newView = super.onCreateView(parent);
    	//Utils.setCustomTitleAndSummaryColors(newView);
	    
	    return newView;
    }
}
