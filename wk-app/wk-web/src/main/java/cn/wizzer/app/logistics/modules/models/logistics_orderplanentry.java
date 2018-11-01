package cn.wizzer.app.logistics.modules.models;

import cn.wizzer.framework.base.model.BaseModel;
import org.nutz.dao.entity.annotation.*;
import cn.wizzer.app.logistics.modules.models.logistics_order;
import cn.wizzer.app.logistics.modules.models.logistics_sendgoods;
import cn.wizzer.app.logistics.modules.models.logistics_Deliveryorder;
import cn.wizzer.app.base.modules.models.base_person;

import java.io.Serializable;

/**
 * Created by cw on 2017/10/29.
 * 提发货详细流程步骤
 */
@Table("logistics_orderplanentry")
public class logistics_orderplanentry extends BaseModel implements Serializable{
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
    @Comment("合同单ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String contractid;

    @One(field = "contractid")
    private  logistics_sendgoods sendgoods;

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
    @Comment("超时原因")
    @ColDefine(type = ColType.VARCHAR, width = 300)
    private String timeoutreason;

    @Column
    @Comment("操作时间")
    @ColDefine(type = ColType.VARCHAR, width = 300)
    private String operatetime;

    @Column
    @Comment("操作人")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String operater;

    @One(field = "operater")
    private base_person person;

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
    @Comment("状态0未进行，1 已进行")
    @ColDefine(type = ColType.INT)
    private Integer pstatus;



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

    public String getContractid(){return contractid;}

    public void setContractid(String contractid){this.contractid=contractid;}

    public logistics_sendgoods getSendgoods(){return sendgoods;}

    public void setSendgoods(logistics_sendgoods sendgoods){this.sendgoods=sendgoods;}

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

    public String getOperater(){return operater;}

    public void setOperater(String operater){this.operater=operater;}

    public base_person getPerson(){return person;}

    public void setPerson(base_person person){this.person=person;}
}
