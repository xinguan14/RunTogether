package com.xinguan14.jdyp.trackshow;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Looper;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.baidu.trace.OnGeoFenceListener;
import com.xinguan14.jdyp.R;
import com.xinguan14.jdyp.trackutils.DateUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 地理围栏
 */
@SuppressLint("NewApi")
public class Geofence implements OnClickListener {

    protected PopupWindow popupwindow = null;

    private Button btnCircularFence = null;
    private Button btnVertexesFence = null;
    private Button btnDeletefence = null;
    private Button btnMonitoredstatus = null;
    private Button btnHistoryalarm = null;

    private LayoutInflater mInflater = null;

    // 围栏圆心纬度
    private double latitude = 0;

    // 围栏圆心经度
    private double longitude = 0;

    // 围栏半径
    protected static int radius = 100;

    // 顶点数量
    protected static int vertexNumber = 4;

    protected static int radiusTemp = radius;

    private List<LatLng> vertexs = new ArrayList<LatLng>();

    private List<LatLng> vertexsTemp = new ArrayList<LatLng>();

    // 围栏类型（0：圆形、1：多边形）
    private static int fenceType = 0;

    // 围栏编号
    protected static int fenceId = 0;

    // 地理围栏监听器
    protected static OnGeoFenceListener geoFenceListener = null;

    protected static Overlay circleFenceOverlay = null;

    protected static Overlay vertexesFenceOverlay = null;

    private List<Overlay> overlays = new ArrayList<Overlay>();

    // 围栏去噪精度
    private static int precision = 30;

    // 围栏覆盖物
    protected static OverlayOptions circleFenceOverlaysOption = null;

    // 多边形围栏覆盖物
    protected static OverlayOptions vertexesFenceOverlayOption = null;

    protected static OverlayOptions circleFenceOverlayOptionsTemp = null;

    protected static OverlayOptions vertexesFenceOverlayOptionsTemp = null;

    private BitmapDescriptor pointBitmap = null;

    protected static boolean isShow = false;

    private Context mContext = null;

    protected OnMapClickListener mapClickListener = new OnMapClickListener() {

        public void onMapClick(LatLng arg0) {
            // TODO Auto-generated method stub

            switch (fenceType) {
                case 0:
                    if (null != circleFenceOverlaysOption) {
                        circleFenceOverlayOptionsTemp = circleFenceOverlaysOption;
                    }
                    if (null != circleFenceOverlay) {
                        circleFenceOverlay.remove();
                    }
                    latitude = arg0.latitude;
                    longitude = arg0.longitude;

                    circleFenceOverlaysOption = new CircleOptions().fillColor(0x000000FF).center(arg0)
                            .stroke(new Stroke(5, Color.rgb(0xff, 0x00, 0x33)))
                            .radius(radius);

                    addMarker();
                    createOrUpdateDialog();

                    break;

                case 1:

                    vertexs.add(arg0);

                    int resourceId = R.mipmap.icon_gcoding;

                    Resources res = mContext.getResources();
                    if (vertexs.size() <= 10) {
                        resourceId =
                                res.getIdentifier("icon_mark" + vertexs.size(), "mipmap", mContext.getPackageName());
                    } else {
                        resourceId =
                                res.getIdentifier("icon_gcoding", "mipmap",
                                        mContext.getPackageName());
                    }

                    pointBitmap = BitmapDescriptorFactory
                            .fromResource(resourceId);

                    OverlayOptions overlayOptions = new MarkerOptions().position(arg0)
                            .icon(pointBitmap).zIndex(9).draggable(true);
                    overlays.add(BaiduActivity.mBaiduMap.addOverlay(overlayOptions));

                    if (vertexs.size() == vertexNumber) {

                        if (null != vertexesFenceOverlayOption) {
                            vertexesFenceOverlayOptionsTemp = vertexesFenceOverlayOption;
                        }
                        if (null != vertexesFenceOverlay) {
                            vertexesFenceOverlay.remove();
                        }

                        vertexesFenceOverlayOption =
                                new PolygonOptions().points(vertexs).stroke(new Stroke(vertexNumber, 0xAAFF0000))
                                        .fillColor(0x30FFFFFF);
                        addMarker();
                        createOrUpdateDialog();
                    }

                    break;

                default:
                    break;
            }

        }

        public boolean onMapPoiClick(MapPoi arg0) {
            // TODO Auto-generated method stub
            return false;
        }
    };

