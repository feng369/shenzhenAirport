package cn.wizzer.app.web.modules.controllers.platform.logistics;

import cn.wizzer.framework.base.Result;
import cn.wizzer.app.web.commons.slog.annotation.SLog;
import cn.wizzer.framework.page.datatable.DataTableColumn;
import cn.wizzer.framework.page.datatable.DataTableOrder;
import cn.wizzer.framework.util.StringUtil;
import cn.wizzer.app.logistics.modules.models.logistics_orderplan;
import cn.wizzer.app.logistics.modules.services.LogisticsOrderplanService;
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
@At("/platform/logistics/orderplan")
public class LogisticsOrderplanController{
    private static final Log log = Logs.get();
    @Inject
    private LogisticsOrderplanService logisticsOrderplanService;

    @At("")
    @Ok("beetl:/platform/logistics/orderplan/index.html")
    @RequiresPermissions("platform.logistics.orderplan")
    public void index() {
    }

    @At("/data")
    @Ok("json")
    @RequiresPermissions("platform.logistics.orderplan")
    public Object data(@Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns) {
		Cnd cnd = Cnd.NEW();
    	return logisticsOrderplanService.data(length, start, draw, order, columns, cnd, null);
    }

    @At("/add")
    @Ok("beetl:/platform/logistics/orderplan/add.html")
    @RequiresPermissions("platform.logistics.orderplan")
    public void add() {

    }

    @At("/addDo")
    @Ok("json")
    @RequiresPermissions("platform.logistics.orderplan.add")
    @SLog(tag = "logistics_orderplan", msg = "${args[0].id}")
    public Object addDo(@Param("..")logistics_orderplan logisticsOrderplan, HttpServletRequest req) {
		try {
			logisticsOrderplanService.insert(logisticsOrderplan);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }

    @At("/edit/?")
    @Ok("beetl:/platform/logistics/orderplan/edit.html")
    @RequiresPermissions("platform.logistics.orderplan")
    public void edit(String id,HttpServletRequest req) {
		req.setAttribute("obj", logisticsOrderplanService.fetch(id));
    }

    @At("/editDo")
    @Ok("json")
    @RequiresPermissions("platform.logistics.orderplan.edit")
    @SLog(tag = "logistics_orderplan", msg = "${args[0].id}")
    public Object editDo(@Param("..")logistics_orderplan logisticsOrderplan, HttpServletRequest req) {
		try {
            logisticsOrderplan.setOpBy(StringUtil.getUid());
			logisticsOrderplan.setOpAt((int) (System.currentTimeMillis() / 1000));
			logisticsOrderplanService.updateIgnoreNull(logisticsOrderplan);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }

    @At({"/delete/?", "/delete"})
    @Ok("json")
    @RequiresPermissions("platform.logistics.orderplan.delete")
    @SLog(tag = "logistics_orderplan", msg = "${req.getAttribute('id')}")
    public Object delete(String id, @Param("ids")  String[] ids, HttpServletRequest req) {
		try {
			if(ids!=null&&ids.length>0){
				logisticsOrderplanService.delete(ids);
    			req.setAttribute("id", org.apache.shiro.util.StringUtils.toString(ids));
			}else{
				logisticsOrderplanService.delete(id);
    			req.setAttribute("id", id);
			}
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }

    @At("/detail/?")
    @Ok("beetl:/platform/logistics/orderplan/detail.html")
    @RequiresPermissions("platform.logistics.orderplan")
	public void detail(String id, HttpServletRequest req) {
		if (!Strings.isBlank(id)) {
            req.setAttribute("obj", logisticsOrderplanService.fetch(id));
		}else{
            req.setAttribute("obj", null);
        }
    }

}
