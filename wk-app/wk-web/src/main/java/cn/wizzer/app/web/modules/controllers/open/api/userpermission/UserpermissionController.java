package cn.wizzer.app.web.modules.controllers.open.api.userpermission;

import cn.wizzer.app.base.modules.models.base_cnctobj;
import cn.wizzer.app.base.modules.models.base_person;
import cn.wizzer.app.base.modules.services.BaseCnctobjService;
import cn.wizzer.app.base.modules.services.BasePersonService;
import cn.wizzer.app.sys.modules.models.Sys_role;
import cn.wizzer.app.sys.modules.models.Sys_unit;
import cn.wizzer.app.sys.modules.models.Sys_user;
import cn.wizzer.framework.base.service.BaseService;
import cn.wizzer.framework.base.service.BaseServiceImpl;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Param;

import java.util.*;

/**
 * Created by xl on 2017/6/8.
 */
@IocBean
@At("/open/api/userpermission")
public class UserpermissionController {
    @Inject
    private BasePersonService basePersonService;
    @Inject
    private BaseCnctobjService baseCnctobjService;
    @At("")
    public Cnd userDataPermission(@Param("unitid") String unitid, @Param("deptid") String deptid, @Param("userid") String userid,Cnd cnd){
        //获取登录用户基本信息
        Subject subject = SecurityUtils.getSubject();
        Sys_user curUser = (Sys_user) subject.getPrincipal();
        String Punitid = curUser.getUnitid();
        List<Sys_role> role = curUser.getRoles();
        //获取超级管理员权限标识，超级管理员权限标识为sysadmin可以查看所有数据
        if(!role.get(0).getCode().toString().equals("sysadmin")) {
            if (!Strings.isBlank(unitid) && !"root".equals(unitid))
               cnd.and(unitid, "=", Punitid);
            if (!Strings.isBlank(deptid))
                cnd.and(deptid, "=", deptid);
            if (!Strings.isBlank(userid))
                cnd.and(userid, "=", userid);
        }

        return cnd;
    }
}
