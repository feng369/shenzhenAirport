package cn.wizzer.app.web.modules.controllers.open.api.token;

import cn.wizzer.app.sys.modules.models.Sys_api;
import cn.wizzer.app.sys.modules.services.SysApiService;
import cn.wizzer.app.web.commons.base.Globals;
import cn.wizzer.framework.base.Result;
import cn.wizzer.framework.util.DateUtil;
import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.adaptor.JsonAdaptor;
import org.nutz.mvc.annotation.*;
import org.nutz.mvc.filter.CrossOriginFilter;
import org.nutz.plugins.apidoc.annotation.Api;
import org.nutz.plugins.apidoc.annotation.ApiMatchMode;
import org.nutz.plugins.apidoc.annotation.ApiParam;
import org.nutz.plugins.apidoc.annotation.ReturnKey;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by wizzer on 2016/8/11.
 */
@IocBean
@At("/open/api/token")
@Api(name = "Token功能", match = ApiMatchMode.ALL,description="token处理相关接口")
public class TokenController {
    private static final Log log = Logs.get();
    @Inject
    private SysApiService apiService;

    /**
     * @api {post} /open/api/token/get 获取Token
     * @apiGroup Token
     * @apiVersion 1.0.0
     * @apiPermission anyone
     * @apiParam {String}	appId 					appId
     * @apiParam {String}	sign 				appId+appSecret+yyyyMMddHH 计算出的MD5值
     * @apiParamExample {json} 示例
     * POST /open/api/token
     * {
     * "appId": "appId",
     * "sign": "sign"
     * }
     * @apiSuccess {number} code 			         code
     * @apiSuccess {String} msg 			         msg
     * @apiSuccess {Object[]} data 				数据对象
     * @apiSuccess {number} data.expires 			有效期
     * @apiSuccess {String} data.token 			Token
     * @apiSuccessExample {json} 示例
     * HTTP/1.1 200 OK
     * {
     * "code": 0,
     * "msg": "ok",
     * "data": {
     * "token": ""eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0IiwiZXhwIjoxNDcwOTA5OTc4fQ._T7egDYhCL27jCvEv4J0cyjRj8s_YLj2gZjjTA8mzk81mTdeM-JXnH7VmtfaenW33BpJJzs2Hs2sXiiNHdzU6Q",
     * "expires": 7200,
     * }
     * }
     * @apiError (失败) {number} code 不等于0
     * @apiError (失败) {string} msg 错误文字描述
     * @apiErrorExample {json} 示例
     * HTTP/1.1 200 OK
     * {
     * "code": 1
     * "msg": "token invalid"
     * }
     */
    @At("/get")
    @Ok("json")
//    @AdaptBy(type = JsonAdaptor.class)//注释后兼容兼容post和get请求，通过url中添加参数
    @Filters(@By(type=CrossOriginFilter.class))//支持跨域，兼容H5 APP调用，采用cors方式调用
    @Api(name="获取token",params ={
            @ApiParam(name = "appid", type = "String", description = "登录接口返回的appid",optional = true)
            , @ApiParam(name = "sign", type = "String", description = "MD5(appid+appSecret+yyyyMMddHH)",optional = true)
    }, ok={@ReturnKey(key="expires",description="token有效期")
            ,@ReturnKey(key="token",description="token字符串")})
    public Object get(@Param("..") NutMap map) {
        try {
            Sys_api api = apiService.fetch(Cnd.where("appId", "=", map.getString("appid", "")));
            if (api == null)
                return Result.error("appId error");
//            if (!Lang.md5(map.getString("appid", "") + api.getAppSecret() + DateUtil.format(new Date(), "yyyyMMddHH")).equalsIgnoreCase(map.getString("sign", "")))
//                return Result.error("sign error");
            NutMap resmap = new NutMap();
            Date date = new Date();
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            int expires = 0;
            try {
                expires = Integer.valueOf(Globals.APITokenExpire);
            }catch (Exception e){
                log.error("Globals.APITokenExpire is not valid!"+e.getMessage());
                expires = 7200;
            }
            c.add(Calendar.SECOND, +expires);
            date = c.getTime();
            resmap.addv("expires", expires);
            resmap.addv("token", apiService.generateToken(date, map.getString("appid", "")));
            return Result.success("ok", resmap);
        } catch (Exception e) {
            log.error(e.getMessage());
            return Result.error("fail");
        }
    }


//    @At("/getp")
//    @Ok("jsonp:full")//支持JSONP跨域方式调用
//    public Object getp(@Param("appid") String appid,@Param("sign") String sign) {
//        try {
//            Sys_api api = apiService.fetch(Cnd.where("appId", "=",appid));
//            if (api == null)
//                return Result.error("appId error");
//            if (!Lang.md5(appid + api.getAppSecret() + DateUtil.format(new Date(), "yyyyMMddHH")).equalsIgnoreCase(sign))
//                return Result.error("sign error");
//            NutMap resmap = new NutMap();
//            Date date = new Date();
//            Calendar c = Calendar.getInstance();
//            c.setTime(date);
//            c.add(Calendar.HOUR, +2);
//            date = c.getTime();
//            resmap.addv("expires", 7200);
//            resmap.addv("token", apiService.generateToken(date, appid));
//            return Result.success("ok", resmap);
//        } catch (Exception e) {
//            log.debug(e.getMessage());
//            return Result.error("fail");
//        }
//    }
}
