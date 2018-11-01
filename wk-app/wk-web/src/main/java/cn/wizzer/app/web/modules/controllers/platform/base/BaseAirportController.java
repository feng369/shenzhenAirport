package cn.wizzer.app.web.modules.controllers.platform.base;

import cn.wizzer.app.sys.modules.models.Sys_user;
import cn.wizzer.app.sys.modules.services.SysUserService;
import cn.wizzer.framework.base.Result;
import cn.wizzer.app.web.commons.slog.annotation.SLog;
import cn.wizzer.framework.page.datatable.DataTableColumn;
import cn.wizzer.framework.page.datatable.DataTableOrder;
import cn.wizzer.framework.util.StringUtil;
import cn.wizzer.app.base.modules.models.base_airport;
import cn.wizzer.app.base.modules.services.BaseAirportService;
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
@At("/platform/base/airport")
public class BaseAirportController{
    private static final Log log = Logs.get();
    @Inject
    private BaseAirportService baseAirportService;

    @Inject
    private SysUserService sysUserService;

    @At("")
    @Ok("beetl:/platform/base/airport/index.html")
    @RequiresPermissions("platform.base.airport.select")
    public void index() {

    }

    @At("/data")
    @Ok("json")
    @RequiresPermissions("platform.base.airport.select")
    public Object data(@Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns) {
		Cnd cnd = Cnd.NEW();
    	return baseAirportService.data(length, start, draw, order, columns, cnd, "sys_user");
    }

    @At("/add")
    @Ok("beetl:/platform/base/airport/add.html")
    @RequiresPermissions("platform.base.airport")
    public void add() {

    }

    @At("/position")
    @Ok("beetl:/platform/base/airport/position.html")
    @RequiresPermissions("platform.base.airport")
    public void position() {

    }

    @At("/addDo")
    @Ok("json")
    @RequiresPermissions("platform.base.airport.add")
    @SLog(tag = "base_airport", msg = "${args[0].id}")
    public Object addDo(@Param("..")base_airport baseAirport, HttpServletRequest req) {
		try {
			baseAirportService.insert(baseAirport);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }

    @At("/edit/?")
    @Ok("beetl:/platform/base/airport/edit.html")
    @RequiresPermissions("platform.base.airport")
    public void edit(String id,HttpServletRequest req) {
        base_airport airport = baseAirportService.fetch(id);
		req.setAttribute("obj", airport);
		req.setAttribute("creater",sysUserService.fetch(airport.getCreater()));
        req.setAttribute("opby",sysUserService.fetch(airport.getOpBy()));
    }

    @At("/editDo")
    @Ok("json")
    @RequiresPermissions("platform.base.airport.edit")
    @SLog(tag = "base_airport", msg = "${args[0].id}")
    public Object editDo(@Param("..")base_airport baseAirport, HttpServletRequest req) {
		try {
            baseAirport.setOpBy(StringUtil.getUid());
			baseAirport.setOpAt((int) (System.currentTimeMillis() / 1000));
			baseAirportService.updateIgnoreNull(baseAirport);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }

    @At({"/delete/?", "/delete"})
    @Ok("json")
    @RequiresPermissions("platform.base.airport.delete")
    @SLog(tag = "base_airport", msg = "${req.getAttribute('id')}")
    public Object delete(String id, @Param("ids")  String[] ids, HttpServletRequest req) {
		try {
			if(ids!=null&&ids.length>0){
				baseAirportService.delete(ids);
    			req.setAttribute("id", org.apache.shiro.util.StringUtils.toString(ids));
			}else{
				baseAirportService.delete(id);
    			req.setAttribute("id", id);
			}
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }

    @At("/detail/?")
    @Ok("beetl:/platform/base/airport/detail.html")
    @RequiresPermissions("platform.base.airport.select")
	public void detail(String id, HttpServletRequest req) {
		if (!Strings.isBlank(id)) {
            base_airport airport = baseAirportService.fetch(id);
            req.setAttribute("obj", airport);
            req.setAttribute("creater",sysUserService.fetch(airport.getCreater()));
            req.setAttribute("opby",sysUserService.fetch(airport.getOpBy()));
		}else{
            req.setAttribute("obj", null);
        }
    }

//    @At("/getAirportbyM")
//    @Ok("jsonp:full")
//    public Object getAirportbyM(){
//        return baseAirportService.query();
//    }

    @At("/countByNum")
    @Ok("json:full")
    public Object countByNum(@Param("num")String num) {
        return baseAirportService.count("base_airport",Cnd.where("airportnum","=",num));
    }
}
