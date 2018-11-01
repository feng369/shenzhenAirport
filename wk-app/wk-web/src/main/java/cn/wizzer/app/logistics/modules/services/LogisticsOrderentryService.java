package cn.wizzer.app.logistics.modules.services;

import cn.wizzer.framework.base.service.BaseService;
import cn.wizzer.app.logistics.modules.models.logistics_orderentry;

import java.util.List;
import java.util.Map;

public interface LogisticsOrderentryService extends BaseService<logistics_orderentry>{

    List<Map<String,Object>> getOrderEntryByOrderId(String orderid, String dataType, Integer pagesize, Integer pagenumber);

    void editOrderEntriesByMobile(String orderid, String orderEntryIDList);

    List<Map<String,Object>> getOrderEntryList(String orderEntryIDList);

    List<Map<String,Object>> getOrderEntryListByOderFlag(String orderflag);

    void deleteOrderEntryListByOderFlag(String orderflag);

    void deleteOrderEntryByID(String orderentryID);
}
