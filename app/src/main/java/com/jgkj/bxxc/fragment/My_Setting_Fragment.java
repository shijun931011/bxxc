package com.jgkj.bxxc.fragment;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.jgkj.bxxc.R;
import com.jgkj.bxxc.activity.LearnHisActivity;
import com.jgkj.bxxc.activity.LearnProActivity;
import com.jgkj.bxxc.activity.LoginActivity;
import com.jgkj.bxxc.activity.MyActivity;
import com.jgkj.bxxc.activity.MyCoachActivity;
import com.jgkj.bxxc.activity.MyReservationActivity;
import com.jgkj.bxxc.activity.PersonalInfoActivity;
import com.jgkj.bxxc.activity.SettingActivity;
import com.jgkj.bxxc.activity.Setting_AccountActivity;
import com.jgkj.bxxc.bean.UserInfo;
import com.jgkj.bxxc.tools.CallDialog;
import com.jgkj.bxxc.tools.RefreshLayout;
import com.lidroid.xutils.http.client.multipart.MultipartEntity;
import com.lidroid.xutils.http.client.multipart.content.FileBody;
import com.lidroid.xutils.http.client.multipart.content.StringBody;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.Charset;

import okhttp3.Call;

/**
* 我的资料Fragment
 * 个人中心页面
 */
