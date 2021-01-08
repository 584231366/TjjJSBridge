package com.tjj.jsbridge.ext;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.webkit.JavascriptInterface;


import com.tjj.jsbridge.abstracts.JSBridgeExtension;
import com.tjj.jsbridge.JSBridgeRequest;
import com.tjj.jsbridge.JSBridgeResponse;

import org.json.JSONException;
import org.json.JSONObject;

public class Device extends JSBridgeExtension {
    private Context mContext;
    private Handler mHandler;
    public Device(Context context){
        mContext = context;
        mHandler = new Handler();
        setName("Device");
    }
    @JavascriptInterface
    public void info(JSBridgeRequest request){
        JSBridgeResponse response = new JSBridgeResponse();
        JSONObject json = new JSONObject();
        try {
            json.put("BOARD",Build.BOARD);// 主板
            json.put("BRAND",Build.BRAND);// 系统定制商
            json.put("DEVICE",Build.DEVICE);// 设备参数
            json.put("DISPLAY",Build.DISPLAY);// 显示屏参数
            json.put("SERIAL",Build.SERIAL);// 硬件序列号
            json.put("ID",Build.ID);// 修订版本列表
            json.put("MANUFACTURER",Build.MANUFACTURER);// 硬件制造商
            json.put("MODEL",Build.MODEL);//版本
            json.put("HARDWARE",Build.HARDWARE);//硬件名
            json.put("PRODUCT",Build.PRODUCT);//手机产品名
            json.put("TAGS",Build.TAGS);// 描述build的标签
            json.put("TYPE",Build.TYPE);// Builder类型
            json.put("VERSION_CODENAME",Build.VERSION.CODENAME);//当前开发代号
            json.put("VERSION_INCREMENTAL",Build.VERSION.INCREMENTAL);//源码控制版本号
            json.put("VERSION_RELEASE",Build.VERSION.RELEASE);//版本字符串
            json.put("VERSION_SDK_INT",Build.VERSION.SDK_INT);//版本号
            json.put("HOST",Build.HOST);// HOST值
            json.put("USER",Build.USER);// User名
            response.setData(json);
            request.callback(response);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
