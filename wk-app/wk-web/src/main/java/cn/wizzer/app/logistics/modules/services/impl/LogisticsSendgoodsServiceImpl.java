package cn.wizzer.app.logistics.modules.services.impl;

import cn.wizzer.app.web.modules.controllers.open.api.Datatime.newDataTime;
import cn.wizzer.framework.base.service.BaseServiceImpl;
import cn.wizzer.app.logistics.modules.models.logistics_sendgoods;
import cn.wizzer.app.logistics.modules.services.LogisticsSendgoodsService;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Record;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.Sql;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.NutMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@IocBean(args = {"refer:dao"})
public class LogisticsSendgoodsServiceImpl extends BaseServiceImpl<logistics_sendgoods> implements LogisticsSendgoodsService {
    public LogisticsSendgoodsServiceImpl(Dao dao) {
        super(dao);
    }

    public NutMap getJsonDataOfCountOrder(String airportId) {
        NutMap json = new NutMap();
            //今日合同
            int todayContracts = this.count("logistics_sendgoods",
                    Cnd.where("createTime", ">=", newDataTime.getIntegerByDate(newDataTime.startOfToday())).
                        and("createTime", "<=", newDataTime.getIntegerByDate(newDataTime.endOfToday())).and("airportID","=",airportId));
            //全部合同
            int allContracts = this.count("logistics_sendgoods",Cnd.where("airportID","=",airportId));
            //发货中
            int sendingGoods = this.count("logistics_sendgoods", Cnd.where("pstatus", "=",7 ).and("airportID","=",airportId));
            //已完成
            int completeSend = this.count("logistics_sendgoods", Cnd.where("pstatus", "=",9 ).and("airportID","=",airportId));
            json.put("todayContracts",todayContracts);
            json.put("allContracts",allContracts);
            json.put("sendingGoods",sendingGoods);
            json.put("completeSend",completeSend);
        return json;
    }

    public List<Map<String, Object>> mdata(String pstatus, String airportid, Integer pagenumber, Integer pagesize) {
        Cnd cnd =Cnd.NEW();
        cnd.and("pstatus","in",pstatus);
        cnd.and("airportID","=",airportid);
        Pager pager = new Pager
                (1);
        if (pagenumber != null && pagenumber.intValue() > 0) {
            pager.setPageNumber(pagenumber.intValue());
        }
        if (pagesize != null) {
            pager.setPageSize(pagesize.intValue());
        }
        List<logistics_sendgoods> logisticsSendgoods = this.query(cnd,pager);
        List<Map<String,Object>> list = new ArrayList<>();
        for (logistics_sendgoods logisticsSendgood : logisticsSendgoods) {
                Map<String,Object> map = new HashMap<>();
                map.put("id",logisticsSendgood.getId());
                map.put("receivename",logisticsSendgood.getReceivename());
                map.put("packID",logisticsSendgood.getPackID());
                map.put("contractcode",logisticsSendgood.getContractcode());
                list.add(map);
        }
        return list;
    }

    public List<Map<String, Object>> planAndBeforeSendData(String pstatus, String airportid, Integer pagenumber, Integer pagesize) {
        String sqlstr="select *,(select count(*) from logistics_sendgoods where planID=sendplan.id) as totalpacknum from logistics_order a left join logistics_sendplan sendplan on a.sendplanid=sendplan.id" +
                " where isTF is not null and airportid='"+airportid+"' and sendplan.pstatus in ("+pstatus+")";
        sqlstr += " LIMIT "  + (pagenumber - 1) * pagesize+ "," + pagesize;
        Sql sql= Sqls.queryRecord(sqlstr);

        dao().execute(sql);
        List<Record> res = sql.getList(Record.class);
        List<Map<String,Object>> list = new ArrayList<>();
        for (Record re : res) {
            Map<String,Object> map =new HashMap<>();
            map.put("receivename",re.getString("receivename"));
            map.put("tansporttype",re.getString("tansporttype"));
            map.put("totalpacknum",re.getString("totalpacknum"));
            map.put("sender",re.getString("sender"));
            map.put("senddate",re.getString("senddate"));
            map.put("pstatus",re.getString("pstatus"));
            map.put("sendplanid",re.getString("sendplanid"));
            list.add(map);
        }
        return list;
    }

    public Map<String, Object> getGoodsInfo(String hlid) {
        logistics_sendgoods logisticsSendgoods = this.fetch(hlid);
        Map<String,Object> map = new HashMap<>();
        map.put("contractcode",logisticsSendgoods.getContractcode());
        map.put("receivename",logisticsSendgoods.getReceivename());
        map.put("partnum",logisticsSendgoods.getPartnum());
        map.put("serialnum",logisticsSendgoods.getSerialnum());
        map.put("number",logisticsSendgoods.getNumber());
        return map;
    }

    @Override
    public List<Map<String, Object>> getListbyPlanID(String planid, Integer pagenumber, Integer pagesize) {
        String sqlstr="select a.*,unplan.materielnum packnum,d.name packname,(case a.tansporttype when '0' then '空运' when '1' then '陆运' when '2' then '上门自提' else '' end) transname from logistics_sendgoods a left join logistics_unplan unplan on a.packID=unplan.id" +
                " left join sys_dict d on unplan.packtype=d.id where  a.planID='"+planid+"' ";
        sqlstr += " LIMIT  " + (pagenumber-1)* pagesize + "," +pagesize ;
        Sql sql= Sqls.queryRecord(sqlstr);
        dao().execute(sql);
        List<Record> res = sql.getList(Record.class);
        List<Map<String,Object>> list = new ArrayList<>();
        for (Record re : res) {
            Map<String,Object> map =new HashMap<>();
            map.put("contractcode",re.getString("contractcode"));
            map.put("id",re.getString("id"));
            map.put("packname",re.getString("packname"));
            map.put("packnum",re.getString("packnum"));
            map.put("partnum",re.getString("partnum"));
            map.put("serialnum",re.getString("serialnum"));
            list.add(map);
        }
        return list;
    }

}
