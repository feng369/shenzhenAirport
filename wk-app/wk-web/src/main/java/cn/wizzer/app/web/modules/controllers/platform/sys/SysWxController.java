package cn.wizzer.app.web.modules.controllers.platform.sys;

import cn.wizzer.framework.base.Result;
import cn.wizzer.app.web.commons.slog.annotation.SLog;
import cn.wizzer.framework.base.ValidatException;
import cn.wizzer.framework.page.datatable.DataTableColumn;
import cn.wizzer.framework.page.datatable.DataTableOrder;
import cn.wizzer.framework.util.StringUtil;
import cn.wizzer.app.sys.modules.models.Sys_wx;
import cn.wizzer.app.sys.modules.services.SysWxService;
import org.apache.commons.lang.StringUtils;
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
@At("/platform/sys/wx/token")
public class SysWxController{
    private static final Log log = Logs.get();
    @Inject
    private SysWxService sysWxService;

    @At("")
    @Ok("beetl:/platform/sys/wx/token/index.html")
    @RequiresPermissions("platform.sys.wx.token.select")
    public void index() {
    }

    @At("/data")
    @Ok("json:full")
    @RequiresPermissions("platform.sys.wx.token.select")
    public Object data(@Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns) {
		Cnd cnd = Cnd.NEW();
    	return sysWxService.data(length, start, draw, order, columns, cnd, null);
    }

    @At("/add")
    @Ok("beetl:/platform/sys/wx/token/add.html")
    @RequiresPermissions("platform.sys.wx.token")
    public void add() {

    }

    @At("/addDo")
    @Ok("json")
    @RequiresPermissions("platform.sys.wx.token.add")
    @SLog(tag = "企业微信Token", msg = "${args[0].id}")
    public Object addDo(@Param("..")Sys_wx sysWx, HttpServletRequest req) {
		try {
			sysWxService.insert(sysWx);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }

    @At("/edit/?")
    @Ok("beetl:/platform/sys/wx/token/edit.html")
    @RequiresPermissions("platform.sys.wx.token")
    public void edit(String id,HttpServletRequest req) {
		req.setAttribute("obj", sysWxService.fetch(id));
    }

    @At("/editDo")
    @Ok("json")
    @RequiresPermissions("platform.sys.wx.token.edit")
    @SLog(tag = "企业微信Token", msg = "${args[0].id}")
    public Object editDo(@Param("..")Sys_wx sysWx, HttpServletRequest req) {
		try {
            sysWx.setOpBy(StringUtil.getUid());
			sysWx.setOpAt((int) (System.currentTimeMillis() / 1000));
			sysWxService.updateIgnoreNull(sysWx);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }

    @At({"/delete/?", "/delete"})
    @Ok("json")
    @RequiresPermissions("platform.sys.wx.token.delete")
    @SLog(tag = "企业微信Token", msg = "${req.getAttribute('id')}")
    public Object delete(String id, @Param("ids")  String[] ids, HttpServletRequest req) {
		try {
			if(ids!=null&&ids.length>0){
				sysWxService.delete(ids);
    			req.setAttribute("id", org.apache.shiro.util.StringUtils.toString(ids));
			}else{
				sysWxService.delete(id);
    			req.setAttribute("id", id);
			}
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }

    @At("/detail/?")
    @Ok("beetl:/platform/sys/wx/token/detail.html")
    @RequiresPermissions("platform.sys.wx.token.select")
	public void detail(String id, HttpServletRequest req) {
        		if (!Strings.isBlank(id)) {
            req.setAttribute("obj", sysWxService.fetch(id));
		}else{
            req.setAttribute("obj", null);
        }
    }
    @At("/updateToken")
    @Ok("json")
    @RequiresPermissions("platform.sys.wx.token.update")
    public Object updateToken(@Param("id")String id,HttpServletRequest req){
        try{
            if(StringUtils.isNotBlank(id)){
                Sys_wx wx = sysWxService.fetch(id);
                Cnd cnd = Cnd.NEW();
                cnd.and("agentid","=",wx.getAgentid());
                sysWxService.getTokenOrNew(cnd);
                return Result.success("system.success");
            }
            throw new ValidatException("请求参数不合法！");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("system.error",e);
        }
    }

}
