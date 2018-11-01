package cn.wizzer.app.logistics.modules.services;

import cn.wizzer.framework.base.service.BaseService;
import cn.wizzer.app.logistics.modules.models.logistics_entourage;

public interface LogisticsEntourageService extends BaseService<logistics_entourage>{

    void insertDataByMobile(String personlist, String orderid);
}
