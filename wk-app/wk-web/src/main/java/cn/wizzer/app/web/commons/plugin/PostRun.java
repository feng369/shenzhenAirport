package cn.wizzer.app.web.commons.plugin;

import org.nutz.lang.Strings;

import java.util.HashMap;
import java.util.Map;

public class PostRun {

    private  String charset  = "UTF-8";
    private HttpClientUtil httpClientUtil = null;

    //POST
    public String doWXTokenGet(String url){
        httpClientUtil = new HttpClientUtil();
        return httpClientUtil.doGet(url,charset);
    }




}
