package cn.wizzer.app.web.modules.controllers.platform.logistics;

import cn.wizzer.app.logistics.modules.models.logistics_unplan;
import cn.wizzer.app.web.commons.base.Globals;
import cn.wizzer.framework.base.Result;
import cn.wizzer.app.web.commons.slog.annotation.SLog;
import cn.wizzer.framework.page.datatable.DataTableColumn;
import cn.wizzer.framework.page.datatable.DataTableOrder;
import cn.wizzer.framework.util.DateUtil;
import cn.wizzer.framework.util.StringUtil;
import cn.wizzer.app.logistics.modules.models.logistics_orderplanentry;
import cn.wizzer.app.logistics.modules.services.LogisticsOrderplanentryService;
import cn.wizzer.app.logistics.modules.services.LogisticsSendplanService;
import cn.wizzer.app.logistics.modules.models.logistics_sendplan;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Record;
import org.nutz.dao.sql.Sql;
import org.nutz.lang.Strings;
import org.nutz.lang.random.R;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.*;
import org.nutz.mvc.impl.AdaptorErrorContext;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@IocBean
@At("/platform/logistics/orderplanentry")
public class LogisticsOrderplanentryController{
    private static final Log log = Logs.get();
    @Inject
    private LogisticsOrderplanentryService logisticsOrderplanentryService;
    @Inject
    private Dao dao;
    @Inject
    private LogisticsSendplanService logisticsSendplanService;

    @At("")
    @Ok("beetl:/platform/logistics/orderplanentry/index.html")
    @RequiresPermissions("platform.logistics.orderplanentry")
    public void index() {
    }

    @At("/data")
    @Ok("json")
    @RequiresPermissions("platform.logistics.orderplanentry")
    public Object data(@Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns) {
		Cnd cnd = Cnd.NEW();
    	return logisticsOrderplanentryService.data(length, start, draw, order, columns, cnd, null);
    }

    @At("/add")
    @Ok("beetl:/platform/logistics/orderplanentry/add.html")
    @RequiresPermissions("platform.logistics.orderplanentry")
    public void add() {

    }

