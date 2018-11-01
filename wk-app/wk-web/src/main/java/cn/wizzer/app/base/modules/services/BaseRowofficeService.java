package cn.wizzer.app.base.modules.services;

import cn.wizzer.framework.base.service.BaseService;
import cn.wizzer.app.base.modules.models.base_rowoffice;

public interface BaseRowofficeService extends BaseService<base_rowoffice>{
    void save(base_rowoffice base_rowoffice, String base_rowofficeentry);
    base_rowoffice getBase_rowoffice(String id);
}