    public Geofence(Context context, LayoutInflater inflater) {

        initOnGeoFenceListener();

        mContext = context;
        mInflater = inflater;
        if (null == circleFenceOverlaysOption && null == vertexesFenceOverlayOption) {
            queryFenceList();
        }
    }

    /**
     * 添加entity
     */
    protected static void addEntity() {
        // entity标识
        String entityName = BaiduActivity.entityName;
        // 属性名称（格式 : "key1=value1,columnKey2=columnValue2......."）
        String columnKey = "name=testName";
        BaiduActivity.client.addEntity(BaiduActivity.serviceId, entityName, columnKey, BaiduActivity.entityListener);
    }

    /**
     * 创建圆形围栏（若创建围栏时，还未创建entity标识，请先使用addEntity(...)添加entity）
     */
    private void createCircularFence() {

        // 创建者（entity标识）
        String creator = BaiduActivity.entityName;
        // 围栏名称
        String fenceName = BaiduActivity.entityName + "_fence";
        // 围栏描述
        String fenceDesc = "test";
        // 监控对象列表（多个entityName，以英文逗号"," 分割）
        String monitoredPersons = BaiduActivity.entityName;
        // 观察者列表（多个entityName，以英文逗号"," 分割）
        String observers = BaiduActivity.entityName;
        // 生效时间列表
        String validTimes = "0800,2300";
        // 生效周期
        int validCycle = 4;
        // 围栏生效日期
        String validDate = "";
        // 生效日期列表
        String validDays = "";
        // 坐标类型 （1：GPS经纬度，2：国测局经纬度，3：百度经纬度）
        int coordType = 3;
        // 围栏圆心（圆心位置, 格式 : "经度,纬度"）
        String center = longitude + "," + latitude;
        // 围栏半径（单位 : 米）
        double radius = Geofence.radius;
        // 报警条件（1：进入时触发提醒，2：离开时触发提醒，3：进入离开均触发提醒）
        int alarmCondition = 3;

        BaiduActivity.client.createCircularFence(BaiduActivity.serviceId, creator, fenceName, fenceDesc,
                monitoredPersons, observers,
                validTimes, validCycle, validDate, validDays, coordType, center, radius, precision, alarmCondition,
                geoFenceListener);

    }

    /**
     * 创建多边形围栏（若创建围栏时，还未创建entity标识，请先使用addEntity(...)添加entity）
     */
    private void createVertexesFence() {

        // 创建者（entity标识）
        String creator = BaiduActivity.entityName;
        // 围栏名称
        String fenceName = BaiduActivity.entityName + "_fence";
        // 围栏描述
        String fenceDesc = "test";
        // 监控对象列表（多个entityName，以英文逗号"," 分割）
        String monitoredPersons = BaiduActivity.entityName;
        // 观察者列表（多个entityName，以英文逗号"," 分割）
        String observers = BaiduActivity.entityName;
        // 生效时间列表
        String validTimes = "0800,2300";
        // 生效周期
        int validCycle = 4;
        // 围栏生效日期
        String validDate = "";
        // 生效日期列表
        String validDays = "";
        // 坐标类型 （1：GPS经纬度，2：国测局经纬度，3：百度经纬度）
        int coordType = 3;
        // 报警条件（1：进入时触发提醒，2：离开时触发提醒，3：进入离开均触发提醒）
        int alarmCondition = 3;
        // 顶点列表
        StringBuilder vertexsStr = new StringBuilder();
        for (LatLng ll : vertexs) {
            vertexsStr.append(ll.longitude).append(",").append(ll.latitude).append(";");
        }
        BaiduActivity.client.createVertexesFence(BaiduActivity.serviceId, creator, fenceName, fenceDesc,
                monitoredPersons, observers,
                validTimes, validCycle, validDate, validDays, coordType,
                vertexsStr.substring(0, vertexsStr.length() - 1), precision,
                alarmCondition,
                geoFenceListener);

    }

    /**
     * 删除围栏
     */
    private static void deleteFence(int fenceId) {
        BaiduActivity.client.deleteFence(BaiduActivity.serviceId, fenceId, geoFenceListener);
    }

