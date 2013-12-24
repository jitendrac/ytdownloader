package dentex.youtube.downloader.utils;

/*
	Copyright 2010 The Android Open Source Project
	Copyright 2013 Daniel Smith

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

   	http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
*/

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import dentex.youtube.downloader.R;

public class QustomDialogBuilder extends AlertDialog.Builder{

	/** The custom_body layout */
	private View mDialogView;
    private AlertDialog mDialog;
	
	/** optional dialog title layout */
	private TextView mTitle;
	/** optional alert dialog image */
	private ImageView mIcon;
	/** optional message displayed below title if title exists*/
	private TextView mMessage;
	/** The colored holo divider. You can set its color with the setDividerColor method */
	private View mDivider;
	/** optional custom panel image */
	private FrameLayout mCustom;
	
    public QustomDialogBuilder(Context context) {
        super(context);

        mDialogView = View.inflate(context, R.layout.qustom_dialog_layout, null);
        setView(mDialogView);

        mTitle = (TextView) mDialogView.findViewById(R.id.alertTitle);
        mMessage = (TextView) mDialogView.findViewById(R.id.message);
        mIcon = (ImageView) mDialogView.findViewById(R.id.icon);
        mDivider = mDialogView.findViewById(R.id.titleDivider);
        mCustom = (FrameLayout) mDialogView.findViewById(R.id.customPanel);
	}
    
    /**
     * Use this method to reference the whole dialog view, i.e. when inflating a custom 
     * view with {@link #setCustomView(int, Context)} and you want to find an id from the
     * inflated layout.
     * 
     *   example: 
     *   
     *   CheckBox showAgain = (CheckBox) myQustomDialogBuilder.getDialogView().findViewById(R.id.inflated_checkbox);
     *   if (!showAgain.isChecked()) {
     *   	// do something
     *   }
     *   
     * @return the qustom dialog view
     */
    public View getDialogView() {
    	return mDialogView;
    }

    /** 
     * Use this method to color the divider between the title and content.
     * Will not display if no title is set.
     * 
     * @param colorString for passing "#ffffff"
     */
    public QustomDialogBuilder setDividerColor(String colorString) {
    	mDivider.setBackgroundColor(Color.parseColor(colorString));
    	return this;
    }
 
    @Override
    public QustomDialogBuilder setTitle(CharSequence text) {
        mTitle.setText(text);
        return this;
    }

    public QustomDialogBuilder setTitleColor(String colorString) {
    	mTitle.setTextColor(Color.parseColor(colorString));
    	return this;
    }

    @Override
    public QustomDialogBuilder setMessage(int textResId) {
        mMessage.setText(textResId);
        return this;
    }

    @Override
    public QustomDialogBuilder setMessage(CharSequence text) {
        mMessage.setText(text);
        return this;
    }

    @Override
    public QustomDialogBuilder setIcon(int drawableResId) {
    	mIcon.setVisibility(View.VISIBLE);
        mIcon.setImageResource(drawableResId);
        return this;
    }

    @Override
    public QustomDialogBuilder setIcon(Drawable icon) {
        mIcon.setImageDrawable(icon);
        return this;
    }
    
    /**
     * This allows you to specify a custom layout for the area below the title divider bar
     * in the dialog. As an example you can look at example_ip_address_layout.xml and how
     * I added it in TestDialogActivity.java
     * 
     * @param resId  of the layout you would like to add
     * @param context
     */
    public QustomDialogBuilder setCustomView(int resId, Context context) {
    	View customView = View.inflate(context, resId, null);
    	mCustom.setVisibility(View.VISIBLE);
    	mCustom.addView(customView);
    	return this;
    }

    /**
     * Set a list of items to be displayed in the dialog as the content, you will be notified of the
     * selected item via the supplied listener. This should be an array type i.e. R.array.foo
     *
     * @return This Builder object to allow for chaining of calls to set methods
     */
    @Override
    public QustomDialogBuilder setItems(int itemsId, final DialogInterface.OnClickListener listener) {
        return setItems(getContext().getResources().getTextArray(itemsId), listener);
    }

    /**
     * Set a list of items to be displayed in the dialog as the content, you will be notified of the
     * selected item via the supplied listener.
     *
     * @return This Builder object to allow for chaining of calls to set methods
     */
    @Override
    public QustomDialogBuilder setItems(CharSequence[] items, final DialogInterface.OnClickListener listener) {
    	return (QustomDialogBuilder) setItems(items, null, listener);
    }
    
    public Builder setItems(int itemsId, int[] disabledOptions, final DialogInterface.OnClickListener listener) {
    	CharSequence[] items = getContext().getResources().getTextArray(itemsId);
    	return setItems(items, disabledOptions, listener);
    }
    
    public Builder setItems(CharSequence[] items, int[] disabledOptions, final DialogInterface.OnClickListener listener) {
        LinearLayout itemList = (LinearLayout) mDialogView.findViewById(R.id.items_list);

        for (int i = 0; i < items.length; i++) {
            final int currentItem = i;
            TextView listItem = inflateItem(items[i].toString());
            View divider = inflateDivider();
            
            if (disabledOptions != null) {
            	final boolean enabled = isEnabled(i, disabledOptions);
				listItem.setEnabled(enabled);
            	if (!enabled) listItem.setTextColor(Color.GRAY);
            }
            
            itemList.addView(listItem);
            if (i+1 != items.length) itemList.addView(divider);
            if (listener != null) {
            	// fix
                listItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onClick(mDialog, currentItem);
                        mDialog.dismiss();
                    }
                });
            }
        }

        return this;
    }
    
    public boolean isEnabled(int position, int[] disabledOptions) {
    	if (disabledOptions != null) {
	    	for (int i = 0; i < disabledOptions.length; i++) {
	    		if (position == disabledOptions[i]) return false;
	    	}
    	}
        return true;
    }

    private TextView inflateItem(String itemText) {
        TextView listItem = (TextView) View.inflate(getContext(), R.layout.qustom_dialog_item_layout, null);
        TextView icaoTextView = (TextView) listItem.findViewById(R.id.item_text);
        icaoTextView.setText(itemText);
        return listItem;
    }

    private View inflateDivider() {
        View listDivider = View.inflate(getContext(), R.layout.qustom_dialog_items_divider, null);
        return listDivider;
    }
    
    @Override
    public AlertDialog show() {
    	if (mTitle.getText().equals("")) mDialogView.findViewById(R.id.topPanel).setVisibility(View.GONE);
    	// hide also message TextView if empty
    	if (mMessage.getText().equals("")) mDialogView.findViewById(R.id.contentPanel).setVisibility(View.GONE);
    	mDialog = super.show();
        return mDialog;
    }
}
