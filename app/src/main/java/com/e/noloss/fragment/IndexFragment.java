package com.e.noloss.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;
import com.e.noloss.MapActivity;
import com.e.noloss.R;
import com.e.noloss.pojo.LatAndLon;
import com.e.noloss.utils.MqttManager;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.io.StringReader;

public class IndexFragment extends Fragment implements View.OnClickListener {
    private String host="tcp://192.168.0.101:1883";
    private String clientId=MqttClient.generateClientId();
    private MqttManager sMqttManager=null;
    private MqttClient mMqttClient;
    private MqttConnectOptions mMqttConnectOptions;
    private static IndexFragment instance=null;
    private MapView mMapView;
    private Button mButton,mButton2,mButton3;
    private BaiduMap mBaiduMap;
    private LocationClient mLocationClient;
    private MqttManager mMqttManager;
    private boolean isFirstLocate=true;
    private Gson mGson=new Gson();
    private InfoWindow mInfoWindow;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.index_fragment, container, false);
        mMapView=view.findViewById(R.id.index_map);
        mButton=view.findViewById(R.id.current_hard);
        mButton2=view.findViewById(R.id.current_place);
        mButton3=view.findViewById(R.id.current_add);
        mBaiduMap=mMapView.getMap();
        mBaiduMap.setMyLocationEnabled(true);
        mButton.setOnClickListener(this);
        mButton2.setOnClickListener(this);
        mButton3.setOnClickListener(this);
        mLocationClient=new LocationClient(getContext());
        mLocationClient.registerLocationListener(new locationListener());
        setLocationOption();
        mLocationClient.start();
        mMqttManager=new MqttManager(getContext());
        initMqtt();
        return view;
    }
    public static IndexFragment getInstance() {
        if (instance == null) {
            instance = new IndexFragment();
        }
        return instance;
    }
    private void initMqtt(){
        try {
            mMqttClient=new MqttClient(host,clientId,new MemoryPersistence());
            mMqttConnectOptions=new MqttConnectOptions();
            mMqttClient.setCallback(mMqttCallback);
            mMqttClient.connect(mMqttConnectOptions);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    private void subscribe(String topic,int qos){
        if(mMqttClient!=null){
            try {
                mMqttClient.subscribe(topic,qos);
                Log.d("TAG","订阅了topic"+topic);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }
    private MqttCallback mMqttCallback=new MqttCallback() {
        @Override
        public void connectionLost(Throwable cause) {
            System.out.println("connectionLost");
            Log.i("TAG","connection lost");
        }

        @Override
        public void messageArrived(String topic, MqttMessage message){
            LatAndLon latAndLon = mGson.fromJson(new String(message.getPayload()), LatAndLon.class);
            System.out.println(latAndLon.toString());
//            MyLocationData locationData=new MyLocationData.Builder()
//                    .latitude(latAndLon.getLat())
//                    .longitude(latAndLon.getLon()).build();
//            LatLng ll = new LatLng(latAndLon.getLat(), latAndLon.getLon());
//            MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll, 16.0f);
//            mBaiduMap.animateMapStatus(u);
//            mBaiduMap.setMyLocationData(locationData);
            //todo 1
            LatLng latLng=new LatLng(latAndLon.getLat(),latAndLon.getLon());
            OverlayOptions overlayOptions=new TextOptions()
                    .text("物品位置")
                    .bgColor(0xAAFFFF00)
                    .fontSize(24)
                    .fontColor(0xFFFF00FF)
                    .rotate(-30)
                    .position(latLng);
            Overlay mText=mBaiduMap.addOverlay(overlayOptions);
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {
            System.out.println("deliveryComplete");
            Log.i("TAG","deliveryComplete");
        }
    };

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
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.current_hard:
                MqttMessage mqttMessage=new MqttMessage();
                mqttMessage.setPayload("0".getBytes());
                mqttMessage.setQos(1);
                try {
                    mMqttClient.publish("cat",mqttMessage);
                } catch (MqttException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.current_place:
                subscribe("chat",1);
                break;
            case R.id.current_add:
                MqttMessage mqttMessage1=new MqttMessage();
                mqttMessage1.setPayload("1".getBytes());
                mqttMessage1.setQos(1);
                try {
                    mMqttClient.publish("cat",mqttMessage1);
                } catch (MqttException e) {
                    e.printStackTrace();
                }
                break;

        }

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
            MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll, 16.0f);
            mBaiduMap.animateMapStatus(u);
            mBaiduMap.setMyLocationData(locationData);
        }
    }
}
