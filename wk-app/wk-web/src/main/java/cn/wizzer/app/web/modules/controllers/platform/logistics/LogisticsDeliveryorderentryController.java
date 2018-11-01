package cn.wizzer.app.web.modules.controllers.platform.logistics;

import cn.wizzer.app.base.modules.models.base_person;
import cn.wizzer.app.base.modules.services.BasePersonService;
import cn.wizzer.app.logistics.modules.models.logistics_order;
import cn.wizzer.app.logistics.modules.services.LogisticsOrderService;
import cn.wizzer.framework.base.Result;
import cn.wizzer.app.web.commons.slog.annotation.SLog;
import cn.wizzer.framework.page.datatable.DataTableColumn;
import cn.wizzer.framework.page.datatable.DataTableOrder;
import cn.wizzer.framework.util.StringUtil;
import cn.wizzer.app.logistics.modules.models.logistics_Deliveryorderentry;
import cn.wizzer.app.logistics.modules.services.LogisticsDeliveryorderentryService;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@IocBean
@At("/platform/logistics/Deliveryorderentry")
public class LogisticsDeliveryorderentryController {
    private static final Log log = Logs.get();
    @Inject
    private LogisticsDeliveryorderentryService logisticsDeliveryorderentryService;
    @Inject
    private LogisticsOrderService logisticsOrderService;
    @Inject
    private BasePersonService basePersonService;
    @At("")
    @Ok("beetl:/platform/logistics/Deliveryorderentry/index.html")
    @RequiresPermissions("platform.logistics.Deliveryorderentry")
    public void index() {
    }

    @At("/data")
    @Ok("json")
    @RequiresPermissions("platform.logistics.Deliveryorderentry")
    public Object data(@Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns) {
        Cnd cnd = Cnd.NEW();
        return logisticsDeliveryorderentryService.data(length, start, draw, order, columns, cnd, null);
    }

    @At("/add")
    @Ok("beetl:/platform/logistics/Deliveryorderentry/add.html")
    @RequiresPermissions("platform.logistics.Deliveryorderentry")
    public void add() {

    }

