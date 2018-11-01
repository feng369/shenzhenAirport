package cn.wizzer.app.base.modules.services.impl;

import cn.wizzer.app.sys.modules.models.Sys_dict;
import cn.wizzer.app.sys.modules.services.SysDictService;
import cn.wizzer.framework.base.Result;
import cn.wizzer.framework.base.ValidatException;
import cn.wizzer.framework.base.service.BaseServiceImpl;
import cn.wizzer.app.base.modules.models.base_place;
import cn.wizzer.app.base.modules.services.BasePlaceService;
import cn.wizzer.framework.page.OffsetPager;
import cn.wizzer.framework.page.datatable.DataTableColumn;
import cn.wizzer.framework.page.datatable.DataTableOrder;
import cn.wizzer.framework.util.StringUtil;
import com.sun.prism.impl.Disposer;
import org.nutz.aop.interceptor.ioc.TransAop;
import org.nutz.dao.*;
import org.nutz.dao.entity.Record;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.util.Daos;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@IocBean(args = {"refer:dao"})
public class BasePlaceServiceImpl extends BaseServiceImpl<base_place> implements BasePlaceService {
    public BasePlaceServiceImpl(Dao dao) {
        super(dao);
    }

    //    public String getArea(String areaId)
//    {
//
//    }
    @Inject
    private SysDictService sysDictService;

    public List<base_place> querydata(Condition cnd, String linkName) {
        List<base_place> list = this.dao().query(this.getEntityClass(), cnd);
        Sys_dict dict = sysDictService.fetch(Cnd.where("id", "=", list.get(0).getAreaId()));
        list.get(0).setArea(dict);

        dict = sysDictService.fetch(Cnd.where("id", "=", list.get(0).getTypeId()));
        list.get(0).setType(dict);


        dict = sysDictService.fetch(Cnd.where("id", "=", list.get(0).getCtrlId()));
        list.get(0).setCtrl(dict);

        if (!Strings.isBlank(linkName)) {
            this.dao().fetchLinks(list, linkName);
        }
        return list;
    }

    public Map getPlaceByMobile(String id, Integer pagenumber, Integer pagesize) {
        Map map = new HashMap<>();
        base_place basePlace = this.fetch(Cnd.where("id", "=", id));
        if (basePlace != null) {
            Pager pager = new Pager
                    (1);
            if (pagenumber != null && pagenumber.intValue() > 0) {
                pager.setPageNumber(pagenumber.intValue());
            }
            if (pagesize == null || pagesize > 10) {
                pager.setPageSize(10);
            } else {
                pager.setPageSize(pagesize.intValue());
            }
            basePlace = this.fetchLinks(basePlace, "airport|area|customer|type|ctrl|person");
            map.put("id", basePlace.getId());
            map.put("customername", basePlace.getCustomer() == null ? "" : basePlace.getCustomer().getCustomername());
            map.put("customerId", basePlace.getCustomer() == null ? "" : basePlace.getCustomer().getId());
            map.put("areaname", basePlace.getArea() == null ? basePlace.getArea().getName() : "");
            map.put("areaId", basePlace.getArea() == null ? basePlace.getArea().getId() : "");
            map.put("typename", basePlace.getType() == null ? "" : basePlace.getType().getName());
            map.put("typeId", basePlace.getType() == null ? "" : basePlace.getType().getName());
            map.put("placename", basePlace.getPlacename());
            map.put("placecode", basePlace.getPlacecode());
            map.put("path", basePlace.getPath());
            map.put("position", basePlace.getPosition());

            map.put("ctrlname", basePlace.getCtrl() == null ? "" : basePlace.getCtrl().getName());
            map.put("ctrlId", basePlace.getCtrl() == null ? "" : basePlace.getCtrl().getId());
            map.put("personname", basePlace.getPerson() == null ? "" : basePlace.getPerson().getPersonname());
            map.put("personId", basePlace.getPerson() == null ? "" : basePlace.getPerson().getId());
            map.put("telephone", basePlace.getTelephone());
            map.put("address", basePlace.getAddress());
            map.put("describ", basePlace.getDescrib());
        }
        return map;
    }

