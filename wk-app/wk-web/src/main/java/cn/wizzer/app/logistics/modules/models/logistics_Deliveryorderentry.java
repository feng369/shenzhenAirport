package cn.wizzer.app.logistics.modules.models;

import cn.wizzer.app.base.modules.models.base_person;
import cn.wizzer.app.sys.modules.models.Sys_unit;
import cn.wizzer.framework.base.model.BaseModel;
import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;

/**
 * Created by xl on 2017/6/27.
 * 订单详细流程步骤
 */
@Table("logistics_Deliveryorderentry")
public class logistics_Deliveryorderentry extends BaseModel implements Serializable{
    private static final long serialVersionUID = 1L;
    @Column
    @Name
    @Comment("ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @Prev(els = {@EL("uuid()")})
    private String id;


    @Column
    @Comment("配送单ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String deliveryorderid;

    @One(field = "deliveryorderid")
    private logistics_Deliveryorder logisticsDeliveryorder;

    @Column
    @Comment("订单ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String orderid;

    @One(field = "orderid")
    private  logistics_order logisticsOrder;

    @Column
    @Comment("内容")
    @ColDefine(type = ColType.VARCHAR, width = 300)
    private String content;

    @Column
    @Comment("步骤")
    @ColDefine(type = ColType.INT)
    private Integer step;

    @Column
    @Comment("步骤名称")
    @ColDefine(type = ColType.VARCHAR, width = 100)
    private String stepname;
    @Column
    @Comment("操作时间")
    @ColDefine(type = ColType.VARCHAR, width = 300)
    private String operatetime;

    @Column
    @Comment("图片名称")
    @ColDefine(type = ColType.VARCHAR, width = 300)
    private String picname;

    @Column
    @Comment("原始图片名称")
    @ColDefine(type = ColType.VARCHAR, width = 300)
    private String oldpicname;

    @Column
    @Comment("图片地址")
    @ColDefine(type = ColType.VARCHAR, width = 300)
    private String picpath;

    @Column
    @Comment("状态")
    @ColDefine(type = ColType.INT)
    private Integer pstatus;

    @Column
    @Comment("步骤负责人,例:订单完成对应收货人")
    @ColDefine(type = ColType.VARCHAR, width = 40)
    private String setpPersonId;

    @One(field = "setpPersonId")
    private base_person stepPerson;

    @Column
    @Comment("超时原因")
    @ColDefine(type = ColType.VARCHAR, width = 300)
    private String timeoutreason;

    @Column
    @Comment("是否超时 0未超时 1超时")
    @ColDefine(type = ColType.INT,width = 10)
    private int overtime;

    public int getOvertime() {
        return overtime;
    }

    public void setOvertime(int overtime) {
        this.overtime = overtime;
    }

    public base_person getStepPerson() {
        return stepPerson;
    }

    public void setStepPerson(base_person stepPerson) {
        this.stepPerson = stepPerson;
    }

    public String getSetpPersonId() {
        return setpPersonId;
    }

    public void setSetpPersonId(String setpPersonId) {
        this.setpPersonId = setpPersonId;
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

    public String getDeliveryorderid() {
        return deliveryorderid;
    }

    public void setDeliveryorderid(String deliveryorderid) {
        this.deliveryorderid = deliveryorderid;
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getStep() {
        return step;
    }

    public void setStep(Integer step) {
        this.step = step;
    }

    public String getStepname() {
        return stepname;
    }

    public void setStepname(String stepname) {
        this.stepname = stepname;
    }

    public Integer getPstatus() {
        return pstatus;
    }

    public void setPstatus(Integer pstatus) {
        this.pstatus = pstatus;
    }

    public String getTimeoutreason() {
        return timeoutreason;
    }

    public void setTimeoutreason(String timeoutreason) {
        this.timeoutreason = timeoutreason;
    }

    public String getOperatetime() {
        return operatetime;
    }

    public void setOperatetime(String operatetime) {
        this.operatetime = operatetime;
    }

    public String getPicpath() {
        return picpath;
    }

    public void setPicpath(String picpath) {
        this.picpath = picpath;
    }

    public String getPicname(){ return picname;}

    public void setPicname(String picname){ this.picname=picname;}

    public String getOldpicname(){ return oldpicname;}

    public void setOldpicname(String oldpicname){ this.oldpicname=oldpicname;}

    public logistics_Deliveryorder getLogisticsDeliveryorder() {
        return logisticsDeliveryorder;
    }

    public void setLogisticsDeliveryorder(logistics_Deliveryorder logisticsDeliveryorder) {
        this.logisticsDeliveryorder = logisticsDeliveryorder;
    }

    public logistics_order getLogisticsOrder() {
        return logisticsOrder;
    }

    public void setLogisticsOrder(logistics_order logisticsOrder) {
        this.logisticsOrder = logisticsOrder;
    }
}
