package cn.wizzer.app.base.modules.services.impl;

import cn.wizzer.framework.base.service.BaseServiceImpl;
import cn.wizzer.app.base.modules.models.base_job;
import cn.wizzer.app.base.modules.services.BaseJobService;
import org.nutz.aop.interceptor.ioc.TransAop;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;

@IocBean(args = {"refer:dao"})
public class BaseJobServiceImpl extends BaseServiceImpl<base_job> implements BaseJobService {
    public BaseJobServiceImpl(Dao dao) {
        super(dao);
    }

    /**
     * 新增单位
     *
     * @param job
     * @param pid
     */
    @Aop(TransAop.READ_COMMITTED)
    public void save(base_job job, String pid) {
        String path = "";
        if (!Strings.isEmpty(pid)) {
            base_job pp = this.fetch(pid);
            path = pp.getPath();
        }
        job.setPath(getSubPath("base_job", "path", path));
        job.setParentId(pid);
        dao().insert(job);
        if (!Strings.isEmpty(pid)) {
            this.update(Chain.make("hasChildren", true), Cnd.where("id", "=", pid));
        }
    }

    /**
     * 级联删除单位
     *
     * @param job
     */
    @Aop(TransAop.READ_COMMITTED)
    public void deleteAndChild(base_job job) {
        dao().execute(Sqls.create("delete from base_job where path like @path").setParam("path", job.getPath() + "%"));
        if (!Strings.isEmpty(job.getParentId())) {
            int count = count(Cnd.where("parentId", "=", job.getParentId()));
            if (count < 1) {
                dao().execute(Sqls.create("update base_job set hasChildren=0 where id=@pid").setParam("pid", job.getParentId()));
            }
        }
    }

    /**
     * 更新职务数据
     *
     * @param job
     */
    @Aop(TransAop.READ_COMMITTED)
    public void update(base_job job) {
        dao().updateIgnoreNull(job);
        if (!Strings.isEmpty(job.getParentId())) {
            this.update(Chain.make("hasChildren", true), Cnd.where("id", "=", job.getParentId()));
        }
    }
}
