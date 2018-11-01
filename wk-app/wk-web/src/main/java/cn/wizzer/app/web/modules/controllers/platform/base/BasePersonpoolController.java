package cn.wizzer.app.web.modules.controllers.platform.base;

import cn.wizzer.app.base.modules.models.base_airport;
import cn.wizzer.app.base.modules.models.base_person;
import cn.wizzer.app.base.modules.services.BaseAirportService;
import cn.wizzer.app.base.modules.services.BasePersonService;
import cn.wizzer.framework.base.Result;
import cn.wizzer.app.web.commons.slog.annotation.SLog;
import cn.wizzer.framework.page.OffsetPager;
import cn.wizzer.framework.page.datatable.DataTableColumn;
import cn.wizzer.framework.page.datatable.DataTableOrder;
import cn.wizzer.framework.util.StringUtil;
import cn.wizzer.app.base.modules.models.base_personpool;
import cn.wizzer.app.base.modules.services.BasePersonpoolService;
import com.sun.prism.impl.Disposer;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.bouncycastle.jce.exception.ExtCertPathBuilderException;
import org.nutz.dao.*;
import org.nutz.dao.Chain;
import org.nutz.dao.entity.Record;
import org.nutz.dao.entity.annotation.One;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.Sql;
import org.nutz.json.Json;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@IocBean
@At("/platform/base/personpool")
public class BasePersonpoolController{
    private static final Log log = Logs.get();
    @Inject
    private BasePersonpoolService basePersonpoolService;
    @Inject
    private BaseAirportService baseAirportService;
    @Inject
    private BasePersonService basePersonService;
    @Inject
    private Dao dao;

    @At("")
    @Ok("beetl:/platform/base/personpool/index.html")
    @RequiresPermissions("platform.base.personpool.select")
    public void index() {
    }

    @At("/data")
    @Ok("json")
    @RequiresPermissions("platform.base.personpool.select")
    public Object data(@Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns) {
		Cnd cnd = Cnd.NEW();
        NutMap data=basePersonpoolService.getPersonRow(length,start,draw,cnd,"");
        return data;
    }

    @At("/persondata")
    @Ok("json")
    @RequiresPermissions("platform.base.personpool.select")
    public Object persondata(@Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns) {
        Cnd cnd = Cnd.NEW();
        cnd.and("pstatus","=",1);
        return basePersonpoolService.data(length, start, draw, order, columns, cnd, "basePerson");
    }

    @At("/add")
    @Ok("beetl:/platform/base/personpool/add.html")
    @RequiresPermissions("platform.base.personpool")
    public void add() {

    }

