package cn.wizzer.app.logistics.modules.services.impl;

import cn.wizzer.app.base.modules.models.base_person;
import cn.wizzer.app.base.modules.models.base_place;
import cn.wizzer.app.base.modules.services.BasePersonService;
import cn.wizzer.app.base.modules.services.BasePlaceService;
import cn.wizzer.app.logistics.modules.models.logistics_Deliveryorder;
import cn.wizzer.app.logistics.modules.models.logistics_order;
import cn.wizzer.app.logistics.modules.models.logistics_recordtrace;
import cn.wizzer.app.logistics.modules.services.*;
import cn.wizzer.app.web.modules.controllers.open.api.Datatime.newDataTime;
import cn.wizzer.framework.base.ValidatException;
import cn.wizzer.framework.base.service.BaseServiceImpl;
import cn.wizzer.app.logistics.modules.models.logistics_sendtrace;
import org.nutz.aop.interceptor.async.Async;
import org.nutz.aop.interceptor.ioc.TransAop;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.trans.Trans;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@IocBean(args = {"refer:dao"})
public class LogisticsSendtraceServiceImpl extends BaseServiceImpl<logistics_sendtrace> implements LogisticsSendtraceService {
    public LogisticsSendtraceServiceImpl(Dao dao) {
        super(dao);
    }

    @Inject
    private BasePersonService basePersonService;
    @Inject
    private BasePlaceService basePlaceService;
    @Inject
    private LogisticsOrderService logisticsOrderService;
    @Inject
    private LogisticsDeliveryorderService logisticsDeliveryorderService;
    @Inject
    private LogisticsOrderentryService logisticsOrderentryService;
    @Inject
    private LogisticsRecordtraceService logisticsRecordtraceService;

    @Aop(TransAop.READ_COMMITTED)
    public void addOne(logistics_Deliveryorder deliveryorder, logistics_order order, String curPostion) {
        logistics_sendtrace sendtrace = new logistics_sendtrace();
        if (order != null) {
            sendtrace.setOrderid(order.getId());
            sendtrace.setOrderpstatus(order.getPstatus());

        }
        if (!Strings.isBlank(curPostion)) {
            sendtrace.setPosition(curPostion);
        }
        if (deliveryorder != null) {
            sendtrace.setDeliveryorderid(deliveryorder.getId());
            sendtrace.setPersonid(deliveryorder.getPersonid());

        }
        sendtrace.setIntime(newDataTime.getDateYMDHMS());
        this.insert(sendtrace);
    }

    @Aop(TransAop.READ_COMMITTED)
    @Async
    public void insertSendTrace(String orderid, String personid, String position) {
        logistics_order order = logisticsOrderService.fetch(orderid);
        if (order != null && order.getPstatus() >= 3 && order.getPstatus() <= 7) {
            logistics_sendtrace sendtrace = new logistics_sendtrace();
            sendtrace.setPosition(position);
            sendtrace.setPersonid(personid);
            sendtrace.setOrderid(orderid);
            sendtrace.setDeliveryorderid(order.getDeliveryorderid());
            sendtrace.setOrderpstatus(order.getPstatus());
            sendtrace.setIntime(newDataTime.getDateYMDHMS());
            this.insert(sendtrace);
            //更新到最新位置到logistics_recordtrace表
            List<logistics_recordtrace> logisticsRecordtraces = logisticsRecordtraceService.query(Cnd.where("orderid", "=", orderid));
            if (logisticsRecordtraces.size() == 0) {
                logisticsRecordtraceService.insertRecordTrace(personid, position, order);
            } else if (logisticsRecordtraces.size() == 1) {
                logisticsRecordtraceService.updateRecordTrace(position, order);
            } else {
                throw new ValidatException("找不到唯一的最新轨迹位置数据");
            }
        }
    }


}