package com.jgkj.bxxc.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jgkj.bxxc.R;
import com.jgkj.bxxc.bean.UserInfo;
import com.jgkj.bxxc.tools.StatusBarCompat;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;



/**
 * Created by fangzhou on 2017/3/25.
 */

public class InviteFriendsActivity extends Activity implements View.OnClickListener {
    private TextView title;
    private Button button_back;
    private TextView inviteCode;//新浪微博， 微信好友， 微信朋友圈， 活动规则，邀请码, qq好友
    private ImageView img_qq, img_weCircle, img_weChat, img_sina;
    UMImage image;
    private SharedPreferences sp;
    private UserInfo userInfo;
    private UserInfo.Result result;
    private String shareUrl = "http://www.baixinxueche.com/index.php/Home/index/share?uid=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invite_friends);
        StatusBarCompat.compat(this, Color.parseColor("#37363C"));
        initView();
        // 验证是否登录
        sp = getSharedPreferences("USER", Activity.MODE_PRIVATE);
        int isFirstRun = sp.getInt("isfirst", 0);
        if (isFirstRun == 0) {
            Intent intent = new Intent();
            intent.setClass(InviteFriendsActivity.this, LoginActivity.class);
            intent.putExtra("message", "InviteFriendsActivity");
            startActivity(intent);
        }
    }

    private void initView() {
        title = (TextView) findViewById(R.id.text_title);
        title.setText("邀请好友");
        button_back = (Button) findViewById(R.id.button_backward);
        button_back.setVisibility(View.VISIBLE);
        button_back.setOnClickListener(this);
//        button_forward = (Button) findViewById(button_forward);
//        button_forward.setText("邀请记录");
//        button_forward.setVisibility(View.VISIBLE);
//        button_forward.setOnClickListener(this);
        img_sina = (ImageView) findViewById(R.id.img_sina);
        img_weChat = (ImageView) findViewById(R.id.img_weChat);
        img_weCircle = (ImageView) findViewById(R.id.img_wxCircle);
        img_qq = (ImageView) findViewById(R.id.img_qq);
        img_qq.setOnClickListener(this);
        img_sina.setOnClickListener(this);
        img_weChat.setOnClickListener(this);
        img_weCircle.setOnClickListener(this);
//        activityRules = (TextView) findViewById(R.id.activity_rules);   activityRules.setOnClickListener(this);
//
        inviteCode = (TextView) findViewById(R.id.inviteCode);
        SharedPreferences sp = getSharedPreferences("USER", Activity.MODE_PRIVATE);
        String str = sp.getString("userInfo", null);
        Gson gson = new Gson();
        userInfo = gson.fromJson(str, UserInfo.class);
        result = userInfo.getResult();
        inviteCode.setText(result.getPhone());
    }

    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onStart(SHARE_MEDIA platform) {
            //分享开始的回调
        }
        @Override
        public void onResult(SHARE_MEDIA platform) {
            Log.d("plat","platform"+platform);
            Toast.makeText(InviteFriendsActivity.this, platform + " 分享成功啦", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(InviteFriendsActivity.this,platform + " 分享失败啦", Toast.LENGTH_SHORT).show();
            if(t!=null){
                Log.d("throw","throw:"+t.getMessage());
            }
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(InviteFriendsActivity.this,platform + " 分享取消了", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onClick(View view) {
        userInfo = new Gson().fromJson(sp.getString("userInfo",null),UserInfo.class);
        image = new UMImage(InviteFriendsActivity.this, "http://www.baixinxueche.com/webshow/img/gift.png");
        UMWeb web = new UMWeb(shareUrl+userInfo.getResult().getUid());
        web.setTitle("和我一起来学车,更优惠!");//标题
        web.setThumb(image);  //缩略图
        web.setDescription("科技改变生活，百信引领学车。学驾驶就用百信学车，方便快捷，更优惠！");//描述
        switch (view.getId()) {
            case R.id.button_backward:
                finish();
                break;
//            case button_forward:
//                Intent intent = new Intent();
//                intent.setClass(InviteFriendsActivity.this, InvitedToRecordActivity.class);
//                startActivity(intent);
//                break;
//            case R.id.activity_rules:
//                ActivityRuleDialog.Builder dialog = new ActivityRuleDialog.Builder(this);
//                dialog.setTitle("活动规则")
//                        .setMessage("A车通过分享邀请B学车，则A获得99元推荐红包，" +
//                                "B可享受199元的学车优惠卷，" +
//                                "B车报名学车时，使用优惠卷，直接畅想" +
//                                "优惠活动。当B学员顺利通过科目一考试" +
//                                "后，A便能轻松提取奖励红包")
//                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                            }
//                        });
//                dialog.create().show();
//                break;
            case R.id.img_sina:
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
                 */
                new ShareAction(InviteFriendsActivity.this).setPlatform(SHARE_MEDIA.SINA)
                    .withMedia(web)
                    .setCallback(umShareListener)
                    .share();
                break;
            case R.id.img_weChat:
                new ShareAction(InviteFriendsActivity.this).setPlatform(SHARE_MEDIA.WEIXIN)
                        .withMedia(web)
                        .setCallback(umShareListener)
                        .share();
                break;
            case R.id.img_wxCircle:
                new ShareAction(InviteFriendsActivity.this).setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE)
                        .withMedia(web)
                        .setCallback(umShareListener)
                        .share();
                break;
            case R.id.img_qq:
                new ShareAction(InviteFriendsActivity.this).setPlatform(SHARE_MEDIA.QQ)
                        .withMedia(web)
                        .setCallback(umShareListener)
                        .share();
                break;
        }
    }
}
