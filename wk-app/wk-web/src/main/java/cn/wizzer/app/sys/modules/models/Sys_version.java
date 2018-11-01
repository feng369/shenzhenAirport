package cn.wizzer.app.sys.modules.models;

import cn.wizzer.framework.base.model.BaseModel;
import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;

/**
 * Created by wizzer on 2016/8/11.
 */
@Table("sys_version")
public class Sys_version extends BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;
    @Column
    @Name
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @Prev(els = {@EL("uuid()")})
    private String id;

    @Column
    @Comment("name")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String name;

    @Column
    @Comment("version")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String version;

    @Column
    @Comment("wgt")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private boolean wgt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public boolean getWgt(){return wgt;}

    public void setWgt(boolean wgt){this.wgt=wgt;}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
