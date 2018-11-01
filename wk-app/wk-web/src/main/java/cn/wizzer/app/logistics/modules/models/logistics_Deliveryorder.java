package cn.wizzer.app.logistics.modules.models;

import cn.wizzer.framework.base.model.BaseModel;
import cn.wizzer.app.base.modules.models.base_person;
import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;
import java.util.List;

/**
 * Created by xl on 2017/6/27.
 * 配送单
 */
@Table("logistics_Deliveryorder")
public class logistics_Deliveryorder extends BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;
    @Column
    @Name
    @Comment("ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @Prev(els = {@EL("uuid()")})
    private String id;

    @Column
    @Comment("配送单编号")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String deliveryordernum;

    @Column
    @Comment("配送员ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String personid;

    @One(field = "personid")
    private base_person person;

    @Column
    @Comment("创建日期")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String datetime;

    @Column
    @Comment("完成时间")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String cptime;

    @Column
    @Comment("状态")
    @ColDefine(type = ColType.INT)
    private Integer pstatus;

    @Column
    @Comment("是否提发货")
    @ColDefine(type = ColType.VARCHAR,width=2)
    private String isTF;

    @Many(field = "deliveryorderid")
    private List<logistics_Deliveryorderentry> logistics_deliveryorderentry;

    @Many(field = "deliveryorderid")
    private List<logistics_orderdelivery> logistics_orderdelivery;

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

    public String getDeliveryordernum() {
        return deliveryordernum;
    }

    public void setDeliveryordernum(String deliveryordernum) {
        this.deliveryordernum = deliveryordernum;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public Integer getPstatus() {
        return pstatus;
    }

    public void setPstatus(Integer pstatus) {
        this.pstatus = pstatus;
    }

    public String getCptime() {
        return cptime;
    }

    public void setCptime(String cptime) {
        this.cptime = cptime;
    }

    public List<logistics_Deliveryorderentry> getLogistics_deliveryorderentry() {
        return logistics_deliveryorderentry;
    }

    public void setLogistics_deliveryorderentry(List<logistics_Deliveryorderentry> logistics_deliveryorderentry) {
        this.logistics_deliveryorderentry = logistics_deliveryorderentry;
    }

    public List<cn.wizzer.app.logistics.modules.models.logistics_orderdelivery> getLogistics_orderdelivery() {
        return logistics_orderdelivery;
    }

    public void setLogistics_orderdelivery(List<cn.wizzer.app.logistics.modules.models.logistics_orderdelivery> logistics_orderdelivery) {
        this.logistics_orderdelivery = logistics_orderdelivery;
    }

    public base_person getPerson() {
        return person;
    }

    public void setPerson(base_person person) {
        this.person = person;
    }

    public String getIsTF(){ return isTF;}

    public void setIsTF(String isTF){this.isTF=isTF;}
}
