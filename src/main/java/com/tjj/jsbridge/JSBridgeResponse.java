package com.tjj.jsbridge;

import org.json.JSONException;
import org.json.JSONObject;

public class JSBridgeResponse {
    JSONObject json;
    public JSBridgeResponse(){
        json = new JSONObject();
        try {
            json.put("status",200);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public JSBridgeResponse setStatus(int status){
        try {
            json.put("status",status);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }
    public JSBridgeResponse setMessage(String message){
        try {
            json.put("message",message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }
    public JSBridgeResponse setData(JSONObject data){
        try {
            json.put("data",data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }
    public JSBridgeResponse setData(String data){
        try {
            json.put("data",data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }
    public String toString(){
        return json.toString();
    }
}
