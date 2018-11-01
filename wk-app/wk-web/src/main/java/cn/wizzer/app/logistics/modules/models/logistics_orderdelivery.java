package cn.wizzer.app.logistics.modules.models;

import cn.wizzer.framework.base.model.BaseModel;
import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;
import java.util.List;

/**
 * Created by xl on 2017/6/28.
 * 配送单与订单关联
 */
@Table("logistics_orderdelivery")
public class logistics_orderdelivery extends BaseModel implements Serializable {
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



    @Column
    @Comment("配送单ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String deliveryorderid;

    @One(field = "id")
    private logistics_Deliveryorder logistics_deliveryorder;



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public String getDeliveryorderid() {
        return deliveryorderid;
    }

    public void setDeliveryorderid(String deliveryorderid) {
        this.deliveryorderid = deliveryorderid;
    }

    public logistics_Deliveryorder getLogistics_deliveryorder() {
        return logistics_deliveryorder;
    }

    public void setLogistics_deliveryorder(logistics_Deliveryorder logistics_deliveryorder) {
        this.logistics_deliveryorder = logistics_deliveryorder;
    }

}
