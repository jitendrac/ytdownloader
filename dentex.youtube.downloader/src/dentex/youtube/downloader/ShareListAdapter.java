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

package dentex.youtube.downloader;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;
import dentex.youtube.downloader.utils.Utils;

public class ShareListAdapter extends ArrayAdapter<ShareActivityListItem> {

	private final static String DEBUG_TAG = "ShareListAdapter";
	private Context context;
	private List<ShareActivityListItem> filteredResultList = new ArrayList<ShareActivityListItem>();
	private List<ShareActivityListItem> originalResultList = new ArrayList<ShareActivityListItem>();
	private Filter filter;
	
	public ShareListAdapter(List<ShareActivityListItem> objects, Context ctx) {
		super(ctx, R.layout.activity_share_list_item, objects);
		this.context = ctx;
		this.filteredResultList = objects;
        this.originalResultList = objects;
	}
	
	@Override
	public int getCount() {
		return originalResultList.size();
	}
	
	@Override
	public int getPosition(ShareActivityListItem item) {
		return originalResultList.indexOf(item);
	}
	
	@Override
	public ShareActivityListItem getItem(int position) {
		return originalResultList.get(position);
	}
	
	public long getItemId(int position) {
		return originalResultList.get(position).hashCode();
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		
		ItemHolder holder = new ItemHolder();
		
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.activity_share_list_item, null);
			
			TextView tv = (TextView) v.findViewById(R.id.share_list_item);
			
			holder.text = tv;
			
			v.setTag(holder);
		} else {
			holder = (ItemHolder) v.getTag();
		}
		
		ShareActivityListItem sli = originalResultList.get(position);
		
		holder.text.setText(sli.getText());
		return v;
	}

	private static class ItemHolder {
		public TextView text;
	}
	
	public class ItemsFilter extends Filter {

		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			FilterResults results = new FilterResults();

            if (constraint == null || TextUtils.isEmpty(constraint)) {
            	synchronized (this) {
            		results.values = filteredResultList;
                    results.count = filteredResultList.size();
            	}
            } else {
            	String[] constraintItags = Pattern.compile("/", Pattern.LITERAL).split(constraint);
            	
            	List<ShareActivityListItem> filteredList = new ArrayList<ShareActivityListItem>();
            	List<ShareActivityListItem> unfilteredList = new ArrayList<ShareActivityListItem>();
            	synchronized (this) {
            		unfilteredList.addAll(filteredResultList);
            	}
            	
            	for (int i = 0, l = unfilteredList.size(); i < l; i++) {
	            	int currentItag = unfilteredList.get(i).getItag();
	            	ShareActivityListItem p = unfilteredList.get(i);
	            	//Utils.logger("i", "currentItag: " + currentItag, DEBUG_TAG);
	
	            	for (int j = 0; j < constraintItags.length; j++) {
	            		if (currentItag == Integer.valueOf(constraintItags[j])) {
	            			Utils.logger("i", "currentItag matched: -> " + constraintItags[j], DEBUG_TAG);
	            			filteredList.add(p); 
	            		}
	            	}
            	}
            	results.values = filteredList;
            	results.count = filteredList.size();
            }
            return results;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint, FilterResults results) {
			originalResultList = (ArrayList<ShareActivityListItem>) results.values;
			notifyDataSetChanged();
		}
	}
	
	@Override
	public Filter getFilter() {
	    if (filter == null)
	        filter = new ItemsFilter();
	     
	    return filter;
	}
}

