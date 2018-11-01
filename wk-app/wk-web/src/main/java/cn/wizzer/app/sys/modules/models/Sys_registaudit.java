package cn.wizzer.app.sys.modules.models;

import cn.wizzer.framework.base.model.BaseModel;
import org.nutz.dao.entity.annotation.*;

/**
 *
 * 注册审批
 */
@Table("sys_registaudit")
@Comment("注册审批意见表")
public class Sys_registaudit extends BaseModel {

    @Name
    @Comment("ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @Prev(els = {@EL("uuid()")})
    private String id;

    @Comment("审批人")
    @Column
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String auditorId;
    @One(field = "auditorId")
    private Sys_user auditor;

    @Comment("审批时间")
    @Column
    @ColDefine(type = ColType.VARCHAR, width = 100)
    private String  auditTime;

    @Comment("审批意见")
    @Column
    @ColDefine(type=ColType.VARCHAR,width = 300)
    private String suggestion;


    @Comment("审批结果:-1;未审核  1.退回 0.通过")
    @Column
    @ColDefine(type = ColType.INT,width = 10)
    private int result = -1;

    @Column
    @Comment("关联的注册临时信息ID")
    @ColDefine(type = ColType.VARCHAR,width = 100)
    private String tempuserId;
    @One(field = "tempuserId")
    private Sys_tempuser tempuser;


    //
    @Column
    @Comment("web端审核通过，选择人员的id(保留字段)")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String personId;

    @Column
    @Comment("注册时间")
    @ColDefine(type = ColType.VARCHAR,width = 40)
    private String registTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuditorId() {
        return auditorId;
    }

    public void setAuditorId(String auditorId) {
        this.auditorId = auditorId;
    }

    public Sys_user getAuditor() {
        return auditor;
    }

    public void setAuditor(Sys_user auditor) {
        this.auditor = auditor;
    }

    public String getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(String auditTime) {
        this.auditTime = auditTime;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getTempuserId() {
        return tempuserId;
    }

    public void setTempuserId(String tempuserId) {
        this.tempuserId = tempuserId;
    }

    public Sys_tempuser getTempuser() {
        return tempuser;
    }

    public void setTempuser(Sys_tempuser tempuser) {
        this.tempuser = tempuser;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getRegistTime() {
        return registTime;
    }

    public void setRegistTime(String registTime) {
        this.registTime = registTime;
    }
}