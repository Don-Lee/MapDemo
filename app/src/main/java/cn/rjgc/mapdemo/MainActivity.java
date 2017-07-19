package cn.rjgc.mapdemo;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

import cn.rjgc.mapdemo.databinding.ActivityMainBinding;
import cn.rjgc.mapdemo.utils.CheckPermissionActivity;
import cn.rjgc.mapdemo.utils.Utils;

public class MainActivity extends CheckPermissionActivity {

    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationClientOption = null;
    private ActivityMainBinding mainBinding;

    private AMapLocation mapLocation = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        //初始化定位
        initLocation();

    }

    public void selectAddress(View view) {
        Intent intent = new Intent(this, PoiAddressActivity.class);
        intent.putExtra("latitude", mapLocation.getLatitude());
        intent.putExtra("longitude", mapLocation.getLongitude());
        startActivity(intent);
    }

    private void initLocation() {
        //初始化client
        locationClient = new AMapLocationClient(getApplicationContext());
        locationClientOption = getDefaultOption();
        //设置定位参数
        locationClient.setLocationOption(locationClientOption);
        //设置定位监听
        locationClient.setLocationListener(locationListener);

        // 启动定位
        locationClient.startLocation();
    }

    //默认的定位参数
    private AMapLocationClientOption getDefaultOption() {
        AMapLocationClientOption mapLocationClientOption = new AMapLocationClientOption();
        mapLocationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mapLocationClientOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mapLocationClientOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mapLocationClientOption.setInterval(2000);//可选，设置定位间隔。默认为2秒
        mapLocationClientOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
        mapLocationClientOption.setOnceLocation(false);//可选，设置是否单次定位。默认是false
        mapLocationClientOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        mapLocationClientOption.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
        mapLocationClientOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mapLocationClientOption.setLocationCacheEnable(true); //可选，设置是否使用缓存定位，默认为true
        return mapLocationClientOption;
    }

    //定位监听
    AMapLocationListener locationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation location) {
            if (location != null) {
                StringBuffer sb = new StringBuffer();
                //errCode等于0代表定位成功，其他的为定位失败，具体的可以参照官网定位错误码说明
                if (location.getErrorCode() == 0) {
                    mapLocation = location;
                    sb.append("定位成功" + "\n");
                    sb.append("定位类型: " + location.getLocationType() + "\n");
                    sb.append("经    度    : " + location.getLongitude() + "\n");
                    sb.append("纬    度    : " + location.getLatitude() + "\n");
                    sb.append("精    度    : " + location.getAccuracy() + "米" + "\n");
                    sb.append("提供者    : " + location.getProvider() + "\n");
                    sb.append("速    度    : " + location.getSpeed() + "米/秒" + "\n");
                    sb.append("角    度    : " + location.getBearing() + "\n");
                    // 获取当前提供定位服务的卫星个数
                    sb.append("星    数    : " + location.getSatellites() + "\n");
                    sb.append("国    家    : " + location.getCountry() + "\n");
                    sb.append("省            : " + location.getProvince() + "\n");
                    sb.append("市            : " + location.getCity() + "\n");
                    sb.append("城市编码 : " + location.getCityCode() + "\n");
                    sb.append("区            : " + location.getDistrict() + "\n");
                    sb.append("区域 码   : " + location.getAdCode() + "\n");
                    sb.append("地    址    : " + location.getAddress() + "\n");
                    sb.append("兴趣点    : " + location.getPoiName() + "\n");
                    //定位完成的时间
                    sb.append("定位时间: " + Utils.formatUTC(location.getTime(), "yyyy-MM-dd HH:mm:ss") + "\n");
                } else {
                    //定位失败
                    sb.append("定位失败" + "\n");
                    sb.append("错误码:" + location.getErrorCode() + "\n");
                    sb.append("错误信息:" + location.getErrorInfo() + "\n");
                    sb.append("错误描述:" + location.getLocationDetail() + "\n");
                    mapLocation = null;
                }
                //定位之后的回调时间
                sb.append("回调时间: " + Utils.formatUTC(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss") + "\n");

                //解析定位结果，
                String result = sb.toString();
                mainBinding.address.setText(result);
            } else {
                mainBinding.address.setText("定位失败");
                mapLocation = null;
            }

        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        // 停止定位
        locationClient.stopLocation();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationClient != null) {
        /**
         * 如果AMapLocationClient是在当前Activity实例化的，
         * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
         */
            locationClient.onDestroy();
            locationClient = null;
            locationClientOption = null;
        }
    }
}
