package com.jiecxy.security.engine;

import android.content.Context;

import com.jiecxy.security.domain.UpdateInfo;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by apple on 15-1-27.
 */

public class UpdateInfoService
{

    private Context context;
    private InputStream _is;
    private int _urlId;

    public UpdateInfoService(Context context)
    {
        this.context = context;
    }

    public UpdateInfo getUpdateInfo(int urlId) throws Exception
    {
        this._urlId = urlId;

        Runnable r = new NetWorkHandler();
        Thread thread = new Thread(r);
        thread.start();

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return UpdateInfoParser.getUpdateInfo(_is);//解析xml
    }

    private class NetWorkHandler implements Runnable {

        @Override
        public void run() {
            try {
                String path = context.getResources().getString(_urlId);//拿到config.xml里面存放的地址
                URL url = new URL(path);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();//开启一个http链接
                httpURLConnection.setConnectTimeout(5000);//设置链接的超时时间，现在为5秒
                httpURLConnection.setRequestMethod("GET");//设置请求的方式
                _is = httpURLConnection.getInputStream();//拿到一个输入流。里面包涵了update.xml的信息

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
