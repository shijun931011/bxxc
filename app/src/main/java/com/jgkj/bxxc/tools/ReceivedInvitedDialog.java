package com.jgkj.bxxc.tools;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.jgkj.bxxc.R;

/**
 * Created by Administrator on 2017/6/13.
 */

public class ReceivedInvitedDialog implements View.OnClickListener{
    private Context context;
    private String content;
    private Dialog dialog;
    private View inflate;
    private TextView dialog_textView, dialog_sure;


    public ReceivedInvitedDialog(Context context, String content){
        this.content = content;
        this.context = context;
    }

    public void call(){
        dialog = new Dialog(context, R.style.ActionSheetDialogStyle);
        // 填充对话框的布局
        inflate = LayoutInflater.from(context).inflate(
                R.layout.sure_cancel_dialog2, null);
        // 初始化控件
        dialog_textView = (TextView) inflate.findViewById(R.id.dialog_textView);
        dialog_sure = (TextView) inflate.findViewById(R.id.dialog_sure);
        dialog_sure.setOnClickListener(this);
        dialog_textView.setText(content);
        // 将布局设置给Dialog
        dialog.setContentView(inflate);
        // 获取当前Activity所在的窗体
        Window dialogWindow = dialog.getWindow();
        // 设置dialog宽度
        dialogWindow.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置Dialog从窗体中间弹出
        dialogWindow.setGravity(Gravity.CENTER);
        dialog.show();// 显示对话框
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
           case R.id.dialog_sure:
               dialog.dismiss();
               break;
        }
    }


}
