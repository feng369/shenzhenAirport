package cn.wizzer.app.base.modules.models;

import cn.wizzer.app.sys.modules.models.Sys_user;
import cn.wizzer.framework.base.model.BaseModel;
import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;

/**
 * Created by caiwe on 2017/6/1.
 */
@Table("base_airport")
public class base_airport extends BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;
    @Column
    @Name
    @Comment("ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @Prev(els = {@EL("uuid()")})
    private String id;

    @Column
    @Comment("机场编号")
    @ColDefine(type = ColType.VARCHAR, width = 120)
    private String airportnum;

    @Column
    @Comment("机场名称")
    @ColDefine(type = ColType.VARCHAR, width = 120)
    private String airportname;

    @One(field = "creater")
    private Sys_user createUser;

    @Column
    @Comment("地理位置")
    @ColDefine(type = ColType.VARCHAR, width = 120)
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

    public String getAirportnum() {
        return airportnum;
    }

    public void setAirportnum(String airportnum) {
        this.airportnum = airportnum;
    }

    public String getAirportname() {
        return airportname;
    }

    public void setAirportname(String airportname) {
        this.airportname = airportname;
    }

    public Sys_user getCreateUser() {
        return createUser;
    }

    public void setCreateUser(Sys_user createUser) {
        this.createUser = createUser;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }
}
