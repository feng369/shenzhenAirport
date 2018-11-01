package cn.wizzer.app.web.modules.controllers.open.api.mobile;

import cn.wizzer.app.base.modules.models.base_person;
import cn.wizzer.app.base.modules.services.BaseCnctobjService;
import cn.wizzer.app.base.modules.services.BasePersonService;
import cn.wizzer.app.sys.modules.models.*;
import cn.wizzer.app.sys.modules.services.*;
import cn.wizzer.app.web.commons.base.Globals;
import cn.wizzer.app.web.commons.filter.TokenFilter;
import cn.wizzer.app.web.commons.shiro.filter.PlatformAuthenticationFilter;
import cn.wizzer.app.web.commons.slog.SLogService;
import cn.wizzer.app.web.modules.controllers.open.api.Datatime.newDataTime;
import cn.wizzer.framework.base.Result;
import cn.wizzer.framework.base.ValidatException;
import cn.wizzer.framework.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.session.SessionException;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.lang.random.R;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.*;
import org.nutz.mvc.filter.CrossOriginFilter;
import org.nutz.mvc.impl.AdaptorErrorContext;
import org.nutz.plugins.apidoc.annotation.Api;
import org.nutz.plugins.apidoc.annotation.ApiMatchMode;
import org.nutz.plugins.apidoc.annotation.ApiParam;
import org.nutz.plugins.apidoc.annotation.ReturnKey;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

@IocBean
@At("/open/mobile/sys")
@Filters({@By(type = TokenFilter.class)})
@Api(name = "系统信息API", match = ApiMatchMode.ALL,description="用户登录、系统版本等无需token控制接口")
public class MobileSysController {
    private static final Log log = Logs.get();
    @Inject
    private SysVersionService sysVersionService;
    @Inject
    private SysApiService apiService;
    @Inject
    private SysUserService sysUserService;
    @Inject
    private SLogService sLogService;
    @Inject
    private SysMobileService sysMobileService;
    @Inject
    private BaseCnctobjService baseCnctobjService;
    @Inject
    private BasePersonService basePersonService;
    @Inject
    private SysTempuserService sysTempuserService;
    @Inject
    private SysRegistauditService sysRegistauditService;


    @Inject
    private Dao dao;

    @At("/getVersion")
    @Ok("json")
    @Filters({@By(type=CrossOriginFilter.class)})
    @Api(name="获取版本号",params ={}, ok={@ReturnKey(key="name",description="使用名称"),@ReturnKey(key="version",description="版本号"),@ReturnKey(key="wgt",description="wgt号")})
    public Object getVersion( @Param("name")String name){
        try{
            Cnd cnd = Cnd.NEW();
            cnd.and("name","=",name);
            List<Sys_version> svList = sysVersionService.query(cnd);
            HashMap map=new HashMap();
            if(svList.size()>0){
                return Result.success("system.success",svList.get(0));
            }
            throw new ValidatException("未找到版本号信息");
        }catch(Exception e){
            e.printStackTrace();
            return Result.error("system.error",e);
        }
    }
    @At("/editVersion")
    @Ok("json")
    @Filters({@By(type=CrossOriginFilter.class)})
    @Api(name="修改版本号",params = {
            @ApiParam(name = "name", type = "String", description = "版本名称")
            , @ApiParam(name = "newVersion", type = "String", description = "新版本号")
            , @ApiParam(name = "wgt", type = "boolean", description = "1大更新(IOS 强制更新)  0小更新")
    }, ok={@ReturnKey(key="code",description="code=0修改成功")})
    public Object editVersion(@Param("name")String name,@Param("newVersion")String newVersion,@Param("wgt")boolean wgt){
        try{
            sysVersionService.editVersion(name,newVersion,wgt);
            return Result.success("system.success");
        }catch(Exception e){
            return Result.error(e.getMessage());
        }
    }

