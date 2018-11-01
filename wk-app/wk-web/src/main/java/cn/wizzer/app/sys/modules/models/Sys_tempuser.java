package cn.wizzer.app.sys.modules.models;

import cn.wizzer.framework.base.dao.entity.annotation.Ref;
import cn.wizzer.framework.base.model.BaseModel;
import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by wizzer on 2016/6/21.
 */
@Table("sys_tempuser")
@Comment("注册信息临时表")
public class Sys_tempuser extends BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;
    @Column
    @Name
    @Comment("ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @Prev(els = {@EL("uuid()")})
    private String id;

    @Column
    @Comment("客户ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String customerid;



    @Column
    @Comment("公司ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String unitid;

   @Column
   @Comment("人员姓名")
   @ColDefine(type = ColType.VARCHAR, width = 120)
   private String username;

   @Column
    @Comment("用户名")
    @ColDefine(type = ColType.VARCHAR, width = 120)
   private String loginname;

    @Column
    @Comment("密码")
    @ColDefine(type = ColType.VARCHAR, width = 120)
   private String password;

    @Column
    @Comment("盐")
    @ColDefine(type = ColType.VARCHAR, width = 120)
   private String salt;



    @Column
    @Comment("电话号码")
    @ColDefine(type = ColType.VARCHAR, width = 120)
    private String tel;

    @Column
    @Comment("通行证号码")
    @ColDefine(type = ColType.VARCHAR, width = 120)
    private String permitNumber;
    @Column
    @Comment("工号")
    @ColDefine(type = ColType.VARCHAR,width = 100)
     private String jobNumber;

    @Column
    @Comment("图片路径")
    @ColDefine(type = ColType.VARCHAR,width = 100)
    private String picture;

    @Column
    @Comment("微信账号")
    @ColDefine(type = ColType.VARCHAR,width = 40)
    private String wxNumber;

    @Column
    @Comment("角色编号")
    @ColDefine(type = ColType.VARCHAR, width = 120)
    private String roleCode;

    @Column
    @Comment("注册方式 0:手机 1:web")
    @ColDefine(type = ColType.INT,width = 1)
    private int way;

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getWxNumber() {
        return wxNumber;
    }

    public void setWxNumber(String wxNumber) {
        this.wxNumber = wxNumber;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomerid() {
        return customerid;
    }

    public void setCustomerid(String customerid) {
        this.customerid = customerid;
    }

    public int getWay() {
        return way;
    }

    public void setWay(int way) {
        this.way = way;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLoginname() {
        return loginname;
    }

    public void setLoginname(String loginname) {
        this.loginname = loginname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }
    public String getPermitNumber() {
        return permitNumber;
    }

    public void setPermitNumber(String permitNumber) {
        this.permitNumber = permitNumber;
    }
    public String getUnitid() {
        return unitid;
    }

    public void setUnitid(String unitid) {
        this.unitid = unitid;
    }
    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getJobNumber() {
        return jobNumber;
    }

    public void setJobNumber(String jobNumber) {
        this.jobNumber = jobNumber;
    }

}
