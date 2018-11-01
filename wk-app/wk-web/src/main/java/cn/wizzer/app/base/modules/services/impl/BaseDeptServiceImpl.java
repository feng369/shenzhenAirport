package cn.wizzer.app.base.modules.services.impl;

import cn.wizzer.framework.base.service.BaseServiceImpl;
import cn.wizzer.app.base.modules.models.base_dept;
import cn.wizzer.app.base.modules.services.BaseDeptService;
import org.nutz.aop.interceptor.ioc.TransAop;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;

@IocBean(args = {"refer:dao"})
public class BaseDeptServiceImpl extends BaseServiceImpl<base_dept> implements BaseDeptService {
    public BaseDeptServiceImpl(Dao dao) {
        super(dao);
    }

    /**
     * 新增单位
     *
     * @param dept
     * @param pid
     */
    @Aop(TransAop.READ_COMMITTED)
    public void save(base_dept dept, String pid) {
        String path = "";
        if (!Strings.isEmpty(pid)) {
            base_dept pp = this.fetch(pid);
            path = pp.getPath();
        }
        dept.setPath(getSubPath("base_dept", "path", path));
        dept.setParentId(pid);
        dao().insert(dept);
        if (!Strings.isEmpty(pid)) {
            this.update(Chain.make("hasChildren", true), Cnd.where("id", "=", pid));
        }
    }

    /**
     * 级联删除单位
     *
     * @param dept
     */
    @Aop(TransAop.READ_COMMITTED)
    public void deleteAndChild(base_dept dept) {
        dao().execute(Sqls.create("delete from base_dept where path like @path").setParam("path", dept.getPath() + "%"));
       if (!Strings.isEmpty(dept.getParentId())) {
            int count = count(Cnd.where("parentId", "=", dept.getParentId()));
            if (count < 1) {
                dao().execute(Sqls.create("update base_dept set hasChildren=0 where id=@pid").setParam("pid", dept.getParentId()));
            }
        }
    }

    /**
     * 更新部门数据
     *
     * @param dept
     */
    @Aop(TransAop.READ_COMMITTED)
    public void update(base_dept dept) {
        dao().updateIgnoreNull(dept);
        if (!Strings.isEmpty(dept.getParentId())) {
            this.update(Chain.make("hasChildren", true), Cnd.where("id", "=", dept.getParentId()));
        }
    }
}
