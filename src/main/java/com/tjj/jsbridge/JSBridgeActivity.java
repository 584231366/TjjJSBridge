package com.tjj.jsbridge;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import androidx.annotation.RequiresApi;

import com.tjj.jsbridge.abstracts.JSBridgeExtension;
import com.tjj.jsbridge.ext.Device;
import com.tjj.jsbridge.ext.Test;
import com.tjj.jsbridge.ext.Toast;
import com.tjj.jsbridge.interfaces.JSBridgeRunnable;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

public abstract class JSBridgeActivity extends Activity {
    private String TAG = "JSBridgeActivity";
    public Handler mHandler;
    public Context mContext;
    public WebView mWebView;
    public HashMap<String, JSBridgeExtension> mExtensions = new HashMap<>();
    HashMap<Object, String> mKeyNames = new HashMap<Object, String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler();
        mContext = this;
        mWebView = new WebView(this);
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.addView(mWebView);
        setContentView(linearLayout);
        addExtension(new Toast(this));
        addExtension(new Device(this));
        // addExtension(new Test());
    }
    /**
     * 添加扩展
     * @param extension
     */
    public void addExtension(JSBridgeExtension extension){
        mExtensions.put(extension.getName(),extension);
    }
    /**
     * 加载默认视图路径
     * @param view
     */
    @SuppressLint("JavascriptInterface")
    public void loadView(String view){
        mWebView.addJavascriptInterface(this, "JSBridge");
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url)
            {
                super.onPageFinished(view, url);
                String js = "" +
                        // js回调函数存储
                        "JSBridge.fns = [];" +
                        // 统一对android的请求功能
                        "JSBridge.request = function(om,data,callback){" +
                            "var request = {" +
                                "om: om," +
                                "data: data" +
                            "};" +
                            "if(typeof callback == 'function'){" + // 回调函数存储 并生成一个存储id
                                "this.fns.push(callback);" +
                                "request.callback_id = this.fns.length - 1;" +
                            "};" +
                            "JSBridge.jsbridgeapi(JSON.stringify(request));" + // 序列化请求参数 向JSBridge类发送请求
                        "};" +
                        // JSBridge响应请求调用对应的回调函数
                        "JSBridge.callback = function(response,callback_id){" +
                            "this.fns[callback_id](response);" +
                        "};";
                mWebView.loadUrl("javascript:"+js);
                for(JSBridgeExtension extension:mExtensions.values()){
                    mWebView.loadUrl("javascript:"+extension.getJs());
                }
            }
        });
        mWebView.loadUrl("file:///android_asset/web/"+view);
    }
    /**
     * om参数 格式（object.method） 通过该信息反射调用注册的对象和方法
     * @param str_request
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @JavascriptInterface
    public void jsbridgeapi(String str_request){
        JSBridgeRequest request = new JSBridgeRequest(this,str_request);
        String object_name = request.getString("om").split("\\.")[0];
        String object_method = request.getString("om").split("\\.")[1];
        JSBridgeResponse response = new JSBridgeResponse();
        Object reflex_obj = mExtensions.get(object_name);
        if(mExtensions.containsKey(object_name)){ // 判断是否存在对应的反射对象
            Method reflex_method = null;
            for(Method method:reflex_obj.getClass().getMethods()){ // 判断是否存在对应的反射方法
                if(method.getName().equals(object_method)){
                    reflex_method = method;
                    break;
                }
            };
            if(reflex_method != null){
                Annotation annotation = reflex_method.getAnnotation(JavascriptInterface.class);
                if(annotation!=null){
                    try {
                        reflex_method.invoke(reflex_obj,request);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }else{
                    String message = mExtensions.get(object_name).getClass().getName()+"内指定"+object_method+"方法的未添加@JavascriptInterface注解！";
                    Log.e(TAG,message);
                    response.setMessage(message).setStatus(400);
                    request.callback(response);
                }
            }else{
                String message = mExtensions.get(object_name).getClass().getName()+"内指定"+object_method+"方法的不存在！";
                Log.e(TAG,message);
                response.setMessage(message).setStatus(400);
                request.callback(response);
            }
        }else{
            String message = object_name+"不存在！";
            Log.e(TAG,message);
            response.setMessage(message).setStatus(400);
            request.callback(response);
        }
    }

    /**
     * 调用js
     * @param js
     */
    public void runJavascript(String js){
        mWebView.loadUrl("javascript:"+js);
    }

    /**
     * 条用js并获取返回值
     * @param js
     * @param runnable
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void runJavascript(String js, JSBridgeRunnable runnable){
        mWebView.evaluateJavascript("javascript:"+js, new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String response) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        runnable.run(response);
                    }
                });
            }
        });
    }
    /**
     * 特殊按钮事件注册
     * @param keyCode
     * @param keyName
     */
    public void registerKeyEvent(int keyCode,String keyName){
        mKeyNames.put(keyCode,keyName);
    }
    /**
     * 实体键按键监听 传递给前端
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean dispatchKeyEvent(KeyEvent event){
        if(event.getAction() == KeyEvent.ACTION_DOWN){
            if(mKeyNames.containsKey(event.getKeyCode())){
                runJavascript("document.onkeypress({keyCode:'"+mKeyNames.get(event.getKeyCode())+"'})", new JSBridgeRunnable() {
                    @Override
                    public void run(String response) {
                        if(response.equals("null") || response.equals("true")){
                            JSBridgeActivity.super.dispatchKeyEvent(event);
                        }
                    }
                });
                return false;
            }
        }
        if(event.getAction() == KeyEvent.ACTION_UP){
            if(mKeyNames.containsKey(event.getKeyCode())){
                runJavascript("document.onkeyup({keyCode:'"+mKeyNames.get(event.getKeyCode())+"'})", new JSBridgeRunnable() {
                    @Override
                    public void run(String response) {
                        if(response.equals("null") || response.equals("true")){
                            JSBridgeActivity.super.dispatchKeyEvent(event);
                        }
                    }
                });
                return false;
            }
        }
        return super.dispatchKeyEvent(event);
    }
}