    /**
     * 更新圆形围栏
     */
    private void updateCircularFence() {
        // 围栏名称
        String fenceName = BaiduActivity.entityName + "_fence";
        // 围栏ID
        int fenceId = Geofence.fenceId;
        // 围栏描述
        String fenceDesc = "test fence";
        // 监控对象列表（多个entityName，以英文逗号"," 分割）
        String monitoredPersons = BaiduActivity.entityName;
        // 观察者列表（多个entityName，以英文逗号"," 分割）
        String observers = BaiduActivity.entityName;
        // 生效时间列表
        String validTimes = "0800,2300";
        // 生效周期
        int validCycle = 4;
        // 围栏生效日期
        String validDate = "";
        // 生效日期列表
        String validDays = "";
        // 坐标类型 （1：GPS经纬度，2：国测局经纬度，3：百度经纬度）
        int coordType = 3;
        // 围栏圆心（圆心位置, 格式 : "经度,纬度"）
        String center = longitude + "," + latitude;
        // 围栏半径（单位 : 米）
        double radius = Geofence.radius;
        // 报警条件（1：进入时触发提醒，2：离开时触发提醒，3：进入离开均触发提醒）
        int alarmCondition = 3;

        BaiduActivity.client.updateCircularFence(BaiduActivity.serviceId, fenceName, fenceId, fenceDesc,
                monitoredPersons,
                observers, validTimes, validCycle, validDate, validDays, coordType, center, radius, precision,
                alarmCondition,
                geoFenceListener);
    }

    /**
     * 更新多边形围栏
     */
    private void updateVertexesFence() {
        // 围栏名称
        String fenceName = BaiduActivity.entityName + "_fence";
        // 围栏ID
        int fenceId = Geofence.fenceId;
        // 围栏描述
        String fenceDesc = "test fence";
        // 监控对象列表（多个entityName，以英文逗号"," 分割）
        String monitoredPersons = BaiduActivity.entityName;
        // 观察者列表（多个entityName，以英文逗号"," 分割）
        String observers = BaiduActivity.entityName;
        // 生效时间列表
        String validTimes = "0800,2300";
        // 生效周期
        int validCycle = 4;
        // 围栏生效日期
        String validDate = "";
        // 生效日期列表
        String validDays = "";
        // 坐标类型 （1：GPS经纬度，2：国测局经纬度，3：百度经纬度）
        int coordType = 3;
        // 顶点列表
        StringBuilder vertexsStr = new StringBuilder();
        for (LatLng ll : vertexs) {
            vertexsStr.append(ll.longitude).append(",").append(ll.latitude).append(";");
        }
        System.out.println("vertexs : " + vertexsStr.substring(0, vertexsStr.length() - 1));
        // 报警条件（1：进入时触发提醒，2：离开时触发提醒，3：进入离开均触发提醒）
        int alarmCondition = 3;
        BaiduActivity.client.updateVertexesFence(BaiduActivity.serviceId, fenceName, fenceId, fenceDesc,
                monitoredPersons, observers,
                validTimes, validCycle, validDate, validDays, coordType,
                vertexsStr.substring(0, vertexsStr.length() - 1), precision, alarmCondition,
                geoFenceListener);
    }

    /**
     * 围栏列表
     */
    protected static void queryFenceList() {
        // 创建者（entity标识）
        String creator = BaiduActivity.entityName;
        // 围栏ID列表
        String fenceIds = "";
        BaiduActivity.client.queryFenceList(BaiduActivity.serviceId, creator, fenceIds, geoFenceListener);
    }

    /**
     * 监控状态
     */
    private void monitoredStatus() {
        // 围栏ID
        int fenceId = Geofence.fenceId;
        // 监控对象列表（多个entityName，以英文逗号"," 分割）
        String monitoredPersons = BaiduActivity.entityName;
        BaiduActivity.client.queryMonitoredStatus(BaiduActivity.serviceId, fenceId, monitoredPersons,
                geoFenceListener);
    }

