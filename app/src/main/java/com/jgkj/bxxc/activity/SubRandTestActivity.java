package com.jgkj.bxxc.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.jgkj.bxxc.R;
import com.jgkj.bxxc.adapter.OrderAdapter;
import com.jgkj.bxxc.bean.ErrorMsg;
import com.jgkj.bxxc.bean.HistoryView;
import com.jgkj.bxxc.bean.SubTest;
import com.jgkj.bxxc.bean.entity.SubProjectEntity.SubProjectEntity;
import com.jgkj.bxxc.db.DBManager;
import com.jgkj.bxxc.tools.StatusBarCompat;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

/**
 * Created by fangzhou on 2016/12/23.
 * 科目一随机考题
 */

public class SubRandTestActivity extends Activity implements View.OnClickListener {
    private TextView title, skipToPage;
    private Button back_forward;
    private ViewPager viewPager;
    private ProgressDialog proDialog;
    private SubTest subTest;
    private OrderAdapter orderAdapter, adapter;
    private String subUrl = "http://www.baixinxueche.com/index.php/Home/Apiupdata/sendsubject";
    private int count = 1;
    private ImageView image;
    private TextView order_quester;
    private TextView explain;
    private TextView answer_item1;
    private TextView answer_item2;
    private TextView answer_item3;
    private TextView answer_item4;
    private List<View> list;
    private View view, his_view;
    private LinearLayout detail;

    private SubTest.Result results;
    //历史视图
    private List<HistoryView> hisView;
    //skip控件
    private TextView cancel, ok;
    private EditText editText;
    private int anw;
    //用户选择的答案
    private int user_Answer = 0, right_Answer;
    private Drawable drawable_right, drawable_error;

    private Dialog dialog, saveDialog;

    private TextView dialog_cancel, dialog_sure, dialog_textView;

    private List<SubTest.Result> listTest;

    private SubTest.Result result;
    //上一题下一题
    private TextView above_Question, next_Question;

    private View line;

    private int num;
    /**
     * 自动保存错题
     */
    private List<ErrorMsg.Result> str;
    private ErrorMsg errorSub;
    private class Result{
        private int count;

        public int getCount() {
            return count;
        }
    }

    private List<SubProjectEntity> subProjectEntity = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_test);
        StatusBarCompat.compat(this, Color.parseColor("#37363C"));
        initView();
        //getCount();
        getSubProject();

    }
