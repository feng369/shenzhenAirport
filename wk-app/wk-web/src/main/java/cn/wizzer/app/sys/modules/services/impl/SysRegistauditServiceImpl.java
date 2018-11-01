package cn.wizzer.app.sys.modules.services.impl;

import cn.wizzer.app.base.modules.models.base_person;
import cn.wizzer.app.base.modules.services.BaseAirportService;
import cn.wizzer.app.base.modules.services.BaseCnctobjService;
import cn.wizzer.app.base.modules.services.BasePersonService;
import cn.wizzer.app.sys.modules.models.*;
import cn.wizzer.app.sys.modules.services.*;
import cn.wizzer.app.web.commons.base.Globals;
import cn.wizzer.app.web.modules.controllers.open.api.Datatime.newDataTime;
import cn.wizzer.framework.base.service.BaseServiceImpl;
import cn.wizzer.framework.page.OffsetPager;
import cn.wizzer.framework.page.datatable.DataTableColumn;
import cn.wizzer.framework.page.datatable.DataTableOrder;
import com.alibaba.fastjson.JSONObject;
import org.apache.shiro.SecurityUtils;
import org.nutz.aop.interceptor.ioc.TransAop;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Record;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.util.Daos;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@IocBean(args = {"refer:dao"})
public class SysRegistauditServiceImpl extends BaseServiceImpl<Sys_registaudit> implements SysRegistauditService {
    public SysRegistauditServiceImpl(Dao dao) {
        super(dao);
    }

    @Inject
    private SysUserService sysUserService;
    @Inject
    private SysTempuserService sysTempuserService;
    @Inject
    private BasePersonService basePersonService;
    @Inject
    private BaseCnctobjService baseCnctobjService;
    @Inject
    private BaseAirportService baseAirportService;
    @Inject
    private SysUnitService sysUnitService;
    @Inject
    private SysRoleService sysRoleService;
    @Inject
    private SysWxService sysWxService;
    @Inject
    private SysWxUserService sysWxUserService;

    @Aop(TransAop.READ_COMMITTED)
    public void addUserByMobile(Sys_tempuser tempuser) {
        Sys_registaudit sysRegistaudit = new Sys_registaudit();
        if (tempuser != null) {
            sysRegistaudit.setTempuserId(tempuser.getId());
            sysRegistaudit.setTempuser(tempuser);
        }
        sysRegistaudit.setRegistTime(newDataTime.getSdfByPattern(null).format(new Date()));
        sysRegistaudit.setResult(-1);
        this.insert(sysRegistaudit);
    }

    public NutMap getRegistList(int result, int length, int start, int draw, List<DataTableOrder> orders, List<DataTableColumn> columns, Cnd cnd, String loginname, String username, String tel, String registTime) {
        NutMap re = new NutMap();
        String sqlStr = " SELECT " +
                "sr.*," +                                                                   //注册审核表
                "st.id as tid,st.loginname,st.username,st.tel,st.jobNumber,st.way,st.wxNumber," +    //注册信息表
                "su.name " +                                                                //单位表
                "FROM sys_tempuser  st " +
                "LEFT JOIN sys_registaudit sr  ON st.id = sr.tempuserId " +
                "LEFT JOIN sys_unit su ON st.unitid = su.id  " +
                "WHERE st.unitid = '" + Globals.unitid + "' AND sr.result =" + result + " " + (Strings.isBlank(loginname) ? "":"  AND st.loginname  LIKE  '%" +loginname+"%' ")+ (Strings.isBlank(username) ? "" : "  AND st.username LIKE '%" +username+ "%' ") + (Strings.isBlank(tel) ? "" : " AND st.tel LIKE '%"+tel+"%' ") + (Strings.isBlank(registTime) ? "" : "  AND sr.registTime LIKE  '%"+registTime+"%'  ") ;


        if (orders != null && orders.size() > 0) {
            for (DataTableOrder order : orders) {
                DataTableColumn col = columns.get(order.getColumn());
                sqlStr += " ORDER BY  " + Sqls.escapeSqlFieldValue(col.getData()).toString()+" "+order.getDir() ;
            }
        }
        Sql sql = Sqls.queryRecord(sqlStr);
        Pager pager = new OffsetPager(start, length);
        sql.setPager(pager);
        re.put("recordsFiltered", Daos.queryCount(dao(), sql));
        dao().execute(sql);
        List list = sql.getList(Record.class);
        re.put("data", list);
        re.put("draw", draw);
        re.put("recordsTotal", length);
        return re;
    }

    @Aop(TransAop.READ_COMMITTED)
    public void toAuditPage(Sys_registaudit sysRegistaudit) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sysRegistaudit.setAuditTime(sdf.format(new Date()));
        //auditor
        org.apache.shiro.subject.Subject subject = SecurityUtils.getSubject();
        if (subject != null) {
            Sys_user currentUser = (Sys_user) SecurityUtils.getSubject().getPrincipal();
            if (currentUser != null) {
                sysRegistaudit.setAuditor(currentUser);
                sysRegistaudit.setAuditorId(currentUser.getId());
            }
        }
        this.updateIgnoreNull(sysRegistaudit);

    }

    @Aop(TransAop.READ_COMMITTED)
    public void phoneauditDo(Sys_registaudit sysRegistaudit, Sys_tempuser tempuser) {
        if (sysRegistaudit != null) {
            //修改审核结果等
            this.updateIgnoreNull(sysRegistaudit);
            sysRegistaudit = this.fetch(sysRegistaudit.getId());
            if (sysRegistaudit != null && !Strings.isBlank(sysRegistaudit.getTempuserId())) {
                Sys_tempuser sysTempuser = sysTempuserService.updateRoleCode(sysRegistaudit.getTempuserId(), tempuser);
                if (sysTempuser != null) {
                    //将注册信息填入对应user,person,cnctobj中
                    completeAudit(sysTempuser, sysRegistaudit.getResult());
                }
            }
        }
    }

    @Aop(TransAop.READ_COMMITTED)
    private void completeAudit(Sys_tempuser sysTempuser, int result) {
        if (result == 0) {
            //通过
            if (sysTempuser.getWay() == 0) {
                //手机注册
                //1.生成sys_user
                Sys_user user = sysUserService.insertUserByTempUser(sysTempuser);
                //2,生成base_person
                base_person person = basePersonService.insertOrUpdatePersonTempUser(sysTempuser);
                //3.生成base_cncObj
                baseCnctobjService.addCnctObj(user, person);
                this.insert("sys_user_unit", Chain.make("userId", user.getId()).add("unitId", user.getUnitid()));
                //分配角色
                sysRoleService.linkUserAndRole(sysTempuser.getRoleCode(), user);
                //往企业微信通讯录创建用户
//                sysWxService.addWXUser(sysTempuser.getUsername(),sysTempuser.getTel());
                //建立关系
                String userid = sysWxService.createWXUser(sysTempuser.getUsername(), sysTempuser.getTel(),sysTempuser.getJobNumber());
                //下载到本地sys_wx_user
                if (!Strings.isBlank(userid)) {
                    JSONObject jsonObject = sysWxService.getWxUser(userid);
                    if (jsonObject != null) {
                        sysWxUserService.insertWxUser(jsonObject);
                        //绑定企业微信
                        basePersonService.bindWxuser(person.getId(), userid);
                    }
                }

            }
        } else {

        }
    }

    public static void main(String[] args) {
    }
}
