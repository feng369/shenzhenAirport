package cn.wizzer.app.logistics.modules.services.impl;

import cn.wizzer.framework.base.service.BaseServiceImpl;
import cn.wizzer.app.logistics.modules.models.logistics_orderreject;
import cn.wizzer.app.logistics.modules.services.LogisticsOrderrejectService;
import org.nutz.aop.interceptor.ioc.TransAop;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean(args = {"refer:dao"})
public class LogisticsOrderrejectServiceImpl extends BaseServiceImpl<logistics_orderreject> implements LogisticsOrderrejectService {
    public LogisticsOrderrejectServiceImpl(Dao dao) {
        super(dao);
    }

    @Aop(TransAop.READ_COMMITTED)
    public void insetReject(String orderid, String rejectid, String describ, String personid) {
        Cnd cnd=Cnd.NEW();
        cnd.and("id","=",orderid);
        logistics_orderreject orderreject=new logistics_orderreject();
        orderreject.setDescrib(describ);
        orderreject.setPersonid(personid);
        orderreject.setRejectid(rejectid);
        orderreject.setOrderid(orderid);
        this.insert(orderreject);
    }
}
