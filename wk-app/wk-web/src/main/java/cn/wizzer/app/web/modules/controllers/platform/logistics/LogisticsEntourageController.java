package cn.wizzer.app.web.modules.controllers.platform.logistics;

import cn.wizzer.framework.base.Result;
import cn.wizzer.app.web.commons.slog.annotation.SLog;
import cn.wizzer.framework.page.datatable.DataTableColumn;
import cn.wizzer.framework.page.datatable.DataTableOrder;
import cn.wizzer.framework.util.StringUtil;
import cn.wizzer.app.logistics.modules.models.logistics_entourage;
import cn.wizzer.app.logistics.modules.services.LogisticsEntourageService;
import jdk.nashorn.internal.parser.JSONParser;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.dao.Cnd;
import org.nutz.json.Json;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@IocBean
@At("/platform/logistics/entourage")
public class LogisticsEntourageController{
    private static final Log log = Logs.get();
    @Inject
    private LogisticsEntourageService logisticsEntourageService;

    @At("")
    @Ok("beetl:/platform/logistics/entourage/index.html")
    @RequiresPermissions("platform.logistics.entourage")
    public void index() {
    }

    @At("/data")
    @Ok("json")
    @RequiresPermissions("platform.logistics.entourage")
    public Object data(@Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns) {
		Cnd cnd = Cnd.NEW();
    	return logisticsEntourageService.data(length, start, draw, order, columns, cnd, null);
    }

    @At("/add")
    @Ok("beetl:/platform/logistics/entourage/add.html")
    @RequiresPermissions("platform.logistics.entourage")
    public void add() {

    }

    @At("/addDo")
    @Ok("json")
    @RequiresPermissions("platform.logistics.entourage.add")
    @SLog(tag = "logistics_entourage", msg = "${args[0].id}")
    public Object addDo(@Param("..")logistics_entourage logisticsEntourage, HttpServletRequest req) {
		try {
			logisticsEntourageService.insert(logisticsEntourage);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }

    @At("/edit/?")
    @Ok("beetl:/platform/logistics/entourage/edit.html")
    @RequiresPermissions("platform.logistics.entourage")
    public void edit(String id,HttpServletRequest req) {
		req.setAttribute("obj", logisticsEntourageService.fetch(id));
    }

    @At("/editDo")
    @Ok("json")
    @RequiresPermissions("platform.logistics.entourage.edit")
    @SLog(tag = "logistics_entourage", msg = "${args[0].id}")
    public Object editDo(@Param("..")logistics_entourage logisticsEntourage, HttpServletRequest req) {
		try {
            logisticsEntourage.setOpBy(StringUtil.getUid());
			logisticsEntourage.setOpAt((int) (System.currentTimeMillis() / 1000));
			logisticsEntourageService.updateIgnoreNull(logisticsEntourage);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }

    @At({"/delete/?", "/delete"})
    @Ok("json")
    @RequiresPermissions("platform.logistics.entourage.delete")
    @SLog(tag = "logistics_entourage", msg = "${req.getAttribute('id')}")
    public Object delete(String id, @Param("ids")  String[] ids, HttpServletRequest req) {
		try {
			if(ids!=null&&ids.length>0){
				logisticsEntourageService.delete(ids);
    			req.setAttribute("id", org.apache.shiro.util.StringUtils.toString(ids));
			}else{
				logisticsEntourageService.delete(id);
    			req.setAttribute("id", id);
			}
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }

    @At("/detail/?")
    @Ok("beetl:/platform/logistics/entourage/detail.html")
    @RequiresPermissions("platform.logistics.entourage")
	public void detail(String id, HttpServletRequest req) {
		if (!Strings.isBlank(id)) {
            req.setAttribute("obj", logisticsEntourageService.fetch(id));
		}else{
            req.setAttribute("obj", null);
        }
    }

    @At("/getEntouragebyM")
    @Ok("jsonp:full")
    public Object getEntouragebyM(@Param("airportid") String airportid){
        try{
            Cnd cnd=Cnd.NEW();
            if(!Strings.isBlank(airportid)){
                cnd.and("airportid","=",airportid);
            }
            List<logistics_entourage> logistics_entourages=logisticsEntourageService.query(cnd,"person|order");
            return null;
        }
        catch (Exception e){
            return Result.error("error");
        }
    }

    @At("/insertDatabyM")
    @Ok("jsonp:full")
    public Object insertDatabyM(@Param("orderid") String orderid, @Param("personlist") String personlist){
        try{
            if(!Strings.isBlank(personlist)&&!Strings.isBlank(orderid)){
                String [] personArr=personlist.split(",");
                for(int i=0;i<personArr.length;i++){
                    if(personArr[i].length()>0){
                        logistics_entourage entourage=new logistics_entourage();
                        entourage.setOrderid(orderid);
                        entourage.setPersonid(personArr[i]);
                        logisticsEntourageService.insert(entourage);
                    }
                }
                return Result.success("保存成功");
            }
            return Result.error("无数据保存");
        }
        catch (Exception e){
            return Result.error("保存数据失败");
        }
    }

}
