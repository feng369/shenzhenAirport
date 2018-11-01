package cn.wizzer.app.web.modules.controllers.platform.base;

import cn.wizzer.app.sys.modules.models.Sys_dict;
import cn.wizzer.app.sys.modules.models.Sys_user;
import cn.wizzer.app.sys.modules.services.SysDictService;
import cn.wizzer.app.sys.modules.services.SysUserService;
import cn.wizzer.framework.base.Result;
import cn.wizzer.app.web.commons.slog.annotation.SLog;
import cn.wizzer.framework.page.datatable.DataTableColumn;
import cn.wizzer.framework.page.datatable.DataTableOrder;
import cn.wizzer.framework.util.StringUtil;
import cn.wizzer.app.base.modules.models.base_vehicle;
import cn.wizzer.app.base.modules.services.BaseVehicleService;
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
@At("/platform/base/vehicle")
public class BaseVehicleController{
    private static final Log log = Logs.get();
    @Inject
    private BaseVehicleService baseVehicleService;

    @Inject
    private SysDictService sysDictService;

    @Inject
    private SysUserService sysUserService;

    @At("")
    @Ok("beetl:/platform/base/vehicle/index.html")
    @RequiresPermissions("platform.base.vehicle.select")
    public void index() {
    }

    @At("/data")
    @Ok("json")
    @RequiresPermissions("platform.base.vehicle.select")
    public Object data(@Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns) {
		Cnd cnd = Cnd.NEW();
    	return baseVehicleService.dataCode(length, start, draw, order, columns, cnd, null,null);
    }

    @At("/add")
    @Ok("beetl:/platform/base/vehicle/add.html")
    @RequiresPermissions("platform.base.vehicle")
    public void add() {

    }

    @At("/addDo")
    @Ok("json")
    @RequiresPermissions("platform.base.vehicle.add")
    @SLog(tag = "base_vehicle", msg = "${args[0].id}")
    public Object addDo(@Param("..")base_vehicle baseVehicle, HttpServletRequest req) {
		try {
			baseVehicleService.insert(baseVehicle);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }

    @At("/edit/?")
    @Ok("beetl:/platform/base/vehicle/edit.html")
    @RequiresPermissions("platform.base.vehicle")
    public void edit(String id,HttpServletRequest req) {
        base_vehicle vehicle=baseVehicleService.fetch(id);
        Sys_user user=sysUserService.fetch(vehicle.getCreater());

        req.setAttribute("user",user);

		req.setAttribute("obj", vehicle);
    }

    @At("/editDo")
    @Ok("json")
    @RequiresPermissions("platform.base.vehicle.edit")
    @SLog(tag = "base_vehicle", msg = "${args[0].id}")
    public Object editDo(@Param("..")base_vehicle baseVehicle, HttpServletRequest req) {
		try {
            baseVehicle.setOpBy(StringUtil.getUid());
			baseVehicle.setOpAt((int) (System.currentTimeMillis() / 1000));
			baseVehicleService.updateIgnoreNull(baseVehicle);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }

    @At({"/delete/?", "/delete"})
    @Ok("json")
    @RequiresPermissions("platform.base.vehicle.delete")
    @SLog(tag = "base_vehicle", msg = "${req.getAttribute('id')}")
    public Object delete(String id, @Param("ids")  String[] ids, HttpServletRequest req) {
		try {
			if(ids!=null&&ids.length>0){
				baseVehicleService.delete(ids);
    			req.setAttribute("id", org.apache.shiro.util.StringUtils.toString(ids));
			}else{
				baseVehicleService.delete(id);
    			req.setAttribute("id", id);
			}
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }

    @At("/detail/?")
    @Ok("beetl:/platform/base/vehicle/detail.html")
    @RequiresPermissions("platform.base.vehicle.select")
	public void detail(String id, HttpServletRequest req) {
		if (!Strings.isBlank(id)) {
		    base_vehicle vehicle=baseVehicleService.fetch(id);
		    Sys_dict dict=sysDictService.fetch(vehicle.getVehicletypeId());
		    vehicle.setVehicletype(dict);

		    dict=sysDictService.fetch(vehicle.getVehicleareaId());
		    vehicle.setVehiclearea(dict);

		    dict=sysDictService.fetch(vehicle.getVehiclestateId());
		    vehicle.setVehiclestate(dict);

            Sys_user user=sysUserService.fetch(vehicle.getCreater());
            req.setAttribute("user", user);

            req.setAttribute("obj", vehicle);
		}else{
            req.setAttribute("obj", null);
        }
    }

    @At("/bindVehicleDDL")
    @Ok("json")
    @RequiresPermissions("platform.base.vehicle")
    public Object bindVehicleDDL(String name,String code,String code2)
    {
        //name值传过来是null
        String parentid="";
        if(!Strings.isEmpty(name))
        {
            parentid=sysDictService.getIdByNameAndCode(name,code);
        }
        else
        {
            parentid=sysDictService.getIdByNameAndCode(code,code2);
        }


        if(!Strings.isBlank(parentid)) {
            List<Sys_dict> list = sysDictService.query(Cnd.where("parentId", "=", parentid).asc("location"));
            return list;
        }
        return "";
    }

    @At("/isRepeat")
    @Ok("json")
    @RequiresPermissions("platform.base.vehicle")
    public Object isRepeat(String value)
    {
        Cnd cnd = Cnd.NEW();
        List<base_vehicle> list= baseVehicleService.query(cnd.where("vehiclenum","=",value));
        if(!list.isEmpty())//不为空，说明已存在
            return false;
        return true;

    }

//    @At("/getVehiclebyM")
//    @Ok("jsonp:full")
//    public Object getVehiclebyM(){
//        try{
//            List<base_vehicle> vehicle = baseVehicleService.query("vehiclearea|vehiclestate|vehicletype");
//            return vehicle;
//        }
//        catch (Exception e){
//            return Result.error("error");
//        }
//    }

//    @At("/setBusybyM")
//    @Ok("jsonp:full")
//    public Object setBusybyM(@Param("vehicleid") String vehicleid){
//        try{
//            if(!Strings.isBlank(vehicleid)){
//                String stateid=sysDictService.getIdByNameAndCode("在运","vehicleState.trans");
//
//                base_vehicle vehicle=baseVehicleService.fetch(vehicleid);
//                vehicle.setVehiclestateId(stateid);
//                baseVehicleService.updateIgnoreNull(vehicle);
//                return Result.success("更新成功");
//            }
//            return Result.error("更新失败");
//        }
//        catch (Exception e){
//            return Result.error("更新失败");
//        }
//    }
    @At("/countByNum")
    @Ok("json:full")
    public Object countByNum(@Param("num")String num) {
        return baseVehicleService.count("base_vehicle",Cnd.where("vehiclenum","=",num));
    }

}
