package com.example.baidumap;

import android.app.Activity;

import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.PoiOverlay;
import com.baidu.mapapi.search.MKPoiInfo;
import com.baidu.mapapi.search.MKSearch;

public class MyPoiOverlay extends PoiOverlay{
	
	private MKSearch mkSearch;
	
	public MyPoiOverlay(Activity activity, MapView mapView,MKSearch mkSearch) {
		super(activity, mapView);
		// TODO Auto-generated constructor stub
		this.mkSearch=mkSearch;
	}

	@Override
	protected boolean onTap(int arg0) {
		// TODO Auto-generated method stub
		super.onTap(arg0);
		MKPoiInfo mkPoiInfo=getPoi(arg0);
		if(mkPoiInfo.hasCaterDetails){
			mkSearch.poiDetailSearch(mkPoiInfo.uid);
		}
		return true;
	}
	
}
