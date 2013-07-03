package com.example.baidumap;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MKEvent;
import com.baidu.mapapi.map.MKMapViewListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.RouteOverlay;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPlanNode;
import com.baidu.mapapi.search.MKPoiInfo;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.baidu.platform.comapi.basestruct.GeoPoint;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

public class MyMapActivity extends Activity {

	private BMapManager mBMapMan = null;  
	private MapView mMapView = null;
	private LocationClient mLocClient;
	private MyLocationOverlay myLocationOverlay;
	private LocationData locData;
	
	private MyLocationListener myListener=new MyLocationListener();
	
	private MKSearch mkSearch=null;
	
	private boolean first=true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mBMapMan=new BMapManager(getApplication());  
		mBMapMan.init("48771DCB41DB9E398EDFB4BFCF4664C8DE9DD8B4", null);    
		//注意：请在试用setContentView前初始化BMapManager对象，否则会报错  
		setContentView(R.layout.activity_my_map);  
		
		mMapView=(MapView)findViewById(R.id.bmapView);  
		mMapView.setBuiltInZoomControls(true);  
		mMapView.setTraffic(true);
		//mMapView.setSatellite(true);
		//设置启用内置的缩放控件  
		MapController mMapController=mMapView.getController();  
		// 得到mMapView的控制权,可以用它控制和驱动平移和缩放  
		//GeoPoint point =new GeoPoint((int)(28.496* 1E6),(int)(118.182* 1E6));  
		//用给定的经纬度构造一个GeoPoint，单位是微度 (度 * 1E6)  
		//mMapController.setCenter(point);//设置地图中心点  
		mMapController.setZoom(12);//设置地图zoom级别  
		mMapController.enableClick(true);
		
		Log.d("LoC", "start begining");
		System.out.println("end onMedia");
		
