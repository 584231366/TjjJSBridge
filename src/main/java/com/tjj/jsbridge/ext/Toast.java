package com.tjj.jsbridge.ext;

import android.content.Context;
import android.os.Handler;
import android.webkit.JavascriptInterface;

import com.tjj.jsbridge.abstracts.JSBridgeExtension;
import com.tjj.jsbridge.JSBridgeRequest;


/**
 * 扫描控制器
 */
public class Toast extends JSBridgeExtension {
    private Context mContext;
    private Handler mHandler;
    public Toast(Context context){
        mContext = context;
        mHandler = new Handler();
        setName("Toast");
    }
    @JavascriptInterface
    public void shortToast(JSBridgeRequest request){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                android.widget.Toast.makeText(mContext, request.getDataAsString(), android.widget.Toast.LENGTH_SHORT).show();
            }
        });
    }
    @JavascriptInterface
    public void longToast(JSBridgeRequest request){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                android.widget.Toast.makeText(mContext, request.getDataAsString(), android.widget.Toast.LENGTH_LONG).show();
            }
        });
    }
}
