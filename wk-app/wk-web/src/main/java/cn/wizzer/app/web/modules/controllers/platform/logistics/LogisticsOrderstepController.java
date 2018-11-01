package cn.wizzer.app.web.modules.controllers.platform.logistics;

import cn.wizzer.framework.base.Result;
import cn.wizzer.app.web.commons.slog.annotation.SLog;
import cn.wizzer.framework.page.datatable.DataTableColumn;
import cn.wizzer.framework.page.datatable.DataTableOrder;
import cn.wizzer.framework.util.StringUtil;
import cn.wizzer.app.logistics.modules.models.logistics_orderstep;
import cn.wizzer.app.logistics.modules.services.LogisticsOrderstepService;
import cn.wizzer.app.logistics.modules.models.logistics_order;
import cn.wizzer.app.logistics.modules.services.LogisticsOrderService;
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
@At("/platform/logistics/orderstep")
public class LogisticsOrderstepController{
    private static final Log log = Logs.get();
    @Inject
    private LogisticsOrderstepService logisticsOrderstepService;

    @Inject
    private LogisticsOrderService logisticsOrderService;

    @At("")
    @Ok("beetl:/platform/logistics/orderstep/index.html")
    @RequiresPermissions("platform.logistics.orderstep.select")
    public void index() {
    }

    @At("/data")
    @Ok("json")
    @RequiresPermissions("platform.logistics.orderstep.select")
    public Object data(@Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns) {
		Cnd cnd = Cnd.NEW();
		cnd.orderBy("otype","asc");
//        cnd.orderBy("step","asc");
    	return logisticsOrderstepService.data(length, start, draw, order, columns, cnd, "otypeDict|btypeDict");
    }

    @At("/add")
    @Ok("beetl:/platform/logistics/orderstep/add.html")
    @RequiresPermissions("platform.logistics.orderstep")
    public void add() {

    }

    @At("/addDo")
    @Ok("json")
    @RequiresPermissions("platform.logistics.orderstep.add")
    @SLog(tag = "logistics_orderstep", msg = "${args[0].id}")
    public Object addDo(@Param("..")logistics_orderstep logisticsOrderstep, HttpServletRequest req) {
		try {
			logisticsOrderstepService.insert(logisticsOrderstep);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }

    @At("/edit/?")
    @Ok("beetl:/platform/logistics/orderstep/edit.html")
    @RequiresPermissions("platform.logistics.orderstep")
    public void edit(String id,HttpServletRequest req) {
		req.setAttribute("obj", logisticsOrderstepService.fetch(id));
    }

    @At("/editDo")
    @Ok("json")
    @RequiresPermissions("platform.logistics.orderstep.edit")
    @SLog(tag = "logistics_orderstep", msg = "${args[0].id}")
    public Object editDo(@Param("..")logistics_orderstep logisticsOrderstep, HttpServletRequest req) {
		try {
            logisticsOrderstep.setOpBy(StringUtil.getUid());
			logisticsOrderstep.setOpAt((int) (System.currentTimeMillis() / 1000));
			logisticsOrderstepService.updateIgnoreNull(logisticsOrderstep);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }

    @At({"/delete/?", "/delete"})
    @Ok("json")
    @RequiresPermissions("platform.logistics.orderstep.delete")
    @SLog(tag = "logistics_orderstep", msg = "${req.getAttribute('id')}")
    public Object delete(String id, @Param("ids")  String[] ids, HttpServletRequest req) {
		try {
			if(ids!=null&&ids.length>0){
				logisticsOrderstepService.delete(ids);
    			req.setAttribute("id", org.apache.shiro.util.StringUtils.toString(ids));
			}else{
				logisticsOrderstepService.delete(id);
    			req.setAttribute("id", id);
			}
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }

    @At("/detail/?")
    @Ok("beetl:/platform/logistics/orderstep/detail.html")
    @RequiresPermissions("platform.logistics.orderstep.select")
	public void detail(String id, HttpServletRequest req) {
		if (!Strings.isBlank(id)) {
            req.setAttribute("obj", logisticsOrderstepService.fetch(id));
		}else{
            req.setAttribute("obj", null);
        }
    }

    /*@At("/getStepbyMobile")
    @Ok("jsonp:full")
    public Object getStepbyMobile(@Param("orderid") String orderid,@Param("stepnum") String stepnum){
        try{
            if(!Strings.isBlank(orderid)&&!Strings.isBlank(stepnum)){
                Cnd cnd= Cnd.NEW();
                logistics_order order=logisticsOrderService.fetch(orderid);
                if(!order.equals(null)){
                    cnd.and("otype","=",order.getOtype());
                    cnd.and("stepnum","=",stepnum);
                    logistics_orderstep orderstep = logisticsOrderstepService.fetch(cnd);
                    return  orderstep;
                }

            }
            return null;
        }
        catch (Exception e){
            return null;
        }
    }*/
    @At("/getStepAndStepName")
    @Ok("json")
    public Object getStepAndStepName(@Param("completeValue")String completeValue){
          return logisticsOrderstepService.getStepAndStepNameByOtype(completeValue);
    }

}
