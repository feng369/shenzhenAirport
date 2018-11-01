package cn.wizzer.app.web.modules.controllers.platform.base;

import cn.wizzer.app.base.modules.services.BaseCnctobjService;
import cn.wizzer.app.sys.modules.services.SysUnitService;
import cn.wizzer.app.web.modules.controllers.open.api.userpermission.UserpermissionController;
import cn.wizzer.framework.base.Result;
import cn.wizzer.app.web.commons.slog.annotation.SLog;
import cn.wizzer.framework.page.datatable.DataTableColumn;
import cn.wizzer.framework.page.datatable.DataTableOrder;
import cn.wizzer.framework.util.StringUtil;
import cn.wizzer.app.base.modules.models.base_post;
import cn.wizzer.app.base.modules.services.BasePostService;
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
@At("/platform/base/post")
public class BasePostController{
    private static final Log log = Logs.get();
    @Inject
    private BasePostService basePostService;


    @Inject
    private SysUnitService unitService;
    @Inject
    private BaseCnctobjService baseCnctobjService;

    @At("")
    @Ok("beetl:/platform/base/post/index.html")
    @RequiresPermissions("platform.base.post.select")
    public Object index() {
        Cnd cnd=Cnd.NEW();
        UserpermissionController userpermission=new UserpermissionController();
        cnd.exps("parentId", "=", "").or("parentId", "is", null);
        cnd= baseCnctobjService.unitDataPermission(cnd);
        cnd.asc("path");
        return basePostService.query(cnd,"unit");
    }

    @At("/data")
    @Ok("json")
    @RequiresPermissions("platform.base.post.select")
    public Object data(@Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns) {
		Cnd cnd = Cnd.NEW();
        UserpermissionController userpermission=new UserpermissionController();
        cnd.exps("parentId", "=", "").or("parentId", "is", null);
        cnd= baseCnctobjService.unitDataPermission(cnd);
        cnd.asc("path");
    	return basePostService.data(length, start, draw, order, columns, cnd, null);
    }

    @At("/add")
    @Ok("beetl:/platform/base/post/add.html")
    @RequiresPermissions("platform.base.post")
    public Object add(@Param("pid") String pid) {
        return Strings.isBlank(pid) ? null : basePostService.fetch(pid);
    }

    @At("/addDo")
    @Ok("json")
    @RequiresPermissions("platform.base.post.add")
    @SLog(tag = "base_post", msg = "${args[0].id}")
    public Object addDo(@Param("..")base_post basePost,  @Param("parentId") String parentId,HttpServletRequest req) {
        try {
            basePostService.save(basePost, parentId);
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }

    @At("/edit/?")
    @Ok("beetl:/platform/base/post/edit.html")
    @RequiresPermissions("platform.base.post")
    public Object edit(String id,HttpServletRequest req) {
        try{
            base_post post = basePostService.fetch(id);
            if (post != null) {
                req.setAttribute("unit", unitService.fetch(post.getUnitid()));
                req.setAttribute("parentJob", basePostService.fetch(post.getParentId()));
            }
            return post;
        }catch (Exception e) {
            return Result.error("system.error");
        }
    }

    @At("/editDo")
    @Ok("json")
    @RequiresPermissions("platform.base.post.edit")
    @SLog(tag = "base_post", msg = "${args[0].id}")
    public Object editDo(@Param("..")base_post basePost, HttpServletRequest req) {
		try {
            basePost.setOpBy(StringUtil.getUid());
			basePost.setOpAt((int) (System.currentTimeMillis() / 1000));
			basePostService.update(basePost);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }

    @At({"/delete/?", "/delete"})
    @Ok("json")
    @RequiresPermissions("platform.base.post.delete")
    @SLog(tag = "base_post", msg = "${req.getAttribute('id')}")
    public Object delete(String id, @Param("ids")  String[] ids, HttpServletRequest req) {
        try {
            base_post post = basePostService.fetch(id);
            req.setAttribute("name", post.getPostname());

            basePostService.deleteAndChild(post);
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }

    @At("/detail/?")
    @Ok("beetl:/platform/base/post/detail.html")
    @RequiresPermissions("platform.base.post.select")
	public Object detail(String id, HttpServletRequest req) {
        base_post post = basePostService.fetch(id);
        if(post!=null){
            req.setAttribute("unit", unitService.fetch(post.getUnitid()));
            req.setAttribute("parentPost", basePostService.fetch(post.getParentId()));
        }
        return post;
    }

    @At("/child/?")
    @Ok("beetl:/platform/base/post/child.html")
    @RequiresPermissions("platform.base.post")
    public Object child(String id) {
        return basePostService.query(Cnd.where("parentId", "=", id).asc("path"),"unit");
    }

    @At
    @Ok("json")
    @RequiresPermissions("platform.base.post.select")
    public Object tree(@Param("pid") String pid) {
        List<base_post> list = basePostService.query(Cnd.where("parentId", "=", Strings.sBlank(pid)).asc("path"));
        List<Map<String, Object>> tree = new ArrayList<>();
        for (base_post post : list) {
            Map<String, Object> obj = new HashMap<>();
            obj.put("id", post.getId());
            obj.put("text", post.getPostname());
            obj.put("children", post.isHasChildren());
            tree.add(obj);
        }
        return tree;
    }

    @At("/countByNum")
    @Ok("json:full")
    public Object countByNum(@Param("num")String num) {
        return basePostService.count("base_post",Cnd.where("postnum","=",num));
    }

}
