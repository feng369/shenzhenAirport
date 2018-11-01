package cn.wizzer.app.logistics.modules.models;

import cn.wizzer.framework.base.model.BaseModel;
import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;

/**
 * Created by xl on 2017/6/19.
 */
@Comment("物料表")
@Table("logistics_orderentry")
public class logistics_orderentry extends BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;
    @Column
    @Name
    @Comment("ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @Prev(els = {@EL("uuid()")})
    private String id;

    @Column
    @Comment("物料ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String materielid;

    @Column
    @Comment("订单ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String orderid;

    @Column
    @Comment("MR号")
    @ColDefine(type = ColType.VARCHAR, width = 200)
    private String materielname;

    @Column
    @Comment("锁定PN")
    @ColDefine(type = ColType.VARCHAR, width = 200)
    private String materielnum;

    @Column
    @Comment("项次")
    @ColDefine(type = ColType.VARCHAR, width = 200)
    private String sequencenum;


    @Column
    @Comment("批号")
    @ColDefine(type = ColType.VARCHAR, width = 100)
    private String batch;

    @Column
    @Comment("数量")
    @ColDefine(type = ColType.VARCHAR,width = 100)
    private String quantity;

    @Column
    @Comment("出库架位")
    @ColDefine(type = ColType.VARCHAR,width = 200)
    private String outframe;

    @Column
    @Comment("入库架位")
    @ColDefine(type = ColType.VARCHAR,width = 200)
    private String inframe;

    @Column
    @Comment("配送状态")
    @ColDefine(type = ColType.INT)
    private String pstatus;

    @Column
    @Comment("库房号")
    @ColDefine(type = ColType.VARCHAR,width = 200)
    private String stock;

    @Column
    @Comment("架位")
    @ColDefine(type = ColType.VARCHAR,width = 200)
    private String location;

    @Column
    @Comment("预留件号")
    @ColDefine(type = ColType.VARCHAR,width = 200)
    private String pnr;

    @Column
    @Comment("预留序号")
    @ColDefine(type = ColType.VARCHAR,width = 200)
    private String sn;

    @Column
    @Comment("飞机号")
    @ColDefine(type = ColType.VARCHAR,width = 200)
    private String msn;
    @Column
    @Comment("单项物料领用状态")
    @ColDefine(type = ColType.VARCHAR, width = 20)
    private String orderstate;
    @Column
    @Comment("物料导入标识")
    @ColDefine(type = ColType.VARCHAR,width = 200)
    private String orderflag;

    @Column
    @Comment("指令号")
    @ColDefine(type = ColType.VARCHAR,width = 200)
    private String lorderno;
    @Column
    @Comment("物料所属仓位")
    @ColDefine(type = ColType.VARCHAR,width = 200)
    private String materialStock;

    public String getMaterialStock() {
        return materialStock;
    }

    public void setMaterialStock(String materialStock) {
        this.materialStock = materialStock;
    }

    public String getMsn() {
        return msn;
    }

    public void setMsn(String msn) {
        this.msn = msn;
    }
    public String getOrderstate() {
        return orderstate;
    }
    public void setOrderstate(String orderstate) {
        this.orderstate = orderstate;
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

    public String getMaterielid() {
        return materielid;
    }

    public void setMaterielid(String materielid) {
        this.materielid = materielid;
    }

    public String getInframe() {
        return inframe;
    }

    public void setInframe(String inframe) {
        this.inframe = inframe;
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public String getMaterielname() {
        return materielname;
    }

    public void setMaterielname(String materielname) {
        this.materielname = materielname;
    }

    public String getMaterielnum() {
        return materielnum;
    }

    public void setMaterielnum(String materielnum) {
        this.materielnum = materielnum;
    }

    public String getSequencenum() {
        return sequencenum;
    }

    public void setSequencenum(String sequencenum) {
        this.sequencenum = sequencenum;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getOutframe() {
        return outframe;
    }

    public void setOutframe(String outframe) {
        this.outframe = outframe;
    }

    public String getPstatus() {
        return pstatus;
    }

    public void setPstatus(String pstatus) {
        this.pstatus = pstatus;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPnr() {
        return pnr;
    }

    public void setPnr(String pnr) {
        this.pnr = pnr;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getOrderflag() {
        return orderflag;
    }

    public void setOrderflag(String orderflag) {
        this.orderflag = orderflag;
    }

    public String getLorderno() {
        return lorderno;
    }

    public void setLorderno(String lorderno) {
        this.lorderno = lorderno;
    }
}
