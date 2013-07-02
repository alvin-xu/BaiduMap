package com.example.baidumap;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.platform.comapi.basestruct.GeoPoint;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;

public class MyMapActivity extends Activity {

	private BMapManager mBMapMan = null;  
	private MapView mMapView = null;
	private LocationClient mLocClient;
	private MyLocationOverlay myLocationOverlay;
	private LocationData locData;
	private boolean isFirstLoc=true;
	
	private MyLocationListener myListener=new MyLocationListener();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mBMapMan=new BMapManager(getApplication());  
		mBMapMan.init("126AD953B2D1E29A49D3392BC98E03E594F31971", null);    
		//ע�⣺��������setContentViewǰ��ʼ��BMapManager���󣬷���ᱨ��  
		setContentView(R.layout.activity_my_map);  
		mMapView=(MapView)findViewById(R.id.bmapView);  
		mMapView.setBuiltInZoomControls(true);  
		mMapView.setTraffic(true);
		//mMapView.setSatellite(true);
		//�����������õ����ſؼ�  
		MapController mMapController=mMapView.getController();  
		// �õ�mMapView�Ŀ���Ȩ,�����������ƺ�����ƽ�ƺ�����  
		GeoPoint point =new GeoPoint((int)(28.496* 1E6),(int)(118.182* 1E6));  
		//�ø����ľ�γ�ȹ���һ��GeoPoint����λ��΢�� (�� * 1E6)  
		mMapController.setCenter(point);//���õ�ͼ���ĵ�  
		mMapController.setZoom(12);//���õ�ͼzoom����  
		
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
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.my_map, menu);
		return true;
	}
	
	@Override  
	protected void onDestroy(){  
	        mMapView.destroy();  
	        if(mBMapMan!=null){  
	                mBMapMan.destroy();  
	                mBMapMan=null;  
	        }  
	        super.onDestroy();  
	}  
	@Override  
	protected void onPause(){  
	        mMapView.onPause();  
	        if(mBMapMan!=null){  
	               mBMapMan.stop();  
	        }  
	        super.onPause();  
	}  
	@Override  
	protected void onResume(){  
	        mMapView.onResume();  
	        if(mBMapMan!=null){  
	                mBMapMan.start();  
	        }  
	       super.onResume();  
	}  
	
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
            if (isFirstLoc){
            	//�ƶ���ͼ����λ��
                mMapView.getController().animateTo(new GeoPoint((int)(locData.latitude* 1e6), (int)(locData.longitude *  1e6)));
                Log.d("Listener", "animate to ....");
               
            }
            //�״ζ�λ���
            isFirstLoc = false;
		}

		@Override
		public void onReceivePoi(BDLocation arg0) {
			// TODO Auto-generated method stub
			
		}
		
	}
}