public class My_Setting_Fragment extends Fragment implements OnClickListener,
        SwipeRefreshLayout.OnRefreshListener {
    private View view;
    private TextView userInfo_textView_id;
    private TextView customer;
    private Dialog dialog, sureDialog;
    private View inflate, sureView;
    private TextView dialog_textView, dialog_sure, dialog_cancel;
    //我的教练
    private TextView myCoach;
    //我的预约
    private TextView myreservation;
    private LinearLayout learnPro;
    private TextView learnPro1;
    private TextView Setting;
    //头像处理
    private ImageView choose_headImage;
    private Dialog headDialog;
    private Button choosePhoto;
    private Button takePhoto;
    private Button she_cancel;
    private View dialogView;
    private TextView userNick;
    // 拍照
    private final static int CAMERA_REQUEST_CODE = 1001;
    // 相册选图
    private final static int CHOOSE_PICTIRE = 1002;
    // 裁剪
    private final static int CROP_REQUEST_CODE = 1003;
    //读取本地信息
    private SharedPreferences sp,sp1;
    private SharedPreferences.Editor editor;
    private UserInfo userInfo;
    private UserInfo.Result result;
    //刷新控件
    private RefreshLayout swipeLayout;
    private Uri corpUri,corpnewUri;
    //我的账户
    private TextView myOrder;
    private Boolean isLogined = false;
    private String token;
    private String refreashUrl = "http://www.baixinxueche.com/index.php/Home/Apialltoken/refresh";
    private String refreashOldUrl = "http://www.baixinxueche.com/index.php/Home/Api/refresh";
    private String uploadImageUrl = "http://www.baixinxueche.com/index.php/Home/Apiinfotoken/get_file";
    private long random,random1=0;
    private TextView learnRecord;
    private TextView myactivity;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.my_setting, container, false);
        File tmpDir = new File(Environment.getExternalStorageDirectory()
                + "/baixinxueche/image/");
        if (!tmpDir.exists()) {
            tmpDir.mkdirs();
        }
        init();
        return view;
    }
    //控件实例化及监听
    private void init() {
        learnRecord = (TextView) view.findViewById(R.id.learn_record);
        learnRecord.setOnClickListener(this);
        choose_headImage = (ImageView) view.findViewById(R.id.choose_headImage);
        userNick = (TextView) view.findViewById(R.id.userNick);
        myactivity = (TextView) view.findViewById(R.id.myactivity);
        myactivity.setOnClickListener(this);
        learnPro = (LinearLayout) view.findViewById(R.id.learnPro);
        learnPro1 = (TextView) view.findViewById(R.id.learnPro1);
        learnPro.setOnClickListener(this);
        Setting = (TextView) view.findViewById(R.id.setting);
        Setting.setOnClickListener(this);
        // 验证是否登录
        sp = getActivity().getSharedPreferences("USER",
                Activity.MODE_PRIVATE);
        int isFirstRun = sp.getInt("isfirst", 0);
        sp1 = getActivity().getSharedPreferences("token",
                Activity.MODE_PRIVATE);
        token = sp1.getString("token",null);
        if (isFirstRun != 0) {
            getData();
        } else {
            isLogined = false;
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.head1);
            choose_headImage.setImageBitmap(bitmap);
            userNick.setText("点击登录");
//            learnPro.setText("无记录");
            if (userNick.getText().toString().trim().equals("点击登录")) {
                userNick.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setClass(getActivity(), LoginActivity.class);
                        startActivity(intent);
                    }
                });
            }
        }
        //下拉刷新
        swipeLayout = (RefreshLayout) view.findViewById(R.id.refresh);
        swipeLayout.setColorSchemeResources(R.color.color_bule2, R.color.color_bule, R.color.color_bule2, R.color.color_bule3);
        swipeLayout.setOnRefreshListener(this);
        //我的账户
        myOrder = (TextView) view.findViewById(R.id.myOrder);
        myOrder.setOnClickListener(this);
        //个人信息
        userInfo_textView_id = (TextView) view.findViewById(R.id.userInfo_textView_id);
        userInfo_textView_id.setOnClickListener(this);
        //客服热线
        customer = (TextView) view.findViewById(R.id.customer);
        customer.setOnClickListener(this);
        //我的教练
        myCoach = (TextView) view.findViewById(R.id.myCoach);
        myCoach.setOnClickListener(this);
        //我的预约
        myreservation = (TextView) view.findViewById(R.id.my_reservation);
        myreservation.setOnClickListener(this);
    }
    /**
     * 获取数据并填充
     */
    private void getData() {
        sp = getActivity().getSharedPreferences("USER",
                Activity.MODE_PRIVATE);
        String str = sp.getString("userInfo", null);
        Gson gson = new Gson();
        userInfo = gson.fromJson(str, UserInfo.class);
        result = userInfo.getResult();
        //退出登录
        isLogined = true;
        String path = result.getPic();
        if(!path.endsWith(".jpg")&&!path.endsWith(".jpeg")&&!path.endsWith(".png")&&
                !path.endsWith(".GIF")&&!path.endsWith(".PNG") &&!path.endsWith(".JPG")&&!path.endsWith(".gif")){
            Glide.with(getActivity()).load("http://www.baixinxueche.com/Public/Home/img/default.png").into(choose_headImage);
        }else{
            Glide.with(getActivity()).load(result.getPic()).into(choose_headImage);
        }
        learnPro1.setText(result.getState());
        if (result.getName() != null&&!result.getName().equals("")) {
            userNick.setText(result.getName());
        } else {
            userNick.setText(result.getPhone());
        }
        choose_headImage.setOnClickListener(this);
    }
    //调用摄像头dialog
    public void showDialog() {
        headDialog = new Dialog(getActivity(), R.style.ActionSheetDialogStyle);
        // 填充对话框的布局
        dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.she_dialog, null);
        // 初始化控件
        choosePhoto = (Button) dialogView.findViewById(R.id.choosePhoto);
        takePhoto = (Button) dialogView.findViewById(R.id.takePhoto);
        she_cancel = (Button) dialogView.findViewById(R.id.she_cancel);
        she_cancel.setOnClickListener(this);
        choosePhoto.setOnClickListener(this);
        takePhoto.setOnClickListener(this);
        // 将布局设置给Dialog
        headDialog.setContentView(dialogView);
        // 获取当前Activity所在的窗体
        Window dialogWindow = headDialog.getWindow();
        // 设置dialog横向充满
        dialogWindow.setLayout(android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogWindow.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.y = 20;// 设置Dialog距离底部的距离
        dialogWindow.setAttributes(lp);
        headDialog.show();// 显示对话框
    }
    // 回调函数，处理头像设置
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (corpUri != null){
                startImgZoom(corpUri);
            }
        } else if (requestCode == CHOOSE_PICTIRE) {
            if (data == null) {
                return;
            } else {
                Uri uri;
                uri = data.getData();
                startImgZoom(uri);
            }
        } else if (requestCode == CROP_REQUEST_CODE) {
            File tmpDir = new File(Environment.getExternalStorageDirectory()
                    + "/baixinxueche/image/"+random1 + ".png");
            if (tmpDir.exists()) {
                uploadImage(uploadImageUrl, Environment.getExternalStorageDirectory()
                        + "/baixinxueche/image/"+random1 + ".png");
            }
        }
    }
    /**
     * 上传头像
     * @param http_url 地址
     * @param filepath 本地图片路径
     */
    public void uploadImage(final String http_url, final String filepath) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    File file = new File(filepath);
                    if (!file.exists()) {
                        Log.i("TAG", "文件不存在");
                    }
                    HttpClient client = new DefaultHttpClient();
                    HttpPost post = new HttpPost(http_url);
                    FileBody fileBody = new FileBody(file, "image/jpg");
                    MultipartEntity entity = new MultipartEntity();
                    entity.addPart("uploadedfile", fileBody);//uploadedfile是图片上传的键值名
                    entity.addPart("uid", new StringBody(result.getUid() + "", Charset.forName("UTF-8")));//设置要传入的参数，key_app是键值名,此外传参时候需要指定编码格式
                    entity.addPart("token", new StringBody(token, Charset.forName("UTF-8")));//设置要传入的参数，key_app是键值名,此外传参时候需要指定编码格式
                    post.setEntity(entity);

                    HttpResponse response = client.execute(post);
                    if (response.getStatusLine().getStatusCode() == 200) {
                        isLogin();
                        HttpEntity httpEntity = response.getEntity();
                        String result = EntityUtils.toString(httpEntity, "utf-8");
                        Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
    /**
     * 图像裁剪
     * @param uri
     */
    private void startImgZoom(Uri uri) {
        random1 = System.currentTimeMillis();
        File fileTemp = new File(Environment.getExternalStorageDirectory() + "/baixinxueche/image/"+random1 + ".png");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //高版本一定要加上这两句话，做一下临时的Uri
            FileProvider.getUriForFile(getActivity(), "com.jgkj.bxxc.fileProvider", fileTemp);
        }
        corpnewUri = Uri.fromFile(fileTemp);
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("return-data", false);//设置为不返回数据
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,corpnewUri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra("scale", true);
        startActivityForResult(intent, CROP_REQUEST_CODE);
    }
    /**
     * 设置拍照图片保存路径
     * @return
     */
    private File getImageStoragePath(Context context){
        File tmpDir = new File(Environment.getExternalStorageDirectory()
                + "/baixinxueche/image/");
        if (!tmpDir.exists()) {
            tmpDir.mkdirs();
        }
        random = System.currentTimeMillis();
        File img = new File(tmpDir.getAbsoluteFile() + "/" + random + ".png");
        try {
            FileOutputStream fos = new FileOutputStream(img);
            fos.flush();
            fos.close();
            return img;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.userInfo_textView_id:
                if (!isLogined) {
                    intent.setClass(getActivity(),LoginActivity.class);
                    startActivity(intent);
                } else {
                    intent.setClass(getActivity(), PersonalInfoActivity.class);
                    intent.putExtra("token",token);
                    startActivity(intent);
                }
                break;
            case R.id.customer:
                new CallDialog(getActivity(), "17756086205").call();
                break;
            case R.id.dialog_sure:
                Intent call_intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "17756086205"));
                startActivity(call_intent);
                dialog.hide();
                break;
            case R.id.dialog_cancel:
                dialog.hide();
                break;
            case R.id.myCoach:
                if (!isLogined) {
                    intent.setClass(getActivity(),LoginActivity.class);
                    startActivity(intent);
                } else {
                    intent.setClass(getActivity(), MyCoachActivity.class);
                    intent.putExtra("uid", result.getUid());
                    if(result.getState().equals("")||result.getState()==null|| result.getState().equals("未报名")){
                        intent.putExtra("state", "未报名");
                    }else{
                        intent.putExtra("state", result.getState());
                    }
                    intent.putExtra("token", token);
                    startActivity(intent);
                }
                break;
            case R.id.my_reservation:
                if (!isLogined){
                    intent.setClass(getActivity(),LoginActivity.class);
                    startActivity(intent);
                }else{
                    intent.setClass(getActivity(), MyReservationActivity.class);
                    intent.putExtra("uid", result.getUid());
                    intent.putExtra("token", token);
                    startActivity(intent);
                }
                break;
            case R.id.learnPro:
                if (!isLogined) {
                    intent.setClass(getActivity(),LoginActivity.class);
                    startActivity(intent);
                } else {
                    intent.setClass(getActivity(), LearnProActivity.class);
                    intent.putExtra("token", token);
                    startActivity(intent);
                }
                break;
            case R.id.choose_headImage:
                if (!isLogined) {
                    intent.setClass(getActivity(),LoginActivity.class);
                    startActivity(intent);
                } else {
                    showDialog();
                }
                break;
            case R.id.takePhoto:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    /*
                    * 这里就是高版本需要注意的，需用使用FileProvider来获取Uri，同时需要注意getUriForFile
                    * 方法第二个参数要与AndroidManifest.xml中provider的里面的属性authorities的值一致
                    * */
                    corpUri = FileProvider.getUriForFile(getActivity(), "com.jgkj.bxxc.fileProvider", getImageStoragePath(getActivity()));
                    Intent intent_takePhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent_takePhoto.putExtra(MediaStore.EXTRA_OUTPUT, corpUri);
                    intent_takePhoto.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivityForResult(intent_takePhoto, CAMERA_REQUEST_CODE);
                }else{
                    corpUri = Uri.fromFile(getImageStoragePath(getActivity()));
                    Intent intent_takePhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent_takePhoto.putExtra(MediaStore.EXTRA_OUTPUT, corpUri);
                    startActivityForResult(intent_takePhoto, CAMERA_REQUEST_CODE);
                }
                headDialog.dismiss();
                break;
            case R.id.choosePhoto:
