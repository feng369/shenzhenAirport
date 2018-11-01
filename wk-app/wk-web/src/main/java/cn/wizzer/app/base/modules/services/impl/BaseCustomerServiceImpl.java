package cn.wizzer.app.base.modules.services.impl;

import cn.wizzer.framework.base.service.BaseServiceImpl;
import cn.wizzer.app.base.modules.models.base_customer;
import cn.wizzer.app.base.modules.services.BaseCustomerService;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.pager.Pager;
import org.nutz.ioc.loader.annotation.IocBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@IocBean(args = {"refer:dao"})
public class BaseCustomerServiceImpl extends BaseServiceImpl<base_customer> implements BaseCustomerService {
    public BaseCustomerServiceImpl(Dao dao) {
        super(dao);
    }

    public List<Map<String,Object>> getCustomerByM(Integer pagenumber,Integer pagesize) {
        /*
            @ReturnKey(key = "id", description = "客户ID")
            ,@ReturnKey(key = "customercode", description = "客户编号")
            ,@ReturnKey(key = "customername", description = "客户名称")
            ,@ReturnKey(key = "customeraddress", description = "客户地址")
            ,@ReturnKey(key = "customertel", description = "联系电话")
            ,@ReturnKey(key = "cusnum", description = "编号开头")
            ,@ReturnKey(key = "customerdecrib", description = "备注")}
            */
        Pager pager = new Pager(1);
        if (pagenumber != null && pagenumber.intValue() > 0) {
            pager.setPageNumber(pagenumber.intValue());
        }
        if (pagesize == null || pagesize > 10) {
            pager.setPageSize(10);
        }else{
            pager.setPageSize(pagesize.intValue());
        }
        List<base_customer> baseCustomerList = this.query(Cnd.NEW(),pager);
          List<Map<String,Object>> list = new ArrayList<>();
        for (base_customer baseCustomer : baseCustomerList) {
            Map<String,Object> map = new HashMap<>();
            map.put("id",baseCustomer.getId());
            map.put("customercode",baseCustomer.getCustomercode());
            map.put("customername",baseCustomer.getCustomername());
            map.put("customeraddress",baseCustomer.getCustomeraddress());
            map.put("customertel",baseCustomer.getCustomertel());
            map.put("cusnum",baseCustomer.getCusnum());
            map.put("customerdecrib",baseCustomer.getCustomerdecrib());
            list.add(map);
        }
        return list;
    }
}
