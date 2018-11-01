package cn.wizzer.app.base.modules.services;

import cn.wizzer.app.base.modules.models.base_person;
import cn.wizzer.app.sys.modules.models.Sys_user;
import cn.wizzer.framework.base.service.BaseService;
import cn.wizzer.app.base.modules.models.base_cnctobj;
import org.nutz.dao.Cnd;

public interface BaseCnctobjService extends BaseService<base_cnctobj>{

     base_cnctobj  getbase_cnctobj(String  condition);

     base_cnctobj getcnctobj(String userid);

     Cnd airportDataPermission(Cnd cnd);

     Cnd unitDataPermission(Cnd cnd);

     Cnd airportOrderPermission(Cnd cnd);

    base_cnctobj addCnctObj(Sys_user user, base_person person);
}
