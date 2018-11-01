package cn.wizzer.app.web.modules.controllers.platform.logistics;

import cn.wizzer.framework.base.Result;
import cn.wizzer.app.web.commons.slog.annotation.SLog;
import cn.wizzer.framework.page.datatable.DataTableColumn;
import cn.wizzer.framework.page.datatable.DataTableOrder;
import cn.wizzer.framework.util.StringUtil;
import cn.wizzer.app.logistics.modules.models.logistics_orderdelivery;
import cn.wizzer.app.logistics.modules.services.LogisticsOrderdeliveryService;
import cn.wizzer.app.logistics.modules.services.LogisticsDeliveryorderService;
import cn.wizzer.app.logistics.modules.models.logistics_Deliveryorder;
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
@At("/platform/logistics/orderdelivery")
public class LogisticsOrderdeliveryController{
    private static final Log log = Logs.get();
    @Inject
    private LogisticsOrderdeliveryService logisticsOrderdeliveryService;

    @Inject
    private LogisticsDeliveryorderService logisticsDeliveryorderService;

    @Inject
    private logistics_Deliveryorder logistics_deliveryorder;

    @At("")
    @Ok("beetl:/platform/logistics/orderdelivery/index.html")
    @RequiresPermissions("platform.logistics.orderdelivery")
    public void index() {
    }

    @At("/data")
    @Ok("json")
    @RequiresPermissions("platform.logistics.orderdelivery")
    public Object data(@Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns) {
		Cnd cnd = Cnd.NEW();
    	return logisticsOrderdeliveryService.data(length, start, draw, order, columns, cnd, null);
    }



    @At("/add")
    @Ok("beetl:/platform/logistics/orderdelivery/add.html")
    @RequiresPermissions("platform.logistics.orderdelivery")
    public void add() {

    }

    @At("/addDo")
    @Ok("json")
    @RequiresPermissions("platform.logistics.orderdelivery.add")
    @SLog(tag = "logistics_orderdelivery", msg = "${args[0].id}")
    public Object addDo(@Param("..")logistics_orderdelivery logisticsOrderdelivery, HttpServletRequest req) {
		try {
			logisticsOrderdeliveryService.insert(logisticsOrderdelivery);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }

    @At("/edit/?")
    @Ok("beetl:/platform/logistics/orderdelivery/edit.html")
    @RequiresPermissions("platform.logistics.orderdelivery")
    public void edit(String id,HttpServletRequest req) {
		req.setAttribute("obj", logisticsOrderdeliveryService.fetch(id));
    }

    @At("/editDo")
    @Ok("json")
    @RequiresPermissions("platform.logistics.orderdelivery.edit")
    @SLog(tag = "logistics_orderdelivery", msg = "${args[0].id}")
    public Object editDo(@Param("..")logistics_orderdelivery logisticsOrderdelivery, HttpServletRequest req) {
		try {
            logisticsOrderdelivery.setOpBy(StringUtil.getUid());
			logisticsOrderdelivery.setOpAt((int) (System.currentTimeMillis() / 1000));
			logisticsOrderdeliveryService.updateIgnoreNull(logisticsOrderdelivery);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }

    @At({"/delete/?", "/delete"})
    @Ok("json")
    @RequiresPermissions("platform.logistics.orderdelivery.delete")
    @SLog(tag = "logistics_orderdelivery", msg = "${req.getAttribute('id')}")
    public Object delete(String id, @Param("ids")  String[] ids, HttpServletRequest req) {
		try {
			if(ids!=null&&ids.length>0){
				logisticsOrderdeliveryService.delete(ids);
    			req.setAttribute("id", org.apache.shiro.util.StringUtils.toString(ids));
			}else{
				logisticsOrderdeliveryService.delete(id);
    			req.setAttribute("id", id);
			}
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }

    @At("/detail/?")
    @Ok("beetl:/platform/logistics/orderdelivery/detail.html")
    @RequiresPermissions("platform.logistics.orderdelivery")
	public void detail(String id, HttpServletRequest req) {
		if (!Strings.isBlank(id)) {
            req.setAttribute("obj", logisticsOrderdeliveryService.fetch(id));
		}else{
            req.setAttribute("obj", null);
        }
    }

}
