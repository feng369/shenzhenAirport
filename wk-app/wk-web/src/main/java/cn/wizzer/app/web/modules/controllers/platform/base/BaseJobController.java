package cn.wizzer.app.web.modules.controllers.platform.base;

import cn.wizzer.app.base.modules.services.BaseCnctobjService;
import cn.wizzer.app.sys.modules.services.SysUnitService;
import cn.wizzer.app.sys.modules.services.SysUserService;
import cn.wizzer.app.web.modules.controllers.open.api.userpermission.UserpermissionController;
import cn.wizzer.framework.base.Result;
import cn.wizzer.app.web.commons.slog.annotation.SLog;
import cn.wizzer.framework.page.datatable.DataTableColumn;
import cn.wizzer.framework.page.datatable.DataTableOrder;
import cn.wizzer.framework.util.StringUtil;
import cn.wizzer.app.base.modules.models.base_job;
import cn.wizzer.app.base.modules.services.BaseJobService;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.dao.Cnd;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@IocBean
@At("/platform/base/job")
public class BaseJobController{
    private static final Log log = Logs.get();
    @Inject
    private BaseJobService baseJobService;

    @Inject
    private SysUnitService unitService;

    @Inject
    private SysUserService sysUserService;

    @Inject
    private BaseCnctobjService baseCnctobjService;

    @At("")
    @Ok("beetl:/platform/base/job/index.html")
    @RequiresPermissions("platform.base.job.select")
    public Object index() {
        Cnd cnd=Cnd.NEW();
        cnd.exps("parentId", "=", "").or("parentId", "is", null);
        cnd=baseCnctobjService.unitDataPermission(cnd);
        cnd.asc("path");
        return baseJobService.query(cnd,"unit");
    }

    @At("/data")
    @Ok("json")
    @RequiresPermissions("platform.base.job.select")
    public Object data(@Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns) {
		Cnd cnd = Cnd.NEW();
        UserpermissionController userpermission=new UserpermissionController();
		cnd = baseCnctobjService.unitDataPermission(cnd);
    	return baseJobService.data(length, start, draw, order, columns, cnd, null);
    }

    @At("/add")
    @Ok("beetl:/platform/base/job/add.html")
    @RequiresPermissions("platform.base.job")
    public Object add(@Param("pid") String pid) {
        return Strings.isBlank(pid) ? null : baseJobService.fetch(pid);

    }

    @At("/addDo")
    @Ok("json")
    @RequiresPermissions("platform.base.job.add")
    @SLog(tag = "base_job", msg = "${args[0].id}")
    public Object addDo(@Param("..")base_job baseJob,  @Param("parentId") String parentId,HttpServletRequest req) {
		try {
            baseJobService.save(baseJob, parentId);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }

    @At("/edit/?")
    @Ok("beetl:/platform/base/job/edit.html")
    @RequiresPermissions("platform.base.job")
    public Object edit(String id,HttpServletRequest req) {
        try{
            base_job job = baseJobService.fetch(id);
            if (job != null) {
                req.setAttribute("unit", unitService.fetch(job.getUnitid()));
                req.setAttribute("parentJob", baseJobService.fetch(job.getParentId()));
                req.setAttribute("creater",sysUserService.fetch(job.getCreater()));
                req.setAttribute("opby",sysUserService.fetch(job.getOpBy()));
            }
            return job;
        }catch (Exception e) {
            return Result.error("system.error");
        }

    }

    @At("/editDo")
    @Ok("json")
    @RequiresPermissions("platform.base.job.edit")
    @SLog(tag = "base_job", msg = "${args[0].id}")
    public Object editDo(@Param("..")base_job baseJob, HttpServletRequest req) {
		try {
            baseJob.setOpBy(StringUtil.getUid());
			baseJob.setOpAt((int) (System.currentTimeMillis() / 1000));
			baseJobService.update(baseJob);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }

    @At({"/delete/?", "/delete"})
    @Ok("json")
    @RequiresPermissions("platform.base.job.delete")
    @SLog(tag = "base_job", msg = "${req.getAttribute('id')}")
    public Object delete(String id, @Param("ids")  String[] ids, HttpServletRequest req) {
        try {
            base_job job = baseJobService.fetch(id);
            req.setAttribute("name", job.getJobname());

            baseJobService.deleteAndChild(job);
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }

    @At("/detail/?")
    @Ok("beetl:/platform/base/job/detail.html")
    @RequiresPermissions("platform.base.job.select")
	public Object detail(String id, HttpServletRequest req) {
        base_job job = baseJobService.fetch(id);
        if(job!=null){
            req.setAttribute("unit", unitService.fetch(job.getUnitid()));
            req.setAttribute("parentJob", baseJobService.fetch(job.getParentId()));
            req.setAttribute("creater",sysUserService.fetch(job.getCreater()));
            req.setAttribute("opby",sysUserService.fetch(job.getOpBy()));
        }
        return job;
    }

    @At("/child/?")
    @Ok("beetl:/platform/base/job/child.html")
    @RequiresPermissions("platform.base.job")
    public Object child(String id) {
        return baseJobService.query(Cnd.where("parentId", "=", id).asc("path"),"unit");
    }

    @At
    @Ok("json")
    @RequiresPermissions("platform.base.job.select")
    public Object tree(@Param("pid") String pid) {
        List<base_job> list = baseJobService.query(Cnd.where("parentId", "=", Strings.sBlank(pid)).asc("path"));
        List<Map<String, Object>> tree = new ArrayList<>();
        for (base_job job : list) {
            Map<String, Object> obj = new HashMap<>();
            obj.put("id", job.getId());
            obj.put("text", job.getJobname());
            obj.put("children", job.isHasChildren());
            tree.add(obj);
        }
        return tree;
    }

    @At("/countByNum")
    @Ok("json:full")
    @RequiresPermissions("platform.base.job.select")
    public Object countByNum(@Param("num")String num) {
        return baseJobService.count("base_job",Cnd.where("jobnum","=",num));
    }

}
