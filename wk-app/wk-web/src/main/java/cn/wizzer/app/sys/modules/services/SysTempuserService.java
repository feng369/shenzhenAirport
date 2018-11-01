package cn.wizzer.app.sys.modules.services;

import cn.wizzer.framework.base.service.BaseService;
import cn.wizzer.app.sys.modules.models.Sys_tempuser;

public interface SysTempuserService extends BaseService<Sys_tempuser>{

    Sys_tempuser addUserByMobile(String cutomerid, String username, String loginname, String password, String jobNumber, String tel,String wxNumber);

    Sys_tempuser updateRoleCode(String tempuserId, Sys_tempuser tempuser);

}
