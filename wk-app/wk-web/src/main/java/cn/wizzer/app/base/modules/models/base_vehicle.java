package cn.wizzer.app.base.modules.models;

import cn.wizzer.app.sys.modules.models.Sys_dict;
import cn.wizzer.framework.base.model.BaseModel;
import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;

/**
 * Created by caiwe on 2017/6/10.
 */
@Table("base_vehicle")
public class base_vehicle  extends BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;
    @Column
    @Name
    @Comment("ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @Prev(els = {@EL("uuid()")})
    private String id;

    @Column
    @Comment("车牌号")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String vehiclenum;

    @Column
    @Comment("类型")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String vehicletypeId;

    @Column
    @Comment("状态")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String vehiclestateId;

    @Column
    @Comment("区域")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String vehicleareaId;

    @Column
    @Comment("货箱长(cm)")
    @ColDefine(type = ColType.FLOAT)
    private Float containerlength;

    @Column
    @Comment("货箱宽(cm)")
    @ColDefine(type = ColType.FLOAT)
    private Float containerwidth;

    @Column
    @Comment("货箱高(cm)")
    @ColDefine(type = ColType.FLOAT)
    private Float containerheight;

    @Column
    @Comment("载重量(kg)")
    @ColDefine(type = ColType.FLOAT)
    private Float containerweight;

    @Column
    @Comment("备注")
    @ColDefine(type = ColType.VARCHAR)
    private String describ;

    @One(field = "vehicleareaId")
    private Sys_dict vehiclearea;

    @One(field = "vehiclestateId")
    private Sys_dict vehiclestate;

    @One(field = "vehicletypeId")
    private Sys_dict vehicletype;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVehiclenum() {
        return vehiclenum;
    }

    public void setVehiclenum(String vehiclenum) {
        this.vehiclenum = vehiclenum;
    }

    public String getVehicletypeId() {
        return vehicletypeId;
    }

    public void setVehicletypeId(String vehicletypeId) {
        this.vehicletypeId = vehicletypeId;
    }

    public String getVehiclestateId() {
        return vehiclestateId;
    }

    public void setVehiclestateId(String vehiclestateId) {
        this.vehiclestateId = vehiclestateId;
    }

    public String getVehicleareaId() {
        return vehicleareaId;
    }

    public void setVehicleareaId(String vehicleareaId) {
        this.vehicleareaId = vehicleareaId;
    }

    public Float getContainerlength() {
        return containerlength;
    }

    public void setContainerlength(Float containerlength) {
        this.containerlength = containerlength;
    }

    public Float getContainerwidth() {
        return containerwidth;
    }

    public void setContainerwidth(Float containerwidth) {
        this.containerwidth = containerwidth;
    }

    public Float getContainerheight() {
        return containerheight;
    }

    public void setContainerheight(Float containerheight) {
        this.containerheight = containerheight;
    }

    public Float getContainerweight() {
        return containerweight;
    }

    public void setContainerweight(Float containerweight) {
        this.containerweight = containerweight;
    }

    public String getDescrib() {
        return describ;
    }

    public void setDescrib(String describ) {
        this.describ = describ;
    }

    public Sys_dict getVehiclearea() {
        return vehiclearea;
    }

    public void setVehiclearea(Sys_dict vehiclearea) {
        this.vehiclearea = vehiclearea;
    }

    public Sys_dict getVehiclestate() {
        return vehiclestate;
    }

    public void setVehiclestate(Sys_dict vehiclestate) {
        this.vehiclestate = vehiclestate;
    }

    public Sys_dict getVehicletype() {
        return vehicletype;
    }

    public void setVehicletype(Sys_dict vehicletype) {
        this.vehicletype = vehicletype;
    }
}
