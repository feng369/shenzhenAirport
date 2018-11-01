package cn.wizzer.app.web.modules.controllers.platform.logistics;

import cn.wizzer.app.base.modules.models.base_personpool;
import cn.wizzer.app.base.modules.models.base_place;
import cn.wizzer.app.base.modules.services.BaseCustomerService;
import cn.wizzer.app.base.modules.services.BasePersonpoolService;
import cn.wizzer.app.base.modules.services.BasePlaceService;
import cn.wizzer.app.logistics.modules.models.logistics_Deliveryorderentry;
import cn.wizzer.app.logistics.modules.models.logistics_order;
import cn.wizzer.app.logistics.modules.models.logistics_orderdelivery;
import cn.wizzer.app.logistics.modules.services.*;
import cn.wizzer.app.sys.modules.services.SysDictService;
import cn.wizzer.app.web.modules.controllers.open.api.Datatime.newDataTime;
import cn.wizzer.framework.base.Result;
import cn.wizzer.app.web.commons.slog.annotation.SLog;
import cn.wizzer.framework.map.MapUtils;
import cn.wizzer.framework.map.ValueComparator;
import cn.wizzer.framework.page.datatable.DataTableColumn;
import cn.wizzer.framework.page.datatable.DataTableOrder;
import cn.wizzer.framework.util.StringUtil;
import cn.wizzer.app.logistics.modules.models.logistics_Deliveryorder;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.dao.Cnd;
import org.nutz.dao.Condition;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Record;
import org.nutz.dao.entity.annotation.Comment;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.util.cri.SqlExpressionGroup;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.IocLoader;
import org.nutz.ioc.impl.NutIoc;
import org.nutz.ioc.loader.combo.ComboIocLoader;
import org.nutz.json.Json;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.*;
import org.nutz.resource.Scans;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;

@IocBean
@At("/platform/logistics/Deliveryorder")
public class LogisticsDeliveryorderController{
    private static final Log log = Logs.get();
    @Inject
    private LogisticsDeliveryorderService logisticsDeliveryorderService;

    @Inject
    private LogisticsOrderService logisticsOrderService;

    @Inject
    private LogisticsOrderstepService logisticsOrderstepService;

    @Inject
    private LogisticsOrderdeliveryService logisticsOrderdeliveryService;

    @Inject
    private BaseCustomerService baseCustomerService;

    @Inject
    private BasePlaceService basePlaceService;

    @Inject
    private SysDictService sysDictService;

    @Inject
    private BasePersonpoolService basePersonpoolService;
    @Inject
    private LogisticsDeliveryorderentryService logisticsDeliveryorderentryService;
    @Inject
    private Dao dao;


    @At("")
    @Ok("beetl:/platform/logistics/Deliveryorder/index.html")
    @RequiresPermissions("platform.logistics.Deliveryorder")
    public void index() {
    }
    @At("/data")
    @Ok("json")
    @RequiresPermissions("platform.logistics.Deliveryorder.select")
    public Object data(@Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns) {
		Cnd cnd = Cnd.NEW();
		cnd.and("delFlag","=",0);
		return logisticsDeliveryorderService.data(length, start, draw, order, columns, cnd, "person");
    }



    @At("/msdata")
    @Ok("json")
    public Object msdata(@Param("personid") String personid, @Param("pstatus") String status,@Param("deliveryordernum") String deliveryordernum, @Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns) {
        try{
//            Cnd cnd = Cnd.NEW();
            SqlExpressionGroup s=null;
            SqlExpressionGroup q=null;
            SqlExpressionGroup l=null;
            if (!Strings.isBlank(status))
            {
                String [] stat=status.split(",");
                s= Cnd.exps("pstatus","=",stat[0]);
                for(int i=1;i<stat.length;i++){
                    s.or("pstatus","=",stat[i]);
                }

            }

            if(!Strings.isBlank(deliveryordernum)){
                q=Cnd.exps("deliveryordernum","like","%"+deliveryordernum+"%");
            }
            if(!Strings.isBlank(personid)){
                l=Cnd.exps("personid","=",personid);
            }
            Cnd cnd=Cnd.where(s).and(q).and(l);

//          cnd.and("deliveryordernum","like","%"+deliveryordernum+"%");
            return logisticsDeliveryorderService.data(length, start, draw, order, columns, cnd, "person");
        }
        catch(Exception e){
            e.printStackTrace();
            return  null;
        }
    }