    /**
     * 指定位置的监控状态
     */
    @SuppressWarnings("unused")
    private void monitoredStatusByLocation() {
        // 围栏ID
        int fenceId = Geofence.fenceId;
        // 监控对象列表（多个entityName，以英文逗号"," 分割）
        String monitoredPersons = BaiduActivity.entityName;
        BaiduActivity.client.queryMonitoredStatusByLocation(BaiduActivity.serviceId, fenceId,
                monitoredPersons, "116.31283995461331,40.0469717410504,3", geoFenceListener);

        BaiduActivity.client.queryMonitoredStatusByLocation(BaiduActivity.serviceId, fenceId,
                monitoredPersons, "117,41,3", geoFenceListener);
    }

    /**
     * 报警信息
     */
    private void historyAlarm() {
        // 围栏ID
        int fenceId = Geofence.fenceId;
        // 监控对象列表（多个entityName，以英文逗号"," 分割）
        String monitoredPersons = BaiduActivity.entityName;
        // 开始时间（unix时间戳）
        int beginTime = (int) (System.currentTimeMillis() / 1000 - 12 * 60 * 60);
        // 结束时间（unix时间戳）
        int endTime = (int) (System.currentTimeMillis() / 1000);

        BaiduActivity.client.queryFenceHistoryAlarmInfo(BaiduActivity.serviceId, fenceId, monitoredPersons, beginTime,
                endTime,
                geoFenceListener);
    }

