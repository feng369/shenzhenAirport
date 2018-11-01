package cn.wizzer.app.base.modules.services;

import cn.wizzer.framework.base.service.BaseService;
import cn.wizzer.app.base.modules.models.base_vehicle;
import cn.wizzer.framework.page.datatable.DataTableColumn;
import cn.wizzer.framework.page.datatable.DataTableOrder;
import org.nutz.dao.Cnd;
import org.nutz.lang.util.NutMap;

import java.util.List;
import java.util.Map;

public interface BaseVehicleService extends BaseService<base_vehicle>{
    NutMap dataCode(int length, int start, int draw, List<DataTableOrder> orders, List<DataTableColumn> columns,
                    Cnd cnd, String linkName, Cnd subCnd);

    void setBusybyM(String vehicleid);

    List<Map<String,Object>> getVehicleByMobile(Integer pagenumber, Integer pagesize);

}
