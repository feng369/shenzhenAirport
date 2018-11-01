package cn.wizzer.app.base.modules.models;

import cn.wizzer.framework.base.model.BaseModel;
import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;
import java.util.List;

/**
 * Created by xl on 2017/7/5.
 */
@Table("base_rowoffice")
public class base_rowoffice extends BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;
    @Column
    @Name
    @Comment("ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @Prev(els = {@EL("uuid()")})
    private String id;

    @Column
    @Comment("班次名称")
    @ColDefine(type = ColType.VARCHAR, width = 120)
    private String officename;

    @Column
    @Comment("开始时间")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String startdata;

    @Column
    @Comment("结束时间")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String enddata;

    @Many(field = "officeid")
    private List<base_rowofficeentry> rowofficeentry;


    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOfficename() {
        return officename;
    }

    public void setOfficename(String officename) {
        this.officename = officename;
    }

    public List<base_rowofficeentry> getRowofficeentry() {
        return rowofficeentry;
    }

    public void setRowofficeentry(List<base_rowofficeentry> rowofficeentry) {
        this.rowofficeentry = rowofficeentry;
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
}
