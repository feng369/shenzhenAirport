package cn.wizzer.app.web.modules.controllers.platform.logistics;

import cn.wizzer.framework.base.Result;
import cn.wizzer.app.web.commons.slog.annotation.SLog;
import cn.wizzer.framework.page.datatable.DataTableColumn;
import cn.wizzer.framework.page.datatable.DataTableOrder;
import cn.wizzer.framework.util.StringUtil;
import cn.wizzer.app.logistics.modules.models.logistics_sendplan;
import cn.wizzer.app.logistics.modules.services.LogisticsSendplanService;
import cn.wizzer.app.logistics.modules.services.LogisticsOrderService;
import cn.wizzer.app.logistics.modules.models.logistics_order;
import cn.wizzer.app.logistics.modules.services.LogisticsOrderplanService;
import cn.wizzer.app.logistics.modules.models.logistics_orderplan;
import cn.wizzer.app.logistics.modules.services.LogisticsSendgoodsService;
import cn.wizzer.app.logistics.modules.models.logistics_sendgoods;
import cn.wizzer.app.logistics.modules.services.LogisticsUnplanService;
import cn.wizzer.app.logistics.modules.models.logistics_unplan;
import cn.wizzer.app.logistics.modules.services.LogisticsSendapprovalService;
import cn.wizzer.app.logistics.modules.models.logistics_sendapproval;
import cn.wizzer.app.logistics.modules.services.LogisticsDeliveryorderService;
import cn.wizzer.app.logistics.modules.models.logistics_Deliveryorder;
import cn.wizzer.app.logistics.modules.models.logistics_orderplanentry;
import cn.wizzer.app.logistics.modules.services.LogisticsOrderplanentryService;
import cn.wizzer.app.web.modules.controllers.open.api.Datatime.newDataTime;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Record;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.util.cri.SqlExpression;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@IocBean
@At("/platform/logistics/sendplan")
public class LogisticsSendplanController{
    private static final Log log = Logs.get();
    @Inject
    private LogisticsSendplanService logisticsSendplanService;
    @Inject
    private LogisticsOrderService logisticsOrderService;
    @Inject
    private LogisticsOrderplanService logisticsOrderplanService;
    @Inject
    private LogisticsSendgoodsService logisticsSendgoodsService;
    @Inject
    private LogisticsUnplanService logisticsUnplanService;
    @Inject
    private LogisticsSendapprovalService logisticsSendapprovalService;
    @Inject
    private Dao dao;
    @Inject
    private LogisticsDeliveryorderService logisticsDeliveryorderService;
    @Inject
    private LogisticsOrderplanentryService logisticsOrderplanentryService;

    @At("")
    @Ok("beetl:/platform/logistics/sendplan/index.html")
    @RequiresPermissions("platform.logistics.sendplan")
    public void index() {
    }

    @At("/data")
    @Ok("json")
    @RequiresPermissions("platform.logistics.sendplan")
    public Object data(HttpServletRequest req, @Param("pstatus") String pstatus, @Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns) {
		try{
//            Cnd cnd = Cnd.NEW();
//            cnd.and("isTF","is not",null);
//            return logisticsOrderService.data(length, start, draw, order, columns, cnd, "sendplan");
            Cnd cnd=Cnd.NEW();
//            if(pstatus=="3"){
//                cnd.and("pstatus","in","3,4");
//            }else{
                cnd.and("pstatus","=",pstatus);
//            }

            return logisticsSendplanService.data(length, start, draw, order, columns, cnd, "order");
//            String sqlstr="select * from logistics_order a left join logistics_sendplan sendplan on a.sendplanid=sendplan.id" +
//                    " where isTF is not null and airportid='"+req.getSession().getAttribute("airportid").toString()+"' and sendplan.pstatus in ("+pstatus+")";
//            Sql sql= Sqls.queryRecord(sqlstr);
//
//            dao.execute(sql);
//            List<Record> res = sql.getList(Record.class);
//            return res;
		}catch(Exception e){
		    return  null;
        }

//    	return logisticsSendplanService.data(length, start, draw, order, columns, cnd, "order");
    }

