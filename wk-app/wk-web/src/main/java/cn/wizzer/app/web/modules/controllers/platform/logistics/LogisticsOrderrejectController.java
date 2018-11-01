package cn.wizzer.app.web.modules.controllers.platform.logistics;

import cn.wizzer.framework.base.Result;
import cn.wizzer.app.web.commons.slog.annotation.SLog;
import cn.wizzer.framework.page.datatable.DataTableColumn;
import cn.wizzer.framework.page.datatable.DataTableOrder;
import cn.wizzer.framework.util.StringUtil;
import cn.wizzer.app.logistics.modules.models.logistics_orderreject;
import cn.wizzer.app.logistics.modules.services.LogisticsOrderrejectService;
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
@At("/platform/logistics/orderreject")
public class LogisticsOrderrejectController{
    private static final Log log = Logs.get();
    @Inject
    private LogisticsOrderrejectService logisticsOrderrejectService;

    @At("")
    @Ok("beetl:/platform/logistics/orderreject/index.html")
    @RequiresPermissions("platform.logistics.orderreject")
    public void index() {
    }

    @At("/data")
    @Ok("json")
    @RequiresPermissions("platform.logistics.orderreject")
    public Object data(@Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns) {
		Cnd cnd = Cnd.NEW();
    	return logisticsOrderrejectService.data(length, start, draw, order, columns, cnd, null);
    }

    @At("/add")
    @Ok("beetl:/platform/logistics/orderreject/add.html")
    @RequiresPermissions("platform.logistics.orderreject")
    public void add() {

    }

    @At("/addDo")
    @Ok("json")
    @RequiresPermissions("platform.logistics.orderreject.add")
    @SLog(tag = "logistics_orderreject", msg = "${args[0].id}")
    public Object addDo(@Param("..")logistics_orderreject logisticsOrderreject, HttpServletRequest req) {
		try {
			logisticsOrderrejectService.insert(logisticsOrderreject);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }

    @At("/edit/?")
    @Ok("beetl:/platform/logistics/orderreject/edit.html")
    @RequiresPermissions("platform.logistics.orderreject")
    public void edit(String id,HttpServletRequest req) {
		req.setAttribute("obj", logisticsOrderrejectService.fetch(id));
    }

    @At("/editDo")
    @Ok("json")
    @RequiresPermissions("platform.logistics.orderreject.edit")
    @SLog(tag = "logistics_orderreject", msg = "${args[0].id}")
    public Object editDo(@Param("..")logistics_orderreject logisticsOrderreject, HttpServletRequest req) {
		try {
            logisticsOrderreject.setOpBy(StringUtil.getUid());
			logisticsOrderreject.setOpAt((int) (System.currentTimeMillis() / 1000));
			logisticsOrderrejectService.updateIgnoreNull(logisticsOrderreject);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }

    @At({"/delete/?", "/delete"})
    @Ok("json")
    @RequiresPermissions("platform.logistics.orderreject.delete")
    @SLog(tag = "logistics_orderreject", msg = "${req.getAttribute('id')}")
    public Object delete(String id, @Param("ids")  String[] ids, HttpServletRequest req) {
		try {
			if(ids!=null&&ids.length>0){
				logisticsOrderrejectService.delete(ids);
    			req.setAttribute("id", org.apache.shiro.util.StringUtils.toString(ids));
			}else{
				logisticsOrderrejectService.delete(id);
    			req.setAttribute("id", id);
			}
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }

    @At("/detail/?")
    @Ok("beetl:/platform/logistics/orderreject/detail.html")
    @RequiresPermissions("platform.logistics.orderreject")
	public void detail(String id, HttpServletRequest req) {
		if (!Strings.isBlank(id)) {
            req.setAttribute("obj", logisticsOrderrejectService.fetch(id));
		}else{
            req.setAttribute("obj", null);
        }
    }

    /*@At("/insertReject")
    @Ok("jsonp:full")
    public Object insertReject(@Param("orderid") String orderid,@Param("rejectid") String rejectid,@Param("describ") String describ,@Param("personid") String personid){
        try{
            if(!Strings.isBlank(orderid)){
                Cnd cnd=Cnd.NEW();
                cnd.and("id","=",orderid);
                logistics_orderreject orderreject=new logistics_orderreject();
                orderreject.setDescrib(describ);
                orderreject.setPersonid(personid);
                orderreject.setRejectid(rejectid);
                orderreject.setOrderid(orderid);
                logisticsOrderrejectService.insert(orderreject);
                return  Result.success("保存成功");
            }
            return Result.error("保存失败");

        }
        catch (Exception e){
            return Result.error("保存失败");
        }
    }
*/
}
