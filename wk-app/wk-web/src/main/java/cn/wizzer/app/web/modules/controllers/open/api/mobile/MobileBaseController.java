package cn.wizzer.app.web.modules.controllers.open.api.mobile;

import cn.wizzer.app.base.modules.models.*;
import cn.wizzer.app.base.modules.services.*;
import cn.wizzer.app.logistics.modules.models.logistics_entourage;
import cn.wizzer.app.logistics.modules.services.LogisticsEntourageService;
import cn.wizzer.app.logistics.modules.services.LogisticsOrderService;
import cn.wizzer.app.sys.modules.models.Sys_dict;
import cn.wizzer.app.sys.modules.models.Sys_mobile;
import cn.wizzer.app.sys.modules.models.Sys_unit;
import cn.wizzer.app.sys.modules.models.Sys_user;
import cn.wizzer.app.sys.modules.services.SysDictService;
import cn.wizzer.app.sys.modules.services.SysMobileService;
import cn.wizzer.app.sys.modules.services.SysUnitService;
import cn.wizzer.app.sys.modules.services.SysUserService;
import cn.wizzer.app.web.commons.filter.TokenFilter;
import cn.wizzer.framework.base.Result;
import cn.wizzer.framework.base.ValidatException;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Record;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.Sql;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.mvc.annotation.*;
import org.nutz.mvc.filter.CrossOriginFilter;
import org.nutz.plugins.apidoc.annotation.Api;
import org.nutz.plugins.apidoc.annotation.ApiMatchMode;
import org.nutz.plugins.apidoc.annotation.ApiParam;
import org.nutz.plugins.apidoc.annotation.ReturnKey;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@IocBean
@At("/open/mobile/base")
@Filters({@By(type = TokenFilter.class)})
@Api(name = "基础数据API", match = ApiMatchMode.ALL, description = "基础资料api接口")
public class MobileBaseController {

    @Inject
    private BasePlaceService basePlaceService;
    @Inject
    private SysDictService dictService;
    @Inject
    private BaseAirportService baseAirportService;
    @Inject
    private BaseCustomerService baseCustomerService;
    @Inject
    private BasePersonService basePersonService;
    @Inject
    private BaseCnctobjService baseCnctobjService;
    @Inject
    private SysUnitService sysUnitService;
    @Inject
    private SysUserService sysUserService;
    @Inject
    private BasePersonpoolService basePersonpoolService;
    @Inject
    private BaseVehicleService baseVehicleService;
    @Inject
    private SysDictService sysDictService;
    @Inject
    private LogisticsEntourageService logisticsEntourageService;
    @Inject
    private LogisticsOrderService logisticsOrderService;
    @Inject
    private SysMobileService sysMobileService;

    @Inject
    private BaseAirmeterialService baseAirmeterialService;
    @Inject
    private Dao dao;

    @At({"/bindDDLByMobile"})
    @Ok("json")
    @Filters({@By(type = CrossOriginFilter.class), @By(type = TokenFilter.class)})
    @Api(name = "获取地点列表"
            , params = {
            @ApiParam(name = "keyword", type = "String", description = "查询关键字", optional = true),
            @ApiParam(name = "airportId", type = "String", description = "机场id")
            , @ApiParam(name = "pagenumber", type = "Integer", description = "当前页")
            , @ApiParam(name = "pagesize", type = "Integer", description = "默认返回10条", optional = true)
    }
            , ok = {
            @ReturnKey(key = "id", description = "地点id"),
            @ReturnKey(key = "placename", description = "地点名称")
    }
    )
    public Object bindDDLByMobile(@Param("keyword") String keyword , @Param("airportId") String airportId, @Param("pagenumber") Integer pagenumber, @Param("pagesize") Integer pagesize) {
        try {
            return Result.success("system.success", basePlaceService.bindDDLByMobile(keyword,airportId, pagenumber, pagesize));
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("system.error", e);
        }
    }

