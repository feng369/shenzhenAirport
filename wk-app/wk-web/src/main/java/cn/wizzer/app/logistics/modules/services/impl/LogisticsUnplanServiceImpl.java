package cn.wizzer.app.logistics.modules.services.impl;

import cn.wizzer.framework.base.service.BaseServiceImpl;
import cn.wizzer.app.logistics.modules.models.logistics_unplan;
import cn.wizzer.app.logistics.modules.services.LogisticsUnplanService;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean(args = {"refer:dao"})
public class LogisticsUnplanServiceImpl extends BaseServiceImpl<logistics_unplan> implements LogisticsUnplanService {
    public LogisticsUnplanServiceImpl(Dao dao) {
        super(dao);
    }
}
