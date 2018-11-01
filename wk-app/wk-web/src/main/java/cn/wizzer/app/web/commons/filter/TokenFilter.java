package cn.wizzer.app.web.commons.filter;

import cn.wizzer.app.sys.modules.services.SysApiService;
import cn.wizzer.app.sys.modules.services.impl.SysApiServiceImpl;
import cn.wizzer.framework.base.Result;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.nutz.dao.Cnd;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.ActionContext;
import org.nutz.mvc.ActionFilter;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.View;
import org.nutz.mvc.view.UTF8JsonView;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by wizzer on 2016/8/11.
 */
public class TokenFilter implements ActionFilter {
    private static final Log log = Logs.get();
    private SysApiService apiService= Mvcs.ctx().getDefaultIoc().get(SysApiServiceImpl.class);

    public View match(ActionContext context) {
        String happid = Strings.sNull(context.getRequest().getHeader("appid"));
        String htoken = Strings.sNull(context.getRequest().getHeader("token"));
//        String appId = Strings.sNull(context.getRequest().getParameter("appid"));
//        String token = Strings.sNull(context.getRequest().getParameter("token"));
        //打印请求参数
        if(log.isDebugEnabled()) {
            Map<String, String[]> parameterMap = context.getRequest().getParameterMap();
            Map map = new HashMap();
            for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
                map.put(entry.getKey(),entry.getValue());
            }
            JSONObject json=new JSONObject(map);//Json.toJson(map)
            json.remove("base64");//过滤掉附件图片内容的打印，影响性能
            log.debug("TokenFilter收到API请求("+context.getRequest().getContextPath()+context.getPath()+")，header：{happid:"+happid+",htoken:"+htoken+"},参数："+ json);
        }
        Cnd cnd = Cnd.NEW();
        cnd.and("appId","=",happid);
        if(StringUtils.isBlank(happid)|| StringUtils.isBlank(htoken)) {
            return new UTF8JsonView(JsonFormat.compact()).setData(Result.error(406,"appid or token is null"));
        }else if(apiService.count(cnd)!=1){//分布式部署应使用redis时，应该从redis中读取判断，此时建议appid=userid，按用户生成token
            return new UTF8JsonView(JsonFormat.compact()).setData(Result.error(403, "server is forbbidening API invoke!"));//需要往Sys_api表中加一条数据即可使用
        }else if (!apiService.verifyToken(happid, htoken)) {
            return new UTF8JsonView(JsonFormat.compact()).setData(Result.error(401,"appid or token invalid"));
        }
        return null;
    }
}