//                Intent action_getPhoto = new Intent(Intent.ACTION_GET_CONTENT);
//                action_getPhoto.setType("image/*");
//                startActivityForResult(action_getPhoto, CHOOSE_PICTIRE);

                Intent action_getPhoto;
                //当sdk版本低于19时使用此方法
                if (Build.VERSION.SDK_INT < 19) {
                    action_getPhoto = new Intent(Intent.ACTION_GET_CONTENT);
                } else {
                    action_getPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                }
                action_getPhoto.setType("image/*");
                startActivityForResult(action_getPhoto, CHOOSE_PICTIRE);
                headDialog.dismiss();
                break;
            case R.id.she_cancel:
                headDialog.dismiss();
                break;
            case R.id.myOrder:
                if (!isLogined) {
                    intent.setClass(getActivity(),LoginActivity.class);
                    startActivity(intent);
                } else {
                    intent.setClass(getActivity(), Setting_AccountActivity.class);
                    intent.putExtra("uid", result.getUid());
                    intent.putExtra("token", token);
                    startActivity(intent);
                }
                break;
            case R.id.learn_record:
                if (!isLogined) {
                    intent.setClass(getActivity(),LoginActivity.class);
                    startActivity(intent);
                } else {
                    intent.setClass(getActivity(), LearnHisActivity.class);
                    if(result.getState().equals("")||result.getState()==null|| result.getState().equals("未报名")){
                        intent.putExtra("state","未报名");
                    }else{
                        intent.putExtra("state",result.getState());
                        intent.putExtra("token",token);
                    }
                    intent.putExtra("uid",result.getUid());
                    startActivity(intent);
                }
                break;
            case R.id.myactivity:
                intent.setClass(getActivity(),MyActivity.class);
                startActivity(intent);
                break;
            case R.id.setting:
                intent.setClass(getActivity(), SettingActivity.class);
                startActivity(intent);
                break;

        }
    }
    /**
     * 下拉刷新控件
     */
    @Override
    public void onRefresh() {
        swipeLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                isLogin();
                swipeLayout.setRefreshing(false);
            }
        }, 2000);
    }
    private void isLogin(){
        // 验证是否登录
        sp = getActivity().getSharedPreferences("USER", Activity.MODE_PRIVATE);
        String str = sp.getString("userInfo", null);
        Gson gson = new Gson();
        userInfo = gson.fromJson(str, UserInfo.class);
        if(userInfo != null){
            result = userInfo.getResult();
            int isFirstRun = sp.getInt("isfirst", 0);
            if(isFirstRun!=0){
                try {
                    refreshInfo(result.getUid() + "");
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(getActivity(),"暂未登录，请登录后再试！", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    /**
     * 获取焦点时刷新界面
     */
    @Override
    public void onResume() {
        //Toast.makeText(getActivity(),"啦啦啦！", Toast.LENGTH_SHORT).show();
        isLogin();
        init();
        super.onResume();
    }


    /**
     * 刷新个人信息
     * @param uid 用户uid
      */
    private void refreshInfo(String uid) {
        OkHttpUtils
                .post()
                .url(refreashUrl)
                .addParams("uid", uid)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        Toast.makeText(getActivity(), "请检查网络", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onResponse(String s, int i) {
                        Gson gson = new Gson();
                        UserInfo userInfo = gson.fromJson(s, UserInfo.class);
                        Log.d("zyzhang","userinfo:::"+s);
                        if (userInfo.getCode() == 200) {
                            /**
                             * 保存登录状态
                             */
                            SharedPreferences sp = getActivity().getSharedPreferences("USER", Activity.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putInt("isfirst", 1);
                            editor.putString("userInfo", s);
                            editor.commit();
                            getData();
                        }
                    }
                });
    }
}
