package cn.wizzer.app.web.modules.controllers.platform.sys;

import cn.wizzer.framework.base.Result;
import cn.wizzer.app.web.commons.slog.annotation.SLog;
import cn.wizzer.framework.page.datatable.DataTableColumn;
import cn.wizzer.framework.page.datatable.DataTableOrder;
import cn.wizzer.framework.util.StringUtil;
import cn.wizzer.app.sys.modules.models.Sys_tempuser;
import cn.wizzer.app.sys.modules.services.SysTempuserService;
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
@At("/platform/sys/tempuser")
public class SysTempuserController{
    private static final Log log = Logs.get();
    @Inject
    private SysTempuserService sysTempuserService;

    @At("")
    @Ok("beetl:/platform/sys/tempuser/index.html")
    @RequiresPermissions("platform.sys.tempuser")
    public void index() {
    }

    @At("/data")
    @Ok("json:full")
    @RequiresPermissions("platform.sys.tempuser")
    public Object data(@Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns) {
		Cnd cnd = Cnd.NEW();
    	return sysTempuserService.data(length, start, draw, order, columns, cnd, null);
    }

    @At("/add")
    @Ok("beetl:/platform/sys/tempuser/add.html")
    @RequiresPermissions("platform.sys.tempuser")
    public void add() {

    }

    @At("/addDo")
    @Ok("json")
    @RequiresPermissions("platform.sys.tempuser.add")
    @SLog(tag = "注册信息临时表", msg = "${args[0].id}")
    public Object addDo(@Param("..")Sys_tempuser sysTempuser, HttpServletRequest req) {
		try {
			sysTempuserService.insert(sysTempuser);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }

    @At("/edit/?")
    @Ok("beetl:/platform/sys/tempuser/edit.html")
    @RequiresPermissions("platform.sys.tempuser")
    public void edit(String id,HttpServletRequest req) {
		req.setAttribute("obj", sysTempuserService.fetch(id));
    }

    @At("/editDo")
    @Ok("json")
    @RequiresPermissions("platform.sys.tempuser.edit")
    @SLog(tag = "注册信息临时表", msg = "${args[0].id}")
    public Object editDo(@Param("..")Sys_tempuser sysTempuser, HttpServletRequest req) {
		try {
            sysTempuser.setOpBy(StringUtil.getUid());
			sysTempuser.setOpAt((int) (System.currentTimeMillis() / 1000));
			sysTempuserService.updateIgnoreNull(sysTempuser);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }

    @At({"/delete/?", "/delete"})
    @Ok("json")
    @RequiresPermissions("platform.sys.tempuser.delete")
    @SLog(tag = "注册信息临时表", msg = "${req.getAttribute('id')}")
    public Object delete(String id, @Param("ids")  String[] ids, HttpServletRequest req) {
		try {
			if(ids!=null&&ids.length>0){
				sysTempuserService.delete(ids);
    			req.setAttribute("id", org.apache.shiro.util.StringUtils.toString(ids));
			}else{
				sysTempuserService.delete(id);
    			req.setAttribute("id", id);
			}
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }

    @At("/detail/?")
    @Ok("beetl:/platform/sys/tempuser/detail.html")
    @RequiresPermissions("platform.sys.tempuser")
	public void detail(String id, HttpServletRequest req) {
        		if (!Strings.isBlank(id)) {
            req.setAttribute("obj", sysTempuserService.fetch(id));
		}else{
            req.setAttribute("obj", null);
        }}

}
