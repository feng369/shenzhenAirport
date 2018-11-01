package cn.wizzer.app.base.modules.models;

import cn.wizzer.framework.base.model.BaseModel;
import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;

/**
 * Created by on 2017/6/7.
 */
@Table("base_customer")
public class base_customer extends BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;
    @Column
    @Name
    @Comment("ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @Prev(els = {@EL("uuid()")})
    private String id;

    @Column
    @Comment("客户名称")
    @ColDefine(type = ColType.VARCHAR, width = 120)
    private String customername;

    @Column
    @Comment("客户编码")
    @ColDefine(type = ColType.VARCHAR, width = 120)
    private String customercode;

    @Column
    @Comment("客户地址")
    @ColDefine(type = ColType.VARCHAR, width = 360)
    private String customeraddress;

    @Column
    @Comment("联系电话")
    @ColDefine(type = ColType.VARCHAR, width = 360)
    private String customertel;



    @Column
    @Comment("编号开头")
    @ColDefine(type = ColType.VARCHAR, width = 360)
    private String cusnum;

    @Column
    @Comment("备注")
    @ColDefine(type = ColType.VARCHAR, width = 1024)
    private String customerdecrib;


    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomername() {
        return customername;
    }

    public void setCustomername(String customername) {
        this.customername = customername;
    }

    public String getCustomercode() {
        return customercode;
    }

    public void setCustomercode(String customercode) {
        this.customercode = customercode;
    }

    public String getCustomeraddress() {
        return customeraddress;
    }

    public void setCustomeraddress(String customeraddress) {
        this.customeraddress = customeraddress;
    }

    public String getCustomertel() {
        return customertel;
    }

    public void setCustomertel(String customertel) {
        this.customertel = customertel;
    }

    public String getCustomerdecrib() {
        return customerdecrib;
    }

    public void setCustomerdecrib(String customerdecrib) {
        this.customerdecrib = customerdecrib;
    }

    public String getCusnum() {
        return cusnum;
    }

    public void setCusnum(String cusnum) {
        this.cusnum = cusnum;
    }

}
