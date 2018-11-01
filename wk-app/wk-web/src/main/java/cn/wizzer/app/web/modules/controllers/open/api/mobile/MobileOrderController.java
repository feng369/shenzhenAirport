package cn.wizzer.app.web.modules.controllers.open.api.mobile;

import cn.wizzer.app.base.modules.services.BasePersonService;
import cn.wizzer.app.base.modules.services.BasePersonpoolService;
import cn.wizzer.app.logistics.modules.services.*;
import cn.wizzer.app.web.commons.filter.TokenFilter;
import cn.wizzer.app.web.commons.plugin.HttpClientUtil;
import cn.wizzer.app.web.commons.slog.annotation.SLog;
import cn.wizzer.framework.base.Result;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sun.prism.impl.Disposer;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.rpc.client.RPCServiceClient;
import org.apache.axis2.transport.http.HTTPConstants;
import org.nutz.dao.Dao;
import org.nutz.dao.entity.Record;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.*;
import org.nutz.mvc.filter.CrossOriginFilter;
import org.nutz.mvc.impl.AdaptorErrorContext;
import org.nutz.plugins.apidoc.annotation.Api;
import org.nutz.plugins.apidoc.annotation.ApiMatchMode;
import org.nutz.plugins.apidoc.annotation.ApiParam;
import org.nutz.plugins.apidoc.annotation.ReturnKey;

import javax.servlet.http.HttpServletRequest;
import javax.xml.namespace.QName;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@IocBean
@At("/open/mobile/order")
@Filters({@By(type = TokenFilter.class)})
@Api(name = "订单API", match = ApiMatchMode.ALL, description = "订单相关api接口")
public class MobileOrderController {

    private static final Log log = Logs.get();
    @Inject
    Dao dao;
    @Inject
    private LogisticsOrderService logisticsOrderService;
    @Inject
    private LogisticsDeliveryorderService logisticsDeliveryorderService;
    @Inject
    private LogisticsOrderdeliveryService logisticsOrderdeliveryService;
    @Inject
    private LogisticsOrderentryService logisticsOrderentryService;
    @Inject
    private LogisticsDeliveryorderentryService logisticsDeliveryorderentryService;
    @Inject
    private BasePersonpoolService basePersonpoolService;
    @Inject
    private LogisticsEntourageService logisticsEntourageService;
    @Inject
    private LogisticsOrderstepService logisticsOrderstepService;
    @Inject
    private LogisticsOrderrejectService logisticsOrderrejectService;
    @Inject
    private BasePersonService basePersonService;
    @Inject
    private LogisticsSendtraceService logisticsSendtraceService;

    @At("/getOrderCountByMobile")
    @Ok("json")
    @Filters({@By(type = CrossOriginFilter.class), @By(type = TokenFilter.class)})
    @Api(name = "获取待接订单、配送中订单数量、当日订单、当月订单、超时订单"
            , params = {@ApiParam(name = "personid", type = "String", description = "员工id，如果派单模式，则需要此参数"),@ApiParam(name = "receivedCheck", type = "boolean", description = "是否需要轮询待接订单,需要为true")}, ok = {@ReturnKey(key = "ordercount", description = "待接订单数量"),@ReturnKey(key = "sendingOrders", description = "配送中订单数量"),@ReturnKey(key = "todayOrders", description = "当日订单"),@ReturnKey(key = "monthOrders", description = "当月订单"),@ReturnKey(key = "overTimeOrders", description = "超时订单")}
    )
    public Object getOrderCountByMobile(@Param("personid") String personid,@Param("receivedCheck") boolean receivedCheck) {
        try {
            return Result.success("system.success", logisticsOrderService.getOrderCountByMobile(personid,receivedCheck));
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("system.error", e);
        }
    }


   /* @At("/getOrderCountByDateByMobile")
    @Ok("json")
    @Filters({@By(type = CrossOriginFilter.class), @By(type = TokenFilter.class)})
    @Api(name = "获取已完成订单数量"
            , params = {@ApiParam(name = "pstatus", type = "String", description = "8表示已完成,如果要得到超时订单，则不需此参数")
            , @ApiParam(name = "personid", type = "String", description = "员工id", optional = true)
            , @ApiParam(name = "curdate", type = "String", description = "today/month/overtime 时间阶段(today标识当日，month标识当月，overtime标识超时)")}
            , ok = {@ReturnKey(key = "ordercount", description = "订单数量")}
    )
    public Object getOrderCountByDateByMobile(@Param("pstatus") String pstatus, @Param("personid") String personid, @Param("curdate") String curdate) {
        try {
            Cnd cnd = Cnd.NEW();
            Map map = new HashMap();
            switch (curdate) {
                case "today":
                    cnd = Cnd.NEW().where(new Static("Date(cptime)='" + newDataTime.getTodayStr("yyyy-MM-dd") + "'"));
                    break;
                case "month":
                    Calendar c = Calendar.getInstance();
                    int month = c.get(Calendar.MONTH) + 1;
                    int year = c.get(Calendar.YEAR);
                    cnd = Cnd.NEW().where(new Static("month(cptime)=" + month + " and year(cptime)=" + year));
                    //return logisticsOrderService.count(cnd);
                    break;
                case "overtime":
                    String currentDate = newDataTime.getDateYMDHMS();
                    cnd.and("timerequest", "<", currentDate);
                    break;
                    default:
                    break;
            }

            if (!Strings.isBlank(pstatus))
                cnd.and("pstatus", "=", pstatus);

            List<logistics_order> logisticsOrders = logisticsOrderService.query(cnd);
            List<logistics_order> ltemp = new ArrayList<>();
            for (logistics_order logisticsOrder : logisticsOrders) {
                if (!Strings.isBlank(logisticsOrder.getDeliveryorderid())) {
                    logisticsOrder.setLogisticsDeliveryorder(logisticsDeliveryorderService.fetch(logisticsOrder.getDeliveryorderid()));
                    if (!Strings.isBlank(personid) && !personid.equals(logisticsOrder.getLogisticsDeliveryorder().getPersonid()))
                        ltemp.add(logisticsOrder);
                } else {
                    ltemp.add(logisticsOrder);
                }
            }
            for (logistics_order l : ltemp) {
                logisticsOrders.remove(l);
            }

            map.put("ordercount", logisticsOrders.size());
            return Result.success("system.success", map);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("system.error", e);
        }
    }*/

    /*
    @At("/getOrderCount")
     @Ok("json")
     @Api(name = "配送中订单数量"
             , params = {@ApiParam(name = "personid", type = "String", description = "员工id")
             , @ApiParam(name = "pstatus", type = "String", description = "状态为1表示配送中")}
             , ok = {@ReturnKey(key = "sendindCount", description = "配送中订单数量")}
     )
     @Filters({@By(type = CrossOriginFilter.class), @By(type = TokenFilter.class)})
     public Object getOrderCount(@Param("pstatus") String pstatus, @Param("personid") String personid) {
         try {
             int ordercount = 0;
             if (!Strings.isBlank(pstatus) && !Strings.isBlank(personid)) {
                 List<logistics_Deliveryorder> logisticsDeliveryorderList = logisticsDeliveryorderService.getDeliveryOrderByMobile(pstatus, personid);
                 int count = logisticsDeliveryorderList.size();
                 List<String> list = new ArrayList<>();
                 for (int i = 0; i < count; i++) {
                     list.add(logisticsDeliveryorderList.get(i).getId());
                 }
                 if (list.size() > 0) {
                     ordercount = logisticsOrderdeliveryService.getOrderCount(list);
                 }
             }
             HashMap map = new HashMap();
             map.put("sendindCount", ordercount);
             return Result.success("system.success", map);
         } catch (Exception e) {
             e.printStackTrace();
             return Result.error("system.error", e);
         }
     }
     */

