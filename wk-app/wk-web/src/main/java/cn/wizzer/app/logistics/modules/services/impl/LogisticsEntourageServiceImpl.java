package cn.wizzer.app.logistics.modules.services.impl;

import cn.wizzer.framework.base.service.BaseServiceImpl;
import cn.wizzer.app.logistics.modules.models.logistics_entourage;
import cn.wizzer.app.logistics.modules.services.LogisticsEntourageService;
import org.nutz.aop.interceptor.ioc.TransAop;
import org.nutz.dao.Dao;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean(args = {"refer:dao"})
public class LogisticsEntourageServiceImpl extends BaseServiceImpl<logistics_entourage> implements LogisticsEntourageService {
    public LogisticsEntourageServiceImpl(Dao dao) {
        super(dao);
    }

    @Aop(TransAop.READ_COMMITTED)
    public void insertDataByMobile(String personlist, String orderid) {
        String [] personArr=personlist.split(",");
        for(int i=0;i<personArr.length;i++){
            if(personArr[i].length()>0){
                logistics_entourage entourage=new logistics_entourage();
                entourage.setOrderid(orderid);
                entourage.setPersonid(personArr[i]);
                this.insert(entourage);
            }
        }
    }
}
