package cn.wizzer.app.logistics.modules.services;

import cn.wizzer.framework.base.service.BaseService;
import cn.wizzer.app.logistics.modules.models.logistics_orderreject;

public interface LogisticsOrderrejectService extends BaseService<logistics_orderreject>{

    void insetReject(String orderid, String rejectid, String describ, String personid);
}