    //配送中订单数量
    @At("/getOrderList")
    @Ok("json")
    @Api(name = "待接订单列表"
            , params = {
            @ApiParam(name = "personid", type = "String", description = "员工id",optional = true),
            @ApiParam(name = "dataType", type = "String", description = "需求端传getOwnerType,配合personid,表示得到下单人自己的",optional = true)
            , @ApiParam(name = "pstatus", type = "String", description = "状态为2表示待接单")
            , @ApiParam(name = "numorder", type = "String", description = "订单编号", optional = true)
            , @ApiParam(name = "endstock", type = "String", description = "目的地ID", optional = true)
            , @ApiParam(name = "btype", type = "String", description = "业务类型（航线运输，接送航班等)", optional = true)
            , @ApiParam(name = "lorderno", type = "String", description = "指令号", optional = true)
            , @ApiParam(name = "pagenumber", type = "Integer", description = "当前页")
            , @ApiParam(name = "pagesize", type = "Integer", description = "默认返回10条",optional = true)
    }
            , ok = {
            @ReturnKey(key = "id", description = "待接订单id"),
            @ReturnKey(key = "ordernum", description = "订单编号"),
            @ReturnKey(key = "timerequest", description = "时限要求"),
            @ReturnKey(key = "cptime", description = "订单完成时间"),
            @ReturnKey(key = "endstockphone", description = "目的地联系电话"),
            @ReturnKey(key = "startstockphone", description = "出发地联系电话"),
            @ReturnKey(key = "otype", description = "类型id"),
            @ReturnKey(key = "otypename", description = "类型名称"),
            @ReturnKey(key = "emergency", description = "紧急程度id"),
            @ReturnKey(key = "emergencypath", description = "紧急程度路径"),
            @ReturnKey(key = "startstockphone", description = "出发地联系电话"),
            @ReturnKey(key = "startstock", description = "出发地id"),
            @ReturnKey(key = "startstockname", description = "出发地名称"),
            @ReturnKey(key = "endstock", description = "目的地id"),
            @ReturnKey(key = "endstockname", description = "目的地名称"),
            @ReturnKey(key = "hctype", description = "航材类型id"),
            @ReturnKey(key = "hctypename", description = "航材类型名称"),
            @ReturnKey(key = "btype", description = "业务类型id"),
            @ReturnKey(key = "btypename", description = "业务类型名称"),
            @ReturnKey(key = "pstatus", description = "订单状态值"),
            @ReturnKey(key = "pstatusname", description = "订单状态名称"),
            @ReturnKey(key = "note", description = "订单备注"),
            @ReturnKey(key = "sdTimeoutReason", description = "超时原因(目前仅针对送达)"),
            @ReturnKey(key = "overtime", description = "超时状态 0未超时 1已超时"),
            @ReturnKey(key = "personname", description = "订单所属人姓名"),
            @ReturnKey(key = "tel", description = "订单所属人电话"),
            @ReturnKey(key = "haveSn", description = "是否当前订单含有周转件SN（预留序号）"),
            @ReturnKey(key = "lorderno", description = "订单故障号"),
            @ReturnKey(key = "inTime", description = "订单录入时间"),
            @ReturnKey(key = "msn", description = "飞机号"),
    }
    )
    @Filters({@By(type = CrossOriginFilter.class), @By(type = TokenFilter.class)})
    public Object getOrderList(@Param("personid") String personid,@Param("dataType")String dataType, @Param("pstatus") String pstatus, @Param("numorder") String numorder, @Param("startstock") String startstock, @Param("endstock") String endstock, @Param("btype") String btype, @Param("deliveryorderid") String deliveryorderid,@Param("pagenumber") Integer pagenumber,@Param("pagesize") Integer pagesize,@Param("lorderno")String lorderno) {
        try {
            return Result.success("system.success", logisticsOrderService.getOrderList(dataType,personid, pstatus, numorder, startstock, endstock, btype, deliveryorderid,pagenumber,pagesize,lorderno));
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("system.error", e);
        }
    }

