package cn.wizzer.app.logistics.modules.services.impl;

import cn.wizzer.framework.base.service.BaseServiceImpl;
import cn.wizzer.app.logistics.modules.models.logistics_sendplan;
import cn.wizzer.app.logistics.modules.services.LogisticsSendplanService;
import org.nutz.aop.interceptor.ioc.TransAop;
import org.nutz.dao.Dao;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;

@IocBean(args = {"refer:dao"})
public class LogisticsSendplanServiceImpl extends BaseServiceImpl<logistics_sendplan> implements LogisticsSendplanService {
    public LogisticsSendplanServiceImpl(Dao dao) {
        super(dao);
    }

    @Aop(TransAop.READ_COMMITTED)
    public void updateSendPlan(logistics_sendplan sendplan,String deliverynum, String packnum, String weight, String flightnum, String thirdname, String drivername, String carnum, int i) {
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
        this.updateIgnoreNull(sendplan);
    }
}