    @At({"/getDictList"})
    @Ok("json")
    @Filters({@By(type = CrossOriginFilter.class), @By(type = TokenFilter.class)})
    @Api(name = "获取数据字典列表"
            , params = {@ApiParam(name = "parentName", type = "String", description = "父级名称")
            , @ApiParam(name = "parentCode", type = "String", description = "父级编码"),
    }
            , ok = {@ReturnKey(key = "id", description = "id")
            , @ReturnKey(key = "name", description = "名称")
            , @ReturnKey(key = "code", description = "机构编码")
            , @ReturnKey(key = "location", description = "排序字段")
            , @ReturnKey(key = "hasChildren", description = "是否有子节点")}
    )
    public Object getDictList(@Param("parentName") String parentName, @Param("parentCode") String parentCode) {
        try {
            String parentId = dictService.getIdByNameAndCode(parentName, parentCode);
            if (!Strings.isBlank(parentId)) {
                List<Sys_dict> sys_dict = dictService.query(Cnd.where("parentId", "=", parentId));
                return Result.success("system.success", sys_dict);
            }
            throw new ValidatException("参数不能为空!");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("system.error", e);
        }

    }


    @At("/updatePlacebyM")
    @Ok("json")
    @Filters({@By(type = CrossOriginFilter.class), @By(type = TokenFilter.class)})
    @Api(name = "新增或更新地点"
            , params = {@ApiParam(name = "id", type = "String", description = "地点ID，非必填，有ID且存在时则表明update", optional = true)
            , @ApiParam(name = "placecode", type = "String", description = "地点编码", optional = true)
            , @ApiParam(name = "parentId", type = "String", description = "父id", optional = true)
            , @ApiParam(name = "path", type = "String", description = "路径", optional = true)
            , @ApiParam(name = "airportId", type = "String", description = "机场ID", optional = true)
            , @ApiParam(name = "customerId", type = "String", description = "单位ID", optional = true)
            , @ApiParam(name = "areaId", type = "String", description = "区域ID", optional = true)
            , @ApiParam(name = "typeId", type = "String", description = "类型ID", optional = true)
            , @ApiParam(name = "placename", type = "String", description = "地点名称", optional = true)
            , @ApiParam(name = "ctrlId", type = "String", description = "管控点ID", optional = true)
            , @ApiParam(name = "personId", type = "String", description = "负责人ID", optional = true)
            , @ApiParam(name = "telephone", type = "String", description = "联系电话", optional = true)
            , @ApiParam(name = "address", type = "String", description = "地址", optional = true)
            , @ApiParam(name = "position", type = "String", description = "经纬度", optional = true)
            , @ApiParam(name = "describ", type = "String", description = "备注", optional = true)}
            , ok = {@ReturnKey(key = "code", description = "成功时code=0")}
    )
    public Object updatePlacebyM(@Param("id") String id, @Param("placecode") String placecode, @Param("parentId") String parentId, @Param("path") String path, @Param("airportId") String airportId
            , @Param("customerId") String customerId, @Param("areaId") String areaId, @Param("typeId") String typeId, @Param("placename") String placename, @Param("ctrlId") String ctrlId
            , @Param("personId") String personId, @Param("telephone") String telephone, @Param("address") String address, @Param("position") String position, @Param("describ") String describ) {
        try {
            return basePlaceService.updatePlaceByM(id, placecode, parentId, path, airportId, customerId, areaId, typeId, placename, ctrlId, personId, telephone, address, position, describ);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("system.error", e);
        }
    }

