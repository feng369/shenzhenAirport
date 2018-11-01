package cn.wizzer.app.base.modules.services.impl;

import cn.wizzer.framework.base.service.BaseServiceImpl;
import cn.wizzer.app.base.modules.models.base_post;
import cn.wizzer.app.base.modules.services.BasePostService;
import org.nutz.aop.interceptor.ioc.TransAop;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;

@IocBean(args = {"refer:dao"})
public class BasePostServiceImpl extends BaseServiceImpl<base_post> implements BasePostService {
    public BasePostServiceImpl(Dao dao) {
        super(dao);
    }
    /**
     * 新增单位
     *
     * @param post
     * @param pid
     */
    @Aop(TransAop.READ_COMMITTED)
    public void save(base_post post, String pid) {
        String path = "";
        if (!Strings.isEmpty(pid)) {
            base_post pp = this.fetch(pid);
            path = pp.getPath();
        }
        post.setPath(getSubPath("base_post", "path", path));
        post.setParentId(pid);
        dao().insert(post);
        if (!Strings.isEmpty(pid)) {
            this.update(Chain.make("hasChildren", true), Cnd.where("id", "=", pid));
        }
    }

    /**
     * 级联删除单位
     *
     * @param post
     */
    @Aop(TransAop.READ_COMMITTED)
    public void deleteAndChild(base_post post) {
        dao().execute(Sqls.create("delete from base_post where path like @path").setParam("path", post.getPath() + "%"));
        if (!Strings.isEmpty(post.getParentId())) {
            int count = count(Cnd.where("parentId", "=", post.getParentId()));
            if (count < 1) {
                dao().execute(Sqls.create("update base_post set hasChildren=0 where id=@pid").setParam("pid", post.getParentId()));
            }
        }
    }

    /**
     * 更新部门数据
     *
     * @param post
     */
    @Aop(TransAop.READ_COMMITTED)
    public void update(base_post post) {
        dao().updateIgnoreNull(post);
        if (!Strings.isEmpty(post.getParentId())) {
            this.update(Chain.make("hasChildren", true), Cnd.where("id", "=", post.getParentId()));
        }
    }
}
