package cn.wizzer.app.base.modules.services.impl;

import cn.wizzer.app.base.modules.models.base_customer;
import cn.wizzer.app.base.modules.models.base_rowofficeentry;
import cn.wizzer.app.base.modules.services.BasePersonService;
import cn.wizzer.framework.base.service.BaseServiceImpl;
import cn.wizzer.app.base.modules.models.base_rowoffice;
import cn.wizzer.app.base.modules.services.BaseRowofficeService;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;

import java.util.List;

@IocBean(args = {"refer:dao"})
public class BaseRowofficeServiceImpl extends BaseServiceImpl<base_rowoffice> implements BaseRowofficeService {
    @Inject
    private BasePersonService basePersonService;

    public BaseRowofficeServiceImpl(Dao dao) {
        super(dao);
    }
    public void save(base_rowoffice base_rowoffice, String base_rowofficeentry){
        dao().insertWith(base_rowoffice,base_rowofficeentry);
    }
    public base_rowoffice getBase_rowoffice(String id){
        base_rowoffice baseRowoffice = dao().fetchLinks(dao().fetch(base_rowoffice.class, id), "rowofficeentry");
        List<base_rowofficeentry> base_rowofficeentryList=baseRowoffice.getRowofficeentry();

        for(base_rowofficeentry baseRowofficeentry:base_rowofficeentryList) {
            if(!Strings.isBlank(baseRowofficeentry.getPersonid())){
                baseRowofficeentry.setPerson(basePersonService.fetch(baseRowofficeentry.getPersonid()));
            }
        }
        baseRowoffice.setRowofficeentry(base_rowofficeentryList);
        return baseRowoffice;
    }
}
