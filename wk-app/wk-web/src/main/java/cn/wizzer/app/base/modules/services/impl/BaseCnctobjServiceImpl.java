package cn.wizzer.app.base.modules.services.impl;

import cn.wizzer.app.base.modules.models.base_person;
import cn.wizzer.app.sys.modules.models.Sys_role;
import cn.wizzer.app.sys.modules.models.Sys_user;
import cn.wizzer.framework.base.service.BaseServiceImpl;
import cn.wizzer.app.base.modules.models.base_cnctobj;
import cn.wizzer.app.base.modules.services.BaseCnctobjService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.nutz.aop.interceptor.ioc.TransAop;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.mvc.annotation.At;

import java.util.List;

@IocBean(args = {"refer:dao"})
public class BaseCnctobjServiceImpl extends BaseServiceImpl<base_cnctobj> implements BaseCnctobjService {
    public BaseCnctobjServiceImpl(Dao dao) {
        super(dao);
    }

    public base_cnctobj  getbase_cnctobj(String condition){

        return dao().fetch(base_cnctobj.class,condition);
    }

    public base_cnctobj getcnctobj(String userid){
        Cnd cnd=Cnd.NEW();
        cnd.and("userid","=",userid);
        base_cnctobj baseCnctobj = dao().fetch(base_cnctobj.class,cnd);
        if(baseCnctobj != null){
            String personid = baseCnctobj.getPersonId();
            if(!Strings.isBlank(personid))
                baseCnctobj.setPerson(dao().fetch(base_person.class,personid));
        }


        return baseCnctobj;

    }

    //根据当前登录人信息，过滤单位信息,当前登录人只能查看自己的组织信息
    public  Cnd unitDataPermission(Cnd cnd){
        Subject subject = SecurityUtils.getSubject();
        Sys_user curUser = (Sys_user) subject.getPrincipal();

        List<Sys_role> role = curUser.getRoles();
        if(!role.get(0).getCode().toString().equals("sysadmin")) {
            String curUserId = curUser.getId();
            base_cnctobj baseCnctobj = this.getcnctobj(curUserId);
            if(baseCnctobj != null){
                base_person basePerson = baseCnctobj.getPerson();
                String Punitid = basePerson.getUnitid();
                if (!Strings.isBlank(Punitid)) {
                    cnd.and("unitid", "=", Punitid);
                } else {
                    cnd.and("unitid", "=", "");
                }
            }else{
                cnd.and("unitid", "=", curUser.getUnitid());
            }

        }
        return cnd;
    }

    //根据当前登录人信息，过滤机场数据，查看当前机场信息
    public  Cnd airportDataPermission(Cnd cnd){
        Subject subject = SecurityUtils.getSubject();
        Sys_user curUser = (Sys_user) subject.getPrincipal();
        String curUserId = curUser.getId();
        List<Sys_role> role = curUser.getRoles();
        if(!role.get(0).getCode().toString().equals("sysadmin")) {
            base_cnctobj baseCnctobj=this.getcnctobj(curUserId);
            base_person basePerson=baseCnctobj.getPerson();
            String airportid= basePerson.getAirportid();
            if (!Strings.isBlank(airportid)){
                cnd.and("airportid", "=", airportid);
            }else{
                cnd.and("airportid", "=", "");
            }
        }
        return cnd;
    }

    //根据当前登录人信息，过滤机场数据，查看当前机场信息
    public  Cnd airportOrderPermission(Cnd cnd){
        Subject subject = SecurityUtils.getSubject();
        Sys_user curUser = (Sys_user) subject.getPrincipal();
        String curUserId = curUser.getId();
        List<Sys_role> role = curUser.getRoles();
        if(!role.get(0).getCode().toString().equals("sysadmin")) {
            base_cnctobj baseCnctobj = this.getcnctobj(curUserId);
//            base_person basePerson = baseCnctobj.getPerson();
            String customerid = "";
            String unitid = "";
            String startstock = "";
//            //获取人员类型  20180731屏蔽个人过滤条件
//            String persontype = basePerson.getEmptypeId();
//            if ("empType.customer".equals(persontype)) {
//                customerid = basePerson.getCustomerId();
//                cnd.and("customerid", "=", customerid);
//            } else if ("empType.stock".equals(persontype)) {
//                startstock = basePerson.getPlaceid();
//                cnd.and("startstock", "=", startstock);
//            }else if ("empType.employee".equals(persontype)) {
//                startstock = basePerson.getPlaceid();
//                if(role.get(0).getIsUnit() ==0)
//                cnd.and("personid", "=", basePerson.getId());
//            }


            if (baseCnctobj!=null && baseCnctobj.getPerson()!=null && !Strings.isBlank(baseCnctobj.getPerson().getAirportid())) {
                cnd.and("airportid", "=", baseCnctobj.getPerson().getAirportid());
            } else {
                cnd.and("airportid", "=", "");
            }
        }
        return cnd;
    }

    @Aop(TransAop.READ_COMMITTED)
    public base_cnctobj addCnctObj(Sys_user user, base_person person) {
        base_cnctobj cnctobj = new base_cnctobj();
        if(person!=null){
            cnctobj.setPersonId(person.getId());
        }
        if(user != null){
            cnctobj.setUserId(user.getId());
        }
        return this.insert(cnctobj);
    }


}
