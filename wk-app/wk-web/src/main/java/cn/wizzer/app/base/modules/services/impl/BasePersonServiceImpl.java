package cn.wizzer.app.base.modules.services.impl;

import cn.wizzer.app.base.modules.models.base_airport;
import cn.wizzer.app.base.modules.models.base_cnctobj;
import cn.wizzer.app.base.modules.services.BaseAirportService;
import cn.wizzer.app.base.modules.services.BaseCnctobjService;
import cn.wizzer.app.logistics.modules.models.logistics_order;
import cn.wizzer.app.logistics.modules.models.logistics_sendtrace;
import cn.wizzer.app.logistics.modules.services.LogisticsOrderService;
import cn.wizzer.app.logistics.modules.services.LogisticsSendtraceService;
import cn.wizzer.app.sys.modules.models.Sys_tempuser;
import cn.wizzer.app.sys.modules.models.Sys_unit;
import cn.wizzer.app.sys.modules.models.Sys_user;
import cn.wizzer.app.sys.modules.services.SysUnitService;
import cn.wizzer.app.sys.modules.services.SysWxUserService;
import cn.wizzer.app.web.commons.base.Globals;
import cn.wizzer.framework.base.Result;
import cn.wizzer.framework.base.service.BaseServiceImpl;
import cn.wizzer.app.base.modules.models.base_person;
import cn.wizzer.app.base.modules.services.BasePersonService;
import cn.wizzer.framework.util.StringUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.nutz.aop.interceptor.ioc.TransAop;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.Record;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.Sql;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.trans.Trans;

import javax.xml.bind.ValidationException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@IocBean(args = {"refer:dao"})
public class BasePersonServiceImpl extends BaseServiceImpl<base_person> implements BasePersonService {
    @Inject
    private BasePersonService basePersonService;
    @Inject
    private BaseCnctobjService baseCnctobjService;
    @Inject
    private LogisticsOrderService logisticsOrderService;
    @Inject
    private LogisticsSendtraceService logisticsSendtraceService;
    @Inject
    private BaseAirportService baseAirportService;
    @Inject
    private SysUnitService sysUnitService;
    @Inject
    private SysWxUserService sysWxUserService;


    public BasePersonServiceImpl(Dao dao) {
        super(dao);
    }

    public List<Sys_unit> getDatas(String personid) {
        Sql sql = Sqls.create("select a.id,a.name from sys_unit a INNER JOIN base_person b WHERE a.id = b.unitid and b.id = @personid");
        sql.params().set("personid", personid);
        sql.params().set("f",false);
        Entity<Sys_unit> entity = dao().getEntity(Sys_unit.class);
        sql.setEntity(entity);
        sql.setCallback(Sqls.callback.entities());
        dao().execute(sql);
        return sql.getList(Sys_unit.class);
    }
    //根据登录人ID获取人员相关信息
    public base_person getPersonInfo(){
        try {
            Subject subject = SecurityUtils.getSubject();
            Sys_user curUser = (Sys_user) subject.getPrincipal();
            String userid = curUser.getId();
            //通过登录人ID获取人员信息

            base_person person = new base_person();
            Cnd cnd = Cnd.NEW();
            cnd.and("userId", "=", userid);
            base_cnctobj cnctobj=baseCnctobjService.fetch(cnd);

            if (cnctobj != null) {
                person = basePersonService.fetch(cnctobj.getPersonId());
            }
            return person;
        }catch (Exception e){
            return null;
        }
    }

    public List<Map<String, Object>> getPersonByMobile(String airportid, Integer pagenumber, Integer pagesize) {
        Pager pager = new Pager
                (1);
        if (pagenumber != null && pagenumber.intValue() > 0) {
            pager.setPageNumber(pagenumber.intValue());
        }
        if (pagesize == null || pagesize > 10) {
            pager.setPageSize(10);
        }else{
            pager.setPageSize(pagesize.intValue());
        }
        List<base_person> basePersonList = basePersonService.query(Cnd.where("airportid","=",airportid),pager);
        List<Map<String,Object>> list = new ArrayList<>();
        for (base_person basePerson : basePersonList) {
            Map<String,Object> map = new HashMap<>();
            map.put("id",basePerson.getId());
            map.put("customercode",basePerson.getCustomer() == null ? "" :basePerson.getCustomer().getCustomercode());
            map.put("customername",basePerson.getCustomer() == null ? "" : basePerson.getCustomer().getCustomername());
            map.put("customeraddress",basePerson.getCustomer() == null ? "" : basePerson.getCustomer().getCustomeraddress());
            map.put("customertel",basePerson.getCustomer() == null ? "" : basePerson.getCustomer().getCustomertel());
            map.put("cusnum",basePerson.getCustomer() == null ? "" :basePerson.getCustomer().getCusnum());
            map.put("customerdecrib",basePerson.getCustomer() == null ? "" : basePerson.getCustomer().getCustomerdecrib());
            list.add(map);
        }
        return list;
    }

