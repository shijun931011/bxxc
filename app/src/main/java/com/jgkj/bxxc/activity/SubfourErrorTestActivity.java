package com.jgkj.bxxc.activity;

import android.app.Activity;
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
import com.jgkj.bxxc.bean.entity.Sub4ProjectEntity.Sub4ProjectEntity;
import com.jgkj.bxxc.db.DBManager;
import com.jgkj.bxxc.tools.StatusBarCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fangzhou on 2016/12/23.
 * 科目一随机考题
 */

public class SubfourErrorTestActivity extends Activity implements View.OnClickListener {
    private TextView title, skipToPage;
    private Button back_forward;
    private ViewPager viewPager;
    private ProgressDialog proDialog;
    private SubTest subTest;
    private OrderAdapter orderAdapter, adapter;
    private String subUrl = "http://www.baixinxueche.com/index.php/Home/Apiupdata/sendsubjectfour";

    private int count = 0;
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
    //历史视图
    private List<HistoryView> hisView;
    //skip控件
    private TextView cancel, ok;
    private EditText editText;
    //用户选择的答案
    private int user_Answer = 0, right_Answer;
    private Drawable drawable_right, drawable_error, check_background;
    private boolean anw1Flag = false;
    private boolean anw2Flag = false;
    private boolean anw3Flag = false;
    private boolean anw4Flag = false;
    private SubTest.Result results;
    private List<String> userAnw = new ArrayList<>();
    private ErrorMsg errorSub;
    //上一题下一题
    private TextView above_Question, next_Question;
    private View line;
    private int num;
    //答案
    private List<String> arr;
    private String anw;

    /**
     * 自动保存错题
     */
    private List<ErrorMsg.Result> str = null;

    private List<ErrorMsg.Result> strTemp;
    //总题目
    private List<Sub4ProjectEntity> sub4ProjectEntity = null;

    //正确个数
    private int countAns = 0;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private Button sureAnswer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_test);
        StatusBarCompat.compat(this, Color.parseColor("#37363C"));
        initView();
    }

    private void initView() {
        title = (TextView) findViewById(R.id.text_title);
        above_Question = (TextView) findViewById(R.id.above_Question);
        next_Question = (TextView) findViewById(R.id.next_Question);
        next_Question.setOnClickListener(this);
        above_Question.setOnClickListener(this);
        line = findViewById(R.id.line);
        line.setVisibility(View.VISIBLE);

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
        check_background = getResources().getDrawable(R.drawable.check_background);
        check_background.setBounds(0, 0, check_background.getMinimumWidth(), check_background.getMinimumHeight());

        sp = getSharedPreferences("error_fourtest", Activity.MODE_PRIVATE);
        String jsonStr = sp.getString("error_fourcode", null);
        if (jsonStr == null || jsonStr == "") {
            strTemp = new ArrayList<>();
            errorSub = new ErrorMsg();
            Toast.makeText(SubfourErrorTestActivity.this, "暂时没有错题哦", Toast.LENGTH_SHORT).show();
        } else {
            Gson gson = new Gson();
            errorSub = gson.fromJson(jsonStr, ErrorMsg.class);
            strTemp = errorSub.getResult();
            //过滤掉重复题目
            str = remove(strTemp);

            if(!str.isEmpty()){
                getSubProject(str.get(count).getSubCount());
            }else{
                Toast.makeText(SubfourErrorTestActivity.this, "暂时没有错题哦", Toast.LENGTH_SHORT).show();
            }
        }
    }

//    //网络请求
//    private void getSub(String id) {
//        proDialog = ProgressDialog.show(SubfourErrorTestActivity.this, null, "加载中...");
//        OkHttpUtils
//                .post()
//                .url(subUrl)
//                .addParams("id", id)
//                .build()
//                .execute(new StringCallback() {
//                    @Override
//                    public void onError(Call call, Exception e, int i) {
//                        Toast.makeText(SubfourErrorTestActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onResponse(String s, int i) {
//                        viewPager.setTag(s);
//                        if (viewPager.getTag().toString() != null) {
//                            getViewTag();
//                        } else {
//                            Toast.makeText(SubfourErrorTestActivity.this, "网络不佳请稍后再试", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//    }

