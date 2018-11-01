package cn.wizzer.app.logistics.modules.services;

import cn.wizzer.framework.base.service.BaseService;
import cn.wizzer.app.logistics.modules.models.logistics_order;
import cn.wizzer.framework.page.datatable.DataTableColumn;
import cn.wizzer.framework.page.datatable.DataTableOrder;
import org.nutz.dao.Cnd;
import org.nutz.lang.util.NutMap;

import javax.xml.bind.ValidationException;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

public interface LogisticsOrderService extends BaseService<logistics_order>{
    void save(logistics_order logistics_order, String logisticsOrder);

    logistics_order getLogistics_order(String id);

    List<logistics_order>  getdata(Cnd cnd,String linkname);

    NutMap getJsonDataOfCountOrder(String otype,String airportId);

    Map getOrderCountByMobile(String personid,boolean receivedCheck);

    List<Map<String,Object>> getOrderList(String dataType, String personid, String pstatus, String numorder, String startstock, String endstock, String btype, String deliveryorderid, Integer pagenumber, Integer pagesize, String lorderno);

    void updataVehicle(String vehicleid, String orderid);

    Map updateDvSteps(String orderid, String content, String stepnum, String toreason, String personid) throws ValidationException;

    void addelDo(String orderid, String personid,String curPosition) throws ValidationException;

    Map<String,Object> getOrderInfo(String id) throws ValidationException;

    void updateOrderReject(String id);

    Map getOrderPstatus(String id);

    Map<String,Object> getOrderListOfCustomer(String personid);

    Map getOrderInfoOfCustomer(String orderid);

    List<Map<String,Object>> getOrderListByPersonId(String personid, Integer pagenumber, Integer pagesize);

    void addOrderAndOrderEntry(String logisticsOrder, String logisticsOrderentry) throws ParseException;

    Map<String,Object> addOrderByMobile(String btype, String emergency, String hctype, String startstock, String endstock, String customerid, String personid, String userid,String timerequest,String describ) throws ParseException;

    Map<String,Object> addOrderEntryByMobiile(String materielid, String materielname, String materielnum, String sequencenum, String batch, String quantity, String pnr, String sn, String stock, String location, String orderflag, String lorderno, String state, String msn, String materialStock);

    void receiveOrderBy2Code(String personId, String orderId);

    Map<String, Object> ListenOrderid(String orderid);

    Map<String, Object> checkIsOver(String orderid);

    void archiveOrdersOfSD(String... ids);

    Map<String,Object> isSDOrderOvertime(String orderid) throws ValidationException, ParseException;

    NutMap getOrderChartList(boolean exportExcel, String startTime, String endTime, String customerid, String overtime, int length, int start, int draw, List<DataTableOrder> order, List<DataTableColumn> columns, Cnd cnd) throws IOException;

    Map<String,Object> exportOrderChart(String startTime, String endTime, String customerid, String overtime) throws IOException;

    NutMap getOrderDataList(String personname, String otype, String selectForm, String pstatus, String completeValue, int length, int start, int draw, List<DataTableOrder> order, List<DataTableColumn> columns, Cnd cnd, String startTime, String endTime);

    String getOrderNum(String customerid, String intime) throws ParseException;

    void vDeleteOrders(String[] ids);

    void vDeleteOrder(String id);

    List<Map<String,Object>> getdvorderList(String[] pstatus, String deliveryorderid);

    NutMap getCheckOrders(String emergency, String endstockname, String ordernum, String personname, String otype, String startTime,String endTime,int length, int start, int draw, List<DataTableOrder> order, List<DataTableColumn> columns, Cnd cnd, String linkName);


    Map<String,Object> getUnreceivedCount(String personid);

    NutMap getLogisticStockList(boolean exportExcel, String startTime, String endTime, String customerid, int length, int start, int draw, List<DataTableOrder> orders, List<DataTableColumn> columns, Cnd cnd) throws IOException;;

    NutMap getLogisticChart( String startTime, String endTime, String customerid) throws IOException;
}


