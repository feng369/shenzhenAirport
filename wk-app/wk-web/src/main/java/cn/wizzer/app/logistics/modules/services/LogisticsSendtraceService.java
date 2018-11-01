package cn.wizzer.app.logistics.modules.services;

import cn.wizzer.app.logistics.modules.models.logistics_Deliveryorder;
import cn.wizzer.app.logistics.modules.models.logistics_order;
import cn.wizzer.framework.base.service.BaseService;
import cn.wizzer.app.logistics.modules.models.logistics_sendtrace;

import java.util.List;
import java.util.Map;

public interface LogisticsSendtraceService extends BaseService<logistics_sendtrace>{
    /**
     *20180601zhf1717
     手机端点击接单时
     插入一条数据到配送跟踪表中(第一条)
     (后续改进:可只传logisticsOrder)
     */
    void addOne(logistics_Deliveryorder deliveryorder, logistics_order logisticsOrder,String curPostion);

    void insertSendTrace(String orderid, String personid, String position);


}
