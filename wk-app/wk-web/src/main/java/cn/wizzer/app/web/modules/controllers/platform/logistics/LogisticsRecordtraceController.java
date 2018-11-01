package cn.wizzer.app.web.modules.controllers.platform.logistics;

import cn.wizzer.framework.base.Result;
import cn.wizzer.app.web.commons.slog.annotation.SLog;
import cn.wizzer.framework.page.datatable.DataTableColumn;
import cn.wizzer.framework.page.datatable.DataTableOrder;
import cn.wizzer.framework.util.StringUtil;
import cn.wizzer.app.logistics.modules.models.logistics_recordtrace;
import cn.wizzer.app.logistics.modules.services.LogisticsRecordtraceService;
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
@At("/platform/logistics/recordtrace")
public class LogisticsRecordtraceController{
    private static final Log log = Logs.get();
    @Inject
    private LogisticsRecordtraceService logisticsRecordtraceService;

    @At("")
    @Ok("beetl:/platform/logistics/recordtrace/index.html")
    @RequiresPermissions("platform.logistics.recordtrace")
    public void index() {
    }

    @At("/data")
    @Ok("json:full")
    @RequiresPermissions("platform.logistics.recordtrace")
    public Object data(@Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns) {
		Cnd cnd = Cnd.NEW();
    	return logisticsRecordtraceService.data(length, start, draw, order, columns, cnd, null);
    }

    @At("/add")
    @Ok("beetl:/platform/logistics/recordtrace/add.html")
    @RequiresPermissions("platform.logistics.recordtrace")
    public void add() {

    }

    @At("/addDo")
    @Ok("json")
    @RequiresPermissions("platform.logistics.recordtrace.add")
    @SLog(tag = "logistics_recordtrace", msg = "${args[0].id}")
    public Object addDo(@Param("..")logistics_recordtrace logisticsRecordtrace, HttpServletRequest req) {
		try {
			logisticsRecordtraceService.insert(logisticsRecordtrace);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }

    @At("/edit/?")
    @Ok("beetl:/platform/logistics/recordtrace/edit.html")
    @RequiresPermissions("platform.logistics.recordtrace")
    public void edit(String id,HttpServletRequest req) {
		req.setAttribute("obj", logisticsRecordtraceService.fetch(id));
    }

    @At("/editDo")
    @Ok("json")
    @RequiresPermissions("platform.logistics.recordtrace.edit")
    @SLog(tag = "logistics_recordtrace", msg = "${args[0].id}")
    public Object editDo(@Param("..")logistics_recordtrace logisticsRecordtrace, HttpServletRequest req) {
		try {
            logisticsRecordtrace.setOpBy(StringUtil.getUid());
			logisticsRecordtrace.setOpAt((int) (System.currentTimeMillis() / 1000));
			logisticsRecordtraceService.updateIgnoreNull(logisticsRecordtrace);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }

    @At({"/delete/?", "/delete"})
    @Ok("json")
    @RequiresPermissions("platform.logistics.recordtrace.delete")
    @SLog(tag = "logistics_recordtrace", msg = "${req.getAttribute('id')}")
    public Object delete(String id, @Param("ids")  String[] ids, HttpServletRequest req) {
		try {
			if(ids!=null&&ids.length>0){
				logisticsRecordtraceService.delete(ids);
    			req.setAttribute("id", org.apache.shiro.util.StringUtils.toString(ids));
			}else{
				logisticsRecordtraceService.delete(id);
    			req.setAttribute("id", id);
			}
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }

    @At("/detail/?")
    @Ok("beetl:/platform/logistics/recordtrace/detail.html")
    @RequiresPermissions("platform.logistics.recordtrace")
	public void detail(String id, HttpServletRequest req) {
        		if (!Strings.isBlank(id)) {
            req.setAttribute("obj", logisticsRecordtraceService.fetch(id));
		}else{
            req.setAttribute("obj", null);
        }}

}
