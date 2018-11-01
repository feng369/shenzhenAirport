package cn.wizzer.app.logistics.modules.models;

import cn.wizzer.app.base.modules.models.base_airport;
import cn.wizzer.app.base.modules.models.base_person;
import cn.wizzer.app.base.modules.models.base_warehouse;
import cn.wizzer.app.sys.modules.models.Sys_dict;
import cn.wizzer.framework.base.model.BaseModel;
import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;

/**
 * Created by cw on 2017/10/26.
 */
@Table("logistics_pickgoods")
public class logistics_pickgoods extends BaseModel implements Serializable {
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
    @Comment("单据编号")
    @ColDefine(type = ColType.VARCHAR, width = 100)
    private String picknum;

    @Column
    @Comment("合同编号")
    @ColDefine(type = ColType.VARCHAR,width=100)
    private String contractcode;

    @Column
    @Comment("件号")
    @ColDefine(type = ColType.VARCHAR, width = 100)
    private String partnum;

    @Column
    @Comment("序号")
    @ColDefine(type = ColType.VARCHAR,width = 100)
    private String serialnum;

    @Column
    @Comment("数量")
    @ColDefine(type = ColType.FLOAT)
    private float number;

    @Column
    @Comment("架位")
    @ColDefine(type = ColType.VARCHAR,width = 32)
    private String shelf;

    @Column
    @Comment("批次号")
    @ColDefine(type = ColType.VARCHAR,width = 100)
    private String batchnum;

    @Column
    @Comment("发料类型")
    @ColDefine(type = ColType.VARCHAR,width = 32)
    private String issuetype;
    @One(field = "issuetype")
    private Sys_dict issue;

    @Column
    @Comment("运输类型")
    @ColDefine(type = ColType.VARCHAR,width = 32)
    private String tansporttype;
    @One(field = "tansporttype")
    private Sys_dict transport;

    @Column
    @Comment("毛重")
    @ColDefine(type = ColType.VARCHAR,width = 32)
    private String rough;

    @Column
    @Comment("库存地")
    @ColDefine(type = ColType.VARCHAR,width = 32)
    private String warehouseID;
    @One(field = "warehouseID")
    private base_warehouse warehouse;

    @Column
    @Comment("优先级")
    @ColDefine(type = ColType.VARCHAR,width = 32)
    private String prioritylv;
    @One(field = "prioritylv")
    private Sys_dict priority;

    @Column
    @Comment("状态(1创建需求-2发料完成-3核料-4包装-5计划-6审批-7发货中-8交货-9完成)")
    @ColDefine(type = ColType.INT)
    private int pstatus;

    @Column
    @Comment("备注")
    @ColDefine(type = ColType.VARCHAR,width = 500)
    private String note;


    @Column
    @Comment("收货方")
    @ColDefine(type = ColType.VARCHAR,width = 500)
    private String receivename;

    @Column
    @Comment("收货地址")
    @ColDefine(type = ColType.VARCHAR,width = 500)
    private String receiveaddress;

    @Column
    @Comment("联系电话")
    @ColDefine(type = ColType.VARCHAR,width = 32)
    private String tel;

    @Column
    @Comment("发货日期")
    @ColDefine(type = ColType.VARCHAR,width = 32)
    private String senddate;

    @Column
    @Comment("发货人")
    @ColDefine(type = ColType.VARCHAR,width = 32)
    private String sender;
    @One(field = "sender")
    private base_person person;

    @Column
    @Comment("运单号")
    @ColDefine(type = ColType.VARCHAR,width = 100)
    private String billnum;

    @Column
    @Comment("包装ID")
    @ColDefine(type = ColType.VARCHAR,width = 32)
    private String packID;
    @One(field = "packID")
    private logistics_unplan pack;

    @Column
    @Comment("计划ID")
    @ColDefine(type = ColType.VARCHAR,width = 32)
    private String planID;
    @One(field = "planID")
    private logistics_sendplan plan;

    @Column
    @Comment("登记人")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String personid;

    @One(field = "personid")
    private base_person checker;

    @Column
    @Comment("登记时间")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String intime;

    @Column
    @Comment("订单完成时间")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String cptime;

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

    @Column
    @Comment("核料人")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String hlpersonid;
    @One(field = "hlpersonid")
    private base_person hlperson;

    @Column
    @Comment("核料时间")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String hltime;

    @Column
    @Comment("核料备注")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    private String hlnote;

    @Column
    @Comment("图片名称")
    @ColDefine(type = ColType.VARCHAR, width = 1000)
    private String bpicname;

    @Column
    @Comment("原始图片名称")
    @ColDefine(type = ColType.VARCHAR, width = 1000)
    private String boldpicname;

    @Column
    @Comment("图片地址")
    @ColDefine(type = ColType.VARCHAR, width = 1000)
    private String bpicpath;

    @Column
    @Comment("包装人")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String bzpersonid;
    @One(field = "bzpersonid")
    private base_person bzperson;

    @Column
    @Comment("包装时间")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String bztime;

    @Column
    @Comment("包装备注")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    private String bznote;

    @Column
    @Comment("是否禁航")
    @ColDefine(type = ColType.BOOLEAN, width = 100)
    private boolean isAir;



    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPicknum() { return picknum; }

    public void setPicknum(String picknum){ this.picknum=picknum;}

    public String getContractcode(){ return contractcode;}

    public void setContractcode(String contractcode){this.contractcode=contractcode;}

    public String getPartnum(){ return partnum;}

