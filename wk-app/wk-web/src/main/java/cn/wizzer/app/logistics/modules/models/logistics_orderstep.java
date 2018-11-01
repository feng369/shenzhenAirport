package cn.wizzer.app.logistics.modules.models;

import cn.wizzer.app.sys.modules.models.Sys_dict;
import cn.wizzer.framework.base.model.BaseModel;
import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;

/**
 * Created by xl on 2017/6/28.
 */
@Table("logistics_orderstep")
public class logistics_orderstep extends BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;
    @Column
    @Name
    @Comment("ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @Prev(els = {@EL("uuid()")})
    private String id;


    @Column
    @Comment("步骤编码")
    @ColDefine(type = ColType.VARCHAR, width = 100)
    private String stepnum;

    @Column
    @Comment("步骤")
    @ColDefine(type = ColType.INT)
    private Integer step;

    @Column
    @Comment("步骤名称")
    @ColDefine(type = ColType.VARCHAR, width = 100)
    private String stepname;

    @Column
    @Comment("类型")
    @ColDefine(type = ColType.VARCHAR,width = 32)
    private String otype;
    @One(field = "otype")
    private Sys_dict otypeDict;

    @Column
    @Comment("业务类型")
    @ColDefine(type = ColType.VARCHAR,width = 32)
    private String btype;
    @One(field = "btype")
    private Sys_dict btypeDict;

    @Column
    @Comment("是否前端为按钮事件")
    @ColDefine(type = ColType.BOOLEAN)
    private boolean isButton;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getStep() {
        return step;
    }

    public void setStep(Integer step) {
        this.step = step;
    }

    public Boolean getIsButton() {
        return isButton;
    }

    public void setIsButton(boolean isButton) {
        this.isButton = isButton;
    }

    public String getStepname() {
        return stepname;
    }

    public void setStepname(String stepname) {
        this.stepname = stepname;
    }

    public String getStepnum() {
        return stepnum;
    }

    public void setStepnum(String stepnum) {
        this.stepnum = stepnum;
    }

    public String getOtype() {
        return otype;
    }

    public void setOtype(String otype) {
        this.otype = otype;
    }

    public String getBtype() {
        return btype;
    }

    public void setBtype(String btype) {
        this.btype = btype;
    }

    public Sys_dict getOtypeDict() {
        return otypeDict;
    }

    public void setOtypeDict(Sys_dict otypeDict) {
        this.otypeDict = otypeDict;
    }

    public Sys_dict getBtypeDict() {
        return btypeDict;
    }

    public void setBtypeDict(Sys_dict btypeDict) {
        this.btypeDict = btypeDict;
    }
}
