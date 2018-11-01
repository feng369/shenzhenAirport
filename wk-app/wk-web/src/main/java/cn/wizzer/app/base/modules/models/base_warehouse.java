package cn.wizzer.app.base.modules.models;

import cn.wizzer.framework.base.model.BaseModel;
import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;

/**
 * Created by cw on 2017/10/14.
 */
@Table("base_warehouse")
public class base_warehouse extends BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;
    @Column
    @Name
    @Comment("ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @Prev(els = {@EL("uuid()")})
    private String id;

    @Column
    @Comment("所属机场")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String airportID;
    @One(field = "airportID")
    private base_airport airport;

    @Column
    @Comment("库存地名称")
    @ColDefine(type = ColType.VARCHAR, width = 100)
    private String warehousename;

    @Column
    @Comment("库存地编号")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String whnum;

    @Column
    @Comment("状态")
    @ColDefine(type = ColType.INT)
    private int pstatus;


    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAirportID() { return airportID; }

    public void setAirportID(String airportID) { this.airportID=airportID; }

    public base_airport getAirport() { return airport; }

    public void setAirport(base_airport airport) { this.airport=airport; }

    public String getWarehousename() {
        return warehousename;
    }

    public void setWarehousename(String warehousename) {
        this.warehousename = warehousename;
    }

    public String getWhnum() {
        return whnum;
    }

    public void setWhnum(String whnum) {
        this.whnum = whnum;
    }

    public int getPstatus() {
        return pstatus;
    }

    public void setPstatus(int pstatus) {
        this.pstatus = pstatus;
    }

    //20180227zhf1156
    public String getStatusName(){
        switch (pstatus){
            case 1: return "启用";
            case 2:return "未启用";
            default: return "";
        }
    }
}