    @At("/doLoginMobile")
    @Ok("json")
    @Filters({@By(type = PlatformAuthenticationFilter.class)})
    @Api(name="手机登录",params ={
            @ApiParam(name = "username", type = "String", description = "用户名")
            , @ApiParam(name = "password", type = "String", description = "密码")
    }, ok={
            @ReturnKey(key="userId",description="登录用户id")
            ,@ReturnKey(key="appid",description="appid")
            ,@ReturnKey(key="appsecret",description="appsecret")
    })
    public Object doLoginMobile(@Attr("loginToken") AuthenticationToken token, HttpServletRequest req, HttpSession session)
    {
        int errCount = 0;
        try {

            Subject subject = SecurityUtils.getSubject();
            ThreadContext.bind(subject);
            subject.login(token);
            Sys_user user = (Sys_user) subject.getPrincipal();
            int count = user.getLoginCount() == null ? 0 : user.getLoginCount();
            sysUserService.update(Chain.make("loginIp", user.getLoginIp()).add("loginAt", (int) (System.currentTimeMillis() / 1000))
                            .add("loginCount", count + 1).add("isOnline", true)
                    , Cnd.where("id", "=", user.getId()));
            Sys_log sysLog = new Sys_log();
            sysLog.setType("info");
            sysLog.setTag("用户登陆");
            sysLog.setSrc(this.getClass().getName() + "#doLogin");
            sysLog.setMsg("成功登录系统！");
            sysLog.setIp(StringUtil.getRemoteAddr());
            sysLog.setOpBy(user.getId());
            sysLog.setOpAt((int) (System.currentTimeMillis() / 1000));
            sysLog.setUsername(user.getUsername());
            sLogService.async(sysLog);
            //添加生成token的参数20180516
            List<Sys_api> apiList = apiService.query();
            Sys_api api = null;
            if(apiList.size()>0){
                api = apiList.get(0);
            }else{
                return Result.error(403, "服务器端未启用移动应用!");//需要往Sys_api表中加一条数据即可使用
            }
            Map map = new HashMap<>();
            map.put("userId",user.getId());
            map.put("appid",api.getAppId());
            map.put("appsecret",api.getAppSecret());
            return Result.success("login.success",map);
        }  catch (LockedAccountException e) {
            return Result.error(3, "login.error.locked");
        } catch (UnknownAccountException e) {
            errCount++;
            SecurityUtils.getSubject().getSession(true).setAttribute("platformErrCount", errCount);
            return Result.error(4, "login.error.user");
        } catch (AuthenticationException e) {
            errCount++;
            SecurityUtils.getSubject().getSession(true).setAttribute("platformErrCount", errCount);
            return Result.error(5, "login.error.user");
        } catch (Exception e) {
            errCount++;
            SecurityUtils.getSubject().getSession(true).setAttribute("platformErrCount", errCount);
            return Result.error(6, "login.error.system");
        }
    }

    @At("/getDeviceInfo")
    @Ok("json")
    @Filters({@By(type=CrossOriginFilter.class)})
    @Api(name="得到用户登录设备信息",params ={
            @ApiParam(name = "deviceid", type = "String", description = "设备id")
    }, ok={
            @ReturnKey(key="account",description="账号")
            ,@ReturnKey(key="password",description="密码")
            ,@ReturnKey(key="vehicleid",description="车辆id")
            ,@ReturnKey(key="vehiclenum",description="车辆编码")
    })
    public Object getDeviceInfo(@Param("deviceid") String deviceid){
        try{
            return sysMobileService.getDeviceInfo(deviceid);
        }catch(Exception e){
            e.printStackTrace();
            return Result.error("system.error",e);
        }
    }