//    private void getTotalCount(){
//        Gson gson = new Gson();
//        String jsonStr = title.getTag().toString();
//        Result result = gson.fromJson(jsonStr,Result.class);
//        num = result.getCount();
//        count = (int)(Math.random()*num);
//        //getSub(count + "");
//    }
//    //网络请求
//    private void getCount() {
//        OkHttpUtils
//                .post()
//                .url("http://www.baixinxueche.com/index.php/Home/Apiupdata/countsubject")
//                .build()
//                .execute(new StringCallback() {
//                    @Override
//                    public void onError(Call call, Exception e, int i) {
//                        Toast.makeText(SubRandTestActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
//                    }
//                    @Override
//                    public void onResponse(String s, int i) {
//                        title.setTag(s);
//                        if(title.getTag().toString()!=null){
//                            getTotalCount();
//                        }else{
//                            Toast.makeText(SubRandTestActivity.this,"网络不佳请检查网络设置", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//    }

    //网络请求,根据题号加载题目内容
    private void getSubProject() {
        if(subProjectEntity == null){
            subProjectEntity = DBManager.getInstance().getSubProject();
            num = subProjectEntity.size();
        }
        SubProjectEntity entity = null;
        if(subProjectEntity != null){
            count = (int)(Math.random()*num);
            for(int i=0;i<subProjectEntity.size();i++){
                if(subProjectEntity.get(i).getId().equals(count + "")){
                    entity = subProjectEntity.get(i);
                    break;
                }
            }
            if(entity != null){
                count = Integer.parseInt(entity.getId());
                addView(entity);
            }else {
                count--;
                Toast.makeText(SubRandTestActivity.this,"题号不存在", Toast.LENGTH_SHORT).show();
            }
        }else {
            count--;
            Toast.makeText(SubRandTestActivity.this,"题号不存在", Toast.LENGTH_SHORT).show();
        }
    }

    private void initView() {
        title = (TextView) findViewById(R.id.text_title);
        title.setText("第1题");

        above_Question = (TextView) findViewById(R.id.above_Question);
        next_Question = (TextView) findViewById(R.id.next_Question);
        next_Question.setOnClickListener(this);
        above_Question.setVisibility(View.GONE);
        line = findViewById(R.id.line);
        line.setVisibility(View.GONE);

        back_forward = (Button) findViewById(R.id.button_backward);
        back_forward.setVisibility(View.VISIBLE);
        back_forward.setOnClickListener(this);
        skipToPage = (TextView) findViewById(R.id.skipToPage);
        skipToPage.setVisibility(View.GONE);
        viewPager = (ViewPager) findViewById(R.id.order_test_viewPager);
        //转化right和error的图标为drawable类型
        drawable_right = getResources().getDrawable(R.drawable.right);
        drawable_right.setBounds(0, 0, drawable_right.getMinimumWidth(), drawable_right.getMinimumHeight());
        drawable_error = getResources().getDrawable(R.drawable.error);
        drawable_error.setBounds(0, 0, drawable_error.getMinimumWidth(), drawable_error.getMinimumHeight());
        SharedPreferences sp = getSharedPreferences("error_test", Activity.MODE_PRIVATE);
        String jsonStr = sp.getString("error_code",null);
        if(jsonStr==null||jsonStr==""){
            str = new ArrayList<>() ;
            errorSub = new ErrorMsg();
        }else{
            Gson gson = new Gson();
            errorSub = gson.fromJson(jsonStr,ErrorMsg.class);
            str = errorSub.getResult();
        }
    }

//    //网络请求
//    private void getSub(String id) {
//        proDialog = ProgressDialog.show(SubRandTestActivity.this, null, "加载中...");
//        OkHttpUtils
//                .post()
//                .url(subUrl)
//                .addParams("id", id)
//                .build()
//                .execute(new StringCallback() {
//                    @Override
//                    public void onError(Call call, Exception e, int i) {
//                        Toast.makeText(SubRandTestActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
//                    }
//                    @Override
//                    public void onResponse(String s, int i) {
//                        viewPager.setTag(s);
//                        if (viewPager.getTag().toString() != null) {
//                            getViewTag();
//                        } else {
//                            Toast.makeText(SubRandTestActivity.this, "网络不佳请稍后再试", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//    }

