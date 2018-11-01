package cn.wizzer.app.web.modules.controllers.platform.sys;

import cn.wizzer.framework.base.Result;
import cn.wizzer.app.web.commons.slog.annotation.SLog;
import cn.wizzer.framework.page.datatable.DataTableColumn;
import cn.wizzer.framework.page.datatable.DataTableOrder;
import cn.wizzer.framework.util.StringUtil;
import cn.wizzer.app.sys.modules.models.Sys_wx_dept;
import cn.wizzer.app.sys.modules.services.SysWxDeptService;
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
@At("/platform/sys/wx/dept")
public class SysWxDeptController{
    private static final Log log = Logs.get();
    @Inject
    private SysWxDeptService sysWxDeptService;



    @At("")
    @Ok("beetl:/platform/sys/wx/dept/index.html")
    @RequiresPermissions("platform.sys.wx.dept.select")
    public void index() {
    }

    @At("/data")
    @Ok("json:full")
    @RequiresPermissions("platform.sys.wx.dept.select")
    public Object data(@Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns) {
		Cnd cnd = Cnd.NEW();
    	return sysWxDeptService.data(length, start, draw, order, columns, cnd, null);
    }

    @At("/add")
    @Ok("beetl:/platform/sys/wx/dept/add.html")
    @RequiresPermissions("platform.sys.wx.dept")
    public void add() {

    }

    @At("/addDo")
    @Ok("json")
    @RequiresPermissions("platform.sys.wx.dept.add")
    @SLog(tag = "企业微信部门表", msg = "${args[0].id}")
    public Object addDo(@Param("..")Sys_wx_dept sysWxDept, HttpServletRequest req) {
		try {
			sysWxDeptService.insert(sysWxDept);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }

    @At("/edit/?")
    @Ok("beetl:/platform/sys/wx/dept/edit.html")
    @RequiresPermissions("platform.sys.wx.dept")
    public void edit(String id,HttpServletRequest req) {
		req.setAttribute("obj", sysWxDeptService.fetch(id));
    }

    @At("/editDo")
    @Ok("json")
    @RequiresPermissions("platform.sys.wx.dept.edit")
    @SLog(tag = "企业微信部门表", msg = "${args[0].id}")
    public Object editDo(@Param("..")Sys_wx_dept sysWxDept, HttpServletRequest req) {
		try {
            sysWxDept.setOpBy(StringUtil.getUid());
			sysWxDept.setOpAt((int) (System.currentTimeMillis() / 1000));
			sysWxDeptService.updateIgnoreNull(sysWxDept);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }

    @At({"/delete/?", "/delete"})
    @Ok("json")
    @RequiresPermissions("platform.sys.wx.dept.delete")
    @SLog(tag = "企业微信部门表", msg = "${req.getAttribute('id')}")
    public Object delete(String id, @Param("ids")  String[] ids, HttpServletRequest req) {
		try {
			if(ids!=null&&ids.length>0){
				sysWxDeptService.delete(ids);
    			req.setAttribute("id", org.apache.shiro.util.StringUtils.toString(ids));
			}else{
				sysWxDeptService.delete(id);
    			req.setAttribute("id", id);
			}
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }

    @At("/detail/?")
    @Ok("beetl:/platform/sys/wx/dept/detail.html")
    @RequiresPermissions("platform.sys.wx.dept.select")
	public void detail(String id, HttpServletRequest req) {
        		if (!Strings.isBlank(id)) {
            req.setAttribute("obj", sysWxDeptService.fetch(id));
		}else{
            req.setAttribute("obj", null);
        }}

    @At("/download")
    @Ok("json")
    @RequiresPermissions("platform.sys.wx.dept.download")
    @SLog(tag = "企业微信部门", msg = "下载部门信息")
    public Object download(HttpServletRequest req) {
        try {
            sysWxDeptService.download();
            return Result.success("system.success");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("system.error",e);
        }
    }


}
