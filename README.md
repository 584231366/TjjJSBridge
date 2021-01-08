# 介绍
自定义的JSBridge
# 用法
1. 创建assets/web/index.html入口文件
2. 继承JSBridgeActivity
   * 添加扩展 addExtension(Extension extension)
   * 加载入口文件 loadView("index.html")
   * 执行js字符串代码(无返回值) runJavascript(String jsStr)
   * 执行js字符串代码 runJavascript(String jsStr,JSBridgeRunnable runable)
   * 注册特殊实体按钮使得js可以获取对应的onkeypress和onkeyup事件 registerKeyEvent(int keyCode,String keyName)
3. 扩展创建demo
   ```
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
   ```
4. js调用扩展和对应的扩展方法
    ```
    function test1(){
        JSBridge.request("Test.test1","test1")
    }
    function test2(){
        JSBridge.request("Test.test2","test2",function(response){
            alert(response.message)
        })
    }
    ```
# 已实现的内部扩展
1. Device 设备信息相关
    * info() 实现对设备信息的获取
2. Toast 实现对Android Toast的调用
    * longToast
    * shortToast

