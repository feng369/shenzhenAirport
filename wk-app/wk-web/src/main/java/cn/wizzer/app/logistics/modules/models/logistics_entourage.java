package cn.wizzer.app.logistics.modules.models;

import cn.wizzer.framework.base.model.BaseModel;
import org.nutz.dao.entity.annotation.*;
import cn.wizzer.app.base.modules.models.base_person;

import java.io.Serializable;

/**
 * Created by cw on 2017/8/23.
 */
@Table("logistics_entourage")
public class logistics_entourage extends BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;
    @Column
    @Name
    @Comment("ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @Prev(els = {@EL("uuid()")})
    private String id;

    @Column
    @Comment("随行人员id")
    @ColDefine(type = ColType.VARCHAR,width = 32)
    private String personid;

    @Column
    @Comment("订单号")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String orderid;

    @One(field = "personid")
    private base_person person;

    @One(field="orderid")
    private logistics_order order;



    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public base_person getPerson(){ return person;}

    public void setPerson(base_person person){ this.person = person; }

    public logistics_order getOrder(){ return order; }

    public void setOrder(logistics_order order){ this.order=order; }

}
