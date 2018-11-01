package cn.wizzer.app.sys.modules.services.impl;

import cn.wizzer.framework.base.service.BaseServiceImpl;
import cn.wizzer.app.sys.modules.models.Sys_barcode;
import cn.wizzer.app.sys.modules.services.SysBarcodeService;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean(args = {"refer:dao"})
public class SysBarcodeServiceImpl extends BaseServiceImpl<Sys_barcode> implements SysBarcodeService {
    public SysBarcodeServiceImpl(Dao dao) {
        super(dao);
    }
}
