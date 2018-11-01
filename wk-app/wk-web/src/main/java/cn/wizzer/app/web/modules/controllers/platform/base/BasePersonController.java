package cn.wizzer.app.web.modules.controllers.platform.base;

import cn.wizzer.app.base.modules.services.*;
import cn.wizzer.app.sys.modules.services.SysUnitService;
import cn.wizzer.app.sys.modules.services.SysUserService;
import cn.wizzer.app.web.modules.controllers.open.api.userpermission.UserpermissionController;
import cn.wizzer.framework.base.Result;
import cn.wizzer.app.web.commons.slog.annotation.SLog;
import cn.wizzer.framework.base.ValidatException;
import cn.wizzer.framework.page.datatable.DataTableColumn;
import cn.wizzer.framework.page.datatable.DataTableOrder;
import cn.wizzer.framework.util.StringUtil;
import cn.wizzer.app.base.modules.models.base_person;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.dao.Cnd;
import org.nutz.dao.Sqls;
import org.nutz.json.Json;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.ValidationException;
import java.util.List;

@IocBean
@At("/platform/base/person")
public class BasePersonController {
    private static final Log log = Logs.get();
    @Inject
    private BasePersonService basePersonService;

    @Inject
    private BasePostService basePostService;

    @Inject
    private BaseJobService baseJobService;

    @Inject
    private SysUnitService unitService;

    @Inject
    private BaseDeptService baseDeptService;

    @Inject
    private BaseAirportService baseAirportService;

    @Inject
    private SysUserService sysUserService;
    @Inject
    private BasePlaceService basePlaceService;

    @Inject
    private BaseCnctobjService baseCnctobjService;
    @Inject
    private BaseCustomerService baseCustomerService;


    @At("")
    @Ok("beetl:/platform/base/person/index.html")
    @RequiresPermissions("platform.base.person.select")
    public void index() {
    }

    @At("/data")
    @Ok("json")
    @RequiresPermissions("platform.base.person.select")
    public Object data(@Param("jobNumber")String jobNumber,@Param("tel")String tel,@Param("flag") boolean flag, @Param("keyword") String keyword, @Param("selectForm") String selectForm, @Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns) {
        Cnd cnd = Cnd.NEW();
        UserpermissionController userpermission = new UserpermissionController();
        if (selectForm != null) {
            base_person select = Json.fromJson(base_person.class, selectForm);

            if (!Strings.isBlank(select.getPersonnum()))
                cnd.and("personnum", "like", "%" + select.getPersonnum() + "%");
            if (!Strings.isBlank(select.getPersonname()))
                cnd.and("personname", "like", "%" + select.getPersonname() + "%");
        }
        if (flag) {
            if (!Strings.isBlank(keyword)) {
                keyword = keyword.replace("'", "");
                cnd.and(cnd.exps("jobNumber", "LIKE", "%" + keyword + "%").or("personname", "LIKE", "%" + keyword + "%"));
            }        //是否是在新增订单时
            cnd.and("customerId", "IS NOT", null);
        }
        cnd = baseCnctobjService.unitDataPermission(cnd);
        if(!Strings.isBlank(jobNumber)){
            cnd.and("jobNumber","LIKE","%" + jobNumber + "%");
        }
        if(!Strings.isBlank(tel)){
            cnd.and("tel","LIKE","%" + tel + "%");
        }
        return basePersonService.data(length, start, draw, order, columns, cnd, "unit|base_airport|base_dept|base_post|base_job|base_place");
    }

    @At("/add")
    @Ok("beetl:/platform/base/person/add.html")
    @RequiresPermissions("platform.base.person")
    public void add() {

    }

