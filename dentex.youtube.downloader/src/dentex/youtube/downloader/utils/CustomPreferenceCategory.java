package dentex.youtube.downloader.utils;

/*
 * Adapted from code by Udi Cohen
 * https://github.com/Udinic/SmallExamples/tree/master/CustomPreferenceActivity
 * Licensed under the Apache License, Version 2.0 (the "License")
 */

import android.content.Context;
import android.preference.PreferenceCategory;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import dentex.youtube.downloader.YTD;

public class CustomPreferenceCategory extends PreferenceCategory {
	
	String theme = YTD.settings.getString("choose_theme", "D");

    public CustomPreferenceCategory(Context context) {
    	super(context);
    }

    public CustomPreferenceCategory(Context context, AttributeSet attrs) {
    	super(context, attrs);
    }
    
    public CustomPreferenceCategory(Context context, AttributeSet attrs, int defStyle) {
    	super(context, attrs, defStyle);
    }
    
    private int getHoloShade() {
    	if (theme.equals("D")) {
    		return android.R.color.holo_blue_light;
    	} else {
    		return android.R.color.holo_blue_dark;
    	}
    }

	/**
	* We catch the view after its creation, and before the activity will use it, in order to make our changes
	* @param parent
	* @return
	*/
    @Override
    protected View onCreateView(ViewGroup parent) {
	    TextView categoryTitle = (TextView) super.onCreateView(parent);
	    categoryTitle.setTextColor(YTD.ctx.getResources().getColor(getHoloShade()));
	    return categoryTitle;
    }
}