    //配送单列表显示始发地-目的地
    @At("/getdata")
    @Ok("json")
    public Object getdata(@Param("personid") String personid, @Param("pstatus") String pstatus,@Param("deliveryordernum") String deliveryordernum){
        try{
            if(!Strings.isBlank(personid)&&!Strings.isBlank(pstatus)){
                String sqlstr="select a.id,b.deliveryorderid,b.startname,b.endname,c.personname,b.btypename,b.otypename from logistics_Deliveryorder as a left join "+
                        "( SELECT DISTINCT  o.deliveryorderid, p.placename startname,l.placename endname,o.btype,s.name as btypename,y.name as otypename from logistics_order as o left join base_place as p on o.startstock=p.id "+
                        "left join base_place l on o.endstock=l.id left join sys_dict as s on o.btype=s.id left join sys_dict as y on o.otype=y.id ) as b "+
                        " on a.id=b.deliveryorderid "+
                        "left join base_person as c on a.personid=c.id where a.personid='"+personid+"' and a.pstatus="+pstatus;
                if(!Strings.isBlank(deliveryordernum)){
                    sqlstr+=" and a.deliveryordernum='"+deliveryordernum+"'";
                }
                Sql sql= Sqls.queryRecord(sqlstr);

                dao.execute(sql);
                List<Record> res = sql.getList(Record.class);
                return res;
            }

            return Result.success("error");
        }
        catch(Exception e){
            return Result.error("error");
        }
    }

//    @At("/getOrderCount")
//    @Ok("jsonp:full")
//    public Object getOrderCount(@Param("pstatus") String pstatus,@Param("personid") String personid){
//        if(!Strings.isBlank(pstatus)&&!Strings.isBlank(personid)){
//            List<logistics_Deliveryorder> logisticsDeliveryorderList =logisticsDeliveryorderService.getDeliveryOrderByMobile(pstatus, personid);
//            int count=logisticsDeliveryorderList.size();
//            List<String> list=new ArrayList<>();
//            for(int i=0;i<count;i++){
//                list.add(logisticsDeliveryorderList.get(i).getId());
//            }
//            if(list.size()>0){
//                return logisticsOrderdeliveryService.getOrderCount(list);
//            }
//
//            return  0;
//
//        }
//        return 0;
//    }

    @At("/add")
    @Ok("beetl:/platform/logistics/Deliveryorder/add.html")
    @RequiresPermissions("platform.logistics.Deliveryorder")
    public void add() {

    }



