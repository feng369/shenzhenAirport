package cn.wizzer.app.sys.modules.models;

import cn.wizzer.framework.base.dao.entity.annotation.Ref;
import cn.wizzer.framework.base.model.BaseModel;
import org.nutz.dao.entity.annotation.*;
import cn.wizzer.app.base.modules.models.base_vehicle;

import java.io.Serializable;

/**
 * Created by caiwe on 2017/7/19.
 */
@Table("sys_mobile")
@Comment("移动设备")
public class Sys_mobile extends BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;
    @Column
    @Name
    @Comment("ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @Prev(els = {@EL("uuid()")})
    private String id;

    @Column
    @Comment("设备编码")
    @ColDefine(type = ColType.VARCHAR, width = 120)
    private String deviceid;

    @Column
    @Comment("用户id")
    @ColDefine(type = ColType.VARCHAR, width = 120)
    @Ref(Sys_user.class)
    private String userid;

    @One(field="userid")
    private Sys_user user;

    @Column
    @Comment("生效日期")
    @ColDefine(type = ColType.VARCHAR, width = 120)
    private String startDate;

    @Column
    @Comment("失效日期")
    @ColDefine(type = ColType.VARCHAR, width = 120)
    private String endDate;

    @Column
    @Comment("设备系统")
    @ColDefine(type = ColType.VARCHAR,width = 120)
    private String deviceOS;

    @Column
    @Comment("设备型号")
    @ColDefine(type = ColType.VARCHAR,width = 120)
    private String deviceModel;

    @Column
    @Comment("状态")
    @ColDefine(type = ColType.VARCHAR,width = 10)
    private String pstatus;

    @Column
    @Comment("account")
    @ColDefine(type = ColType.VARCHAR,width = 120)
    private String account;

    @Column
    @Comment("password")
    @ColDefine(type = ColType.VARCHAR,width = 255)
    private String password;

    @Column
    @Comment("vehicleid")
    @ColDefine(type = ColType.VARCHAR,width = 32)
    private String vehicleid;

    @One(field="vehicleid")
    private base_vehicle vehicle;


    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDeviceid () {
        return deviceid;
    }

    public void setDeviceid(String deviceid) {
        this.deviceid = deviceid;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public Sys_user getUser(){
        return user;
    }

    public void setUser(Sys_user user){
        this.user=user;
    }

    public String getStartDate(){
        return startDate;
    }

    public void setStartDate(String startDate){
        this.startDate=startDate;
    }

    public String getEndDate(){
        return endDate;
    }

    public void setEndDate(String endDate){
        this.endDate=endDate;
    }

    public String getDeviceOS(){
        return deviceOS;
    }

    public void setDeviceOS(String deviceOS){
        this.deviceOS=deviceOS;
    }

    public String getDeviceModel(){
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel){
        this.deviceModel=deviceModel;
    }

    public String getPstatus(){
        return pstatus;
    }

    public void setPstatus(String pstatus){
        this.pstatus=pstatus;
    }

    public String getAccount(){ return account;}

    public void setAccount(String account){ this.account=account;}

    public String getPassword(){return password;}

    public void setPassword(String password){this.password=password;}

    public String getVehicleid(){return vehicleid;}

    public void setVehicleid(String vehicleid){this.vehicleid=vehicleid;}

    public base_vehicle getVehicle(){ return vehicle;}

    public  void setVehicle(base_vehicle vehicle){ this.vehicle=vehicle;}
}
