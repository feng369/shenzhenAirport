package cn.wizzer.app.web.modules.controllers.platform.base;

import cn.wizzer.app.sys.modules.models.Sys_dict;
import cn.wizzer.app.sys.modules.models.Sys_user;
import cn.wizzer.app.sys.modules.services.SysDictService;
import cn.wizzer.app.sys.modules.services.SysUserService;
import cn.wizzer.framework.base.Result;
import cn.wizzer.app.web.commons.slog.annotation.SLog;
import cn.wizzer.framework.page.datatable.DataTableColumn;
import cn.wizzer.framework.page.datatable.DataTableOrder;
import cn.wizzer.framework.util.StringUtil;
import cn.wizzer.app.base.modules.models.base_airmeterial;
import cn.wizzer.app.base.modules.services.BaseAirmeterialService;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Record;
import org.nutz.dao.sql.Sql;
import org.nutz.json.Json;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@IocBean
@At("/platform/base/airmeterial")
public class BaseAirmeterialController{
    private static final Log log = Logs.get();
    @Inject
    private BaseAirmeterialService baseAirmeterialService;
    @Inject
    private SysDictService sysDictService;
    @Inject
    private SysUserService sysUserService;
    @Inject
    private Dao dao;

    @At("")
    @Ok("beetl:/platform/base/airmeterial/index.html")
    @RequiresPermissions("platform.base.airmeterial.select")
    public void index() {
    }

    @At("/data")
    @Ok("json")
    @RequiresPermissions("platform.base.airmeterial.select")
    public Object data(@Param("selectForm") String selectForm,@Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns) {
		Cnd cnd = Cnd.NEW();
        base_airmeterial  select =Json.fromJson(base_airmeterial.class,selectForm);
        if (!Strings.isBlank(select.getMeterialnum()))
            cnd.and("meterialnum", "like", "%" + select.getMeterialnum() + "%");
        if (!Strings.isBlank(select.getMeterialname()))
            cnd.and("meterialname", "like", "%" + select.getMeterialname() + "%");
    	return baseAirmeterialService.dataCode(length, start, draw, order, columns, cnd, null,null);
    }

    @At("/add")
    @Ok("beetl:/platform/base/airmeterial/add.html")
    @RequiresPermissions("platform.base.airmeterial")
    public void add() {

    }

