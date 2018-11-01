package cn.wizzer.app.base.modules.services;

import cn.wizzer.framework.base.service.BaseService;
import cn.wizzer.app.base.modules.models.base_post;

public interface BasePostService extends BaseService<base_post>{

    void save(base_post post, String pid);

    void deleteAndChild(base_post post);

    void update(base_post post);

}
