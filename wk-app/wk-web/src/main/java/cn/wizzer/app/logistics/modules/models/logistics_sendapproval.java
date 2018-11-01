package cn.wizzer.app.logistics.modules.models;

import cn.wizzer.app.base.modules.models.base_person;
import cn.wizzer.app.sys.modules.models.Sys_dict;
import cn.wizzer.framework.base.model.BaseModel;
import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;

/**
 * Created by cw on 2017/10/14.
 */
@Table("logistics_sendapproval")
public class logistics_sendapproval extends BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;
    @Column
    @Name
    @Comment("ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @Prev(els = {@EL("uuid()")})
    private String id;

    @Column
    @Comment("计划ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String planID;
    @One(field = "planID")
    private logistics_sendplan plan;


    @Column
    @Comment("审批结果 0 未通过 1 通过")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String aresult;
    @One(field = "aresult")
    private Sys_dict approval;

    @Column
    @Comment("审批意见")
    @ColDefine(type = ColType.VARCHAR, width = 200)
    private String suggest;

    @Column
    @Comment("审批人")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String approvaler;
    @One(field = "approvaler")
    private base_person person;

    @Column
    @Comment("审批时间")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String approvaldate;

    @Column
    @Comment("审批状态 0 未审批 1 已审批")
    @ColDefine(type = ColType.INT)
    private  int pstatus;

    @Column
    @Comment("申请人")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String applyer;
    @One(field = "applyer")
    private base_person apply;


    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlanID() {
        return planID;
    }

    public void setPlanID(String planID) {
        this.planID = planID;
    }

    public logistics_sendplan getPlan(){return plan;}

    public void setPlan(logistics_sendplan plan){this.plan=plan;}

    public String getAresult() { return aresult; }

    public void setAresult(String aresult) { this.aresult=aresult; }

    public Sys_dict getApproval() {
        return approval;
    }

    public void setApproval(Sys_dict approval) {
        this.approval = approval;
    }

    public String getSuggest() {
        return suggest;
    }

    public void setSuggest(String suggest) {
        this.suggest = suggest;
    }

    public String getApprovaler() {
        return approvaler;
    }

    public void setApprovaler(String approvaler) {
        this.approvaler = approvaler;
    }

    public base_person getPerson() {
        return person;
    }

    public void setPerson(base_person person) {
        this.person = person;
    }

    public String getApprovaldate() {
        return approvaldate;
    }

    public void setApprovaldate(String approvaldate) {
        this.approvaldate = approvaldate;
    }

    public String getApplyer(){ return applyer;}

    public void setApplyer(String applyer){this.applyer=applyer;}

    public base_person getApply(){return apply;}

    public void setApply(base_person apply){this.apply=apply;}

    public int getPstatus(){ return pstatus;}

    public void setPstatus(int pstatus){this.pstatus=pstatus;}

}
