package cn.wizzer.app.sys.modules.services;

import cn.wizzer.framework.base.service.BaseService;
import cn.wizzer.app.sys.modules.models.Sys_wx_dept;

public interface SysWxDeptService extends BaseService<Sys_wx_dept>{
    void download() throws Exception;
}
