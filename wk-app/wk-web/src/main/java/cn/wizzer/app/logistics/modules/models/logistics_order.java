package cn.wizzer.app.logistics.modules.models;

import cn.wizzer.app.base.modules.models.base_customer;
import cn.wizzer.app.base.modules.models.base_person;
import cn.wizzer.app.base.modules.models.base_place;
import cn.wizzer.app.sys.modules.models.Sys_dict;
import cn.wizzer.framework.base.model.BaseModel;
import org.nutz.dao.entity.annotation.*;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xl on 2017/6/19.
 */
@Table("logistics_order")
public class logistics_order extends BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;
    @Column
    @Name
    @Comment("ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @Prev(els = {@EL("uuid()")})
    private String id;

    @Column
    @Comment("订单编号")
    @ColDefine(type = ColType.VARCHAR, width = 120)
    private String ordernum;

    @Column
    @Comment("机场id")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String airportid;

    @Column
    @Comment("客户id")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String customerid;


    @One(field = "customerid")
    private base_customer customer;

    @Column
    @Comment("类型")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String otype;

    @One(field = "otype")
    private Sys_dict dict_otype;

    @Column
    @Comment("业务类型")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String btype;

    @One(field = "btype")
    private Sys_dict dict_btype;

    @Column
    @Comment("紧急程度")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String emergency;

    @One(field = "emergency")
    private Sys_dict dict_emergency;

    @Column
    @Comment("是否打包")
    @ColDefine(type = ColType.BOOLEAN)
    private boolean isPackage;


    @Column
    @Comment("出发地")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String startstock;

    @One(field = "startstock")
    private base_place place_startstock;

    @Column
    @Comment("目的地")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String endstock;

    @One(field = "endstock")
    private base_place place_endstock;

    @Column
    @Comment("出发地联系电话")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String startstockphone;


    @Column
    @Comment("目的地联系电话")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String endstockphone;


    @Column
    @Comment("运输类型")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String transporttype;

    @One(field = "transporttype")
    private Sys_dict dict_transporttype;

    @Column
    @Comment("航材类型")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String hctype;

    @One(field = "hctype")
    private Sys_dict dict_hctype;

    @Column
    @Comment("航班/运单号")
    @ColDefine(type = ColType.VARCHAR, width = 100)
    private String airwaybillno;

    @Column
    @Comment("合同号/名称")
    @ColDefine(type = ColType.VARCHAR, width = 100)
    private String contractnum;

    @Column
    @Comment("状态")
    @ColDefine(type = ColType.INT)
    private int pstatus;

    @Column
    @Comment("时限要求")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String timerequest;

    @Column
    @Comment("航班时间")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String hbtime;

    @Column
    @Comment("预计送达时间")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String estimatetime;

    @Column
    @Comment("备注")
    @ColDefine(type = ColType.VARCHAR, width = 500)
    private String note;

    @Column
    @Comment("登记人")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String personid;

    @One(field = "personid")
    private base_person person;

    @Column
    @Comment("登记时间")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String intime;

    @Column
    @Comment("订单完成时间")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String cptime;


    @Many(field = "orderid")
    private List<logistics_orderentry> logistics_orderentry = new ArrayList<>();

    @Column
    @Comment("件数")
    @ColDefine(type = ColType.VARCHAR, width = 100)
    private String packagesnum;

    @Column
    @Comment("重量")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String weight;

    @Column
    @Comment("仓位")
    @ColDefine(type = ColType.VARCHAR, width = 100)
    private String stockspace;

    @Column
    @Comment("包装方式")
    @ColDefine(type = ColType.VARCHAR, width = 100)
    private String packagetype;

    @Column
    @Comment("是否有AOG标签")
    @ColDefine(type = ColType.BOOLEAN, width = 100)
    private boolean isAOG;

    @Column
    @Comment("是否有特殊标志")
    @ColDefine(type = ColType.BOOLEAN, width = 100)
    private boolean isSpecial;

    @Column
    @Comment("机号")
    @ColDefine(type = ColType.VARCHAR, width = 100)
    private String devicenum;

    @Column
    @Comment("配送单关联")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String deliveryorderid;

    @Column
    @Comment("车辆")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String vehicleid;

    @One(field = "deliveryorderid")
    private logistics_Deliveryorder logisticsDeliveryorder;

    @Column
    @Comment("是否提发货")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String isTF;

    @Column
    @Comment("库存地")
    @ColDefine(type = ColType.VARCHAR, width = 200)
    private String warehousename;

    @Column
    @Comment("厂家")
    @ColDefine(type = ColType.VARCHAR, width = 200)
    private String receivename;

    @Column
    @Comment("收货人")
    @ColDefine(type = ColType.VARCHAR, width = 40)
    private String receivePersonId;

    @One(field = "receivePersonId")
    private base_person receivePerson;

    @Column
    @Comment("发货补充信息ID")
    @ColDefine(type = ColType.VARCHAR, width =32 )
    private String sendplanid;
    @One(field = "sendplanid")
    private logistics_sendplan sendplan;

    public String getReceivePersonId() {

        return receivePersonId;
    }

    public void setReceivePersonId(String receivePersonId) {
        this.receivePersonId = receivePersonId;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrdernum() {
        return ordernum;
    }

    public void setOrdernum(String ordernum) {
        this.ordernum = ordernum;
    }

    public String getCustomerid() {
        return customerid;
    }

    public void setCustomerid(String customerid) {
        this.customerid = customerid;
    }

    public String getOtype() {
        return otype;
    }

    public void setOtype(String otype) {
        this.otype = otype;
    }

    public String getHbtime() {
        return hbtime;
    }

    public void setHbtime(String hbtime) {
        this.hbtime = hbtime;
    }

    public String getBtype() {
        return btype;
    }

    public void setBtype(String btype) {
        this.btype = btype;
    }

    public String getEmergency() {
        return emergency;
    }

    public void setEmergency(String emergency) {
        this.emergency = emergency;
    }

    public boolean isPackage() {
        return isPackage;
    }

    public void setPackage(boolean aPackage) {
        isPackage = aPackage;
    }

    public String getCptime() {
        return cptime;
    }

    public void setCptime(String cptime) {
        this.cptime = cptime;
    }

    public String getStartstock() {
        return startstock;
    }

    public void setStartstock(String startstock) {
        this.startstock = startstock;
    }

    public String getEndstock() {
        return endstock;
    }

    public void setEndstock(String endstock) {
        this.endstock = endstock;
    }

    public String getStartstockphone() {
        return startstockphone;
    }

    public void setStartstockphone(String startstockphone) {
        this.startstockphone = startstockphone;
    }

    public String getEndstockphone() {
        return endstockphone;
    }

    public void setEndstockphone(String endstockphone) {
        this.endstockphone = endstockphone;
    }

    public String getTransporttype() {
        return transporttype;
    }

    public void setTransporttype(String transporttype) {
        this.transporttype = transporttype;
    }

    public String getAirwaybillno() {
        return airwaybillno;
    }

    public void setAirwaybillno(String airwaybillno) {
        this.airwaybillno = airwaybillno;
    }

    public String getContractnum() {
        return contractnum;
    }

    public void setContractnum(String contractnum) {
        this.contractnum = contractnum;
    }

    public int getPstatus() {
        return pstatus;
    }

    public void setPstatus(int pstatus) {
        this.pstatus = pstatus;
    }

    public String getTimerequest() {
        return timerequest;
    }

    public void setTimerequest(String timerequest) {
        this.timerequest = timerequest;
    }

    public String getEstimatetime() {
        return estimatetime;
    }

    public void setEstimatetime(String estimatetime) {
        this.estimatetime = estimatetime;
    }

    public String getIntime() {
        return intime;
    }

    public void setIntime(String intime) {
        this.intime = intime;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getPersonid() {
        return personid;
    }

    public void setPersonid(String personid) {
        this.personid = personid;
    }

    public base_person getReceivePerson() {
        return receivePerson;
    }

    public void setReceivePerson(base_person receivePerson) {
        this.receivePerson = receivePerson;
    }

    public base_person getPerson() {
        return person;
    }

    public void setPerson(base_person person) {
        this.person = person;
    }

    public List<cn.wizzer.app.logistics.modules.models.logistics_orderentry> getLogistics_orderentry() {
        return logistics_orderentry;
    }

    public void setLogistics_orderentry(List<cn.wizzer.app.logistics.modules.models.logistics_orderentry> logistics_orderentry) {
        this.logistics_orderentry = logistics_orderentry;
    }

    public Sys_dict getDict_transporttype() {
        return dict_transporttype;
    }

    public void setDict_transporttype(Sys_dict dict_transporttype) {
        this.dict_transporttype = dict_transporttype;
    }

    public String getPackagesnum() {
        return packagesnum;
    }

    public void setPackagesnum(String packagesnum) {
        this.packagesnum = packagesnum;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getStockspace() {
        return stockspace;
    }

    public void setStockspace(String stockspace) {
        this.stockspace = stockspace;
    }

    public String getPackagetype() {
        return packagetype;
    }

    public void setPackagetype(String packagetype) {
        this.packagetype = packagetype;
    }

    public boolean isAOG() {
        return isAOG;
    }

    public void setAOG(boolean AOG) {
        isAOG = AOG;
    }

    public boolean isSpecial() {
        return isSpecial;
    }

    public void setSpecial(boolean special) {
        isSpecial = special;
    }

    public String getDevicenum() {
        return devicenum;
    }

    public void setDevicenum(String devicenum) {
        this.devicenum = devicenum;
    }

    public base_customer getCustomer() {
        return customer;
    }

    public void setCustomer(base_customer customer) {
        this.customer = customer;
    }

    public Sys_dict getDict_btype() {
        return dict_btype;
    }

    public void setDict_btype(Sys_dict dict_btype) {
        this.dict_btype = dict_btype;
    }

    public Sys_dict getDict_emergency() {
        return dict_emergency;
    }

    public void setDict_emergency(Sys_dict dict_emergency) {
        this.dict_emergency = dict_emergency;
    }

    public base_place getPlace_startstock() {
        return place_startstock;
    }

    public void setPlace_startstock(base_place place_startstock) {
        this.place_startstock = place_startstock;
    }

    public base_place getPlace_endstock() {
        return place_endstock;
    }

    public void setPlace_endstock(base_place place_endstock) {
        this.place_endstock = place_endstock;
    }

    public String getDeliveryorderid() {
        return deliveryorderid;
    }

    public void setDeliveryorderid(String deliveryorderid) {
        this.deliveryorderid = deliveryorderid;
    }

    public logistics_Deliveryorder getLogisticsDeliveryorder() {
        return logisticsDeliveryorder;
    }

    public void setLogisticsDeliveryorder(logistics_Deliveryorder logisticsDeliveryorder) {
        this.logisticsDeliveryorder = logisticsDeliveryorder;
    }

    public String getAirportid() {
        return airportid;
    }

    public void setAirportid(String airportid) {
        this.airportid = airportid;
    }

    public String getVehicleid(){ return vehicleid;}

    public void setVehicleid(String vehicleid){this.vehicleid=vehicleid;}

    public Sys_dict getDict_otype() {
        return dict_otype;
    }

    public void setDict_otype(Sys_dict dict_otype) {
        this.dict_otype = dict_otype;
    }

    public String getHctype() {
        return hctype;
    }

    public void setHctype(String hctype) {
        this.hctype = hctype;
    }

    public Sys_dict getDict_hctype() {
        return dict_hctype;
    }

    public void setDict_hctype(Sys_dict dict_hctype) {
        this.dict_hctype = dict_hctype;
    }

    public String getIsTF(){return isTF;}

    public void setIsTF(String isTF){this.isTF=isTF;}

    public String getWarehousename(){return warehousename;}

    public void setWarehousename(String warehousename){this.warehousename=warehousename;}

    public String getReceivename(){return receivename;}

    public void  setReceivename(String receivename){this.receivename=receivename;}

    public String getSendplanid(){return  sendplanid;}

    public void setSendplanid(String sendplanid){this.sendplanid=sendplanid;}

    public logistics_sendplan getSendplan(){return sendplan;}

    public void setSendplan(logistics_sendplan sendplan){this.sendplan=sendplan;}
    //20180518zhf1142
    public String getPstatusname(){
        switch (pstatus){
            case 0:return "保存";
            case 1:return "订单提交，等待派单";
            case 2:return "已派单，待配送员接单";
            case 3:return "配送员已接单";
            case 4:return "仓库备料中";
            case 5:return "仓库备料完成";
            case 6:return "配送中";
            case 7:return "送达";
          default:return "订单完成";
        }
    }
    
    public static void main(String[] args){
        String xx = "璁㈠崟瀹屾垚";
        System.out.println();
    }
}
