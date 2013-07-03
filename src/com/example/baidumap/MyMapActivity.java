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
		//ע�⣺��������setContentViewǰ��ʼ��BMapManager���󣬷���ᱨ��  
		setContentView(R.layout.activity_my_map);  
		
		mMapView=(MapView)findViewById(R.id.bmapView);  
		mMapView.setBuiltInZoomControls(true);  
		mMapView.setTraffic(true);
		//mMapView.setSatellite(true);
		//�����������õ����ſؼ�  
		MapController mMapController=mMapView.getController();  
		// �õ�mMapView�Ŀ���Ȩ,�����������ƺ�����ƽ�ƺ�����  
		//GeoPoint point =new GeoPoint((int)(28.496* 1E6),(int)(118.182* 1E6));  
		//�ø����ľ�γ�ȹ���һ��GeoPoint����λ��΢�� (�� * 1E6)  
		//mMapController.setCenter(point);//���õ�ͼ���ĵ�  
		mMapController.setZoom(12);//���õ�ͼzoom����  
		mMapController.enableClick(true);
		
		Log.d("LoC", "start begining");
		System.out.println("end onMedia");
		
		 //��λ��ʼ��
        mLocClient = new LocationClient( this );
        locData = new LocationData();
        mLocClient.registerLocationListener( myListener );
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);//��gps
        option.setCoorType("bd09ll");     //������������
        option.setScanSpan(5000);
        mLocClient.setLocOption(option);
        Log.d("LoC", "start before");
        mLocClient.start();
        Log.d("LoC", "start after");
        
        
        //��λͼ���ʼ��
		myLocationOverlay = new MyLocationOverlay(mMapView);
		//���ö�λ����
	    myLocationOverlay.setData(locData);
	    //��Ӷ�λͼ��
		mMapView.getOverlays().add(myLocationOverlay);
	
		myLocationOverlay.enableCompass();
		//�޸Ķ�λ���ݺ�ˢ��ͼ����Ч
		mMapView.refresh();
		Log.d("LoC", "ending");
		
		mkSearch=new MKSearch();
		mkSearch.init(mBMapMan, new MySearchListener());
		mkSearch.poiSearchInCity("����", "����");
		Toast.makeText(this, "searching", Toast.LENGTH_SHORT).show();
		
		//��ͼ�����¼��������������
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

	//��λ��
	class MyLocationListener implements BDLocationListener{

		@Override
		public void onReceiveLocation(BDLocation location) {
			// TODO Auto-generated method stub
			Log.d("Listener", "begin listener");                                                        
			if (location == null)
                return ;
            locData.latitude = location.getLatitude();
            locData.longitude = location.getLongitude();
            //�������ʾ��λ����Ȧ����accuracy��ֵΪ0����
            locData.accuracy = location.getRadius();
            locData.direction = location.getDerect();
            //���¶�λ����
            myLocationOverlay.setData(locData);
            //����ͼ������ִ��ˢ�º���Ч
            
            mMapView.refresh();
            Log.d("Listener", "after refresh"+locData.latitude+"-"+locData.longitude);
            
            //���ֶ�����������״ζ�λʱ���ƶ�����λ��
            if(first){
            	//�ƶ���ͼ����λ��
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
	
	//�������������
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
			//·��ͼ����ʾ
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
			// ����ſɲο�MKEvent�еĶ���  
			if(error==MKEvent.ERROR_RESULT_NOT_FOUND){
				Toast.makeText(MyMapActivity.this, "sorry,not found the result", Toast.LENGTH_LONG).show();
				return;
			}else if(error!=0 || res==null){
				Toast.makeText(MyMapActivity.this, "search wrong", Toast.LENGTH_LONG).show();
				return;
			}
			Toast.makeText(MyMapActivity.this, "showing the result", Toast.LENGTH_LONG).show();
			//poi�����ʾ��ͼ��
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
			
			//·������
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
