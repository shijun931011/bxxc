package com.jgkj.bxxc.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jgkj.bxxc.R;
import com.jgkj.bxxc.tools.StatusBarCompat;

import static android.view.View.VISIBLE;

/**
 * Created by fangzhou on 2017/3/15.
 * 走进百信
 */

public class BXCenterActivity extends Activity implements View.OnClickListener {
    private Button back;
    private TextView title;
    private LinearLayout bxcenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bxcenter);
        StatusBarCompat.compat(this, Color.parseColor("#37363C"));
        initView();
    }

    private void initView() {

        back = (Button) findViewById(R.id.button_backward);
        back.setVisibility(VISIBLE);
        back.setOnClickListener(this);
        title = (TextView) findViewById(R.id.text_title);
        title.setText("走进百信");
        bxcenter = (LinearLayout) findViewById(R.id.bxcenter);
        bxcenter.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.button_backward:
                finish();
                break;
            case R.id.bxcenter:
                intent.setClass(BXCenterActivity.this,WebViewActivity.class);
                intent.putExtra("url","http://www.baixinxueche.com/webshow/keer/baixin.html");
                intent.putExtra("title","走进百信");
                startActivity(intent);
                break;
        }

    }
}
