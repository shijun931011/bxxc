package com.jgkj.bxxc.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.CoordinateConverter;
import com.amap.api.location.DPoint;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.navisdk.adapter.BNCommonSettingParam;
import com.baidu.navisdk.adapter.BNOuterLogUtil;
import com.baidu.navisdk.adapter.BNOuterTTSPlayerCallback;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BNaviSettingManager;
import com.baidu.navisdk.adapter.BaiduNaviManager;
import com.jgkj.bxxc.R;
import com.jgkj.bxxc.bean.CoachInfo;
import com.jgkj.bxxc.tools.MyOrientationListener;
import com.jgkj.bxxc.tools.OpenLocalMapUtil;
import com.jgkj.bxxc.tools.SelectPopupWindow;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class BDMAPActivity extends Activity{
    // 定位相关
    public LocationClient mLocClient;
    public MyLocationConfiguration.LocationMode mCurrentMode;
    public BitmapDescriptor mCurrentMarker;
    private final MyLocationListenner myListener = new MyLocationListenner();
    public MapView mMapView;
    public BaiduMap mBaiduMap;
    //方向传感器
    private MyOrientationListener myOrientationListener;
    private int mXDirection;
    private double mCurrentLantitude, mCurrentLongitude;
    private float mCurrentAccracy;
    //popupWindow
    private SelectPopupWindow mPopupWindow = null;
    // UI相关
    boolean isFirstLoc = true; // 是否首次定位
    //Marker地图标签
    private LatLng point;
    //标题
    private TextView title;
    private Button button_backward;
    private ImageView btn_go_there;
    public static List<Activity> activityList = new LinkedList<Activity>();
    private static final String APP_FOLDER_NAME = "BNSDKSimpleDemo";
    //	private Button mWgsNaviBtn = null;
//	private Button mGcjNaviBtn = null;
//	private Button mBdmcNaviBtn = null;
    private Button mDb06ll = null;
    private String mSDCardPath = null;
    public static final String ROUTE_PLAN_NODE = "routePlanNode";
    public static final String SHOW_CUSTOM_ITEM = "showCustomItem";
    public static final String RESET_END_NODE = "resetEndNode";
    public static final String VOID_MODE = "voidMode";
    private static final String[] authBaseArr = { Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION };
    private static final String[] authComArr = { Manifest.permission.READ_PHONE_STATE };
    private static final int authBaseRequestCode = 1;
    private static final int authComRequestCode = 2;
    private boolean hasInitSuccess = false;
    private boolean hasRequestComAuth = false;
    private double latitudes;
    private double longitudes;
    private LocationClient mLocationClient;
    private BDLocationListener mBDLocationListener;
    private String faddress,school,address,res;
    private CoachInfo.Result result;
    private TextView route_title;
    private TextView route_address;
    private Dialog dialog;
    private View inflate;
    private TextView dialog_baidu,dialog_no,dialog_gaode,dialog_cancel;
    private boolean isOpened;
    private static String SRC = "thirdapp.navi.beiing.openlocalmapdemo";
    private String SNAME = "我的位置";
    private String DNAME = "已选择的位置";
    private String CITY = "合肥";
    private static String APP_NAME = "百信学车";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityList.add(this);
        setContentView(R.layout.baidumap);
        title = (TextView) findViewById(R.id.text_title);
        title.setText("驾校地址");
        button_backward = (Button) findViewById(R.id.button_backward);
        btn_go_there = (ImageView) findViewById(R.id.btn_go_there);
        route_title = (TextView) findViewById(R.id.route_title);
        route_address = (TextView) findViewById(R.id.route_address);
        button_backward.setVisibility(View.VISIBLE);
        button_backward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getIntentData();
        //初始化控件
        initMap();
        BNOuterLogUtil.setLogSwitcher(true);
        if (initDirs()) {
            initNavi();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isOpened = false;
    }

    /**
     * 打开百度地图
     * @param
     */
    private void openBaiduMap(double slat, double slon, String sname, double dlat, double dlon, String dname, String city){
        if(OpenLocalMapUtil.isBaiduMapInstalled()){
            try{
                String uri = OpenLocalMapUtil.getBaiduMapUri(String.valueOf(slat), String.valueOf(slon), sname,
                        String.valueOf(dlat), String.valueOf(dlon), dname, city, SRC);
                Intent intent = Intent.parseUri(uri, 0);
                startActivity(intent); //启动调用
                isOpened = true;
            } catch (Exception e) {
                isOpened = false;
                e.printStackTrace();
            }
        }else{
//            Toast.makeText(BDMAPActivity.this,"抱歉，您暂未安装百度地图客户端，请前去安装！",Toast.LENGTH_SHORT).show();
            isOpened = false;
        }
    }



    /**
     *打开高德地图
     * @param
     */
    private void openGaoDeMap(double slat, double slon, String sname, double dlat, double dlon, String dname){
        if(OpenLocalMapUtil.isGdMapInstalled()){
            try {
                CoordinateConverter converter= new CoordinateConverter(this);
                converter.from(CoordinateConverter.CoordType.BAIDU);
                DPoint sPoint = null, dPoint = null;
                try {
                    converter.coord(new DPoint(slat, slon));
                    sPoint = converter.convert();
                    converter.coord(new DPoint(dlat, dlon));
                    dPoint = converter.convert();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (sPoint != null && dPoint != null) {
                    String uri = OpenLocalMapUtil.getGdMapUri(APP_NAME, String.valueOf(sPoint.getLatitude()), String.valueOf(sPoint.getLongitude()),
                            sname, String.valueOf(dPoint.getLatitude()), String.valueOf(dPoint.getLongitude()), dname);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setPackage("com.autonavi.minimap");
                    intent.setData(Uri.parse(uri));
                    startActivity(intent); //启动调用
                    isOpened = true;
                }
            } catch (Exception e) {
                isOpened = false;
                e.printStackTrace();
            }
        } else{
//            Toast.makeText(BDMAPActivity.this,"抱歉，您暂未安装高德地图客户端，请前去安装！",Toast.LENGTH_SHORT).show();
            isOpened = false;
        }
    }


    public void openLocalMap(View view) {
        openLocalMap(mCurrentLantitude, mCurrentLongitude, SNAME,  CITY);
    }

    /**
     *
     * @param slat
     * @param slon
     * @param address 当前位置
     * @param city 所在城市
     */
    private void openLocalMap(double slat, double slon, String address, String city) {
        chooseOpenMap(slat, slon, address, city);
    }

    /**
     * 如果两个地图都安装，提示选择
     * @param slat
     * @param slon
     * @param address
     * @param city
     */
    private void chooseOpenMap(final double slat, final double slon, final String address, final String city) {
        dialog = new Dialog(this, R.style.ActionSheetDialogStyle);
        // 填充对话框的布局
        inflate = LayoutInflater.from(this).inflate(R.layout.sure_choose_dialog, null);
        //控件
        dialog_baidu = (TextView) inflate.findViewById(R.id.dialog_yes);
        dialog_baidu.setText("百度地图");
        dialog_gaode = (TextView) inflate.findViewById(R.id.dialog_no);
        dialog_gaode.setText("高德地图");
        dialog_cancel = (TextView) inflate.findViewById(R.id.dialog_cancel);
        dialog_baidu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                openBaiduMap(slat, slon, address, latitudes, longitudes, DNAME, city);
                if (!OpenLocalMapUtil.isBaiduMapInstalled()){
                    Toast.makeText(BDMAPActivity.this,"抱歉，您暂未安装百度地图客户端，请前去安装！",Toast.LENGTH_SHORT).show();
                }else{
                    openBaiduMap(slat, slon, address,latitudes, longitudes, DNAME, city);
                    dialog.dismiss();
                }

            }
        });
        dialog_gaode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                openGaoDeMap(slat, slon, address,latitudes, longitudes, DNAME);
                if(!OpenLocalMapUtil.isGdMapInstalled()){
                    Toast.makeText(BDMAPActivity.this,"抱歉，您暂未安装高德地图客户端，请前去安装！",Toast.LENGTH_SHORT).show();
                }else{
                    openGaoDeMap(slat, slon, address, latitudes, longitudes, DNAME);
                    dialog.dismiss();
                }

            }
        });
        dialog_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        // 将布局设置给Dialog
        dialog.setContentView(inflate);
        // 获取当前Activity所在的窗体
        Window dialogWindow = dialog.getWindow();
        // 设置dialog宽度
        dialogWindow.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置Dialog从窗体中间弹出
        dialogWindow.setGravity(Gravity.BOTTOM);
        dialog.show();// 显示对话框
    }

    private void getIntentData(){
        Intent intent = getIntent();
//        coach = intent.getStringExtra("coachInfo");
//        Gson gson = new Gson();
//        CoachInfo coachInfo = gson.fromJson(coach, CoachInfo.class);
//        List<CoachInfo.Result> list = coachInfo.getResult();
//        result = list.get(0);
//        route_title.setText("百信学车 · " + result.getFaddress());
//        route_address.setText(result.getAddress());
        faddress = intent.getStringExtra("faddress");
        school = intent.getStringExtra("school");
        address = intent.getStringExtra("address");
        route_address.setText(address);
        res = intent.getStringExtra("value");
        if (res.equals("1")){
            route_title.setText("百信学车 · " + faddress);
        }else if (res.equals("2")){
            route_title.setText("百信学车 · " +  school);
        }


    }

    /**
     * 初始化地图
     */
    private void initMap() {
        // 地图初始化
        mMapView = (MapView) findViewById(R.id.placeMap);
        mBaiduMap = mMapView.getMap();
//        // 开启定位图层
//        mBaiduMap.setMyLocationEnabled(true);
        // 定位初始化
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(myListener);
        myOrientationListener = new MyOrientationListener(getApplicationContext());
        myOrientationListener.setOnOrientationListener(new MyOrientationListener.OnOrientationListener() {
            @Override
            public void onOrientationChanged(float x) {
                mXDirection = (int) x;
                // 构造定位数据
                MyLocationData locData = new MyLocationData.Builder()
                        .accuracy(mCurrentAccracy)
                        // 此处设置开发者获取到的方向信息，顺时针0-360
                        .direction(mXDirection)
                        .latitude(mCurrentLantitude)
                        .longitude(mCurrentLongitude).build();
                // 设置定位数据
                mBaiduMap.setMyLocationData(locData);
                // 设置自定义图标
                mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
                        mCurrentMode, true, mCurrentMarker));
            }
        });
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);// 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        mLocClient.setLocOption(option);
//        mCurrentMode = MyLocationConfiguration.LocationMode.FOLLOWING;
//        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(new MapStatus.Builder().zoom(15).build()));
//        mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(mCurrentMode, true, mCurrentMarker));
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        if (!mLocClient.isStarted()) {
            mLocClient.start();
        }
        // 开启方向传感器
        myOrientationListener.start();
    }

    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {
        @Override
        public void onReceiveLocation(final BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }
            // 构造定位数据
            if (isFirstLoc) {
                MyLocationData locData = new MyLocationData.Builder()
                        .accuracy(location.getRadius())
                        // 此处设置开发者获取到的方向信息，顺时针0-360
                        .direction(mXDirection).latitude(location.getLatitude())
                        .longitude(location.getLongitude()).build();
                mCurrentAccracy = location.getRadius();
                mBaiduMap.setMyLocationData(locData);
                mCurrentLantitude = location.getLatitude();
                mCurrentLongitude = location.getLongitude();
                allMarker(latitudes,longitudes);
                isFirstLoc = false;
            }
            //allMarker(31.830394,117.208494);
            mCurrentLantitude = location.getLatitude();
            mCurrentLongitude = location.getLongitude();
        }
    }

    /**
     * 全城的marker标记
     */
    private void allMarker( double latitude,double longitude){
        //设置指定定位坐标
        point = new LatLng(latitude, longitude);
        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.yaogan);
        OverlayOptions options = new MarkerOptions().icon(icon).position(point);
        mBaiduMap.addOverlay(options);
        //设定中心点坐标
        //LatLng cenpt = new LatLng(30.663791,104.07281);
        //定义地图状态
        MapStatus mMapStatus = new MapStatus.Builder().target(point).zoom(15).build();
        //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        mBaiduMap.setMapStatus(mMapStatusUpdate);
        //改变地图状态
        mBaiduMap.animateMapStatus(mMapStatusUpdate,500);

    }

    @Override
    protected void onStart() {
        Intent it = getIntent();
        latitudes = it.getDoubleExtra("lantitude",1.1);
        longitudes = it.getDoubleExtra("longitude",1.1);
        super.onStart();
    }

    @Override
    public void onDestroy() {
        // 退出时销毁定位
        mLocClient.stop();
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        // 取消监听函数
        if (mLocationClient != null) {
            mLocationClient.unRegisterLocationListener(mBDLocationListener);
        }
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private boolean initDirs() {
        mSDCardPath = getSdcardDir();
        if (mSDCardPath == null) {
            return false;
        }
        File f = new File(mSDCardPath, APP_FOLDER_NAME);
        if (!f.exists()) {
            try {
                f.mkdir();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    String authinfo = null;

    /**
     * 内部TTS播报状态回传handler
     */
    private Handler ttsHandler = new Handler() {
        public void handleMessage(Message msg) {
            int type = msg.what;
            switch (type) {
                case BaiduNaviManager.TTSPlayMsgType.PLAY_START_MSG: {
                    // showToastMsg("Handler : TTS play start");
                    break;
                }
                case BaiduNaviManager.TTSPlayMsgType.PLAY_END_MSG: {
                    // showToastMsg("Handler : TTS play end");
                    break;
                }
                default:
                    break;
            }
        }
    };

    /**
     * 内部TTS播报状态回调接口
     */
    private BaiduNaviManager.TTSPlayStateListener ttsPlayStateListener = new BaiduNaviManager.TTSPlayStateListener() {

        @Override
        public void playEnd() {
            // showToastMsg("TTSPlayStateListener : TTS play end");
        }

        @Override
        public void playStart() {
            // showToastMsg("TTSPlayStateListener : TTS play start");
        }
    };

    public void showToastMsg(final String msg) {
        BDMAPActivity.this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(BDMAPActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean hasBasePhoneAuth() {
        // TODO Auto-generated method stub

        PackageManager pm = this.getPackageManager();
        for (String auth : authBaseArr) {
            if (pm.checkPermission(auth, this.getPackageName()) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private boolean hasCompletePhoneAuth() {
        // TODO Auto-generated method stub

        PackageManager pm = this.getPackageManager();
        for (String auth : authComArr) {
            if (pm.checkPermission(auth, this.getPackageName()) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void initNavi() {

        BNOuterTTSPlayerCallback ttsCallback = null;

        // 申请权限
        if (android.os.Build.VERSION.SDK_INT >= 23) {

            if (!hasBasePhoneAuth()) {
                this.requestPermissions(authBaseArr, authBaseRequestCode);
                return;
            }
        }

        BaiduNaviManager.getInstance().init(this, mSDCardPath, APP_FOLDER_NAME, new BaiduNaviManager.NaviInitListener() {
            @Override
            public void onAuthResult(int status, String msg) {
                if (0 == status) {
                    authinfo = "key校验成功!";
                } else {
                    authinfo = "key校验失败, " + msg;
                }
                BDMAPActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        if(!"key校验成功!".equals(authinfo)){
                            Toast.makeText(BDMAPActivity.this, authinfo, Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }

            public void initSuccess() {
                //Toast.makeText(ReservationForPrivateActivity.this, "百度导航引擎初始化成功", Toast.LENGTH_SHORT).show();
                hasInitSuccess = true;
                initSetting();
            }

            public void initStart() {
                //Toast.makeText(ReservationForPrivateActivity.this, "百度导航引擎初始化开始", Toast.LENGTH_SHORT).show();
            }

            public void initFailed() {
                Toast.makeText(BDMAPActivity.this, "百度导航引擎初始化失败", Toast.LENGTH_SHORT).show();
            }

        }, null, ttsHandler, ttsPlayStateListener);

    }

    private String getSdcardDir() {
        if (Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().toString();
        }
        return null;
    }

    private BNRoutePlanNode.CoordinateType mCoordinateType = null;

    private void routeplanToNavi(BNRoutePlanNode.CoordinateType coType) {
        mCoordinateType = coType;
        if (!hasInitSuccess) {
            Toast.makeText(BDMAPActivity.this, "还未初始化!", Toast.LENGTH_SHORT).show();
        }
        // 权限申请
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            // 保证导航功能完备
            if (!hasCompletePhoneAuth()) {
                if (!hasRequestComAuth) {
                    hasRequestComAuth = true;
                    this.requestPermissions(authComArr, authComRequestCode);
                    return;
                } else {
                    Toast.makeText(BDMAPActivity.this, "没有完备的权限!", Toast.LENGTH_SHORT).show();
                }
            }

        }
        BNRoutePlanNode sNode = null;
        BNRoutePlanNode eNode = null;
        switch (coType) {
//            case GCJ02: {
//                sNode = new BNRoutePlanNode(116.30142, 40.05087, "百度大厦", null, coType);
//                eNode = new BNRoutePlanNode(116.39750, 39.90882, "北京天安门", null, coType);
//                break;
//            }
//            case WGS84: {
//                sNode = new BNRoutePlanNode(116.300821, 40.050969, "百度大厦", null, coType);
//                eNode = new BNRoutePlanNode(116.397491, 39.908749, "北京天安门", null, coType);
//                break;
//            }
//            case BD09_MC: {
//                sNode = new BNRoutePlanNode(12947471, 4846474, "百度大厦", null, coType);
//                eNode = new BNRoutePlanNode(12958160, 4825947, "北京天安门", null, coType);
//                break;
//            }
            case BD09LL: {
                sNode = new BNRoutePlanNode(mCurrentLongitude, mCurrentLantitude, "起点", null, coType);
                eNode = new BNRoutePlanNode(longitudes, latitudes, "终点", null, coType);
                break;
            }
            default:
                ;
        }
        if (sNode != null && eNode != null) {
            List<BNRoutePlanNode> list = new ArrayList<BNRoutePlanNode>();
            list.add(sNode);
            list.add(eNode);
            BaiduNaviManager.getInstance().launchNavigator(this, list, 1, true, new BDMAPActivity.DemoRoutePlanListener(sNode));
        }
    }

    public class DemoRoutePlanListener implements BaiduNaviManager.RoutePlanListener {

        private BNRoutePlanNode mBNRoutePlanNode = null;

        public DemoRoutePlanListener(BNRoutePlanNode node) {
            mBNRoutePlanNode = node;
        }

        @Override
        public void onJumpToNavigator() {
            /*
             * 设置途径点以及resetEndNode会回调该接口
             */

            for (Activity ac : activityList) {

                if (ac.getClass().getName().endsWith("BNDemoGuideActivity")) {

                    return;
                }
            }
            Intent intent = new Intent(BDMAPActivity.this, BNDemoGuideActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(ROUTE_PLAN_NODE, (BNRoutePlanNode) mBNRoutePlanNode);
            intent.putExtras(bundle);
            startActivity(intent);

        }

        @Override
        public void onRoutePlanFailed() {
            // TODO Auto-generated method stub
            Toast.makeText(BDMAPActivity.this, "算路失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void initSetting() {
        // BNaviSettingManager.setDayNightMode(BNaviSettingManager.DayNightMode.DAY_NIGHT_MODE_DAY);
        BNaviSettingManager
                .setShowTotalRoadConditionBar(BNaviSettingManager.PreViewRoadCondition.ROAD_CONDITION_BAR_SHOW_ON);
        BNaviSettingManager.setVoiceMode(BNaviSettingManager.VoiceMode.Veteran);
        // BNaviSettingManager.setPowerSaveMode(BNaviSettingManager.PowerSaveMode.DISABLE_MODE);
        BNaviSettingManager.setRealRoadCondition(BNaviSettingManager.RealRoadCondition.NAVI_ITS_ON);
        Bundle bundle = new Bundle();
        // 必须设置APPID，否则会静音
        bundle.putString(BNCommonSettingParam.TTS_APP_ID, "9829331");
        BNaviSettingManager.setNaviSdkParam(bundle);
    }

    private BNOuterTTSPlayerCallback mTTSCallback = new BNOuterTTSPlayerCallback() {

        @Override
        public void stopTTS() {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "stopTTS");
        }

        @Override
        public void resumeTTS() {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "resumeTTS");
        }

        @Override
        public void releaseTTSPlayer() {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "releaseTTSPlayer");
        }

        @Override
        public int playTTSText(String speech, int bPreempt) {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "playTTSText" + "_" + speech + "_" + bPreempt);

            return 1;
        }

        @Override
        public void phoneHangUp() {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "phoneHangUp");
        }

        @Override
        public void phoneCalling() {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "phoneCalling");
        }

        @Override
        public void pauseTTS() {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "pauseTTS");
        }

        @Override
        public void initTTSPlayer() {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "initTTSPlayer");
        }

        @Override
        public int getTTSState() {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "getTTSState");
            return 1;
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // TODO Auto-generated method stub
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == authBaseRequestCode) {
            for (int ret : grantResults) {
                if (ret == 0) {
                    continue;
                } else {
                    Toast.makeText(BDMAPActivity.this, "缺少导航基本的权限!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            initNavi();
        } else if (requestCode == authComRequestCode) {
            for (int ret : grantResults) {
                if (ret == 0) {
                    continue;
                }
            }
            routeplanToNavi(mCoordinateType);
        }

    }

}