    public void setPartnum(String partnum){ this.partnum=partnum;}

    public String getShelf(){ return shelf;}

    public void setShelf(String partnum){ this.shelf=shelf;}

    public String getSerialnum(){return serialnum;}

    public void setSerialnum(String serialnum){this.serialnum=serialnum;}

    public float getNumber(){return number;}

    public void setNumber(float number){this.number=number;}

    public String getBatchnum(){return batchnum;}

    public void setBatchnum(String batchnum){this.batchnum=batchnum;}

    public String getIssuetype(){return  issuetype;}

    public void setIssuetype(String issuetype){this.issuetype=issuetype;}

    public Sys_dict getIssue(){return issue;}

    public void setIssue(Sys_dict issue){this.issue=issue;}

    public String getTansporttype(){return tansporttype;}

    public void setTansporttype(String transporttype){this.tansporttype=tansporttype;}

    public Sys_dict getTransport(){return transport;}

    public void setTransport(Sys_dict transport){this.transport=transport;}

    public String getRough(){return rough;}

    public void setRough(String rough){this.rough=rough;}

    public String getWarehouseID(){return warehouseID;}

    public void setWarehouseID(String warehouseID){this.warehouseID=warehouseID;}

    public base_warehouse getWarehouse(){return warehouse;}

    public void setWarehouse(base_warehouse warehouse){this.warehouse=warehouse;}

    public String getPrioritylv(){return prioritylv;}

    public void setPrioritylv(String prioritylv){this.prioritylv=prioritylv;}

    public Sys_dict getPriority(){return priority;}

    public void setPriority(Sys_dict priority){this.priority=priority;}

    public int getPstatus(){return pstatus;}

    public void setPstatus(int pstatus){this.pstatus=pstatus;}

    public String getNote(){return note;}

    public void setNote(String note){this.note=note;}

    public String getReceivename(){return receivename;}

    public void setReceivename(String receivename){this.receivename=receivename;}

    public String getReceiveaddress(){return receiveaddress;}

    public void setReceiveaddress(String receiveaddress){this.receiveaddress=receiveaddress;}

    public String getTel(){return tel;}

    public void setTel(String tel){this.tel=tel;}

    public String getSenddate(){return senddate;}

    public void setSenddate(String senddate){this.senddate=senddate;}

    public String getSender(){return sender;}

    public void setSender(String sender){this.sender=sender;}

    public base_person getPerson(){return person;}

    public void setPerson(base_person person){this.person=person;}

    public String getBillnum(){return billnum;}

    public void setBillnum(String billnum){this.billnum=billnum;}

    public String getPackID(){return packID;}

    public void setPackID(String packID){this.packID=packID;}

    public logistics_unplan getPack(){return pack;}

    public void setPack(logistics_unplan pack){this.pack=pack;}

    public String getPlanID(){return planID;}

    public void setPlanID(String planID){this.planID=planID;}

    public logistics_sendplan getPlan(){return plan;}

    public void setPlan(logistics_sendplan plan){this.plan=plan;}

    public String getAirportID() { return airportID; }

    public void setAirportID(String airportID) { this.airportID=airportID; }

    public base_airport getAirport() { return airport; }

    public void setAirport(base_airport airport) { this.airport=airport; }

    public String getPersonid(){return personid;}

    public void setPersonid(String personid){ this.personid=personid;}

    public base_person getChecker(){return checker;}

    public void setChecker(base_person checker){this.checker=checker;}

    public String getIntime(){return intime;}

    public void setIntime(String intime){this.intime=intime;}

    public String getCptime(){return cptime;}

    public void setCptime(String cptime){this.cptime=cptime;}

    public String getPicname(){return picname;}

    public void setPicname(String picname){this.picname=picname;}

    public String getOldpicname(){ return oldpicname;}

    public void setOldpicname(String oldpicname){this.oldpicname=oldpicname;}

    public String getPicpath(){return picpath;}

    public void setPicpath(String picpath){this.picpath=picpath;}

    public String getHlpersonid(){return hlpersonid;}

    public void setHlpersonid(String hlpersonid){this.hlpersonid=hlpersonid;}

    public base_person getHlperson(){return hlperson;}

    public void setHlperson(base_person hlperson){this.hlperson=hlperson;}

    public String getHltime(){return hltime;}

    public void setHltime(String hltime){this.hltime=hltime;}

    public String getBpicname(){return bpicname;}

    public void setBpicname(String bpicname){this.bpicname=bpicname;}

    public String getBoldpicname(){ return boldpicname;}

    public void setBoldpicname(String boldpicname){this.boldpicname=boldpicname;}

    public String getBpicpath(){return bpicpath;}

    public void setBpicpath(String bpicpath){this.bpicpath=bpicpath;}

    public String getBzpersonid(){return bzpersonid;}

    public void setBzpersonid(String bzpersonid){this.bzpersonid=bzpersonid;}

    public base_person getBzperson(){return bzperson;}

    public void setBzperson(base_person bzperson){this.bzperson=bzperson;}

    public String getBztime(){return bztime;}

    public void setBztime(String bztime){this.bztime=bztime;}

    public String getHlnote(){return hlnote;}

    public void setHlnote(String hlnote){this.hlnote=hlnote;}

    public String getBznote(){return bznote;}

    public void setBznote(String bznote){this.bznote=bznote;}

    public boolean getIsAir(){return isAir;}

    public void setIsAir(boolean isAir){this.isAir=isAir;}

}
