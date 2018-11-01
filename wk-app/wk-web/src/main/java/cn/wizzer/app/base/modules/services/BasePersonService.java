package cn.wizzer.app.base.modules.services;

import cn.wizzer.app.sys.modules.models.Sys_tempuser;
import cn.wizzer.app.sys.modules.models.Sys_unit;
import cn.wizzer.framework.base.Result;
import cn.wizzer.framework.base.service.BaseService;
import cn.wizzer.app.base.modules.models.base_person;

import javax.xml.bind.ValidationException;
import java.util.List;
import java.util.Map;

public interface BasePersonService extends BaseService<base_person>{

    List<Sys_unit> getDatas(String personid);

    base_person getPersonInfo();

    List<Map<String,Object>> getPersonByMobile(String airportid, Integer pagenumber, Integer pagesize);

    List<Map<String,Object>> getPersonList(String customerid,String personname, Integer pagenumber, Integer pagesize);

    Map getPersonByID(String id);

    base_person insertOrUpdatePersonTempUser(Sys_tempuser sysTempuser);

    void bindWxuser(String id, String wxuserid);

    Result unbindWx(String[] ids,String[] wxUids) throws ValidationException;

}
