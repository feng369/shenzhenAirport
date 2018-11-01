package cn.wizzer.app.web.modules.controllers.platform.logistics;

import cn.wizzer.framework.base.Result;
import cn.wizzer.app.web.commons.slog.annotation.SLog;
import cn.wizzer.framework.page.datatable.DataTableColumn;
import cn.wizzer.framework.page.datatable.DataTableOrder;
import cn.wizzer.framework.util.StringUtil;
import cn.wizzer.app.logistics.modules.models.logistics_orderentry;
import cn.wizzer.app.logistics.modules.services.LogisticsOrderentryService;
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
import java.util.List;

@IocBean
@At("/platform/logistics/orderentry")
public class LogisticsOrderentryController{
    private static final Log log = Logs.get();
    @Inject
    private LogisticsOrderentryService logisticsOrderentryService;

    @At("")
    @Ok("beetl:/platform/logistics/orderentry/index.html")
    @RequiresPermissions("platform.logistics.orderentry")
    public void index() {
    }

    @At("/data")
    @Ok("json")
    @RequiresPermissions("platform.logistics.orderentry")
    public Object data(@Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns) {
		Cnd cnd = Cnd.NEW();
    	return logisticsOrderentryService.data(length, start, draw, order, columns, cnd, null);
    }

    @At("/add")
    @Ok("beetl:/platform/logistics/orderentry/add.html")
    @RequiresPermissions("platform.logistics.orderentry")
    public void add() {

    }

    @At("/addDo")
    @Ok("json")
    @RequiresPermissions("platform.logistics.orderentry.add")
    @SLog(tag = "logistics_orderentry", msg = "${args[0].id}")
    public Object addDo(@Param("..")logistics_orderentry logisticsOrderentry, HttpServletRequest req) {
		try {
			logisticsOrderentryService.insert(logisticsOrderentry);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }



    @At("/edit/?")
    @Ok("beetl:/platform/logistics/orderentry/edit.html")
    @RequiresPermissions("platform.logistics.orderentry")
    public void edit(String id,HttpServletRequest req) {
		req.setAttribute("obj", logisticsOrderentryService.fetch(id));
    }

    @At("/editDo")
    @Ok("json")
    @RequiresPermissions("platform.logistics.orderentry.edit")
    @SLog(tag = "logistics_orderentry", msg = "${args[0].id}")
    public Object editDo(@Param("..")logistics_orderentry logisticsOrderentry, HttpServletRequest req) {
		try {
            logisticsOrderentry.setOpBy(StringUtil.getUid());
			logisticsOrderentry.setOpAt((int) (System.currentTimeMillis() / 1000));
			logisticsOrderentryService.updateIgnoreNull(logisticsOrderentry);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }

    @At({"/delete/?", "/delete"})
    @Ok("json")
    @RequiresPermissions("platform.logistics.order.edit")
    @SLog(tag = "logistics_orderentry", msg = "${req.getAttribute('id')}")
    public Object delete(String id, @Param("ids")  String[] ids, HttpServletRequest req) {
		try {
			if(ids!=null&&ids.length>0){
				logisticsOrderentryService.delete(ids);
    			req.setAttribute("id", org.apache.shiro.util.StringUtils.toString(ids));
			}else{
				logisticsOrderentryService.delete(id);
    			req.setAttribute("id", id);
			}
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }

    @At("/detail/?")
    @Ok("beetl:/platform/logistics/orderentry/detail.html")
    @RequiresPermissions("platform.logistics.orderentry")
	public void detail(String id, HttpServletRequest req) {
		if (!Strings.isBlank(id)) {
            req.setAttribute("obj", logisticsOrderentryService.fetch(id));
		}else{
            req.setAttribute("obj", null);
        }
    }
    //20180302zhf1649
    @At("/addMaterielid")
    @Ok("jsonp:full")
    public Object addMaterielid(@Param("materielid") String materielid,@Param("materielname") String materielname,@Param("materielnum") String materielnum,@Param("sequencenum") String sequencenum,@Param("batch") String batch,@Param("quantity") String quantity, HttpServletRequest req) {
        try {
            if(!Strings.isBlank(materielid)){
                logistics_orderentry orderentry=new logistics_orderentry();
                orderentry.setMaterielid(materielid);
                if(!Strings.isBlank(materielname)){
                    orderentry.setMaterielname(materielname);
                }
                if(!Strings.isBlank(materielnum)){
                    orderentry.setMaterielnum(materielnum);
                }
                if(!Strings.isBlank(sequencenum)){
                    orderentry.setSequencenum(sequencenum);
                }
                if(!Strings.isBlank(batch)){
                    orderentry.setBatch(batch);
                }
                if(!Strings.isBlank(quantity)){
                    orderentry.setQuantity(quantity);
                }

                return logisticsOrderentryService.insert(orderentry).getId();

            }

            return null;
        } catch (Exception e) {
            return null;
        }
    }
}
