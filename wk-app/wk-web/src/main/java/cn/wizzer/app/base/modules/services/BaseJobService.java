package cn.wizzer.app.base.modules.services;

import cn.wizzer.framework.base.service.BaseService;
import cn.wizzer.app.base.modules.models.base_job;

public interface BaseJobService extends BaseService<base_job>{

    void save(base_job job, String pid);

    void deleteAndChild(base_job job);

    void update(base_job job);

}
