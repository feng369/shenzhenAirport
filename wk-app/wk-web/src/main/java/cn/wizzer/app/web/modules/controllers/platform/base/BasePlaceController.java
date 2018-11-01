package cn.wizzer.app.web.modules.controllers.platform.base;


import cn.wizzer.app.base.modules.models.base_person;
import cn.wizzer.app.base.modules.services.*;
import cn.wizzer.app.logistics.modules.services.LogisticsOrderService;
import cn.wizzer.app.sys.modules.models.Sys_dict;
import cn.wizzer.app.sys.modules.services.SysDictService;
import cn.wizzer.framework.base.Result;
import cn.wizzer.app.web.commons.slog.annotation.SLog;
import cn.wizzer.framework.page.datatable.DataTableColumn;
import cn.wizzer.framework.page.datatable.DataTableOrder;
import cn.wizzer.framework.util.StringUtil;
import cn.wizzer.app.base.modules.models.base_place;
import jdk.nashorn.internal.parser.JSONParser;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.dao.Cnd;
import org.nutz.integration.jedis.RedisService;
import org.nutz.json.Json;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.*;
import org.nutz.plugins.cache.dao.CachedNutDaoExecutor;
import org.nutz.plugins.cache.impl.redis.RedisCache;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@IocBean
@At("/platform/base/place")
public class BasePlaceController{
    private static final Log log = Logs.get();
    @Inject
    private BasePlaceService basePlaceService;
    @Inject
    private SysDictService sysDictService;
    @Inject
    private BaseAirportService baseAirportService;
    @Inject
    private BaseCustomerService baseCustomerService;
    @Inject
    private BasePersonService basePersonService;
    @Inject
    private LogisticsOrderService logisticsOrderService;
    @Inject
    private BaseCnctobjService baseCnctobjService;

    @At("")
    @Ok("beetl:/platform/base/place/index.html")
    @RequiresPermissions("platform.base.place.select")
    public void index() {
//        Cnd cnd=Cnd.NEW();
//        cnd.exps("parentId", "=", "").or("parentId", "is", null);
//        cnd=baseCnctobjService.airportDataPermission(cnd);
//        cnd.asc("placecode");
//        return basePlaceService.query(cnd,"airport|customer|person");
    }

    @At("/place")
    @Ok("json")
    public Object getPlace(){
        //获取地点标记
        base_person person= basePersonService.getPersonInfo();
        Cnd cnd = Cnd.NEW();
        cnd.and("airportId","=",person.getAirportid());
        List<base_place> places= basePlaceService.query(cnd);
        return places;
    }




    @At("/data")
    @Ok("json")
    @RequiresPermissions("platform.base.place.select")
    public Object data(@Param("placecode") String placecode,@Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns) {
		Cnd cnd = Cnd.NEW();
        cnd.asc("placecode");
        if(!Strings.isBlank(placecode))
            cnd.and("placecode","like","%"+placecode+"%");
        cnd=baseCnctobjService.airportDataPermission(cnd);
//        RedisCache<String,String> d=new RedisCache<String, String>();
//        d.put("1","2");

        return basePlaceService.data(length,start,draw,order,columns,cnd,"airport|person");
        //return  basePlaceService.dataCode(length,start,draw,order,columns,cnd,"airport|customer|person",null);

    	//return basePlaceService.data(length, start, draw, order, columns, cnd, "airport|customer|person|dict");
    }
    @At("/getPlaceList")
    @Ok("json")
    @RequiresPermissions("platform.base.place.select")
    public Object getPlaceList(@Param("placecode") String placecode,@Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns) {
		Cnd cnd = Cnd.NEW();
        if(!Strings.isBlank(placecode))
            cnd.and("placecode","like","%"+placecode+"%");
        cnd=baseCnctobjService.airportDataPermission(cnd);

        return basePlaceService.getPlaceList(length,start,draw,order,columns,cnd);
    }

    @At("/autocomplete")
    @Ok("json")
    @RequiresPermissions("platform.base.place")
    public Object autocomplete(@Param("placename") String placename) {
         Cnd cnd = Cnd.NEW();
        List<base_place> List=new ArrayList<>();
        if (!Strings.isBlank(placename)) {
            cnd.and("placename", "like", "%" + placename + "%");List = basePlaceService.query(cnd, "airport|customer|person");
        }
        if(List.size() == 0){
            base_place basePlace=new base_place();
            basePlace.setId("");
            basePlace.setPlacename("无数据");
            basePlace.setPlacecode("无数据");
            basePlace.setTelephone("无数据");
            List.add(basePlace);
        }
        return  List;

    }




    @At("/add")
    @Ok("beetl:/platform/base/place/add.html")
    @RequiresPermissions("platform.base.place")
    public Object add(@Param("pid") String pid) {
        return Strings.isBlank(pid) ? null : basePlaceService.fetch(pid);
    }

