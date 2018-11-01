package cn.wizzer.app.sys.modules.services.impl;

import cn.wizzer.app.sys.modules.models.Sys_wx_user;
import cn.wizzer.app.sys.modules.services.SysWxUserService;
import cn.wizzer.app.web.commons.base.Globals;
import cn.wizzer.app.web.commons.plugin.HttpClientUtil;
import cn.wizzer.app.web.commons.plugin.PostRun;
import cn.wizzer.app.web.modules.controllers.open.api.Datatime.newDataTime;
import cn.wizzer.framework.base.ValidatException;
import cn.wizzer.framework.base.service.BaseServiceImpl;
import cn.wizzer.app.sys.modules.models.Sys_wx;
import cn.wizzer.app.sys.modules.services.SysWxService;
import cn.wizzer.framework.util.PinYinUtil;
import com.alibaba.fastjson.JSONObject;
import org.nutz.aop.interceptor.async.Async;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Record;
import org.nutz.dao.sql.Sql;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import javax.xml.bind.ValidationException;
import java.text.SimpleDateFormat;
import java.util.*;

@IocBean(args = {"refer:dao"})
public class SysWxServiceImpl extends BaseServiceImpl<Sys_wx> implements SysWxService {
    public SysWxServiceImpl(Dao dao) {
        super(dao);
    }
    @Inject
    private SysWxUserService sysWxUserService;
    private static final Log log = Logs.get();


    /**
     * 获取token前需要现在系统中维护corpid,还要现在此model中设置要访问的应用及secret;
     * 若是获取通讯录的access_token,
     *
     * @param cnd
     * @return
     */
    public String getTokenOrNew(Cnd cnd) {
        //

        String corpid = Globals.WxCorpID;
        if (Strings.isBlank(corpid)) {
            throw new ValidatException("请现在系统中维护参数WxCorpID");
        }
        cnd.and("corpid", "=", corpid);
        List<Sys_wx> wxList = this.query(cnd);
        if (wxList.size() != 1) {
            throw new ValidatException("请设置需要访问的应用和访问密钥");
        }
        Sys_wx sysWx = wxList.get(0);
        String token = sysWx.getToken();
        //判断当前token是否失效
        if (!isTokenValid(sysWx)) {
            //已经失效
            token = updateToken(sysWx);
        }
        return token;
    }

    public String sendMessageToUser(String[] userIds, String appnum, String content) {
        Cnd cnd = Cnd.NEW();
        String corpid = Globals.WxCorpID;
        if (Strings.isBlank(corpid) || ".".equals(corpid)) {
            throw new ValidatException("请现在系统中维护参数WxCorpID");
        }
        cnd.and("corpid", "=", corpid);
        cnd.and("appnum", "=", appnum);
        //校验及查询接收用户的微信账号
        if (userIds == null || userIds.length <= 0) {
            throw new ValidatException("请设置接收用户");
        }
        Cnd usecnd = Cnd.NEW();
        Sql sql = Sqls.queryRecord("select t0.username,t1.wxUserid \n" +
                "from sys_user t0 LEFT JOIN base_cnctobj t on t0.id=t.userId LEFT JOIN base_person t1 on t.personId = t1.id \n" +
                " $condition");
        usecnd.and("t0.id", "in", userIds);
        sql.setCondition(usecnd);
        dao().execute(sql);
        List<Record> userlist = sql.getList(Record.class);
        StringBuffer sb = new StringBuffer("");
        List<String> errorUserList = new ArrayList<>();
        for (Record record : userlist) {
            String wxUserid = record.getString("wxUserid");
            String username = record.getString("username");
            if (!Strings.isBlank(wxUserid)) {
                if (sb.length() != 0) {
                    sb.append("|");
                }
                sb.append(wxUserid);
            } else {
                errorUserList.add(username);
            }

        }
        //获取access_token
        List<Sys_wx> list = this.query(cnd);
        if (list.size() != 1) {
            throw new ValidatException("请设置需要访问的应用和访问密钥");
        }
        Sys_wx sysWx = list.get(0);
        String token = sysWx.getToken();
        if (!isTokenValid(sysWx)) {
            token = updateToken(sysWx);
        }
        //POST方式发送信息
        String sendUrl = "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=" + token;
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("touser", sb.toString());
        paramMap.put("agentid", sysWx.getAgentid());
        paramMap.put("msgtype", "text");
        JSONObject textJsonObj = new JSONObject();
        textJsonObj.put("content", content);
        paramMap.put("text", textJsonObj);
        paramMap.put("safe", 0);
        JSONObject jsonObject = new JSONObject(paramMap);
        log.debug("请求参数:" + jsonObject.toJSONString());
        HttpClientUtil httpClientUtil = new HttpClientUtil();
        String retMsg = httpClientUtil.postJson(sendUrl, jsonObject.toJSONString());
        JSONObject retObj = JSONObject.parseObject(retMsg);
        int errcode = retObj.getIntValue("errcode");
        String errmsg = retObj.getString("errmsg");
        String invaliduser = retObj.getString("invaliduser");
        if (errcode != 0) {
            throw new ValidatException("消息发送失败" + errcode + "," + errmsg);
        }
        if (errorUserList.size() > 0) {
            log.debug("发送消息未找到相应的微信用户");
        }
        log.debug("发送无效的微信账号:" + invaliduser.toString());

        return invaliduser + "" + errorUserList.toString().replace("[", "").replace("]", "");
    }

