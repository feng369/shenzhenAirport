package cn.wizzer.app.web.modules.controllers.platform.base;

import cn.wizzer.app.base.modules.models.base_personpool;
import cn.wizzer.app.base.modules.models.base_rowofficeentry;
import cn.wizzer.app.base.modules.services.BasePersonpoolService;
import cn.wizzer.app.base.modules.services.BaseRowofficeentryService;
import cn.wizzer.app.logistics.modules.models.logistics_orderentry;
import cn.wizzer.framework.base.Result;
import cn.wizzer.app.web.commons.slog.annotation.SLog;
import cn.wizzer.framework.page.datatable.DataTableColumn;
import cn.wizzer.framework.page.datatable.DataTableOrder;
import cn.wizzer.framework.util.StringUtil;
import cn.wizzer.app.base.modules.models.base_rowoffice;
import cn.wizzer.app.base.modules.services.BaseRowofficeService;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.dao.Cnd;
import org.nutz.integration.quartz.QuartzManager;
import org.nutz.json.Json;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

@IocBean
@At("/platform/base/rowoffice")
public class BaseRowofficeController{
    private static final Log log = Logs.get();
    @Inject
    private BaseRowofficeService baseRowofficeService;
    @Inject
    private BasePersonpoolService basePersonpoolService;

    @Inject
    private BaseRowofficeentryService baseRowofficeentryService;

    @Inject
    private QuartzManager quartzManager;

    @At("")
    @Ok("beetl:/platform/base/rowoffice/index.html")
    @RequiresPermissions("platform.base.rowoffice.select")
    public void index() {
    }

    @At("/data")
    @Ok("json")
    @RequiresPermissions("platform.base.rowoffice.select")
    public Object data(@Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns) {
		Cnd cnd = Cnd.NEW();
    	return baseRowofficeService.data(length, start, draw, order, columns, cnd, null);
    }

    @At("/personpoolSubmit")
    @Ok("json")
    @RequiresPermissions("platform.base.rowoffice")
    public Object personpoolSubmit(@Param("id")  String id,@Param("datatime") Date datatime){
        try {
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            base_rowoffice baserowoffice = baseRowofficeService.fetchLinks(baseRowofficeService.fetch(id), "rowofficeentry");
            List<base_rowofficeentry> baserowofficeentry = baserowoffice.getRowofficeentry();
            String startdata = "";
            String enddata = "";

            String statrtime = baserowoffice.getEnddata();
            String endtime = baserowoffice.getStartdata();
            int result = endtime.compareTo(statrtime);
            if (result >= 0) {
                startdata = sdf.format(datatime) + " " + statrtime;
                enddata = sdf.format(datatime) + " " + endtime;
            } else {
                startdata = sdf.format(datatime) + " " + statrtime;
                //日期多加一天
                Calendar calendar = new GregorianCalendar();
                calendar.setTime(datatime);
                calendar.add(calendar.DATE, 1);
                datatime = calendar.getTime();
                enddata = sdf.format(datatime) + " " + endtime;
            }
            //检查当天班次ID是否存在，每天只允许同一班次只能存在一个
            Cnd cnd = Cnd.NEW();
            cnd.and("rowofficeid", "=", id);
            cnd.and("enddata", ">=", startdata);
            List<base_personpool> basepersonpools = basePersonpoolService.query(cnd);
            if (basepersonpools.size() > 0) {
                return Result.error("personpool.sumerror");
            } else {
                for (int i = 0; i < baserowofficeentry.size(); i++) {
                    base_personpool basepersonpool = new base_personpool();
                    basepersonpool.setRowofficeid(id);
                    basepersonpool.setPersonid(baserowofficeentry.get(i).getPersonid());
                    basepersonpool.setStartdata(startdata);
                    basepersonpool.setEnddata(enddata);
                    basePersonpoolService.insert(basepersonpool);
                }
                return Result.success("system.success");
            }

        } catch (Exception e) {
            return Result.error("system.error");
        }

    }

    @At("/add")
    @Ok("beetl:/platform/base/rowoffice/add.html")
    @RequiresPermissions("platform.base.rowoffice")
    public void add() {

    }

    @At("/addDo")
    @Ok("json")
    @RequiresPermissions("platform.base.rowoffice.add")
    @SLog(tag = "班次", msg = "新增")
    public Object addDo(@Param("rowoffice")String rowoffice,@Param("rowofficeentry")String rowofficeentry, HttpServletRequest req) {
		try {
            base_rowoffice baserowoffice= Json.fromJson(base_rowoffice.class,rowoffice);
            List<base_rowofficeentry> baserowofficeentry= Json.fromJsonAsList(base_rowofficeentry.class, rowofficeentry);
            baserowoffice.setRowofficeentry(baserowofficeentry);
            baseRowofficeService.save(baserowoffice,"rowofficeentry");
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }

    @At("/edit/?")
    @Ok("beetl:/platform/base/rowoffice/edit.html")
    @RequiresPermissions("platform.base.rowoffice")
    public void edit(String id,HttpServletRequest req) {
		req.setAttribute("obj", baseRowofficeService.getBase_rowoffice(id));
    }

    @At("/editDo")
    @Ok("json")
    @RequiresPermissions("platform.base.rowoffice.edit")
    @SLog(tag = "班次", msg = "修改")
    public Object editDo(@Param("rowoffice")String rowoffice,@Param("rowofficeentry")String rowofficeentry, HttpServletRequest req) {
		try {
            base_rowoffice baserowoffice= Json.fromJson(base_rowoffice.class,rowoffice);
            List<base_rowofficeentry> baserowofficeentry= Json.fromJsonAsList(base_rowofficeentry.class, rowofficeentry);
            baserowoffice.setRowofficeentry(baserowofficeentry);
			baseRowofficeService.dao().updateIgnoreNull(baserowoffice);
            for(base_rowofficeentry baseRowofficeentry:baserowofficeentry){
                if(Strings.isBlank(baseRowofficeentry.getId())){
                    baseRowofficeentryService.dao().insert(baseRowofficeentry);
                }else {
                    baseRowofficeentryService.dao().updateWith(baserowoffice,"rowofficeentry");
                }
            }
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }

    @At({"/delete/?", "/delete"})
    @Ok("json")
    @RequiresPermissions("platform.base.rowoffice.delete")
    @SLog(tag = "base_rowoffice", msg = "${req.getAttribute('id')}")
    public Object delete(String id, @Param("ids")  String[] ids, HttpServletRequest req) {
		try {
			if(ids!=null&&ids.length>0){
				baseRowofficeService.delete(ids);
    			req.setAttribute("id", org.apache.shiro.util.StringUtils.toString(ids));
			}else{
				baseRowofficeService.delete(id);
    			req.setAttribute("id", id);
			}
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }

    @At("/detail/?")
    @Ok("beetl:/platform/base/rowoffice/detail.html")
    @RequiresPermissions("platform.base.rowoffice.select")
	public void detail(String id, HttpServletRequest req) {
		if (!Strings.isBlank(id)) {
            req.setAttribute("obj", baseRowofficeService.fetch(id));
		}else{
            req.setAttribute("obj", null);
        }
    }

}
