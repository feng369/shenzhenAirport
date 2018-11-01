package cn.wizzer.app.web.modules.controllers.platform.base;

import cn.wizzer.app.base.modules.models.base_airport;
import cn.wizzer.app.base.modules.models.base_person;
import cn.wizzer.app.base.modules.services.BaseAirportService;
import cn.wizzer.app.base.modules.services.BaseCustomerService;
import cn.wizzer.app.base.modules.services.BasePersonService;
import cn.wizzer.app.sys.modules.models.Sys_unit;
import cn.wizzer.app.sys.modules.models.Sys_user;
import cn.wizzer.app.sys.modules.models.Sys_dict;
import cn.wizzer.app.sys.modules.services.SysUnitService;
import cn.wizzer.app.sys.modules.services.SysUserService;
import cn.wizzer.app.sys.modules.services.SysDictService;
import cn.wizzer.framework.base.Result;
import cn.wizzer.app.web.commons.slog.annotation.SLog;
import cn.wizzer.framework.page.datatable.DataTableColumn;
import cn.wizzer.framework.page.datatable.DataTableOrder;
import cn.wizzer.framework.util.StringUtil;
import cn.wizzer.app.base.modules.models.base_cnctobj;
import cn.wizzer.app.base.modules.services.BaseCnctobjService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.nutz.dao.Cnd;
import org.nutz.dao.Sqls;
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
@At("/platform/base/cnctobj")
public class BaseCnctobjController{
    private static final Log log = Logs.get();
    @Inject
    private BaseCnctobjService baseCnctobjService;

    @Inject
    private SysUserService sysUserService;

    @Inject
    private SysDictService sysDictService;

    @Inject
    private BasePersonService basePersonService;

    @Inject
    private BaseAirportService baseAirportService;

    @Inject
    private BaseCustomerService baseCustomerService;

    @Inject
    private SysUnitService sysUnitService;





    @At("")
    @Ok("beetl:/platform/base/cnctobj/index.html")
    @RequiresPermissions("platform.base.cnctobj.select")
    public void index() {
    }

    @At("/data")
    @Ok("json")
    @RequiresPermissions("platform.base.cnctobj.select")
    public Object data(@Param("length") int length, @Param("start") int start,
                       @Param("draw") int draw, @Param("::order") List<DataTableOrder> order,
                       @Param("::columns") List<DataTableColumn> columns) {
		Cnd cnd = Cnd.NEW();

    	return baseCnctobjService.data(length, start, draw, order, columns, cnd, "user|person|dict");
    }

    @At("/add")
    @Ok("beetl:/platform/base/cnctobj/add.html")
    @RequiresPermissions("platform.base.cnctobj")
    public void add() {

    }

