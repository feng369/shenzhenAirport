package cn.wizzer.app.logistics.modules.services;

import cn.wizzer.framework.base.Result;
import cn.wizzer.framework.base.service.BaseService;
import cn.wizzer.app.logistics.modules.models.logistics_orderplanentry;

public interface LogisticsOrderplanentryService extends BaseService<logistics_orderplanentry>{
    Result updateDelivergoods(String personid, String id, String deliverynum, String packnum, String weight, String flightnum, String thirdname, String drivername, String carnum);

}
