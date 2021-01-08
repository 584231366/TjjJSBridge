package com.tjj.jsbridge.ext;

import android.util.Log;
import android.webkit.JavascriptInterface;

import com.tjj.jsbridge.JSBridgeRequest;
import com.tjj.jsbridge.JSBridgeResponse;
import com.tjj.jsbridge.abstracts.JSBridgeExtension;

public class Test extends JSBridgeExtension {
    public Test(){
        // 设置扩展别名
        setName("Test");
        // 扩展向web注入js
        //setJs("js代码");
    }
    /**
     * 无回调的测试方法
     * @param request
     */
    @JavascriptInterface
    public void test1(JSBridgeRequest request){
        Log.e("传参:",request.getDataAsString());
    }
    /**
     * 回调的测试方法
     * @param request
     */
    @JavascriptInterface
    public void test2(JSBridgeRequest request){
        // 获取参数
        String data = request.getDataAsString();
        // 响应回调处理
        JSBridgeResponse response = new JSBridgeResponse();
        response.setData("传参："+data);
        response.setMessage("已经经过Test.test2处理");
        response.setStatus(200);
        request.callback(response);
    }
}
