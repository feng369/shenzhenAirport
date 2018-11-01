package cn.wizzer.app.logistics.modules.services;

import cn.wizzer.app.logistics.modules.models.logistics_orderdelivery;
import cn.wizzer.framework.base.service.BaseService;
import cn.wizzer.app.logistics.modules.models.logistics_Deliveryorder;
import org.nutz.dao.entity.Record;

import javax.xml.bind.ValidationException;
import java.awt.*;
import java.util.List;
import java.util.Map;

public interface LogisticsDeliveryorderService extends BaseService<logistics_Deliveryorder>{

    void save(logistics_Deliveryorder logistics_deliveryorder);

    List<logistics_Deliveryorder> getDeliveryOrderByMobile(String pstatus,String personid);

    void orderexe(String orderid);

    boolean checkorderre(String orderid);

    List<Map<String,Object>> getDeliveryOrderList(String personid, String pstatus, String deliveryordernum,Integer pagenumber,Integer pagesize);

    void addDelDo(String orderid, String personid,String curPosition) throws ValidationException;

    List<Map<String,Object>> haveSendingOrders(String personid);
}
