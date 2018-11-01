package cn.wizzer.app.sys.modules.models;

import cn.wizzer.framework.base.model.BaseModel;
import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;
@Table("sys_wx_dept")
@Comment("企业微信部门表")
public class Sys_wx_dept extends BaseModel implements Serializable {
    @Column
    @Name
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @Prev(els = {@EL("uuid()")})
    private String id;


    @Column
    @Comment("企业ID")
    @ColDefine(type=ColType.VARCHAR,width = 64)
    private String corpid;

    @Column
    @Comment("部门ID")
    @ColDefine(type = ColType.VARCHAR,width = 16)
    private String deptid;

    @Column
    @Comment("部门名称")
    @ColDefine(type = ColType.VARCHAR,width = 64)
    private String name;

    @Column
    @Comment("上级部门ID")
    @ColDefine(type = ColType.VARCHAR,width = 16)
    private String parentid;

    @Column
    @Comment("排序")
    @ColDefine(type = ColType.VARCHAR,width = 16)
    private String orderNum;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCorpid() {
        return corpid;
    }

    public void setCorpid(String corpid) {
        this.corpid = corpid;
    }

    public String getDeptid() {
        return deptid;
    }

    public void setDeptid(String deptid) {
        this.deptid = deptid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParentid() {
        return parentid;
    }

    public void setParentid(String parentid) {
        this.parentid = parentid;
    }

    public String getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }
}