    @At("/getPlacebyM")
    @Ok("json")
    @Filters({@By(type = CrossOriginFilter.class), @By(type = TokenFilter.class)})
    @Api(name = "得到地点信息"
            , params = {
            @ApiParam(name = "id", type = "String", description = "地点ID"),
            @ApiParam(name = "pagenumber", type = "Integer", description = "当前页")
            , @ApiParam(name = "pagesize", type = "Integer", description = "默认返回10条", optional = true)
    }
            , ok = {@ReturnKey(key = "placecode", description = "地点编号")
            , @ReturnKey(key = "placename", description = "地点名称")
            , @ReturnKey(key = "telephone", description = "联系电话")
            , @ReturnKey(key = "address", description = "地址")
            , @ReturnKey(key = "position", description = "地理位置")
            , @ReturnKey(key = "describ", description = "备注")
            , @ReturnKey(key = "customername", description = "客户名称")
            , @ReturnKey(key = "customerId", description = "客户ID")
            , @ReturnKey(key = "areaname", description = "区域名称")
            , @ReturnKey(key = "areaId", description = "区域ID")
            , @ReturnKey(key = "typeId", description = "地点类型ID")
            , @ReturnKey(key = "path", description = "地点范围")
            , @ReturnKey(key = "ctrlId", description = "管控地ID")
            , @ReturnKey(key = "ctrlname", description = "管控地名称")
            , @ReturnKey(key = "personname", description = "人员名称")
            , @ReturnKey(key = "personId", description = "人员ID")
    }
    )
    public Object getPlacebyM(@Param("id") String id, @Param("pagenumber") Integer pagenumber, @Param("pagesize") Integer pagesize) {
        try {
            if (!Strings.isBlank(id)) {
                return Result.success("system.success", basePlaceService.getPlaceByMobile(id, pagenumber, pagesize));
            }
            throw new ValidatException("参数不能为空!");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("system.error", e);
        }
    }

    @At("/getAirportbyM")
    @Ok("json")
    @Filters({@By(type = CrossOriginFilter.class), @By(type = TokenFilter.class)})
    @Api(name = "得到机场信息"
            , params = {@ApiParam(name = "personid", type = "String", description = "人员ID")
    }
            , ok = {@ReturnKey(key = "airportid", description = "机场ID")
            , @ReturnKey(key = "airportnum", description = "机场编号")
            , @ReturnKey(key = "airportname", description = "机场名称")
    }
    )
    public Object getAirportbyM(@Param("personid") String personid) {
        try {
            return Result.success("system.success", baseAirportService.getInfo(personid));
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("system.error", e);
        }
    }


    @At("/getCustomerbyM")
    @Ok("json")
    @Filters({@By(type = CrossOriginFilter.class), @By(type = TokenFilter.class)})
    @Api(name = "得到单位列表信息"
            , params = {
            @ApiParam(name = "pagenumber", type = "Integer", description = "当前页")
            , @ApiParam(name = "pagesize", type = "Integer", description = "默认返回10条", optional = true)
    }
            , ok = {@ReturnKey(key = "id", description = "客户ID")
            , @ReturnKey(key = "customercode", description = "客户编号")
            , @ReturnKey(key = "customername", description = "客户名称")
            , @ReturnKey(key = "customeraddress", description = "客户地址")
            , @ReturnKey(key = "customertel", description = "联系电话")
            , @ReturnKey(key = "cusnum", description = "编号开头")
            , @ReturnKey(key = "customerdecrib", description = "备注")}
    )
    public Object getCustomerbyM(@Param("pagenumber") Integer pagenumber, @Param("pagesize") Integer pagesize) {
        try {
            return Result.success("system.success", baseCustomerService.getCustomerByM(pagenumber, pagesize));
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("system.error", e);
        }
    }

    @At({"/bindDDLbyM"})
    @Ok("json")
    @Filters({@By(type = CrossOriginFilter.class), @By(type = TokenFilter.class)})
    @Api(name = "通过路径编码得到数据字典列表"
            , params = {@ApiParam(name = "path", type = "String", description = "路径编码")
            , @ApiParam(name = "pagenumber", type = "Integer", description = "当前页")
            , @ApiParam(name = "pagesize", type = "Integer", description = "默认返回10条", optional = true)
    }
            , ok = {@ReturnKey(key = "id", description = "id")
            , @ReturnKey(key = "name", description = "名称")
            , @ReturnKey(key = "code", description = "机构编码")
            , @ReturnKey(key = "location", description = "排序字段")
            , @ReturnKey(key = "hasChildren", description = "是否有子节点")}
    )
    public Object bindDDLbyM(@Param("path") String path, @Param("pagenumber") Integer pagenumber, @Param("pagesize") Integer pagesize) {
        try {

            if (!Strings.isBlank(path)) {

                return Result.success("system.success", dictService.bindDDLbyM(path, pagenumber, pagesize));
            }
            throw new ValidatException("参数不能为空!");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("system.error", e);
        }

    }