    @At("/addDo")
    @Ok("json")
    @RequiresPermissions("platform.base.personpool.add")
    @SLog(tag = "base_personpool", msg = "${args[0].id}")
    public Object addDo(@Param("..")base_personpool basePersonpool, HttpServletRequest req) {
		try {
			basePersonpoolService.insert(basePersonpool);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }

//    @At("/updatePosition")
//    @Ok("json")
//    public Object updatePosition(@Param("personid")String personid, @Param("position")String position,HttpServletRequest req) {
//        try {
//            Cnd cnd=Cnd.NEW();
//            cnd.and("pstatus","=",1);
//            cnd.and("personid","=",personid);
//            base_personpool basepersonpool = basePersonpoolService.fetch(cnd);
//            basepersonpool.setPosition(position);
//            basePersonpoolService.updateIgnoreNull(basepersonpool);
//            return Result.success("system.success");
//        } catch (Exception e) {
//            return Result.error("system.error");
//        }
//    }

    @At("/edit/?")
    @Ok("beetl:/platform/base/personpool/edit.html")
    @RequiresPermissions("platform.base.personpool")
    public void edit(String id,HttpServletRequest req) {
		req.setAttribute("obj", basePersonpoolService.fetch(id));
    }

    @At("/editDo")
    @Ok("json")
    @RequiresPermissions("platform.base.personpool.edit")
    @SLog(tag = "base_personpool", msg = "${args[0].id}")
    public Object editDo(@Param("..")base_personpool basePersonpool, HttpServletRequest req) {
		try {
            basePersonpool.setOpBy(StringUtil.getUid());
			basePersonpool.setOpAt((int) (System.currentTimeMillis() / 1000));
			basePersonpoolService.updateIgnoreNull(basePersonpool);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }

    @At({"/delete/?", "/delete"})
    @Ok("json")
    @RequiresPermissions("platform.base.personpool.delete")
    @SLog(tag = "排班管理", msg = "删除排班")
    public Object delete(String id, @Param("ids")  String[] ids,@Param("startdata")  String[] startdata, HttpServletRequest req) {
		try {
		    Cnd cnd = Cnd.NEW();
		    cnd.and("rowofficeid","in",ids);
            cnd.and("startdata","in",startdata);
			if(ids!=null&&ids.length>0){
                basePersonpoolService.clear(cnd);
                return Result.success("system.success");
			}else{
                return Result.success("删除失败");
            }
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }

    @At("/detail/?")
    @Ok("beetl:/platform/base/personpool/detail.html")
    @RequiresPermissions("platform.base.personpool.select")
	public void detail(String id, HttpServletRequest req) {
		if (!Strings.isBlank(id)) {
            req.setAttribute("obj", basePersonpoolService.fetch(id));
		}else{
            req.setAttribute("obj", null);
        }
    }
    @At("/getPersonPo")
    @Ok("json")
    public Object getPersonPo(int status){
        Cnd cnd =Cnd.NEW();
        cnd.and("pstatus","=",status);
        List<base_personpool> pool= basePersonpoolService.query(cnd);
        return pool;
    }

    @At("/updatawork")
    @Ok("json")
    @RequiresPermissions("platform.base.personpool")
    public Object updatawork(@Param("ids")  String ids,@Param("startdata")  String startdata){
        try{
            Cnd cnd = Cnd.NEW();
            basePersonpoolService.update("base_personpool", Chain.make("pstatus",0),cnd.where("pstatus","=",1));
            if (!Strings.isBlank(ids) && !Strings.isBlank(startdata)) {
                cnd.and("rowofficeid","=",ids);
                cnd.and("startdata","=",startdata);
                if(basePersonpoolService.checkWork(ids,startdata)){
                    List<base_personpool> basepersonpools = basePersonpoolService.query(cnd);
                    basePersonpoolService.update("base_personpool", Chain.make("pstatus",1).add("personstatus",0),cnd);
                    //初始化人员位置,初始化人员位置为人员所属机场中心点
                    List<base_personpool> persons=basePersonpoolService.query(cnd.where("pstatus","=",1));
                    for(base_personpool p:persons){
                        base_person basePerson= basePersonService.fetch(p.getPersonid());
                        base_airport base_airport=baseAirportService.fetch(basePerson.getAirportid());
                        Cnd cri =Cnd.NEW();
                        cri.and("personid","=",p.getPersonid());
                        cri.and("pstatus","=",1);
                        basePersonpoolService.update("base_personpool", Chain.make("position",base_airport.getPosition()),cri);
                    }

                    return Result.success("排班成功");
                }else{
                    return Result.error("不允许同一时间段重复排班");
                }
            }else{
                return Result.error("排班错误");
            }
        }catch (Exception e) {
            return Result.error("系统错误，请联系管理员");
        }
    }
    @At({"/ppool/?","/ppool"})
    @Ok("beetl:/platform/base/personpool/personpool.html")
    @RequiresPermissions("platform.logistics.order")
    public void ppool(String id, HttpServletRequest req) {
        if (!Strings.isBlank(id)) {
            req.setAttribute("orderid",id);
        }else {
            req.setAttribute("orderid","");
        }

    }

    @At("/getPersonpoolbyM")
    @Ok("jsonp:full")
    public Object getPersonpoolbyM(@Param("airportid") String airportid){
        try{
            Cnd cnd=Cnd.NEW();
            if(!Strings.isBlank(airportid)){
                Sql sql=Sqls.queryRecord("select a.*,b.airportid,b.personname from base_personpool a left join base_person b on a.personid=b.id where a.pstatus=1  and b.airportid='"+airportid+"'");//1 当班 0 下班  and (now() BETWEEN startdata and enddata) 去掉这段话，去掉当班时间，因为可能存在加班
                dao.execute(sql);
                List<Record> res = sql.getList(Record.class);
                return res;
            }
            return  null;
        }
        catch (Exception e){
            return Result.error("error");
        }
    }

//    @At("/setPersonBusybyM")
//    @Ok("jsonp:full")
//    public Object setPersonBusybyM(@Param("personlist") String personlist){
//        try{
//            if(!Strings.isBlank(personlist)){
//                Sql sql=Sqls.queryRecord("update base_personpool set personstatus=1 where personid in ("+personlist+") ");//去掉了 and now() BETWEEN startdata and enddata 人员池里每个人只会存在一条数据
//                dao.execute(sql);
//                return Result.success("更新成功");
//            }
//            return Result.error("更新失败");
//        }
//        catch (Exception e){
//            return Result.error("更新失败");
//        }
//    }

}