    @At("/addDo")
    @Ok("json")
    @RequiresPermissions("platform.base.airmeterial.add")
    @SLog(tag = "base_airmeterial", msg = "${args[0].id}")
    public Object addDo(@Param("..")base_airmeterial baseAirmeterial, HttpServletRequest req) {
		try {
			baseAirmeterialService.insert(baseAirmeterial);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }

    @At("/edit/?")
    @Ok("beetl:/platform/base/airmeterial/edit.html")
    @RequiresPermissions("platform.base.airmeterial")
    public void edit(String id,HttpServletRequest req) {
        base_airmeterial airmeterial=baseAirmeterialService.fetch(id);

		req.setAttribute("obj", airmeterial);
    }

    @At("/editDo")
    @Ok("json")
    @RequiresPermissions("platform.base.airmeterial.edit")
    @SLog(tag = "base_airmeterial", msg = "${args[0].id}")
    public Object editDo(@Param("..")base_airmeterial baseAirmeterial, HttpServletRequest req) {
		try {
            baseAirmeterial.setOpBy(StringUtil.getUid());
			baseAirmeterial.setOpAt((int) (System.currentTimeMillis() / 1000));
			baseAirmeterialService.updateIgnoreNull(baseAirmeterial);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }

    @At({"/delete/?", "/delete"})
    @Ok("json")
    @RequiresPermissions("platform.base.airmeterial.delete")
    @SLog(tag = "base_airmeterial", msg = "${req.getAttribute('id')}")
    public Object delete(String id, @Param("ids")  String[] ids, HttpServletRequest req) {
		try {
			if(ids!=null&&ids.length>0){
				baseAirmeterialService.delete(ids);
    			req.setAttribute("id", org.apache.shiro.util.StringUtils.toString(ids));
			}else{
				baseAirmeterialService.delete(id);
    			req.setAttribute("id", id);
			}
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }

    @At("/detail/?")
    @Ok("beetl:/platform/base/airmeterial/detail.html")
    @RequiresPermissions("platform.base.airmeterial.select")
	public void detail(String id, HttpServletRequest req) {
		if (!Strings.isBlank(id)) {
		    base_airmeterial airmeterial=baseAirmeterialService.fetch(id);
            if(!Strings.isBlank(airmeterial.getMeterialtypeId())){
                Sys_dict meterialtype=sysDictService.fetch(airmeterial.getMeterialtypeId());
                airmeterial.setMeterialtype(meterialtype);
            }
		    if(!Strings.isBlank(airmeterial.getHaspack())){
                Sys_dict pack=sysDictService.fetch(airmeterial.getHaspack());
                airmeterial.setPack(pack);
            }
            if(!Strings.isBlank(airmeterial.getVehicletypeId())){
                Sys_dict vehicletype=sysDictService.fetch(airmeterial.getVehicletypeId());
                airmeterial.setVehicletype(vehicletype);
            }
            if(!Strings.isBlank(airmeterial.getHasforklift())){
                Sys_dict forklift=sysDictService.fetch(airmeterial.getHasforklift());
                airmeterial.setForklift(forklift);
            }
            req.setAttribute("obj", airmeterial);
		}else{
            req.setAttribute("obj", null);
        }
    }

    @At("/bindMeterialDDL")
    @Ok("json")
    @RequiresPermissions("platform.base.airmeterial")
    public Object bindMeterialDDL(String name,String code,String code2)
    {
        //name值传过来是null
        String parentid="";
        if(!Strings.isEmpty(name))
        {
            parentid=sysDictService.getIdByNameAndCode(name,code);
        }
        else
        {
            parentid=sysDictService.getIdByNameAndCode(code,code2);
        }


        if(!Strings.isBlank(parentid)) {
            List<Sys_dict> list = sysDictService.query(Cnd.where("parentId", "=", parentid).asc("location"));
            return list;
        }
        return "";
    }
    @At("/autocomplete")
    @Ok("json")
    @RequiresPermissions("platform.base.airmeterial")
    public Object autocomplete(@Param("meterialnum") String meterialnum) {
        Cnd cnd = Cnd.NEW();
        List<base_airmeterial> List=new ArrayList<>();
        if (!Strings.isBlank(meterialnum)) {
            cnd.and("meterialnum", "like", "%" + meterialnum + "%");
            List = baseAirmeterialService.query(cnd);
        }

        if(List.size() == 0){
            base_airmeterial baseAirmeterial=new base_airmeterial();
            baseAirmeterial.setId("");
            baseAirmeterial.setMeterialname("--");
            baseAirmeterial.setMeterialnum("--");
            List.add(baseAirmeterial);
        }

        return  List;
    }

    //20180302zhf1105
    @At("/autocompleteforMobile")
    @Ok("jsonp:full")
    public Object autocompleteforMobile(@Param("meterialnum") String meterialnum) {
        try{
            if(!Strings.isBlank(meterialnum)){
                String sqlstr="select id,meterialname,meterialnum from base_airmeterial where meterialnum like '%"+meterialnum+"%' order by meterialnum asc limit 15";
                Sql sql= Sqls.queryRecord(sqlstr);

                dao.execute(sql);
                List<Record> res = sql.getList(Record.class);
                return res;
            }
            return null;
        }catch(Exception r){
            return null;
        }

    }
    @At("/countByNum")
    @Ok("json:full")

    public Object countByNum(@Param("num")String num) {
        return baseAirmeterialService.count("base_airmeterial",Cnd.where("meterialnum","=",num));
    }

}
