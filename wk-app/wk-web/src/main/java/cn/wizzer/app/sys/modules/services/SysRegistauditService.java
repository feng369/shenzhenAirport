package cn.wizzer.app.sys.modules.services;

import cn.wizzer.app.sys.modules.models.Sys_tempuser;
import cn.wizzer.framework.base.service.BaseService;
import cn.wizzer.app.sys.modules.models.Sys_registaudit;
import cn.wizzer.framework.page.datatable.DataTableColumn;
import cn.wizzer.framework.page.datatable.DataTableOrder;
import org.nutz.dao.Cnd;
import org.nutz.lang.util.NutMap;

import java.util.List;

public interface SysRegistauditService extends BaseService<Sys_registaudit>{

    void addUserByMobile(Sys_tempuser tempuser);

    NutMap getRegistList(int result, int length, int start, int draw, List<DataTableOrder> order, List<DataTableColumn> columns, Cnd cnd, String loginname, String username, String tel, String registTime);

    void toAuditPage(Sys_registaudit sysRegistaudit);


    void phoneauditDo(Sys_registaudit sysRegistaudit, Sys_tempuser tempuser);
}
