package cn.wizzer.app.logistics.modules.services.impl;

import cn.wizzer.framework.base.service.BaseServiceImpl;
import cn.wizzer.app.logistics.modules.models.logistics_orderentry;
import cn.wizzer.app.logistics.modules.services.LogisticsOrderentryService;
import org.nutz.aop.interceptor.ioc.TransAop;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.pager.Pager;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@IocBean(args = {"refer:dao"})
public class LogisticsOrderentryServiceImpl extends BaseServiceImpl<logistics_orderentry> implements LogisticsOrderentryService {
    public LogisticsOrderentryServiceImpl(Dao dao) {
        super(dao);
    }

    public List<Map<String,Object>> getOrderEntryByOrderId(String orderid, String dataType, Integer pagesize, Integer pagenumber) {
        List<logistics_orderentry> logistics_orderentries;
        if("getAllMateriaData".equals(dataType)){
             logistics_orderentries = this.query(Cnd.where("orderid", "=", orderid).orderBy("sequencenum","ASC"));
        }else{
            Pager pager = new Pager(1);
            if (pagenumber != null && pagenumber.intValue() > 0) {
                pager.setPageNumber(pagenumber.intValue());
            }
            if (pagesize == null) {
                pager.setPageSize(10);
            }else{
                pager.setPageSize(pagesize.intValue());
            }
            logistics_orderentries  = this.query(Cnd.where("orderid", "=", orderid).orderBy("sequencenum","ASC"),pager);
        }
        List<Map<String,Object>> list = new ArrayList<>();
        for (logistics_orderentry logisticsOrderentry : logistics_orderentries) {
            Map<String,Object> map = new HashMap<>();
            map.put("materielnum",logisticsOrderentry.getMaterielnum());
            map.put("materielname",logisticsOrderentry.getMaterielname());
            map.put("sequencenum",logisticsOrderentry.getSequencenum() == null ? "" : logisticsOrderentry.getSequencenum());
            map.put("quantity",logisticsOrderentry.getQuantity());
            map.put("batch",logisticsOrderentry.getBatch());
            map.put("pstatus",logisticsOrderentry.getPstatus());
            map.put("orderstate",logisticsOrderentry.getOrderstate());
            map.put("stock",logisticsOrderentry.getStock()  != null? logisticsOrderentry.getStock() :"");
            map.put("pnr",logisticsOrderentry.getPnr());
            map.put("sn",logisticsOrderentry.getSn());
            map.put("location",logisticsOrderentry.getLocation());
            map.put("lorderno",logisticsOrderentry.getLorderno());
            map.put("msn",logisticsOrderentry.getMsn());
            map.put("materialStock",logisticsOrderentry.getMaterialStock() == null ? "" :logisticsOrderentry.getMaterialStock());
            list.add(map);
        }
        return list;
    }

    @Aop(TransAop.READ_COMMITTED)
    public void editOrderEntriesByMobile(String orderid, String orderflag) {
        if(!Strings.isBlank(orderflag)){
            this.update(Chain.make("orderid",orderid),Cnd.where("orderflag","=",orderflag));
        }
    }

    public List<Map<String, Object>> getOrderEntryList(String orderEntryIDList) {
        List<Map<String,Object>> list = new ArrayList<>();
        if(!Strings.isBlank(orderEntryIDList)){
            String[] ids = orderEntryIDList.split(",");
            if(ids.length > 0){
                for (String id : ids) {
                    logistics_orderentry orderentry = this.fetch(id);
                    if(orderentry != null){
                        Map<String,Object> map = new HashMap<>();
                        map.put("materielname",orderentry.getMaterielname());
                        map.put("id",orderentry.getId());
                        map.put("materielnum",orderentry.getMaterielnum());
                        map.put("sequencenum",orderentry.getSequencenum());
                        map.put("batch",orderentry.getBatch());
                        map.put("quantity",orderentry.getQuantity());
                        map.put("materielid",orderentry.getMaterielid());
                        map.put("lorderno",orderentry.getLorderno());
                        map.put("msn",orderentry.getMsn());
                        list.add(map);
                    }
                }
            }
        }
        return list;
    }

    public List<Map<String, Object>> getOrderEntryListByOderFlag(String orderflag) {
        List<logistics_orderentry> orderentries = this.query(Cnd.where("orderflag", "=", orderflag));
        List<Map<String, Object>> list = new ArrayList<>();
        if(orderentries.size() > 0){
            for(logistics_orderentry orderentry : orderentries){
                Map<String, Object> map = new HashMap<>();
                map.put("id",orderentry.getId());
                map.put("MRNO",orderentry.getMaterielname());
                map.put("PN_L",orderentry.getMaterielnum());
                map.put("ITEMNO",orderentry.getSequencenum());
                map.put("BN",orderentry.getBatch());
                map.put("QTY",orderentry.getQuantity());
                map.put("STOCK",orderentry.getStock());
                map.put("LOCATION",orderentry.getLocation());
                map.put("PN_R",orderentry.getPnr());
                map.put("SN",orderentry.getSn());
                map.put("ORDERSTATE",orderentry.getOrderstate());
                map.put("msn",orderentry.getMsn());
                map.put("materialStock",orderentry.getMaterialStock() == null ?  "":orderentry.getMaterialStock());
                list.add(map);
            }
        }
        return list;
    }
    @Aop(TransAop.READ_COMMITTED)
    public void deleteOrderEntryListByOderFlag(String orderflag) {
        this.clear(Cnd.where("orderflag","=",orderflag));
    }
    @Aop(TransAop.READ_COMMITTED)
    public void deleteOrderEntryByID(String orderentryID) {
        this.clear(Cnd.where("id","=",orderentryID));
    }
}
