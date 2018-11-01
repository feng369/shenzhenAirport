package cn.wizzer.app.web.commons.quartz.job;

import cn.wizzer.app.base.modules.models.base_personpool;
import cn.wizzer.app.base.modules.models.base_place;
import cn.wizzer.app.base.modules.services.BaseCustomerService;
import cn.wizzer.app.base.modules.services.BasePersonpoolService;
import cn.wizzer.app.base.modules.services.BasePlaceService;
import cn.wizzer.app.logistics.modules.models.logistics_Deliveryorder;
import cn.wizzer.app.logistics.modules.models.logistics_Deliveryorderentry;
import cn.wizzer.app.logistics.modules.models.logistics_order;
import cn.wizzer.app.logistics.modules.models.logistics_orderdelivery;
import cn.wizzer.app.logistics.modules.services.*;
import cn.wizzer.app.sys.modules.models.Sys_task;
import cn.wizzer.app.sys.modules.services.SysDictService;
import cn.wizzer.app.sys.modules.services.SysTaskService;
import cn.wizzer.framework.base.Result;
import cn.wizzer.framework.map.MapUtils;
import cn.wizzer.framework.map.ValueComparator;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.At;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by xl on 2017/7/10.
 */
@IocBean
public class orderExecute implements Job {
    private static final Log log = Logs.get();
    @Inject
    private SysTaskService sysTaskService;
    @Inject
    private LogisticsDeliveryorderService logisticsDeliveryorderService;

    @Inject
    private LogisticsDeliveryorderentryService logisticsDeliveryorderentryService;

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
    protected Dao dao;

    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap data = context.getJobDetail().getJobDataMap();
        String taskId = context.getJobDetail().getKey().getName();
        try{
            //获取订单状态为1的订单
            Cnd cnd = Cnd.NEW();
            cnd.and("pstatus","=","1");
            List<logistics_order>  logisticsOrders = logisticsOrderService.getdata(cnd,"");
            if(logisticsOrders.size()>0){
                for(logistics_order orderids:logisticsOrders){
                    if(logisticsDeliveryorderService.checkorderre(orderids.getId())){
                        logisticsDeliveryorderService.orderexe(orderids.getId());
                        log.info("自动派单成功："+orderids.getOrdernum());
                    }

                }
            }
            dao.update(Sys_task.class, Chain.make("exeAt", (int) (System.currentTimeMillis() / 1000)).add("exeResult", "执行成功"), Cnd.where("id", "=", taskId));
        }catch(Exception e){
            log.error("自动派单失败:"+e.getMessage());
            dao.update(Sys_task.class, Chain.make("exeAt", (int) (System.currentTimeMillis() / 1000)).add("exeResult", "执行失败"), Cnd.where("id", "=", taskId));
        }

    }




}
