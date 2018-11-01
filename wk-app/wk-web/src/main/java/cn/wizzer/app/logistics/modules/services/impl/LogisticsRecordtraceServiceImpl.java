package cn.wizzer.app.logistics.modules.services.impl;

import cn.wizzer.app.logistics.modules.models.logistics_Deliveryorder;
import cn.wizzer.app.logistics.modules.models.logistics_order;
import cn.wizzer.app.logistics.modules.models.logistics_sendtrace;
import cn.wizzer.app.web.modules.controllers.open.api.Datatime.newDataTime;
import cn.wizzer.framework.base.service.BaseServiceImpl;
import cn.wizzer.app.logistics.modules.models.logistics_recordtrace;
import cn.wizzer.app.logistics.modules.services.LogisticsRecordtraceService;
import org.nutz.aop.interceptor.ioc.TransAop;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.trans.Trans;

@IocBean(args = {"refer:dao"})
public class LogisticsRecordtraceServiceImpl extends BaseServiceImpl<logistics_recordtrace> implements LogisticsRecordtraceService {
    public LogisticsRecordtraceServiceImpl(Dao dao) {
        super(dao);
    }

    @Aop(TransAop.READ_COMMITTED)
    public void insertRecordTrace(String personid, String position, logistics_order order) {
        logistics_recordtrace logisticsRecordtrace = new logistics_recordtrace();
        logisticsRecordtrace.setPosition(position);
        logisticsRecordtrace.setPersonid(personid);
        logisticsRecordtrace.setOrderid(order.getId());
        if(order != null){
            logisticsRecordtrace.setDeliveryorderid(order.getDeliveryorderid());
            logisticsRecordtrace.setOrderpstatus(order.getPstatus());
        }
        this.insert(logisticsRecordtrace);
    }

    @Aop(TransAop.READ_COMMITTED)
    public void updateRecordTrace(String position, logistics_order order){
        this.update(Chain.make("position",position).add("orderpstatus",order.getPstatus()).add("intime", newDataTime.getDateYMDHMS()), Cnd.where("orderid","=",order.getId()));
    }

    @Aop(TransAop.READ_COMMITTED)
    public void firstInsert(logistics_Deliveryorder deliveryorder, logistics_order order, String curPostion) {
        logistics_recordtrace recordtrace = new logistics_recordtrace();
        if (order != null) {
            recordtrace.setOrderid(order.getId());
            recordtrace.setOrderpstatus(order.getPstatus());

        }
        if (!Strings.isBlank(curPostion)) {
            recordtrace.setPosition(curPostion);
        }
        if (deliveryorder != null) {
            recordtrace.setDeliveryorderid(deliveryorder.getId());
            recordtrace.setPersonid(deliveryorder.getPersonid());

        }
        recordtrace.setIntime(newDataTime.getDateYMDHMS());
        this.insert(recordtrace);
    }
}
