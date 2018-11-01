package cn.wizzer.app.web.modules.controllers.platform.sys;

import cn.wizzer.framework.base.Result;
import cn.wizzer.app.web.commons.slog.annotation.SLog;
import cn.wizzer.framework.page.datatable.DataTableColumn;
import cn.wizzer.framework.page.datatable.DataTableOrder;
import cn.wizzer.framework.util.StringUtil;
import cn.wizzer.app.sys.modules.models.Sys_barcode;
import cn.wizzer.app.sys.modules.services.SysBarcodeService;
import net.sourceforge.jtds.jdbc.DateTime;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import cn.wizzer.app.web.modules.controllers.open.api.Datatime.newDataTime;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.dao.Cnd;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@IocBean
@At("/platform/sys/barcode")
public class SysBarcodeController{
    private static final Log log = Logs.get();
    @Inject
    private SysBarcodeService sysBarcodeService;

    @At("")
    @Ok("beetl:/platform/sys/barcode/index.html")
    @RequiresPermissions("platform.sys.barcode")
    public void index() {
    }

    @At("/data")
    @Ok("json")
    @RequiresPermissions("platform.sys.barcode")
    public Object data(@Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns) {
		Cnd cnd = Cnd.NEW();
    	return sysBarcodeService.data(length, start, draw, order, columns, cnd, null);
    }

    @At("/fetchUser")
    @Ok("json")
    public Object fetchUser(@Param("code") String code) {
        Cnd cnd = Cnd.NEW();
        cnd.and("codenum","=",code);
        List<Sys_barcode> barcode = sysBarcodeService.query(cnd,"user");
        if(barcode.size()>0){
            return barcode.get(0);
        }
        else{
            return  -1;
        }
    }

    @At("/add")
    @Ok("beetl:/platform/sys/barcode/add.html")
    @RequiresPermissions("platform.sys.barcode")
    public void add() {

    }

    @At("/addDo")
    @Ok("json")
    @RequiresPermissions("platform.sys.barcode.add")
    @SLog(tag = "Sys_barcode", msg = "${args[0].id}")
    public Object addDo(@Param("..")Sys_barcode sysBarcode, HttpServletRequest req) {
		try {
			sysBarcodeService.insert(sysBarcode);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }

    @At("/addTo")
    @Ok("json")
    public Object addTo(@Param("code") String code){
        try{
            String startDate=newDataTime.getDateYMDHMS();
            Calendar calEndDate=Calendar.getInstance();
            calEndDate.add(Calendar.MINUTE,1);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date end=calEndDate.getTime();
            String endDate = sdf.format(end);
            Sys_barcode barcode=new Sys_barcode();
            barcode.setCodenum(code);
            barcode.setStartDate(startDate);
            barcode.setEndDate(endDate);
            barcode.setPstatus("0");//0 等待 1 已使用
            sysBarcodeService.insert(barcode);
            return Result.success("system.success");
        }
        catch(Exception e){
            return Result.error("system.error");
        }
    }

    @At("/updateUserId")
    @Ok("jsonp:full")
    public Object updateUserId(@Param("code") String code,@Param("userid") String userid){

        int rslt=0;
        try {
            if (!Strings.isBlank(userid)) {
                Cnd cnd = Cnd.NEW();
                cnd.and("codenum", "=", code);
                Sys_barcode barcode = sysBarcodeService.fetch(cnd);
                barcode.setUserid(userid);
                barcode.setCurDate(newDataTime.getDateYMDHMS());
                rslt = sysBarcodeService.updateIgnoreNull(barcode);
            }
            return rslt;
        }
        catch(Exception e){
            return  rslt;
        }
    }

    @At("/edit/?")
    @Ok("beetl:/platform/sys/barcode/edit.html")
    @RequiresPermissions("platform.sys.barcode")
    public void edit(String id,HttpServletRequest req) {
		req.setAttribute("obj", sysBarcodeService.fetch(id));
    }

    @At("/editDo")
    @Ok("json")
    @RequiresPermissions("platform.sys.barcode.edit")
    @SLog(tag = "Sys_barcode", msg = "${args[0].id}")
    public Object editDo(@Param("..")Sys_barcode sysBarcode, HttpServletRequest req) {
		try {
            sysBarcode.setOpBy(StringUtil.getUid());
			sysBarcode.setOpAt((int) (System.currentTimeMillis() / 1000));
			sysBarcodeService.updateIgnoreNull(sysBarcode);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }

    @At({"/delete/?", "/delete"})
    @Ok("json")
    @RequiresPermissions("platform.sys.barcode.delete")
    @SLog(tag = "Sys_barcode", msg = "${req.getAttribute('id')}")
    public Object delete(String id, @Param("ids")  String[] ids, HttpServletRequest req) {
		try {
			if(ids!=null&&ids.length>0){
				sysBarcodeService.delete(ids);
    			req.setAttribute("id", org.apache.shiro.util.StringUtils.toString(ids));
			}else{
				sysBarcodeService.delete(id);
    			req.setAttribute("id", id);
			}
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }

    @At("/detail/?")
    @Ok("beetl:/platform/sys/barcode/detail.html")
    @RequiresPermissions("platform.sys.barcode")
	public void detail(String id, HttpServletRequest req) {
		if (!Strings.isBlank(id)) {
            req.setAttribute("obj", sysBarcodeService.fetch(id));
		}else{
            req.setAttribute("obj", null);
        }
    }

}