    @At("/setDeviceLogin")
    @Ok("json")
    @Filters({@By(type=CrossOriginFilter.class)})
    @Api(name = "设置用户登录设备信息"
            , params = {@ApiParam(name = "deviceid", type = "String", description = "设备ID，非必填，有ID且存在时则表明update",optional = true)
            ,@ApiParam(name = "deviceOS", type = "String", description = "设备系统",optional = true)
            ,@ApiParam(name = "deviceModel", type = "String", description = "设备型号",optional = true)
            ,@ApiParam(name = "userid", type = "String", description = "用户ID")
            ,@ApiParam(name = "expire", type = "int", description = "有效期(天数)",optional = true)
            ,@ApiParam(name = "pstatus", type = "String", description = "状态(0)")
            ,@ApiParam(name = "account", type = "String", description = "账户")
            ,@ApiParam(name = "password", type = "String", description = "简单加密后的密码")}
            , ok = {@ReturnKey(key = "code", description = "成功code=0")}
    )
    public Object setDeviceLogin(@Param("deviceid") String deviceid,@Param("deviceOS") String deviceOS,@Param("deviceModel") String deviceModel,@Param("userid") String userid,@Param("expire") String expire
            ,@Param("pstatus") String pstatus,@Param("account") String account,@Param("password") String password)
    {
        boolean insert=false;
        try {
            sysMobileService.setDeviceLogin(insert,deviceid,deviceOS,deviceModel,userid,expire,pstatus,account,password);
            return Result.success("system.success");
        }catch(Exception e){
            e.printStackTrace();
            return Result.error("system.error",e);
        }
    }


    //20180601zhf1820
    //需求端
    //手机地图缩放
    @At("/getH5AppZoom")
    @Ok("json")
    @Api(name = "App地图缩放比例(适用于H5)"
            , params = {}
            , ok = {@ReturnKey(key = "zoom", description = "地图缩放比例")}
    )
    @Filters({@By(type=CrossOriginFilter.class),@By(type = TokenFilter.class)})
    public Object getH5AppZoom(){
        try{
            HashMap map =new HashMap();
            map.put("zoom", Globals.H5AppZoom);
            return Result.success("system.success",map);
        }catch(Exception e){
            return Result.error("system.error",e);
        }
    }

    //20180601zhf1820
    @At("/getAppZoom")
    @Ok("json")
    @Api(name = "App地图缩放比例(适用于IOS)"
            , params = {}
            , ok = {@ReturnKey(key = "zoom", description = "地图缩放比例")}
    )
    @Filters({@By(type=CrossOriginFilter.class),@By(type = TokenFilter.class)})
    public Object getAppZoom(){
        try{
            HashMap map =new HashMap();
            map.put("zoom",Globals.AppZoom);
            return Result.success("system.success",map);
        }catch(Exception e){
            return Result.error("system.error",e);
        }
    }

    @At("/getRoleByUserId")
    @Ok("json")
    @Filters({@By(type=CrossOriginFilter.class),@By(type = TokenFilter.class)})
    @Api(name = "通过userid获得角色名称"
            , params = {@ApiParam(name = "userid", type = "String", description = "用户id")}
            , ok = {@ReturnKey(key = "roleName", description = "角色名称")}
    )
    public Object getRoleByUserId(@Param("userid")String userid){
        try{
            if(!Strings.isBlank(userid)){
                Sys_user sysUser = dao.fetchLinks(dao.fetch(Sys_user.class, userid), "roles");
                List<Sys_role> roles = sysUser.getRoles();
                if(roles.size() > 0 ){
                    HashMap map=new HashMap();
                    map.put("roleName",roles.get(0).getName());
                    return  Result.success("system.success",map);
                }
                return Result.error(2,"roles is null");
            }
            return Result.error(2,"userid is null");
        }
        catch (Exception e){
            e.printStackTrace();
            return Result.error("system.error",e);
        }
    }


