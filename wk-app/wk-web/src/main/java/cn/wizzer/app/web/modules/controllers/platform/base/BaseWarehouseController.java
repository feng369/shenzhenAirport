package cn.wizzer.app.web.modules.controllers.platform.base;

import cn.wizzer.app.base.modules.services.BaseCnctobjService;
import cn.wizzer.framework.base.Result;
import cn.wizzer.app.web.commons.slog.annotation.SLog;
import cn.wizzer.framework.page.datatable.DataTableColumn;
import cn.wizzer.framework.page.datatable.DataTableOrder;
import cn.wizzer.framework.util.StringUtil;
import cn.wizzer.app.base.modules.models.base_warehouse;
import cn.wizzer.app.base.modules.models.base_cnctobj;
import cn.wizzer.app.base.modules.services.BaseWarehouseService;
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
import java.util.List;

@IocBean
@At("/platform/base/warehouse")
public class BaseWarehouseController {
    private static final Log log = Logs.get();
    @Inject
    private BaseWarehouseService baseWarehouseService;
    @Inject
    private BaseCnctobjService baseCnctobjService;

    @At("")
    @Ok("beetl:/platform/base/warehouse/index.html")
    @RequiresPermissions("platform.base.warehouse.select")
    public void index() {
    }

    @At("/data")
    @Ok("json")
    @RequiresPermissions("platform.base.warehouse.select")
    public Object data(@Param("whnum") String whnum, @Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns) {
        Cnd cnd = Cnd.NEW();
        cnd.asc("whnum");
        if (!Strings.isBlank(whnum))
            cnd.and("whnum", "like", "%" + whnum + "%");
        cnd = baseCnctobjService.airportDataPermission(cnd);
        return baseWarehouseService.data(length, start, draw, order, columns, cnd, null);
    }

    @At("/add")
    @Ok("beetl:/platform/base/warehouse/add.html")
    @RequiresPermissions("platform.base.warehouse")
    public void add() {

    }

    @At("/addDo")
    @Ok("json")
    @RequiresPermissions("platform.base.warehouse.add")
    @SLog(tag = "base_warehouse", msg = "${args[0].id}")
    public Object addDo(@Param("..") base_warehouse baseWarehouse, HttpServletRequest req) {
        try {
            baseWarehouseService.insert(baseWarehouse);
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }

    @At("/edit/?")
    @Ok("beetl:/platform/base/warehouse/edit.html")
    @RequiresPermissions("platform.base.warehouse")
    public void edit(String id, HttpServletRequest req) {
        //20180227zhf1159
        if (!Strings.isBlank(id)) {
            base_warehouse baseWarehouse = baseWarehouseService.fetch(id);
            req.setAttribute("obj", baseWarehouseService.fetchLinks(baseWarehouse, "airport"));
        }
    }

    @At("/editDo")
    @Ok("json")
    @RequiresPermissions("platform.base.warehouse.edit")
    @SLog(tag = "base_warehouse", msg = "${args[0].id}")
    public Object editDo(@Param("..") base_warehouse baseWarehouse, HttpServletRequest req) {
        try {
            baseWarehouse.setOpBy(StringUtil.getUid());
            baseWarehouse.setOpAt((int) (System.currentTimeMillis() / 1000));
            baseWarehouseService.updateIgnoreNull(baseWarehouse);
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }

    @At({"/delete/?", "/delete"})
    @Ok("json")
    @RequiresPermissions("platform.base.warehouse.delete")
    @SLog(tag = "base_warehouse", msg = "${req.getAttribute('id')}")
    public Object delete(String id, @Param("ids") String[] ids, HttpServletRequest req) {
        try {
            if (ids != null && ids.length > 0) {
                baseWarehouseService.delete(ids);
                req.setAttribute("id", org.apache.shiro.util.StringUtils.toString(ids));
            } else {
                baseWarehouseService.delete(id);
                req.setAttribute("id", id);
            }
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }

    @At("/detail/?")
    @Ok("beetl:/platform/base/warehouse/detail.html")
    @RequiresPermissions("platform.base.warehouse.select")
    public void detail(String id, HttpServletRequest req) {
        if (!Strings.isBlank(id)) {
            //20180227zhf1159
            base_warehouse baseWarehouse = baseWarehouseService.fetch(id);
            req.setAttribute("obj", baseWarehouseService.fetchLinks(baseWarehouse, "airport"));
        } else {
            req.setAttribute("obj", null);
        }
    }

    @At("/countByNum")
    @Ok("json:full")
    public Object countByNum(@Param("num") String num) {
        return baseWarehouseService.count("base_warehouse", Cnd.where("whnum", "=", num));
    }

}