    @At("/addDo")
    @Ok("json")
    @RequiresPermissions("platform.logistics.Deliveryorder.add")
    @SLog(tag = "logistics_Deliveryorder", msg = "${args[0].id}")
    public Object addDo(@Param("..")logistics_Deliveryorder logisticsDeliveryorder, HttpServletRequest req) {
		try {

			logisticsDeliveryorderService.insert(logisticsDeliveryorder);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }

    //接单
 /*   @At("/adddelDo")
    @Ok("json")
    @SLog(tag = "配送单", msg = "生成配送单")
    public Object adddelDo(@Param("orderid")String orderid,@Param("personid") String personid,@Param("curPosition")String      curPosition ,HttpServletRequest req) {
        try {
          logisticsDeliveryorderService.addDelDo(orderid,personid,curPosition);
            return Result.success("system.success");

        }catch (Exception e) {
            return Result.error("system.error");
        }
    }*/

    @At("/edit/?")
    @Ok("beetl:/platform/logistics/Deliveryorder/edit.html")
    @RequiresPermissions("platform.logistics.Deliveryorder")
    public void edit(String id,HttpServletRequest req) {
		req.setAttribute("obj", logisticsDeliveryorderService.fetch(id));
    }

    @At("/editDo")
    @Ok("json")
    @RequiresPermissions("platform.logistics.Deliveryorder.edit")
    @SLog(tag = "logistics_Deliveryorder", msg = "${args[0].id}")
    public Object editDo(@Param("..")logistics_Deliveryorder logisticsDeliveryorder, HttpServletRequest req) {
		try {
            logisticsDeliveryorder.setOpBy(StringUtil.getUid());
			logisticsDeliveryorder.setOpAt((int) (System.currentTimeMillis() / 1000));
			logisticsDeliveryorderService.updateIgnoreNull(logisticsDeliveryorder);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }

    @At({"/delete/?", "/delete"})
    @Ok("json")
    @RequiresPermissions("platform.logistics.Deliveryorder.delete")
    @SLog(tag = "logistics_Deliveryorder", msg = "${req.getAttribute('id')}")
    public Object delete(String id, @Param("ids")  String[] ids, HttpServletRequest req) {
		try {
			if(ids!=null&&ids.length>0){
				logisticsDeliveryorderService.delete(ids);
    			req.setAttribute("id", org.apache.shiro.util.StringUtils.toString(ids));
			}else{
				logisticsDeliveryorderService.delete(id);
    			req.setAttribute("id", id);
			}
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }

    @At("/detail/?")
    @Ok("beetl:/platform/logistics/Deliveryorder/detail.html")
    @RequiresPermissions("platform.logistics.Deliveryorder.select")
	public void detail(String id, HttpServletRequest req) {
		if (!Strings.isBlank(id)) {
            req.setAttribute("obj", logisticsDeliveryorderService.fetch(id));
		}else{
            req.setAttribute("obj", null);
        }
    }

    @At("/getOrder/?")
    @Ok("beetl:/platform/logistics/Deliveryorder/getOrder.html")
    public void getOrder(String deliveryorderid, HttpServletRequest req) {

        //根据配送单ID取得相关的订单数据
        Cnd cnd = Cnd.NEW();
        cnd.and("deliveryorderid","=",deliveryorderid);
        logistics_orderdelivery orderdelivery= logisticsOrderdeliveryService.fetch(cnd);

        if (!Strings.isBlank(deliveryorderid)) {
            req.setAttribute("obj",orderdelivery);
        }else{
            req.setAttribute("obj", null);
        }
    }

    @At("/getOrderList/")
    @Ok("json")
    public Object getOrderList(HttpServletRequest req) {
        //根据配送单ID取得相关的订单数据
        String deliveryorderid= req.getParameter("deliveryorloderid");
        //根据配送单ID来获取订单数据
        Cnd cnd = Cnd.NEW();
        Cnd cri = Cnd.NEW();
        cnd.and("deliveryorderid","=",deliveryorderid);
        if (!Strings.isBlank(deliveryorderid)) {

            List<logistics_orderdelivery> orderdelivery =  orderdelivery=logisticsOrderdeliveryService.query(cnd);

            List<logistics_order> orders=new ArrayList<logistics_order>();

            for (int i=0;i<orderdelivery.size();i++){
                cri.and("id","=",orderdelivery.get(i).getOrderid());
                logistics_order order = logisticsOrderService.dao().fetchLinks(logisticsOrderService.dao().fetch(logistics_order.class, orderdelivery.get(i).getOrderid()), "logistics_orderentry");
                order.setCustomer(baseCustomerService.fetch(order.getCustomerid()));
                order.setDict_btype(sysDictService.fetch(order.getBtype()));
                order.setDict_emergency(sysDictService.fetch(order.getEmergency()));
                order.setPlace_startstock(basePlaceService.fetch(order.getStartstock()));
                order.setPlace_endstock(basePlaceService.fetch(order.getEndstock()));
                orders.add(order);
            }
            return orders;
        }else{
            return null;
        }
    }

}
