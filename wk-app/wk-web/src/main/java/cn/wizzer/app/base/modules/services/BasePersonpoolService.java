package cn.wizzer.app.base.modules.services;

import cn.wizzer.framework.base.service.BaseService;
import cn.wizzer.app.base.modules.models.base_personpool;
import org.nutz.dao.Cnd;
import org.nutz.lang.util.NutMap;

import java.util.List;
import java.util.Map;

public interface BasePersonpoolService extends BaseService<base_personpool>{
    NutMap getPersonRow(int length, int start, int draw, Cnd cnd, String linkName);
    boolean checkWork(String id ,String startdata);

    void updatePosition(String personid, String position);

    void setPersonByM(String personlist);

    List<Map<String,Object>> getPersonpoolByM(String airportid, Integer pagenumber, Integer pagesize);
}
