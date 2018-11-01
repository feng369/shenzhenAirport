package cn.wizzer.app.base.modules.services.impl;

import cn.wizzer.framework.base.Result;
import cn.wizzer.framework.base.service.BaseServiceImpl;
import cn.wizzer.app.base.modules.models.base_personpool;
import cn.wizzer.app.base.modules.services.BasePersonpoolService;
import cn.wizzer.framework.page.OffsetPager;
import org.nutz.aop.interceptor.ioc.TransAop;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Record;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.util.Daos;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.annotation.Param;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@IocBean(args = {"refer:dao"})
public class BasePersonpoolServiceImpl extends BaseServiceImpl<base_personpool> implements BasePersonpoolService {
    public BasePersonpoolServiceImpl(Dao dao) {
        super(dao);
    }


    public NutMap getPersonRow(int length,int start,int draw,Cnd cnd,String linkName) {
        Daos.CHECK_COLUMN_NAME_KEYWORD  = true;
        Sql sqlcount =Sqls.queryRecord("SELECT distinct(rowofficeid),startdata,enddata FROM base_personpool order by startdata asc");
        dao().execute(sqlcount);
        List listcount = sqlcount.getList(Record.class);

        Pager pager = new OffsetPager(start, length);
        Sql sql = Sqls.queryRecord("SELECT distinct(t.rowofficeid),t1.officename,t.startdata,t.enddata,t.pstatus FROM base_personpool t  INNER JOIN base_rowoffice t1 on t.rowofficeid=t1.id order by t.startdata desc");
        sql.setPager(pager);
        dao().execute(sql);
        List list = sql.getList(Record.class);
        NutMap re = new NutMap();
        if (!Strings.isBlank(linkName)) {
            this.dao().fetchLinks(list, linkName);
        }
        re.put("recordsFiltered", listcount.size());
        re.put("data", list);
        re.put("draw", draw);
        re.put("recordsTotal", length);
        return re;
    }
    //检查班次
    public boolean checkWork(String id ,String startdata){
        Cnd cnd = Cnd.NEW();
        cnd.and("rowofficeid", "=", id);
        cnd.and("enddata", ">=", startdata);
        cnd.and("pstatus","=","1");
        List<base_personpool> basepersonpools = this.dao().query(base_personpool.class,cnd);
        if (basepersonpools.size() > 0) {
            return false;
        }else{
            return true;
        }
    }

    @Aop(TransAop.READ_COMMITTED)
    public void updatePosition(String personid, String position) {
        Cnd cnd=Cnd.NEW();
        cnd.and("pstatus","=",1);
        cnd.and("personid","=",personid);
        base_personpool basepersonpool = this.fetch(cnd);
        basepersonpool.setPosition(position);
        this.updateIgnoreNull(basepersonpool);
    }

    @Aop(TransAop.READ_COMMITTED)
    public void setPersonByM(String personlist) {
        Sql sql= Sqls.queryRecord("update base_personpool set personstatus=1 where personid in ("+personlist+") ");//去掉了 and now() BETWEEN startdata and enddata 人员池里每个人只会存在一条数据
        dao().execute(sql);
    }

    public List<Map<String,Object>> getPersonpoolByM(String airportid,Integer pagenumber,Integer pagesize) {
        List<Map<String,Object>> list = new ArrayList<>();
        if(!Strings.isBlank(airportid)){
            String sqlStr = "select a.*,b.airportid,b.personname from base_personpool a left join base_person b on a.personid=b.id where a.pstatus=1  and b.airportid='"+airportid+"'";
            if (pagesize == null || pagesize > 10) {
                pagesize = 10;
            }
            if (pagenumber != 0) {
                if (pagenumber == null) {
                    pagenumber = 1;
                }
                if (pagesize == null || pagesize > 10) {
                    pagesize = 10;
                }
                sqlStr += " LIMIT " + (pagenumber - 1) * pagesize + "," + pagesize;
            }
            Sql sql=Sqls.queryRecord(sqlStr);//1 当班 0 下班  and (now() BETWEEN startdata and enddata) 去掉这段话，去掉当班时间，因为可能存在加班
            dao().execute(sql);
            List<Record> res = sql.getList(Record.class);
            for (Record re : res) {
                Map<String,Object> map = new HashMap<>();
                map.put("personname",re.getString("personname"));
                map.put("personid",re.getString("personid"));
                list.add(map);
            }
        }
        return list;
    }
}
