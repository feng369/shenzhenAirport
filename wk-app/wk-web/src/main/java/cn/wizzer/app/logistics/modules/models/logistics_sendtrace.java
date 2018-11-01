package cn.wizzer.app.logistics.modules.models;

import cn.wizzer.framework.base.model.BaseModel;
import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;
@Table("logistics_sendtrace")
public class logistics_sendtrace extends BaseModel implements Serializable {

    /*
    id
deliveryorderid
personid
position
intime
orderpstatus
orderid
*/
    @Column
    @Name
    @Comment("ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @Prev(els = {@EL("uuid()")})
    private String id;




    @Column
    @Comment("状态")
    @ColDefine(type = ColType.INT)
    private int orderpstatus;

    @Column
    @Comment("地理坐标")
    @ColDefine(type = ColType.VARCHAR, width = 100)
    private String position;

    @Column
    @Comment("插入表时时间")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String intime;

    @Comment("运单id")
    @Column
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String deliveryorderid;
    @One(field = "deliveryorderid")
    private logistics_Deliveryorder logisticsDeliveryorder;


    @Comment("订单id")
    @Column
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String orderid;

    @Comment("配送人员id")
    @Column
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String personid;

    public String getPersonid() {
        return personid;
    }

    public void setPersonid(String personid) {
        this.personid = personid;
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getOrderpstatus() {
        return orderpstatus;
    }

    public void setOrderpstatus(int orderpstatus) {
        this.orderpstatus = orderpstatus;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getIntime() {
        return intime;
    }

    public void setIntime(String intime) {
        this.intime = intime;
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

    //20180518zhf1142
    public String getPstatusname(){
        switch (orderpstatus){
            case 0:return "保存";
            case 1:return "订单提交，等待派单";
            case 2:return "已派单，待配送员接单";
            case 3:return "配送员已接单";
//            case 4:return "仓库备料中";
//            case 5:return "仓库备料完成";
            case 6:return "核料";
            case 7:return "送达";
          default:return "订单完成";
        }
    }
}
