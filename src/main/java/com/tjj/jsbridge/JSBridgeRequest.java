package com.tjj.jsbridge;

import org.json.JSONException;
import org.json.JSONObject;

public class JSBridgeRequest {
    JSONObject json;
    JSBridgeActivity mActivity;
    public JSBridgeRequest(JSBridgeActivity activity, String json_str){
        mActivity = activity;
        try {
            json = new JSONObject(json_str);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public boolean hasCallback(){
        return json.has("callback_id");
    }
    public String getString(String name){
        try {
            return json.getString(name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
    public JSONObject getDataAsJSONObject(){
        try {
            return json.getJSONObject("data");
        } catch (JSONException e) {
            return null;
        }
    }
    public String getDataAsString(){
        try {
            return json.getString("data");
        } catch (JSONException e) {
            return null;
        }
    }
    public boolean getDataAsBoolean(){
        try {
            return json.getBoolean("data");
        } catch (JSONException e) {
            return false;
        }
    }
    public void callback(JSBridgeResponse response){
        mActivity.mHandler.post(new Runnable() {
            @Override
            public void run() {
                // 回调处理
                if(hasCallback()){
                    mActivity.runJavascript("javascript:JSBridge.callback("+response.toString()+","+getString("callback_id")+")");
                }
            }
        });
    }
}
