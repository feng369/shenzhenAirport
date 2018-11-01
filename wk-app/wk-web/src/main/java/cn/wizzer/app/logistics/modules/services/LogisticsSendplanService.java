package cn.wizzer.app.logistics.modules.services;

import cn.wizzer.framework.base.service.BaseService;
import cn.wizzer.app.logistics.modules.models.logistics_sendplan;

public interface LogisticsSendplanService extends BaseService<logistics_sendplan>{

    void updateSendPlan(logistics_sendplan sendplan,String deliverynum, String packnum, String weight, String flightnum, String thirdname, String drivername, String carnum, int i);
}
