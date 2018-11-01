package cn.wizzer.app.base.modules.services.impl;

import cn.wizzer.framework.base.service.BaseServiceImpl;
import cn.wizzer.app.base.modules.models.base_warehouse;
import cn.wizzer.app.base.modules.services.BaseWarehouseService;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean(args = {"refer:dao"})
public class BaseWarehouseServiceImpl extends BaseServiceImpl<base_warehouse> implements BaseWarehouseService {
    public BaseWarehouseServiceImpl(Dao dao) {
        super(dao);
    }
}