//    private void getViewTag() {
//        String str = viewPager.getTag().toString();
//        Gson gson = new Gson();
//        subTest = gson.fromJson(str, SubTest.class);
//        proDialog.dismiss();
//        if (subTest.getCode() == 200) {
//            count = Integer.parseInt(subTest.getResult().getId());
//            addView(subTest.getResult());
//        } else {
//            Toast.makeText(SubRandTestActivity.this, subTest.getReason(), Toast.LENGTH_SHORT).show();
//        }
//
//    }

    private void addView(SubProjectEntity results) {
        list = new ArrayList<View>();
        title.setText("第" + results.getId() + "题");

        view = LayoutInflater.from(SubRandTestActivity.this).inflate(R.layout.first_test_order, null);
        image = (ImageView) view.findViewById(R.id.test_imageView);
        order_quester = (TextView) view.findViewById(R.id.order_quester);
        explain = (TextView) view.findViewById(R.id.explain);
        answer_item1 = (TextView) view.findViewById(R.id.answer_item1);
        answer_item2 = (TextView) view.findViewById(R.id.answer_item2);
        answer_item3 = (TextView) view.findViewById(R.id.answer_item3);
        answer_item4 = (TextView) view.findViewById(R.id.answer_item4);
        detail = (LinearLayout) view.findViewById(R.id.detail);

        anw = Integer.parseInt(results.getAnswer());

        order_quester.setText(results.getQuestion());
        if(results.getAnswer().equals("1")){
            explain.setText("正确答案为：A。"+results.getExplains());
        }else if(results.getAnswer().equals("2")){
            explain.setText("正确答案为：B。"+results.getExplains());
        }else if(results.getAnswer().equals("3")){
            explain.setText("正确答案为：C。"+results.getExplains());
        }else if(results.getAnswer().equals("4")){
            explain.setText("正确答案为：D。"+results.getExplains());
        }
        answer_item1.setText("A:"+results.getItem1());
        answer_item2.setText("B:"+results.getItem2());

        if(results.getItem3().equals("")||results.getItem3()==null||
                results.getItem4().equals("")||results.getItem4()==null){
            answer_item3.setVisibility(View.GONE);
            answer_item4.setVisibility(View.GONE);
        }
        answer_item3.setText("C:"+results.getItem3());
        answer_item4.setText("D:"+results.getItem4());
        String path = results.getUrl();
        if(!path.endsWith(".jpg")&&!path.endsWith(".jpeg")&&!path.endsWith(".png")&&
                !path.endsWith(".GIF")&&!path.endsWith(".PNG") &&!path.endsWith(".JPG")&&!path.endsWith(".gif")){
            image.setVisibility(View.GONE);
        }else{
            Glide.with(SubRandTestActivity.this).load(results.getUrl()).into(image);
        }
        list.add(view);
        orderAdapter = new OrderAdapter(SubRandTestActivity.this, list);
        viewPager.setAdapter(orderAdapter);
    }

    public void userClick(View view) {
        int user_Answer = 0;
        switch (view.getId()) {
            case R.id.answer_item1:
                if (anw == 1) {
                    answer_item1.setCompoundDrawables(null, null, drawable_right, null);
                } else {
                    answer_item1.setCompoundDrawables(null, null, drawable_error, null);
                }
                user_Answer = 1;
                break;
            case R.id.answer_item2:
                if (anw == 2) {
                    answer_item2.setCompoundDrawables(null, null, drawable_right, null);
                } else {
                    answer_item2.setCompoundDrawables(null, null, drawable_error, null);
                }
                user_Answer = 2;
                break;
            case R.id.answer_item3:
                if (anw == 3) {
                    answer_item3.setCompoundDrawables(null, null, drawable_right, null);
                } else {
                    answer_item3.setCompoundDrawables(null, null, drawable_error, null);
                }
                user_Answer = 3;
                break;
            case R.id.answer_item4:
                if (anw == 4) {
                    answer_item4.setCompoundDrawables(null, null, drawable_right, null);
                } else {
                    answer_item4.setCompoundDrawables(null, null, drawable_error, null);
                }
                user_Answer = 4;
                break;
        }
        explain.setVisibility(View.VISIBLE);
        detail.setVisibility(View.VISIBLE);
        answer_item2.setEnabled(false);
        answer_item1.setEnabled(false);
        answer_item3.setEnabled(false);
        answer_item4.setEnabled(false);
        //自动保存错题数据
        SharedPreferences sp = getSharedPreferences("error_test", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        ErrorMsg result = new ErrorMsg();
        ErrorMsg.Result res = result.new Result();
        res.setSubCount(count+"");
        if (user_Answer != anw) {
            str.add(res);
        } else {
            str.contains(res);
            str.remove(res);
        }
        errorSub.setResult(str);
        editor.putString("error_code", new Gson().toJson(errorSub));
        editor.commit();
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_backward:
                finish();
                break;
            case R.id.next_Question:
                //getSub((int)(Math.random()*num)+ "");
                getSubProject();
                break;
        }
    }
}