    @Aop(TransAop.READ_COMMITTED)
    public Result updatePlaceByM(String id, String placecode, String parentId, String path, String airportId, String customerId, String areaId, String typeId, String placename, String ctrlId, String personId, String telephone, String address, String position, String describ) {
        base_place place = null;
        if (!Strings.isBlank(id)) {
            place = this.fetch(id);
        }
        if (place == null) {
            id = null;
            place = new base_place();
        }
        if (!Strings.isBlank(ctrlId))
            place.setCtrlId(ctrlId);
        if (!Strings.isBlank(typeId))
            place.setTypeId(typeId);
        if (!Strings.isBlank(address))
            place.setAddress(address);
        if (!Strings.isBlank(airportId))
            place.setAirportId(airportId);
        if (!Strings.isBlank(areaId))
            place.setAreaId(areaId);
        if (!Strings.isBlank(customerId))
            place.setCustomerId(customerId);
        if (!Strings.isBlank(describ))
            place.setDescrib(describ);
        if (!Strings.isBlank(parentId))
            place.setParentId(parentId);
        if (!Strings.isBlank(path))
            place.setPath(path);
        if (!Strings.isBlank(personId))
            place.setPersonId(personId);
        if (!Strings.isBlank(placecode))
            place.setPlacecode(placecode);
        if (!Strings.isBlank(placename))
            place.setPlacename(placename);
        if (!Strings.isBlank(position))
            place.setPosition(position);
        if (!Strings.isBlank(telephone))
            place.setTelephone(telephone);
        if (!Strings.isBlank(id)) {
            this.update(place);
            return Result.success("更新成功");
        } else {
            this.insert(place);
            return Result.success("新增成功");
        }
    }

    public List<Map<String, Object>> getPlaceListByMobile(String keyword, String airportId) {
        List<Map<String, Object>> list = new ArrayList<>();
        if (!Strings.isBlank(keyword)) {
            keyword = keyword.replace("'", "");
        }
        //   cnd = cnd.where(Cnd.exps("stakenum","LIKE","%"+name+"%").or("stakename","LIKE","%"+name+"%"));
        List<base_place> places = this.query(Cnd.where(Cnd.exps("placename", "LIKE", "%" + keyword + "%").or("placecode", "LIKE", "%" + keyword + "%")).and("airportId", "=", airportId).orderBy("placecode","ASC"));
        if (places.size() > 0) {
            for (base_place place : places) {
                Map<String, Object> map = new HashMap<>();
                map.put("placeId", place.getId());
                map.put("placeName", place.getPlacename());
                list.add(map);
            }
        }
        return list;
    }

    public List<Map<String, Object>> bindDDLByMobile(String keyword, String airportId, Integer pagenumber, Integer pagesize) {
        pagesize = 30;//IOS要求
        List<Map<String, Object>> list = new ArrayList<>();
        if (!Strings.isBlank(airportId)) {
            List<base_place> place;
            if(keyword==null){
                place = this.query(Cnd.where("airportId", "=", airportId).orderBy("placecode", "ASC"), "airport|area|customer|type|ctrl|person", StringUtil.getPagerObject(pagenumber, pagesize));
            }else{
                place = this.query(Cnd.where("airportId", "=", airportId).and(Cnd.exps("placename","like","%" + keyword + "%").or("placecode","like","%" + keyword + "%")).orderBy("placecode", "ASC"), "airport|area|customer|type|ctrl|person", StringUtil.getPagerObject(pagenumber, pagesize));
            }

            if (place.size() > 0) {
                for (base_place basePlace : place) {
                    Map<String, Object> map = new HashMap<>();
//                    @ReturnKey(key = "id", description = "地点id"),
//            @ReturnKey(key = "placename", description = "地点名称")
                    map.put("id",basePlace.getId());
                    map.put("placename",StringUtil.nullStr2Trim(basePlace.getPlacename()));
                    list.add(map);
                }
            }
            return list;
        }
        throw new ValidatException("参数不能为空!");
    }

