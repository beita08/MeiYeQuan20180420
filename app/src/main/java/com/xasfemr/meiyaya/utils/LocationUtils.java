package com.xasfemr.meiyaya.utils;


import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;

import java.io.IOException;
import java.util.List;

/**
 * Created by sen.luo on 2018/3/12.
 */

public class LocationUtils {

    // 纬度
    public static double latitude = 0.0;
    // 经度
    public static double longitude = 0.0;
    public static LocationManager locationManager;
    public static Location location;
    private static String provider;
    /**
     * 初始化位置信息
     *
     * @param context
     */
    public static void initLocation(Context context) {

        LocationListener locationListener = new LocationListener() {

            @Override
            public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onProviderEnabled(String arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onProviderDisabled(String arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onLocationChanged(Location arg0) {
                // TODO Auto-generated method stub
                // 更新当前经纬度
            }
        };

        //获取定位服务
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        //获取当前可用的位置控制器
        List<String> list = locationManager.getProviders(true);

        if (list.contains(LocationManager.GPS_PROVIDER)) {
            //是否为GPS位置控制器
            provider = LocationManager.GPS_PROVIDER;
        }
        else if (list.contains(LocationManager.NETWORK_PROVIDER)) {
            //是否为网络位置控制器
            provider = LocationManager.NETWORK_PROVIDER;

        } else {
            longitude=108.9470279217;  //如果用户没有给与权限  默认返回钟楼的坐标
            latitude=34.2594067084;
//            ToastUtil.showShort(context, "请检查网络或GPS是否打开");
            return;
        }

        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( context, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission( context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return  ;
        }


        location = locationManager.getLastKnownLocation(provider);


        if (location != null) {
            //获取当前位置，这里只用到了经纬度
            String stringPosition = "纬度为：" + location.getLatitude() + ",经度为："
                    + location.getLongitude();
            longitude=location.getLongitude();
            latitude=location.getLatitude();
//            Toast.makeText(context, stringPosition, Toast.LENGTH_LONG).show();

        }
        //绑定定位事件，监听位置是否改变
        //第一个参数为控制器类型第二个参数为监听位置变化的时间间隔（单位：毫秒）
        //第三个参数为位置变化的间隔（单位：米）第四个参数为位置监听器

        locationManager.requestLocationUpdates(provider, 2000, 2, locationListener);


    }


    public static String getAddress(Context context,Double lon,Double lat ) throws IOException {
//        if(location==null){
//            LogUtils.show("错误","未找到location");
//            return "";
//        }

        LogUtils.show("经纬度",Double.toString(lon)+Double.toString(lat));

        Geocoder geocoder = new Geocoder(context);
        boolean flag = geocoder.isPresent();
        LogUtils.show("位置信息","the flag is "+flag);
        StringBuilder stringBuilder = new StringBuilder();
        try {

            //根据经纬度获取地理位置信息
            List<Address> addresses = geocoder.getFromLocation(34.2575002055,108.9479827881, 1);

//            LogUtils.show("纬度",Double.toString(location.getLongitude()));


            //根据地址获取地理位置信息
            //List<Address> addresses = geocoder.getFromLocationName( "广东省珠海市香洲区沿河路321号", 1);

            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                    stringBuilder.append(address.getAddressLine(i)).append("\n");
                }
                stringBuilder.append(address.getCountryName()).append("_");//国家
                stringBuilder.append(address.getFeatureName()).append("_");//周边地址
                stringBuilder.append(address.getLocality()).append("_");//市
                stringBuilder.append(address.getPostalCode()).append("_");
                stringBuilder.append(address.getCountryCode()).append("_");//国家编码
                stringBuilder.append(address.getAdminArea()).append("_");//省份
                stringBuilder.append(address.getSubAdminArea()).append("_");
                stringBuilder.append(address.getThoroughfare()).append("_");//道路
                stringBuilder.append(address.getSubLocality()).append("_");//香洲区
                stringBuilder.append(address.getLatitude()).append("_");//经度
                stringBuilder.append(address.getLongitude());//维度
                /*System.out.println(stringBuilder.toString());*/
                LogUtils.show("获取到的地理位置为：",stringBuilder.toString());

            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            ToastUtil.showShort(context, "未获取到的地理位置");
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }
}
