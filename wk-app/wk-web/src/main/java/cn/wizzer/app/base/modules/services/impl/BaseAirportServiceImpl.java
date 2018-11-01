package cn.wizzer.app.base.modules.services.impl;

import cn.wizzer.app.base.modules.models.base_person;
import cn.wizzer.app.base.modules.services.BasePersonService;
import cn.wizzer.framework.base.service.BaseServiceImpl;
import cn.wizzer.app.base.modules.models.base_airport;
import cn.wizzer.app.base.modules.services.BaseAirportService;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.annotation.Param;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@IocBean(args = {"refer:dao"})
public class BaseAirportServiceImpl extends BaseServiceImpl<base_airport> implements BaseAirportService {
    public BaseAirportServiceImpl(Dao dao) {
        super(dao);
    }
    @Inject
    private BasePersonService basePersonService;

    public Map getInfo(String personid) {
        Map map = new HashMap();

        base_person basePerson = basePersonService.fetch(personid);
        if(basePerson!=null && !Strings.isBlank(basePerson.getAirportid())){
            base_airport baseAirport = this.fetch(basePerson.getAirportid());
            map.put("airportid",baseAirport.getId());
            map.put("airportname",baseAirport.getAirportname());
            map.put("airportnum",baseAirport.getAirportnum());
        }
        return map;
    }
}
