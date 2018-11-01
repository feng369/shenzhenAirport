package cn.wizzer.app.web.commons.plugin;

/**
 * Created by xl on 2017/9/18.
 */

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.nutz.log.Logs;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/*
 * 利用HttpClient进行post请求的工具类
 */
public class HttpClientUtil {
    private static final org.nutz.log.Log log = Logs.get();
    /**
     * JSON参数 调用 API
     *
     * @param parameters
     * @return
     */
    public String postJson(String apiURL,String parameters) {
        String body = null;
        log.info("parameters:" + parameters);
        HttpClient httpClient = null;
        HttpPost method = null;
        long startTime = 0L;
        long endTime = 0L;
        if(apiURL!=null) {
            httpClient = new DefaultHttpClient();
            method = new HttpPost(apiURL);
        }
        if (method != null & parameters != null
                && !"".equals(parameters.trim())) {
            try {
//                com.google.gson.JsonArray arry = new JsonArray();
//                JsonObject j = new JsonObject();
//                j.addProperty("orderId", "中文");
//                j.addProperty("createTimeOrder", "2015-08-11");
//                arry.add(j);
//                String jsonPar = arry.toString();
                //或
//                Map<String,Object> paramMap = new HashMap<String,Object>();
//                paramMap.put("touser",sb.toString());
//                paramMap.put("msgtype","text");
//                paramMap.put("agentid",Integer.valueOf(sysWx.getAgentid()));
//                com.alibaba.fastjson.JSONObject textJsonObj = new JSONObject();
//                textJsonObj.put("content",content);
//                paramMap.put("text",textJsonObj);
//                paramMap.put("safe",0);
//                JSONObject jsonObject = new JSONObject(paramMap);
//                log.info("请求参数："+jsonObject.toJSONString());
                // 建立一个NameValuePair数组，用于存储欲传送的参数
                method.addHeader("Content-type","application/json; charset=utf-8");
                method.setHeader("Accept", "application/json");
                method.setEntity(new StringEntity(parameters, Charset.forName("UTF-8")));
                startTime = System.currentTimeMillis();

                HttpResponse response = httpClient.execute(method);

                endTime = System.currentTimeMillis();
                int statusCode = response.getStatusLine().getStatusCode();

                log.info("statusCode:" + statusCode);
                log.info("调用API 花费时间(单位：毫秒)：" + (endTime - startTime));
                if (statusCode != HttpStatus.SC_OK) {
                    log.error("Method failed:" + response.getStatusLine());
                }

                // Read the response body
                body = EntityUtils.toString(response.getEntity());

            } catch (IOException e) {
                // 网络错误
                e.printStackTrace();
            } finally {
                log.info("调用接口完成！" );
            }

        }else{
            log.error("参数体不能为空!");
        }
        return body;
    }

    public String doPost(String url,Map<String,String> map,String charset){
        HttpClient httpClient = null;
        HttpPost httpPost = null;
        String result = null;
        try{
            httpClient = new SSLClient();
            httpPost = new HttpPost(url);
            //设置参数
            List<NameValuePair> list = new ArrayList<NameValuePair>();
            Iterator iterator = map.entrySet().iterator();
            while(iterator.hasNext()){
                Entry<String,Object> elem = (Entry<String, Object>) iterator.next();
                list.add(new BasicNameValuePair(elem.getKey(),elem.getValue().toString()));
            }
            if(list.size() > 0){
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list,charset);
                httpPost.setEntity(entity);
            }
            HttpResponse response = httpClient.execute(httpPost);
            if(response != null){
                HttpEntity resEntity = response.getEntity();
                if(resEntity != null){
                    result = EntityUtils.toString(resEntity,charset);
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return result;
    }

    public String doGet(String url,String charset){
        HttpClient httpClient = null;
        HttpGet httpget = null;
        String result = null;
        try{
            httpClient = new SSLClient();
            httpget = new HttpGet(url);

            HttpResponse response = httpClient.execute(httpget);
            if(response != null){
                HttpEntity resEntity = response.getEntity();
                if(resEntity != null){
                    result = EntityUtils.toString(resEntity,charset);
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return result;
    }


//    //微信专用
//    public String doWXPost(String url,Map<String,String> map,String charset){
//        HttpClient client = null;
//        HttpPost post = null;
//        String result = "";
//        try{
//            client = new SSLClient();
//            post = new HttpPost(url);
//        }catch (Exception e){
//
//        }
//
//        // 封装表单
//        if (null != map && !map.isEmpty()) {
//            List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
//            for (Map.Entry<String, String> entry : map.entrySet()) {
//                String name = entry.getKey();
//                String value = entry.getValue();
//                BasicNameValuePair pair = new BasicNameValuePair(name, value);
//                parameters.add(pair);
//            }
//
//            try {
//                // 此处为了避免中文乱码，保险起见要加上编码格式
//                UrlEncodedFormEntity encodedFormEntity = new UrlEncodedFormEntity(
//                        parameters, "utf8");
//                post.setEntity(encodedFormEntity);
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//
//            }
//        }
//        try {
//            HttpResponse response = client.execute(post);
////            if (HTTP_STATUS_OK == response.getStatusLine().getStatusCode()) {
//                // 获取服务器请求的返回结果，注意此处为了保险要加上编码格式
//                result = EntityUtils.toString(response.getEntity(), charset);
////            } else {
////                throw new Exception("Invalide response from API"
////                        + response.toString());
////            }
//        } catch (ClientProtocolException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        return result;
//    }
}
