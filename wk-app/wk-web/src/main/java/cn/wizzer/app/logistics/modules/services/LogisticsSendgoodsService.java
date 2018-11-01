package cn.wizzer.app.logistics.modules.services;

import cn.wizzer.framework.base.service.BaseService;
import cn.wizzer.app.logistics.modules.models.logistics_sendgoods;
import org.nutz.lang.util.NutMap;

import java.util.List;
import java.util.Map;

public interface LogisticsSendgoodsService extends BaseService<logistics_sendgoods>{

    NutMap getJsonDataOfCountOrder(String airportId);


    List<Map<String,Object>> mdata(String pstatus, String airportid, Integer pagenumber, Integer pagesize);

    List<Map<String,Object>> planAndBeforeSendData(String pstatus, String airportid, Integer pagenumber, Integer pagesize);

    Map<String,Object> getGoodsInfo(String hlid);

    List<Map<String,Object>> getListbyPlanID(String planid, Integer pagenumber, Integer pagesize);
}