    /**
     * 退出系统
     */
    @At("/logoutBymobile")
    @Ok("json")
    @Filters({@By(type=CrossOriginFilter.class)})
    @Api(name = "(需求端)退出系统"
            , params = {}
            , ok = {@ReturnKey(key = "roleName", description = "角色名称")}
    )
    public Object logoutBymobile(HttpSession session) {
        try {
            Subject currentUser = SecurityUtils.getSubject();
            Sys_user user = (Sys_user) currentUser.getPrincipal();
            currentUser.logout();
            if (user != null) {
                sysUserService.logoutByMobile(user);
            }
            return Result.success("system.success");
        } catch (SessionException ise) {
            log.debug("Encountered session exception during logout.  This can generally safely be ignored.", ise);
            ise.printStackTrace();
            return Result.error("system.error",ise);
        } catch (Exception e) {
            log.debug("Logout error", e);
            e.printStackTrace();
            return Result.error("system.error",e);
        }
    }



    @At("/addUserByMobile")
    @Ok("json")
    @Filters({@By(type=CrossOriginFilter.class)})
    @Api(name = "(需求端:手机注册)"
            , params = {
            @ApiParam(name = "loginname", type = "String", description = "用户名(显示请填写工号)"),
            @ApiParam(name = "username", type = "String", description = "真实姓名"),
            @ApiParam(name = "password", type = "String", description = "密码"),
            @ApiParam(name = "wxNumber", type = "String", description = "微信号"),
            @ApiParam(name = "tel", type = "String", description = "电话号码"),
//            @ApiParam(name = "typePhone", type = "Integer", description = "移动端类型 安卓传1,IOS暂不传",optional = true),
    }
            , ok = {
            @ReturnKey(key = "code", description = "0操作成功,等待后台审核,2该用户已经注册3此用户名正在注册审核中"),
            @ReturnKey(key = "tempuserId", description = "注册临时对象ID")
    }
    )
    public Object addUserByMobile(@Param("username") String username, @Param("loginname") String loginname,@Param("password") String password,@Param("tel") String tel,@Param("wxNumber")String wxNumber, HttpServletRequest req) {
        try{
            req.setCharacterEncoding("UTF-8");
            /*if(typePhone != 1){
                username  = new String(username.getBytes("ISO-8859-1"),"UTF-8");
            }*/
            Sys_tempuser tempuser = sysTempuserService.fetch(Cnd.where("loginname", "=", loginname));
            if(!isPhone(tel)){
                return Result.error("电话号码格式不正确");
            }
            Sys_tempuser tp = sysTempuserService.fetch(Cnd.where("tel", "=", tel));
            if(tp != null){
                Sys_registaudit sysRegistaudit = sysRegistauditService.fetch(Cnd.where("result", "=", -1).and("tempuserId", "=", tp.getId()));
                if(sysRegistaudit != null){
                    return Result.error("该电话号码正在注册审核中,请耐心等待!");
                }
            }
            int count = basePersonService.count(Cnd.where("tel", "=", tel));
            if(count > 0 ){
                return Result.error("该电话号码已经注册过");
            }
            if(tempuser != null){
                Sys_registaudit sysRegistaudit = sysRegistauditService.fetch(Cnd.where("result", "=", -1).and("tempuserId", "=", tempuser.getId()));
                if(sysRegistaudit != null){
                    return Result.error(3,"此用户名正在注册审核中");
                }
            }
            Sys_user sys_user = sysUserService.fetch(Cnd.where("loginname", "=", loginname));

            if(sys_user !=null){
                return Result.error(2,"该用户已经注册");
            }
            //填写的用户名是
            String  jobNumber = loginname;
            /*
            List<base_person> basePersonList = basePersonService.query(Cnd.where("jobNumber", "=", jobNumber).and("customerId", "=",Globals.cutomerid));
            if(basePersonList.size() > 0 ){
                int count = baseCnctobjService.count(Cnd.where("personId", "=", basePersonList.get(0).getId()));
                if(count > 0){
                    //该证件号已经注册过
                    return Result.error(2,"该员工已经注册过");
                }
            }*/

            return  Result.success("system.success",sysUserService.addUserByMobile(Globals.customerid, username, loginname,password,jobNumber,tel,wxNumber));
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("system.error",e);
        }
    }


