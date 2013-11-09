package dentex.youtube.downloader;

import java.util.Arrays;
import java.util.List;

import dentex.youtube.downloader.utils.Utils;
import android.app.Activity;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnClickListener;

public class ShareActivityListFilters {
	
	private static final String DEBUG_TAG = "ShareActivityListFilters";
	
	private static Integer[] iMp4 = { 18, 22, 37, 38, 82, 83, 84, 133, 134, 135, 136, 137, 138, 160, 264 };
	private static Integer[] iWebm = { 43, 44, 45, 46, 100, 101, 102, 242, 243, 244, 245, 246, 247, 248 };
	private static Integer[] iFlv = { 5, 6, 34, 35 };
	private static Integer[] i3gp = { 17, 36 };
	
	private static Integer[] iHd = { 22, 37, 38, 45, 46, 84, 102, 136, 137, 138, 247, 248, 264 };
	private static Integer[] iLd = { 35, 44, 85, 135, 244, 245, 246 };
	private static Integer[] iMd = { 18, 34, 43, 82, 100, 101, 134, 243 };
	private static Integer[] iSd = { 5, 6, 17, 36, 83, 133 };
	
	private static Integer[] i3d = { 82, 83, 84, 85, 100, 101, 102 };
	
	private static Integer[] iVo = { 133, 134, 135, 136, 137, 138, 160, 242, 243, 244, 245, 246, 247, 248, 264 };
	private static Integer[] iAo = { 139, 140, 141, 171, 172 };

	public static CharSequence getListFilterConstraint(int c) {
		//0
		List<Integer> iMp4List = Arrays.asList(iMp4);
		//1
		List<Integer> iWebmList = Arrays.asList(iWebm);
		//2
		List<Integer> iFlvList = Arrays.asList(iFlv);
		//3
		List<Integer> i3gpList = Arrays.asList(i3gp);
		
		//4
		List<Integer> iHdList = Arrays.asList(iHd);
		//5
		List<Integer> iLdList = Arrays.asList(iLd);
		//6
		List<Integer> iMdList = Arrays.asList(iMd);
		//7
		List<Integer> iSdList = Arrays.asList(iSd);
		
		//8
		List<Integer> i3dList = Arrays.asList(i3d);
		
		//9
		List<Integer> iVoList = Arrays.asList(iVo);
		//10
		List<Integer> iAoList = Arrays.asList(iAo);
		
		SparseArray<List<Integer>> filtersMap = new SparseArray<List<Integer>>();
		
		filtersMap.put(0, iMp4List);
		filtersMap.put(1, iWebmList);
		filtersMap.put(2, iFlvList);
		filtersMap.put(3, i3gpList);
		filtersMap.put(4, iHdList);
		filtersMap.put(5, iLdList);
		filtersMap.put(6, iMdList);
		filtersMap.put(7, iSdList);
		filtersMap.put(8, i3dList);
		filtersMap.put(9, iVoList);
		filtersMap.put(10, iAoList);
		
		CharSequence constraint = null;
		List<Integer> selectedMap = filtersMap.get(c);
		
		for (int i = 0; i < selectedMap.size(); i++) {
			if (constraint == null) { 
				constraint = String.valueOf(selectedMap.get(i));
			} else {
				constraint = constraint + "/" + selectedMap.get(i);
			}
		}
		return constraint;
	}
	
	public static CharSequence getMultipleListFilterConstraints(int[] c) {
		CharSequence constraint = null;
		for (int i = 0 ; i < c.length; i++) {
			if (constraint == null) { 
				constraint = getListFilterConstraint(c[i]);
			} else {
				constraint = constraint + "/" + getListFilterConstraint(c[i]);
			}
		}
		return constraint;
	}
	
