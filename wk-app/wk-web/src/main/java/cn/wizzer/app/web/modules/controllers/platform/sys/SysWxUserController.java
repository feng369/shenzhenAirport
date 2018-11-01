package cn.wizzer.app.web.modules.controllers.platform.sys;

import cn.wizzer.framework.base.Result;
import cn.wizzer.app.web.commons.slog.annotation.SLog;
import cn.wizzer.framework.page.datatable.DataTableColumn;
import cn.wizzer.framework.page.datatable.DataTableOrder;
import cn.wizzer.framework.util.StringUtil;
import cn.wizzer.app.sys.modules.models.Sys_wx_user;
import cn.wizzer.app.sys.modules.services.SysWxUserService;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.dao.Cnd;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@IocBean
@At("/platform/sys/wx/user")
public class SysWxUserController{
    private static final Log log = Logs.get();
    @Inject
    private SysWxUserService sysWxUserService;

    @At("")
    @Ok("beetl:/platform/sys/wx/user/index.html")
    @RequiresPermissions("platform.sys.wx.user.select")
    public void index() {
    }

    @At("/data")
    @Ok("json:full")
    @RequiresPermissions("platform.sys.wx.user.select")
    public Object data(@Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns) {
		Cnd cnd = Cnd.NEW();
    	return sysWxUserService.data(length, start, draw, order, columns, cnd, null);
    }

    @At("/add")
    @Ok("beetl:/platform/sys/wx/user/add.html")
    @RequiresPermissions("platform.sys.wx.user")
    public void add() {

    }

    @At("/addDo")
    @Ok("json")
    @RequiresPermissions("platform.sys.wx.user.add")
    @SLog(tag = "企业微信用户表", msg = "${args[0].id}")
    public Object addDo(@Param("..")Sys_wx_user sysWxUser, HttpServletRequest req) {
		try {
			sysWxUserService.insert(sysWxUser);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }

    @At("/edit/?")
    @Ok("beetl:/platform/sys/wx/user/edit.html")
    @RequiresPermissions("platform.sys.wx.user")
    public void edit(String id,HttpServletRequest req) {
		req.setAttribute("obj", sysWxUserService.fetch(id));
    }

    @At("/editDo")
    @Ok("json")
    @RequiresPermissions("platform.sys.wx.user.edit")
    @SLog(tag = "企业微信用户表", msg = "${args[0].id}")
    public Object editDo(@Param("..")Sys_wx_user sysWxUser, HttpServletRequest req) {
		try {
            sysWxUser.setOpBy(StringUtil.getUid());
			sysWxUser.setOpAt((int) (System.currentTimeMillis() / 1000));
			sysWxUserService.updateIgnoreNull(sysWxUser);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }

    @At({"/delete/?", "/delete"})
    @Ok("json")
    @RequiresPermissions("platform.sys.wx.user.delete")
    @SLog(tag = "企业微信用户表", msg = "${req.getAttribute('id')}")
    public Object delete(String id, @Param("ids")  String[] ids, HttpServletRequest req) {
		try {
			if(ids!=null&&ids.length>0){
				sysWxUserService.delete(ids);
    			req.setAttribute("id", org.apache.shiro.util.StringUtils.toString(ids));
			}else{
				sysWxUserService.delete(id);
    			req.setAttribute("id", id);
			}
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }

    @At("/detail/?")
    @Ok("beetl:/platform/sys/wx/user/detail.html")
    @RequiresPermissions("platform.sys.wx.user.select")
	public void detail(String id, HttpServletRequest req) {
        		if (!Strings.isBlank(id)) {
            req.setAttribute("obj", sysWxUserService.fetch(id));
		}else{
            req.setAttribute("obj", null);
        }}


    @At("/getDatabyParam")
    @Ok("json")
    @RequiresPermissions("platform.sys.wx.user")
    public Object getDatabyParam(@Param("keyword")String keyword,@Param("isBindPerson") String isBindPerson,@Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns){
        try{
            String airportid="";
            if(!Strings.isBlank(isBindPerson)){
//                String personid = baseCnctobjService.getPersonIDByUserID();
//                if(!Strings.isBlank(personid))
//                    airportid = basePersonService.fetch(personid).getAirportid();

                Cnd cnd=Cnd.NEW();
                if(!Strings.isBlank(keyword)){
                    keyword = keyword.replace("'", "");
                    cnd.and("name","LIKE","%" + keyword +"%");
                }
                cnd.and("bindPerson","=",isBindPerson);
//                if(!airportid.equals("")){
//                    cnd.and("airportId","=",airportid);
//                }
                return sysWxUserService.data(length, start, draw, order, columns,cnd,null);
            }
            return null;
        }catch (Exception e){
            return null;
        }

    }

    @At("/download")
    @Ok("json")
    @RequiresPermissions("platform.sys.wx.user.download")
    @SLog(tag = "企业微信用户", msg = "下载企业微信用户")
    public Object download(HttpServletRequest req) {
        try {
            sysWxUserService.download();
            return Result.success("system.success");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("system.error",e);
        }
    }
}
