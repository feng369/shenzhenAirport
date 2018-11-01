package cn.wizzer.app.base.modules.models;

import cn.wizzer.app.sys.modules.models.Sys_dict;
import cn.wizzer.app.sys.modules.models.Sys_unit;
import cn.wizzer.app.sys.modules.models.Sys_user;
import cn.wizzer.framework.base.dao.entity.annotation.Ref;
import cn.wizzer.framework.base.model.BaseModel;
import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;

/**
 * Created by on 2017/6/7.
 */
@Table("base_place")
@Comment("地点")
public class base_place extends BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;
    @Column
    @Name
    @Comment("ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @Prev(els = {@EL("uuid()")})
    private String id;

    @Column
    @Comment("地点编码")
    @ColDefine(type = ColType.VARCHAR, width = 120)
    private String placecode;

    @Column
    @Comment("父级编码")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String parentId;

    @Column
    @Comment("树路径")
    @ColDefine(type = ColType.VARCHAR, width = 100)
    private String path;

    @Column
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String airportId;

    @Column
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String customerId;

    @Column
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String areaId;


    @Column
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String typeId;



    @Column
    @Comment("地点名称")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    private String placename;

    @Column
    @Comment("管控点")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String ctrlId;



    @Column
    @Comment("负责人")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String personId;

    @Column
    @Comment("联系电话")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String telephone;

    @Column
    @Comment("地址")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    private String address;

    @Column
    @Comment("地理位置")
    @ColDefine(type = ColType.VARCHAR, width = 120)
    private String position;



    @Column
    @Comment("备注")
    @ColDefine(type = ColType.VARCHAR, width = 360)
    private String describ;

    @Column
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @Ref(Sys_unit.class)
    private String unitId;

    @Column
    @Comment("有子节点")
    private boolean hasChildren;

    public boolean isHasChildren() {
        return hasChildren;
    }

    public void setHasChildren(boolean hasChildren) {
        this.hasChildren = hasChildren;
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

    public String getPlacecode() {
        return placecode;
    }

    public void setPlacecode(String placecode) {
        this.placecode = placecode;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getAirportId() {
        return airportId;
    }

    public void setAirportId(String airportId) {
        this.airportId = airportId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getPlacename() {
        return placename;
    }

    public void setPlacename(String placename) {
        this.placename = placename;
    }

    public String getCtrlId() {
        return ctrlId;
    }

    public void setCtrlId(String ctrlId) {
        this.ctrlId = ctrlId;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getDescrib() {
        return describ;
    }

    public void setDescrib(String describ) {
        this.describ = describ;
    }

    public String getUnitId() {
        return unitId;
    }

    public void setUnitId(String unitId) {
        this.unitId = unitId;
    }

    public base_airport getAirport() {
        return airport;
    }

    public void setAirport(base_airport airport) {
        this.airport = airport;
    }

    public base_customer getCustomer() {
        return customer;
    }

    public void setCustomer(base_customer customer) {
        this.customer = customer;
    }

    public base_person getPerson() {
        return person;
    }

    public void setPerson(base_person person) {
        this.person = person;
    }

    public Sys_dict getArea() {
        return area;
    }

    public void setArea(Sys_dict area) {
        this.area = area;
    }

    public Sys_dict getType() {
        return type;
    }

    public void setType(Sys_dict type) {
        this.type = type;
    }

    public Sys_dict getCtrl() {
        return ctrl;
    }

    public void setCtrl(Sys_dict ctrl) {
        this.ctrl = ctrl;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    /*
    One airport,customer,dict,person

     */

    @One(field = "airportId")
    private base_airport airport;

    @One(field = "customerId")
    private base_customer customer;

    @One(field = "personId")
    private base_person person;

    @One(field = "areaId")
    private Sys_dict area;

    @One(field = "typeId")
    private Sys_dict type;

    @One(field = "ctrlId")
    private Sys_dict ctrl;


}
