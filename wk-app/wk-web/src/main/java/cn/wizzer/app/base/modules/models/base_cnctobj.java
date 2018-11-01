package cn.wizzer.app.base.modules.models;

import cn.wizzer.app.sys.modules.models.Sys_dict;
import cn.wizzer.app.sys.modules.models.Sys_user;
import cn.wizzer.framework.base.dao.entity.annotation.Ref;
import cn.wizzer.framework.base.model.BaseModel;
import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;

/**
 * Created by caiwe on 2017/6/1.
 * 联系对象
 */
@Table("base_cnctobj")
@Comment("联系对象")
public class base_cnctobj  extends BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column
    @Name
    @Comment("ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @Prev(els = {@EL("uuid()")})
    private String id;

    @Column
    @Comment("用户")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @Ref(Sys_user.class)
    private String userId;

    @Column
    @Comment("人员")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String personId;

    @Column
    @Comment("类型")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String emptypeId;


    @One(field = "userId")
    private Sys_user user;

    @One(field="personId")
    private base_person person;

    @One(field = "emptypeId")
    private Sys_dict dict;



    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getEmptypeId() {
        return emptypeId;
    }

    public void setEmptypeId(String emptypeId) {
        this.emptypeId = emptypeId;
    }

    public Sys_user getUser() {
        return user;
    }

    public void setUser(Sys_user user) {
        this.user = user;
    }

    public base_person getPerson() {
        return person;
    }

    public void setPerson(base_person person) {
        this.person = person;
    }

    public Sys_dict getDict() {
        return dict;
    }

    public void setDict(Sys_dict dict) {
        this.dict = dict;
    }
}
