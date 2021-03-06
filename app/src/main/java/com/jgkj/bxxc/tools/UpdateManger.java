package com.jgkj.bxxc.tools;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import com.jgkj.bxxc.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by fangzhou on 2016/11/23.
 * 软件版本更新
 */

public class UpdateManger {
    // 应用程序Context
    private Context mContext;
    // 提示消息
    private String updateMsg = "有最新的软件包，请下载！";
    private String apkUrl;
    private String style;   //是否强制更新
    private Dialog noticeDialog;// 提示有软件更新的对话框
    private Dialog downloadDialog;// 下载对话框
    private static final String savePath = "/sdcard/BX/";// 保存apk的文件夹
    private static final String saveFileName = savePath + "bxxc.apk";
    // 进度条与通知UI刷新的handler和msg常量
    private ProgressBar mProgress;
    private static final int DOWN_UPDATE = 1;
    private static final int DOWN_OVER = 2;
    private int progress;// 当前进度
    private Thread downLoadThread; // 下载线程
    private boolean interceptFlag = false;// 用户取消下载
    private String mCurrentVersionName;
    // 通知处理刷新界面的handler
    private Handler mHandler = new Handler() {
        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DOWN_UPDATE:
                    mProgress.setProgress(progress);
                    break;
                case DOWN_OVER:
                    installApk();
                    break;
            }
            super.handleMessage(msg);
        }
    };
    public UpdateManger(Context context, String apkUrl, String mCurrentVersionName,String style) {
        this.apkUrl = apkUrl;
        this.mContext = context;
        this.mCurrentVersionName = mCurrentVersionName;
        this.style = style;
    }
    // 显示更新程序对话框，供主程序调用
    public void checkUpdateInfo() {
        showNoticeDialog();
    }
    private void showNoticeDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext);// Builder，可以通过此builder设置改变AleartDialog的默认的主题样式及属性相关信息
        builder.setCancelable(false);
        builder.setTitle("检查到新版本"+mCurrentVersionName);
        builder.setMessage(updateMsg);
        if (style.equals("1")){
            builder.setPositiveButton("强制更新", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();// 当取消对话框后进行操作一定的代码？取消对话框
                    showDownloadDialog();
                }
            });
        }else if (style.equals("0")){
            builder.setPositiveButton("更新", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();// 当取消对话框后进行操作一定的代码？取消对话框
                    showDownloadDialog();
                }
            });
            builder.setNegativeButton("以后再说", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }
        noticeDialog = builder.create();
        noticeDialog.show();
    }
    protected void showDownloadDialog() {
        if(style.equals("1")){
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext);
            builder.setTitle("正在下载"+mCurrentVersionName);
            final LayoutInflater inflater = LayoutInflater.from(mContext);
            View v = inflater.inflate(R.layout.softdownload, null);
            mProgress = (ProgressBar) v.findViewById(R.id.softUpdate);
            builder.setView(v);// 设置对话框的内容为一个View
            downloadDialog = builder.create();
            downloadDialog.show();
            downloadApk();
        }else if (style.equals("0")){
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext);
            builder.setTitle("正在下载"+mCurrentVersionName);
            final LayoutInflater inflater = LayoutInflater.from(mContext);
            View v = inflater.inflate(R.layout.softdownload, null);
            mProgress = (ProgressBar) v.findViewById(R.id.softUpdate);
            builder.setView(v);// 设置对话框的内容为一个View
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    interceptFlag = true;
                }
            });
            downloadDialog = builder.create();
            downloadDialog.show();
            downloadApk();
        }

    }
    private void downloadApk() {
        downLoadThread = new Thread(mdownApkRunnable);
        downLoadThread.start();
    }
    protected void installApk() {
        File apkfile = new File(saveFileName);
        if (!apkfile.exists()) {
            return;
        }
        Intent i = new Intent(Intent.ACTION_VIEW);
        Uri uri;
        //判断是否是AndroidN以及更高的版本
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.N) {
//            File apkfiles = new File("file://" + saveFileName);
//            if (!apkfiles.exists()) {
//                return;
//            }
            uri = FileProvider.getUriForFile(mContext,"com.jgkj.bxxc.fileProvider",apkfile);
            //Uri uri = FileProvider.getUriForFile(mContext,"com.jgkj.bxxc.fileProvider",apkfiles);
            //i.setDataAndType(contentUri,"application/vnd.android.package-archive");
        }else{
            uri = Uri.fromFile(apkfile);
            //uri = Uri.parse("file://" + apkfile.toString());
            //i.setDataAndType(Uri.parse("file://" + apkfile.toString()),"application/vnd.android.package-archive");// File.toString()会返回路径信息
        }
        i.setDataAndType(uri,"application/vnd.android.package-archive");
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        mContext.startActivity(i);
    }
    private Runnable mdownApkRunnable = new Runnable() {
        @Override
        public void run() {
            URL url;
            try {
                url = new URL(apkUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.connect();
                int length = conn.getContentLength();
                InputStream ins = conn.getInputStream();
                File file = new File(savePath);
                if (!file.exists()) {
                    file.mkdir();
                }
                String apkFile = saveFileName;
                File ApkFile = new File(apkFile);
                FileOutputStream outStream = new FileOutputStream(ApkFile);
                int count = 0;
                byte buf[] = new byte[1024];
                do {
                    int numread = ins.read(buf);
                    count += numread;
                    progress = (int) (((float) count / length) * 100);
                    // 下载进度
                    mHandler.sendEmptyMessage(DOWN_UPDATE);
                    if (numread <= 0) {
                        // 下载完成通知安装
                        mHandler.sendEmptyMessage(DOWN_OVER);
                        break;
                    }
                    outStream.write(buf, 0, numread);
                } while (!interceptFlag);// 点击取消停止下载
                outStream.close();
                ins.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
}