    @At("/checkLoginname")
    @Ok("json")
    @Filters({@By(type=CrossOriginFilter.class)})
    @Api(name = "(需求端)检查用户名重复"
            , params = {
            @ApiParam(name = "loginname", type = "String", description = "用户名"),
    }
            , ok = {@ReturnKey(key = "result", description = "此用户名:OK不重复,Fail存在重复")}
    )
    public Object checkLoginnameByMobile(@Param("loginname") String loginname){
        try{
            Cnd cnd=Cnd.NEW();
            Map map = new HashMap();
            cnd.and("loginname","=",loginname);
            Sys_user user=sysUserService.fetch(cnd);
            if(user==null){
                map.put("result","OK");
            }
            else{
                map.put("result","Fail");
            }
            return Result.success("system.success",map);
        }catch (Exception e){
            return Result.error("system.error",e);
        }
    }
    //20180424zhf1814
  /*  @At("/checkPersonByPermitNumber")
    @Ok("json")
    @Filters({@By(type=CrossOriginFilter.class)})
    @Api(name = "(需求端)通过通行证号检查该人员是否已经注册"
            , params = {
            @ApiParam(name = "permitNumber", type = "String", description = "通行证号"),
    }
            , ok = {@ReturnKey(key = "result", description = "此员工:OK未注册,Fail已注册")}
    )
    public Object checkPersonByPermitNumber(@Param("permitNumber") String permitNumber){
        try{
            Map map = new HashMap();
            List<base_person> basePersonList = basePersonService.query(Cnd.where("permitNumber", "=", permitNumber).and("customerId", "=",Globals.cutomerid));
            if(basePersonList.size() > 0 ){
                int count = baseCnctobjService.count(Cnd.where("personId", "=", basePersonList.get(0).getId()));
                if(count > 0){
                    //该证件号已经注册过
                    map.put("result","OK");
                }else{
                    map.put("result","Fail");
                }
            }else{
                map.put("result","OK");
            }
            return Result.success("system.success",map);
        }catch (Exception e){
            return Result.error("system.error",e);
        }
    }*/



    @At("/doChangePassword")
    @Ok("json")
    @Api(name = "(需求端)更改密码" , params = {
            @ApiParam(name = "userId", type = "String", description = "用户ID"),
            @ApiParam(name = "oldPassword", type = "String", description = "老密码"),
            @ApiParam(name = "newPassword", type = "String", description = "新密码"),
    }, ok = {
            @ReturnKey(key = "code",  description = "0修改成功,1对应返回信息(原密码不正确/操作失败)"),
    }
    )
    @Filters({@By(type = CrossOriginFilter.class), @By(type = TokenFilter.class)})
    public Object doChangePassword(@Param("userId")String userId,@Param("oldPassword") String oldPassword, @Param("newPassword") String newPassword, HttpServletRequest req) {
        try {
            Sys_user user = sysUserService.fetch(userId);
            if (user != null) {
                String old = new Sha256Hash(oldPassword, user.getSalt(), 1024).toBase64();
                if (old.equals(user.getPassword())) {
                    sysUserService.doChangePassword(user,newPassword);
                    return Result.success("修改成功");
                }
                return Result.error("原密码不正确");
            }
            return Result.error("system.error");
        }catch (Exception e){
            return Result.error("system.error");
        }
    }