    @At("/mdata")
    @Ok("json")
    public Object mdata(@Param("pstatus") String pstatus,@Param("airportid") String airportid){
        if(!Strings.isBlank(pstatus)&&!Strings.isBlank(airportid)){
//            Cnd cnd = Cnd.NEW();
//            cnd.and("isTF","is not",null);
//            cnd.and("pstatus","in",pstatus);
//            cnd.and("airportid","=",airportid);
//            //cnd.and()
//            return logisticsOrderService.query(cnd,"sendplan");

            String sqlstr="select *,(select count(*) from logistics_sendgoods where planID=sendplan.id) as totalpacknum from logistics_order a left join logistics_sendplan sendplan on a.sendplanid=sendplan.id" +
                    " where isTF is not null and airportid='"+airportid+"' and sendplan.pstatus in ("+pstatus+")";
            Sql sql= Sqls.queryRecord(sqlstr);

            dao.execute(sql);
            List<Record> res = sql.getList(Record.class);
            return res;

        }
        return "";
    }

    @At("/add")
    @Ok("beetl:/platform/logistics/sendplan/add.html")
    @RequiresPermissions("platform.logistics.sendplan")
    public void add() {

    }

    @At("/addDo")
    @Ok("json")
    @RequiresPermissions("platform.logistics.sendplan.add")
    @SLog(tag = "logistics_sendplan", msg = "${args[0].id}")
    public Object addDo(@Param("..")logistics_sendplan logisticsSendplan, HttpServletRequest req) {
		try {
			logisticsSendplanService.insert(logisticsSendplan);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }

    @At("/edit/?")
    @Ok("beetl:/platform/logistics/sendplan/edit.html")
    @RequiresPermissions("platform.logistics.sendplan")
    public void edit(String id,HttpServletRequest req) {
		req.setAttribute("obj", logisticsSendplanService.fetch(id));
    }

    @At("/editDo")
    @Ok("json")
    @RequiresPermissions("platform.logistics.sendplan.edit")
    @SLog(tag = "logistics_sendplan", msg = "${args[0].id}")
    public Object editDo(@Param("..")logistics_sendplan logisticsSendplan, HttpServletRequest req) {
		try {
            logisticsSendplan.setOpBy(StringUtil.getUid());
			logisticsSendplan.setOpAt((int) (System.currentTimeMillis() / 1000));
			logisticsSendplanService.updateIgnoreNull(logisticsSendplan);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }

    @At({"/delete/?", "/delete"})
    @Ok("json")
    @RequiresPermissions("platform.logistics.sendplan.delete")
    @SLog(tag = "logistics_sendplan", msg = "${req.getAttribute('id')}")
    public Object delete(String id, @Param("ids")  String[] ids, HttpServletRequest req) {
		try {
			if(ids!=null&&ids.length>0){
				logisticsSendplanService.delete(ids);
    			req.setAttribute("id", org.apache.shiro.util.StringUtils.toString(ids));
			}else{
				logisticsSendplanService.delete(id);
    			req.setAttribute("id", id);
			}
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }

    @At("/detail/?")
    @Ok("beetl:/platform/logistics/sendplan/detail.html")
    @RequiresPermissions("platform.logistics.sendplan")
	public void detail(String id, HttpServletRequest req) {
		if (!Strings.isBlank(id)) {
            req.setAttribute("obj", logisticsSendplanService.fetch(id));
		}else{
            req.setAttribute("obj", null);
        }
    }

    @At("/updateSendplan")
    @Ok("json")
    @SLog(tag = "订单附加信息", msg = "更新")
    public Object updateSendplan(@Param("spid") String spid,@Param("ptid") String pricetype,@Param("tempprice") String tempprice,@Param("protocol") String protocol,@Param("free") String free){
        try{
            if(!Strings.isBlank(spid)){
                logistics_sendplan sendplan=logisticsSendplanService.fetch(spid);
                if(sendplan!=null){
                    if(!Strings.isBlank(pricetype)){
                        sendplan.setCosttype(pricetype);
                    }
                    if(!Strings.isBlank(tempprice)){
                        sendplan.setTempprice(Float.parseFloat(tempprice));
                        sendplan.setProtocolnum("");
                        sendplan.setFreenum("");
                    }
                    if(!Strings.isBlank(protocol)){
                        sendplan.setProtocolnum(protocol);
                        sendplan.setFreenum("");
                        sendplan.setTempprice(0);
                    }
                    if(!Strings.isBlank(free)){
                        sendplan.setFreenum(free);
                        sendplan.setProtocolnum("");
                        sendplan.setTempprice(0);
                    }
                    return logisticsSendplanService.updateIgnoreNull(sendplan);
                }
            }
            return null;
        }catch(Exception e){
            return null;
        }

    }

    @At("/setApproval")
    @Ok("json")
    @SLog(tag = "审批申请", msg = "审批")
    public Object setApproval(@Param("spids") String [] spids,HttpServletRequest req){
        try{
            //0 新增审批数据
            //1 修改订单补充信息的状态 为1 审批中
            //2 不修改订单和配送单状态 因为订单状态没有审批中
            //3 修改订单对应合同状态为 6

            int num=0;
            for(int p=0;p< spids.length;p++){
                logistics_sendapproval sendapproval=new logistics_sendapproval();
                sendapproval.setPlanID(spids[p]);
                sendapproval.setPstatus(0);
                sendapproval.setApplyer(req.getSession().getAttribute("personid").toString());
                logisticsSendapprovalService.insert(sendapproval);
                logistics_sendplan sendplan=logisticsSendplanService.fetch(spids[p]);
                if(sendplan!=null){
                    sendplan.setPstatus(1);
                    logisticsSendplanService.updateIgnoreNull(sendplan);
                    num++;
                    Cnd cnd=Cnd.NEW();
                    cnd.and("orderid","=",sendplan.getOrderid());
                    List<logistics_orderplan> orderplans=logisticsOrderplanService.query(cnd);
                    if(orderplans.size()>0){
                        for(int i=0;i<orderplans.size();i++){
                            logistics_sendgoods sendgoods=logisticsSendgoodsService.fetch(orderplans.get(i).getContractid());
                            if(sendgoods!=null){
                                sendgoods.setPstatus(6);
                                logisticsSendgoodsService.updateIgnoreNull(sendgoods);
//                            logistics_unplan unplan=logisticsUnplanService.fetch(sendgoods.getPackID());
//                            if(unplan!=null){
//
//                            }
                                num++;
                            }
                        }

                    }
                    //将订单id及step=6（审批中）对应的数据从orderplanentry中找到，把这些数据的平status改为1
                    cnd.and("step","=","6");
                    List<logistics_orderplanentry> orderplanentrys=logisticsOrderplanentryService.query(cnd);
                    if(orderplanentrys.size()>0){
                        for(int k=0;k<orderplanentrys.size();k++){
                            orderplanentrys.get(k).setPstatus(1);
                            orderplanentrys.get(k).setOperatetime(newDataTime.getDateYMDHMS());
                            orderplanentrys.get(k).setOperater(req.getSession().getAttribute("personid").toString());
                            logisticsOrderplanentryService.updateIgnoreNull(orderplanentrys.get(k));
                        }
                    }
                }
            }


            return num;
        }catch(Exception e){
            return  null;
        }
    }

    /*@At("/updateDelivergoods")
    @Ok("jsonp:full")
    @SLog(tag = "交货补充信息app", msg = "交货补充信息")
    public Object updateDelivergoods(@Param("personid") String personid, @Param("spid") String id,@Param("deliverynum") String deliverynum,@Param("packnum") String packnum,@Param("weight") String weight,@Param("flightnum") String flightnum,@Param("thirdname") String thirdname,@Param("drivername") String drivername,@Param("carnum") String carnum){
        try{
            if(!Strings.isBlank(id)){
                logistics_sendplan sendplan=logisticsSendplanService.fetch(id);
                if(sendplan!=null){
                    if(!Strings.isBlank(deliverynum))
                        sendplan.setDeliverynum(deliverynum);
                    if(!Strings.isBlank(packnum))
                        sendplan.setPacknum(packnum);
                    if(!Strings.isBlank(weight))
                        sendplan.setWeight(weight);
                    if(!Strings.isBlank(flightnum))
                        sendplan.setFlightnum(flightnum);
                    if(!Strings.isBlank(thirdname))
                        sendplan.setThirdname(thirdname);
                    if(!Strings.isBlank(drivername))
                        sendplan.setDrivername(drivername);
                    if(!Strings.isBlank(carnum))
                        sendplan.setCarnum(carnum);
                    sendplan.setPstatus(4);
                    logisticsSendplanService.updateIgnoreNull(sendplan);

                    //订单状态改为7完成
                    logistics_order order=logisticsOrderService.fetch(sendplan.getOrderid());
                    if(order!=null){
                        order.setPstatus(7);
                        logisticsOrderService.updateIgnoreNull(order);
                        //配送单状态改为3
                        logistics_Deliveryorder deliveryorder=logisticsDeliveryorderService.fetch(order.getDeliveryorderid());
                        if(deliveryorder!=null){
                            deliveryorder.setPstatus(3);
                            logisticsDeliveryorderService.updateIgnoreNull(deliveryorder);
                        }
                        //合同状态改为8
                        Cnd cnd=Cnd.NEW();
                        cnd.and("orderid","=",order.getId());
                        List<logistics_orderplan> orderplans = logisticsOrderplanService.query(cnd);
                        if(orderplans.size()>0){
                            for(int i=0;i<orderplans.size();i++){
                                String contractid = orderplans.get(i).getContractid();
                                if(contractid!=null){
                                    logistics_sendgoods sendgoods=logisticsSendgoodsService.fetch(contractid);
                                    sendgoods.setPstatus(9);
                                    sendgoods.setBillnum(deliverynum);
                                    logisticsSendgoodsService.updateIgnoreNull(sendgoods);

                                    //orderplanentry合同ID，step9的pstatus改为1
                                    Cnd c=Cnd.NEW();
                                    c.and("contractid","=",contractid);
                                    c.and("step","=","9");
                                    logistics_orderplanentry logistics_orderplanentry=logisticsOrderplanentryService.fetch(c);
                                    if(logistics_orderplanentry!=null){
                                        logistics_orderplanentry.setPstatus(1);
                                        logistics_orderplanentry.setOperater(personid);
                                        logistics_orderplanentry.setOperatetime(newDataTime.getDateYMDHMS());
                                        logisticsOrderplanentryService.updateIgnoreNull(logistics_orderplanentry);
                                    }
                                }
                            }
                            return Result.success("保存成功");
                        }
                    }
                }
            }
            return null;
        }catch(Exception e){
            return null;
        }
    }
*/
    /*@At("/updateDelivergoodsbyDone")
    @Ok("jsonp:full")
    @SLog(tag = "进货", msg = "进货")
    public Object updateDelivergoodsbyDone(@Param("spid") String id,@Param("cids") String contractids,@Param("personid") String personid){
        //contractids是检验不合格的合同id
        try{
            if(!Strings.isBlank(id)){
                logistics_sendplan sendplan=logisticsSendplanService.fetch(id);
                if(!Strings.isEmpty(contractids)){
                    //将对应的合同状态改为4
                    //将合同的planID去掉
                    //将planID找到orderid和合同ID对应的orderplan表数据删除
                    String [] cids=contractids.split(",");
//                    String [] opids;
                    for(int i=0;i<cids.length;i++){
                        logistics_sendgoods sg=logisticsSendgoodsService.fetch(cids[i]);
                        sg.setPstatus(4);
                        sg.setPlanID("");
                        if(sendplan!=null){
                            Cnd c=Cnd.NEW();
                            c.and("orderid","=",sendplan.getOrderid());
                            c.and("contractid","=",cids[i]);
                            logistics_orderplan op=logisticsOrderplanService.fetch(c);
                            if(op!=null)
                            {
                                logisticsOrderplanService.delete(op.getId());
                            }
                        }

                        ///将此合同对应的orderplanentry中step7，6，5的pstatus，operater,operatetime，deliveryorderid,orderid全部去掉
                        Cnd cnd=Cnd.NEW();
                        cnd.and("contractid","=",cids[i]);
                        cnd.and("step","in","5,6,7");
                        List<logistics_orderplanentry> logistics_orderplanentries= logisticsOrderplanentryService.query(cnd);
                        if(logistics_orderplanentries.size()>0){
                            for(int k=0;k<logistics_orderplanentries.size();k++){
                                logistics_orderplanentries.get(k).setPstatus(0);
                                logistics_orderplanentries.get(k).setOperatetime("");
                                logistics_orderplanentries.get(k).setOperater("");
                                logistics_orderplanentries.get(k).setDeliveryorderid("");
                                logistics_orderplanentries.get(k).setOrderid("");
                                logisticsOrderplanentryService.updateIgnoreNull(logistics_orderplanentries.get(k));
                            }
                        }

                    }

                }


                if(sendplan!=null){
                    sendplan.setPstatus(3);
                    sendplan.setSender(personid);
                    logisticsSendplanService.updateIgnoreNull(sendplan);

                    //订单状态改为6 送达
                    logistics_order order=logisticsOrderService.fetch(sendplan.getOrderid());
                    if(order!=null){
                        order.setPstatus(6);
                        logisticsOrderService.updateIgnoreNull(order);
                        //配送单状态改为3
                        logistics_Deliveryorder deliveryorder=logisticsDeliveryorderService.fetch(order.getDeliveryorderid());
                        if(deliveryorder!=null){
                            deliveryorder.setPstatus(2);
                            deliveryorder.setPersonid(personid);
                            logisticsDeliveryorderService.updateIgnoreNull(deliveryorder);
                        }
                        //合同状态改为8
                        Cnd cnd=Cnd.NEW();
                        cnd.and("orderid","=",order.getId());
                        List<logistics_orderplan> orderplans = logisticsOrderplanService.query(cnd);
                        if(orderplans.size()>0){
                            for(int i=0;i<orderplans.size();i++){
                                String contractid = orderplans.get(i).getContractid();
                                if(contractid!=null){
                                    logistics_sendgoods sendgoods=logisticsSendgoodsService.fetch(contractid);
                                    sendgoods.setPstatus(8);
                                    sendgoods.setSender(personid);
                                    logisticsSendgoodsService.updateIgnoreNull(sendgoods);

                                    //orderplanentry合同ID，step8的pstatus改为1
                                    Cnd c=Cnd.NEW();
                                    c.and("contractid","=",contractid);
                                    c.and("step","=","8");
                                    logistics_orderplanentry logistics_orderplanentry=logisticsOrderplanentryService.fetch(c);
                                    if(logistics_orderplanentry!=null){
                                        logistics_orderplanentry.setPstatus(1);
                                        logistics_orderplanentry.setOperater(personid);
                                        logistics_orderplanentry.setOperatetime(newDataTime.getDateYMDHMS());
//                                        if(!Strings.isEmpty(contractids)){
//                                            logistics_orderplanentry.setContent("合同号: "+contractids+" 检验未通过");
//                                        }
                                        logisticsOrderplanentryService.updateIgnoreNull(logistics_orderplanentry);
                                    }
                                }
                            }

                            return Result.success("保存成功");
                        }
                    }
                }
            }
            return null;
        }catch(Exception e){
            return null;
        }
    }*/

    @At("/updateDegoods")
    @Ok("json")
    @SLog(tag = "交货补充信息web", msg = "交货补充信息")
    public Object updateDegoods( HttpServletRequest req,@Param("spid") String id,@Param("deliverynum") String deliverynum,@Param("packnum") String packnum,@Param("weight") String weight,@Param("flightnum") String flightnum,@Param("thirdname") String thirdname,@Param("drivername") String drivername,@Param("carnum") String carnum){
        try{
            if(!Strings.isBlank(id)){
                logistics_sendplan sendplan=logisticsSendplanService.fetch(id);
                if(sendplan!=null){
                    if(!Strings.isBlank(deliverynum))
                        sendplan.setDeliverynum(deliverynum);
                    if(!Strings.isBlank(packnum))
                        sendplan.setPacknum(packnum);
                    if(!Strings.isBlank(weight))
                        sendplan.setWeight(weight);
                    if(!Strings.isBlank(flightnum))
                        sendplan.setFlightnum(flightnum);
                    if(!Strings.isBlank(thirdname))
                        sendplan.setThirdname(thirdname);
                    if(!Strings.isBlank(drivername))
                        sendplan.setDrivername(drivername);
                    if(!Strings.isBlank(carnum))
                        sendplan.setCarnum(carnum);
                    sendplan.setPstatus(4);
                    logisticsSendplanService.updateIgnoreNull(sendplan);

                    //订单状态改为7 完成
                    logistics_order order=logisticsOrderService.fetch(sendplan.getOrderid());
                    if(order!=null){
                        order.setPstatus(7);
                        logisticsOrderService.updateIgnoreNull(order);
                        //配送单状态改为3
                        logistics_Deliveryorder deliveryorder=logisticsDeliveryorderService.fetch(order.getDeliveryorderid());
                        if(deliveryorder!=null){
                            deliveryorder.setPstatus(3);
                            logisticsDeliveryorderService.updateIgnoreNull(deliveryorder);
                        }
                        //合同状态改为9
                        Cnd cnd=Cnd.NEW();
                        cnd.and("orderid","=",order.getId());
                        List<logistics_orderplan> orderplans = logisticsOrderplanService.query(cnd);
                        if(orderplans.size()>0){
                            for(int i=0;i<orderplans.size();i++){
                                String contractid = orderplans.get(i).getContractid();
                                if(contractid!=null){
                                    logistics_sendgoods sendgoods=logisticsSendgoodsService.fetch(contractid);
                                    sendgoods.setPstatus(9);
                                    sendgoods.setBillnum(deliverynum);
                                    logisticsSendgoodsService.updateIgnoreNull(sendgoods);

                                    //orderplanentry合同ID，step9的pstatus改为1
                                    Cnd c=Cnd.NEW();
                                    c.and("contractid","=",contractid);
                                    c.and("step","=","9");
                                    logistics_orderplanentry logistics_orderplanentry=logisticsOrderplanentryService.fetch(c);
                                    if(logistics_orderplanentry!=null){
                                        logistics_orderplanentry.setPstatus(1);
                                        logistics_orderplanentry.setOperater(req.getSession().getAttribute("personid").toString());
                                        logistics_orderplanentry.setOperatetime(newDataTime.getDateYMDHMS());
                                        logisticsOrderplanentryService.updateIgnoreNull(logistics_orderplanentry);
                                    }
                                }
                            }
                            return Result.success("保存成功");
                        }
                    }
                }
            }
            return null;
        }catch(Exception e){
            return null;
        }
    }


    @At("/updateZTDelivergoods")
    @Ok("json")
    @SLog(tag = "自提交货", msg = "自提交货")
    public Object updateZTDelivergoods(@Param("spid") String id,HttpServletRequest req){
        try{
            if(!Strings.isBlank(id)){
                logistics_sendplan sendplan=logisticsSendplanService.fetch(id);
                if(sendplan!=null){
                    sendplan.setPstatus(4);
                    sendplan.setSender(req.getSession().getAttribute("personid").toString());
                    logisticsSendplanService.updateIgnoreNull(sendplan);

                    //订单状态改为7完成
                    logistics_order order=logisticsOrderService.fetch(sendplan.getOrderid());
                    if(order!=null){
                        order.setPstatus(7);
                        logisticsOrderService.updateIgnoreNull(order);
                        //配送单状态改为3
                        logistics_Deliveryorder deliveryorder=logisticsDeliveryorderService.fetch(order.getDeliveryorderid());
                        if(deliveryorder!=null){
                            deliveryorder.setPstatus(3);
                            deliveryorder.setPersonid(req.getSession().getAttribute("personid").toString());
                            logisticsDeliveryorderService.updateIgnoreNull(deliveryorder);
                        }
                        //合同状态改为9
                        Cnd cnd=Cnd.NEW();
                        cnd.and("orderid","=",order.getId());
                        List<logistics_orderplan> orderplans = logisticsOrderplanService.query(cnd);
                        if(orderplans.size()>0){
                            for(int i=0;i<orderplans.size();i++){
                                String contractid = orderplans.get(i).getContractid();
                                if(contractid!=null){
                                    logistics_sendgoods sendgoods=logisticsSendgoodsService.fetch(contractid);
                                    sendgoods.setPstatus(9);
                                    sendgoods.setSender(req.getSession().getAttribute("personid").toString());
                                    logisticsSendgoodsService.updateIgnoreNull(sendgoods);

                                    //orderplanentry合同ID，step9的pstatus改为1
                                    Cnd c=Cnd.NEW();
                                    c.and("contractid","=",contractid);
                                    c.and("step","=","9");
                                    logistics_orderplanentry logistics_orderplanentry=logisticsOrderplanentryService.fetch(c);
                                    if(logistics_orderplanentry!=null){
                                        logistics_orderplanentry.setPstatus(1);
                                        logistics_orderplanentry.setOperater(req.getSession().getAttribute("personid").toString());
                                        logistics_orderplanentry.setOperatetime(newDataTime.getDateYMDHMS());
                                        logisticsOrderplanentryService.updateIgnoreNull(logistics_orderplanentry);
                                    }
                                }
                            }
                            return Result.success("保存成功");
                        }
                    }
                }
            }
            return null;
        }catch(Exception e){
            return null;
        }
    }

    @At("/getSendplan")
    @Ok("json")
    public Object getSendplan(@Param("id") String id){
        try{
            if(!Strings.isBlank(id)){
                return logisticsSendplanService.fetch(id);
            }
            return null;
        }catch(Exception e){
            return null;
        }
    }

}
