package cn.wizzer.app.logistics.modules.models;

import cn.wizzer.framework.base.model.BaseModel;
import org.nutz.dao.entity.annotation.*;
import cn.wizzer.app.sys.modules.models.Sys_dict;
import cn.wizzer.app.base.modules.models.base_person;

import java.io.Serializable;

/**
 * Created by xl on 2017/6/28.
 */
@Table("logistics_orderreject")
public class logistics_orderreject extends BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;
    @Column
    @Name
    @Comment("ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @Prev(els = {@EL("uuid()")})
    private String id;

    @Column
    @Comment("订单id")
    @ColDefine(type = ColType.VARCHAR, width = 512)
    private String orderid;

    @Column
    @Comment("拒接原因ID")
    @ColDefine(type = ColType.VARCHAR, width = 512)
    private String rejectid;

    @Column
    @Comment("拒接人")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String personid;

    @Column
    @Comment("备注")
    @ColDefine(type = ColType.VARCHAR, width = 200)
    private String describ;

    @One(field = "rejectid")
    private Sys_dict reject;

    @One(field = "personid")
    private base_person person;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrderid() { return orderid; }

    public void setOrderid(String orderid) { this.orderid=orderid; }

    public String getRejectid() {
        return rejectid;
    }

    public void setRejectid(String rejectid) {
        this.rejectid = rejectid;
    }

    public String getPersonid() {
        return personid;
    }

    public void setPersonid(String personid) {
        this.personid = personid;
    }

    public String getDescrib() {
        return describ;
    }

    public void setDescrib(String describ) {
        this.describ = describ;
    }

    public Sys_dict getReject() { return reject; }

    public void setReject(Sys_dict reject) { this.reject = reject; }

    public base_person getPerson() { return  person; }

    public void setPerson(base_person person) { this.person = person; }
}