    @At("/getUserInfo")
    @Ok("json")
    @Filters({@By(type = CrossOriginFilter.class), @By(type = TokenFilter.class)})
    @Api(name = "得到用户信息"
            , params = {@ApiParam(name = "userid", type = "String", description = "用户ID")}
            , ok = {
            @ReturnKey(key = "userId", description = "用户ID"),
            @ReturnKey(key = "personId", description = "人员ID"),
            @ReturnKey(key = "username", description = "人员姓名"),
            @ReturnKey(key = "airportid", description = "机场ID"),
            @ReturnKey(key = "unitid", description = "单位ID"),
            @ReturnKey(key = "airportname", description = "机场名称") ,
            @ReturnKey(key = "roleName", description = "角色名称"),
            @ReturnKey(key = "customerid", description = "客户ID"),
            @ReturnKey(key = "picPath", description = "用户头像路径"),
             }
    )
    public Object getUserInfo(@Param("userid") String userid) {
        try {
            //Subject subject = SecurityUtils.getSubject();
            Sys_user curUser = sysUserService.fetch(userid);
            if (curUser != null) {
                return Result.success("system.success", sysUserService.getUserInfo(curUser));
            }
            throw new ValidatException("参数不正确!");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("system.error", e);
        }

    }


    @At("/getPersonList")
    @Ok("json")
    @Filters({@By(type = CrossOriginFilter.class), @By(type = TokenFilter.class)})
    @Api(name = "获取人员列表"
            , params = {@ApiParam(name = "customerid", type = "String", description = "客户id")
            , @ApiParam(name = "personname", type = "String", description = "人员名称")
            , @ApiParam(name = "pagenumber", type = "Integer", description = "当前页")
            , @ApiParam(name = "pagesize", type = "Integer", description = "默认返回10条", optional = true)
    }
            , ok = {
            @ReturnKey(key = "personid", description = "人员id")
            , @ReturnKey(key = "userid", description = "用户id")
            , @ReturnKey(key = "personname", description = "名称")
            , @ReturnKey(key = "jobnum", description = "工号")
    }
    )
    public Object getPersonList(@Param("customerid") String customerid,@Param("personname") String personname, @Param("pagenumber") Integer pagenumber, @Param("pagesize") Integer pagesize) {
        try {
            if (!Strings.isBlank(customerid)) {
                return Result.success("system.success", basePersonService.getPersonList(customerid,personname, pagenumber, pagesize));
            }
            throw new ValidatException("参数不能为空!");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("system.error", e);
        }

    }



