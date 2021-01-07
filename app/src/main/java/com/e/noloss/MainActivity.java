package com.e.noloss;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.icu.util.LocaleData;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.e.noloss.fragment.IndexFragment;

/**
 * 百度基站定位错误返回码
 */
// 61 ： GPS定位结果
// 62 ： 扫描整合定位依据失败。此时定位结果无效。
// 63 ： 网络异常，没有成功向服务器发起请求。此时定位结果无效。
// 65 ： 定位缓存的结果。
// 66 ： 离线定位结果。通过requestOfflineLocaiton调用时对应的返回结果
// 67 ： 离线定位失败。通过requestOfflineLocaiton调用时对应的返回结果
// 68 ： 网络连接失败时，查找本地离线定位时对应的返回结果
// 161： 表示网络定位结果
// 162~167： 服务端定位失败
// 502：KEY参数错误
// 505：KEY不存在或者非法
// 601：KEY服务被开发者自己禁用
// 602: KEY Mcode不匹配,意思就是您的ak配置过程中安全码设置有问题，请确保： sha1正确，“;”分号是英文状态；且包名是您当前运行应用的包名
// 501-700：KEY验证失败

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private IndexFragment mIndexFragment;
    private Toolbar mToolbar;
    private TextView mToolbarTextView;
    private static int FRAGMENT_INDEX=0;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        showFragment(FRAGMENT_INDEX);

    }
    private void initView(){
        mToolbar=findViewById(R.id.tool_bar);
        mToolbarTextView=findViewById(R.id.tool_bar_text_view);
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void showFragment(int index){
        FragmentTransaction transaction= getSupportFragmentManager().beginTransaction();
        hideFragment(transaction);
        switch (index){
            case 0:
                mToolbar.setTitle(" ");
                mToolbarTextView.setText("设备");
                if(mIndexFragment==null){
                    mIndexFragment=mIndexFragment.getInstance();
                    transaction.add(R.id.fragment,mIndexFragment,IndexFragment.class.getName());
                }else {
                    transaction.show(mIndexFragment);
                }
                break;

        }
        transaction.commit();

    }
    private void hideFragment(FragmentTransaction transaction){
        if(mIndexFragment!=null){
            transaction.hide(mIndexFragment);
        }
    }
    private void initLocationClientOptions(){

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
//            case R.id.show_map:
//                Intent intent=new Intent(this,MapActivity.class);
//                startActivity(intent);
        }

    }

}