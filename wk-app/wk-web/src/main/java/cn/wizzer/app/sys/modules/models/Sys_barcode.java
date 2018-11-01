package cn.wizzer.app.sys.modules.models;

import cn.wizzer.framework.base.model.BaseModel;
import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;

/**
 * Created by caiwe on 2017/7/19.
 */
@Table("sys_barcode")
public class Sys_barcode extends BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;
    @Column
    @Name
    @Comment("ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @Prev(els = {@EL("uuid()")})
    private String id;

    @Column
    @Comment("编码")
    @ColDefine(type = ColType.VARCHAR, width = 120)
    private String codenum;

    @Column
    @Comment("用户id")
    @ColDefine(type = ColType.VARCHAR, width = 120)
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
    @Comment("扫码日期")
    @ColDefine(type = ColType.VARCHAR,width = 120)
    private String curDate;

    @Column
    @Comment("状态")
    @ColDefine(type = ColType.VARCHAR,width = 10)
    private String pstatus;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCodenum() {
        return codenum;
    }

    public void setCodenum(String codenum) {
        this.codenum = codenum;
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

    public String getCurDate(){
        return curDate;
    }

    public void setCurDate(String curDate){
        this.curDate=curDate;
    }

    public String getPstatus(){
        return pstatus;
    }

    public void setPstatus(String pstatus){
        this.pstatus=pstatus;
    }
}
