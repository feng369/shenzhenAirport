package cn.wizzer.app.base.modules.services.impl;

import cn.wizzer.app.base.modules.models.base_vehicle;
import cn.wizzer.app.sys.modules.models.Sys_dict;
import cn.wizzer.app.sys.modules.services.SysDictService;
import cn.wizzer.framework.base.ValidatException;
import cn.wizzer.framework.base.service.BaseServiceImpl;
import cn.wizzer.app.base.modules.models.base_vehicle;
import cn.wizzer.app.base.modules.services.BaseVehicleService;
import cn.wizzer.framework.page.OffsetPager;
import cn.wizzer.framework.page.datatable.DataTableColumn;
import cn.wizzer.framework.page.datatable.DataTableOrder;
import org.nutz.aop.interceptor.ioc.TransAop;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.pager.Pager;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.trans.Trans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@IocBean(args = {"refer:dao"})
public class BaseVehicleServiceImpl extends BaseServiceImpl<base_vehicle> implements BaseVehicleService {
    public BaseVehicleServiceImpl(Dao dao) {
        super(dao);
    }

    @Inject
    private SysDictService sysDictService;

    public NutMap dataCode(int length, int start, int draw, List<DataTableOrder> orders, List<DataTableColumn> columns,
                           Cnd cnd, String linkName, Cnd subCnd) {
        NutMap re = new NutMap();
        if (orders != null && orders.size() > 0) {
            for (DataTableOrder order : orders) {
                DataTableColumn col = columns.get(order.getColumn());
                cnd.orderBy(Sqls.escapeSqlFieldValue(col.getData()).toString(), order.getDir());
            }
        }
        Pager pager = new OffsetPager(start, length);
        re.put("recordsFiltered", this.dao().count(this.getEntityClass(), cnd));
        List<base_vehicle> list = this.dao().query(this.getEntityClass(), cnd, pager);
        if (!Strings.isBlank(linkName)) {
            if (subCnd != null)
                this.dao().fetchLinks(list, linkName, subCnd);
            else
                this.dao().fetchLinks(list, linkName);
        }
        for(int i=0;i<list.size();i++){
            try {
                base_vehicle entity = list.get(i);

                Sys_dict dict=sysDictService.fetch(Cnd.where("id","=",entity.getVehicleareaId()));
                entity.setVehiclearea(dict);



                dict=sysDictService.fetch(Cnd.where("id","=",entity.getVehiclestateId()));
                entity.setVehiclestate(dict);


                dict=sysDictService.fetch(Cnd.where("id","=",entity.getVehicletypeId()));
                entity.setVehicletype(dict);


                list.set(i, entity);

            } catch (SecurityException e) {
                e.printStackTrace();
            }  catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
        re.put("data", list);
        re.put("draw", draw);
        re.put("recordsTotal", length);
        return re;
    }
    @Aop(TransAop.READ_COMMITTED)
    public void setBusybyM(String vehicleid) {
        String stateid=sysDictService.getIdByNameAndCode("在运","vehicleState.trans");
        if(Strings.isBlank(stateid)){
            throw new ValidatException("数据字典未找到在运[vehicleState.trans]数据状态!");
        }
        base_vehicle vehicle=this.fetch(vehicleid);
        vehicle.setVehiclestateId(stateid);
        this.updateIgnoreNull(vehicle);
    }

    public List<Map<String, Object>> getVehicleByMobile(Integer pagenumber, Integer pagesize) {
        Pager pager = new Pager(1);
        if (pagenumber != null && pagenumber.intValue() > 0) {
            pager.setPageNumber(pagenumber.intValue());
        }
        if (pagesize == null || pagesize > 10) {
            pager.setPageSize(10);
        }else{
            pager.setPageSize(pagesize.intValue());
        }
        List<base_vehicle> vehicles = this.query(Cnd.NEW(),"vehiclearea|vehiclestate|vehicletype",pager);
        List<Map<String,Object>> list = new ArrayList<>();
        for (base_vehicle vehicle : vehicles) {
            Map<String,Object> map = new HashMap<>();
            map.put("id",vehicle.getId());
            map.put("vehiclenum",vehicle.getVehiclenum());
            list.add(map);
        }
        return list;
    }
}