    @At("/addDo")
    @Ok("json")
    @RequiresPermissions("platform.base.person.add")
    @SLog(tag = "base_person", msg = "${args[0].id}")
    public Object addDo(@Param("..") base_person basePerson, HttpServletRequest req) {
        try {
            basePersonService.insert(basePerson);
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }

    @At("/edit/?")
    @Ok("beetl:/platform/base/person/edit.html")
    @RequiresPermissions("platform.base.person")
    public void edit(String id, HttpServletRequest req) {
        this.getInfo(id, req);
    }

    @At("/editDo")
    @Ok("json")
    @RequiresPermissions("platform.base.person.edit")
    @SLog(tag = "base_person", msg = "${args[0].id}")
    public Object editDo(@Param("..") base_person basePerson, HttpServletRequest req) {
        try {
            basePerson.setOpBy(StringUtil.getUid());
            basePerson.setOpAt((int) (System.currentTimeMillis() / 1000));
            basePersonService.updateIgnoreNull(basePerson);
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }

    @At({"/delete/?", "/delete"})
    @Ok("json")
    @RequiresPermissions("platform.base.person.delete")
    @SLog(tag = "base_person", msg = "${req.getAttribute('id')}")
    public Object delete(String id, @Param("ids") String[] ids, HttpServletRequest req) {
        try {
            if (ids != null && ids.length > 0) {
                basePersonService.delete(ids);
                req.setAttribute("id", org.apache.shiro.util.StringUtils.toString(ids));
            } else {
                basePersonService.delete(id);
                req.setAttribute("id", id);
            }
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }

    @At("/detail/?")
    @Ok("beetl:/platform/base/person/detail.html")
    @RequiresPermissions("platform.base.person.select")
    public void detail(String id, HttpServletRequest req) {
        this.getInfo(id, req);
    }

    @At
    @Ok("beetl:/platform/base/person/selectAirport.html")
    @RequiresPermissions("platform.base.person")
    public void getInfo(String id, HttpServletRequest req) {
        base_person person = basePersonService.fetch(id);
        req.setAttribute("obj", basePersonService.fetch(id));
        if (person.getUnitid() != null) {
            req.setAttribute("unit", unitService.fetch(person.getUnitid()));
        } else {
            req.setAttribute("unit", null);
        }
        if (person.getPostid() != null) {
            req.setAttribute("post", basePostService.fetch(person.getPostid()));
        } else {
            req.setAttribute("post", null);
        }
        if (person.getDeptid() != null) {
            req.setAttribute("dept", baseDeptService.fetch(person.getDeptid()));
        } else {
            req.setAttribute("dept", null);
        }

        if (person.getJobid() != null) {
            req.setAttribute("job", baseJobService.fetch(person.getJobid()));
        } else {
            req.setAttribute("job", null);
        }
        if (person.getAirportid() != null) {
            req.setAttribute("airport", baseAirportService.fetch(person.getAirportid()));
        } else {
            req.setAttribute("airport", null);
        }
        if (person.getPlaceid() != null) {
            req.setAttribute("place", basePlaceService.fetch(person.getPlaceid()));
        } else {
            req.setAttribute("place", null);
        }
        if (person.getCustomerId() != null) {
            req.setAttribute("customer", baseCustomerService.fetch(person.getCustomerId()));
        } else {
            req.setAttribute("customer", null);
        }
        req.setAttribute("creater", sysUserService.fetch(person.getCreater()));
        req.setAttribute("opby", sysUserService.fetch(person.getOpBy()));
    }

    @At
    @Ok("beetl:/platform/base/person/selectAirport.html")
    @RequiresPermissions("platform.base.person")
    public void selectAirport(HttpServletRequest req) {

    }
//    @At
//    @Ok("beetl:/platform/sys/role/selectUser.html")
//    @RequiresPermissions("sys.manager.role")
//    public void selectAirport(HttpServletRequest req) {
//
//    }


    @At("/selectData")
    @Ok("json:full")
    @RequiresPermissions("platform.base.person")
    public Object selectData(@Param("name") String name, @Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns) {
        String sql = "select a.*,b.`name` as unitname,c.airportname,d.deptname,e.jobname,f.postname from base_person a left join ";
        sql += " sys_unit b on a.unitid=b.id left JOIN ";
        sql += " base_airport c on a.airportid=c.id left join ";
        sql += " base_dept d on a.deptid=d.id left JOIN ";
        sql += " base_job e on a.jobid=e.id left JOIN ";
        sql += " base_post f on a.postid=f.id ";
        sql += " where 1=1 ";
        if (!Strings.isBlank(name)) {
            sql += " and a.personname like '%" + name + "%' ";
        }
        String s = sql;
        if (order != null && order.size() > 0) {
            for (DataTableOrder o : order) {
                DataTableColumn col = columns.get(o.getColumn());
                s += " order by " + Sqls.escapeSqlFieldValue(col.getData()).toString() + " " + o.getDir();
            }
        }
        return basePersonService.data(length, start, draw, Sqls.create(sql), Sqls.create(s));
    }

    @At("/getPerson")
    @Ok("json")
    public Object getPerson(@Param("airportid") String airportid) {
        try {
            if (!Strings.isBlank(airportid)) {
                return basePersonService.query(Cnd.where("airportid", "=", airportid));
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }

    }

    @At("/autocomplete")
    @Ok("json")
    @RequiresPermissions("platform.base.person")
    public Object autocomplete(@Param("personnum") String personnum) {
        Cnd cnd = Cnd.NEW();
        List<base_person> List;
        if (!Strings.isBlank(personnum))
            cnd.and("personnum", "like", "%" + personnum + "%");
        int count = basePersonService.count(cnd);
        if (count == 0) {
            List = null;
        } else {
            List = basePersonService.query(cnd, "unit|base_airport|base_dept|base_job|base_post");
        }
        return List;
    }

//    @At("/getPersonbyM")
//    @Ok("jsonp:full")
//    public Object getPersonbyM(@Param("airportid") String airportid){
//        try{
//            if(!Strings.isBlank(airportid)){
//                return basePersonService.query(Cnd.where("airportid","=",airportid));
//            }else{
//                return null;
//            }
//        }
//        catch (Exception e){
//            return null;
//        }
//
//    }

    @At("/getPersonbyID")
    @Ok("jsonp:full")
    public Object getPersonbyID(@Param("id") String id) {
        try {
            if (!Strings.isBlank(id)) {
                return basePersonService.fetch(id);
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }

    }


    @At("/countByNum")
    @Ok("json:full")
    public Object countByNum(@Param("num") String num) {
        return basePersonService.count("base_person", Cnd.where("personnum", "=", num));
    }

    @At("/bindWxuser")
    @Ok("json")
    @RequiresPermissions("platform.base.person.bindWxuser")
    public Object bindWxuser(@Param("id") String id, @Param("wxuserid") String wxuserid, HttpServletRequest req) {
        try {
            if (StringUtils.isNotBlank(id) && StringUtils.isNotBlank(wxuserid)) {
                basePersonService.bindWxuser(id, wxuserid);
                return Result.success("system.success");
            }
            throw new ValidatException("请求参数不合法!");
        } catch (Exception e) {
            return Result.error("system.error", e);
        }
    }

    @At("/unbindWx")
    @Ok("json")
    @RequiresPermissions("platform.base.person.unbindWxuser")
    public Result unbindWx(@Param("ids") String[] ids, @Param("wxUids")String[] wxUids, HttpServletRequest req) {
        try {
            if(wxUids == null || wxUids.length == 0){
                return Result.error(2,"本地未找到您的企业微信,无需解绑");
            }
            return basePersonService.unbindWx(ids,wxUids);
        } catch (Exception e) {
            return Result.error("system.error", e);
        }
    }

}
