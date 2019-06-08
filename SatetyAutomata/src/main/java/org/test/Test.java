package org.test;

import java.io.File;
import java.io.IOException;
import java.io.Reader;

import javax.script.Bindings;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;

import utils.graph.MyTransition;
import utils.graph.Node;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Test {
	
	public static void main(String[] args) throws ScriptException {
		MediaType MEDIA_TYPE = MediaType.parse("text/text; charset=utf-8");
        String url = "http://localhost:8080/getParserResultOfJS"; // 请求链接
        OkHttpClient okHttpClient = new OkHttpClient(); // OkHttpClient对象
        String string = "if (b > 10) {\r\n" + 
        		"    b = b + 2;\r\n" + 
        		"} else {\r\n" + 
        		"    a = a + 2;\r\n" + 
        		"}\r\n" + 
        		"\r\n" + 
        		"if (a + b > 10) {\r\n" + 
        		"    a *= a;\r\n" + 
        		"} else {\r\n" + 
        		"    b *= b;\r\n" + 
        		"}\r\n" + 
        		"\r\n" + 
        		"if (a < 5) {\r\n" + 
        		"    b -= 2;\r\n" + 
        		"}"; // 要发送的字符串
        /**
         * RequestBody.create(MEDIA_TYPE, string)
         * 第二个参数可以分别为：byte[]，byteString,File,String。
         */
        Request request = new Request.Builder().url(url)
                    .post(RequestBody.create(MEDIA_TYPE,string)).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            public void onResponse(Call call, Response response) throws IOException {
                System.out.println(response.body().string());
            }
            
            public void onFailure(Call call, IOException e) {
                System.out.println(e.getMessage());
            }
        });
	}
	private static void analyse(Node entry) throws ScriptException {
		int a = 10, b = 20;
		String init = "var a = " + a + ";";
		init += "var b = " + b + ";";
		ScriptEngineManager manager = new ScriptEngineManager();
	    ScriptEngine se = manager.getEngineByName("js");
	    se.eval(init);
	    System.out.println("a = " + a + "; b = " + b);
	    Node head = entry;
	    
	    while(true) {
	    	for(MyTransition mt : head.getOutgoing()) {
	    		if(mt.getCondition() == null) {
	    			head = mt.getTarget();
	    		} else {
	    			boolean flag = (boolean) se.eval(mt.getCondition());
	    			if(flag) {
	    				head = mt.getTarget();
	    				for(String action : head.getActions()) {
	    					se.eval(action);
	    				}
	    				System.out.println("a = " + se.eval("a") + "; b = " + se.eval("b"));
	    				break;
	    			}
	    		}
	    	}
	    	if(head.getOutgoing().size() == 1 && head.getOutgoing().get(0).getTarget().getType() == "exit") {
	    		break;
	    	}
	    }
	    
	    //System.out.println(se.eval("a"));
	}
}