    public void sendWxMessageAsy(Set<String> userIds, String content) {
        try {
            if (userIds != null && userIds.size() > 0) {
                String[] ids = new String[userIds.size()];
                int i = 0;
                while (userIds.iterator().hasNext()) {
                    if (i > userIds.size() - 1) {
                        break;
                    }
                    ids[i] = (String) userIds.iterator().next();
                    i++;
                }
                this.sendMessageToUser(ids, "APP_004", content);
            } else {
                throw new ValidatException("未传入微信用户信息,发送微信失败!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //重新获取Token
    private String updateToken(Sys_wx sysWx) {
        String access_token = "";
        String corpID = Globals.WxCorpID;
        String secret = sysWx.getSecret();
        String tokenurl = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=" + corpID + "&corpsecret=" + secret;
        PostRun pr = new PostRun();
        String tokenJson = pr.doWXTokenGet(tokenurl);
        if (!Strings.isBlank(tokenurl)) {
            {
                JSONObject jsonObject = JSONObject.parseObject(tokenJson);
                if (jsonObject != null) {
                    int errcode = jsonObject.getIntValue("errcode");
                    String errmsg = jsonObject.getString("errmsg");
                    access_token = jsonObject.getString("access_token");
                    String expires_in = jsonObject.getString("expires_in");
                    if (errcode != 0) {
                        throw new ValidatException("获取Token失败:" + errcode + "," + errmsg);
                    }
                    if (Integer.valueOf(Globals.WXExpire) > Integer.valueOf(expires_in)) {
                        log.debug("System config param 'WXExpire' bigger than the Weixin Inteface gettoken value，need modify the system config!");
                        Globals.WXExpire = expires_in;
                    }
                    long times = System.currentTimeMillis() + Integer.valueOf(expires_in) * 1000;
                    sysWx.setToken(access_token);
                    sysWx.setExpire(expires_in);
                    sysWx.setStarttime(newDataTime.getDateYMDHMS());
                    sysWx.setEndtime(newDataTime.getSdfByPattern(null).format(new Date(times)));
                    this.updateIgnoreNull(sysWx);
                    return access_token;
                }

            }
        }
        throw new ValidatException("获取/更新Token失败,请检查设置参数");
    }

    //判断当前token是否失效
    private boolean isTokenValid(Sys_wx sysWx) {
        Date enddate = null;
        try {
            if (!Strings.isBlank(sysWx.getEndtime())) {
                SimpleDateFormat sdf = newDataTime.getSdfByPattern(null);
                enddate = sdf.parse(sysWx.getEndtime());
            }
            Date day = new Date();
            //超时
            if (enddate == null || day.getTime() > enddate.getTime()) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    //在通讯录创建微信人员
    public String createWXUser(String username,String mobile,String jobNumber){
        String userid = null;
        try{
            String corpid = Globals.WxCorpID;
            if(Strings.isBlank(corpid)){
                throw new ValidatException("请现在系统中维护系统参数WxCorpID");
            }

            List<Sys_wx> sysWxes = this.query(Cnd.where("appnum", "=", "APP_003").and("corpid", "=", corpid));
            if(sysWxes.size() != 1){
                throw new ValidatException("请先在系统中维护正确的agentid和corpid");
            }
            Sys_wx sysWx = sysWxes.get(0);
            String token = sysWx.getToken();
            if(!isTokenValid(sysWx)){
                token = updateToken(sysWx);
            }
            if(Strings.isBlank(jobNumber)){
                throw new ValidationException("工号不能为空");
            }
                userid = jobNumber;
            if(!Strings.isBlank(username)){
                String addUrl = "https://qyapi.weixin.qq.com/cgi-bin/user/create?access_token=" + token;
                HttpClientUtil httpClientUtil = new HttpClientUtil();
                Map<String,Object> paramMap = new HashMap<>();
                /*String xing = username.substring(0,1);
                String ming = username.substring(1, username.length());
                userid = "sz"+PinYinUtil.getFullSpell(xing)+PinYinUtil.getFirstSpell(ming);
                int count = sysWxUserService.count(Cnd.where("userid", "=", userid));
                if(count >0 ){
                    //保证账号的唯一性
                    userid = userid + count;
                }*/
                paramMap.put("userid", userid);
                paramMap.put("name",username);
                paramMap.put("mobile",mobile);
                //不发送短信进行邀请
                paramMap.put("to_invite",false);
                //部门
                int[] depts = {4};
                paramMap.put("department",depts);
                JSONObject addJson = new JSONObject(paramMap);
                String ret = httpClientUtil.postJson(addUrl, addJson.toJSONString());
                JSONObject jsonObject = JSONObject.parseObject(ret);
                if(jsonObject != null){
                    int errcode = jsonObject.getIntValue("errcode");
                    String errmsg = jsonObject.getString("errmsg");
                    if(errcode != 0){
                        throw new ValidatException("通讯录添加用户失败:errcode=" + errcode + "," +errmsg);
                    }
                }
            }else{
                throw new ValidatException("姓名不能为空");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return userid;
    }


    public JSONObject getWxUser(String userid){
        //获取access_token
        String corpid = Globals.WxCorpID;
        if(Strings.isBlank(corpid)){
            throw new ValidatException("请现在系统中维护系统参数WxCorpID");
        }
        List<Sys_wx> list = this.query(Cnd.where("agentid", "=", "memo").and("corpid","=",corpid));
        if(list.size() != 1 ){
            throw new ValidatException("请在系统中选择应用和WxCorpID");
        }
        Sys_wx sysWx = list.get(0);
        String token = sysWx.getToken();
        if(!isTokenValid(sysWx)){
            token = updateToken(sysWx);
        }
        if(Strings.isBlank(userid)){
           throw new ValidatException("请先传入要查询用户微信号");
        }
        String getUrl = "https://qyapi.weixin.qq.com/cgi-bin/user/get?access_token="+token+"&userid=" + userid;
        HttpClientUtil httpClientUtil = new HttpClientUtil();
        String ret = httpClientUtil.doGet(getUrl, "UTF-8");
        JSONObject jsonObject =JSONObject.parseObject(ret);
        if(jsonObject !=null){
            int errcode = jsonObject.getIntValue("errcode");
            String errmsg = jsonObject.getString("errmsg");
            //userid
            if(errcode != 0){
                throw new ValidatException("获得用户失败:errorcode=" + errcode + ",errmsg=" + errmsg);
            }
        }
        return jsonObject;
    }

    public void authWXUser(String userid) {
        String corpid = Globals.WxCorpID;
        if(Strings.isBlank(corpid)){
            throw new ValidatException("请现在系统中维护系统参数WxCorpID");
        }
        List<Sys_wx> list = this.query(Cnd.where("agentid", "=", "memo").and("corpid","=",corpid));
        if(list.size() != 1 ){
            throw new ValidatException("请在系统中选择应用和WxCorpID");
        }
        Sys_wx sysWx = list.get(0);
        String token = sysWx.getToken();
        if(!isTokenValid(sysWx)){
            token = updateToken(sysWx);
        }
        //二次验证
        String authUrl = "https://qyapi.weixin.qq.com/cgi-bin/user/authsucc?access_token="+token+"&userid="+userid;
        HttpClientUtil httpClientUtil = new HttpClientUtil();
        String ret = httpClientUtil.doGet(authUrl, "UTF-8");
        JSONObject jsonObject =JSONObject.parseObject(ret);
        if(jsonObject !=null){
            int errcode = jsonObject.getIntValue("errcode");
            String errmsg = jsonObject.getString("errmsg");
            //userid
            if(errcode != 0){
                throw new ValidatException("获得用户失败:errorcode=" + errcode + ",errmsg=" + errmsg);
            }
        }
    }
}
