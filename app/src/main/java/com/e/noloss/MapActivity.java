package com.e.noloss;

import androidx.appcompat.app.AppCompatActivity;

import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

public class MapActivity extends AppCompatActivity {
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private LocationClient mLocationClient;
    private boolean isFirstLocate=true;
    private Button placeButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        initView();
        mBaiduMap.setMyLocationEnabled(true);

    }
    private void initView(){
        mMapView=findViewById(R.id.map_view);
        mBaiduMap=mMapView.getMap();
        mLocationClient=new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(new locationListener());
        setLocationOption();
        mLocationClient.start();   
        placeButton=findViewById(R.id.place);
        placeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyLocationData locationData=new MyLocationData.Builder()
                        .latitude(36.68278473)
                        .longitude(117.02496707).build();
                mBaiduMap.setMyLocationData(locationData);
            }
        });
    }
    private void setLocationOption(){
        LocationClientOption option=new LocationClientOption();
        option.setIsNeedAddress(true);
        option.setNeedNewVersionRgc(true);
        option.setWifiCacheTimeOut(5*24*60*60);
        option.setCoorType("bd0911");
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        mLocationClient.setLocOption(option);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }
    public class locationListener extends BDAbstractLocationListener{
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if(bdLocation==null&&mMapView==null){
                return;
            }
            LatLng ll = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
            if (isFirstLocate) {
                isFirstLocate = false;
                //给地图设置状态
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLng(ll));
            }
            MyLocationData locationData=new MyLocationData.Builder()
                    .latitude(bdLocation.getLatitude())
                    .longitude(bdLocation.getLongitude()).build();
            System.out.println(bdLocation.getRadius()+"radis");
            System.out.println(bdLocation.getDirection()+"getDirection");
            System.out.println(bdLocation.getLatitude()+"getLatitude");
            System.out.println(bdLocation.getLongitude()+"getLongitude");
            mBaiduMap.setMyLocationData(locationData);
        }
    }

}