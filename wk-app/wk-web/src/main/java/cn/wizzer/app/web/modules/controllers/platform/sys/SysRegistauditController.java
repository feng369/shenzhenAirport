package cn.wizzer.app.web.modules.controllers.platform.sys;

import cn.wizzer.app.sys.modules.models.Sys_tempuser;
import cn.wizzer.app.sys.modules.models.Sys_unit;
import cn.wizzer.app.sys.modules.models.Sys_user;
import cn.wizzer.app.sys.modules.services.SysTempuserService;
import cn.wizzer.app.sys.modules.services.SysUnitService;
import cn.wizzer.app.sys.modules.services.SysUserService;
import cn.wizzer.framework.base.Result;
import cn.wizzer.framework.page.datatable.DataTableColumn;
import cn.wizzer.framework.page.datatable.DataTableOrder;
import cn.wizzer.app.sys.modules.models.Sys_registaudit;
import cn.wizzer.app.sys.modules.services.SysRegistauditService;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.dao.Cnd;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

@IocBean
@At("/platform/sys/registaudit")
public class SysRegistauditController{
    private static final Log log = Logs.get();
    @Inject
    private SysRegistauditService sysRegistauditService;
    @Inject
    private SysTempuserService sysTempuserService;
    @Inject
    private SysUnitService sysUnitService;
    @Inject
    private SysUserService sysUserService;

    @At("")
    @Ok("beetl:/platform/sys/registaudit/index.html")
    @RequiresPermissions("sys.manager.registaudit")
    public void index() {
    }

    @At("/data")
    @Ok("json:full")
    @RequiresPermissions("sys.manager.registaudit")
    public Object data(@Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns) {
		Cnd cnd = Cnd.NEW();
    	return sysRegistauditService.data(length, start, draw, order, columns, cnd, null);
    }
    @At("/getRegistList")
    @Ok("json:full")
    @RequiresPermissions("sys.manager.registaudit")
    public NutMap getRegistList(@Param("loginname")String loginname,@Param("username")String username,@Param("tel")String tel,@Param("registTime")String registTime,@Param("result")int result, @Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns) {
    	return sysRegistauditService.getRegistList(result,length, start, draw, order, columns, Cnd.NEW(),loginname,username,tel,registTime);
    }



    //手机注册点击审核操作
    @At("/phoneauditDo")
    @Ok("json:full")
    @RequiresPermissions("sys.manager.registaudit")
    public Object phoneauditDo(@Param("..") Sys_registaudit sysRegistaudit,@Param("..")Sys_tempuser tempuser, HttpServletRequest req) {
        try {
            sysRegistauditService.phoneauditDo(sysRegistaudit,tempuser);
            return Result.success("完成审核");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("审核错误");
        }
    }
    //进入手机注册审核页面
    @At("/phoneaudit/?")
    @Ok("beetl:/platform/sys/registaudit/phoneaudit.html")
    @RequiresAuthentication
    public void phoneaudit(@Param("id") String id, HttpServletRequest req) {
        Sys_registaudit sysRegistaudit = sysRegistauditService.fetch(id);
        if(sysRegistaudit!=null){
            sysRegistauditService.toAuditPage(sysRegistaudit);
            req.setAttribute("sysRegistaudit", sysRegistaudit);
            getRegistInfo(sysRegistaudit.getTempuserId(),true,req);
        }
    }

    //进入手机注册查看页面
    @At("/phonedetail/?")
    @Ok("beetl:/platform/sys/registaudit/phonedetail.html")
    @RequiresAuthentication
    public void phonedetail(@Param("id") String id, HttpServletRequest req) {
        Sys_registaudit sysRegistaudit = sysRegistauditService.fetch(id);
        if(sysRegistaudit!=null){
            req.setAttribute("sysRegistaudit", sysRegistaudit);
            if(!Strings.isBlank(sysRegistaudit.getAuditorId())){
                Sys_user sysUser = sysUserService.fetch(sysRegistaudit.getAuditorId());
                if(sysUser != null){
                    req.setAttribute("auditor",sysUser);
                }
            }
            getRegistInfo(sysRegistaudit.getTempuserId(),true,req);
        }
    }

    //注册信息
    private void getRegistInfo(String tempuserId,boolean isPhone,HttpServletRequest req) {
        if (!Strings.isBlank(tempuserId)) {
            Sys_tempuser tempuser = sysTempuserService.fetch(tempuserId);
            if(tempuser != null){
                if(isPhone){
                     req.setAttribute("tempuser",tempuser);
                    String picture = tempuser.getPicture();
                    if(!Strings.isBlank(picture)){
                        req.setAttribute("imgPath",picture);
                    }
                    if(!Strings.isBlank(tempuser.getUnitid())){
                        Sys_unit sysUnit = sysUnitService.fetch(tempuser.getUnitid());
                        if(sysUnit != null){
                           req.setAttribute("unitName",sysUnit.getName());
                        }
                    }

                }
            }
        }
    }
}