    /**
     * 点击事件
     */
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {

            // 设置圆形围栏
            case R.id.btn_circularfence:
                fenceType = 0;
                inputDialog(mContext.getString(R.string.circular_fence_caption));
                BaiduActivity.mBaiduMap.setOnMapClickListener(mapClickListener);
                if (popupwindow != null && popupwindow.isShowing()) {
                    popupwindow.dismiss();
                }
                break;

            // 设置多边形围栏
            case R.id.btn_vertexesfence:
                fenceType = 1;
                vertexsTemp.addAll(vertexs);
                vertexs.clear();
                inputDialog(mContext.getString(R.string.vertexes_fence_caption));
                BaiduActivity.mBaiduMap.setOnMapClickListener(mapClickListener);
                if (popupwindow != null && popupwindow.isShowing()) {
                    popupwindow.dismiss();
                }
                break;

            // 删除围栏
            case R.id.btn_deletefence:
                System.out.println("删除围栏");
                deleteFence(fenceId);
                break;

            // 历史报警
            case R.id.btn_historyalarm:
                historyAlarm();
                if (popupwindow != null && popupwindow.isShowing()) {
                    popupwindow.dismiss();
                }
                break;

            // 监控对象状态
            case R.id.btn_monitoredstatus:
                monitoredStatus();
                if (popupwindow != null && popupwindow.isShowing()) {
                    popupwindow.dismiss();
                }
                break;

            default:
                break;
        }

    }

    @SuppressLint({"InflateParams", "ClickableViewAccessibility"})
    protected void initPopupWindowView() {

        // 获取自定义布局文件menu_geofence.xml的视图
        View customView = mInflater.inflate(R.layout.menu_geofence, null);
        popupwindow = new PopupWindow(customView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
        // 自定义view添加触摸事件
        customView.setOnTouchListener(new OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                if (popupwindow != null && popupwindow.isShowing()) {
                    popupwindow.dismiss();
                    popupwindow = null;
                }

                return false;
            }

        });

        btnCircularFence = (Button) customView.findViewById(R.id.btn_circularfence);
        btnVertexesFence = (Button) customView.findViewById(R.id.btn_vertexesfence);
        btnMonitoredstatus = (Button) customView.findViewById(R.id.btn_monitoredstatus);
        btnHistoryalarm = (Button) customView.findViewById(R.id.btn_historyalarm);
        btnDeletefence = (Button) customView.findViewById(R.id.btn_deletefence);

        btnCircularFence.setOnClickListener(this);
        btnVertexesFence.setOnClickListener(this);
        btnDeletefence.setOnClickListener(this);
        btnMonitoredstatus.setOnClickListener(this);
        btnHistoryalarm.setOnClickListener(this);

    }

    /**
     * 初始化OnGeoFenceListener
     */
    private void initOnGeoFenceListener() {
        // 初始化geoFenceListener
        geoFenceListener = new OnGeoFenceListener() {

            // 请求失败回调接口
            @Override
            public void onRequestFailedCallback(String arg0) {
                // TODO Auto-generated method stub
                BaiduActivity.mBaiduMap.clear();
                if (null != circleFenceOverlayOptionsTemp) {
                    circleFenceOverlaysOption = circleFenceOverlayOptionsTemp;
                }
                if (null != vertexesFenceOverlayOptionsTemp) {
                    vertexesFenceOverlayOption = vertexesFenceOverlayOptionsTemp;
                }
                radius = radiusTemp;
                addMarker();

                Toast.makeText(BaiduActivity.mContext, "geoFence请求失败回调接口消息 : " + arg0, Toast.LENGTH_LONG).show();

            }

            // 创建圆形围栏回调接口
            @Override
            public void onCreateCircularFenceCallback(String arg0) {
                // TODO Auto-generated method stub

                JSONObject dataJson = null;
                try {
                    dataJson = new JSONObject(arg0);
                    if (null != dataJson && dataJson.has("status") && 0 == dataJson.getInt("status")) {
                        fenceId = dataJson.getInt("fence_id");
                        circleFenceOverlayOptionsTemp = null;
                        showMessage("圆形围栏创建成功");
                    } else {
                        BaiduActivity.mBaiduMap.clear();
                        if (null != circleFenceOverlayOptionsTemp) {
                            circleFenceOverlaysOption = circleFenceOverlayOptionsTemp;
                        }
                        radius = radiusTemp;
                        addMarker();
                        showMessage("创建圆形围栏回调接口消息 : " + arg0);
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    showMessage("解析创建圆形围栏回调消息失败");
                }

            }

            // 更新圆形围栏回调接口
            @Override
            public void onUpdateCircularFenceCallback(String arg0) {
                // TODO Auto-generated method stub
                showMessage("更新圆形围栏回调接口消息 : " + arg0);
            }

            // 创建多边形围栏回调接口
            @Override
            public void onCreateVertexesFenceCallback(String arg0) {
                // TODO Auto-generated method stub
                JSONObject dataJson = null;
                try {
                    dataJson = new JSONObject(arg0);
                    if (null != dataJson && dataJson.has("status") && 0 == dataJson.getInt("status")) {
                        fenceId = dataJson.getInt("fence_id");
                        vertexesFenceOverlayOptionsTemp = null;
                        showMessage("多边形围栏创建成功");
                    } else {
                        BaiduActivity.mBaiduMap.clear();
                        if (null != vertexesFenceOverlayOptionsTemp) {
                            vertexesFenceOverlayOption = vertexesFenceOverlayOptionsTemp;
                        }
                        addMarker();
                        showMessage("创建多边形形围栏回调接口消息 : " + arg0);
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    showMessage("解析创建多边形围栏回调消息失败");
                }
            }

            // 更新多边形围栏回调接口
            @Override
            public void onUpdateVertexesFenceCallback(String arg0) {
                // TODO Auto-generated method stub
                showMessage("更新多边形形围栏回调接口消息 : " + arg0);
            }

            // 删除围栏回调接口
            @Override
            public void onDeleteFenceCallback(String arg0) {
                // TODO Auto-generated method stub
                showMessage(" 删除围栏回调接口消息 : " + arg0);
            }

            // 查询围栏列表回调接口
            @Override
            public void onQueryFenceListCallback(String arg0) {
                // TODO Auto-generated method stub

                JSONObject dataJson = null;
                try {
                    dataJson = new JSONObject(arg0);
                    if (null != dataJson && dataJson.has("status") && 0 == dataJson.getInt("status")) {
                        if (dataJson.has("size")) {
                            JSONArray jsonArray = dataJson.getJSONArray("fences");
                            JSONObject jsonObj = jsonArray.getJSONObject(0);
                            fenceId = jsonObj.getInt("fence_id");
                            int shape = jsonObj.getInt("shape");
                            if (1 == shape) {
                                JSONObject center = jsonObj.getJSONObject("center");

                                latitude = center.getDouble("latitude");
                                longitude = center.getDouble("longitude");
                                radius = (int) (jsonObj.getDouble("radius"));

                                LatLng latLng = new LatLng(latitude, longitude);

                                circleFenceOverlaysOption = new CircleOptions().fillColor(0x000000FF).center(latLng)
                                        .stroke(new Stroke(5, Color.rgb(0xff, 0x00, 0x33)))
                                        .radius(radius);

                            } else if (2 == shape) {

                                JSONArray vertexArray = jsonObj.getJSONArray("vertexes");
                                for (int i = 0; i < vertexArray.length(); ++i) {
                                    JSONObject vertex = vertexArray.getJSONObject(i);
                                    longitude = vertex.getDouble("longitude");
                                    latitude = vertex.getDouble("latitude");
                                    LatLng latLng = new LatLng(latitude, longitude);
                                    vertexs.add(latLng);
                                }

                                vertexesFenceOverlayOption =
                                        new PolygonOptions().points(vertexs)
                                                .stroke(new Stroke(vertexArray.length(), 0xAAFF0000))
                                                .fillColor(0x30FFFFFF);
                            }

                            addMarker();
                        }
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    System.out.println("解析围栏列表回调消息失败");
                }

            }

            // 查询历史报警回调接口
            @Override
            public void onQueryHistoryAlarmCallback(String arg0) {
                // TODO Auto-generated method stub
                StringBuffer historyAlarm = new StringBuffer();
                JSONObject dataJson = null;
                try {
                    dataJson = new JSONObject(arg0);
                    if (null != dataJson && dataJson.has("status") && 0 == dataJson.getInt("status")) {
                        int size = dataJson.getInt("size");
                        for (int i = 0; i < size; ++i) {
                            JSONArray jsonArray = dataJson.getJSONArray("monitored_person_alarms");
                            JSONObject jsonObj = jsonArray.getJSONObject(i);
                            String mPerson = jsonObj.getString("monitored_person");
                            int alarmSize = jsonObj.getInt("alarm_size");
                            JSONArray jsonAlarms = jsonObj.getJSONArray("alarms");
                            historyAlarm.append("监控对象[" + mPerson + "]报警信息\n");
                            for (int j = 0; j < alarmSize && j < jsonAlarms.length(); ++j) {
                                String action = jsonAlarms.getJSONObject(j).getInt("action") == 1 ? "进入" : "离开";
                                String date = DateUtils.getDate(jsonAlarms.getJSONObject(j).getInt("time"));
                                historyAlarm.append(date + " 【" + action + "】围栏\n");
                            }
                        }
                        if (TextUtils.isEmpty(historyAlarm)) {
                            showMessage("未查询到历史报警信息");
                        } else {
                            showMessage(historyAlarm.toString());
                        }
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    showMessage("解析查询历史报警回调消息失败");
                }

            }

            // 查询监控对象状态回调接口
            @Override
            public void onQueryMonitoredStatusCallback(String arg0) {
                // TODO Auto-generated method stub

                JSONObject dataJson = null;
                StringBuffer status = new StringBuffer();
                try {
                    dataJson = new JSONObject(arg0);
                    if (null != dataJson && dataJson.has("status") && 0 == dataJson.getInt("status")) {
                        int size = dataJson.getInt("size");
                        for (int i = 0; i < size; ++i) {
                            JSONArray jsonArray = dataJson.getJSONArray("monitored_person_statuses");
                            JSONObject jsonObj = jsonArray.getJSONObject(0);
                            String mPerson = jsonObj.getString("monitored_person");
                            int mStatus = jsonObj.getInt("monitored_status");
                            if (1 == mStatus) {
                                status.append("监控对象[ " + mPerson + " ]在围栏内\n");
                            } else if (2 == mStatus) {
                                status.append("监控对象[ " + mPerson + " ]在围栏外\n");
                            } else {
                                status.append("监控对象[ " + mPerson + " ]状态未知\n");
                            }
                        }
                        showMessage(status.toString());
                    } else {
                        showMessage("查询监控对象状态回调消息 : " + arg0);
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    showMessage("解析查询监控对象状态回调消息失败");
                }
            }
        };
    }

    // 围栏信息对话框
    @SuppressLint("InflateParams")
    public void inputDialog(final String caption) {

        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View dialogView = layoutInflater.inflate(R.layout.dialog_fence, null);

        // 获取布局中的控件
        final TextView fenceKey = (TextView) dialogView.findViewById(R.id.fence_key);
        final EditText fenceValue = (EditText) dialogView.findViewById(R.id.fence_value);
        final EditText fencePrecision = (EditText) dialogView.findViewById(R.id.fence_precision);

        fenceValue.setInputType(InputType.TYPE_CLASS_NUMBER);
        fencePrecision.setInputType(InputType.TYPE_CLASS_NUMBER);

        if (0 == fenceType) {
            fenceKey.setText(mContext.getString(R.string.fence_radius));
            fenceValue.setText(String.valueOf(radius));
        } else if (1 == fenceType) {
            fenceKey.setText(mContext.getString(R.string.vertex_number));
            fenceValue.setText(String.valueOf(vertexNumber));
        }

        fencePrecision.setText(String.valueOf(precision));

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        builder.setTitle(mContext.getString(R.string.set_fence));

        builder.setView(dialogView);

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                BaiduActivity.mBaiduMap.setOnMapClickListener(null);
            }

        });

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {

                if (TextUtils.isDigitsOnly(fenceValue.getText())) {
                    if (0 == fenceType) {
                        radiusTemp = radius;
                        radius = Integer.parseInt(fenceValue.getText().toString()) > 0 ? Integer.parseInt(fenceValue
                                .getText().toString()) : radius;
                    } else if (1 == fenceType) {
                        vertexNumber = Integer.parseInt(fenceValue.getText().toString()) > 0 ? Integer
                                .parseInt(fenceValue.getText().toString()) : vertexNumber;
                    }
                }

                if (TextUtils.isDigitsOnly(fencePrecision.getText())) {
                    precision = Integer.parseInt(fencePrecision.getText().toString()) >= 0 ? Integer
                            .parseInt(fencePrecision.getText().toString()) : precision;
                }

                System.out.println("radius : " + radius);
                System.out.println("vertexNumber : " + vertexNumber);
                System.out.println("precision : " + precision);
                Toast.makeText(mContext, caption, Toast.LENGTH_LONG).show();
            }
        });
        builder.show();
    }

    /**
     * 设置围栏对话框
     */
    private void createOrUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        builder.setTitle("确定设置围栏?");

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                if (0 == fenceType) {
                    circleFenceOverlay.remove();
                    circleFenceOverlaysOption = null;
                    // 添加覆盖物
                    if (null != circleFenceOverlayOptionsTemp) {
                        circleFenceOverlaysOption = circleFenceOverlayOptionsTemp;
                    }
                    radius = radiusTemp;
                } else if (1 == fenceType) {
                    for (Overlay overlay : overlays) {
                        overlay.remove();
                    }
                    if (!vertexsTemp.isEmpty()) {
                        vertexs.clear();
                        vertexs.addAll(vertexsTemp);
                    }
                    vertexesFenceOverlay.remove();
                    vertexesFenceOverlayOption = null;
                    // 添加覆盖物
                    if (null != vertexesFenceOverlayOptionsTemp) {
                        vertexesFenceOverlayOption = vertexesFenceOverlayOptionsTemp;
                    }
                }
                addMarker();
                BaiduActivity.mBaiduMap.setOnMapClickListener(null);
            }
        });

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {

                for (Overlay overlay : overlays) {
                    overlay.remove();
                }

                vertexsTemp.clear();
                vertexsTemp.addAll(vertexs);

                if (0 == fenceId) {
                    // 创建围栏
                    if (0 == fenceType) {
                        createCircularFence();
                    } else if (1 == fenceType) {
                        createVertexesFence();
                    }
                } else {
                    // 更新围栏
                    if (0 == fenceType) {
                        updateCircularFence();
                    } else if (1 == fenceType) {
                        updateVertexesFence();
                    }
                }
                BaiduActivity.mBaiduMap.setOnMapClickListener(null);
            }
        });
        builder.show();
    }

    private void showMessage(String message) {
        Looper.prepare();
        Toast.makeText(BaiduActivity.mContext, message, Toast.LENGTH_LONG).show();
        Looper.loop();
    }

    /**
     * 添加地图覆盖物
     */
    protected static void addMarker() {
        try {

            // 圆形围栏覆盖物
            if (null != circleFenceOverlaysOption) {
                circleFenceOverlay = BaiduActivity.mBaiduMap.addOverlay(circleFenceOverlaysOption);
            }

            // 多边形围栏覆盖物
            if (null != vertexesFenceOverlayOption) {
                vertexesFenceOverlay = BaiduActivity.mBaiduMap.addOverlay(vertexesFenceOverlayOption);
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

}