    @At("/addDo")
    @Ok("json")
    @RequiresPermissions("platform.logistics.orderplanentry.add")
    @SLog(tag = "logistics_orderplanentry", msg = "${args[0].id}")
    public Object addDo(@Param("..")logistics_orderplanentry logisticsOrderplanentry, HttpServletRequest req) {
		try {
			logisticsOrderplanentryService.insert(logisticsOrderplanentry);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }

    @At("/edit/?")
    @Ok("beetl:/platform/logistics/orderplanentry/edit.html")
    @RequiresPermissions("platform.logistics.orderplanentry")
    public void edit(String id,HttpServletRequest req) {
		req.setAttribute("obj", logisticsOrderplanentryService.fetch(id));
    }

    @At("/editDo")
    @Ok("json")
    @RequiresPermissions("platform.logistics.orderplanentry.edit")
    @SLog(tag = "logistics_orderplanentry", msg = "${args[0].id}")
    public Object editDo(@Param("..")logistics_orderplanentry logisticsOrderplanentry, HttpServletRequest req) {
		try {
            logisticsOrderplanentry.setOpBy(StringUtil.getUid());
			logisticsOrderplanentry.setOpAt((int) (System.currentTimeMillis() / 1000));
			logisticsOrderplanentryService.updateIgnoreNull(logisticsOrderplanentry);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }

    @At({"/delete/?", "/delete"})
    @Ok("json")
    @RequiresPermissions("platform.logistics.orderplanentry.delete")
    @SLog(tag = "logistics_orderplanentry", msg = "${req.getAttribute('id')}")
    public Object delete(String id, @Param("ids")  String[] ids, HttpServletRequest req) {
		try {
			if(ids!=null&&ids.length>0){
				logisticsOrderplanentryService.delete(ids);
    			req.setAttribute("id", org.apache.shiro.util.StringUtils.toString(ids));
			}else{
				logisticsOrderplanentryService.delete(id);
    			req.setAttribute("id", id);
			}
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }

    @At("/detail/?")
    @Ok("beetl:/platform/logistics/orderplanentry/detail.html")
    @RequiresPermissions("platform.logistics.sendgoods")
	public void detail(String id, HttpServletRequest req) {
		if (!Strings.isBlank(id)) {
//            req.setAttribute("obj", logisticsOrderplanentryService.fetch(id));
            req.setAttribute("id",id);
		}else{
            req.setAttribute("id", null);
        }
    }

    @At("/getSGInfo")
    @Ok("json")
    @RequiresPermissions("platform.logistics.sendgoods")
    public List<logistics_orderplanentry> getSGInfo(@Param("sgid") String sgid){
        Cnd cnd =Cnd.NEW();
        cnd.and("contractid","=",sgid);
        cnd.orderBy("step","asc");

        String sql="select a.id,a.content,a.step,a.stepname,a.operatetime,a.picpath,a.picname,a.oldpicname,a.pstatus " +
                ",b.contractcode,b.partnum,b.serialnum,b.number,b.shelf,b.batchnum,b.issuetype,b.rough,b.receivename, " +
                "b.receiveaddress,b.senddate,b.billnum,(case b.isAir when 0 then '否' when 1 then '是' else '' end) 是否禁航, " +
                "c.packnum,c.tempprice,c.protocolnum,c.freenum,c.thirdname,c.flightnum,c.drivername,c.carnum,c.weight, " +
                "d.personname 操作人,e.airportname 机场,(case b.tansporttype when '0' then '空运' when '1' " +
                "then '陆运' when '2' then '上门自提' else '' end) 运输方式,CONCAT(f.warehousename,'(',f.whnum,')') 库存地,g.name 优先级, " +
                "h.personname 交货人,i.name 费用类型,j.materielnum 包装件数,k.name 包装方式,m.personname 审批人,l.suggest,l.approvaldate 审批时间,l.aresult from logistics_orderplanentry a " +
                "left join logistics_sendgoods b on a.contractid=b.id " +
                "left join logistics_sendplan c on b.planID=c.id " +
                "left join base_person d on a.operater=d.id " +
                "left join base_airport e on b.airportID=e.id " +
                "left join base_warehouse f on b.warehouseID=f.id " +
                "left join sys_dict g on b.prioritylv=g.id " +
                "left join base_person h on b.sender=h.id " +
                "left join sys_dict i on c.costtype=i.id "+
                "left join logistics_unplan j on b.packID=j.ID " +
                "left join sys_dict k on j.packtype=k.id "+
                "left join (select * from ( select DISTINCT * from logistics_sendapproval ORDER BY approvaldate desc) approval group BY planID) l on c.id=l.planID " +
                "left join base_person m on l.approvaler=m.id "+
                "where a.contractid='"+sgid+"' order by a.step asc";

        Sql sqlstr= Sqls.queryRecord(sql);

        dao.execute(sqlstr);
        List<logistics_orderplanentry> res = sqlstr.getList(logistics_orderplanentry.class);
        return res;

        //这里要获取合同的所有信息及状态，未完成

//        List<logistics_orderplanentry> logistics_deliveryorderentries=logisticsOrderplanentryService.query(cnd,"logisticsDeliveryorder|logisticsOrder");
//
//
//        return logistics_deliveryorderentries;
    }

 /*   @At("/img")
    @Ok("jsonp:full")
    //AdaptorErrorContext必须是最后一个参数
    public Object img(@Param("filename") String filename, @Param("base64") String base64,@Param("spid") String spid,@Param("step") String step,HttpServletRequest req, AdaptorErrorContext err) {
        byte[] buffer;
        try {
            if (err != null && err.getAdaptorErr() != null) {
                return NutMap.NEW().addv("code", 1).addv("msg", "文件不合法");
            } else if (base64 == null) {
                return Result.error("空文件");
            } else {
                String p = Globals.AppRoot;
                String fn= R.UU32()+filename.substring(filename.lastIndexOf("."));
                String path=Globals.AppUploadPath + "/image/" + DateUtil.format(new Date(), "yyyyMMdd") + "/";
                String f = Globals.AppUploadPath + "/image/" + DateUtil.format(new Date(), "yyyyMMdd") + "/" + fn ;
                File file=new File(p+Globals.AppUploadPath + "/image/" + DateUtil.format(new Date(), "yyyyMMdd"));
                if(!file.exists()){
                    file.mkdirs();
                }
                buffer = Base64.getDecoder().decode(base64.split(",")[1]);
                FileOutputStream out = new FileOutputStream(p + f);
                out.write(buffer);
                out.close();
                *//********************
                 * 将上传的文件路径修改到数据库中
                 * 1.先将数据库中的此行得到
                 * 2.得到picpath及picname、oldpicname
                 * 3.将新的合并到这些字段
                 * 4.修改
                 *******************//*
                logistics_sendplan sendplan = logisticsSendplanService.fetch(spid);
                if(sendplan!=null){
                    Cnd cnd = Cnd.NEW();
                    cnd.and("orderid","=",sendplan.getOrderid());
                    cnd.and("step","=","9");
                    List<logistics_orderplanentry> orderplanentry=logisticsOrderplanentryService.query(cnd);

                    if(orderplanentry.size()>0){
                        for(int i=0;i<orderplanentry.size();i++){
                            String picpath=orderplanentry.get(i).getPicpath();
                            String picname=orderplanentry.get(i).getPicname();
                            String oldpicname=orderplanentry.get(i).getOldpicname();
                            if(!Strings.isBlank(picpath)){
                                picpath+=","+path+fn;
                            }else{
                                picpath=path+fn;
                            }
                            if(!Strings.isBlank(picname)){
                                picname+=","+fn;
                            }else{
                                picname=fn;
                            }
                            if(!Strings.isBlank(oldpicname)){
                                oldpicname+=","+filename;
                            }else{
                                oldpicname=filename;
                            }
                            orderplanentry.get(i).setPicpath(picpath);
                            orderplanentry.get(i).setPicname(picname);
                            orderplanentry.get(i).setOldpicname(oldpicname);
                            logisticsOrderplanentryService.updateIgnoreNull(orderplanentry);
                        }
                    }
                    return Result.success("上传成功", Globals.AppBase+f);
                }

            }
            return Result.error("错误");
        } catch (Exception e) {
            return Result.error("系统错误");
        } catch (Throwable e) {
            return Result.error("图片格式错误");
        }
    }*/

    /*@At("/delUpload")
    @Ok("jsonp:full")
    public Object delUpload(@Param("filename") String filename,@Param("spid") String spid,@Param("step") String step){
        if(!Strings.isBlank(filename)){
                Cnd cnd=Cnd.NEW();
                cnd.and("orderid","=",spid);
                cnd.and("step","=","9");
                List<logistics_orderplanentry> orderplanentry =logisticsOrderplanentryService.query(cnd);
                if(orderplanentry.size()>0){
                    for(int p=0;p<orderplanentry.size();p++){
                        String oldpicname=orderplanentry.get(p).getOldpicname();
                        String picname=orderplanentry.get(p).getPicname();
                        String picpath=orderplanentry.get(p).getPicpath();
                        if(oldpicname.indexOf(",")>-1){
                            String [] op=oldpicname.split(",");
                            List<String> oplist=java.util.Arrays.asList(op);
                            ArrayList opArray = new ArrayList<>(oplist);
                            String [] pn=picname.split(",");
                            List<String> pnlist=java.util.Arrays.asList(pn);
                            ArrayList pnArray = new ArrayList<>(pnlist);
                            String [] pp=picpath.split(",");
                            List<String> pplist=java.util.Arrays.asList(pp);
                            ArrayList ppArray = new ArrayList<>(pplist);
                            int number=-1;
                            for(int i=0;i<opArray.size();i++){
                                if(ppArray.get(i).toString().equals(filename)){
                                    number=i;
                                    File file=new File(Globals.AppRoot+ppArray.get(i));
                                    if(file.exists()){
                                        file.delete();
                                        break;
                                    }
                                }
                            }

                            *//**************
                             * 修改字段
                             * picpath,picname,oldpicname
                             *//*
                            if(number>-1){
                                String newoldpn="";
                                String newpn="";
                                String newpp="";
                                opArray.remove(number);
                                pnArray.remove(number);
                                ppArray.remove(number);
                                newoldpn=org.apache.commons.lang.StringUtils.join(opArray.toArray(),",");
                                newpn=org.apache.commons.lang.StringUtils.join(pnArray.toArray(),",");
                                newpp=org.apache.commons.lang.StringUtils.join(ppArray.toArray(),",");

                                orderplanentry.get(p).setOldpicname(newoldpn);
                                orderplanentry.get(p).setPicname(newpn);
                                orderplanentry.get(p).setPicpath(newpp);
                                logisticsOrderplanentryService.updateIgnoreNull(orderplanentry);
                                return Result.success("已删除");
                            }
                        }
                        else{
                            if((picpath).equals(filename)){
                                File file=new File(Globals.AppRoot+picpath);
                                if(file.exists()){
                                    file.delete();
                                    *//**************
                                     * 修改字段
                                     * picpath,picname,oldpicname
                                     *//*
                                    orderplanentry.get(p).setOldpicname("");
                                    orderplanentry.get(p).setPicname("");
                                    orderplanentry.get(p).setPicpath("");
                                    logisticsOrderplanentryService.updateIgnoreNull(orderplanentry);
                                    return Result.success("已删除");
                                }
                            }
                        }
                    }

                }
        }
        return Result.error("没有此文件");
    }*/


}
