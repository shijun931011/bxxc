package com.jgkj.bxxc.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.jgkj.bxxc.R;
import com.jgkj.bxxc.adapter.CoachFullDetailAdapter;
import com.jgkj.bxxc.bean.CoachInfo;
import com.jgkj.bxxc.bean.SchoolPlaceTotal;
import com.jgkj.bxxc.bean.UserInfo;
import com.jgkj.bxxc.bean.entity.CommentEntity.CommentEntity;
import com.jgkj.bxxc.bean.entity.CommentEntity.CommentResult;
import com.jgkj.bxxc.tools.CallDialog;
import com.jgkj.bxxc.tools.MyOrientationListener;
import com.jgkj.bxxc.tools.RefreshLayout;
import com.jgkj.bxxc.tools.StatusBarCompat;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import okhttp3.Call;

/**
 * Created by fangzhou on 2016/10/29.
 * 教练个人简介和显示部分学员评价，
 * 可以进行，对教练的收藏和分享
 */
public class  ReservationActivity extends Activity implements OnClickListener, SwipeRefreshLayout.OnRefreshListener,
        RefreshLayout.OnLoadListener {
    private CoachFullDetailAdapter adapter;
    private ListView listView;
    private TextView text_title, call;
    private Button back;
    private TextView coach_name;
    //教练评价星星
    private LinearLayout star;
    private TextView place;
    //上拉刷新
    private RefreshLayout swipeLayout;
    private View headView;
    //接受数据
    private String coachId;
    /**
     * ListView的加载中footer
     */
    private View mListViewFooter;
    UMImage image;
    String url = "http://www.baixinxueche.com/index.php/Home/Info/indexs.html?pid=";
    private int commentPage = 0;
    private TextView signup_Coach;
    private ImageView coach_head, share;
    private TextView costsThat;
    private TextView marketPrise, chexing, myclass;
    //创建费用说明dialog
    private Dialog dialog;
    private ProgressDialog progressDialog;
    private View dialogView;
    private LinearLayout fourPromise;
    //教练信息
    private TextView price, currentStu, tongguo, totalStu,coach_address;
    private LinearLayout xinyong, zhiliang, fuwu;
    private TextView zhiliangfen, fuwufen;
    private LinearLayout.LayoutParams wrapParams;
    private String state;
    private int uid;
    private int educationType;
    private String token;
    private SharedPreferences sp;
    private UserInfo userInfo;
    private List<CommentEntity> listStu;
    private TextView connectCus,haopinglv;
    //url
    private String coachUrl = "http://www.baixinxueche.com/index.php/Home/Apitokenpt/CoachinfoAgain";
    private String comment = "http://www.baixinxueche.com/index.php/Home/Api/comment";
    private String changeUrl = "http://www.baixinxueche.com/index.php/Home/Apitokenupdata/subjectTwoCoachConfirm";
    private String commentUrl = "http://www.baixinxueche.com/index.php/Home/Apitoken/commentMore";
    //地图
    public MapView mMapView;
    //定位
    public LocationClient mLocClient;
    public MyLocationConfiguration.LocationMode mCurrentMode;
    public BitmapDescriptor mCurrentMarker;
    //方向传感器
    private MyOrientationListener myOrientationListener;
    private int mXDirection;
    private double mCurrentLantitude, mCurrentLongitude;
    private float mCurrentAccracy;
    private String address= "";
    private BaiduMap mBaiduMap;
    private BitmapDescriptor bitmap;
    private SchoolPlaceTotal schoolPlaceTotal;
    //Marker地图标签
    private LatLng point;
    //private final  MyLocationListenner myListener = new MyLocationListenner();
    boolean isFirstLoc = true; // 是否首次定位
    private InfoWindow mInfoWindow;
    private BitmapDescriptor  bitmapA;
    private String[] city = new String[0];
    private Marker mMarker;
    private class Result {
        private int code;
        private String reason;
        public String getReason() {
            return reason;
        }
        public int getCode() {
            return code;
        }
    }
    private TextView noSmsData;
    private LinearLayout linear_list_noData;
    private CoachInfo.Result result;
    private boolean falg = false;

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

    private String latitude;
    private String longitude;

    private double latitudes;
    private double longitudes;

    private LocationClient mLocationClient;
    private BDLocationListener mBDLocationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 注意该方法要再setContentView方法之前实现
        //SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.reservation);
        StatusBarCompat.compat(this, Color.parseColor("#37363C"));
        progressDialog = ProgressDialog.show(ReservationActivity.this, "加载中...", "请等待...", true, false);
        headView = getLayoutInflater().inflate(R.layout.coach_head, null);
        init();
        getData(coachId, coachUrl);
        bitmapA = BitmapDescriptorFactory.fromResource(R.drawable.a2);
    }
    /**
     * 初始化控件
     */
    private void init() {
        listStu = new ArrayList<CommentEntity>();
        connectCus = (TextView) findViewById(R.id.connectCus);
        connectCus.setOnClickListener(this);
        signup_Coach = (TextView) findViewById(R.id.signup_Coach);
        signup_Coach.setOnClickListener(this);
        xinyong = (LinearLayout) headView.findViewById(R.id.xinyong);
        zhiliang = (LinearLayout) headView.findViewById(R.id.zhiliang);
        fuwu = (LinearLayout) headView.findViewById(R.id.fuwu);
        zhiliangfen = (TextView) headView.findViewById(R.id.zhiliangfen);
        fuwufen = (TextView) headView.findViewById(R.id.fuwufen);
        Intent intent = getIntent();
        uid = intent.getIntExtra("uid",uid);
        educationType = intent.getIntExtra("educationType",educationType);
        coachId = intent.getStringExtra("coachId");
        token = intent.getStringExtra("token");
        int isChange = intent.getIntExtra("isChange", -1);
        if (isChange == 0) {
            signup_Coach.setText("更改教练");
        } else {
            signup_Coach.setText("立即报名");
        }
        chexing = (TextView) headView.findViewById(R.id.chexing);
        myclass = (TextView) headView.findViewById(R.id.myclass);
        coach_head = (ImageView) headView.findViewById(R.id.coach_head);
        place = (TextView) headView.findViewById(R.id.place);
        coach_name = (TextView) headView.findViewById(R.id.coach_name);
        marketPrise = (TextView) headView.findViewById(R.id.marketPrise);
        marketPrise.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        price = (TextView) headView.findViewById(R.id.price);
        currentStu = (TextView) headView.findViewById(R.id.currentStu);
        tongguo = (TextView) headView.findViewById(R.id.tongguo);
        totalStu = (TextView) headView.findViewById(R.id.totalStu);
        haopinglv = (TextView) headView.findViewById(R.id.haopinglv);
        share = (ImageView) headView.findViewById(R.id.share);
        linear_list_noData = (LinearLayout)headView.findViewById(R.id.linear_list_noData);
        coach_address = (TextView) headView.findViewById(R.id.coach_address);
        share.setOnClickListener(this);
        //费用说明
        costsThat = (TextView) headView.findViewById(R.id.costsThat);
        costsThat.setOnClickListener(this);
        // 实例化控件
        text_title = (TextView) findViewById(R.id.text_title);
        text_title.setText("教练详情");
        back = (Button) findViewById(R.id.button_backward);
        back.setOnClickListener(this);
        back.setVisibility(View.VISIBLE);
        fourPromise = (LinearLayout) headView.findViewById(R.id.fourPromiselinearLayout);
        fourPromise.setOnClickListener(this);
        // 实例化listView显示学员的评价
        listView = (ListView) findViewById(R.id.student_evaluate_listView);
        listView.setFocusable(false);
        listView.addHeaderView(headView, null, false);
        //上拉刷新
        swipeLayout = (RefreshLayout) findViewById(R.id.swipe_container);
        swipeLayout.setColorSchemeResources(R.color.color_bule2, R.color.color_bule, R.color.color_bule2, R.color.color_bule3);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setOnLoadListener(this);
    }
    /**
     * 初始化地图
     */
    private void initMap(final String lantitude, final String longitude){
        // 地图初始化
        mMapView = (MapView) findViewById(R.id.coach_map);
        mBaiduMap = mMapView.getMap();
        //设置是否显示比例尺控件
        mMapView.showScaleControl(false);
        //设置是否显示缩放控件
        mMapView.showZoomControls(false);
        //设置指定定位坐标
        point = new LatLng(Double.parseDouble(lantitude), Double.parseDouble(longitude));
        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.yaogan);
        OverlayOptions options = new MarkerOptions().icon(icon).position(point);
        mBaiduMap.addOverlay(options);
        //定义地图状态
        MapStatus mMapStatus = new MapStatus.Builder()
                .target(point)
                .zoom(16)
                .build();
        //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        //改变地图状态
        mBaiduMap.setMapStatus(mMapStatusUpdate);
        mBaiduMap.setOnMarkerClickListener(new ReservationActivity.markerClickListener());
        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Intent intent = new Intent();
                intent.setClass(ReservationActivity.this,BDMAPActivity.class);
                intent.putExtra("lantitude",Double.parseDouble(latitude));
                intent.putExtra("longitude",Double.parseDouble(longitude));
                intent.putExtra("coachInfo", signup_Coach.getTag().toString());
                startActivity(intent);
            }
            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });
    }

    /**
     * 全城的marker标记
     */
    private void allMarker(){
        List<SchoolPlaceTotal.Result.Res> listSch = schoolPlaceTotal.getResult().get(0).getResult();
        for (int j = 0; j < listSch.size(); j++) {
            double latitude = Double.parseDouble(listSch.get(j).getLatitude());
            double longitude = Double.parseDouble(listSch.get(j).getLongitude());
            point = new LatLng(latitude, longitude);
            OverlayOptions option = null;
            switch (j) {
                case 0:
                    option = new MarkerOptions().position(point).zIndex(j).icon(
                            bitmapA);
                    break;
            }
            mMarker = (Marker) mBaiduMap.addOverlay(option);
            MapStatus.Builder builder = new MapStatus.Builder();
            builder.target(point).zoom(17.0f);
            mBaiduMap.animateMapStatus(MapStatusUpdateFactory
                    .newMapStatus(builder.build()));
            mBaiduMap.setOnMarkerClickListener(new markerClickListener());
        }
    }

    /**
     * 设置popWindow
     * @param results
     */
    private void setPup(List<SchoolPlaceTotal.Result> results) {
        city = new String[results.size()];
        for (int i = 0; i < results.size(); i++) {
            city[i] = results.get(i).getSchool_aera();
        }
        allMarker();
    }

    /**
     * marker点击事件处理
     */
    private class markerClickListener implements BaiduMap.OnMarkerClickListener {
        @Override
        public boolean onMarkerClick(final Marker marker) {
            return true;
        }
    }

    /**
     * 根据cid(教练id)获取教练信息
     *
     * @param coachId 教练信息
     * @param url     请求地址
     */
    private void getData(String coachId, String url) {
        Log.i("百信学车","教练cid=" + coachId + "   url=" + url);
        OkHttpUtils
                .post()
                .url(url)
                .addParams("cid", coachId)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        Toast.makeText(ReservationActivity.this, "加载失败", Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onResponse(String s, int i) {
                        signup_Coach.setTag(s);
                        Gson gson = new Gson();
                        CoachInfo coachInfo = gson.fromJson(s, CoachInfo.class);
                        Log.i("百信学车","预约教练信息结果" + s);
                        if (coachInfo.getCode() == 200) {
                            List<CoachInfo.Result> list = coachInfo.getResult();
                            result = list.get(0);
                            coach_name.setText(result.getCoachname());
                            place.setHint(result.getFaddress());
                            DecimalFormat df = new DecimalFormat("#.00");
                            marketPrise.setHint("￥" + df.format(result.getMarket_price()));
                            price.setText("￥" + df.format(result.getPrice()));
                            currentStu.setHint("当前学员数" + result.getStunum() + "人");
                            tongguo.setHint("通过率：" + result.getTguo() + "%");
                            coach_address.setText(result.getAddress());
                            String path = result.getFile();
                            if (!path.endsWith(".jpg") && !path.endsWith(".jpeg") && !path.endsWith(".png") &&
                                    !path.endsWith(".GIF") && !path.endsWith(".PNG") && !path.endsWith(".JPG") && !path.endsWith(".gif")) {
                                coach_head.setImageResource(R.drawable.coach_pic);
                            } else {
                                Glide.with(ReservationActivity.this).load(result.getFile()).placeholder(R.drawable.coach_pic).error(R.drawable.coach_pic).into(coach_head);
                            }
                            totalStu.setHint("累计学员数" + result.getCount_stu() + "人");
                            chexing.setHint("车型：" + result.getChexing());
                            myclass.setHint("班型：" + result.getClass_type());
                            coach_head.setTag(result.getFile());
                            share.setTag(result.getCid());
                            int credit = result.getCredit();
                            Double teachnum = Double.parseDouble(result.getTeach());
                            Double waitnum = Double.parseDouble(result.getWait());
                            haopinglv.setText("好评率："+result.getHaopin()+"%");
                            zhiliang.removeAllViews();
                            xinyong.removeAllViews();
                            fuwu.removeAllViews();
                            for (int k = 0; k < credit; k++) {
                                ImageView image = new ImageView(ReservationActivity.this);
                                image.setBackgroundResource(R.drawable.xin_1);
                                LinearLayout.LayoutParams wrapParams = new LinearLayout.LayoutParams(30, 30);
                                image.setLayoutParams(wrapParams);
                                xinyong.addView(image);
                            }
                            if (teachnum < 1){
                                ImageView image = new ImageView(ReservationActivity.this);
                                image.setBackgroundResource(R.drawable.star0);
                                wrapParams = new LinearLayout.LayoutParams(150,30);
                                image.setLayoutParams(wrapParams);
                                zhiliang.addView(image);
                            }
                            if (teachnum == 1){
                                ImageView image = new ImageView(ReservationActivity.this);
                                image.setBackgroundResource(R.drawable.star1);
                                wrapParams = new LinearLayout.LayoutParams(30,30);
                                image.setLayoutParams(wrapParams);
                                zhiliang.addView(image);
                            }
                            if (teachnum > 1  && teachnum < 2){
                                ImageView image = new ImageView(ReservationActivity.this);
                                image.setBackgroundResource(R.drawable.star2);
                                wrapParams = new LinearLayout.LayoutParams(150,30);
                                image.setLayoutParams(wrapParams);
                                zhiliang.addView(image);
                            }
                            if (teachnum == 2){
                                for (double k = 0; k < 2; k++) {
                                    ImageView image = new ImageView(ReservationActivity.this);
                                    image.setBackgroundResource(R.drawable.star1);
                                    wrapParams = new LinearLayout.LayoutParams(30, 30);
                                    image.setLayoutParams(wrapParams);
                                    zhiliang.addView(image);
                                }
                            }
                            if (teachnum > 2  && teachnum < 3){
                                ImageView image = new ImageView(ReservationActivity.this);
                                image.setBackgroundResource(R.drawable.star3);
                                wrapParams = new LinearLayout.LayoutParams(150,30);
                                image.setLayoutParams(wrapParams);
                                zhiliang.addView(image);
                            }
                            if (teachnum == 3){
                                for (double k = 0; k < 3; k++) {
                                    ImageView image = new ImageView(ReservationActivity.this);
                                    image.setBackgroundResource(R.drawable.star1);
                                    wrapParams = new LinearLayout.LayoutParams(30, 30);
                                    image.setLayoutParams(wrapParams);
                                    zhiliang.addView(image);
                                }
                            }
                            if (teachnum > 3  && teachnum < 4){
                                ImageView image = new ImageView(ReservationActivity.this);
                                image.setBackgroundResource(R.drawable.star4);
                                wrapParams = new LinearLayout.LayoutParams(150,30);
                                image.setLayoutParams(wrapParams);
                                zhiliang.addView(image);
                            }
                            if (teachnum == 4){
                                for (double k = 0; k < 4; k++) {
                                    ImageView image = new ImageView(ReservationActivity.this);
                                    image.setBackgroundResource(R.drawable.star1);
                                    wrapParams = new LinearLayout.LayoutParams(30, 30);
                                    image.setLayoutParams(wrapParams);
                                    zhiliang.addView(image);
                                }
                            }
                            if (teachnum > 4  && teachnum < 5){
                                ImageView image = new ImageView(ReservationActivity.this);
                                image.setBackgroundResource(R.drawable.star5);
                                wrapParams = new LinearLayout.LayoutParams(150,30);
                                image.setLayoutParams(wrapParams);
                                zhiliang.addView(image);
                            }
                            if (teachnum == 5){
                                for (double k = 0; k < 5; k++) {
                                    ImageView image = new ImageView(ReservationActivity.this);
                                    image.setBackgroundResource(R.drawable.star1);
                                    wrapParams = new LinearLayout.LayoutParams(30, 30);
                                    image.setLayoutParams(wrapParams);
                                    zhiliang.addView(image);
                                }
                            }

                            if (waitnum < 1){
                                ImageView image = new ImageView(ReservationActivity.this);
                                image.setBackgroundResource(R.drawable.star0);
                                wrapParams = new LinearLayout.LayoutParams(150,30);
                                image.setLayoutParams(wrapParams);
                                fuwu.addView(image);
                            }
                            if (waitnum == 1){
                                ImageView image = new ImageView(ReservationActivity.this);
                                image.setBackgroundResource(R.drawable.star1);
                                wrapParams = new LinearLayout.LayoutParams(30,30);
                                image.setLayoutParams(wrapParams);
                                fuwu.addView(image);
                            }
                            if (waitnum > 1  && waitnum < 2){
                                ImageView image = new ImageView(ReservationActivity.this);
                                image.setBackgroundResource(R.drawable.star2);
                                wrapParams = new LinearLayout.LayoutParams(150,30);
                                image.setLayoutParams(wrapParams);
                                fuwu.addView(image);
                            }
                            if (waitnum == 2){
                                for (double k = 0; k < 2; k++) {
                                    ImageView image = new ImageView(ReservationActivity.this);
                                    image.setBackgroundResource(R.drawable.star1);
                                    wrapParams = new LinearLayout.LayoutParams(30, 30);
                                    image.setLayoutParams(wrapParams);
                                    fuwu.addView(image);
                                }
                            }
                            if (waitnum > 2  && waitnum < 3){
                                ImageView image = new ImageView(ReservationActivity.this);
                                image.setBackgroundResource(R.drawable.star3);
                                wrapParams = new LinearLayout.LayoutParams(150,30);
                                image.setLayoutParams(wrapParams);
                                fuwu.addView(image);
                            }
                            if (waitnum == 3){
                                for (double k = 0; k < 3; k++) {
                                    ImageView image = new ImageView(ReservationActivity.this);
                                    image.setBackgroundResource(R.drawable.star1);
                                    wrapParams = new LinearLayout.LayoutParams(30, 30);
                                    image.setLayoutParams(wrapParams);
                                    fuwu.addView(image);
                                }
                            }
                            if (waitnum > 3  && waitnum < 4){
                                ImageView image = new ImageView(ReservationActivity.this);
                                image.setBackgroundResource(R.drawable.star4);
                                wrapParams = new LinearLayout.LayoutParams(150,30);
                                image.setLayoutParams(wrapParams);
                                fuwu.addView(image);
                            }
                            if (waitnum == 4){
                                for (double k = 0; k < 4; k++) {
                                    ImageView image = new ImageView(ReservationActivity.this);
                                    image.setBackgroundResource(R.drawable.star1);
                                    wrapParams = new LinearLayout.LayoutParams(30, 30);
                                    image.setLayoutParams(wrapParams);
                                    fuwu.addView(image);
                                }
                            }
                            if (waitnum > 4  && waitnum < 5){
                                ImageView image = new ImageView(ReservationActivity.this);
                                image.setBackgroundResource(R.drawable.star5);
                                wrapParams = new LinearLayout.LayoutParams(150,30);
                                image.setLayoutParams(wrapParams);
                                fuwu.addView(image);
                            }
                            if (waitnum == 5){
                                for (double k = 0; k < 5; k++) {
                                    ImageView image = new ImageView(ReservationActivity.this);
                                    image.setBackgroundResource(R.drawable.star1);
                                    wrapParams = new LinearLayout.LayoutParams(30, 30);
                                    image.setLayoutParams(wrapParams);
                                    fuwu.addView(image);
                                }
                            }
                            zhiliangfen.setText(result.getTeach() + "分");
                            fuwufen.setText(result.getWait() + "分");

                            falg = true;
                            latitude = result.getLatitude();
                            longitude = result.getLongitude();
                            initMap(result.getLatitude(),result.getLongitude());

                            getCommentFirst(commentUrl);

                        } else {
                            Toast.makeText(ReservationActivity.this, "没有更多的！", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    /**
     * 更改教练
     * @param uid 用户id
     */
    private void changeCoach(String uid) {
        OkHttpUtils
                .post()
                .url(changeUrl)
                .addParams("cid", coachId)
                .addParams("uid", uid)
                .addParams("token", token)
                .build()
                .execute(new StringCallback() {
                             @Override
                             public void onError(Call call, Exception e, int i) {
                                 progressDialog.dismiss();
                                 Toast.makeText(ReservationActivity.this, "网络状态不佳，请稍后再试。", Toast.LENGTH_LONG).show();
                             }

                             @Override
                             public void onResponse(String s, int i) {
                                 progressDialog.dismiss();
                                 Gson gson = new Gson();
                                 Result result = gson.fromJson(s, Result.class);
                                 Toast.makeText(ReservationActivity.this, result.getReason(), Toast.LENGTH_LONG).show();
                                 if (result.getCode() == 200) {
                                     Toast.makeText(ReservationActivity.this, result.getReason(), Toast.LENGTH_SHORT).show();
                                     finish();
                                 }
                             }
                         }
                );
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_backward:
                finish();
                break;
            case R.id.connectCus:
                new CallDialog(this,"055165555744").call();
                break;
            case R.id.signup_Coach:
                if (signup_Coach.getText().toString().equals("立即报名")) {
                    SharedPreferences sp = getSharedPreferences("USER", Activity.MODE_PRIVATE);
                    String str = sp.getString("userInfo", null);
                    Gson gson = new Gson();
                    SharedPreferences sp1 = getSharedPreferences("token", Activity.MODE_PRIVATE);
                    token = sp1.getString("token", null);
                    userInfo = gson.fromJson(str, UserInfo.class);
                    if (userInfo == null) {
                        Intent intent = new Intent();
                        intent.setClass(ReservationActivity.this, LoginActivity.class);
                        intent.putExtra("message","reservation");
                        startActivity(intent);
                    }else {
                        Intent intent2 = new Intent();
                        intent2.setClass(ReservationActivity.this, PayInfoActivity.class);
                        intent2.putExtra("uid", uid);
                        intent2.putExtra("token", token);
                        intent2.putExtra("coachInfo", signup_Coach.getTag().toString());
                        startActivity(intent2);
                    }
                } else if (signup_Coach.getText().toString().equals("更改教练")) {
                    SharedPreferences sp = getSharedPreferences("USER", Activity.MODE_PRIVATE);
                    String str = sp.getString("userInfo", null);
                    Gson gson = new Gson();
                    userInfo = gson.fromJson(str, UserInfo.class);
                    if (userInfo == null) {
                        Intent intent2 = new Intent();
                        intent2.setClass(ReservationActivity.this, LoginActivity.class);
                        intent2.putExtra("message","modifyCoach");
                        startActivity(intent2);
                        finish();
                    } else {
                        progressDialog = ProgressDialog.show(ReservationActivity.this, null, "修改中...");
                        changeCoach(userInfo.getResult().getUid() + "");
                    }
                }
                break;
            case R.id.share:
                /**
                 * setDisplayList方法是友盟内部封装好的，我们拿来调用就好了，
                 * 当我们需要多个分享时只需，在括号里面填写即可，不要另外的写dialog
                 * withTargetUrl设置分享链接，
                 * withTitle设置分享显示的标题
                 * withMedia设置分享图片等
                 * withText设置分享文本
                 * setCallback，当分享成功会回调setCallback此方法，用于显示分享动态
                 *
                 * tips：温馨提示,此版本只能分享到签名版的，debug版本无法分享，如果需要debug分享的话
                 *       请在各大平台上注册并完善debug和正式版本的信息填写，然后更换此项目的appkey
                 *
                 */
                image = new UMImage(ReservationActivity.this, coach_head.getTag().toString());
                UMWeb web = new UMWeb(url + share.getTag().toString());
                web.setTitle("百信学车向您分享");//标题
                web.setThumb(image);  //缩略图
                web.setDescription("科技改变生活，百信引领学车！百信学车在这里向您分享我们这里最优秀的教练" + coach_name.getText().toString());//描述

                new ShareAction(this).setDisplayList(SHARE_MEDIA.SINA,SHARE_MEDIA.QQ,SHARE_MEDIA.WEIXIN,SHARE_MEDIA.WEIXIN_CIRCLE)
                        .withMedia(web)
                        .setCallback(umShareListener)
                        .open();
                break;
            case R.id.costsThat:
                createDialog(0);
                break;
            case R.id.fourPromiselinearLayout:
                createDialog(1);
                break;
        }
    }

    /**
     * 创建展示弹框
     *
     * @param i 表示是展示4个承诺还是服务费用
     */
    private void createDialog(int i) {
        dialog = new Dialog(ReservationActivity.this, R.style.ActionSheetDialogStyle);
        if (i == 0) {
            dialogView = LayoutInflater.from(ReservationActivity.this).inflate(R.layout.coststhat, null);
        } else if (i == 1) {
            dialogView = LayoutInflater.from(ReservationActivity.this).inflate(R.layout.fourpromise, null);
        }
        Button btn = (Button) dialogView.findViewById(R.id.dialog_sure);
        btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        // 将布局设置给Dialog
        dialog.setContentView(dialogView);
        // 获取当前Activity所在的窗体
        Window dialogWindow = dialog.getWindow();
        // 设置dialog横向充满
        dialogWindow.setLayout(android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogWindow.setGravity(Gravity.BOTTOM);
        /**
         * 将对话框的大小按屏幕大小的百分比设置
         */
        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        p.height = (int) (d.getHeight() * 0.5); // 高度设置为屏幕的0.6
        dialogWindow.setAttributes(p);
        dialog.show();// 显示对话框
    }

    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onStart(SHARE_MEDIA platform) {
            //分享开始的回调
        }
        @Override
        public void onResult(SHARE_MEDIA platform) {
            Log.d("plat","platform"+platform);

            Toast.makeText(ReservationActivity.this, platform + " 分享成功啦", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(ReservationActivity.this,platform + " 分享失败啦", Toast.LENGTH_SHORT).show();
            if(t!=null){
                Log.d("throw","throw:"+t.getMessage());
            }
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(ReservationActivity.this,platform + " 分享取消了", Toast.LENGTH_SHORT).show();
        }
    };

    //分享必须重写这个借口
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getComment(String comment) {
        Log.i("百信学车","评论参数" +"page=" + commentPage + "   cid=" + coachId);
        OkHttpUtils
                .post()
                .url(comment)
                .addParams("page", commentPage + "")
                .addParams("cid", coachId)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        Toast.makeText(ReservationActivity.this, "加载失败", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        Log.i("百信学车","评论结果" + s);
                        listView.setTag(s);
                        if (listView.getTag() != null) {
                            setCom();
                        }
                    }
                });
    }

    private void getCommentFirst(String comment) {
        OkHttpUtils
                .post()
                .url(comment)
                .addParams("page","1")
                .addParams("cid", coachId)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        progressDialog.dismiss();
                        Toast.makeText(ReservationActivity.this, "加载失败", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        progressDialog.dismiss();
                        Log.i("百信学车","评论结果" + s);
                        Gson gson = new Gson();
                        CommentResult coachInfos = gson.fromJson(s, CommentResult.class);
                        if(coachInfos.getCode() == 200){
                            listStu = coachInfos.getResult();
                            if(listStu.size() == 0){
                                linear_list_noData.setVisibility(View.VISIBLE);
                            }
                            adapter = new CoachFullDetailAdapter(ReservationActivity.this, listStu);
                            listView.setAdapter(adapter);
                        }else{
                            listStu = coachInfos.getResult();
                            if(listStu.size() == 0){
                                linear_list_noData.setVisibility(View.VISIBLE);
                            }
                            adapter = new CoachFullDetailAdapter(ReservationActivity.this, listStu);
                            listView.setAdapter(adapter);
                            //Toast.makeText(ReservationActivity.this, coachInfos.getReason(), Toast.LENGTH_LONG).show();
                        }

                    }
                });
    }

    private void setCom() {
        String str = listView.getTag().toString();
        Gson gson = new Gson();
        CommentResult coachInfo = gson.fromJson(str, CommentResult.class);
        if (coachInfo.getCode() == 200) {
            //listView.setFocusable(false);
            // 实例化listView显示学员的评价
            //listStu = coachInfo.getResult();
            listStu.addAll(coachInfo.getResult());
            if(listStu.size() == 0){
                linear_list_noData.setVisibility(View.VISIBLE);
            }else{
                linear_list_noData.setVisibility(View.GONE);
            }
            adapter.notifyDataSetChanged();
        } else {
            Toast.makeText(ReservationActivity.this, coachInfo.getReason(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLoad() {
        swipeLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(falg == true){
                    if(linear_list_noData.getVisibility() == View.VISIBLE){
                        commentPage = 1;
                    }else{
                        commentPage = 2;
                    }
                    falg = false;
                }else{
                    if(linear_list_noData.getVisibility() == View.VISIBLE){
                        commentPage = 1;
                    }else{
                        commentPage++;
                    }
                }
                getComment(commentUrl);
                swipeLayout.setLoading(false);
            }
        }, 2000);
    }

    @Override
    public void onRefresh() {
        swipeLayout.postDelayed(new Runnable() {

            @Override
            public void run() {
                commentPage = 1;
                listStu.clear();
                getComment(commentUrl);
                swipeLayout.setRefreshing(false);
            }
        }, 2000);
    }
}