    public NutMap getPlaceList(int length, int start, int draw, List<DataTableOrder> orders, List<DataTableColumn> columns, Cnd cnd) {
        NutMap re = new NutMap();
        String sqlStr = " SELECT bp.id,bp.telephone,bp.placecode,bp.placename,p.personname,su.name FROM base_place bp LEFT JOIN  base_person  p ON bp.personId = p.id LEFT JOIN sys_unit su ON bp.unitId = su.id WHERE 1= 1  " ;
       sqlStr += cnd.toString().replace("WHERE","and").replace("airportid","bp.airportid");
        if (orders != null && orders.size() > 0) {
            for (DataTableOrder order : orders) {
                DataTableColumn col = columns.get(order.getColumn());
                sqlStr += " ORDER BY " + Sqls.escapeSqlFieldValue(col.getData()).toString() + " " +order.getDir() + " ";
            }
        }
        Sql sql = Sqls.queryRecord(sqlStr);
        Pager pager = new OffsetPager(start, length);
        sql.setPager(pager);
        re.put("recordsFiltered", Daos.queryCount(dao(), sql));
        dao().execute(sql);
        List list = sql.getList(Record.class);
        re.put("data", list);
        re.put("draw", draw);
        re.put("recordsTotal", length);
        return re;
    }

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
        List<base_place> list = this.dao().query(this.getEntityClass(), cnd, pager);
        if (!Strings.isBlank(linkName)) {
            if (subCnd != null)
                this.dao().fetchLinks(list, linkName, subCnd);
            else
                this.dao().fetchLinks(list, linkName);
        }
        for (int i = 0; i < list.size(); i++) {
            try {
                base_place entity = list.get(i);

                Sys_dict dict = sysDictService.fetch(Cnd.where("id", "=", entity.getAreaId()));
                entity.setArea(dict);


                dict = sysDictService.fetch(Cnd.where("id", "=", entity.getTypeId()));
                entity.setType(dict);


                dict = sysDictService.fetch(Cnd.where("id", "=", entity.getCtrlId()));
                entity.setCtrl(dict);


                list.set(i, entity);

            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
        re.put("data", list);
        re.put("draw", draw);
        re.put("recordsTotal", length);
        return re;
    }


    /**
     * 新增
     *
     * @param place
     * @param pid
     */
    @Aop(TransAop.READ_COMMITTED)
    public void save(base_place place, String pid) {
        String path = "";
        if (!Strings.isEmpty(pid)) {
            base_place pp = this.fetch(pid);
            path = pp.getPath();
        }
        place.setPath(getSubPath("base_place", "path", path));
        place.setParentId(pid);
        dao().insert(place);
        if (!Strings.isEmpty(pid)) {
            this.update(Chain.make("hasChildren", true), Cnd.where("id", "=", pid));
        }
    }

    /**
     * 级联删除地点
     *
     * @param place
     */
    @Aop(TransAop.READ_COMMITTED)
    public void deleteAndChild(base_place place) {
        dao().execute(Sqls.create("delete from base_place where path like @path").setParam("path", place.getPath() + "%"));
        if (!Strings.isEmpty(place.getParentId())) {
            int count = count(Cnd.where("parentId", "=", place.getParentId()));
            if (count < 1) {
                dao().execute(Sqls.create("update base_dept set hasChildren=0 where id=@pid").setParam("pid", place.getParentId()));
            }
        }
    }

    /**
     * 更新地点数据
     *
     * @param place
     */
    @Aop(TransAop.READ_COMMITTED)
    public void update(base_place place) {
        dao().updateIgnoreNull(place);
        if (!Strings.isEmpty(place.getParentId())) {
            this.update(Chain.make("hasChildren", true), Cnd.where("id", "=", place.getParentId()));
        }
    }
}
