package cn.wizzer.app.base.modules.models;

import cn.wizzer.app.sys.modules.models.Sys_dict;
import cn.wizzer.framework.base.model.BaseModel;
import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;

/**
 * Created by caiwe on 2017/6/10.
 */
@Table("base_airmeterial")
public class base_airmeterial extends BaseModel implements Serializable {


    private static final long serialVersionUID = 1L;
    @Column
    @Name
    @Comment("ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @Prev(els = {@EL("uuid()")})
    private String id;

    @Column
    @Comment("件号")
    @ColDefine(type = ColType.VARCHAR, width = 120)
    private String meterialnum;

    @Column
    @Comment("名称")
    @ColDefine(type = ColType.VARCHAR, width = 120)
    private String meterialname;

    @Column
    @Comment("类型")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String meterialtypeId;

    @Column
    @Comment("厂家")
    @ColDefine(type = ColType.VARCHAR, width = 120)
    private String manufactor;

    @Column
    @Comment("型号")
    @ColDefine(type = ColType.VARCHAR, width = 120)
    private String meterialmodel;

    @Column
    @Comment("单位")
    @ColDefine(type = ColType.VARCHAR, width = 64)
    private String meteritalunit;

    @Column
    @Comment("机型")
    @ColDefine(type = ColType.VARCHAR, width = 120)
    private String meteritalspec;

    @Column
    @Comment("长度(厘米)")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String meteritallength;

    @Column
    @Comment("宽度(厘米)")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String meteritalwidth;

    @Column
    @Comment("高度(厘米)")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String meteritalhigh;

    @Column
    @Comment("重量(千克)")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String meteritalweight;

    @Column
    @Comment("是否打包")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @Default("0")
    private String haspack;

    @Column
    @Comment("容纳量(标准单位)")
    @ColDefine(type = ColType.VARCHAR, width = 120)
    private String capacity;

    @Column
    @Comment("最少搬运人数")
    @ColDefine(type = ColType.INT)
    private String mincarrier;

    @Column
    @Comment("单次搬运数量")
    @ColDefine(type = ColType.FLOAT)
    private String singlecarrynum;

    @Column
    @Comment("单次装车时间")
    @ColDefine(type = ColType.VARCHAR,width = 32)
    private String singleloading;

    @Column
    @Comment("单次卸车时间")
    @ColDefine(type = ColType.VARCHAR,width = 32)
    private String singleunloading;

    @Column
    @Comment("配送车辆类型")
    @ColDefine(type = ColType.VARCHAR,width = 32)
    private String vehicletypeId;

    @Column
    @Comment("是否需要叉车")
    @ColDefine(type = ColType.VARCHAR,width = 32)
    private String hasforklift;

    @Column
    @Comment("备注")
    @ColDefine(type = ColType.VARCHAR,width = 255)
    private String describ;

    @One(field = "meterialtypeId")
    private Sys_dict meterialtype;

    @One(field = "haspack")
    private Sys_dict pack;

    @One(field = "vehicletypeId")
    private Sys_dict vehicletype;

    @One(field = "hasforklift")
    private Sys_dict forklift;


    public void setMeterialtype(Sys_dict meterialtype) {
        this.meterialtype = meterialtype;
    }

    public Sys_dict getMeterialtype() {
        return meterialtype;
    }

    public void setPack(Sys_dict pack) {
        this.pack = pack;
    }

    public Sys_dict getPack() {
        return pack;
    }

    public void setVehicletype(Sys_dict vehicletype) {
        this.vehicletype = vehicletype;
    }

    public Sys_dict getVehicletype() {
        return vehicletype;
    }

    public void setForklift(Sys_dict forklift) {
        this.forklift = forklift;
    }

    public Sys_dict getForklift() {
        return forklift;
    }


    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMeterialnum() {
        return meterialnum;
    }

    public void setMeterialnum(String meterialnum) {
        this.meterialnum = meterialnum;
    }

    public String getMeterialname() {
        return meterialname;
    }

    public void setMeterialname(String meterialname) {
        this.meterialname = meterialname;
    }

    public String getMeterialtypeId() {
        return meterialtypeId;
    }

    public void setMeterialtypeId(String meterialtypeId) {
        this.meterialtypeId = meterialtypeId;
    }

    public String getManufactor() {
        return manufactor;
    }

    public void setManufactor(String manufactor) {
        this.manufactor = manufactor;
    }

    public String getMeterialmodel() {
        return meterialmodel;
    }

    public void setMeterialmodel(String meterialmodel) {
        this.meterialmodel = meterialmodel;
    }

    public String getMeteritalunit() {
        return meteritalunit;
    }

    public void setMeteritalunit(String meteritalunit) {
        this.meteritalunit = meteritalunit;
    }

    public String getMeteritalspec() {
        return meteritalspec;
    }

    public void setMeteritalspec(String meteritalspec) {
        this.meteritalspec = meteritalspec;
    }

    public String getMeteritallength() {
        return meteritallength;
    }

    public void setMeteritallength(String meteritallength) {
        this.meteritallength = meteritallength;
    }

    public String getMeteritalwidth() {
        return meteritalwidth;
    }

    public void setMeteritalwidth(String meteritalwidth) {
        this.meteritalwidth = meteritalwidth;
    }

    public String getMeteritalhigh() {
        return meteritalhigh;
    }

    public void setMeteritalhigh(String meteritalhigh) {
        this.meteritalhigh = meteritalhigh;
    }

    public String getMeteritalweight() {
        return meteritalweight;
    }

    public void setMeteritalweight(String meteritalweight) {
        this.meteritalweight = meteritalweight;
    }

    public String getHaspack() {
        return haspack;
    }

    public void setHaspack(String haspack) {
        this.haspack = haspack;
    }

    public String getCapacity() {
        return capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public String getMincarrier() {
        return mincarrier;
    }

    public void setMincarrier(String mincarrier) {
        this.mincarrier = mincarrier;
    }

    public String getSinglecarrynum() {
        return singlecarrynum;
    }

    public void setSinglecarrynum(String singlecarrynum) {
        this.singlecarrynum = singlecarrynum;
    }

    public String getSingleloading() {
        return singleloading;
    }

    public void setSingleloading(String singleloading) {
        this.singleloading = singleloading;
    }

    public String getSingleunloading() {
        return singleunloading;
    }

    public void setSingleunloading(String singleunloading) {
        this.singleunloading = singleunloading;
    }

    public String getVehicletypeId() {
        return vehicletypeId;
    }

    public void setVehicletypeId(String vehicletypeId) {
        this.vehicletypeId = vehicletypeId;
    }

    public String getHasforklift() {
        return hasforklift;
    }

    public void setHasforklift(String hasforklift) {
        this.hasforklift = hasforklift;
    }

    public String getDescrib() {
        return describ;
    }

    public void setDescrib(String describ) {
        this.describ = describ;
    }

}
