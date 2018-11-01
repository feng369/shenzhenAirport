package cn.wizzer.app.logistics.modules.models;

import cn.wizzer.app.base.modules.models.base_airport;
import cn.wizzer.app.base.modules.models.base_person;
import cn.wizzer.app.sys.modules.models.Sys_dict;
import cn.wizzer.framework.base.model.BaseModel;
import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;

/**
 * Created by cw on 2017/10/14.
 */
@Table("logistics_sendplan")
public class logistics_sendplan extends BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;
    @Column
    @Name
    @Comment("ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @Prev(els = {@EL("uuid()")})
    private String id;

    @Column
    @Comment("订单ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String orderid;
    @One(field = "orderid")
    private logistics_order order;

    @Column
    @Comment("状态（计划中-审批中-已计划-发货中-已交货-已完成）")
    @ColDefine(type = ColType.INT)
    private int pstatus;

    @Column
    @Comment("收货方")
    @ColDefine(type = ColType.VARCHAR, width = 200)
    private String receivename;

    @Column
    @Comment("收货地址")
    @ColDefine(type = ColType.VARCHAR, width = 200)
    private String receiveaddress;

    @Column
    @Comment("件数")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String packnum;

    @Column
    @Comment("运输类型")
    @ColDefine(type = ColType.VARCHAR,width = 32)
    private String tansporttype;
    @One(field = "tansporttype")
    private Sys_dict transport;

    @Column
    @Comment("费用类型")
    @ColDefine(type = ColType.VARCHAR,width = 32)
    private String costtype;
    @One(field = "costtype")
    private Sys_dict cost;

    @Column
    @Comment("临时价格")
    @ColDefine(type = ColType.FLOAT)
    private float tempprice;

    @Column
    @Comment("协议号")
    @ColDefine(type = ColType.VARCHAR,width = 100)
    private String protocolnum;

    @Column
    @Comment("免单号")
    @ColDefine(type = ColType.VARCHAR,width = 100)
    private String freenum;

    @Column
    @Comment("运单号")
    @ColDefine(type = ColType.VARCHAR,width = 100)
    private String deliverynum;

    @Column
    @Comment("重量")
    @ColDefine(type = ColType.VARCHAR,width = 32)
    private String weight;

    @Column
    @Comment("航班号")
    @ColDefine(type = ColType.VARCHAR,width = 32)
    private String flightnum;

    @Column
    @Comment("第三方名称")
    @ColDefine(type = ColType.VARCHAR,width = 50)
    private String thirdname;

    @Column
    @Comment("取货司机")
    @ColDefine(type = ColType.VARCHAR,width = 32)
    private String drivername;

    @Column
    @Comment("车牌号")
    @ColDefine(type = ColType.VARCHAR,width = 32)
    private String carnum;

    @Column
    @Comment("联系电话")
    @ColDefine(type = ColType.VARCHAR,width = 32)
    private String tel;

    @Column
    @Comment("配送员")
    @ColDefine(type = ColType.VARCHAR,width = 32)
    private String sender;
    @One(field = "sender")
    private base_person send;

    @Column
    @Comment("配送日期")
    @ColDefine(type = ColType.VARCHAR,width = 32)
    private String senddate;

    @Column
    @Comment("创建人")
    @ColDefine(type = ColType.VARCHAR,width = 32)
    private String personid;
    @One(field = "personid")
    private base_person person;





    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getPstatus() { return pstatus; }

    public void setPstatus(int pstatus) { this.pstatus=pstatus; }

    public String getReceivename() {
        return receivename;
    }

    public void setReceivename(String receivename) {
        this.receivename = receivename;
    }

    public String getReceiveaddress() {
        return receiveaddress;
    }

    public void setReceiveaddress(String receiveaddress) {
        this.receiveaddress = receiveaddress;
    }

    public String getPacknum() {
        return packnum;
    }

    public void setPacknum(String packnum) {
        this.packnum = packnum;
    }

    public String getTansporttype() {
        return tansporttype;
    }

    public void setTansporttype(String tansporttype) {
        this.tansporttype = tansporttype;
    }

    public Sys_dict getTransport() {
        return transport;
    }

    public void setTransport(Sys_dict transport) {
        this.transport = transport;
    }

    public String getCosttype() {
        return costtype;
    }

    public void setCosttype(String costtype) {
        this.costtype = costtype;
    }

    public Sys_dict getCost() {
        return cost;
    }

    public void setCost(Sys_dict cost) {
        this.cost = cost;
    }

    public float getTempprice() {
        return tempprice;
    }

    public void setTempprice(float tempprice) {
        this.tempprice = tempprice;
    }

    public String getProtocolnum() {
        return protocolnum;
    }

    public void setProtocolnum(String protocolnum) {
        this.protocolnum = protocolnum;
    }

    public String getFreenum() {
        return freenum;
    }

    public void setFreenum(String freenum) {
        this.freenum = freenum;
    }

    public String getDeliverynum() {
        return deliverynum;
    }

    public void setDeliverynum(String deliverynum) {
        this.deliverynum = deliverynum;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getFlightnum() {
        return flightnum;
    }

    public void setFlightnum(String flightnum) {
        this.flightnum = flightnum;
    }

    public String getThirdname() {
        return thirdname;
    }

    public void setThirdname(String thirdname) {
        this.thirdname = thirdname;
    }

    public String getDrivername() {
        return drivername;
    }

    public void setDrivername(String drivername) {
        this.drivername = drivername;
    }

    public String getCarnum() {
        return carnum;
    }

    public void setCarnum(String carnum) {
        this.carnum = carnum;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getOrderid() { return orderid; }

    public void setOrderid(String orderid) { this.orderid=orderid; }

    public logistics_order getOrder() { return order; }

    public void setOrder(logistics_order order) { this.order=order; }

    public String getSender(){return sender;}

    public void setSender(String sender){this.sender=sender;}

    public base_person getSend(){return send;}

    public void setSend(base_person send){this.send=send;}

    public String getSenddate(){return senddate;}

    public void setSenddate(String senddate){this.senddate=senddate;}

    public String getPersonid(){return personid;}

    public void setPersonid(String personid){this.personid=personid;}

    public base_person getPerson(){return person;}

    public void setPerson(base_person person){this.person=person;}
}
