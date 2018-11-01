package cn.wizzer.app.logistics.modules.services.impl;

import cn.wizzer.framework.base.service.BaseServiceImpl;
import cn.wizzer.app.logistics.modules.models.logistics_orderplan;
import cn.wizzer.app.logistics.modules.services.LogisticsOrderplanService;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean(args = {"refer:dao"})
public class LogisticsOrderplanServiceImpl extends BaseServiceImpl<logistics_orderplan> implements LogisticsOrderplanService {
    public LogisticsOrderplanServiceImpl(Dao dao) {
        super(dao);
    }
}
