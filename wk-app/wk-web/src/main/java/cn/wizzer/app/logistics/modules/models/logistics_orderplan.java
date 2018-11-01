package cn.wizzer.app.logistics.modules.models;

import cn.wizzer.framework.base.model.BaseModel;
import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;

/**
 * Created by xl on 2017/6/28.
 * 合同与订单关联
 */
@Table("logistics_orderplan")
public class logistics_orderplan extends BaseModel implements Serializable {
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
    @Comment("合同ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String contractid;
    @One(field = "contractid")
    private logistics_sendgoods sendgoods;




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

    public String getContractid() {
        return contractid;
    }

    public void setContractid(String contractid) {
        this.contractid = contractid;
    }


    public logistics_order getOrder(){return order;}

    public void setOrder(logistics_order order){this.order=order;}

    public logistics_sendgoods getSendgoods(){return sendgoods;}

    public void setSendgoods(logistics_sendgoods sendgoods){this.sendgoods=sendgoods;}

}