    //配送单列表显示始发地-目的地
    @At("/getDeliveryOrderList")
    @Ok("json")
    @Filters({@By(type = CrossOriginFilter.class), @By(type = TokenFilter.class)})
    @Api(name = "配送中/完成/未完成运单列表"
            , params = {
              @ApiParam(name = "personid", type = "String", description = "员工id")
            , @ApiParam(name = "pstatus", type = "String", description = "1 配送中，2 未完成 ，3 已完成")                      // , @ApiParam(name = "deliveryordernum", type = "String", description = "配送单编号", optional = true)
            , @ApiParam(name = "pagenumber", type = "Integer", description = "当前页")
            , @ApiParam(name = "pagesize", type = "Integer", description = "默认返回10条",optional = true)
            }
            , ok = {
            @ReturnKey(key = "id", description = "运单id"),
            @ReturnKey(key = "deliveryorderid", description = "配送单关联id"),
            @ReturnKey(key = "startname", description = "配送起始地"),
            @ReturnKey(key = "endname", description = "配送目的地"),
            @ReturnKey(key = "receivePersonName", description = "配送人姓名"),
            @ReturnKey(key = "btypename", description = "业务类型名称"),
            @ReturnKey(key = "otypename", description = "订单类型名称"),
            @ReturnKey(key = "orderid", description = "订单id"),
            @ReturnKey(key = "pstatus", description = "订单状态"),
            @ReturnKey(key = "orderNum", description = "订单编号"),
            @ReturnKey(key = "haveSn", description = "订单中是否有周转件SN（预留序号）"),
            @ReturnKey(key = "lorderno", description = "指令号"),
            @ReturnKey(key = "msn", description = "飞机号"),
            @ReturnKey(key = "receivePersonName", description = "接单人姓名"),
            @ReturnKey(key = "receiveTime", description = "接单时间"),
            @ReturnKey(key = "sdTime", description = "送达时间"),
            @ReturnKey(key = "checkPersonName", description = "收货人姓名"),
            @ReturnKey(key = "checkOrderTime", description = "收货时间"),
    }
    )
    public Object getDeliveryOrderList(@Param("personid") String personid, @Param("pstatus") String pstatus, @Param("deliveryordernum") String deliveryordernum,@Param("pagenumber")Integer pagenumber,@Param("pagesize")Integer pagesize) {
        try {
            if (!Strings.isBlank(personid) && !Strings.isBlank(pstatus)) {
                return Result.success("system.success", logisticsDeliveryorderService.getDeliveryOrderList(personid,pstatus,deliveryordernum,pagenumber,pagesize));
            }
            return Result.success("system.error");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }

    //接单
    @At("/adddelDo")
    @Ok("json")
    @SLog(tag = "配送单", msg = "生成配送单")
    @Api(name = "生成配送单"
            , params = {
              @ApiParam(name = "orderid", type = "String", description = "订单号"),
              @ApiParam(name = "curPosition", type = "String", description = "配送员当前位置"),
              @ApiParam(name = "personid", type = "String", description = "员工ID")
              }, ok = {@ReturnKey(key = "code", description = "接单成功")
    }
    )
    @Filters({@By(type = CrossOriginFilter.class), @By(type = TokenFilter.class)})
    public Object adddelDo(@Param("orderid") String orderid, @Param("personid") String personid,@Param("curPosition")String curPosition, HttpServletRequest req) {
        try {
            logisticsOrderService.addelDo(orderid,personid,curPosition);
            return Result.success("system.success");

        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("system.error",e);
        }
    }

    @At("/updateVehicle")
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
            e.printStackTrace();
            return Result.error("system.error",e);
        }
    }

    @At({"/getOrderInfo"})
    @Ok("json")
    @Api(name = "获取订单信息"
            , params = {@ApiParam(name = "id", type = "String", description = "订单ID")}, ok = {
            @ReturnKey(key = "id", description = "订单对象id"),
            @ReturnKey(key = "ordernum", description = "订单对象编号"),
            @ReturnKey(key = "customername", description = "客户名称"),
            @ReturnKey(key = "otype", description = "订单对应数据字典订单类型ID"),
            @ReturnKey(key = "btypename", description = "业务类型名称"),
            @ReturnKey(key = "btype", description = "业务类型ID"),
            @ReturnKey(key = "emergencyname", description = "紧急程度名称"),
            @ReturnKey(key = "isPackage", description = "是否打包"),
            @ReturnKey(key = "startstockplacename", description = "出发地"),
            @ReturnKey(key = "endstockplacename", description = "目的地"),
            @ReturnKey(key = "startstockphone", description = "出发地联系电话"),
            @ReturnKey(key = "transporttypename", description = "运输类型名称"),
            @ReturnKey(key = "endstockphone", description = "目的地联系电话"),
            @ReturnKey(key = "airwaybillno", description = "航班/运单号"),
            @ReturnKey(key = "contractnum", description = "合同号/名称"),
            @ReturnKey(key = "timerequest", description = "时限要求"),
            @ReturnKey(key = "estimatetime", description = "预计送达时间"),
            @ReturnKey(key = "pstatus", description = "订单状态值"),
            @ReturnKey(key = "pstatusname", description = "订单状态名称"),
            @ReturnKey(key = "note", description = "备注"),
            @ReturnKey(key = "hctypename", description = "航材类型"),
            @ReturnKey(key = "otypename", description = "订单类型名称"),
            @ReturnKey(key = "receiver", description = "收货人"),
            @ReturnKey(key = "sdTimeoutReason", description = "超时原因(目前仅针对送达)"),
            @ReturnKey(key = "overtime", description = "超时状态 0未超时 1已超时"),
            @ReturnKey(key = "personname", description = "订单所属人姓名"),
            @ReturnKey(key = "tel", description = "订单所属人电话"),
    }
    )
    @Filters({@By(type = CrossOriginFilter.class), @By(type = TokenFilter.class)})
    public Object getOrderInfo(@Param("id") String id) {
        try {
            return Result.success("system.success", logisticsOrderService.getOrderInfo(id));
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("system.error",e);
        }
    }

    @At("/updateDvSteps")
    @Ok("json")
    @Api(name = "订单完成归档"
            , params = {
            @ApiParam(name = "orderid", type = "String", description = "订单ID"),
            @ApiParam(name = "content", type = "String", description = "备注"),
            @ApiParam(name = "stepnum", type = "String", description = "步骤编号"),
            @ApiParam(name = "toreason", type = "String", description = "超时原因(送达超时必填)",optional = true),
            @ApiParam(name = "pid", type = "String", description = "员工ID")

    },
            ok = {@ReturnKey(key = "rslt", description = "归档更新影响行数")}
    )
    @Filters({@By(type = CrossOriginFilter.class), @By(type = TokenFilter.class)})
    public Object updateDvSteps(@Param("orderid") String orderid, @Param("content") String content, @Param("stepnum") String stepnum, @Param("toreason") String toreason, @Param("pid") String personid) {
        /************
         * 1. 通过订单ID orderid 查询到订单类型otype
         * 2. 通过otype和stepnum从orderstep表中得到
         * 3. logisticsDeliveryorderentryService.upDventry修改配送状态，核料、送达、完成
         * 4. 如果是在时限内的送达，则直接完成。
         * 5. 如果是送达和完成状态，则反过来再需要修改订单、配送单的状态
         * 6. 通过订单ID找到订单，修改订单状态
         * 7. 通过订单ID找到对应的配送单ID
         * 8. 通过配送单ID找到配送单下所有的订单
         * 9. 循环订单得到最小的pstatus值
         * 10. 将pstatus值给配送单
         * 11. 如果是完成状态，则查找当前配送员下是否还存在未完成的配送单
         * 12. 如果配送单都已经完成，则修改当前配送员状态为空闲
         ************/
        try {
            return Result.success("system.success",logisticsOrderService.updateDvSteps(orderid,content,stepnum,toreason,personid));
        }catch (Exception e){
            e.printStackTrace();
            return Result.error("system.error",e);
        }
    }

    @At("/isSDOrderOvertime")
    @Ok("json")
    @Api(name = "判断送达订单是否超时"
            , params = {
            @ApiParam(name = "orderid", type = "String", description = "订单ID"),
    },
            ok = {@ReturnKey(key = "overtime", description = "超时1 未超时0")}
    )
    @Filters({@By(type = CrossOriginFilter.class), @By(type = TokenFilter.class)})
    public Object isSDOrderOvertime(@Param("orderid") String orderid) {
        try {
            return Result.success("system.success",logisticsOrderService.isSDOrderOvertime(orderid));
        }catch (Exception e){
            e.printStackTrace();
            return Result.error("system.error",e);
        }
    }



    @At("/insertReject")
    @Ok("json")
    @Api(name = "订单拒接"
            , params = {
            @ApiParam(name = "orderid", type = "String", description = "订单ID"),
            @ApiParam(name = "describ", type = "String", description = "备注"),
            @ApiParam(name = "rejectid", type = "String", description = "拒接原因"),
            @ApiParam(name = "personid", type = "String", description = "员工ID")

    },
            ok = {@ReturnKey(key = "code", description = "操作成功code=0")}
    )
    @Filters({@By(type = CrossOriginFilter.class), @By(type = TokenFilter.class)})
    public Object insertReject(@Param("orderid") String orderid,@Param("rejectid") String rejectid,@Param("describ") String describ,@Param("personid") String personid){
        try{
            if(!Strings.isBlank(orderid)){
                logisticsOrderrejectService.insetReject(orderid,rejectid,describ,personid);
                return  Result.success("system.success");
            }
            return Result.error("system.error");
        }
        catch (Exception e){
            e.printStackTrace();
            return Result.error("system.error",e);
        }
    }

    @At("/getStepbyMobile")
    @Ok("json")
    @Api(name = "获得订单步骤"
            , params = {
            @ApiParam(name = "orderid", type = "String", description = "订单ID"),
            @ApiParam(name = "stepnum", type = "String", description = "步骤")
    },
            ok = {
                    @ReturnKey(key = "step", description = "订单步骤对象步骤值"),
            }
    )
    @Filters({@By(type = CrossOriginFilter.class), @By(type = TokenFilter.class)})
    public Object getStepbyMobile(@Param("orderid") String orderid,@Param("stepnum") String stepnum){
        try{
            if(!Strings.isBlank(orderid)&&!Strings.isBlank(stepnum)){
                return  Result.success("system.success",logisticsOrderstepService.getStepByMobile(orderid,stepnum));
            }
            return  Result.error("system.error");
        }
        catch (Exception e){
            e.printStackTrace();
            return Result.error("system.error",e);
        }
    }

    @At("/getDataByMobile")
    @Ok("json")
    @Api(name = "通过订单id和步骤号得到当前步骤"
            , params = {
            @ApiParam(name = "orderid", type = "String", description = "订单ID"),
            @ApiParam(name = "step", type = "String", description = "步骤")
    },
            ok = {
                    @ReturnKey(key = "picname", description = "图片名称"),
                    @ReturnKey(key = "picpath", description = "图片地址")
            }
    )
    @Filters({@By(type = CrossOriginFilter.class), @By(type = TokenFilter.class)})
    public Object getDataByMobile(@Param("orderid") String orderid,@Param("step") String step){
        try{
            return Result.success("system.success",logisticsDeliveryorderentryService.getDataByMobile(orderid,step));
        }catch(Exception e){
            e.printStackTrace();
            return Result.error("system.error",e);
        }
    }

    @At("/getStepData")
    @Ok("json")
    @Api(name = "通过订单id和步骤号得到当前步骤2"
            , params = {@ApiParam(name = "orderid", type = "String", description = "订单ID"),
            @ApiParam(name = "step", type = "String", description = "步骤")},
            ok = {
                    @ReturnKey(key = "content", description = "内容"),
            }
    )
    @Filters({@By(type = CrossOriginFilter.class), @By(type = TokenFilter.class)})
    public Object getStepData(@Param("orderid") String orderid,@Param("step") String step){
        try {
            return Result.success("system.success",logisticsDeliveryorderentryService.getStepData(orderid,step));
        }catch (Exception e){
            e.printStackTrace();
            return Result.error("system.error",e);
        }
    }

    @At("/img")
    @Ok("json")
    @Api(name = "上传图片"
            , params = {
            @ApiParam(name = "filename", type = "String", description = "文件名"),
            @ApiParam(name = "orderid", type = "String", description = "订单id"),
            @ApiParam(name = "base64", type = "String", description = "图片"),
            @ApiParam(name = "step", type = "String", description = "步骤")
    },
            ok = {
                    @ReturnKey(key = "filePath", description = "图片上传的路径+文件名")
            }
    )
    @Filters({@By(type = CrossOriginFilter.class), @By(type = TokenFilter.class)})
    //AdaptorErrorContext必须是最后一个参数
    public Object img(@Param("filename") String filename, @Param("base64") String base64,@Param("orderid") String orderid,@Param("step") String step,HttpServletRequest req, AdaptorErrorContext err) {
        try {
            if (err != null && err.getAdaptorErr() != null) {
                return NutMap.NEW().addv("code", 1).addv("msg", "文件不合法");
            } else if (base64 == null) {
                return Result.error("空文件");
            } else {
                return Result.success("上传成功", logisticsDeliveryorderentryService.img(filename,base64,orderid,step,err));
            }
        } catch (Exception e) {
            log.debug(e.getMessage());
            return Result.error("system.error",e);
        } catch (Throwable e) {
            log.debug(e.getMessage());
            return Result.error("图片格式错误");
        }
    }

    @At("/delUpload")
    @Ok("json")
    @Api(name = "删除照片"
            , params = {
            @ApiParam(name = "filename", type = "String", description = "文件名"),
            @ApiParam(name = "spid", type = "String", description = "计划ID"),
            @ApiParam(name = "step", type = "String", description = "阶段")
    },
            ok = {@ReturnKey(key = "code", description = "操作成功")}
    )
    @Filters({@By(type = CrossOriginFilter.class), @By(type = TokenFilter.class)})
    public Object delUpload(@Param("filename") String filename,@Param("orderid") String orderid,@Param("step") String step){
        try{
            return logisticsDeliveryorderentryService.delUpload(filename,orderid,step);
        }catch (Exception e){
            e.printStackTrace();
            return Result.error("没有此文件");
        }
    }



    @At({"/getOrderEntryByOrderId"})
    @Ok("json")
    @Api(name = "获取订单中的物料清单"
            , params = {
            @ApiParam(name = "orderid", type = "String", description = "订单ID"),
            @ApiParam(name = "materialStock", type = "String", description = "所属仓库"),
            @ApiParam(name = "dataType", type = "String", description = "核料备注(getAllMateriaData)")
            , @ApiParam(name = "pagenumber", type = "Integer", description = "当前页",optional = true),
            @ApiParam(name = "pagesize", type = "Integer", description = "默认返回10条",optional = true)
    }, ok = {
            @ReturnKey(key = "materielnum", description = "锁定PN"),
            @ReturnKey(key = "materielname", description = "MR号"),
            @ReturnKey(key = "quantity", description = "数量"),
            @ReturnKey(key = "batch", description = "批号"),
            @ReturnKey(key = "pstatus", description = "配送状态 0 未进行 1 已进行"),
            @ReturnKey(key = "sequencenum", description = "项次"),
            @ReturnKey(key = "orderstate", description = "单项物料领用状态"),
            @ReturnKey(key = "stock", description = "库房号"),
            @ReturnKey(key = "location", description = "架位"),
            @ReturnKey(key = "pnr", description = "预留件号"),
            @ReturnKey(key = "sn", description = "预留序号"),
            @ReturnKey(key = "lorderno", description = "指令号(DE1234)"),
            @ReturnKey(key = "msn", description = "飞机号"),
            @ReturnKey(key = "materialStock", description = "物料所属仓位"),
    }
    )
    @Filters({@By(type = CrossOriginFilter.class), @By(type = TokenFilter.class)})
    public Object getOrderEntryByOrderId(@Param("orderid") String orderid,@Param("dataType")String  dataType,@Param("pagesize")Integer pagesize,@Param("pagenumber")Integer pagenumber) {
        try {
            return Result.success("system.success",logisticsOrderentryService.getOrderEntryByOrderId(orderid,dataType,pagesize,pagenumber));
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("system.error",e);
        }
    }


    @At("/updateOrderReject")
    @Ok("json")
    @Api(name = "订单拒接后,订单状态更新"
            , params = {@ApiParam(name = "id", type = "String", description = "订单ID")}, ok = {
            @ReturnKey(key = "code", description = "成功code=0")
    }
    )
    @Filters({@By(type = CrossOriginFilter.class), @By(type = TokenFilter.class)})
    public Object updateOrderReject(@Param("id") String id){
        try{
            if(!Strings.isBlank(id)){
                logisticsOrderService.updateOrderReject(id);
                return Result.success("更新成功");
            }
            return Result.error("更新失败");
        }
        catch (Exception e){
            e.printStackTrace();
            return Result.error("system.error",e);
        }
    }

    @At({"/getOrderPstatus"})
    @Ok("json")
    @Api(name = "获取订单状态(是否是已接单"
            , params = {@ApiParam(name = "id", type = "String", description = "订单ID")}, ok = {
            @ReturnKey(key = "pstatus", description = "订单状态"),
    }
    )
    @Filters({@By(type = CrossOriginFilter.class), @By(type = TokenFilter.class)})
    public Object getOrderPstatus(@Param("id") String id) {
        try {
            return Result.success("system.success", logisticsOrderService.getOrderPstatus(id));
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("system.error",e);
        }
    }

    //需求端新增接口

    @At({"/getOrderListOfCustomer"})
    @Ok("json")
    @Api(name = "(需求端首页)得到配送中的订单列表、待接订单数量"
            , params = {
            @ApiParam(name = "personid", type = "String", description = "需求所属人人员ID"),
            @ApiParam(name = "pstatus", type = "String", description = "保留字段",optional = true)
    }, ok = {
//            //起点位置,目的地,当前人的位置,order当前的状态
            @ReturnKey(key = "waitOrderNumber", description = "待接订单数量"),
            @ReturnKey(key = "deliveryOrderList", description = "得到配送中的订单列表"),
            @ReturnKey(key = "endstockname", description = "目的地"),
            @ReturnKey(key = "curPosition", description = "当前订单所在位置"),
            @ReturnKey(key = "pstatus", description = "订单当前的状态"),
            @ReturnKey(key = "btypename", description = "业务类型"),
    }
    )
    @Filters({@By(type = CrossOriginFilter.class), @By(type = TokenFilter.class)})
    public Object getOrderListOfCustomer(@Param("personid") String personid,@Param("pagenumber")Integer pagenumber,@Param("pagesize")Integer pagesize) {
        try {
            return Result.success("system.success", logisticsOrderService.getOrderListOfCustomer(personid));
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("system.error",e);
        }
    }

    @At("/haveSendingOrders")
    @Ok("json")
    @Api(name = "(配送端)是否有配送中订单"
            , params = {
            @ApiParam(name = "personid", type = "String", description = "配送人员ID"),
    }, ok = {
            @ReturnKey(key = "orderid", description = "订单ID"),
    }
    )
    @Filters({@By(type = CrossOriginFilter.class), @By(type = TokenFilter.class)})
    public Object haveSendingOrders(@Param("personid") String personid) {
        try {
            return Result.success("system.success", logisticsDeliveryorderService.haveSendingOrders(personid));
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("system.error",e);
        }
    }


    @At("/getOrderInfoOfCustomer")
    @Ok("json")
    @Api(name = "(需求端首页)点击图标得到配送中的订单详细信息"
            , params = {
            @ApiParam(name = "orderid", type = "String", description = "订单id"),
    }, ok = {
            @ReturnKey(key = "pstatus", description = "此订单状态"),
            @ReturnKey(key = "pstatusname", description = "此订单状态名称"),
            @ReturnKey(key = "startplace", description = "起点位置"),
            @ReturnKey(key = "endplace", description = "目的地"),
            @ReturnKey(key = "sendperson", description = "配送负责人"),
            @ReturnKey(key = "vehiclenum", description = "车牌号"),
//            @ReturnKey(key = "followername", description = "随行人员姓名"),
            @ReturnKey(key = "receiveOrderTime", description = "接单时间"),
            @ReturnKey(key = "hctypename", description = "航材类型"),
            @ReturnKey(key = "tel", description = "配送员手机"),
            @ReturnKey(key = "sdTimeoutReason", description = "超时原因(目前仅针对送达)"),
    }
    )
    @Filters({@By(type = CrossOriginFilter.class), @By(type = TokenFilter.class)})
    public Object getOrderInfoOfCustomer(@Param("orderid")String orderid) {
        try {
            return Result.success("system.success",logisticsOrderService.getOrderInfoOfCustomer(orderid));
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("system.error",e);
        }
    }


    @At("/insertSendTrace")
    @Ok("json")
    @Api(name = "(注意:此方法新增在配送端)每隔固定时间(前端控制)获取配送员位置,插入到sendtrace表,更新最新数据到logistics_recordtrace表"
            , params = {
            @ApiParam(name = "orderid", type = "String", description = "订单id"),
            @ApiParam(name = "personid", type = "String", description = "配送人员id"),
            @ApiParam(name = "position", type = "String", description = "当前地理位置"),
    }, ok = {
            @ReturnKey(key = "code", description = "成功code=0"),
    }
    )
    @Filters({@By(type = CrossOriginFilter.class), @By(type = TokenFilter.class)})
    public Object insertSendTrace(@Param("orderid") String orderid,@Param("personid")String personid,@Param("position")String position) {
        try {
            logisticsSendtraceService.insertSendTrace(orderid,personid,position);
            return Result.success("system.success");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("system.error",e);
        }
    }

    @At("/getDeliveryorderentryList")
    @Ok("json")
    @Api(name = "(需求端：订单跟踪页面)根据订单ID获得跟踪列表"
            , params = {
            @ApiParam(name = "orderid", type = "String", description = "订单id")
            , @ApiParam(name = "pagenumber", type = "Integer", description = "当前页,默认返回第一页")
            , @ApiParam(name = "pagesize", type = "Integer", description = "每页最多显示条数,默认返回10条",optional = true)
    }, ok = {
            @ReturnKey(key = "pstatus", description = "当前步骤完成状态(0：未完成，1：已完成)"),
            @ReturnKey(key = "operatetime", description = "步骤操作时间"),
            @ReturnKey(key = "step", description = "步骤"),
            @ReturnKey(key = "stepname", description = "步骤名称"),
        }
    )
    @Filters({@By(type = CrossOriginFilter.class), @By(type = TokenFilter.class)})
    public Object getDeliveryorderentryList(@Param("orderid") String orderid,@Param("pagenumber")Integer pagenumber,@Param("pagesize")Integer pagesize) {
        try {
            return Result.success("system.success",logisticsDeliveryorderentryService.getDeliveryorderentryList(orderid,pagenumber,pagesize));
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }


    @At("/getOrderListByPersonId")
    @Ok("json")
    @Api(name = "(需求端：订单列表页面)根据personid获取订单列表"
            , params = {
              @ApiParam(name = "personid", type = "String", description = "人员ID")
            , @ApiParam(name = "pagenumber", type = "Integer", description = "当前页,默认返回第一页")
            , @ApiParam(name = "pagesize", type = "Integer", description = "每页最多显示条数,默认返回10条",optional = true)
    }, ok = {
            /*
            @ReturnKey(key = "btype", description = "订单类型ID,对应数据字典ID"),
            @ReturnKey(key = "btypename", description = "订单类型名称,对应数据字典名称"),
            @ReturnKey(key = "hctype", description = "航材类型ID,对应数据字典ID"),
            @ReturnKey(key = "hctypename", description = "航材类型名称,对应数据字典名称"),
//          @ReturnKey(key = "startPlace", description = "出发地"),
            @ReturnKey(key = "endPlace", description = "目的地"),
            @ReturnKey(key = "pstatus", description = "订单状态值"),
            @ReturnKey(key = "pstatusName", description = "订单状态名称"),
            @ReturnKey(key = "opTime", description = "订单状态对应的操作时间"),
            @ReturnKey(key = "opTimeType", description = "订单状态对应的操作时间类型"),
            @ReturnKey(key = "orderid", description = "订单ID"),
            @ReturnKey(key = "receiver", description = "收货人"),
            @ReturnKey(key = "note", description = "订单备注"),
            @ReturnKey(key = "sdTimeoutReason", description = "超时原因(目前仅针对送达)"),
            @ReturnKey(key = "overtime", description = "超时状态 0未超时 1已超时"),
            @ReturnKey(key = "haveSn", description = "是都有预留序号 0:是 1:否"),
            */
         @ReturnKey(key = "orderId", description = "订单ID"),
         @ReturnKey(key = "orderPstatus", description = "订单状态"),
         @ReturnKey(key = "orderPstatusName", description = "订单状态名称"),
         @ReturnKey(key = "emerName", description = "紧急状态名称"),
         @ReturnKey(key = "btypename", description = "业务类型"),
         @ReturnKey(key = "inTime", description = "下单时间"),
         @ReturnKey(key = "receivePersonName", description = "接单人名称"),
         @ReturnKey(key = "receivePersonTel", description = "接单人电话"),
         @ReturnKey(key = "receiveOrderTime", description = "接单时间"),
         @ReturnKey(key = "checkPersonName", description = "收货人名称"),
         @ReturnKey(key = "checkOrderTime", description = "收货时间"),
         @ReturnKey(key = "endPlaceName", description = "目的地名称"),
         @ReturnKey(key = "picPath", description = "用户头像路径"),
         @ReturnKey(key = "haveSn", description = "是否有周转件 是:true"),
        }
    )
    @Filters({@By(type = CrossOriginFilter.class), @By(type = TokenFilter.class)})
    public Object getOrderListByPersonId(@Param("personid") String personid,@Param("pagenumber")Integer pagenumber,@Param("pagesize")Integer pagesize) {
        try {
            return Result.success("system.success",logisticsOrderService.getOrderListByPersonId(personid,pagenumber,pagesize));
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }


    @At("/addOrderByMobile")
    @Ok("json")
    @Api(name = "(需求端：新增订单页面)新增订单" , params = {
            @ApiParam(name = "btype", type = "String", description = "业务类型ID"),
            @ApiParam(name = "emergency", type = "String", description = "紧急程度ID"),
            @ApiParam(name = "hctype", type = "String",description = "航材类型ID"),
            @ApiParam(name = "startstock", type = "String",description = "出发地ID"),
            @ApiParam(name = "endstock", type = "String",description = "目的地ID"),
            @ApiParam(name = "userid", type = "String",description = "提交订单用户ID"),
            @ApiParam(name = "personid", type = "String",description = "提交订单人员ID"),
            @ApiParam(name = "customerid", type = "String",description = "客户ID"),
            @ApiParam(name = "note", type = "String",description = "备注",optional = true),
    }, ok = {
            @ReturnKey(key = "orderid", description = "订单ID"),
        }
    )
    @Filters({@By(type = CrossOriginFilter.class), @By(type = TokenFilter.class)})
    public Object addOrderByMobile(@Param("note")String note,@Param("btype")String btype,@Param("emergency")String emergency,@Param("hctype")String hctype,@Param("startstock")String startstock,@Param("endstock")String endstock,@Param("timerequest")String timerequest,@Param("customerid") String customerid,@Param("personid") String personid,@Param("userid") String userid) {
        try {
            //注:timerequest为保留字段
            return Result.success("system.success",logisticsOrderService.addOrderByMobile(btype,emergency,hctype,startstock,endstock,customerid,personid,userid,timerequest,note));
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("system.error",e);
        }
    }

    @At("/addOrderEntryByMobiile")
    @Ok("json")
    @Api(name = "(需求端：在提交订单前(可用循环方式)添加物料)添加物料" , params = {
            @ApiParam(name = "materielname", type = "String", description = "MR号"),
            @ApiParam(name = "materielnum", type = "String", description = "锁定PN"),
            @ApiParam(name = "sequencenum", type = "String", description = "项次"),
            @ApiParam(name = "batch", type = "String", description = "批号"),
            @ApiParam(name = "quantity", type = "String", description = "数量"),
            @ApiParam(name = "materielid", type = "String", description = "物料ID(基础资料的物料表base_airmeterial)"),
            @ApiParam(name = "pnr", type = "String", description = "预留件号"),
            @ApiParam(name = "sn", type = "String", description = "预留序号"),
            @ApiParam(name = "stock", type = "String", description = "库房"),
            @ApiParam(name = "location", type = "String", description = "架位"),
            @ApiParam(name = "state", type = "String", description = "单项物料领用状态(OPEN清单内未领出航材,ISSUE已配送过的航材)"),
            @ApiParam(name = "lorderno", type = "String", description = "指令号(类似DE1157318)"),
            @ApiParam(name = "msn", type = "String", description = "飞机号"),
            @ApiParam(name = "materialStock", type = "String", description = "所属仓库",optional = true),
    }, ok = {
            @ReturnKey(key = "code", description = "添加物料成功code=0"),
            @ReturnKey(key = "orderentryId", description = "物料ID"),
        }
    )
    @Filters({@By(type = CrossOriginFilter.class), @By(type = TokenFilter.class)})
    public Object addOrderEntryByMobiile(@Param("materielname")String materielname,@Param("materielnum")String materielnum,@Param("sequencenum")String sequencenum,@Param("batch")String batch,@Param("quantity")String quantity,@Param("materielid")String materielid,@Param("pnr")String pnr,@Param("sn")String sn,@Param("stock")String stock,@Param("location")String location,@Param("orderflag")String orderflag,@Param("lorderno")String lorderno,@Param("state")String state,@Param("msn")String msn,@Param("materialStock")String materialStock){
        try {
            return Result.success("system.success",logisticsOrderService.addOrderEntryByMobiile(materielid,materielname,materielnum,sequencenum,batch,quantity,pnr,sn,stock,location,orderflag,lorderno,state,msn,materialStock));
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("system.error",e);
        }
    }
    @At("/editOrderEntriesByMobile")
    @Ok("json")
    @Api(name = "(需求端：在提交订单后(可用循环方式))编辑物料" , params = {
            @ApiParam(name = "oderid", type = "String", description = "订单ID"),
//            @ApiParam(name = "orderEntryIDList", type = "String", description = "物料ID列表字符串，以','分隔,例'123,234,345'"),
            @ApiParam(name = "orderflag", type = "String", description = "唯一标识，用于编辑物料orderid"),
    }, ok = {
            @ReturnKey(key = "code", description = "编辑物料成功code=0"),
        }
    )
    @Filters({@By(type = CrossOriginFilter.class), @By(type = TokenFilter.class)})
    public Object editOrderEntriesByMobile(@Param("oderid")String orderid,@Param("orderflag")String orderflag){
        try {
            logisticsOrderentryService.editOrderEntriesByMobile(orderid,orderflag);
            return Result.success("system.success");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("system.error",e);
        }
    }


    //从ERP获取
    @At("/downloadFromERPByMobile")
    @Ok("json")
    @Api(name = "(需求端：物料导入页面)物料导入" , params = {
            @ApiParam(name = "orderno", type = "String", description = "故障号"),
            @ApiParam(name = "ordertype", type = "String", description = "故障号类型"),
    }, ok = {
            @ReturnKey(key = "OrderNoList", description = "故障号对应的物料列表"),
            @ReturnKey(key = "MRNO", description = "MR号，对应物料表materielname"),
            @ReturnKey(key = "PN_L",  description = "锁定PN，对应物料表materielnum"),
            @ReturnKey(key = "ITEMNO",  description = "项次，对应物料表sequencenum"),
            @ReturnKey(key = "BN",  description = "批号，对应物料表batch"),
            @ReturnKey(key = "QTY",  description = "数量，对应物料表quantity"),
            @ReturnKey(key = "STOCK",  description = "库房"),
            @ReturnKey(key = "LOCATION",  description = "架位"),
            @ReturnKey(key = "MSN",  description = "飞机号"),
    }
    )
    @Filters({@By(type = CrossOriginFilter.class), @By(type = TokenFilter.class)})
    public Object downloadFromERPByMobile(@Param("orderno") String orderno,@Param("ordertype")String ordertype){
        RPCServiceClient serviceClient = null;
        try {
//            String wurl ="http://10.14.1.18/MeWebService/services/JIWUService?wsdl";
//            String nameSpace = "http://service.peanuts.smartme.szair.com";
//            serviceClient = new RPCServiceClient();
//            Options options = serviceClient.getOptions();
//            // 这一步指定了该服务的提供地址
//            EndpointReference targetEPR = new EndpointReference(wurl);
//            // 将option绑定到该服务地址
//            options.setTo(targetEPR);
//            options.setManageSession(true);
//            options.setProperty(HTTPConstants.REUSE_HTTP_CLIENT, true);
//            options.setTimeOutInMilliSeconds(600000L);
//            // 添加具体要调用的方法，这个可以从该服务的wsdl文件中得知
//            // 第一个参数是该服务的targetNamespace，第二个为你所要调用的operation名称
//            QName namespace = new QName(nameSpace, "zh_material_dispatching");
//            // 输入参数
//            Long l_orderno = Long.valueOf(orderno);
//            String l_ordertype = ordertype;
//            Object[] param = new Object[] {l_ordertype,l_orderno};
//            // 指定返回的数据类型
//            Class[] clazz = new Class[] { String.class };
            // 设置返回值类型
            Object[] res=new Object[]{ Object.class};
//            res = serviceClient.invokeBlocking(namespace, param, clazz);
            String getUrl = "http://medet.shenzhenair.com/MeApp/tools/gps/getDataslogin?orderno="+orderno+"&ordertype="+ordertype+"&flag=SZAMEIMP";
            HttpClientUtil httpClientUtil = new HttpClientUtil();
            Object res1 = JSONArray.parse(httpClientUtil.doGet(getUrl, "UTF-8"));

            log.info("消息接口返回结果:" + res[0]);
            /*
            String rest =  "[
            {\"ORDERNO\":\"1466449\",\"ITEMNO\":\"1\",\"ORDERSTATE\":\"OPEN\",\"PN_L\":\"0-112-0016-2000\",\"PN_R\":\"0\",\"SN\":\"0\",\"BN\":\"0\",\"QTY\":\"1\"},
            {\"ORDERNO\":\"1466449\",\"ITEMNO\":\"2\",\"ORDERSTATE\":\"OPEN\",\"PN_L\":\"10-60751-1\",\"PN_R\":\"0\",\"SN\":\"0\",\"BN\":\"0\",\"QTY\":\"1\"},{\"ORDERNO\":\"1466449\",\"ITEMNO\":\"3\",\"ORDERSTATE\":\"OPEN\",\"PN_L\":\"101660-205\",\"PN_R\":\"0\",\"SN\":\"0\",\"BN\":\"0\",\"QTY\":\"1\"},{\"ORDERNO\":\"1466449\",\"ITEMNO\":\"4\",\"ORDERSTATE\":\"OPEN\",\"PN_L\":\"9059110-1\",\"PN_R\":\"9059110-1\",\"SN\":\"UNKM1778\",\"BN\":\"1447148\",\"QTY\":\"1\"},{\"ORDERNO\":\"1466449\",\"ITEMNO\":\"5\",\"ORDERSTATE\":\"OPEN\",\"PN_L\":\"8450B5\",\"PN_R\":\"8450B5\",\"SN\":\"8450B5-233\",\"BN\":\"1428418\",\"QTY\":\"1\"},{\"ORDERNO\":\"1466449\",\"ITEMNO\":\"7\",\"ORDERSTATE\":\"OPEN\",\"PN_L\":\"3291238-2\",\"PN_R\":\"0\",\"SN\":\"0\",\"BN\":\"0\",\"QTY\":\"1\"},{\"ORDERNO\":\"1466454\",\"ITEMNO\":\"1\",\"ORDERSTATE\":\"ISSUED\",\"PN_L\":\"3291238-2\",\"PN_R\":\"3291238-2\",\"SN\":\"10383\",\"BN\":\"890129\",\"QTY\":\"1\"}]";
             HashMap[] maps = Json.fromJsonAsArray(HashMap.class,rest);
             */
            List<HashMap> lists = Json.fromJsonAsList(HashMap.class,res1.toString());
            List<Object> onoList = new ArrayList<Object>();//存放需求单号
            Map<String,List<Object>> dataMap = new HashMap<String,List<Object>>();
            Iterator<HashMap> iterator = lists.iterator();
            while (iterator.hasNext()){
                HashMap<String,Object> map = iterator.next();
                String ono = (String) map.get("MRNO");
//                String state = (String) map.get("ORDERSTATE");
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    if(entry.getValue() == null ||  "".equals(entry.getValue().toString().trim())){
                        //将map中空值或空字符串转换为字符串0
                        map.put(entry.getKey(),"0");
                    }
                }
                //过滤掉已经领料的数据
                /*if(!"OPEN".equals(state)){
                    iterator.remove();
                    continue;
                }*/
                //按需求单号进行归类
                if(dataMap.containsKey(ono)){
                    List<Object> lm = dataMap.get(ono);
                    lm.add(map);
                    dataMap.put(ono,lm);
                }else{
                    onoList.add(ono);
                    List<Object> lm = new ArrayList<Object>();
                    lm.add(map);
                    dataMap.put(ono,lm);
                }
            }
             /*
             for(int i=0;i<lists.size();i++){
                HashMap map =lists.get(i);
                String ono = (String) map.get("ORDERNO");
                String state = (String) map.get("ORDERSTATE");
                //过滤掉已经领料的数据
                if(!"OPEN".equals(state)){
                    lists.remove(map);
                    i--;
                    continue;
                }
                //按需求单号进行归类
                if(dataMap.containsKey(ono)){
                    List<Object> lm = dataMap.get(ono);
                    lm.add(map);
                    dataMap.put(ono,lm);
                }else{
                    onoList.add(ono);
                    List<Object> lm = new ArrayList<Object>();
                    lm.add(map);
                    dataMap.put(ono,lm);
                }
            }*/
            dataMap.put("OrderNoList",onoList);
            org.json.JSONObject obj = new org.json.JSONObject(dataMap);
            System.out.println(obj.toString());
            return Result.success("system.success",dataMap);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            if(e instanceof AxisFault){
                return Result.error("system.error",new Exception("ERP系统接口查询失败："+e.getMessage()));
            }
            e.printStackTrace();
            System.out.println(e.toString());
            return Result.error("system.error",e);
        }finally {
            try {
                if(serviceClient!=null)
                    serviceClient.cleanupTransport();
            } catch (AxisFault axisFault) {
                axisFault.printStackTrace();
            }
        }
    }


    @At("/getOrderEntryList")
    @Ok("json")
    @Api(name = "(需求端：物料清单页面)根据物料ID列表获得物料列表" , params = {
            @ApiParam(name = "orderEntryIDList", type = "String", description = "物料ID列表字符串，以','分隔,例'123,234,345'"),
    }, ok = {
            @ReturnKey(key= "id",  description = "物料ID"),
            @ReturnKey(key= "materielname",  description = "MR号"),
            @ReturnKey(key= "materielnum",  description = "锁定PN"),
            @ReturnKey(key= "sequencenum",  description = "项次"),
            @ReturnKey(key= "batch",  description = "批号"),
            @ReturnKey(key= "quantity",  description = "数量"),
            @ReturnKey(key= "materielid",  description = "基础资料物料ID"),
            @ReturnKey(key= "lorderno",  description = "指令号"),
            @ReturnKey(key= "msn",  description = "飞机号"),
        }
    )
    @Filters({@By(type = CrossOriginFilter.class), @By(type = TokenFilter.class)})
    public Object getOrderEntryList(@Param("orderEntryIDList")String orderEntryIDList){
        try {
            return Result.success("system.success",logisticsOrderentryService.getOrderEntryList(orderEntryIDList));
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }

   @At("/receiveOrderBy2Code")
    @Ok("json")
    @Api(name = "(需求端:扫码完成订单)订单完成,完善订单状态和收货人字段" , params = {
            @ApiParam(name = "personId", type = "String", description = "登录需求端扫码人员ID"),
            @ApiParam(name = "orderId", type = "String", description = "订单ID"),
    }, ok = {
            @ReturnKey(key= "code",  description = "成功code=0"),
    }
    )
    @Filters({@By(type = CrossOriginFilter.class), @By(type = TokenFilter.class)})
    public Object receiveOrderBy2Code(@Param("personId")String personId,@Param("orderId")String orderId){
        try {
            logisticsOrderService.receiveOrderBy2Code(personId,orderId);
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }


    @At("/getOrderEntryListByOderFlag")
    @Ok("json")
    @Api(name = "(需求端)通过物料标识(时间戳)得到物料列表" , params = {
            @ApiParam(name = "orderflag", type = "String", description = "物料标识(时间戳)"),
    }, ok = {
            @ReturnKey(key= "id",  description = "物料ID"),
            @ReturnKey(key= "MRNO",  description = "MR号"),
            @ReturnKey(key= "PN_L",  description = "锁定PN"),
            @ReturnKey(key= "ITEMNO",  description = "项次"),
            @ReturnKey(key= "BN",  description = "批次号"),
            @ReturnKey(key= "QTY",  description = "数量"),
            @ReturnKey(key= "PN_R",  description = "预留件号"),
            @ReturnKey(key= "SN",  description = "预留序号"),
            @ReturnKey(key= "ORDERSTATE",  description = "物料领用状态"),
            @ReturnKey(key= "lorderno",  description = "指令号"),
            @ReturnKey(key= "msn",  description = "飞机号"),
            @ReturnKey(key= "materialStock",  description = "所属仓库"),
    }
    )
    @Filters({@By(type = CrossOriginFilter.class), @By(type = TokenFilter.class)})
    public Object getOrderEntryListByOderFlag(@Param("orderflag")String orderflag){
        try {
            return Result.success("system.success",logisticsOrderentryService.getOrderEntryListByOderFlag(orderflag));
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }
    @At("/deleteOrderEntryListByOderFlag")
    @Ok("json")
    @Api(name = "(需求端)通过物料标识(时间戳)删除对应物料" , params = {
            @ApiParam(name = "orderflag", type = "String", description = "物料标识(时间戳)"),
    }, ok = {
            @ReturnKey(key= "code",  description = "成功code=0")}
    )
    @Filters({@By(type = CrossOriginFilter.class), @By(type = TokenFilter.class)})
    public Object deleteOrderEntryListByOderFlag(@Param("orderflag")String orderflag){
        try {
            logisticsOrderentryService.deleteOrderEntryListByOderFlag(orderflag);
            return Result.success("system.success");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("system.error",e);
        }
    }

    @At("/deleteOrderEntryByID")
    @Ok("json")
    @Api(name = "(需求端)通过物料ID删除对应物料" , params = {
            @ApiParam(name = "orderentryID", type = "String", description = "物料ID"),
    }, ok = {
            @ReturnKey(key= "code",  description = "成功code=0")}
    )
    @Filters({@By(type = CrossOriginFilter.class), @By(type = TokenFilter.class)})
    public Object deleteOrderEntryByID(@Param("orderentryID")String orderentryID){
        try {
            logisticsOrderentryService.deleteOrderEntryByID(orderentryID);
            return Result.success("system.success");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("system.error",e);
        }
    }

    @At("/ListenOrder")
    @Ok("json")
    @Api(name = "(需求端)监听是否扫码签收" , params = {
            @ApiParam(name = "orderid", type = "String", description = "订单ID"),
    }, ok = {
            @ReturnKey(key= "code",  description = "成功code=0"),
            @ReturnKey(key= "number",  description = "查到的数据数量number=1跳出接口")
    }

    )
    @Filters({@By(type=CrossOriginFilter.class),@By(type = TokenFilter.class)})
    public Object ListenOrder(@Param("orderid") String orderid){
        try{
            return Result.success("system.success",logisticsOrderService.ListenOrderid(orderid));
        }catch (Exception e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
    }

    @At("/checkIsOver")
    @Ok("json")
    @Api(name = "(需求端)检查是否已签收" , params = {
            @ApiParam(name = "orderid", type = "String", description = "订单ID"),
    }, ok = {
            @ReturnKey(key= "code",  description = "成功code=0"),
            @ReturnKey(key= "receive",  description = "收货人ID")
    }
    )
    @Filters({@By(type=CrossOriginFilter.class),@By(type = TokenFilter.class)})
    public Object checkIsOver(@Param("orderid") String orderid){
        try{
            return Result.success("system.success",logisticsOrderService.checkIsOver(orderid));
        }catch (Exception e) {
            e.printStackTrace();
            return Result.error("system.error",e);
        }
    }


    @At("/getUnreceivedCount")
    @Ok("json")
    @Filters({@By(type = CrossOriginFilter.class), @By(type = TokenFilter.class)})
    @Api(name = "获取待接订单数量"
            , params = {
            @ApiParam (name = "personid", type = "String", description = "员工id，则需要此参数")
            },
            ok = {@ReturnKey(key = "unreceivedCount", description = "待接订单数量")}
    )
    public Object getUnreceivedCount(@Param("personid") String personid) {
        try {
            return Result.success("system.success",logisticsOrderService.getUnreceivedCount(personid));
        } catch (Exception e) {
            return Result.error("system.error", e);
        }
    }
/*    public static void main(String[] args){
        String str = "DWI1037988";
        String regEx="[^0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher matcher = p.matcher(str);
        str = str.replaceAll("[^a-z^A-Z]", "");
        System.out.println(str);
    }*/
}


