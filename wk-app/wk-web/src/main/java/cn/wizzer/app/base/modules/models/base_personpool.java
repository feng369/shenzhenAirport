package cn.wizzer.app.base.modules.models;

import cn.wizzer.framework.base.model.BaseModel;
import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;

/**
 * Created by xl on 2017/7/5.
 */
@Table("base_personpool")
public class base_personpool extends BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;
    @Column
    @Name
    @Comment("ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @Prev(els = {@EL("uuid()")})
    private String id;

    @Column
    @Comment("人员ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String personid;

    @One(field = "personid")
    private base_person basePerson;

    @Column
    @Comment("班次ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String rowofficeid;

    @Column
    @Comment("当班状态")
    @ColDefine(type = ColType.INT)
    private int pstatus;

    @Column
    @Comment("人员状态")
    @ColDefine(type = ColType.INT)
    private int personstatus;

    @Column
    @Comment("开始时间")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String startdata;

    @Column
    @Comment("结束时间")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String enddata;


    @Column
    @Comment("地理位置")
    @ColDefine(type = ColType.VARCHAR, width = 100)
    private String position;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPersonid() {
        return personid;
    }

    public void setPersonid(String personid) {
        this.personid = personid;
    }

    public String getRowofficeid() {
        return rowofficeid;
    }

    public void setRowofficeid(String rowofficeid) {
        this.rowofficeid = rowofficeid;
    }

    public int getPstatus() {
        return pstatus;
    }

    public void setPstatus(int pstatus) {
        this.pstatus = pstatus;
    }

    public String getStartdata() {
        return startdata;
    }

    public void setStartdata(String startdata) {
        this.startdata = startdata;
    }

    public String getEnddata() {
        return enddata;
    }

    public void setEnddata(String enddata) {
        this.enddata = enddata;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public base_person getBasePerson() {
        return basePerson;
    }

    public void setBasePerson(base_person basePerson) {
        this.basePerson = basePerson;
    }

    public int getPersonstatus() {
        return personstatus;
    }

    public void setPersonstatus(int personstatus) {
        this.personstatus = personstatus;
    }
}
