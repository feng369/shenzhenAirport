package cn.wizzer.app.base.modules.services;

import cn.wizzer.framework.base.service.BaseService;
import cn.wizzer.app.base.modules.models.base_dept;

public interface BaseDeptService extends BaseService<base_dept>{

    void save(base_dept dept, String pid);

    void deleteAndChild(base_dept dept);

    void update(base_dept dept);

}
