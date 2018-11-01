package cn.wizzer.test;

import cn.wizzer.app.web.commons.plugin.HttpClientUtil;
import cn.wizzer.app.web.commons.plugin.SSLClient;
import cn.wizzer.framework.util.DateUtil;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.nutz.lang.Lang;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class OpenAPITest {

    private static Log log = Logs.get();

    public static void main(String[] args) {
        String url ="http://trn.bv-cloud.com:7080/";
//        String url ="http://localhost:8080";
        String tokenUrl = url+"/open/api/token/get";
        String apiTestUrl = url+"/open/api/test/hi";

//        Map<String,Object> paramMap = new HashMap<String,Object>();
//        paramMap.put("touser",sb.toString());
//        paramMap.put("msgtype","text");
//        paramMap.put("agentid",Integer.valueOf(sysWx.getAgentid()));
//        JSONObject textJsonObj = new JSONObject();
//        textJsonObj.put("content",content);
//        paramMap.put("text",textJsonObj);
//        paramMap.put("safe",0);
//        JSONObject jsonObject = new JSONObject(paramMap);

        DateFormat df = new SimpleDateFormat("yyyyMMddHH");
        System.out.println( DateUtil.format(new Date(), "yyyyMMddHH"));
        System.out.println("java md5:"+ Lang.md5("dbe62ef0e1"+"3e4f3d20f7e34064" + DateUtil.format(new Date(), "yyyyMMddHH")));

        String sign0 = getMD5Result("dbe62ef0e1"+"3e4f3d20f7e34064"+df.format(new Date()));
        String sign = getMD5("dbe62ef0e1"+"3e4f3d20f7e34064"+df.format(new Date()));
        String paramJson = "{\"appid\": \"dbe62ef0e1\",\"sign\": \""+sign+"\"}";
        HttpClientUtil httpClientUtil = new HttpClientUtil();
//        String retToken = httpClientUtil.postJson(tokenUrl,paramJson);//参数已改成普通方式，参数采用此方式不可用
        tokenUrl+= "?appid=dbe62ef0e1&sign="+sign;
        String retToken = httpClientUtil.doPost(tokenUrl,new HashMap<>(),"utf-8");
//        String retToken = httpClientUtil.doGet(tokenUrl,"utf-8");//get和post均可使用
        System.out.println(retToken);
        JSONObject jsonObject = JSONObject.parseObject(retToken);
        if("0".equals(jsonObject.getString("code"))){
            JSONObject data = jsonObject.getJSONObject("data");
            String tk = data.getString("token");
            String apiTestPjson="{\"txt\":\"你好\"}";
            String apiTestRes = postJson(apiTestUrl,apiTestPjson,"dbe62ef0e1",tk);//此方式:必须在入口函数配置JsonAdaptor，才能获得到参数值;未在入口函数加tokenfilter对token未做校验
            System.out.println(apiTestRes);

            String ourl = url+"/open/mobile/sys/getVersion";
            String res = doGet(ourl,"utf-8","dbe62ef0e1",tk);
            System.out.println(res);

//            String postp="{\"filename\":\"sssddddd\",\"isnew\":true,\"repairid\":\"\"}";
//            String ourl = url+"/platform/eq/repair/delUpload";
//            String res = postJson(ourl,postp,"fb4771a975",tk);//此方式必须在入口函数配置JsonAdaptor

//            String ourl2 = url+"/platform/eq/repair/delUpload?filename=14923292cea54d92830a8dc977e5f86b&isnew=true&repairid=";
////            String res = httpClientUtil.doPost(ourl2,new HashMap<>(),"utf-8");
//             String res = doGet(ourl2,"utf-8","fb4771a975",tk);
//            System.out.println(res);

            //查询设备信息
//            String ourl2 = url+"/platform/eq/use/postPosition?serial=358511020000281";
////            String res = httpClientUtil.doPost(ourl2,new HashMap<>(),"utf-8");
//            String res = doGet(ourl2,"utf-8","dbe62ef0e1",tk);
//            System.out.println(res);

//            //查询设备信息
//            String ourl2 = url+"/platform/eq/materiel/getAroundUsableEqList?personid=14923292cea54d92830a8dc977e5f86b&position=102.726834,24.999954";
////            String res = httpClientUtil.doPost(ourl2,new HashMap<>(),"utf-8");
//            String res = doGet(ourl2,"utf-8","fb4771a975",tk);
//            System.out.println(res);

        }

//        String tk = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJmOWU1ODc0YmFhIiwiZXhwIjoxNTIzNDE0NDk4fQ.vn0N0ysXpgu8QgT8i8OdXGIwBhPjiE00vBt-4fyFO5LSigWbGmUaZECwZrsjo188GaCCDE_IVmiX-WbbFrENBQ";
//////        String ourl = url+"/platform/eq/useinfo/getUsingEqList2?userid=14923292cea54d92830a8dc977e5f86b&appid=fb4771a975&token="+tk;
//        String ourl = url+"/platform/eq/useinfo/getUsingEqList?userid=14923292cea54d92830a8dc977e5f86b";
////
//        String res = doGet(ourl,"utf-8","fb4771a975",tk);
//        System.out.println(res);
    }

    public static  String  getMD5Result(String inputStr)
    {
        System.out.println("=======加密前的数据:"+inputStr);
        BigInteger bigInteger=null;
        try {

            MessageDigest md = MessageDigest.getInstance("MD5");

            byte[] inputData = inputStr.getBytes("utf-8");

            md.update(inputData);

            bigInteger = new BigInteger(md.digest());

        } catch (Exception e) {e.printStackTrace();}

        System.out.println("MD5加密后:" + bigInteger.toString(16));

        return bigInteger.toString(16);

    }


    /**
     * 生成MD5
     * @return
     */
    public  static  String getMD5(String message){
        String md5Result="";
        try{
            //1.创建一个提供信息摘要算法的对象，初始化为MD5算法对象
            MessageDigest md=MessageDigest.getInstance("MD5");
            //2.将消息变为byte数组
            byte[] input=message.getBytes();
            //3.计算后获得字节数组，128位长度的MD5加密
            byte[] buff=md.digest(input);
            //4.把数组每一个字节（一个字节占8位）换成16进制的md5字符串
            md5Result=bytesHex(buff);

        }catch (Exception e){
            e.printStackTrace();
        }

        return md5Result;
    }

    public static String bytesHex(byte[]bytes){
        StringBuffer md5Result =new StringBuffer();
        //把数组每一字节换成换成16进制连成md5字符串
        int digital;
        for (int i=0;i<bytes.length;i++){


            digital=bytes[i];
            if (digital<0){
                digital+=256;
            }
            if (digital<16){
                md5Result.append("0");
            }
            md5Result.append(Integer.toHexString(digital));
        }
        return md5Result.toString().toUpperCase();
    }

    //-=============================使用HttpClient发送http请求==================================--//

    /**
     * JSON参数 调用 API
     *
     * @param parameters
     * @param appid
     * @param token
     * @return
     */
    public static String postJson(String apiURL, String parameters, String appid, String token) {
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
                method.addHeader("appid",appid);
                method.addHeader("token",token);
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

    public static String doGet(String url, String charset, String appid, String token){
        HttpClient httpClient = null;
        HttpGet httpget = null;
        String result = null;
        try{
            httpClient = new SSLClient();
            httpget = new HttpGet(url);
            httpget.addHeader("appid",appid);
            httpget.addHeader("token",token);
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

    //-=============================使用HttpClient发送http请求 end==================================--//

    //-=============================使用JDK发送http请求==================================--//

    public static JSONObject httpGetJSON(String requestUrl) {
        String requestMethod = "GET";
        JSONObject jsonObject = null;
        StringBuffer buffer = new StringBuffer();
        InputStream in = null;
        HttpURLConnection httpUrlConn =null;
        try {

            URL url = new URL(requestUrl);
            // http协议传输
            httpUrlConn = (HttpURLConnection) url.openConnection();

            httpUrlConn.setDoOutput(true);
            httpUrlConn.setDoInput(true);
            httpUrlConn.setUseCaches(false);
            // 设置请求方式（GET/POST）
            httpUrlConn.setRequestMethod(requestMethod);
//            httpUrlConn.setRequestProperty("Charset", "UTF-8");
//            httpUrlConn.setRequestProperty("contentType", "application/json");

            if ("GET".equalsIgnoreCase(requestMethod))
                httpUrlConn.connect();
            // 将返回的输入流转换成字符串
            in = httpUrlConn.getInputStream();

//            InputStreamReader inputStreamReader = new InputStreamReader(in, "utf-8");
//            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
//
//            String str = null;
//            while ((str = bufferedReader.readLine()) != null) {
//                buffer.append(str);
//            }
//            bufferedReader.close();
//            inputStreamReader.close();

            //网络操作时往往出错，因为调用available()方法时，对发发送的数据可能还没有到达，你得到的count是0，如下避免
            int count = 0;
            while (count == 0) {
                count = in.available();
            }
            byte[] bytes = new byte[count];
//            in.read(bytes);//read()方法可能不保证能读取bytes.length个字节，如下方法可保证读取bytes.length个字节
            int readCount = 0; // 已经成功读取的字节的个数
            while (readCount < count) {
                readCount += in.read(bytes, readCount, count - readCount);
            }
            String res = new String(bytes,"utf-8");
            log.info(requestUrl+"\r\n"+res);
            //jsonObject = net.sf.json.JSONObject.fromObject(res);
            jsonObject = JSONObject.parseObject(res);

        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            // 释放资源

            try {
                if(in!=null)
                    in.close();
                in = null;
                httpUrlConn.disconnect();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return jsonObject;
    }

    public static String getPOSTJSON(String path,Object obj) {
        InputStream inputStream = null;
        HttpURLConnection conn = null;
        String ret = "";
        try {
            URL url = new URL(path);
            if (url != null) {
                // 建立http连接
                conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(3000); // 设置超时时间为3秒
                // 设置允许输出
                conn.setDoOutput(true);
                conn.setDoInput(true);
                // 设置不用缓存
                conn.setUseCaches(false);
                // 设置传递方式
                conn.setRequestMethod("GET");
                // 设置维持长连接
//	            conn.setRequestProperty("Connection", "Keep-Alive");
                // 设置文件字符集:
                conn.setRequestProperty("Charset", "UTF-8");
                // 设置文件类型:
                conn.setRequestProperty("contentType", "application/json");

                if(obj!=null && !"".equals(obj.toString())){
                    //	            //转换为字节数组
//	            byte[] data = (obj.toString()).getBytes();
//	            // 设置文件长度
//	            conn.setRequestProperty("Content-Length", String.valueOf(data.length));
                    // 开始连接请求
                    conn.connect();
//	            OutputStream  out = conn.getOutputStream();
//	            // POST写入请求的字符串
//	            out.write((obj.toString()).getBytes());
//	            out.flush();
//	            out.close();
                }


                int code = conn.getResponseCode(); // 接受返回码
                if (200 == code) { // 返回码为200为成功
                    inputStream = conn.getInputStream();
                    InputStreamReader inputStreamReader=new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    StringBuffer stringBuffer=new StringBuffer();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuffer.append(line);
                    }
                    inputStreamReader.close();
                    bufferedReader.close();
                    ret = stringBuffer.toString();
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally{
            // 释放资源

            try {
                if(inputStream!=null)
                    inputStream.close();
                inputStream = null;
                conn.disconnect();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return ret;
    }

    //-=============================使用JDK发送http请求 end==================================--//
}
