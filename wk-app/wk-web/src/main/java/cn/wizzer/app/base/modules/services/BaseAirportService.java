package cn.wizzer.app.base.modules.services;

import cn.wizzer.framework.base.service.BaseService;
import cn.wizzer.app.base.modules.models.base_airport;
import org.nutz.lang.util.NutMap;

import java.util.List;
import java.util.Map;

public interface BaseAirportService extends BaseService<base_airport>{
    Map getInfo(String personid);
}
