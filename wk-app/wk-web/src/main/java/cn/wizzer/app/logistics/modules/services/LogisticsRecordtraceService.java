package cn.wizzer.app.logistics.modules.services;

import cn.wizzer.app.logistics.modules.models.logistics_Deliveryorder;
import cn.wizzer.app.logistics.modules.models.logistics_order;
import cn.wizzer.framework.base.service.BaseService;
import cn.wizzer.app.logistics.modules.models.logistics_recordtrace;

public interface LogisticsRecordtraceService extends BaseService<logistics_recordtrace>{
    void insertRecordTrace(String personid, String position, logistics_order order);
    void updateRecordTrace(String position, logistics_order order);
    void firstInsert(logistics_Deliveryorder deliveryorder, logistics_order logisticsOrder, String curPostion);
}