	public static void slideMenuItemsClickListenersSetup(final Activity act, final ShareListAdapter a) {
		final View mp4 = act.findViewById(R.id.MP4);
		mp4.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Utils.logger("d", "MP4 filter clicked", DEBUG_TAG);
				CharSequence constraint = ShareActivityListFilters.getListFilterConstraint(0);
				a.getFilter().filter(constraint);
				mp4.setBackground(act.getResources().getDrawable(R.drawable.grad_bg_sel));
			}
		});
		
		final View webm = act.findViewById(R.id.WEBM);
		webm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Utils.logger("d", "WEBM filter clicked", DEBUG_TAG);
				CharSequence constraint = ShareActivityListFilters.getListFilterConstraint(1);
				a.getFilter().filter(constraint);
				webm.setBackground(act.getResources().getDrawable(R.drawable.grad_bg_sel));
			}
		});
		
		final View flv = act.findViewById(R.id.FLV);
		flv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Utils.logger("d", "FLV filter clicked", DEBUG_TAG);
				CharSequence constraint = ShareActivityListFilters.getListFilterConstraint(2);
				a.getFilter().filter(constraint);
				flv.setBackground(act.getResources().getDrawable(R.drawable.grad_bg_sel));
			}
		});
		
		final View _3gp = act.findViewById(R.id._3GP);
		_3gp.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Utils.logger("d", "3GP filter clicked", DEBUG_TAG);
				CharSequence constraint = ShareActivityListFilters.getListFilterConstraint(3);
				a.getFilter().filter(constraint);
				_3gp.setBackground(act.getResources().getDrawable(R.drawable.grad_bg_sel));
			}
		});
		
		final View all = act.findViewById(R.id.ALL);
		all.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Utils.logger("d", "ALL filter clicked", DEBUG_TAG);
				a.getFilter().filter("");
				all.setBackground(act.getResources().getDrawable(R.drawable.grad_bg_sel));
			}
		});
		
		final View hd = act.findViewById(R.id.HD);
		hd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Utils.logger("d", "HD filter clicked", DEBUG_TAG);
				CharSequence constraint = ShareActivityListFilters.getListFilterConstraint(4);
				a.getFilter().filter(constraint);
				hd.setBackgroundColor(act.getResources().getColor(android.R.color.holo_red_dark));
			}
		});
		
		final View ld = act.findViewById(R.id.LD);
		ld.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Utils.logger("d", "LD filter clicked", DEBUG_TAG);
				CharSequence constraint = ShareActivityListFilters.getListFilterConstraint(5);
				a.getFilter().filter(constraint);
				ld.setBackgroundColor(act.getResources().getColor(android.R.color.holo_red_dark));
			}
		});
		
		final View md = act.findViewById(R.id.MD);
		md.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Utils.logger("d", "MD filter clicked", DEBUG_TAG);
				CharSequence constraint = ShareActivityListFilters.getListFilterConstraint(6);
				a.getFilter().filter(constraint);
				md.setBackgroundColor(act.getResources().getColor(android.R.color.holo_red_dark));
			}
		});
		
		final View sd = act.findViewById(R.id.SD);
		sd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Utils.logger("d", "SD filter clicked", DEBUG_TAG);
				CharSequence constraint = ShareActivityListFilters.getListFilterConstraint(7);
				a.getFilter().filter(constraint);
				sd.setBackgroundColor(act.getResources().getColor(android.R.color.holo_red_dark));
			}
		});
		
		final View _3d = act.findViewById(R.id._3D);
		_3d.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Utils.logger("d", "3D filter clicked", DEBUG_TAG);
				CharSequence constraint = ShareActivityListFilters.getListFilterConstraint(8);
				a.getFilter().filter(constraint);
				_3d.setBackgroundColor(act.getResources().getColor(android.R.color.holo_red_dark));
			}
		});
		
		final View vo = act.findViewById(R.id.VO);
		vo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Utils.logger("d", "VO filter clicked", DEBUG_TAG);
				CharSequence constraint = ShareActivityListFilters.getListFilterConstraint(9);
				a.getFilter().filter(constraint);
				vo.setBackgroundColor(act.getResources().getColor(android.R.color.holo_red_dark));
			}
		});
		
		final View ao = act.findViewById(R.id.AO);
		ao.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Utils.logger("d", "AO filter clicked", DEBUG_TAG);
				CharSequence constraint = ShareActivityListFilters.getListFilterConstraint(10);
				a.getFilter().filter(constraint);
				ao.setBackgroundColor(act.getResources().getColor(android.R.color.holo_red_dark));
			}
		});
	}
}
