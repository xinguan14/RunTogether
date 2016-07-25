package com.xinguan14.jdyp;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.SDKInitializer;
import com.orhanobut.logger.Logger;
import com.xinguan14.jdyp.base.UniversalImageLoader;
import com.xinguan14.jdyp.bean.User;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import cn.bmob.newim.BmobIM;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.listener.UpdateListener;

/**
 * @author :smile
 * @project:BmobIMApplication
 * @date :2016-01-13-10:19
 */
public class MyApplication extends Application {


    public LocationClient mLocationClient;
    public MyLocationListener mMyLocationListener;

    private static MyApplication INSTANCE;

    public static MyApplication INSTANCE() {
        return INSTANCE;
    }

    public static BmobGeoPoint lastPoint = null;// 上一次定位到的经纬度

    private void setInstance(MyApplication app) {
        setBmobIMApplication(app);
    }

    private static void setBmobIMApplication(MyApplication a) {
        MyApplication.INSTANCE = a;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setInstance(this);
        //初始化
        Logger.init("smile");
        //只有主进程运行的时候才需要初始化
        if (getApplicationInfo().packageName.equals(getMyProcessName())) {
            //im初始化
            BmobIM.init(this);
            //注册消息接收器
            BmobIM.registerDefaultMessageHandler(new DemoMessageHandler(this));
        }
        //uil初始化
        UniversalImageLoader.initImageLoader(this);
        INSTANCE = this;
        initBaidu();
    }

    /**
     * 获取当前运行的进程名
     *
     * @return
     */
    public static String getMyProcessName() {
        try {
            File file = new File("/proc/" + android.os.Process.myPid() + "/" + "cmdline");
            BufferedReader mBufferedReader = new BufferedReader(new FileReader(file));
            String processName = mBufferedReader.readLine().trim();
            mBufferedReader.close();
            return processName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public final String PREF_LATITUDE = "latitude";// 经度
    private String latitude = "";

    /**
     * 获取纬度
     *
     * @return
     */
    public String getLatitude() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        latitude = preferences.getString(PREF_LATITUDE, "");
        return latitude;
    }

    /**
     * 设置维度
     *
     * @param pwd
     */
    public void setLatitude(String lat) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        if (editor.putString(PREF_LATITUDE, lat).commit()) {
            latitude = lat;
        }
    }

    public final String PREF_LONGTITUDE = "longtitude";// 经度
    private String longtitude = "";

    /**
     * 获取经度
     *
     * @return
     */
    public String getLongtitude() {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        longtitude = preferences.getString(PREF_LONGTITUDE, "");
        return longtitude;
    }

    /**
     * 设置经度
     *
     * @param pwd
     */
    public void setLongtitude(String lon) {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        if (editor.putString(PREF_LONGTITUDE, lon).commit()) {
            longtitude = lon;
        }
    }
    /**
     * 初始化百度相关sdk initBaidumap
     *
     * @param
     * @return void
     * @throws
     * @Title: initBaidumap
     * @Description: TODO
     */
    private void initBaidu() {
        // 初始化地图Sdk
        SDKInitializer.initialize(this);
        // 初始化定位sdk
        initBaiduLocClient();
    }

    /**
     * 初始化百度定位sdk
     *
     * @param
     * @return void
     * @throws
     * @Title: initBaiduLocClient
     * @Description: TODO
     */
    private void initBaiduLocClient() {
        mLocationClient = new LocationClient(this.getApplicationContext());
        mMyLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(mMyLocationListener);
    }

    /**
     * 实现实位回调监听
     */
    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // Receive Location
            double latitude = location.getLatitude();
            double longtitude = location.getLongitude();
            User u = BmobUser.getCurrentUser(getApplicationContext(),User.class);
            final User user = new User();
            BmobGeoPoint bmobGeoPoint = new BmobGeoPoint(longtitude,latitude);
            user.setLocation(bmobGeoPoint);
            user.setObjectId(u.getObjectId());
            user.update(getApplicationContext(),new UpdateListener() {
                @Override
                public void onSuccess() {
                    // TODO Auto-generated method stub
                    setLatitude(String.valueOf(user.getLocation().getLatitude()));
                    //getInstance().setLongtitude(String.valueOf(user.getLocation().getLongitude()));
//						ShowLog("经纬度更新成功");
                }
                @Override
                public void onFailure(int code, String msg) {
                    // TODO Auto-generated method stub
//						ShowLog("经纬度更新 失败:"+msg);
                }
            });
            if (lastPoint != null) {
                if (lastPoint.getLatitude() == location.getLatitude()
                        && lastPoint.getLongitude() == location.getLongitude()) {
//					BmobLog.i("两次获取坐标相同");// 若两次请求获取到的地理位置坐标是相同的，则不再定位
                    mLocationClient.stop();
                    return;
                }
            }
            lastPoint = new BmobGeoPoint(longtitude, latitude);
        }
    }
}
