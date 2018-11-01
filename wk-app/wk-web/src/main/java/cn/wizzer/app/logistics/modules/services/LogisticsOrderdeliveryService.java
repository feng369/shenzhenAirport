package cn.wizzer.app.logistics.modules.services;

import cn.wizzer.framework.base.service.BaseService;
import cn.wizzer.app.logistics.modules.models.logistics_orderdelivery;
import org.nutz.dao.Cnd;

import java.util.List;

public interface LogisticsOrderdeliveryService extends BaseService<logistics_orderdelivery>{
    List<logistics_orderdelivery> getorderdelivery(Cnd cnd);

    int getOrderCount(List<String> orderDeliveryId);
}