    @At("/updatePosition")
    @Ok("json")
    @Filters({@By(type = CrossOriginFilter.class), @By(type = TokenFilter.class)})
    @Api(name = "上传配送员位置信息"
            , params = {@ApiParam(name = "personid", type = "String", description = "员工ID")
            , @ApiParam(name = "position", type = "String", description = "经纬度")}
            , ok = {@ReturnKey(key = "code", description = "成功时code=0")}
    )
    public Object updatePosition(@Param("personid") String personid, @Param("position") String position, HttpServletRequest req) {
        try {
            basePersonpoolService.updatePosition(personid, position);
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }


    @At("/getVehiclebyM")
    @Ok("json")
    @Filters({@By(type = CrossOriginFilter.class), @By(type = TokenFilter.class)})
    @Api(name = "得到车辆列表"
            , params = {
            @ApiParam(name = "pagenumber", type = "Integer", description = "当前页")
            , @ApiParam(name = "pagesize", type = "Integer", description = "默认返回10条", optional = true)
    }
            , ok = {@ReturnKey(key = "id", description = "ID")
            , @ReturnKey(key = "vehiclenum", description = "车牌号")
    }
    )
    public Object getVehiclebyM(@Param("pagenumber") Integer pagenumber, @Param("pagesize") Integer pagesize) {
        try {

            return Result.success("system.success", baseVehicleService.getVehicleByMobile(pagenumber, pagesize));
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("system.error", e);
        }
    }

    @At("/setPersonBusybyM")
    @Ok("json")
    @Filters({@By(type = CrossOriginFilter.class), @By(type = TokenFilter.class)})
    @Api(name = "将随行人员改为忙碌", params = {@ApiParam(name = "personlist", type = "String", description = "人员ID，以逗号分割")}
            , ok = {@ReturnKey(key = "code", description = "成功时code=0")})
    public Object setPersonBusybyM(@Param("personlist") String personlist) {
        try {
            if (!Strings.isBlank(personlist)) {
                basePersonpoolService.setPersonByM(personlist);
                return Result.success("更新成功");
            }
            throw new ValidatException("参数不能为空!");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("更新失败", e);
        }
    }

    @At("/setBusybyM")
    @Ok("json")
    @Filters({@By(type = CrossOriginFilter.class), @By(type = TokenFilter.class)})
    @Api(name = "将车辆改为在运"
            , params = {@ApiParam(name = "vehicleid", type = "String", description = "车辆ID")}
            , ok = {@ReturnKey(key = "code", description = "成功时code=0")}
    )
    public Object setBusybyM(@Param("vehicleid") String vehicleid) {
        try {
            if (!Strings.isBlank(vehicleid)) {
                baseVehicleService.setBusybyM(vehicleid);
                return Result.success("更新成功");
            }
            throw new ValidatException("参数不能为空!");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("更新失败", e);
        }
    }

    @At("/getPersonbyID")
    @Ok("json")
    @Filters({@By(type = CrossOriginFilter.class), @By(type = TokenFilter.class)})
    @Api(name = "通过人员ID得到人员信息"
            , params = {@ApiParam(name = "id", type = "String", description = "人员ID")}
            , ok = {
            @ReturnKey(key = "personname", description = "人员姓名"),
    }
    )
    public Object getPersonbyID(@Param("id") String id) {
        try {
            if (!Strings.isBlank(id)) {
                return Result.success("system.success", basePersonService.getPersonByID(id));
            } else {
                return Result.error("没有找到该员工");
            }
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }

    @At("/getPersonpoolbyM")
    @Ok("json")
    @Filters({@By(type = CrossOriginFilter.class), @By(type = TokenFilter.class)})
    @Api(name = "得到当班人员信息列表"
            , params = {@ApiParam(name = "airportid", type = "String", description = "机场ID"), @ApiParam(name = "pagenumber", type = "Integer", description = "当前页")
            , @ApiParam(name = "pagesize", type = "Integer", description = "默认返回10条", optional = true)
    }
            , ok = {
            @ReturnKey(key = "id", description = "人员ID"),
            @ReturnKey(key = "personnum", description = "人员编号"),
            @ReturnKey(key = "personname", description = "人员姓名"),
            @ReturnKey(key = "airportid", description = "机场ID"),
            @ReturnKey(key = "unitid", description = "单位ID"),
            @ReturnKey(key = "deptid", description = "部门ID"),
            @ReturnKey(key = "jobid", description = "职务ID"),
            @ReturnKey(key = "postid", description = "岗位ID"),
            @ReturnKey(key = "placeid", description = "地点ID"),
            @ReturnKey(key = "emptypeId", description = "人员类型ID"),
            @ReturnKey(key = "customerId", description = "客户ID"),
    }
    )
    public Object getPersonpoolbyM(@Param("airportid") String airportid, @Param("pagenumber") Integer pagenumber, @Param("pagesize") Integer pagesize) {
        try {
            return Result.success("system.success", basePersonpoolService.getPersonpoolByM(airportid, pagenumber, pagesize));
        } catch (Exception e) {
            return Result.error("error");
        }
    }

    @At("/insertDatabyM")
    @Ok("json")
    @Filters({@By(type = CrossOriginFilter.class), @By(type = TokenFilter.class)})
    @Api(name = "插入随行人员信息,"
            , params = {
            @ApiParam(name = "orderid", type = "String", description = "订单ID"),
            @ApiParam(name = "personlist", type = "String", description = "人员ID，以逗号分割"),
            @ApiParam(name = "deviceid", type = "String", description = "设备编码,IOS端其他方法获得最近使用的车辆，可不传"),
            @ApiParam(name = "vehicleid", type = "String", description = "车辆ID"),
    }
            , ok = {
            @ReturnKey(key = "code", description = "成功code=0"),
    }
    )
    public Object insertDatabyM(@Param("orderid") String orderid, @Param("personlist") String personlist, @Param("deviceid") String deviceid, @Param("vehicleid") String vehicleid) {
        try {
            if (!Strings.isBlank(personlist) && !Strings.isBlank(orderid)) {
                logisticsEntourageService.insertDataByMobile(personlist, orderid);
            }
            if (!Strings.isBlank(vehicleid) && !Strings.isBlank(orderid)) {
                logisticsOrderService.updataVehicle(vehicleid, orderid);
            }
            if (!Strings.isBlank(vehicleid) && !Strings.isBlank(deviceid)) {
                sysMobileService.setVehicle(deviceid, vehicleid);
            }
            return Result.success("system.success");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("system.error");
        }
    }

    /*@At("/updateVehicle")
        @Ok("json")
        @Api(name = "将车辆ID更新到订单中"
                , params = {@ApiParam(name = "vehicleid", type = "String", description = "车辆ID")
                , @ApiParam(name = "orderid", type = "String", description = "订单ID")}, ok = {@ReturnKey(key = "code", description = "更新成功code=0")}
        )
        @Filters({@By(type = CrossOriginFilter.class), @By(type = TokenFilter.class)})
        public Object updateVehicle(@Param("vehicleid") String vehicleid, @Param("orderid") String orderid) {
            try {
                if (!Strings.isBlank(vehicleid) && !Strings.isBlank(orderid)) {
                   logisticsOrderService.updataVehicle(vehicleid,orderid);
                    return Result.success("更新成功");
                }
                return Result.error("更新失败");
            } catch (Exception e) {
                return Result.error("更新失败");
            }
        }*/
    /*@At("/setVehicle")
    @Ok("jsonp:full")
    public Object setVehicle(@Param("deviceid") String deviceid,@Param("vehicleid") String vehicleid){
        try{
            Cnd cnd=Cnd.NEW();
            cnd.and("deviceid","=",deviceid);
            Sys_mobile mobile = sysMobileService.fetch(cnd);
            if(mobile!=null){
                mobile.setVehicleid(vehicleid);
                return sysMobileService.updateIgnoreNull(mobile);
            }
            return null;
        }catch(Exception e){
            return null;
        }
    }
    */
    @At("/getPlaceListByMobile")
    @Ok("json")
    @Filters({@By(type = CrossOriginFilter.class), @By(type = TokenFilter.class)})
    @Api(name = "(需求端：新增订单交互1，出发地界面)获取地点列表(),分页待定(H5不分页显示)"
            , params = {
            @ApiParam(name = "keyword", type = "String", description = "查询关键字,后台高级查询placecode地点编码、placename地点名称"),
            @ApiParam(name = "airportId", type = "String", description = "所属机场ID"),
    }
            , ok = {
            @ReturnKey(key = "placeId", description = "地点ID"),
            @ReturnKey(key = "placeName", description = "地点名称"),
    }
    )
    public Object getPlaceListByMobile(@Param("keyword") String keyword, @Param("airportId") String airportId) {
        try {
            return Result.success("system.success", basePlaceService.getPlaceListByMobile(keyword, airportId));
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }


    @At("/getIdByMaterielNum")
    @Ok("json")
    @Api(name = "(需求端)根据物料件号获得当前物料的ID" , params = {
            @ApiParam(name = "materielnum", type = "String", description = "件号/自编号"),
    }, ok = {
            @ReturnKey(key= "id",  description = "物料ID"),
    }
    )
    @Filters({@By(type = CrossOriginFilter.class), @By(type = TokenFilter.class)})
    public Object getIdByMaterielNum(@Param("materielnum")String materielnum){
        try {


            return Result.success("system.success",baseAirmeterialService.getIdByMaterielNum(materielnum));
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }
}
