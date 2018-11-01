package cn.wizzer.app.sys.modules.services.impl;

import cn.wizzer.app.sys.modules.models.Sys_dict;
import cn.wizzer.app.sys.modules.services.SysDictService;
import cn.wizzer.framework.base.service.BaseServiceImpl;
import org.nutz.aop.interceptor.ioc.TransAop;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.pager.Pager;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by mac on 2017/1/19.
 */
@IocBean(args = {"refer:dao"})
public class SysDictServiceImpl extends BaseServiceImpl<Sys_dict> implements SysDictService {
    public SysDictServiceImpl(Dao dao) {
        super(dao);
    }


    /**
     * 通过code获取name
     *
     * @param code
     * @return
     */
    //@Cacheable
    public String getNameByCode(String code) {
        Sys_dict dict = this.fetch(Cnd.where("code", "=", code));
        return dict == null ? "" : dict.getName();
    }

    public String getIdByCode(String code) {
        Sys_dict dict = this.fetch(Cnd.where("code", "=", code));
        return dict == null ? "" : dict.getId();
    }

    /**
     * 通过id获取name
     *
     * @param id
     * @return
     */
    //@Cacheable
    public String getNameById(String id) {
        Sys_dict dict = this.fetch(id);
        return dict == null ? "" : dict.getName();
    }

    /**
     * 通过name获取id
     *
     * @param name
     * @return
     */
    //@Cacheable
    public String getIdByName(String name)
    {
        Sys_dict dict=this.fetch(Cnd.where("name","=",name));
        return  dict ==null?"":dict.getId();

    }

    /**
     * 通过name和code获取id
     *
     * @param name
     * @param code
     * @return
     */
    //@Cacheable
    public String getIdByNameAndCode(String name,String code)
    {
        Sys_dict dict=this.fetch(Cnd.where("name","=",name).and("code","=",code));
        return  dict ==null?"":dict.getId();

    }

    /**
     * 通过树path获取下级列表
     *
     * @param path
     * @return
     */
    //@Cacheable
    public List<Sys_dict> getSubListByPath(String path) {
        return this.query(Cnd.where("path", "like", Strings.sNull(path) + "____").asc("location"));
    }

    /**
     * 通过父id获取下级列表
     *
     * @param id
     * @return
     */
    //@Cacheable
    public List<Sys_dict> getSubListById(String id) {
        return this.query(Cnd.where("parentId", "=", Strings.sNull(id)).asc("location"));
    }

    /**
     * 通过code获取下级列表
     *
     * @param code
     * @return
     */
    //@Cacheable
    public List<Sys_dict> getSubListByCode(String code) {
        Sys_dict dict = this.fetch(Cnd.where("code", "=", code));
        return dict == null ? new ArrayList<>() : this.query(Cnd.where("parentId", "=", Strings.sNull(dict.getId())).asc("location"));
    }

    /**
     * 通过path获取下级map
     *
     * @param path
     * @return
     */
    //@Cacheable
    public Map getSubMapByPath(String path) {
        return this.getMap(Sqls.create("select code,name from sys_dict where path like @path order by location asc").setParam("path", path + "____"));
    }

    /**
     * 通过id获取下级map
     *
     * @param id
     * @return
     */
    //@Cacheable
    public Map getSubMapById(String id) {
        return this.getMap(Sqls.create("select code,name from sys_dict where parentId = @id order by location asc").setParam("id", id));
    }

    /**
     * 通过code获取下级map
     *
     * @param code
     * @return
     */
    //@Cacheable
    public Map getSubMapByCode(String code) {
        Sys_dict dict = this.fetch(Cnd.where("code", "=", code));
        return dict == null ? new HashMap() : this.getMap(Sqls.create("select code,name from sys_dict where parentId = @id order by location asc").setParam("id", dict.getId()));
    }

    /**
     * 新增单位
     *
     * @param dict
     * @param pid
     */
    @Aop(TransAop.READ_COMMITTED)
    public void save(Sys_dict dict, String pid) {
        String path = "";
        if (!Strings.isEmpty(pid)) {
            Sys_dict pp = this.fetch(pid);
            path = pp.getPath();
        }
        dict.setPath(getSubPath("sys_dict", "path", path));
        dict.setParentId(pid);
        dao().insert(dict);
        if (!Strings.isEmpty(pid)) {
            this.update(Chain.make("hasChildren", true), Cnd.where("id", "=", pid));
        }
    }

    /**
     * 级联删除单位
     *
     * @param dict
     */
    @Aop(TransAop.READ_COMMITTED)
    public void deleteAndChild(Sys_dict dict) {
        dao().execute(Sqls.create("delete from sys_dict where path like @path").setParam("path", dict.getPath() + "%"));
        if (!Strings.isEmpty(dict.getParentId())) {
            int count = count(Cnd.where("parentId", "=", dict.getParentId()));
            if (count < 1) {
                dao().execute(Sqls.create("update sys_dict set hasChildren=0 where id=@pid").setParam("pid", dict.getParentId()));
            }
        }
    }

    public String getDictOtypeId() {
        Sys_dict sysDict = this.fetch(Cnd.where("name", "=", "航线运输"));
        if(sysDict != null){
            return sysDict.getId();
        }
        return "";
    }

    public List<Map<String, Object>> bindDDLbyM(String path,Integer pagenumber, Integer pagesize) {
        Pager pager = new Pager(1);
        if (pagenumber != null && pagenumber.intValue() > 0) {
            pager.setPageNumber(pagenumber.intValue());
        }
        if (pagesize == null || pagesize > 10) {
            pager.setPageSize(10);
        }else{
            pager.setPageSize(pagesize.intValue());
        }
        List<Sys_dict> sysDicts = this.query(Cnd.where("path", "like", path + "%").and("path", "<>", path),pager);
        List<Map<String, Object>> list = new ArrayList<>();
        for (Sys_dict sysDict : sysDicts) {
            Map<String,Object> map = new HashMap<>();
            /*@ReturnKey(key = "id", description = "id")
                    ,@ReturnKey(key = "name", description = "名称")
                    ,@ReturnKey(key = "code", description = "机构编码")
                    ,@ReturnKey(key = "location", description = "排序字段")
                    ,@ReturnKey(key = "hasChildren", description = "是否有子节点")}*/
            map.put("id",sysDict.getId());
            map.put("name",sysDict.getName());
            map.put("code",sysDict.getCode());
            map.put("location",sysDict.getLocation());
            map.put("hasChildren",sysDict.isHasChildren());
            list.add(map);
        }
        return list;
    }
}
