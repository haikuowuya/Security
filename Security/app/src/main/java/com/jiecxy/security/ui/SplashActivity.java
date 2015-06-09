package com.jiecxy.security.ui;

/**
 * Created by apple on 15-1-26.
 */

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;

import com.jiecxy.security.MainActivity;
import com.jiecxy.security.R;
import com.jiecxy.security.domain.UpdateInfo;
import com.jiecxy.security.engine.DownloadTask;
import com.jiecxy.security.engine.UpdateInfoService;

import java.io.File;

public class SplashActivity extends Activity
{
    private TextView tv_version;
    private LinearLayout ll;
    private ProgressDialog progressDialog;

    private UpdateInfo info;
    private String version;

    private static final String TAG = "Security";

    //主线程 处理子线程发过来的消息
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            if(isNeedUpdate(version))
            {
                showUpdateDialog();
            }
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //设置屏幕参数
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.splash);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //获取当前app版本号
        tv_version = (TextView) findViewById(R.id.tv_splash_version);
        version = getVersion();
        tv_version.setText("版本号  " + version);

        //设置启动界面的特效
        ll = (LinearLayout) findViewById(R.id.ll_splash_main);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        alphaAnimation.setDuration(2000);
        ll.startAnimation(alphaAnimation);

        //设置进度圈
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMessage("正在下载...");

        //开启子线程 检查更新
        new Thread()
        {
            public void run()
            {
                try
                {
                    sleep(3000);
                    handler.sendEmptyMessage(0);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            };
        }.start();

    }

    //显示升级提示
    private void showUpdateDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(android.R.drawable.ic_dialog_info);
        builder.setTitle("升级提醒");
        builder.setMessage("版本号: " + info.getVersion() + "\n  " + info.getDescription());
        builder.setCancelable(false);

        //确定升级
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                // 判断SD卡是否存在，并且是否具有读写权限
                if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
                {
                    // 创建下载的更新目录
                    File dir = new File(Environment.getExternalStorageDirectory(), "/security/update");
                    if(!dir.exists())
                    {
                        dir.mkdirs();
                    }

                    //下载
                    String apkPath = Environment.getExternalStorageDirectory() + "/security/update/new.apk";
                    UpdateTask task = new UpdateTask(info.getUrl(), apkPath);
                    progressDialog.show();    //进度条
                    new Thread(task).start();    //子线程下载
                }
                else
                {
                    Toast.makeText(SplashActivity.this, "SD卡不可用，请插入SD卡", Toast.LENGTH_SHORT).show();
                    loadMainUI();
                }
            }
        });

        //取消升级
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener()
        {

            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                loadMainUI();
            }

        });
        builder.create().show();
    }

    private boolean isNeedUpdate(String version)
    {
        UpdateInfoService updateInfoService = new UpdateInfoService(this);
        try
        {
            info = updateInfoService.getUpdateInfo(R.string.serverUrl);
            String v = info.getVersion();
            if(v.equals(version))
            {
                Log.i(TAG, "当前版本：" + version);
                Log.i(TAG, "最新版本：" + v);
                loadMainUI();
                return false;
            }
            else
            {
                Log.i(TAG, "需要更新");
                return true;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Toast.makeText(this, "获取更新信息异常，请稍后再试", Toast.LENGTH_SHORT).show();
            loadMainUI();
        }
        return false;
    }

    private String getVersion()
    {
        try
        {
            PackageManager packageManager = getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);

            return packageInfo.versionName;
        }
        catch (NameNotFoundException e)
        {
            e.printStackTrace();
            return "版本号未知";
        }
    }

    private void loadMainUI()
    {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * 安装apk
     * @param file 要安装的apk的目录
     */
    private void install(File file)
    {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        finish();
        startActivity(intent);
    }

    //===========================================================================================

    /**
     * 下载的线程
     *
     */
    class UpdateTask implements Runnable
    {
        private String path;
        private String filePath;

        public UpdateTask(String path, String filePath)
        {
            this.path = path;
            this.filePath = filePath;
        }

        @Override
        public void run()
        {
            try
            {
                File file = DownloadTask.getFile(path, filePath, progressDialog);
                progressDialog.dismiss();
                install(file);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                progressDialog.dismiss();
                Toast.makeText(SplashActivity.this, "更新失败", Toast.LENGTH_SHORT).show();
                loadMainUI();
            }
        }

    }

}