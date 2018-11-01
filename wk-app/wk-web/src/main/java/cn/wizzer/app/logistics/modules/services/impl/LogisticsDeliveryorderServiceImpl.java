package cn.wizzer.app.logistics.modules.services.impl;

import cn.wizzer.app.base.modules.models.base_personpool;
import cn.wizzer.app.base.modules.models.base_place;
import cn.wizzer.app.base.modules.services.BaseCustomerService;
import cn.wizzer.app.base.modules.services.BasePersonpoolService;
import cn.wizzer.app.base.modules.services.BasePlaceService;
import cn.wizzer.app.logistics.modules.models.*;
import cn.wizzer.app.logistics.modules.services.*;
import cn.wizzer.app.sys.modules.services.SysDictService;
import cn.wizzer.app.sys.modules.services.SysTaskService;
import cn.wizzer.app.web.commons.base.Globals;
import cn.wizzer.app.web.modules.controllers.open.api.Datatime.newDataTime;
import cn.wizzer.framework.base.ValidatException;
import cn.wizzer.framework.base.service.BaseServiceImpl;
import cn.wizzer.framework.map.MapUtils;
import cn.wizzer.framework.map.ValueComparator;
import org.nutz.aop.interceptor.ioc.TransAop;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Record;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.util.cri.SqlExpressionGroup;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.trans.Trans;

import javax.xml.bind.ValidationException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@IocBean(args = {"refer:dao"})
public class LogisticsDeliveryorderServiceImpl extends BaseServiceImpl<logistics_Deliveryorder> implements LogisticsDeliveryorderService {
    private static final Log log = Logs.get();
    @Inject
    private SysTaskService sysTaskService;

    @Inject
    private LogisticsDeliveryorderentryService logisticsDeliveryorderentryService;

    @Inject
    private LogisticsOrderService logisticsOrderService;

    @Inject
    private LogisticsOrderstepService logisticsOrderstepService;

    @Inject
    private LogisticsOrderdeliveryService logisticsOrderdeliveryService;


    @Inject
    private BaseCustomerService baseCustomerService;

    @Inject
    private BasePlaceService basePlaceService;

    @Inject
    private SysDictService sysDictService;

    @Inject
    private BasePersonpoolService basePersonpoolService;


    @Inject
    private LogisticsSendtraceService  logisticsSendtraceService;
    @Inject
    private LogisticsOrderentryService logisticsOrderentryService;

    private ConcurrentHashMap countMap = new ConcurrentHashMap();
    private SimpleDateFormat ymd = new SimpleDateFormat("yyyy-MM-dd");
    public LogisticsDeliveryorderServiceImpl(Dao dao) {
        super(dao);
    }

    public void save(logistics_Deliveryorder logistics_deliveryorder){
        dao().insertWith(logistics_deliveryorder,null);
    }

    public List<logistics_Deliveryorder> getDeliveryOrderByMobile(String pstatus,String personid){
        try{
            SqlExpressionGroup s=null;
            SqlExpressionGroup q=null;
            if (!Strings.isBlank(pstatus))
            {
                String [] stat=pstatus.split(",");
                s= Cnd.exps("pstatus","=",stat[0]);
                for(int i=1;i<stat.length;i++){
                    s.or("pstatus","=",stat[i]);
                }

            }

            if(!Strings.isBlank(personid)){
                q=Cnd.exps("personid","=",personid);
            }
            Cnd cnd=Cnd.where(s).and(q);
            return this.query(cnd);
        }
        catch(Exception e){
            e.printStackTrace();
            return  null;
        }
    }

