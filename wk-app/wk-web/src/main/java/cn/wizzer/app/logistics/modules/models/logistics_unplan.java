package cn.wizzer.app.logistics.modules.models;

import cn.wizzer.app.base.modules.models.base_airport;
import cn.wizzer.app.sys.modules.models.Sys_dict;
import cn.wizzer.framework.base.model.BaseModel;
import org.nutz.dao.entity.annotation.*;
import cn.wizzer.app.base.modules.models.base_person;

import java.io.Serializable;

/**
 * Created by cw on 2017/10/14.
 */
@Table("logistics_unplan")
public class logistics_unplan extends BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;
    @Column
    @Name
    @Comment("ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @Prev(els = {@EL("uuid()")})
    private String id;

    @Column
    @Comment("所属机场")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String airportID;
    @One(field = "airportID")
    private base_airport airport;

    @Column
    @Comment("包装人")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String packer;
    @One(field = "packer")
    private base_person person;

    @Column
    @Comment("包装日期")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String packdate;

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
    private String materielnum;

    @Column
    @Comment("运输类型")
    @ColDefine(type = ColType.VARCHAR,width = 32)
    private String tansporttype;
    @One(field = "tansporttype")
    private Sys_dict transport;

    @Column
    @Comment("外包装类型")
    @ColDefine(type = ColType.VARCHAR,width = 32)
    private String packtype;
    @One(field = "packtype")
    private Sys_dict pack;

    @Column
    @Comment("计划ID")
    @ColDefine(type = ColType.VARCHAR,width = 32)
    private String planID;
    @One(field = "planID")
    private logistics_sendplan plan;

    @Column
    @Comment("备注")
    @ColDefine(type = ColType.VARCHAR,width = 255)
    private String note;

    @Column
    @Comment("图片名称")
    @ColDefine(type = ColType.VARCHAR, width = 1000)
    private String picname;

    @Column
    @Comment("原始图片名称")
    @ColDefine(type = ColType.VARCHAR, width = 1000)
    private String oldpicname;

    @Column
    @Comment("图片地址")
    @ColDefine(type = ColType.VARCHAR, width = 1000)
    private String picpath;


    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPacker() { return packer; }

    public void setPacker(String packer) { this.packer=packer; }

    public base_person getPerson() {
        return person;
    }

    public void setPerson(base_person person) {
        this.person = person;
    }

    public String getPackdate() {
        return packdate;
    }

    public void setPackdate(String packdate) {
        this.packdate = packdate;
    }

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

    public String getMaterielnum() {
        return materielnum;
    }

    public void setMaterielnum(String materielnum) {
        this.materielnum = materielnum;
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

    public String getPacktype() {
        return packtype;
    }

    public void setPacktype(String packtype) {
        this.packtype = packtype;
    }

    public Sys_dict getPack() {
        return pack;
    }

    public void setPack(Sys_dict pack) {
        this.pack = pack;
    }

    public String getPlanID() {
        return planID;
    }

    public void setPlanID(String planID) {
        this.planID = planID;
    }

    public logistics_sendplan getPlan(){return plan;}

    public void setPlan(logistics_sendplan plan){this.plan=plan;}

    public String getAirportID() { return airportID; }

    public void setAirportID(String airportID) { this.airportID=airportID; }

    public base_airport getAirport() { return airport; }

    public void setAirport(base_airport airport) { this.airport=airport; }

    public String getNote() { return note; }

    public void setNote(String note) { this.note=note; }

    public String getPicname(){return picname;}

    public void setPicname(String picname){this.picname=picname;}

    public String getOldpicname(){ return oldpicname;}

    public void setOldpicname(String oldpicname){this.oldpicname=oldpicname;}

    public String getPicpath(){return picpath;}

    public void setPicpath(String picpath){this.picpath=picpath;}
}
