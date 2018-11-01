package cn.wizzer.app.sys.modules.services.impl;

import cn.wizzer.app.sys.modules.models.Sys_menu;
import cn.wizzer.app.sys.modules.models.Sys_role;
import cn.wizzer.app.sys.modules.models.Sys_unit;
import cn.wizzer.app.sys.modules.models.Sys_user;
import cn.wizzer.app.sys.modules.services.SysUnitService;
import cn.wizzer.framework.base.service.BaseServiceImpl;
import org.apache.shiro.SecurityUtils;
import org.nutz.aop.interceptor.ioc.TransAop;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.sql.Sql;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by wizzer on 2016/12/22.
 */
@IocBean(args = {"refer:dao"})
public class SysUnitServiceImpl extends BaseServiceImpl<Sys_unit> implements SysUnitService {
    public SysUnitServiceImpl(Dao dao) {
        super(dao);
    }

    /**
     * 新增单位
     *
     * @param unit
     * @param pid
     */
    @Aop(TransAop.READ_COMMITTED)
    public void save(Sys_unit unit, String pid) {
        String path = "";
        if (!Strings.isEmpty(pid)) {
            Sys_unit pp = this.fetch(pid);
            path = pp.getPath();
        }
        unit.setPath(getSubPath("sys_unit", "path", path));
        unit.setParentId(pid);
        dao().insert(unit);
        if (!Strings.isEmpty(pid)) {
            this.update(Chain.make("hasChildren", true), Cnd.where("id", "=", pid));
        }
    }

    /**
     * 级联删除单位
     *
     * @param unit
     */
    @Aop(TransAop.READ_COMMITTED)
    public void deleteAndChild(Sys_unit unit) {
        dao().execute(Sqls.create("delete from sys_unit where path like @path").setParam("path", unit.getPath() + "%"));
        dao().execute(Sqls.create("delete from sys_user_unit where unitId=@id or unitId in(SELECT id FROM sys_unit WHERE path like @path)").setParam("id", unit.getId()).setParam("path", unit.getPath() + "%"));
        dao().execute(Sqls.create("delete from sys_role where unitid=@id or unitid in(SELECT id FROM sys_unit WHERE path like @path)").setParam("id", unit.getId()).setParam("path", unit.getPath() + "%"));
        if (!Strings.isEmpty(unit.getParentId())) {
            int count = count(Cnd.where("parentId", "=", unit.getParentId()));
            if (count < 1) {
                dao().execute(Sqls.create("update sys_unit set hasChildren=0 where id=@pid").setParam("pid", unit.getParentId()));
            }
        }
    }


    /**
     * 通过parentid找到根id
     * @param parentid 上级id
     */
    public Sys_unit getRoot(String parentid){
        if(!Strings.isBlank(parentid)){
            Sys_unit unit = this.fetch(parentid);
            if(unit!=null){
                if(!Strings.isEmpty(unit.getParentId())){
                    getRoot(unit.getParentId());
                }else{
                    return unit;
                }
            }
        }
        return null;
    }

    public boolean isSysAdmin(Set<String> ids) {
        //当前用户只能看到自己的菜单
        Sys_user currentUser = (Sys_user) SecurityUtils.getSubject().getPrincipal();
        //角色关联的组织id
        if (currentUser != null) {
            //得到当前用户的所有角色
            List<Sys_role> roles = currentUser.getRoles();
            if (roles.size() > 0) {
                for (Sys_role role : roles) {
                    //是否是管理员
                    if(role.getCode().toString().equals("sysadmin")){
                        ids.clear();
                        return true;
                    }
                    String roleUnitId = role.getUnitid();
                    if(!Strings.isBlank(roleUnitId)){
                        ids.add(roleUnitId);
                    }
                }
            }
        }
        if (ids.size() > 0) {
            Iterator<String> iterator = ids.iterator();
            while (iterator.hasNext()) {
                String id = iterator.next();
                Sys_unit sysUnit = this.query(Cnd.where("id", "=", id)).get(0);
                String parentId = sysUnit.getParentId();
                //判断组织的父级id是否已经在于ids中
                if (sysUnit != null) {
                    //已经存在
                    if (isExistParentId(sysUnit, parentId, ids)) {
                        iterator.remove();
                    }
                }
            }
        }
        return false;
    }
    private boolean isExistParentId(Sys_unit sysUnit, String parentId, Set<String> ids) {
        if(parentId == null || "".equals(parentId.trim())){
            return false;
        }
        //上一级就在ids中
        if (ids.contains(parentId)) {
            return true;
        } else {
            //不是则继续执行此方法
            sysUnit = this.query(Cnd.where("id", "=", parentId)).get(0);
            parentId = sysUnit.getParentId();
            isExistParentId(sysUnit, parentId, ids);
        }
        return false;
    }
}
