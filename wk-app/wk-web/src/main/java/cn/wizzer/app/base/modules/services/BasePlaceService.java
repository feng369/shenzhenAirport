package cn.wizzer.app.base.modules.services;

import cn.wizzer.framework.base.Result;
import cn.wizzer.framework.base.service.BaseService;
import cn.wizzer.app.base.modules.models.base_place;
import cn.wizzer.framework.page.datatable.DataTableColumn;
import cn.wizzer.framework.page.datatable.DataTableOrder;
import org.nutz.dao.Cnd;
import org.nutz.dao.Condition;
import org.nutz.lang.util.NutMap;

import java.util.List;
import java.util.Map;

public interface BasePlaceService extends BaseService<base_place>{

    NutMap dataCode(int length, int start, int draw, List<DataTableOrder> orders, List<DataTableColumn> columns,
                           Cnd cnd, String linkName, Cnd subCnd);

    void save(base_place place, String pid);

    void deleteAndChild(base_place place);

    void update(base_place place);

    List<base_place> querydata(Condition cnd, String linkname);

    Map getPlaceByMobile(String id,Integer pagenumber,Integer pagesize);

    Result updatePlaceByM(String id, String placecode, String parentId, String path, String airportId, String customerId, String areaId, String typeId, String placename, String ctrlId, String personId, String telephone, String address, String position, String describ);

    List<Map<String,Object>> getPlaceListByMobile(String s, String keyword);

    List<Map<String,Object>> bindDDLByMobile(String keyword, String airportId, Integer pagenumber, Integer pagesize);

    NutMap getPlaceList(int length, int start, int draw, List<DataTableOrder> order, List<DataTableColumn> columns, Cnd cnd);
}
