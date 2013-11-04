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
import android.widget.Filterable;
import android.widget.TextView;

public class ShareListAdapter extends ArrayAdapter<ShareActivityListItem> implements Filterable {

	private Context context;
	private List<ShareActivityListItem> itemsList;
	private List<ShareActivityListItem> origItemsList;
	private ArrayList<ShareActivityListItem> filteredList;
	private Filter itemsFilter;
	
	public ShareListAdapter(List<ShareActivityListItem> itemsList, Context ctx) {
		super(ctx, R.layout.activity_share_list_item, itemsList);
		this.itemsList = itemsList;
		this.context = ctx;
		this.origItemsList = new ArrayList<ShareActivityListItem>(itemsList);
	}
	
	public int getCount() {
		return itemsList.size();
	}

	public ShareActivityListItem getItem(int position) {
		return itemsList.get(position);
	}
	
	/*public long getItemId(int position) {
		return items.get(position).hashCode();
	}*/
	
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		
		ItemHolder holder = new ItemHolder();
		
		// First let's verify the convertView is not null
		if (convertView == null) {
			// This a new view we inflate the new layout
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.activity_share_list_item, null);
			
			TextView tv = (TextView) v.findViewById(R.id.share_list_item);
			
			holder.text = tv;
			
			v.setTag(holder);
		} else {
			holder = (ItemHolder) v.getTag();
		}
		
		ShareActivityListItem sli = itemsList.get(position);
		
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
	        if (TextUtils.isEmpty(constraint)) {
	            results.values = origItemsList;
	            results.count = origItemsList.size();
	        } else {
	            filteredList = new ArrayList<ShareActivityListItem>();
	            String[] constraintItags = Pattern.compile("/", Pattern.LITERAL).split(constraint);
	            
	            for (int i = 0; i < itemsList.size(); i++) {
	            	int currentItag = itemsList.get(i).getItag();

	                if (Integer.parseInt(constraintItags[i]) == currentItag)
	                    filteredList.add(itemsList.get(i));
	            }
	             
	            results.values = filteredList;
	            results.count = filteredList.size();
	        }
			return results;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint, FilterResults results) {
			itemsList = (List<ShareActivityListItem>) results.values;
	        notifyDataSetChanged();
		}
	}
	
	@Override
	public Filter getFilter() {
	    if (itemsFilter == null)
	        itemsFilter = new ItemsFilter();
	     
	    return itemsFilter;
	}
	
	public void resetData() {
		itemsList = origItemsList;
	}	
}

