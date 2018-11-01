package cn.wizzer.app.logistics.modules.services.impl;

import cn.wizzer.app.logistics.modules.models.logistics_order;
import cn.wizzer.framework.base.service.BaseServiceImpl;
import cn.wizzer.app.logistics.modules.models.logistics_orderdelivery;
import cn.wizzer.app.logistics.modules.services.LogisticsOrderdeliveryService;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.util.cri.SqlExpressionGroup;
import org.nutz.ioc.loader.annotation.IocBean;

import java.util.List;

@IocBean(args = {"refer:dao"})
public class LogisticsOrderdeliveryServiceImpl extends BaseServiceImpl<logistics_orderdelivery> implements LogisticsOrderdeliveryService {
    public LogisticsOrderdeliveryServiceImpl(Dao dao) {
        super(dao);
    }
    public List<logistics_orderdelivery> getorderdelivery(Cnd cnd){
        List<logistics_orderdelivery> list = dao().fetchLinks(dao().query(logistics_orderdelivery.class, cnd),"");
        return list;
    }

    public int getOrderCount(List<String> orderDeliveryId){
        SqlExpressionGroup s=null;
        s=Cnd.exps("deliveryorderid","=",orderDeliveryId.get(0));
        for(int i=1;i<orderDeliveryId.size();i++){
            s.or("deliveryorderid","=",orderDeliveryId.get(i));

        }


        Cnd cnd=Cnd.where(s);
        return this.count(cnd);


    }
}