		 //定位初始化
        mLocClient = new LocationClient( this );
        locData = new LocationData();
        mLocClient.registerLocationListener( myListener );
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);//打开gps
        option.setCoorType("bd09ll");     //设置坐标类型
        option.setScanSpan(5000);
        mLocClient.setLocOption(option);
        Log.d("LoC", "start before");
        mLocClient.start();
        Log.d("LoC", "start after");
        
        
        //定位图层初始化
		myLocationOverlay = new MyLocationOverlay(mMapView);
		//设置定位数据
	    myLocationOverlay.setData(locData);
	    //添加定位图层
		mMapView.getOverlays().add(myLocationOverlay);
	
		myLocationOverlay.enableCompass();
		//修改定位数据后刷新图层生效
		mMapView.refresh();
		Log.d("LoC", "ending");
		
		mkSearch=new MKSearch();
		mkSearch.init(mBMapMan, new MySearchListener());
		mkSearch.poiSearchInCity("厦门", "餐厅");
		Toast.makeText(this, "searching", Toast.LENGTH_SHORT).show();
		
		//地图监听事件（点击。。。）
		MKMapViewListener mkMapViewListener=new MKMapViewListener() {
			
			@Override
			public void onMapMoveFinish() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onMapAnimationFinish() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onGetCurrentMap(Bitmap arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onClickMapPoi(MapPoi position) {
				// TODO Auto-generated method stub
				Log.d("CLICK", "click "+position.strText);
				Toast.makeText(MyMapActivity.this, "click "+position.strText, Toast.LENGTH_LONG).show();
			}
		};
		mMapView.regMapViewListener(mBMapMan, mkMapViewListener);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.my_map, menu);
		return true;
	}
	
	@Override  
	protected void onDestroy(){  
			mLocClient.stop();
	        mMapView.destroy();  
	        if(mBMapMan!=null){  
	                mBMapMan.destroy();  
	                mBMapMan=null;  
	        }  
	        super.onDestroy();  
	}  
	@Override  
	protected void onPause(){
			mLocClient.stop();
	        mMapView.onPause();  
	        if(mBMapMan!=null){  
	               mBMapMan.stop();  
	        }  
	        super.onPause();  
	}  
	@Override  
	protected void onResume(){  
			mLocClient.start();
	        mMapView.onResume();  
	        if(mBMapMan!=null){  
	                mBMapMan.start();  
	        }  
	       super.onResume();  
	}  
	
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);
		mMapView.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		mMapView.onSaveInstanceState(outState);
	}

	//定位类
	class MyLocationListener implements BDLocationListener{

		@Override
		public void onReceiveLocation(BDLocation location) {
			// TODO Auto-generated method stub
			Log.d("Listener", "begin listener");                                                        
			if (location == null)
                return ;
            locData.latitude = location.getLatitude();
            locData.longitude = location.getLongitude();
            //如果不显示定位精度圈，将accuracy赋值为0即可
            locData.accuracy = location.getRadius();
            locData.direction = location.getDerect();
            //更新定位数据
            myLocationOverlay.setData(locData);
            //更新图层数据执行刷新后生效
            
            mMapView.refresh();
            Log.d("Listener", "after refresh"+locData.latitude+"-"+locData.longitude);
            
            //是手动触发请求或首次定位时，移动到定位点
            if(first){
            	//移动地图到定位点
                mMapView.getController().animateTo(new GeoPoint((int)(locData.latitude* 1e6), (int)(locData.longitude *  1e6)));
                Log.d("Listener", "animate to ....");
            }
            first=false;
               
		}

		@Override
		public void onReceivePoi(BDLocation arg0) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	//搜索结果监听类
	class MySearchListener implements MKSearchListener{

		@Override
		public void onGetAddrResult(MKAddrInfo arg0, int arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onGetBusDetailResult(MKBusLineResult arg0, int arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onGetDrivingRouteResult(MKDrivingRouteResult result, int error) {
			// TODO Auto-generated method stub
			if(result==null)	return ;
			//路线图层显示
			RouteOverlay routeOverlay=new RouteOverlay(MyMapActivity.this, mMapView);
			routeOverlay.setData(result.getPlan(0).getRoute(0));
			mMapView.getOverlays().add(routeOverlay);
			mMapView.refresh();
		}

		@Override
		public void onGetPoiDetailSearchResult(int arg0, int arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onGetPoiResult(MKPoiResult res, int type, int error) {
			// TODO Auto-generated method stub
			// 错误号可参考MKEvent中的定义  
			if(error==MKEvent.ERROR_RESULT_NOT_FOUND){
				Toast.makeText(MyMapActivity.this, "sorry,not found the result", Toast.LENGTH_LONG).show();
				return;
			}else if(error!=0 || res==null){
				Toast.makeText(MyMapActivity.this, "search wrong", Toast.LENGTH_LONG).show();
				return;
			}
			Toast.makeText(MyMapActivity.this, "showing the result", Toast.LENGTH_LONG).show();
			//poi结果显示到图层
			//PoiOverlay poiOverlay=new PoiOverlay(MyMapActivity.this, mMapView);
			MyPoiOverlay poiOverlay=new MyPoiOverlay(MyMapActivity.this, mMapView, mkSearch);
			poiOverlay.setData(res.getAllPoi());
			//mMapView.getOverlays().clear();
			mMapView.getOverlays().add(poiOverlay);
			mMapView.refresh();
			for(MKPoiInfo info:res.getAllPoi()){
				if(info.pt!=null){
					mMapView.getController().animateTo(info.pt);
					break;
				}
			}
			
			//路线搜索
			MKPlanNode start=new MKPlanNode();
			MKPlanNode end=new MKPlanNode();
			start.pt=res.getAllPoi().get(0).pt;
			end.pt=res.getAllPoi().get(2).pt;
			mkSearch.setDrivingPolicy(MKSearch.ECAR_TIME_FIRST);
			mkSearch.drivingSearch(null, start, null, end);
		}

		@Override
		public void onGetSuggestionResult(MKSuggestionResult arg0, int arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onGetTransitRouteResult(MKTransitRouteResult arg0, int arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onGetWalkingRouteResult(MKWalkingRouteResult arg0, int arg1) {
			// TODO Auto-generated method stub
			
		}
		
	}
}
