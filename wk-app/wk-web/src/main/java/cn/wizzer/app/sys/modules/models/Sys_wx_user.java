package cn.wizzer.app.sys.modules.models;

import cn.wizzer.framework.base.model.BaseModel;
import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;

@Comment("企业微信用户表")
@Table("sys_wx_user")
public class Sys_wx_user extends BaseModel implements Serializable {
    @Column
    @Name
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @Prev(els = {@EL("uuid()")})
    private String id;

    @Column
    @Comment("企业ID")
    @ColDefine(type = ColType.VARCHAR, width = 64)
    private String corpid;

    @Column
    @Comment("账号")
    @ColDefine(type = ColType.VARCHAR, width = 64)
    private String userid;

    @Column
    @Comment("姓名")
    @ColDefine(type = ColType.VARCHAR, width = 64)
    private String name;


    @Column
    @Comment("所属部门ID")
    @ColDefine(type = ColType.VARCHAR, width = 256)
    private String department;

    @Column
    @Comment("排序")
    @ColDefine(type = ColType.VARCHAR, width = 256)
    private String orderNum;

    @Column
    @Comment("职位")
    @ColDefine(type = ColType.VARCHAR, width = 128)
    private String position;

    @Column
    @Comment("手机号")
    @ColDefine(type = ColType.VARCHAR,width = 64)
    private String mobile;

    //0:表示未定义,1.表示男性,2表示女性
    @Column
    @Comment("性别")
    @ColDefine(type= ColType.VARCHAR,width = 2)
    private String gender;

    @Column
    @Comment("邮箱")
    @ColDefine(type = ColType.VARCHAR,width = 128)
    private String email;

    @Column
    @Comment("是否为上级")
    @ColDefine(type = ColType.VARCHAR,width = 2)
    private String isleader;


    //注：如果要获取小图将url最后的”/0”改成”/100”即可
    @Column
    @Comment("头像URL")
    @ColDefine(type = ColType.VARCHAR,width = 255)
    private String avatar;

    @Column
    @Comment("座机")
    @ColDefine(type = ColType.VARCHAR,width = 64)
    private String telephone;

    @Column
    @Comment("英文名")
    @ColDefine(type = ColType.VARCHAR,width = 64)
    private String english_name;

    //1=已激活，2=已禁用，4=未激活  已激活代表已激活企业微信或已关注微信插件。未激活代表既未激活企业微信又未关注微信插件。
    @Column
    @Comment("激活状态")
    @ColDefine(type=ColType.VARCHAR,width = 2)
    private String status;

    //如：{"attrs":[{"name":"爱好","value":"旅游"},{"name":"卡号","value":"1234567234"}]}~
    @Column
    @Comment("对外属性")
    @ColDefine(type = ColType.VARCHAR,width = 512)
    private String extattr;

    //预留微信(成员使用微信登录企业微信或者关注微信插件才能转成openid)：企业微信成员userid对应的openid，若有传参agentid，则是针对该agentid的openid。否则是针对企业微信corpid的openid
    @Column
    @Comment("微信openid")
    @ColDefine(type = ColType.VARCHAR,width = 64)
    private String openid;

    @Column
    @Comment("是否绑定系统人员")
    @ColDefine(type = ColType.BOOLEAN)
    @Default("false")
    private Boolean bindPerson;


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

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIsleader() {
        return isleader;
    }

    public void setIsleader(String isleader) {
        this.isleader = isleader;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEnglish_name() {
        return english_name;
    }

    public void setEnglish_name(String english_name) {
        this.english_name = english_name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getExtattr() {
        return extattr;
    }

    public void setExtattr(String extattr) {
        this.extattr = extattr;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public Boolean getBindPerson() {
        return bindPerson;
    }

    public void setBindPerson(Boolean bindPerson) {
        this.bindPerson = bindPerson;
    }
}
