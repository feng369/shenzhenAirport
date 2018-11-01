package cn.wizzer.app.sys.modules.services;

import cn.wizzer.framework.base.service.BaseService;
import cn.wizzer.app.sys.modules.models.Sys_version;

import javax.xml.bind.ValidationException;

public interface SysVersionService extends BaseService<Sys_version>{
    /**
     * 修改版本号
     * @param name
     * @param newVersion
     * @param wgt
     */
    void editVersion(String name, String newVersion, boolean wgt) throws ValidationException;

}