    @Aop(TransAop.READ_COMMITTED)
    public void orderexe(String orderid){
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat formatternow = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String date = formatter.format(new Date());
            String datetime=formatternow.format(new Date());
            Cnd stepcnd=Cnd.NEW();
            Date data=new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateNowStr = sdf.format(data);

            String num="";
            //获取订单信息
            logistics_order order = logisticsOrderService.fetch(orderid);
            logistics_Deliveryorder deliveryorder = new logistics_Deliveryorder();

            stepcnd.and("otype","=",order.getOtype());
            String cjxq = logisticsOrderstepService.getStepByStepnum("CJXQ","72dd55d2c3cc42799c4e014745db2cdb");
            String djd = logisticsOrderstepService.getStepByStepnum("DJD","72dd55d2c3cc42799c4e014745db2cdb");
            //获取步骤
            List<logistics_Deliveryorderentry> deliveryorderentry= Json.fromJsonAsList(logistics_Deliveryorderentry.class, Json.toJson(logisticsOrderstepService.query(stepcnd)));

            for (int i = 0; i < deliveryorderentry.size(); i++) {
                deliveryorderentry.get(i).setOrderid(orderid);
                //如果步骤为1，将步骤1设置为完成状态，并且设置订单提交时间
                if(cjxq.equals(deliveryorderentry.get(i).getStep().toString())||djd.equals(deliveryorderentry.get(i).getStep().toString())){
                    deliveryorderentry.get(i).setPstatus(1);
                    deliveryorderentry.get(i).setOperatetime(datetime);
                }else{
                    deliveryorderentry.get(i).setPstatus(0);
                }
            }

            //生成配送单编号

            Cnd cnd = Cnd.NEW();
            cnd.and("DATE_FORMAT(datetime, '%Y-%m-%d')","=",date);
            logistics_Deliveryorder delorder=this.fetch(cnd);


            //20180719zhf1044
            StringBuffer sb = new StringBuffer(80);
            sb.append("PSD-").append(date).append("-");
            int deliveryOrderSeq = getDeliveryOrderSeq(date);
            if(deliveryOrderSeq <= 9999){
                sb.append(String.format("%04d", deliveryOrderSeq));
            }else{
                sb.append(String.format("%0"+String.valueOf(deliveryOrderSeq).length()+"d"));
            }
            num = sb.toString();
            /*if(delorder == null){
                num="PSD-"+date+"-0001";
            }else{

                List<logistics_Deliveryorder> dorder=this.query("select max(right(deliveryordernum,4)) from logistics_order");
                if(dorder.size()==0){
                    num = "PSD-" + date + "-0001";

                }else{
                    String ordernum = dorder.get(0).getDeliveryordernum();
                    int _num= (Integer.parseInt(ordernum.substring(ordernum.length()- 4))+1);
                    num=String.format("%04d", _num);
                    num="PSD"+"-"+date+"-"+num;
                }
            }*/
            //获取订单中的地点经纬度
//            base_place basePlace = basePlaceService.fetch(order.getStartstock());
//            String positions=basePlace.getPosition();
//            List<String> result = Arrays.asList(positions.split(","));
//            double lat1=Double.parseDouble(result.get(1));
//            double lng1=Double.parseDouble(result.get(0));
            //获取人员的经纬度
            Cnd cp = Cnd.NEW();
            cp.and("pstatus","=",1);
            List<base_personpool> basePersonpools = basePersonpoolService.query(cp);
            HashMap<String, Double> hashmap = new HashMap();

            //蔡巍注释20180605，personpool目前没有添加position功能
            //取所有空闲人员的地理位置进行比较
//            for(int i=0;i<basePersonpools.size();i++){
//                String posi = basePersonpools.get(i).getPosition();
//                List<String> results = Arrays.asList(posi.split(","));
//                double lat2=Double.parseDouble(results.get(1));
//                double lng2=Double.parseDouble(results.get(0));
//                double distance= MapUtils.GetDistance(lat1,lng1,lat2,lng2);
//                hashmap.put(basePersonpools.get(i).getPersonid(),distance);
//            }
//            //取出距离最近的personid
//            ValueComparator bvc = new ValueComparator(hashmap);
//            TreeMap<String, Double> sorted_map = new TreeMap<String, Double>(bvc);
//            sorted_map.putAll(hashmap);
//            String persionid = sorted_map.lastKey();
            //生成配送单
            deliveryorder.setDeliveryordernum(num);
            deliveryorder.setDatetime(formatternow.format(new Date()));
            deliveryorder.setPstatus(0);
            //deliveryorder.setPersonid(persionid);
            deliveryorder.setLogistics_deliveryorderentry(deliveryorderentry);

            //生成订单与配送单关联信息
            logistics_orderdelivery lorderdelivery= new logistics_orderdelivery();
            lorderdelivery.setOrderid(orderid);
            List<logistics_orderdelivery> logistics_orderdelivery = new ArrayList();
            logistics_orderdelivery.add(lorderdelivery);
            deliveryorder.setLogistics_orderdelivery(logistics_orderdelivery);
            //检查订单的状态，只有待接订单状态的才能接单
            if(order.getPstatus() == 1){
                this.save(deliveryorder);
                //修改需求订单状态
                order.setPstatus(2);
                //根据订单id获取配送单ID
                Cnd cri = Cnd.NEW();
                cri.and("orderid","=",order.getId());
                logistics_orderdelivery orderdelivery =logisticsOrderdeliveryService.getField("deliveryorderid",cri);
                order.setDeliveryorderid(orderdelivery.getDeliveryorderid());
                logisticsOrderService.update(order);
            }
        } catch (Exception e) {
            log.info("订单接单错误：" + e.getMessage());
            throw new ValidatException("订单接单错误：" + e.getMessage());
        }
    }
    public String getDeliveryOrderNum(String date){
        StringBuffer sb = new StringBuffer(80);
        sb.append("PSD-").append(date).append("-");
        int deliveryOrderSeq = getDeliveryOrderSeq(date);
        if(deliveryOrderSeq <= 9999){
            sb.append(String.format("%04d", deliveryOrderSeq));
        }else{
            sb.append(String.format("%0"+String.valueOf(deliveryOrderSeq).length()+"d"));
        }
        return sb.toString();
    }
    public synchronized int getDeliveryOrderSeq(String date){
        int prevCount = 0;
        //跨天从0开始
        boolean isNewDay = false;
        if(countMap.get("date")!=null){
            String date0 = (String) countMap.get("date");
            if(!date0.equals(date)){
                isNewDay =true;
                countMap.put("date",date);
            }
        }else{
            countMap.put("date",date);
        }
        if(countMap.get(Globals.customerid) == null){
//            prevCount = this.count(Cnd.where("DATE_FORMAT(datetime, '%Y-%m-%d')", "=", date));
            //20180807 防止被删除历史单据后造成单号重复
            Sql sql = Sqls.create("select deliveryordernum from logistics_Deliveryorder where datetime>='"+date+" 00:00:00' and  datetime<='"+date+" 23:59:59' order by deliveryordernum desc  limit 1");
            List<Record> list = this.list(sql);
            if(list.size()==1){
                Record r = list.get(0);
                String number = r.getString("deliveryordernum");
                String[]numArr = number.split("-");
                prevCount = Integer.parseInt(numArr[numArr.length-1]);
            }else{
                prevCount= 0;
            }

            countMap.put(Globals.customerid,prevCount +1);
        }else{
            if(isNewDay){
                countMap.put(Globals.customerid, 1);
            }else {
                prevCount = (int) countMap.get(Globals.customerid);
                countMap.put(Globals.customerid, prevCount + 1);
            }
        }
        return (int)countMap.get(Globals.customerid);
    }

    public boolean checkorderre(String orderid){
        Cnd cnd = Cnd.NEW();
        cnd.and("orderid","=",orderid);
        int count =logisticsDeliveryorderentryService.count(cnd);
        if(count == 0){
            return true;
        }else {
            return false;
        }
    }

    public List<Map<String,Object>> getDeliveryOrderList(String personid, String pstatus, String deliveryordernum,Integer pagenumber,Integer pagesize){
        String sqlstr = " select a.id,b.deliveryorderid,b.startname,b.endname,c.personname,b.btypename,b.otypename,b.id as orderid,b.pstatus,b.ordernum,rec.operatetime as receiveTime,sdDo.operatetime as sdTime,c.personname as receivePersonName,checBp.personname as checkPersonName,chec.operatetime as checkOrderTime  from logistics_Deliveryorder as a left join " +
                " ( SELECT o.id,o.deliveryorderid,p.placename startname,l.placename endname,o.btype,o.ordernum,s.name as btypename,y.name as otypename,o.pstatus  from logistics_order as o left join base_place as p on o.startstock=p.id " +
                " left join base_place l on o.endstock=l.id left join sys_dict as s on o.btype=s.id left join sys_dict as y on o.otype=y.id) as b " +
                " on a.id=b.deliveryorderid " +
                " left join base_person as c on a.personid=c.id " +
                "  LEFT JOIN logistics_Deliveryorderentry rec ON rec.deliveryorderid = a.id "+
                "  LEFT JOIN logistics_Deliveryorderentry sdDo ON sdDo.deliveryorderid = a.id "+
                "  LEFT JOIN logistics_Deliveryorderentry chec ON chec.deliveryorderid = a.id "+
                " LEFT JOIN base_person checBp ON chec.setpPersonId = checBp.id " +
                " where  a.delFlag = 0 AND rec.step = 3 AND sdDo.step = 7 AND chec.step = 8 AND a.personid='" + personid + "' and a.pstatus=" + pstatus;
        if("1".equals(pstatus.trim())){
            sqlstr += " ORDER BY rec.operatetime DESC ";
        }else if("2".equals(pstatus.trim())){
            sqlstr += " ORDER BY sdDo.operatetime DESC ";
        }else if("3".equals(pstatus.trim())){
            sqlstr += " ORDER BY chec.operatetime DESC ";
        }
            /*
            @ReturnKey(key = "msn", description = "飞机号"),
            @ReturnKey(key = "receivePersonName", description = "接单人姓名"),
            @ReturnKey(key = "receiveTime", description = "接单时间"),
            @ReturnKey(key = "sdTime", description = "送达时间"),
            @ReturnKey(key = "checkPersonName", description = "收货人姓名"),
            @ReturnKey(key = "checkOrderTime", description = "收货时间"),
            */
        /*if(!Strings.isBlank(deliveryordernum)){
            sqlstr += " and a.deliveryordernum = '" + deliveryordernum + "' ";
        }*/

        if (pagenumber == null) {
            pagenumber = 1;
        }
        if (pagesize == null) {
            pagesize = 10;
        }
        if(!(pagenumber != null && pagenumber < 1 )){
            sqlstr += " LIMIT " + (pagenumber - 1) * pagesize + "," + pagesize;
        }
        Sql sql = Sqls.queryRecord(sqlstr);
        dao().execute(sql);
        List<Record> recordList = sql.getList(Record.class);
        List<Map<String,Object>> list = new ArrayList<>();
        if(recordList.size() > 0){
            for (Record record : recordList) {
                String orderId = record.getString("orderid");
                Map<String,Object> map = new HashMap<>();
                if(!Strings.isBlank(orderId)){
                    List<logistics_orderentry> logisticsOrderentries = logisticsOrderentryService.query(Cnd.where("orderid", "=", orderId));
                    if(logisticsOrderentries.size()  > 0){
                        for (logistics_orderentry orderentry:logisticsOrderentries){
                            /*
                            @ReturnKey(key = "lorderno", description = "指令号"),
                            @ReturnKey(key = "msn", description = "飞机号"),
                            @ReturnKey(key = "receivePersonName", description = "接单人姓名"),
                            @ReturnKey(key = "receiveTime", description = "接单时间"),
                            @ReturnKey(key = "sdTime", description = "送达时间"),
                            @ReturnKey(key = "checkPersonName", description = "收货人姓名"),
                            @ReturnKey(key = "checkOrderTime", description = "收货时间"),
                            */
                            map.put("lorderno",orderentry.getLorderno());
                            map.put("msn",orderentry.getMsn());
                        }
                    }else{
                        map.put("lorderno","");
                        map.put("msn","");
                    }
                }else{
                    map.put("lorderno","");
                    map.put("msn","");
                }
                map.put("id",record.getString("id"));
                map.put("deliveryorderid",record.getString("deliveryorderid"));
                map.put("startname",record.getString("startname"));
                map.put("endname",record.getString("endname"));
                map.put("personname",record.getString("personname"));
                map.put("btypename",record.getString("btypename"));
                map.put("otypename",record.getString("otypename"));
                map.put("orderid",record.getString("orderid"));
                map.put("pstatus",record.getInt("pstatus"));
                map.put("orderNum",record.getString("ordernum"));
                if(!Strings.isBlank(orderId)){
                    List<logistics_orderentry> logisticsOrderentries = logisticsOrderentryService.query(Cnd.where("orderid", "=", orderId));
                    if(logisticsOrderentries.size() > 0){
                        for (logistics_orderentry logisticsOrderentry : logisticsOrderentries) {
                            if(!Strings.isBlank(logisticsOrderentry.getSn())){
                                map.put("haveSn",true);
                                break;
                            }else{
                                map.put("haveSn",false);
                            }
                        }
                    }
                }
                map.put("receivePersonName",record.getString("receivePersonName"));
                map.put("receiveTime",record.getString("receiveTime"));
                map.put("sdTime",record.getString("sdTime"));
                map.put("checkPersonName",record.getString("checkPersonName"));
                map.put("checkOrderTime",record.getString("checkOrderTime"));
                list.add(map);
            }
        }
        return list;
    }

    @Aop(TransAop.READ_COMMITTED)
    public void addDelDo(String orderid, String personid,String curPosition) throws ValidationException {
        //更新订单
        logistics_order logisticsOrder= logisticsOrderService.getLogistics_order(orderid);
        logisticsOrder.setPstatus(3);
        logisticsOrderService.updateIgnoreNull(logisticsOrder);

        //将接单人更新到配送单中 cw0909
        //更新接单人状态为忙碌
        String dvid =logisticsOrder.getDeliveryorderid();
        logistics_Deliveryorder deliveryorder=this.fetch(dvid);
        if(deliveryorder!=null){
            deliveryorder.setPersonid(personid);
            deliveryorder.setPstatus(1);
            this.updateIgnoreNull(deliveryorder);

            Cnd cnd=Cnd.NEW();
            cnd.and("personid","=",personid);
            base_personpool personpool=basePersonpoolService.fetch(cnd);
            if(personpool!=null){
                personpool.setPersonstatus(1);
                basePersonpoolService.updateIgnoreNull(personpool);
            }
        }

//        20180601zhf1717
//        插入一条数据到配送跟踪表中(第一条)
        logisticsSendtraceService.addOne(deliveryorder,logisticsOrder,curPosition);

        //更新跟踪单
        if(!Strings.isBlank(dvid)){
            Cnd cnd=Cnd.NEW();
            cnd.and("orderid","=",orderid);
            cnd.and("deliveryorderid","=",dvid);
            cnd.and("step","=","2");
            String now = newDataTime.getDateYMDHMS();
            logisticsDeliveryorderentryService.upDventry(1,"","",now,personid,cnd);
        }
    }

    public List<Map<String, Object>> haveSendingOrders(String personid) {
        String sqlStr = "SELECT lo.id FROM logistics_order  lo JOIN logistics_Deliveryorder ld ON lo.deliveryorderid = ld.id  WHERE  ld.personid = '"+personid+"' AND lo.pstatus >= 3 AND lo.pstatus < 7 AND   lo.delFlag = 0 ";
        Sql sql = Sqls.queryRecord(sqlStr);
        dao().execute(sql);
        List<Record> list = sql.getList(Record.class);
        List<Map<String, Object>> orderIdList = new ArrayList<>();
        if(list.size() > 0){
            for (Record record : list) {
                String orderId = record.getString("id");
                if(!Strings.isBlank(orderId)){
                    Map<String, Object> map  = new HashMap<>();
                    map.put("orderid",orderId);
                    orderIdList.add(map);
                }
            }
        }
     return orderIdList;
    }




}
