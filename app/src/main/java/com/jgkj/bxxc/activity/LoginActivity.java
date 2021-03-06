package com.jgkj.bxxc.activity;

import android.app.Activity;
import android.app.Notification;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jgkj.bxxc.R;
import com.jgkj.bxxc.bean.UserInfo;
import com.jgkj.bxxc.tools.JPushDataUitl;
import com.jgkj.bxxc.tools.StatusBarCompat;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import cn.jpush.android.api.BasicPushNotificationBuilder;
import cn.jpush.android.api.CustomPushNotificationBuilder;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import okhttp3.Call;

/**
 * Created by fangzhou on 2016/10/25.
 * 登录
 */
public class LoginActivity extends Activity implements View.OnClickListener {
    private Button login_btn,btn_clear_phone_text,btn_clear_pwd_text;
    private EditText username, password;
    private TextView register_id;
    private TextView morenew;
    private TextView callback;
    private ProgressDialog dialog;
    private static final String TAG = "JPush";
    private String loginOldUrl = "http://www.baixinxueche.com/index.php/Home/Api/LoginPost";
    private String loginUrl = "http://www.baixinxueche.com/index.php/Home/Apialltoken/LoginPost";
    private String loginNewUrl="http://www.baixinxueche.com/index.php/Home/Apialltoken/LoginPostPaypwd";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        StatusBarCompat.compat(this, Color.parseColor("#37363C"));
        init();
    }

    //控件初始化
    private void init() {
        callback = (TextView) findViewById(R.id.callback_id);
        callback.setOnClickListener(this);
        register_id = (TextView) findViewById(R.id.register_id);
        register_id.setOnClickListener(this);
        btn_clear_phone_text = (Button) findViewById(R.id.btn_clear_phone_text);
        btn_clear_phone_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username.getText().clear();
            }
        });
        btn_clear_pwd_text = (Button) findViewById(R.id.btn_clear_pwd_text);
        btn_clear_pwd_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                password.getText().clear();
            }
        });
        username = (EditText) findViewById(R.id.username);
        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                int textLength = username.getText().length();
                if (textLength > 0){
                    btn_clear_phone_text.setVisibility(View.VISIBLE);
                    btn_clear_pwd_text.setVisibility(View.GONE);
                }else{
                    btn_clear_phone_text.setVisibility(View.GONE);
                }
            }
        });
        password = (EditText) findViewById(R.id.password);
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                int textLength = password.getText().length();
                if (textLength > 0){
                    btn_clear_pwd_text.setVisibility(View.VISIBLE);
                    btn_clear_phone_text.setVisibility(View.GONE);
                }else{
                    btn_clear_pwd_text.setVisibility(View.GONE);
                }
            }
        });

        login_btn = (Button) findViewById(R.id.login_button_id);
        login_btn.setOnClickListener(this);
        morenew = (TextView) findViewById(R.id.morenews_id);
        morenew.setOnClickListener(this);
    }

    //登录网络请求
    private void okhttps(final String name, final String pwd) {
        OkHttpUtils
                .post()
                .url(loginNewUrl)
                .addParams("phone", name)
                .addParams("password", pwd)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        dialog.dismiss();
                        Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        dialog.dismiss();
                        Gson gson = new Gson();
                        UserInfo userInfo = gson.fromJson(s, UserInfo.class);
                        if (userInfo.getCode() == 200) {
                            /**
                             * 保存登录状态
                             */
                            SharedPreferences sp = getApplication().getSharedPreferences("USER", Activity.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.clear();
                            editor.putInt("isfirst", 1);
                            editor.putString("userInfo", s);
                            editor.commit();
                            /**
                             * 本地存储登录时间
                             */
                            SharedPreferences sp2 = getSharedPreferences("UserLoginSession", Activity.MODE_PRIVATE);
                            SharedPreferences.Editor editor2 = sp2.edit();
                            editor2.putLong("userSessionTime", new Date().getTime());
                            editor2.commit();
                            /**
                             * 本地存储token值，只有登录之后才会产生token值
                             */
                            SharedPreferences sp1 = getSharedPreferences("token", Activity.MODE_PRIVATE);
                            SharedPreferences.Editor editor1 = sp1.edit();
                            editor1.clear();
                            editor1.putString("token", userInfo.getResult().getToken());
                            editor1.commit();

                            /**
                             * 本地存储paypw(支付密码值)值
                             */
                            SharedPreferences sp_paypwd = getSharedPreferences("paypwd", Activity.MODE_PRIVATE);
                            SharedPreferences.Editor editor_paypwd = sp_paypwd.edit();
                            editor_paypwd.clear();
                            editor_paypwd.putString("paypwd", userInfo.getResult().getPaypwd());
                            editor_paypwd.commit();

                            /**
                             *
                             * 本地储存account值
                             */

                            SharedPreferences account = getSharedPreferences("useraccount", Activity.MODE_PRIVATE);
                            SharedPreferences.Editor editor_account = account.edit();
                            editor_account.clear();
                            editor_account.putString("useraccount", userInfo.getResult().getAccount());
                            editor_account.commit();

                            Intent intent = getIntent();
                            String str = "";
                            str = intent.getStringExtra("message");
                            if (str == null || str.equals("")) {
                                Intent login = new Intent();
                                login.setClass(LoginActivity.this, HomeActivity.class);
                                login.putExtra("FromActivity", "MySetting");
                                startActivity(login);
                                finish();
                            } else if (str.equals("payInfo")||str.equals("InviteFriendsActivity")) {
                                finish();
                            }else{
                                finish();
                            }
                            login_btn.setTag(userInfo.getResult().getUid());
                            if(login_btn.getTag()!=null){
                                setTag();
                                setAlias();
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, userInfo.getReason(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * JPush设置tag
     */
    private void setTag() {
        String tag = login_btn.getTag().toString();

        if (TextUtils.isEmpty(tag)) {
            return;
        }

        // ","隔开的多个 转换成 Set
        String[] sArray = tag.split(",");
        Set<String> tagSet = new LinkedHashSet<String>();
        for (String sTagItme : sArray) {
            if (!JPushDataUitl.isValidTagAndAlias(sTagItme)) {
                return;
            }
            tagSet.add(sTagItme);
        }
        //调用JPush API设置Tag
        mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_TAGS, tagSet));

    }
    /**
     * JPush设置alias
     */
    private void setAlias() {
        String alias = login_btn.getTag().toString();
        if (TextUtils.isEmpty(alias)) {
            return;
        }
        if (!JPushDataUitl.isValidTagAndAlias(alias)) {
            return;
        }

        //调用JPush API设置Alias
        mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_ALIAS, alias));
    }
    private static final int MSG_SET_ALIAS = 1001;
    private static final int MSG_SET_TAGS = 1002;


    /**
     * JPush设置handler
     */
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_SET_ALIAS:
                    Log.d(TAG, "Set alias in handler.");
                    JPushInterface.setAliasAndTags(getApplicationContext(), (String) msg.obj, null, mAliasCallback);
                    break;

                case MSG_SET_TAGS:
                    Log.d(TAG, "Set tags in handler.");
                    JPushInterface.setAliasAndTags(getApplicationContext(), null, (Set<String>) msg.obj, mTagsCallback);
                    break;
                default:
                    Log.i(TAG, "Unhandled msg - " + msg.what);
            }
        }
    };
    /**
     * JPush  tag和alias回调接口，alias回调
     */
    private final TagAliasCallback mAliasCallback = new TagAliasCallback() {

        @Override
        public void gotResult(int code, String alias, Set<String> tags) {
            String logs;
            switch (code) {
                case 0:
                    logs = "Set tag and alias success";
                    Log.i(TAG, logs);
                    break;
                case 6002:
                    logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
                    Log.i(TAG, logs);
                    if (JPushDataUitl.isConnected(getApplicationContext())) {
                        mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_ALIAS, alias), 1000 * 60);
                    } else {
                        Log.i(TAG, "No network");
                    }
                    break;
                default:
                    logs = "Failed with errorCode = " + code;
                    Log.e(TAG, logs);
            }

        }

    };
    /**
     * JPush  tag和alias回调接口，tag回调
     */
    private final TagAliasCallback mTagsCallback = new TagAliasCallback() {
        @Override
        public void gotResult(int code, String alias, Set<String> tags) {
            String logs;
            switch (code) {
                case 0:
                    logs = "Set tag and alias success";
                    Log.i(TAG, logs);
                    break;
                case 6002:
                    logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
                    Log.i(TAG, logs);
                    if (JPushDataUitl.isConnected(getApplicationContext())) {
                        mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_TAGS, tags), 1000 * 60);
                    } else {
                        Log.i(TAG, "No network");
                    }
                    break;

                default:
                    logs = "Failed with errorCode = " + code;
                    Log.e(TAG, logs);
            }

        }

    };
    /**
     * 设置通知提示方式 - 基础属性
     */
    private void setStyleBasic() {
        BasicPushNotificationBuilder builder = new BasicPushNotificationBuilder(LoginActivity.this);
        builder.statusBarDrawable = R.drawable.ic_launcher;
        builder.notificationFlags = Notification.FLAG_AUTO_CANCEL;  //设置为点击后自动消失
        builder.notificationDefaults = Notification.DEFAULT_SOUND;  //设置为铃声（ Notification.DEFAULT_SOUND）或者震动（ Notification.DEFAULT_VIBRATE）
        JPushInterface.setPushNotificationBuilder(1, builder);
        Toast.makeText(LoginActivity.this, "Basic Builder - 1", Toast.LENGTH_SHORT).show();
    }


    /**
     * 设置通知栏样式 - 定义通知栏Layout
     */
    private void setStyleCustom() {
        CustomPushNotificationBuilder builder = new CustomPushNotificationBuilder(LoginActivity.this, R.layout.customer_notitfication_layout, R.id.icon, R.id.title, R.id.text);
        builder.layoutIconDrawable = R.drawable.ic_launcher;
        builder.developerArg0 = "developerArg2";
        JPushInterface.setPushNotificationBuilder(2, builder);
        Toast.makeText(LoginActivity.this, "Custom Builder - 2", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_button_id:
                String name = username.getText().toString();
                String pwd = password.getText().toString();
                if (name.equals("") || name == null) {
                    Toast.makeText(LoginActivity.this, "账号不能为空", Toast.LENGTH_SHORT).show();
                } else if (pwd.equals("") || pwd == null) {
                    Toast.makeText(LoginActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    dialog = ProgressDialog.show(LoginActivity.this, null, "登录中...");
                    okhttps(name, pwd);
                }
                break;
            case R.id.morenews_id:
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, HomeActivity.class);
                intent.putExtra("FromActivity", "LoginActivity");
                startActivity(intent);
                break;
            case R.id.register_id:
                Intent register = new Intent();
                register.setClass(LoginActivity.this, RegisterActivity.class);
                startActivity(register);
                break;
            case R.id.callback_id:
                Intent callBack = new Intent();
                callBack.setClass(LoginActivity.this, CallbackActivity.class);
                startActivity(callBack);
                break;
        }
    }
}