    @At("/uploadImage")
    @Ok("json")
    //AdaptorErrorContext必须是最后一个参数
    @Filters({@By(type=CrossOriginFilter.class)})
    @Api(name = "用户注册上传图片" , params = {
            @ApiParam(name = "filename", type = "String", description = "图片名称"),
            @ApiParam(name = "base64", type = "String", description = "图片base64"),
            @ApiParam(name = "tempuserId", type = "String", description = "临时注册对象ID"),
    }, ok = {
            @ReturnKey(key = "code",  description = "上传成功"),
    }
    )
    public Object uploadImage(@Param("filename") String filename, @Param("base64") String base64,@Param("tempuserId") String tempuserId,HttpServletRequest req, AdaptorErrorContext err) {
        byte[] buffer=null;
        try {
            if (StringUtils.isBlank(base64)) {
                return Result.error("传入文件不能为空！");
            }else if (err != null && err.getAdaptorErr() != null) {
                return Result.error("传入文件不能为空！");
            } else if (StringUtils.isBlank(tempuserId)) {
                return Result.error("传入注册信息不能为空!");
            } else {
                Sys_tempuser tempuser = sysTempuserService.fetch(tempuserId);
                if(tempuser==null){
                    return Result.error("系统未找到相关注册信息!");
                }
                String fn= R.UU32()+filename.substring(filename.lastIndexOf("."));
                String path = Globals.AppUploadPath+"/userCard/";
                String pathfile = Globals.AppUploadPath+"/userCard/" + fn ;
                File file=new File(Globals.AppRoot+path);
                if(!file.exists()){
                    file.mkdirs();
                }
                if(base64.indexOf(",")>=0){//兼容H5
                    buffer = Base64.getDecoder().decode(base64.split(",")[1]);
                }else{
//                    buffer = Base64.getDecoder().decode(base64);
                    buffer = org.apache.commons.codec.binary.Base64.decodeBase64(base64);
                }
                FileOutputStream out = new FileOutputStream(Globals.AppRoot+pathfile);
                out.write(buffer);
                out.close();
                //将上传的文件修改对应用户照片名称
                String picPath=tempuser.getPicture();
                if(!Strings.isBlank(picPath)){
                    picPath+=","+pathfile;
                }else{
                    picPath=pathfile;
                }
                tempuser.setPicture(picPath);
                sysTempuserService.updateIgnoreNull(tempuser);
                return Result.success("上传成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("system.error",e);
        } catch (Throwable e) {
            e.printStackTrace();
            return Result.error("图片错误:"+e.getMessage());
        }
    }
    @At("/uploadUserImage")
    @Ok("json")
    @Filters({@By(type=CrossOriginFilter.class)})
    @Api(name = "上传用户图片" , params = {
            @ApiParam(name = "filename", type = "String", description = "图片名称"),
            @ApiParam(name = "base64", type = "String", description = "图片base64"),
            @ApiParam(name = "userId", type = "String", description = "用户ID"),
    }, ok = {
            @ReturnKey(key = "code",  description = "上传成功"),
    }
    )
    public Object uploadUserImage(@Param("filename") String filename, @Param("base64") String base64,@Param("userId") String userId,HttpServletRequest req, AdaptorErrorContext err) {
        byte[] buffer=null;
        try {
            if (StringUtils.isBlank(base64)) {
                return Result.error("传入文件不能下单人为空！");
            }else if (err != null && err.getAdaptorErr() != null) {
                return Result.error("传入文件不能为空！");
            } else if (StringUtils.isBlank(userId)) {
                return Result.error("传入用户对象ID不能为空!");
            } else {
                Sys_user sysUser = sysUserService.fetch(userId);
                if(sysUser==null){
                    return Result.error("系统未找到相关对象!");
                }
               sysUserService.uploadUserImage(filename,base64,sysUser,buffer);
                return Result.success("上传成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("system.error",e);
        } catch (Throwable e) {
            e.printStackTrace();
            return Result.error("图片错误:"+e.getMessage());
        }
    }
    //验证电话号码
    private static boolean isPhone(String tel) {
        return Pattern.compile("^[1][3,4,5,6,7,8][0-9]{9}$").matcher(tel).matches();
    }
}