//    private void getViewTag() {
//        String string = viewPager.getTag().toString();
//        Gson gson = new Gson();
//        subTest = gson.fromJson(string, SubTest.class);
//        proDialog.dismiss();
//        if (subTest.getCode() == 200) {
//            results = subTest.getResult();
//            addView(results);
//        } else {
//            Toast.makeText(SubfourErrorTestActivity.this, subTest.getReason(), Toast.LENGTH_SHORT).show();
//        }
//
//    }

    //网络请求,根据题号加载题目内容
    private void getSubProject(String id) {
        if(sub4ProjectEntity == null){
            sub4ProjectEntity = DBManager.getInstance().getSub4Project();
        }
        Sub4ProjectEntity entity = null;
        if(sub4ProjectEntity != null){
            for(int i=0;i<sub4ProjectEntity.size();i++){
                if(sub4ProjectEntity.get(i).getId().equals(id)){
                    entity = sub4ProjectEntity.get(i);
                    break;
                }
            }
            if(entity != null){
                addView(entity);
            }else {
                Toast.makeText(SubfourErrorTestActivity.this,"题号不存在", Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(SubfourErrorTestActivity.this,"题号不存在", Toast.LENGTH_SHORT).show();
        }
    }

    private void addView(Sub4ProjectEntity results) {
        list = new ArrayList<View>();
        userAnw.clear();
        String rightAnw = "";
        countAns = 0;
        view = LayoutInflater.from(SubfourErrorTestActivity.this).inflate(R.layout.first_test_order, null);
        image = (ImageView) view.findViewById(R.id.test_imageView);
        order_quester = (TextView) view.findViewById(R.id.order_quester);
        explain = (TextView) view.findViewById(R.id.explain);
        answer_item1 = (TextView) view.findViewById(R.id.answer_item1);
        answer_item2 = (TextView) view.findViewById(R.id.answer_item2);
        answer_item3 = (TextView) view.findViewById(R.id.answer_item3);
        answer_item4 = (TextView) view.findViewById(R.id.answer_item4);

        sureAnswer = (Button) view.findViewById(R.id.sureAnswer);
        sureAnswer.setVisibility(View.VISIBLE);
        sureAnswer.setOnClickListener(this);
        answer_item1.setOnClickListener(this);
        answer_item2.setOnClickListener(this);
        answer_item3.setOnClickListener(this);
        answer_item4.setOnClickListener(this);
        detail = (LinearLayout) view.findViewById(R.id.detail);

        anw = results.getAnswer();
        arr = new ArrayList();
        for (int i = 0; i < anw.length(); i++) {
            arr.add(anw.substring(i, i + 1));
        }
        if (arr.size() > 1) {
            title.setText("第" + results.getId() + "题(多选题)");
        } else {
            title.setText("第" + results.getId() + "题(单选题)");
        }
        if (arr.contains("A")) {
            rightAnw = rightAnw + "A";
        }
        if (arr.contains("B")) {
            rightAnw = rightAnw + "B";
        }
        if (arr.contains("C")) {
            rightAnw = rightAnw + "C";
        }
        if (arr.contains("D")) {
            rightAnw = rightAnw + "D";
        }
        explain.setText("正确答案为：" + rightAnw + "。" + results.getExplains());
        order_quester.setText(results.getQuestion());
        answer_item1.setText("A:" + results.getItem1());
        answer_item2.setText("B:" + results.getItem2());

        if (results.getItem3().equals("") || results.getItem3() == null ||
                results.getItem4().equals("") || results.getItem4() == null) {
            answer_item3.setVisibility(View.GONE);
            answer_item4.setVisibility(View.GONE);
            title.setText("第" + results.getId() + "题(判断题)");
        }
        answer_item3.setText("C:" + results.getItem3());
        answer_item4.setText("D:" + results.getItem4());
        String path = results.getUrl();
        if (!path.endsWith(".jpg") && !path.endsWith(".jpeg") && !path.endsWith(".png") &&
                !path.endsWith(".GIF") && !path.endsWith(".PNG") && !path.endsWith(".JPG") && !path.endsWith(".gif")) {
            image.setVisibility(View.GONE);
        } else {
            Glide.with(SubfourErrorTestActivity.this).load(results.getUrl()).into(image);
        }
        list.add(view);
        orderAdapter = new OrderAdapter(SubfourErrorTestActivity.this, list);
        viewPager.setAdapter(orderAdapter);
    }

    /**
     * 检查答案是否正确
     *
     * @return
     */
    private void checkAnw() {
        if (userAnw.contains("A")) {
            if (arr.contains("A")) {
                countAns++;
            } else {
                answer_item1.setCompoundDrawables(null, null, drawable_error, null);
            }
        }
        if (userAnw.contains("B")) {
            if (arr.contains("B")) {
                countAns++;
            } else {
                answer_item2.setCompoundDrawables(null, null, drawable_error, null);
            }
        }
        if (userAnw.contains("C")) {
            if (arr.contains("C")) {
                countAns++;
            } else {
                answer_item3.setCompoundDrawables(null, null, drawable_error, null);
            }
        }
        if (userAnw.contains("D")) {
            if (arr.contains("D")) {
                countAns++;
            } else {
                answer_item4.setCompoundDrawables(null, null, drawable_error, null);
            }
        }
        explain.setVisibility(View.VISIBLE);
        detail.setVisibility(View.VISIBLE);
        //自动保存错题数据
        ErrorMsg result = new ErrorMsg();
        ErrorMsg.Result res = result.new Result();
        res.setSubCount(count + "");
        if (countAns != arr.size()) {
            str.add(res);
        } else {
            str.remove(count);
        }
        answer_item1.setEnabled(false);
        answer_item2.setEnabled(false);
        answer_item3.setEnabled(false);
        answer_item4.setEnabled(false);

        sp = getSharedPreferences("error_fourtest", Activity.MODE_PRIVATE);
        editor = sp.edit();
        errorSub.setResult(str);
        editor.putString("error_fourcode", new Gson().toJson(errorSub));
        editor.commit();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_backward:
                finish();
                break;
            case R.id.sureAnswer:
                checkAnw();
                sureAnswer.setVisibility(View.GONE);
                break;
            case R.id.next_Question:
                if(str != null){
                    if (next_Question.getText().toString().equals("下一题")) {
                        if ((count + 1) < str.size()) {
                            count++;
//                            Toast.makeText(SubfourErrorTestActivity.this, str.get(count).getSubCount(), Toast.LENGTH_SHORT).show();
                            getSubProject(str.get(count).getSubCount());
                        } else {
                            Toast.makeText(SubfourErrorTestActivity.this, "没有下一题了", Toast.LENGTH_SHORT).show();
                        }
                    }
                }else{
                    Toast.makeText(SubfourErrorTestActivity.this, "没有下一题了", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.above_Question:
                if (above_Question.getText().toString().equals("上一题")) {
                    if (count - 1 >= 0) {
                        count--;
                        getSubProject(str.get(count).getSubCount());
                    } else {
                        Toast.makeText(SubfourErrorTestActivity.this, "没有上一题了", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.answer_item1:
                if (!anw1Flag) {
                    answer_item1.setCompoundDrawables(null, null, drawable_right, null);
                    anw1Flag = true;
                    userAnw.add("A");
                } else {
                    answer_item1.setCompoundDrawables(null, null, check_background, null);
                    anw1Flag = false;
                    userAnw.remove("A");
                }
                break;
            case R.id.answer_item2:
                if (!anw2Flag) {
                    answer_item2.setCompoundDrawables(null, null, drawable_right, null);
                    anw2Flag = true;
                    userAnw.add("B");
                } else {
                    answer_item2.setCompoundDrawables(null, null, check_background, null);
                    anw2Flag = false;
                    userAnw.remove("B");
                }
                break;
            case R.id.answer_item3:
                if (!anw3Flag) {
                    answer_item3.setCompoundDrawables(null, null, drawable_right, null);
                    anw3Flag = true;
                    userAnw.add("C");
                } else {
                    answer_item3.setCompoundDrawables(null, null, check_background, null);
                    anw3Flag = false;
                    userAnw.remove("C");
                }
                break;
            case R.id.answer_item4:
                if (!anw4Flag) {
                    answer_item4.setCompoundDrawables(null, null, drawable_right, null);
                    anw4Flag = true;
                    userAnw.add("D");
                } else {
                    answer_item4.setCompoundDrawables(null, null, check_background, null);
                    anw4Flag = false;
                    userAnw.remove("D");
                }
                break;
        }
    }

    /**
     * 去重复
     * @param ss
     * @return
     */
    private List<ErrorMsg.Result> remove(List<ErrorMsg.Result> ss){
        for (int i=0;i<ss.size();i++){
            for (int j = ss.size() - 1 ; j > i; j--)  //内循环是 外循环一次比较的次数
            {
                if (ss.get(i).getSubCount().equals(ss.get(j).getSubCount()))
                {
                    ss.remove(j);
                }
            }
            if (ss.get(i).getSubCount().equals("0"))
            {
                ss.remove(i);
            }
        }
        return ss;
    }
}
