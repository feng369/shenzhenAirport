package cn.wizzer.app.web.modules.controllers.platform.base;

import cn.wizzer.framework.base.Result;
import cn.wizzer.app.web.commons.slog.annotation.SLog;
import cn.wizzer.framework.page.datatable.DataTableColumn;
import cn.wizzer.framework.page.datatable.DataTableOrder;
import cn.wizzer.framework.util.StringUtil;
import cn.wizzer.app.base.modules.models.base_rowofficeentry;
import cn.wizzer.app.base.modules.services.BaseRowofficeentryService;
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
@At("/platform/base/rowofficeentry")
public class BaseRowofficeentryController{
    private static final Log log = Logs.get();
    @Inject
    private BaseRowofficeentryService baseRowofficeentryService;

    @At("")
    @Ok("beetl:/platform/base/rowofficeentry/index.html")
    @RequiresPermissions("platform.base.rowofficeentry")
    public void index() {
    }

    @At("/data")
    @Ok("json")
    @RequiresPermissions("platform.base.rowofficeentry")
    public Object data(@Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns) {
		Cnd cnd = Cnd.NEW();
    	return baseRowofficeentryService.data(length, start, draw, order, columns, cnd, null);
    }

    @At("/add")
    @Ok("beetl:/platform/base/rowofficeentry/add.html")
    @RequiresPermissions("platform.base.rowofficeentry")
    public void add() {

    }

    @At("/addDo")
    @Ok("json")
    @RequiresPermissions("platform.base.rowofficeentry.add")
    @SLog(tag = "base_rowofficeentry", msg = "${args[0].id}")
    public Object addDo(@Param("..")base_rowofficeentry baseRowofficeentry, HttpServletRequest req) {
		try {
			baseRowofficeentryService.insert(baseRowofficeentry);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }

    @At("/edit/?")
    @Ok("beetl:/platform/base/rowofficeentry/edit.html")
    @RequiresPermissions("platform.base.rowofficeentry")
    public void edit(String id,HttpServletRequest req) {
		req.setAttribute("obj", baseRowofficeentryService.fetch(id));
    }

    @At("/editDo")
    @Ok("json")
    @RequiresPermissions("platform.base.rowofficeentry.edit")
    @SLog(tag = "base_rowofficeentry", msg = "${args[0].id}")
    public Object editDo(@Param("..")base_rowofficeentry baseRowofficeentry, HttpServletRequest req) {
		try {
            baseRowofficeentry.setOpBy(StringUtil.getUid());
			baseRowofficeentry.setOpAt((int) (System.currentTimeMillis() / 1000));
			baseRowofficeentryService.updateIgnoreNull(baseRowofficeentry);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }

    @At({"/delete/?", "/delete"})
    @Ok("json")
    @RequiresPermissions("platform.base.rowoffice.delete")
    @SLog(tag = "base_rowofficeentry", msg = "${req.getAttribute('id')}")
    public Object delete(String id, @Param("ids")  String[] ids, HttpServletRequest req) {
		try {
			if(ids!=null&&ids.length>0){
				baseRowofficeentryService.delete(ids);
    			req.setAttribute("id", org.apache.shiro.util.StringUtils.toString(ids));
			}else{
				baseRowofficeentryService.delete(id);
    			req.setAttribute("id", id);
			}
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }

    @At("/detail/?")
    @Ok("beetl:/platform/base/rowofficeentry/detail.html")
    @RequiresPermissions("platform.base.rowofficeentry")
	public void detail(String id, HttpServletRequest req) {
		if (!Strings.isBlank(id)) {
            req.setAttribute("obj", baseRowofficeentryService.fetch(id));
		}else{
            req.setAttribute("obj", null);
        }
    }

}
