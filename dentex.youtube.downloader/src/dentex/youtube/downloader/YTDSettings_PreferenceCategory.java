package dentex.youtube.downloader;

/*
 * Based on code by Udi Cohen
 * https://github.com/Udinic/SmallExamples/tree/master/CustomPreferenceActivity
 * Licensed under the Apache License, Version 2.0 (the "License")
 */

import android.content.Context;
import android.graphics.Color;
import android.preference.PreferenceCategory;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import dentex.youtube.downloader.utils.Utils;

public class YTDSettings_PreferenceCategory extends PreferenceCategory {

    public YTDSettings_PreferenceCategory(Context context) {
    	super(context);
    }

    public YTDSettings_PreferenceCategory(Context context, AttributeSet attrs) {
    	super(context, attrs);
    }
    
    public YTDSettings_PreferenceCategory(Context context, AttributeSet attrs, int defStyle) {
    	super(context, attrs, defStyle);
    }


	/**
	* We catch the view after its creation, and before the activity will use it, in order to make our changes
	* @param parent
	* @return
	*/
    @Override
    protected View onCreateView(ViewGroup parent) {
	    TextView categoryTitle = (TextView) super.onCreateView(parent);
	    categoryTitle.setTextColor(Color.parseColor(Utils.getThemeColor()));
	    return categoryTitle;
    }
}