    public List<Map<String, Object>> getPersonList(String customerid,String personname, Integer pagenumber, Integer pagesize) {

        Sql sqlStr = Sqls.queryRecord(StringUtil.appendSqlStrByPager(pagenumber,pagesize,"select a.id,b.userId,a.personname,a.jobNumber from base_person a left join base_cnctobj b on a.id=b.personId where a.customerid = @customerid and a.personname like '%"+personname+"%' "));
        sqlStr.params().set("customerid", customerid);

        dao().execute(sqlStr);
        List<Record> record = sqlStr.getList(Record.class);
        List<Map<String, Object>> personList = new ArrayList<>();
        if (record!=null&&record.size()>0){
            for(Record r :record){
                Map<String, Object> map  = new HashMap<>();
                String userid = r.getString("userId");
                map.put("userid",userid);
                String personid = r.getString("id");
                map.put("personid",personid);
                String pname = r.getString("personname");
                map.put("personname",pname);
                String jobnum = r.getString("jobnumber");
                map.put("jobnum",jobnum==null?"":jobnum);
                personList.add(map);
            }
        }

        return personList;
    }

    public Map getPersonByID(String id) {
        base_person basePerson = basePersonService.fetch(id);
        Map map  =new HashMap();
        if(basePerson != null){
            map.put("personname",basePerson.getPersonname());
        }
        return map;
    }

    @Aop(TransAop.READ_COMMITTED)
    public base_person insertOrUpdatePersonTempUser(Sys_tempuser sysTempuser){
        if(sysTempuser != null && !Strings.isBlank(sysTempuser.getCustomerid()) && !Strings.isBlank(sysTempuser.getJobNumber())){
            List<base_person> personList = this.query(Cnd.where("jobNumber", "=", sysTempuser.getJobNumber()).and("customerId", "=", sysTempuser.getCustomerid()));
            if(personList.size() >0 ){
//            已经存在该员工
                this.update(Chain.make("personname",sysTempuser.getUsername()).add("unitid",sysTempuser.getUnitid()).add("tel",sysTempuser.getTel()).add("wxNumber",sysTempuser.getWxNumber()).add
                        ("picture",sysTempuser.getPicture()),Cnd.where("id","=",personList.get(0).getId()));
            return this.fetch(personList.get(0).getId());
            } else {
//            不存在该员工
                base_person person = new base_person();
                person.setPersonname(sysTempuser.getUsername());
                person.setUnitid(sysTempuser.getUnitid());
                person.setTel(sysTempuser.getTel());
                person.setJobNumber(sysTempuser.getJobNumber());
                person.setPicture(sysTempuser.getPicture());
                person.setCustomerId(sysTempuser.getCustomerid());
                person.setAirportid(Globals.airportId);
                person.setWxNumber(sysTempuser.getWxNumber());
                person.setPersonnum(getPersonNum(Globals.airportId,sysTempuser.getUnitid()));
            return this.insert(person);
            }
        }
        return null;
    }


    public String getPersonNum(String airportid, String unitid) {
        base_airport airport = baseAirportService.fetch(airportid);
        Sys_unit unit = sysUnitService.fetch(unitid);

        Cnd cnd1 = Cnd.NEW();
        cnd1.and("airportid", "=", airportid);
        cnd1.and("unitid", "=", unitid);
        cnd1.desc("createTime");
        List<base_person> personList = basePersonService.query(cnd1);

        String pnumber = "";
        System.out.println(personList.get(0).getPersonnum());
        System.out.println(personList.get(0));

        if (airport != null && unit != null && personList.size() > 0 && personList.get(0).getPersonnum().split("-").length > 1) {
            pnumber = personList.get(0).getPersonnum().split("-")[2];
            int num = Integer.parseInt(pnumber) + 1;
            switch (String.valueOf(num).length()) {
                case 1:
                    pnumber = airport.getAirportnum() + "-" + unit.getUnitcode() + "-000" + String.valueOf(num);
                    break;
                case 2:
                    pnumber = airport.getAirportnum() + "-" + unit.getUnitcode() + "-00" + String.valueOf(num);
                    break;
                case 3:
                    pnumber = airport.getAirportnum() + "-" + unit.getUnitcode() + "-0" + String.valueOf(num);
                    break;
                case 4:
                    pnumber = airport.getAirportnum() + "-" + unit.getUnitcode() + "-" + String.valueOf(num);
                    break;
            }
        } else {
            pnumber = airport.getAirportnum() + "-" + unit.getUnitcode() + "-0001";
        }

        return pnumber;
    }
    //绑定系统人员和微信用户账号
    public void bindWxuser(String id, String wxuserid) {
        base_person person = this.fetch(id);
        if(person != null){
            if(!Strings.isBlank(person.getWxUserid())){
                //如果之前已经绑定微信,则要先解绑
                sysWxUserService.update(Chain.make("bindPerson",0),Cnd.where("userid","=",person.getWxUserid()));
            }
            //更新当前person的wxUserid
            this.update(Chain.make("wxUserid",wxuserid),Cnd.where("id","=",id));
            //更新关联的sys_wx_user中的bindPerson
            sysWxUserService.update(Chain.make("bindPerson",1),Cnd.where("userid","=",wxuserid));
        }
    }

    @Aop(TransAop.READ_COMMITTED)
    public Result unbindWx(String[] ids,String[] wxUids) throws ValidationException {
        if(ids != null && ids.length > 0){
            //将企业微信账号清除
            this.update(Chain.make("wxUserid",null),Cnd.where("id","IN",ids));
            sysWxUserService.update(Chain.make("bindPerson",0),Cnd.where("userid","IN",wxUids));
            return Result.success("解绑成功");
        }
        throw new ValidationException("传入参数不为NULL");
    }
}
