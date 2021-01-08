package com.tjj.jsbridge.abstracts;

public abstract class JSBridgeExtension {
    public String mName = null;
    public String mJs = null;
    public void setJs(String js){
        mJs = js;
    }
    public String getJs(){
        return mJs;
    }
    public void setName(String name){
        mName = name;
    }
    public String getName(){
        return mName;
    }
}