    @At("/addDo")
    @Ok("json")
    @RequiresPermissions("platform.logistics.Deliveryorderentry.add")
    @SLog(tag = "logistics_Deliveryorderentry", msg = "${args[0].id}")
    public Object addDo(@Param("..") logistics_Deliveryorderentry logisticsDeliveryorderentry, HttpServletRequest req) {
        try {
            logisticsDeliveryorderentryService.insert(logisticsDeliveryorderentry);
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }

    @At("/edit/?")
    @Ok("beetl:/platform/logistics/Deliveryorderentry/edit.html")
    @RequiresPermissions("platform.logistics.Deliveryorderentry")
    public void edit(String id, HttpServletRequest req) {
        req.setAttribute("obj", logisticsDeliveryorderentryService.fetch(id));
    }

    @At("/editDo")
    @Ok("json")
    @RequiresPermissions("platform.logistics.Deliveryorderentry.edit")
    @SLog(tag = "logistics_Deliveryorderentry", msg = "${args[0].id}")
    public Object editDo(@Param("..") logistics_Deliveryorderentry logisticsDeliveryorderentry, HttpServletRequest req) {
        try {
            logisticsDeliveryorderentry.setOpBy(StringUtil.getUid());
            logisticsDeliveryorderentry.setOpAt((int) (System.currentTimeMillis() / 1000));
            logisticsDeliveryorderentryService.updateIgnoreNull(logisticsDeliveryorderentry);
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }

    @At({"/delete/?", "/delete"})
    @Ok("json")
    @RequiresPermissions("platform.logistics.Deliveryorderentry.delete")
    @SLog(tag = "logistics_Deliveryorderentry", msg = "${req.getAttribute('id')}")
    public Object delete(String id, @Param("ids") String[] ids, HttpServletRequest req) {
        try {
            if (ids != null && ids.length > 0) {
                logisticsDeliveryorderentryService.delete(ids);
                req.setAttribute("id", org.apache.shiro.util.StringUtils.toString(ids));
            } else {
                logisticsDeliveryorderentryService.delete(id);
                req.setAttribute("id", id);
            }
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }

    @At("/detail/?")
    @Ok("beetl:/platform/logistics/Deliveryorderentry/detail.html")
    @RequiresPermissions("platform.logistics.order")
    public void detail(String id, HttpServletRequest req) {
        if (!Strings.isBlank(id)) {
            req.setAttribute("id", id);
        } else {
            req.setAttribute("id", null);
        }
    }

    @At("/getDeliInfo")
    @Ok("json")
    @RequiresPermissions("platform.logistics.order")
    public List<Map<String, Object>> getDeliInfo(@Param("orderid") String orderid) {
        Cnd cnd = Cnd.NEW();
        cnd.and("orderid", "=", orderid);
        cnd.orderBy("step", "asc");
        List<logistics_Deliveryorderentry> logistics_deliveryorderentries = logisticsDeliveryorderentryService.query(cnd, "logisticsDeliveryorder|logisticsOrder|stepPerson");
        logistics_order logisticsOrder = logisticsOrderService.fetch(orderid);
        List<Map<String, Object>> list = new ArrayList<>();
        if (logisticsOrder != null) {
            //201808011423
            for (logistics_Deliveryorderentry ld : logistics_deliveryorderentries) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", ld.getId());
                map.put("stepname", ld.getStepname());
                map.put("content", ld.getContent());
                map.put("operatetime", ld.getOperatetime());
                map.put("step", ld.getStep());
                map.put("stepPerson", ld.getStepPerson());
                map.put("pstatus", ld.getPstatus());
                map.put("pic", ld.getPicpath());
                base_person basePerson = basePersonService.fetch(logisticsOrder.getPersonid());
                if(basePerson != null){
                    map.put("orderPersonName",basePerson.getPersonname());
                    map.put("orderPersonTel",basePerson.getTel());
                }else{
                    map.put("orderPersonName","");
                    map.put("orderPersonTel","");
                }
                list.add(map);
            }
        }
        return list;
    }

    @At("/getSDtime")
    @Ok("json")
    @RequiresPermissions("platform.logistics.order")
    public Object getSDtime(@Param("orderid") String orderid) {
        if (!Strings.isBlank(orderid)) {
            logistics_order order = logisticsOrderService.fetch(orderid);
            if (order != null) {
                List<logistics_Deliveryorderentry> sdDeliveryorderentries = logisticsDeliveryorderentryService.getDeliveryOrderEntriesByStepNum("SD", order.getId());
                if (sdDeliveryorderentries.size() == 1) {
                    return sdDeliveryorderentries.get(0).getOperatetime();
                }
            }
        }
        return "";
    }

  /*  @At("/getStepData")
    @Ok("jsonp:full")
    public Object getStepData(@Param("orderid") String orderid,@Param("step") String step){
        Cnd cnd=Cnd.NEW();
        cnd.and("orderid","=",orderid);
        cnd.and("step","=",step);
        cnd.and("pstatus","=","1");
        logistics_Deliveryorderentry deliveryorderentry= logisticsDeliveryorderentryService.fetch(cnd);
        return deliveryorderentry;
    }*/

    /*@At("/getDataByMobile")
    @Ok("jsonp:full")
    public Object getDataByMobile(@Param("orderid") String orderid,@Param("step") String step){
        try{
            Cnd cnd=Cnd.NEW();
            if(!Strings.isBlank(orderid)&&!Strings.isBlank(step)){
                cnd.and("orderid","=",orderid);
                cnd.and("step","=",step);
            }
            logistics_Deliveryorderentry deliveryorderentry=logisticsDeliveryorderentryService.fetch(cnd);
            return deliveryorderentry;
        }catch(Exception e){
            return null;
        }

    }
*/
}
