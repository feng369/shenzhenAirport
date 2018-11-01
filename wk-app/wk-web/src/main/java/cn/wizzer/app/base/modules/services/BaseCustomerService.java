package cn.wizzer.app.base.modules.services;

import cn.wizzer.framework.base.service.BaseService;
import cn.wizzer.app.base.modules.models.base_customer;

import java.util.List;
import java.util.Map;

public interface BaseCustomerService extends BaseService<base_customer>{

    List<Map<String,Object>> getCustomerByM(Integer pagenumber,Integer pagesize);
}
