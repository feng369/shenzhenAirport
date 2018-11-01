package cn.wizzer.app.base.modules.services.impl;

import cn.wizzer.app.base.modules.models.base_rowoffice;
import cn.wizzer.framework.base.service.BaseServiceImpl;
import cn.wizzer.app.base.modules.models.base_rowofficeentry;
import cn.wizzer.app.base.modules.services.BaseRowofficeentryService;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean(args = {"refer:dao"})
public class BaseRowofficeentryServiceImpl extends BaseServiceImpl<base_rowofficeentry> implements BaseRowofficeentryService {
    public BaseRowofficeentryServiceImpl(Dao dao) {
        super(dao);
    }


}
