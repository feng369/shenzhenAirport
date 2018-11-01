package cn.wizzer.app.web.modules.controllers.platform.base;

import cn.wizzer.framework.base.Result;
import cn.wizzer.app.web.commons.slog.annotation.SLog;
import cn.wizzer.framework.page.datatable.DataTableColumn;
import cn.wizzer.framework.page.datatable.DataTableOrder;
import cn.wizzer.framework.util.StringUtil;
import cn.wizzer.app.base.modules.models.base_customer;
import cn.wizzer.app.base.modules.services.BaseCustomerService;
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
@At("/platform/base/customer")
public class BaseCustomerController{
    private static final Log log = Logs.get();
    @Inject
    private BaseCustomerService baseCustomerService;

    @At("")
    @Ok("beetl:/platform/base/customer/index.html")
    @RequiresPermissions("platform.base.customer.select")
    public void index() {
    }

    @At("/data")
    @Ok("json")
    @RequiresPermissions("platform.base.customer.select")
    public Object data(@Param("selcusname") String selcusname,@Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns) {
		Cnd cnd = Cnd.NEW();
		if(!Strings.isBlank(selcusname))
		    cnd.and("customername","like","%"+selcusname+"%");
    	return baseCustomerService.data(length, start, draw, order, columns, cnd, null);
    }

    @At("/add")
    @Ok("beetl:/platform/base/customer/add.html")
    @RequiresPermissions("platform.base.customer")
    public void add() {

    }

    @At("/addDo")
    @Ok("json")
    @RequiresPermissions("platform.base.customer.add")
    @SLog(tag = "base_customer", msg = "${args[0].id}")
    public Object addDo(@Param("..")base_customer baseCustomer, HttpServletRequest req) {
		try {
			baseCustomerService.insert(baseCustomer);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }

    @At("/edit/?")
    @Ok("beetl:/platform/base/customer/edit.html")
    @RequiresPermissions("platform.base.customer")
    public void edit(String id,HttpServletRequest req) {
		req.setAttribute("obj", baseCustomerService.fetch(id));
    }

    @At("/editDo")
    @Ok("json")
    @RequiresPermissions("platform.base.customer.edit")
    @SLog(tag = "base_customer", msg = "${args[0].id}")
    public Object editDo(@Param("..")base_customer baseCustomer, HttpServletRequest req) {
		try {
            baseCustomer.setOpBy(StringUtil.getUid());
			baseCustomer.setOpAt((int) (System.currentTimeMillis() / 1000));
			baseCustomerService.updateIgnoreNull(baseCustomer);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }

    @At({"/delete/?", "/delete"})
    @Ok("json")
    @RequiresPermissions("platform.base.customer.delete")
    @SLog(tag = "base_customer", msg = "${req.getAttribute('id')}")
    public Object delete(String id, @Param("ids")  String[] ids, HttpServletRequest req) {
		try {
			if(ids!=null&&ids.length>0){
				baseCustomerService.delete(ids);
    			req.setAttribute("id", org.apache.shiro.util.StringUtils.toString(ids));
			}else{
				baseCustomerService.delete(id);
    			req.setAttribute("id", id);
			}
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }

    @At("/detail/?")
    @Ok("beetl:/platform/base/customer/detail.html")
    @RequiresPermissions("platform.base.customer.select")
	public void detail(String id, HttpServletRequest req) {
		if (!Strings.isBlank(id)) {
            req.setAttribute("obj", baseCustomerService.fetch(id));
		}else{
            req.setAttribute("obj", null);
        }
    }
    @At("/autocomplete")
    @Ok("json")
    @RequiresPermissions("platform.base.customer")
    public Object autocomplete(@Param("customername") String placename) {
        Cnd cnd = Cnd.NEW();
        List<base_customer> List;
        if (!Strings.isBlank(placename))
            cnd.and("customername", "like", "%" + placename + "%");
        int count =baseCustomerService.count(cnd);
        if(count == 0){
            List = null;
        }else{
            List=baseCustomerService.query(cnd,"");
        }
        return  List;
    }

//    @At("/getCustomerbyM")
//    @Ok("jsonp:full")
//    public Object getCustomerbyM(){
//        return baseCustomerService.query();
//    }
    @At("/countByNum")
    @Ok("json:full")
    public Object countByNum(@Param("num")String num) {
        return baseCustomerService.count("base_customer",Cnd.where("customercode","=",num));
    }

    @At
    @Ok("json:full")
    public Object getCustomers() {
        return baseCustomerService.query();
    }
}