    @At("/addDo")
    @Ok("json")
    @RequiresPermissions("platform.base.place.add")
    @SLog(tag = "base_place", msg = "${args[0].id}")
    public Object addDo(@Param("..")base_place basePlace, @Param("parentId") String parentId, HttpServletRequest req) {
		try {
			basePlaceService.save(basePlace,parentId);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }

    @At("/edit/?")
    @Ok("beetl:/platform/base/place/edit.html")
    @RequiresPermissions("platform.base.place")
    public void edit(String id,HttpServletRequest req) {
        base_place place=basePlaceService.fetch(id);

		req.setAttribute("obj", basePlaceService.fetch(id));
        req.setAttribute("parentPlace", basePlaceService.fetch(place.getParentId()==null?"":place.getParentId()));
        req.setAttribute("airport",baseAirportService.fetch(place.getAirportId()==null?"":place.getAirportId()));
        req.setAttribute("customer",baseCustomerService.fetch(place.getCustomerId()==null?"":place.getCustomerId()));
        req.setAttribute("person",basePersonService.fetch(place.getPersonId()==null?"":place.getPersonId()));
    }

    @At("/editDo")
    @Ok("json")
    @RequiresPermissions("platform.base.place.edit")
    @SLog(tag = "base_place", msg = "${args[0].id}")
    public Object editDo(@Param("..")base_place basePlace, HttpServletRequest req) {
		try {
            basePlace.setOpBy(StringUtil.getUid());
			basePlace.setOpAt((int) (System.currentTimeMillis() / 1000));
			basePlaceService.update(basePlace);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}

    }

    @At({"/delete/?", "/delete"})
    @Ok("json")
    @RequiresPermissions("platform.base.place.delete")
    @SLog(tag = "base_place", msg = "${req.getAttribute('id')}")
    public Object delete(String id, @Param("ids")  String[] ids, HttpServletRequest req) {
		try {
//			if(ids!=null&&ids.length>0){
//				basePlaceService.delete(ids);
//    			req.setAttribute("id", org.apache.shiro.util.StringUtils.toString(ids));
//			}else{
//				basePlaceService.delete(id);
//    			req.setAttribute("id", id);
//			}

            base_place place = basePlaceService.fetch(id);
            req.setAttribute("name", place.getPlacecode());

            basePlaceService.deleteAndChild(place);

            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }


//        try {
//            base_dept dept = baseDeptService.fetch(id);
//            req.setAttribute("name", dept.getDeptname());
//
//            baseDeptService.deleteAndChild(dept);
//            return Result.success("system.success");
//        } catch (Exception e) {
//            return Result.error("system.error");
//        }
    }

    @At("/detail/?")
    @Ok("beetl:/platform/base/place/detail.html")
    @RequiresPermissions("platform.base.place.select")
	public void detail(String id, HttpServletRequest req) {
        System.out.println("111");
		if (!Strings.isBlank(id)) {
            base_place place=basePlaceService.fetch(id);
            if(!Strings.isBlank(place.getAreaId())){
                Sys_dict area= sysDictService.fetch(place.getAreaId());
                place.setArea(area);
            }
            if(!Strings.isBlank(place.getTypeId())){
                Sys_dict type = sysDictService.fetch(place.getTypeId());
                place.setType(type);
            }
            if(!Strings.isBlank(place.getCtrlId())){
                Sys_dict ctrl = sysDictService.fetch(place.getCtrlId());
                place.setCtrl(ctrl);
            }
            req.setAttribute("obj", place);
            req.setAttribute("parentPlace", basePlaceService.fetch(place.getParentId()==null?"":place.getParentId()));
            req.setAttribute("airport",baseAirportService.fetch(place.getAirportId()==null?"":place.getAirportId()));
            req.setAttribute("customer",baseCustomerService.fetch(place.getCustomerId()==null?"":place.getCustomerId()));
            req.setAttribute("person",basePersonService.fetch(place.getPersonId()==null?"":place.getPersonId()));
		}else{
            req.setAttribute("obj", null);
            req.setAttribute("parentPlace", null);
            req.setAttribute("airport",null);
            req.setAttribute("customer",null);
            req.setAttribute("person",null);
        }
    }

    @At
    @Ok("beetl:/platform/base/place/selectAirport.html")
    @RequiresPermissions("platform.base.place")
    public void selectAirport(HttpServletRequest req) {

    }


    @At
    @Ok("beetl:/platform/base/place/selectCustomer.html")
    @RequiresPermissions("platform.base.place")
    public void selectCustomer(HttpServletRequest req) {

    }

    @At
    @Ok("beetl:/platform/base/place/selectPerson.html")
    @RequiresPermissions("platform.base.place")
    public void selectPerson(HttpServletRequest req) {

    }


    @At("/bindDDL")
    @Ok("json")
    @RequiresPermissions("platform.base.place")
    public Object bindDDL(String name,String code)
    {

        String parentid=sysDictService.getIdByNameAndCode(name,code);
        List<Sys_dict> list=sysDictService.query(Cnd.where("parentId","=", parentid).asc("location"));

        return list;
    }

    @At
    @Ok("json")
    @RequiresPermissions("platform.base.place")
    public Object tree(@Param("pid") String pid) {
        List<base_place> list = basePlaceService.query(Cnd.where("parentId", "=", Strings.sBlank(pid)==""?"":pid));
        List<Map<String, Object>> tree = new ArrayList<>();
        for (base_place place : list) {
            Map<String, Object> obj = new HashMap<>();
            obj.put("id", place.getId());
            obj.put("text", place.getPlacecode()+"|"+place.getPlacename());
            obj.put("children", place.isHasChildren());
            tree.add(obj);
        }
        return tree;
    }

    @At("/child/?")
    @Ok("beetl:/platform/base/place/child.html")
    @RequiresPermissions("platform.base.place")
    public Object child(String id) {
        return basePlaceService.query(Cnd.where("parentId", "=", id).asc("path"),"airport|customer|person");
    }
//
//    @At({"/bindDDLByMobile"})
//    @Ok("jsonp:full")
//    public Object bindDDLByMobile(@Param("airportId") String airportId)
//    {
//
//        try{
//            if(!Strings.isBlank(airportId)){
//                List<base_place> place =basePlaceService.query(Cnd.where("airportId","=",airportId).orderBy("createTime","desc"),"airport|area|customer|type|ctrl|person");
//                return place;
//            }
//            return null;
//        }
//        catch (Exception e){
//            return  null;
//        }
//
//    }

//    @At("/getPlacebyM")
//    @Ok("jsonp:full")
//    public Object getPlacebyM(@Param("id") String id){
//        try{
//            if(!Strings.isBlank(id)){
//                return  basePlaceService.query(Cnd.where("id","=",id),"airport|area|customer|type|ctrl|person");
//
//            }
//            return null;
//        }
//        catch (Exception e){
//            return  null;
//        }
//    }

//    @At("/updatePlacebyM")
//    @Ok("jsonp:full")
//    public Object updatePlacebyM(@Param("id") String id,@Param("placecode") String placecode,@Param("parengId") String parentId,@Param("path") String path,@Param("airportId") String airportId,@Param("customerId") String customerId,@Param("areaId") String areaId,@Param("typeId") String typeId,@Param("placename") String placename,@Param("ctrlId") String ctrlId,@Param("personId") String personId,@Param("telephone") String telephone,@Param("address") String address,@Param("position") String position,@Param("describ") String describ){
//        try{
//            base_place place;
//            if(!Strings.isBlank(id)) {
//                place = basePlaceService.fetch(id);
//            }else{
//                place=new base_place();
//            }
//            if(!Strings.isBlank(ctrlId))
//                place.setCtrlId(ctrlId);
//            if(!Strings.isBlank(typeId))
//                place.setTypeId(typeId);
//            if(!Strings.isBlank(address))
//                place.setAddress(address);
//            if(!Strings.isBlank(airportId))
//                place.setAirportId(airportId);
//            if(!Strings.isBlank(areaId))
//                place.setAreaId(areaId);
//            if(!Strings.isBlank(customerId))
//                place.setCustomerId(customerId);
//            if(!Strings.isBlank(describ))
//                place.setDescrib(describ);
//            if(!Strings.isBlank(parentId))
//                place.setParentId(parentId);
//            if(!Strings.isBlank(path))
//                place.setPath(path);
//            if(!Strings.isBlank(personId))
//                place.setPersonId(personId);
//            if(!Strings.isBlank(placecode))
//                place.setPlacecode(placecode);
//            if(!Strings.isBlank(placename))
//                place.setPlacename(placename);
//            if(!Strings.isBlank(position))
//                place.setPosition(position);
//            if(!Strings.isBlank(telephone))
//                place.setTelephone(telephone);
//            if(!Strings.isBlank(id)) {
//                basePlaceService.update(place);
//                return Result.success("更新成功");
//            }else{
//                basePlaceService.insert(place);
//                return Result.success("新增成功");
//            }
//
//        }
//        catch(Exception e){
//            return Result.error("更新失败");
//        }
//    }

//    @At("/getPlaceBym")
//    @Ok("jsonp:full")
//    public Object getPlaceBym(@Param("airportid") String airportid){
//        try {
//            Cnd cnd = Cnd.NEW();
//            if(!Strings.isBlank(airportid)){
//                cnd.and("airportId", "=", airportid);
//                List<base_place> places = basePlaceService.query(cnd,"airport|area|customer|type|ctrl|person");
//                return places;
//            }
//            else{
//                return null;
//            }
//
//        }
//        catch(Exception ex){
//            return null;
//        }
//    }
@At("/countByNum")
@Ok("json:full")

public Object countByNum(@Param("num")String num) {
    return basePlaceService.count("base_place",Cnd.where("placecode","=",num));
}}