    @At("/addDo")
    @Ok("json")
    @RequiresPermissions("platform.base.cnctobj.add")
    @SLog(tag = "base_cnctobj", msg = "${args[0].id}")
    public Object addDo(@Param("..")base_cnctobj baseCnctobj, HttpServletRequest req) {
		try {
			baseCnctobjService.insert(baseCnctobj);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }

    @At("/edit/?")
    @Ok("beetl:/platform/base/cnctobj/edit.html")
    @RequiresPermissions("platform.base.cnctobj")
    public void edit(String id,HttpServletRequest req) {

		//req.setAttribute("obj", baseCnctobjService.fetch(id));
        base_cnctobj b=baseCnctobjService.fetch(Cnd.where("id", "=", id));
        req.setAttribute("obj",baseCnctobjService.fetchLinks(b,"person|user|dict",Cnd.where("id", "=", id)));
    }

    @At("/editDo")
    @Ok("json")
    @RequiresPermissions("platform.base.cnctobj.edit")
    @SLog(tag = "base_cnctobj", msg = "${args[0].id}")
    public Object editDo(@Param("..")base_cnctobj baseCnctobj, HttpServletRequest req) {
		try {
            baseCnctobj.setOpBy(StringUtil.getUid());
			baseCnctobj.setOpAt((int) (System.currentTimeMillis() / 1000));
			baseCnctobjService.updateIgnoreNull(baseCnctobj);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }

    @At({"/delete/?", "/delete"})
    @Ok("json")
    @RequiresPermissions("platform.base.cnctobj.delete")
    @SLog(tag = "base_cnctobj", msg = "${req.getAttribute('id')}")
    public Object delete(String id, @Param("ids")  String[] ids, HttpServletRequest req) {
		try {
			if(ids!=null&&ids.length>0){
				baseCnctobjService.delete(ids);
    			req.setAttribute("id", org.apache.shiro.util.StringUtils.toString(ids));
			}else{
				baseCnctobjService.delete(id);
    			req.setAttribute("id", id);
			}
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }

    @At("/detail/?")
    @Ok("beetl:/platform/base/cnctobj/detail.html")
    @RequiresPermissions("platform.base.cnctobj.select")
	public void detail(String id, HttpServletRequest req) {
		if (!Strings.isBlank(id)) {
            //req.setAttribute("obj", baseCnctobjService.fetch(id));
            //req.setAttribute("obj", baseCnctobjService.query(Cnd.where("id","=",id),"user|person|dict").toArray());
            //req.setAttribute("obj",null);
            base_cnctobj b=baseCnctobjService.fetch(Cnd.where("id", "=", id));
            req.setAttribute("obj",baseCnctobjService.fetchLinks(b,"person|user|dict",Cnd.where("id", "=", id)));
		}else{
            req.setAttribute("obj", null);
        }
    }


    @At
    @Ok("beetl:/platform/base/cnctobj/selectUser.html")
    @RequiresPermissions("platform.base.cnctobj")
    public void selectUser(HttpServletRequest req) {

    }

    @At
    @Ok("beetl:/platform/base/cnctobj/selectPerson.html")
    @RequiresPermissions("platform.base.cnctobj")
    public void selectPerson(HttpServletRequest req) {

    }


    @At
    @Ok("json")
    @RequiresPermissions("platform.base.cnctobj.select")
    public Object tree(@Param("uid") String uid) {
        List<Sys_user> list = sysUserService.query(Cnd.NEW());
        List<Map<String, Object>> tree = new ArrayList<>();
        Map<String, Object> obj=new HashMap<>() ;
        obj.put("id","-1");
        obj.put("text","所有用户");
        obj.put("children",true);
        tree.add(obj);

        Map<String,Object> subobj;
        for (Sys_user user : list) {
            subobj=new HashMap<>();
            subobj.put("id", user.getId());
            subobj.put("text", user.getUsername());
            subobj.put("children", false);
            tree.add(subobj);


        }
        return tree;
    }

    @At("/bindType")
    @Ok("json")
    @RequiresPermissions("platform.base.cnctobj")
    public Object bindType(String name)
    {

        String parentid=sysDictService.getIdByName(name);
        List<Sys_dict> list=sysDictService.query(Cnd.where("parentId","=", parentid).asc("location"));

        return list;
    }

//    @At("/getUserInfo")
//    @Ok("jsonp:full")
//    public base_cnctobj getUserInfo(@Param("userid") String userid){
//        try{
//            //Subject subject = SecurityUtils.getSubject();
//            Sys_user curUser = sysUserService.fetch(userid);
//            if(curUser == null){
//                return null;
//            }else{
//                Cnd cnd = Cnd.NEW();
//                cnd.and("userId","=",userid);
//                base_cnctobj baseCnctobj=baseCnctobjService.fetch(cnd);
//                if (!Strings.isBlank(baseCnctobj.getPersonId())){
//                    base_person basePerson =basePersonService.fetch(baseCnctobj.getPersonId());
//                    if (!Strings.isBlank(basePerson.getAirportid())) {
//                        base_airport baseAirport = baseAirportService.fetch(basePerson.getAirportid());
//                        basePerson.setBase_airport(baseAirport);
//                    }
//                    if (!Strings.isBlank(basePerson.getUnitid())) {
//                        Sys_unit sysUnit = sysUnitService.fetch(basePerson.getUnitid());
//                        basePerson.setUnit(sysUnit);
//                    }
//                    baseCnctobj.setPerson(basePerson);
//                }
//                baseCnctobj.setUser(curUser);
//                return baseCnctobj;
//            }
//
//        }catch (Exception e) {
//            return null;
//        }
//
//    }



}
