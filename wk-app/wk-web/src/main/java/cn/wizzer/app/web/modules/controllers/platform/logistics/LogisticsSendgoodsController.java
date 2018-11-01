package cn.wizzer.app.web.modules.controllers.platform.logistics;

import cn.wizzer.app.base.modules.models.base_customer;
import cn.wizzer.app.sys.modules.models.Sys_dict;
import cn.wizzer.app.sys.modules.services.SysDictService;
import cn.wizzer.framework.base.Result;
import cn.wizzer.app.web.commons.slog.annotation.SLog;
import cn.wizzer.framework.page.datatable.DataTableColumn;
import cn.wizzer.framework.page.datatable.DataTableOrder;
import cn.wizzer.framework.util.StringUtil;
import cn.wizzer.app.logistics.modules.models.logistics_sendgoods;
import cn.wizzer.app.logistics.modules.services.LogisticsSendgoodsService;
import cn.wizzer.app.base.modules.services.BaseCnctobjService;
import cn.wizzer.app.logistics.modules.models.logistics_Deliveryorder;
import cn.wizzer.app.logistics.modules.services.LogisticsDeliveryorderService;
import cn.wizzer.app.logistics.modules.models.logistics_orderdelivery;
import cn.wizzer.app.logistics.modules.services.LogisticsOrderdeliveryService;
import cn.wizzer.app.logistics.modules.models.logistics_order;
import cn.wizzer.app.logistics.modules.services.LogisticsOrderService;
import cn.wizzer.app.logistics.modules.models.logistics_orderplan;
import cn.wizzer.app.logistics.modules.services.LogisticsOrderplanService;
import cn.wizzer.app.logistics.modules.models.logistics_sendplan;
import cn.wizzer.app.logistics.modules.services.LogisticsSendplanService;
import cn.wizzer.app.base.modules.services.BaseCustomerService;
import cn.wizzer.app.base.modules.services.BaseWarehouseService;
import cn.wizzer.app.base.modules.models.base_warehouse;
import cn.wizzer.app.logistics.modules.models.logistics_orderplanentry;
import cn.wizzer.app.logistics.modules.services.LogisticsOrderplanentryService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.json.Json;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.*;
import cn.wizzer.app.web.modules.controllers.open.api.Datatime.newDataTime;
import cn.wizzer.app.logistics.modules.models.logistics_unplan;
import cn.wizzer.app.logistics.modules.services.LogisticsUnplanService;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;

@IocBean
@At("/platform/logistics/sendgoods")
public class LogisticsSendgoodsController{
    private static final Log log = Logs.get();
    @Inject
    private LogisticsSendgoodsService logisticsSendgoodsService;
    @Inject
    private BaseCnctobjService baseCnctobjService;
    @Inject
    private LogisticsUnplanService logisticsUnplanService;
    @Inject
    private LogisticsOrderdeliveryService orderdeliveryService;
    @Inject
    private LogisticsDeliveryorderService logisticsDeliveryorderService;
    @Inject
    private SysDictService sysDictService;
    @Inject
    private LogisticsOrderService logisticsOrderService;
    @Inject
    private LogisticsOrderplanService logisticsOrderplanService;
    @Inject
    private LogisticsSendplanService logisticsSendplanService;
    @Inject
    private BaseCustomerService baseCustomerService;
    @Inject
    private BaseWarehouseService baseWarehouseService;
    @Inject
    private LogisticsOrderplanentryService logisticsOrderplanentryService;
    @Inject
    private Dao dao;


    @At("")
    @Ok("beetl:/platform/logistics/sendgoods/index.html")
    @RequiresPermissions("platform.logistics.sendgoods")
    public void index() {

    }

    @At("/data")
    @Ok("json")
    @RequiresPermissions("platform.logistics.sendgoods")
    public Object data(HttpServletRequest req,@Param("bplan") String bplan, @Param("length") int length,
                       @Param("startdate") String startdate,@Param("enddate") String enddate,
                       @Param("contractcode") String contractcode,@Param("partnum") String partnum,
                       @Param("serialnum") String serialnum,@Param("isAir") String isAir,
                       @Param("receivename") String receivename,@Param("delivernum") String billnum,
                       @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns) {
		Cnd cnd = Cnd.NEW();
		cnd.desc("createTime");
		if(!Strings.isBlank(req.getSession().getAttribute("airportid").toString()))
		    cnd.and("airportID","=",req.getSession().getAttribute("airportid").toString());

        if(!Strings.isBlank(bplan)){
		    if(bplan.equals("0"))//新合同
            {
                cnd.and("pstatus","=","1");
            }else if(bplan.equals("1")){//未计划
                cnd.and("pstatus","=","4");
            }else if(bplan.equals("2")){
                cnd.and("pstatus","=","5");
                cnd.or("pstatus","=","6");
            }else if(bplan.equals("3")){
                cnd.and("pstatus","=","7");
            }else if(bplan.equals("4")){
                cnd.and("pstatus","=","8");
            }else if(bplan.equals("5")){
                cnd.and("pstatus","=","2");
            }
            else if(bplan.equals("6")){
                cnd.and("pstatus","=","3");
            }
            else if(bplan.equals("7")){
                cnd.and("pstatus","=","9");
            }
        }
        if(!Strings.isBlank(startdate)&&!Strings.isBlank(enddate)){
            cnd.and("createTime",">=",Integer.parseInt(startdate));
            cnd.and("createTime","<=",Integer.parseInt(enddate));
        }else if(!Strings.isBlank(startdate)&&Strings.isBlank(enddate)) {
            cnd.and("createTime", ">=", Integer.parseInt(startdate));
        }else if(Strings.isBlank(startdate)&&!Strings.isBlank(enddate)){
            cnd.and("createTime","<=",Integer.parseInt(enddate));
        }
        if(!Strings.isBlank(contractcode)){
            cnd.and("contractcode","like","%"+contractcode+"%");
        }
        if(!Strings.isBlank(partnum)){
            cnd.and("partnum","like","%"+partnum+"%");
        }
        if(!Strings.isBlank(serialnum)){
            cnd.and("serialnum","like","%"+serialnum+"%");
        }
        if(!Strings.isBlank(isAir)&&!isAir.equals("-1")){
            cnd.and("isAir","=",isAir.equals("0")?0:1);
        }
        if(!Strings.isBlank(receivename)){
            cnd.and("receivename","like","%"+receivename+"%");
        }
        if(!Strings.isBlank(billnum)){
            cnd.and("billnum","like","%"+billnum+"%");
        }

    	return logisticsSendgoodsService.data(length, start, draw, order, columns, cnd, "warehouse|priority|person|checker|transport|pack");
    }

    //20180228zhf1643
    @At({"/countByType"})
    @Ok("json")
    @RequiresPermissions("platform.logistics.order")
    public Object countByType(HttpServletRequest req,@Param("countOrderByType") String countOrderByType) {
        String airportId = req.getSession().getAttribute("airportid").toString();
        if("sendGoods".equals(countOrderByType)){
            return logisticsSendgoodsService.getJsonDataOfCountOrder(airportId);
        }
        return new NutMap();
    }
    @At("/mdata")
    @Ok("json")
    public Object mdata(@Param("pstatus") String pstatus,@Param("airportid") String airportid
            ,@Param("pagenumber")Integer pagenumber,@Param("pagesize")Integer pagesize){
        if(!Strings.isBlank(pstatus)&&!Strings.isBlank(airportid)){
            return logisticsSendgoodsService.mdata(pstatus,airportid, pagenumber, pagesize);
        }
        return "";
    }

    @At("/add")
    @Ok("beetl:/platform/logistics/sendgoods/add.html")
    @RequiresPermissions("platform.logistics.sendgoods")
    public void add() {

    }

    @At("/addDo")
    @Ok("json")
    @RequiresPermissions("platform.logistics.sendgoods.add")
    @SLog(tag = "合同", msg = "新增合同")
    public Object addDo(HttpServletRequest req,@Param("logisticsSendgoods") String logisticsSendgoods ) {
		try {
//			logisticsSendgoodsService.insert(logisticsSendgoods);
            logistics_sendgoods sendgoods= Json.fromJson(logistics_sendgoods.class,logisticsSendgoods);
            sendgoods.setIntime(newDataTime.getDateYMDHMS());
            logistics_sendgoods s = logisticsSendgoodsService.insert(sendgoods);
            //新增orderplanentry
            for(int i=0;i<9;i++){
                logistics_orderplanentry orderplanentry=new logistics_orderplanentry();
                orderplanentry.setContractid(s.getId());
                switch(i){
                    case 0:
                        orderplanentry.setPstatus(1);
                        orderplanentry.setStep(1);
                        orderplanentry.setStepname("创建合同");
                        orderplanentry.setOperatetime(newDataTime.getDateYMDHMS());
                        orderplanentry.setOperater(req.getSession().getAttribute("personid").toString());
                        logisticsOrderplanentryService.insert(orderplanentry);
                        continue;
                    case 1:
                        orderplanentry.setPstatus(0);
                        orderplanentry.setStep(2);
                        orderplanentry.setStepname("发料完成");
                        logisticsOrderplanentryService.insert(orderplanentry);
                        continue;
                    case 2:
                        orderplanentry.setPstatus(0);
                        orderplanentry.setStep(3);
                        orderplanentry.setStepname("核料完成");
                        logisticsOrderplanentryService.insert(orderplanentry);
                        continue;
                    case 3:
                        orderplanentry.setPstatus(0);
                        orderplanentry.setStep(4);
                        orderplanentry.setStepname("包装完成");
                        logisticsOrderplanentryService.insert(orderplanentry);
                        continue;
                    case 4:
                        orderplanentry.setPstatus(0);
                        orderplanentry.setStep(5);
                        orderplanentry.setStepname("已计划");
                        logisticsOrderplanentryService.insert(orderplanentry);
                        continue;
                    case 5:
                        orderplanentry.setPstatus(0);
                        orderplanentry.setStep(6);
                        orderplanentry.setStepname("审批中");
                        logisticsOrderplanentryService.insert(orderplanentry);
                        continue;
                    case 6:
                        orderplanentry.setPstatus(0);
                        orderplanentry.setStep(7);
                        orderplanentry.setStepname("发货中");
                        logisticsOrderplanentryService.insert(orderplanentry);
                        continue;
                    case 7:
                        orderplanentry.setPstatus(0);
                        orderplanentry.setStep(8);
                        orderplanentry.setStepname("已交货");
                        logisticsOrderplanentryService.insert(orderplanentry);
                        continue;
                    case 8:
                        orderplanentry.setPstatus(0);
                        orderplanentry.setStep(9);
                        orderplanentry.setStepname("已完成");
                        logisticsOrderplanentryService.insert(orderplanentry);
                        continue;
                }
            }
            return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }

    @At("/edit/?")
    @Ok("beetl:/platform/logistics/sendgoods/edit.html")
    @RequiresPermissions("platform.logistics.sendgoods")
    public void edit(String id,HttpServletRequest req) {
		req.setAttribute("obj", logisticsSendgoodsService.fetch(id));
    }

    @At("/editDo")
    @Ok("json")
    @RequiresPermissions("platform.logistics.sendgoods.edit")
    @SLog(tag = "logistics_sendgoods", msg = "${args[0].id}")
    public Object editDo(@Param("..")logistics_sendgoods logisticsSendgoods, HttpServletRequest req) {
		try {
            logisticsSendgoods.setOpBy(StringUtil.getUid());
			logisticsSendgoods.setOpAt((int) (System.currentTimeMillis() / 1000));
			logisticsSendgoodsService.updateIgnoreNull(logisticsSendgoods);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }

    @At({"/delete/?", "/delete"})
    @Ok("json")
    @RequiresPermissions("platform.logistics.sendgoods.delete")
    @SLog(tag = "logistics_sendgoods", msg = "${req.getAttribute('id')}")
    public Object delete(String id, @Param("ids")  String[] ids, HttpServletRequest req) {
		try {
			if(ids!=null&&ids.length>0){
				logisticsSendgoodsService.delete(ids);
    			req.setAttribute("id", org.apache.shiro.util.StringUtils.toString(ids));
                for(int i=0;i<ids.length;i++){
                    Cnd cnd=Cnd.NEW();
                    cnd.and("contractid","=",ids[i]);
                    List<logistics_orderplanentry> orderplanentries=logisticsOrderplanentryService.query(cnd);
                    if(orderplanentries.size()>0){
                        for(int k=0;k<orderplanentries.size();k++){
                            logisticsOrderplanentryService.delete(orderplanentries.get(k).getId());
                        }
                    }
                }
			}else{
				logisticsSendgoodsService.delete(id);
    			req.setAttribute("id", id);
                Cnd cnd=Cnd.NEW();
                cnd.and("contractid","=",id);
                List<logistics_orderplanentry> orderplanentries=logisticsOrderplanentryService.query(cnd);
                if(orderplanentries.size()>0){
                    for(int k=0;k<orderplanentries.size();k++){
                        logisticsOrderplanentryService.delete(orderplanentries.get(k).getId());
                    }
                }
			}



            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }

    @At("/detail/?")
    @Ok("beetl:/platform/logistics/sendgoods/detail.html")
    @RequiresPermissions("platform.logistics.sendgoods")
	public void detail(String id, HttpServletRequest req) {
		if (!Strings.isBlank(id)) {
            logistics_sendgoods logisticsSendgoods = logisticsSendgoodsService.fetch(id);
            req.setAttribute("obj", logisticsSendgoodsService.fetchLinks(logisticsSendgoods,"warehouse|priority|person|pack|plan|transport"));

            logisticsSendgoods = logisticsSendgoodsService.fetchLinks(logisticsSendgoods, "warehouse|priority|person");
            Sys_dict transport = logisticsSendgoods.getTransport();
            //System.out.println(transport);
            //包装对象
            logistics_unplan unplan = logisticsSendgoods.getPack();
            req.setAttribute("unplan",logisticsUnplanService.fetchLinks(unplan,"person"));
		    //计划对象
            /*logistics_sendplan plan = logisticsSendgoods.getPlan();
            req.setAttribute("plan",logisticsSendplanService.fetchLinks(plan,"person"));*/
        }else{
            req.setAttribute("obj", null);
        }
    }

    ///发料完成
    @At({"/upFlSend"})
    @Ok("json")
    @RequiresPermissions("platform.logistics.sendgoods")
    public Object upFlSend(@Param("ids")String [] ids,@Param("pstatus") String pstatus,HttpServletRequest req) {
        try {
            for(int i=0;i<ids.length;i++)
            {
                logistics_sendgoods sendgoods = logisticsSendgoodsService.fetch(ids[i]);
                //变更状态为2

                if(!Strings.isBlank(pstatus)){
                    sendgoods.setPstatus(Integer.parseInt(pstatus));
                }
                logisticsSendgoodsService.updateIgnoreNull(sendgoods);

                Cnd cnd=Cnd.NEW();
                cnd.and("contractid","=",sendgoods.getId());
                cnd.and("step","=",pstatus);
                logistics_orderplanentry orderplanentry=logisticsOrderplanentryService.fetch(cnd);
                if(orderplanentry!=null){
                    orderplanentry.setOperater(req.getSession().getAttribute("personid").toString());
                    orderplanentry.setOperatetime(newDataTime.getDateYMDHMS());
                    orderplanentry.setPstatus(1);
                    logisticsOrderplanentryService.updateIgnoreNull(orderplanentry);
                }
            }


            return Result.success("system.success");
        }catch (Exception e) {
            return Result.error("system.error");
        }

    }

    /*@At("/getGoodsInfo")
    @Ok("jsonp:full")
    public Object getGoodsInfo(@Param("hlid") String hlid){
        try{
            if(!Strings.isBlank(hlid)){
              logistics_sendgoods sendgoods = logisticsSendgoodsService.fetch(hlid);
              return sendgoods;
            }
            return null;
        }catch(Exception e){
            return null;
        }
    }*/

    /*//核料/包装
    @At("/updateSgStatus")
    @Ok("jsonp:full")
    public Object updateSgStatus(@Param("bzid") String bzid,@Param("transporttype") String transporttype,@Param("packtype") String packtype,@Param("airportid") String airportid,@Param("materielnum") String materielnum, @Param("id") String id,@Param("pstatus") String pstatus,@Param("personid") String personid,@Param("note") String note,@Param("isAir") boolean isAir){
        try{
            String unplanid="0";
            if(!Strings.isBlank(id)){
                logistics_sendgoods sendgoods = logisticsSendgoodsService.fetch(id);
                if(sendgoods!=null){
                    String now = newDataTime.getDateYMDHMS();
                    sendgoods.setPstatus(Integer.parseInt(pstatus));
                    if(pstatus.equals("3")){//核料
                        sendgoods.setHlpersonid(personid);
                        sendgoods.setHlnote(note);
                        sendgoods.setHltime(now);
                        sendgoods.setIsAir(isAir);

                        logistics_unplan unplan=new logistics_unplan();
                        unplan.setPacker(personid);
                        logistics_unplan u = logisticsUnplanService.insert(unplan);
                        sendgoods.setPackID(u.getId());
                        logisticsSendgoodsService.updateIgnoreNull(sendgoods);
                        unplanid=u.getId();

                    }else if(pstatus.equals("4")){//包装
                        logisticsSendgoodsService.updateIgnoreNull(sendgoods);
//                        sendgoods.setBzpersonid(personid);
//                        sendgoods.setBznote(note);
//                        sendgoods.setBztime(now);
                        //更新包装数据
                        if(!Strings.isBlank(bzid)){
                            logistics_unplan unplan=logisticsUnplanService.fetch(bzid);
                            unplan.setAirportID(airportid);
                            unplan.setMaterielnum(materielnum);
                            unplan.setNote(note);
                            unplan.setPacker(personid);
                            unplan.setPacktype(packtype);
                            unplan.setPackdate(now);
                            unplan.setTansporttype(transporttype);
                            logisticsUnplanService.updateIgnoreNull(unplan);
                        }

                    }
                    //修改logisticsOrderplanentry
                    Cnd cnd=Cnd.NEW();
                    cnd.and("contractid","=",sendgoods.getId());
                    cnd.and("step","=",pstatus);
                    logistics_orderplanentry orderplanentry=logisticsOrderplanentryService.fetch(cnd);
                    if(orderplanentry!=null){
                        orderplanentry.setOperater(personid);
                        orderplanentry.setOperatetime(newDataTime.getDateYMDHMS());
                        orderplanentry.setPstatus(1);
                        orderplanentry.setContent(note);
                        logisticsOrderplanentryService.updateIgnoreNull(orderplanentry);
                    }
                    return unplanid;
                }
            }
            return null;
        }catch(Exception e){
            return null;
        }
    }
*/

    /*@At("/img")
    @Ok("jsonp:full")
    //AdaptorErrorContext必须是最后一个参数
    public Object img(@Param("filename") String filename, @Param("base64") String base64,@Param("hlid") String hlid,@Param("bzid") String bzid,@Param("step") String step,HttpServletRequest req, AdaptorErrorContext err) {
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
//                Cnd cnd=Cnd.NEW();
//                cnd.and("id","=",orderid);
//                cnd.and("step","=",step);
                if(step.equals("HL")){
                    Cnd cnd = Cnd.NEW();
                    cnd.and("contractid","=",hlid);
                    cnd.and("step","=","3");
                    logistics_orderplanentry orderplanentry=logisticsOrderplanentryService.fetch(cnd);

                    if(orderplanentry!=null){
                        String picpath=orderplanentry.getPicpath();
                        String picname=orderplanentry.getPicname();
                        String oldpicname=orderplanentry.getOldpicname();
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
                        orderplanentry.setPicpath(picpath);
                        orderplanentry.setPicname(picname);
                        orderplanentry.setOldpicname(oldpicname);
                        logisticsOrderplanentryService.updateIgnoreNull(orderplanentry);

                    }
                }else{//BZ
                    logistics_unplan unplan=logisticsUnplanService.fetch(bzid);
                    Cnd cnd=Cnd.NEW();
                    cnd.and("contractid","=",hlid);
                    cnd.and("step","=","4");
                    logistics_orderplanentry orderplanentry=logisticsOrderplanentryService.fetch(cnd);
                    if(unplan!=null){
                        String picpath=unplan.getPicpath();
                        String picname=unplan.getPicname();
                        String oldpicname=unplan.getOldpicname();
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
                        unplan.setPicpath(picpath);
                        unplan.setPicname(picname);
                        unplan.setOldpicname(oldpicname);
                        logisticsUnplanService.updateIgnoreNull(unplan);

                    }
                    if(orderplanentry!=null){
                        String picpath=orderplanentry.getPicpath();
                        String picname=orderplanentry.getPicname();
                        String oldpicname=orderplanentry.getOldpicname();
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
                        orderplanentry.setPicpath(picpath);
                        orderplanentry.setPicname(picname);
                        orderplanentry.setOldpicname(oldpicname);
                        logisticsOrderplanentryService.updateIgnoreNull(orderplanentry);

                    }
                }

                return Result.success("上传成功", Globals.AppBase+f);
            }
        } catch (Exception e) {
            return Result.error("系统错误");
        } catch (Throwable e) {
            return Result.error("图片格式错误");
        }
    }
*/

 /*   @At("/delUpload")
    @Ok("jsonp:full")
    public Object delUpload(@Param("filename") String filename,@Param("hlid") String hlid,@Param("bzid") String bzid,@Param("step") String step){
        if(!Strings.isBlank(filename)){
            if(step.equals("HL")){
//                logistics_sendgoods sendgoods= logisticsSendgoodsService.fetch(hlid);
                Cnd cnd=Cnd.NEW();
                cnd.and("contractid","=",hlid);
                cnd.and("step","=","4");
                logistics_orderplanentry orderplanentry =logisticsOrderplanentryService.fetch(cnd);
                if(orderplanentry!=null){
                    String oldpicname=orderplanentry.getOldpicname();
                    String picname=orderplanentry.getPicname();
                    String picpath=orderplanentry.getPicpath();
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

                            orderplanentry.setOldpicname(newoldpn);
                            orderplanentry.setPicname(newpn);
                            orderplanentry.setPicpath(newpp);
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
                                orderplanentry.setOldpicname("");
                                orderplanentry.setPicname("");
                                orderplanentry.setPicpath("");
                                logisticsOrderplanentryService.updateIgnoreNull(orderplanentry);
                                return Result.success("已删除");
                            }
                        }
                    }
                }
            }
            else if(step.equals("BZ")){
                logistics_unplan unplan= logisticsUnplanService.fetch(bzid);
                Cnd cnd=Cnd.NEW();
                cnd.and("contractid","=",bzid);
                cnd.and("step","=","4");
                logistics_orderplanentry orderplanentry=logisticsOrderplanentryService.fetch(cnd);
                if(unplan!=null){
                    String oldpicname=unplan.getOldpicname();
                    String picname=unplan.getPicname();
                    String picpath=unplan.getPicpath();
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

                            unplan.setOldpicname(newoldpn);
                            unplan.setPicname(newpn);
                            unplan.setPicpath(newpp);
                            logisticsUnplanService.updateIgnoreNull(unplan);
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
                                unplan.setOldpicname("");
                                unplan.setPicname("");
                                unplan.setPicpath("");
                                logisticsUnplanService.updateIgnoreNull(unplan);
                            }
                        }
                    }
                }

                if(orderplanentry!=null){
                    String oldpicname=orderplanentry.getOldpicname();
                    String picname=orderplanentry.getPicname();
                    String picpath=orderplanentry.getPicpath();
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

                            orderplanentry.setOldpicname(newoldpn);
                            orderplanentry.setPicname(newpn);
                            orderplanentry.setPicpath(newpp);
                            logisticsOrderplanentryService.updateIgnoreNull(orderplanentry);

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
                                orderplanentry.setOldpicname("");
                                orderplanentry.setPicname("");
                                orderplanentry.setPicpath("");
                                logisticsOrderplanentryService.updateIgnoreNull(orderplanentry);

                            }
                        }
                    }
                }
                return Result.success("已删除");
            }

        }
        return Result.error("没有此文件");
    }*/

    @At("/mergeplan")
    @Ok("json")
    public Object mergeplan(HttpServletRequest req, @Param("ids") String [] ids,@Param("pstatus") String pstatus,@Param("transtype") String transtype,@Param("senddate") String senddate,@Param("ptid") String pricetype,@Param("tempprice") String tempprice,@Param("protocol") String protocol,@Param("free") String free){
        try{
            //1.判断ids是否有值
            //2、新增一个运单，isTF改为F 运单状态为1
            //3、新增一个订单，对应合同
            //3、将现有订单ID对应运单id到运单订单对应表orderdelivery
            //4、将现有合同的状态改为5已计划
            int number=0;

            if(ids.length>0){
                //1 新增一条运单
                logistics_Deliveryorder deliveryorder=new logistics_Deliveryorder();
                //新增运单号 未完成 延后再补
                deliveryorder.setIsTF("0");
                deliveryorder.setPstatus(1);
                deliveryorder.setDatetime(newDataTime.getDateYMDHMS());

                //生成配送单编号
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                String ddate = formatter.format(new Date());
                String dnum="";
                Cnd cnd2 = Cnd.NEW();
                cnd2.and("DATE_FORMAT(datetime, '%Y-%m-%d')","=",ddate);
                logistics_Deliveryorder delorder=logisticsDeliveryorderService.fetch(cnd2);

                if(delorder == null){
                    dnum="PSD-"+ddate+"-0001";
                }else{

                    List<logistics_Deliveryorder> dorder=logisticsDeliveryorderService.query("select max(right(deliveryordernum,4)) from logistics_order");
                    if(dorder.size()==0) {
                        dnum = "PSD-" + ddate + "-0001";

                    }else{
                        String ordernum = dorder.get(0).getDeliveryordernum();
                        int _num= (Integer.parseInt(ordernum.substring(ordernum.length()- 4))+1);
                        dnum=String.format("%04d", _num);
                        dnum="PSD"+"-"+ddate+"-"+dnum;
                    }
                }
                deliveryorder.setDeliveryordernum(dnum);

                logistics_Deliveryorder deliver = logisticsDeliveryorderService.insert(deliveryorder);

                //2新增一条订单
                logistics_order order=new logistics_order();

                order.setIsTF("0");
                order.setPstatus(98); //98代表未审批
                order.setPersonid(req.getSession().getAttribute("personid").toString());//登记人，需要当前创建人id
                order.setDeliveryorderid(deliver.getId());
                //插入客户为东航成都 后面要根据实际来改变
                Cnd c=Cnd.NEW();
                c.and("customercode","=","0001");
                base_customer customer=baseCustomerService.fetch(c);
                if(customer!=null){
                    order.setCustomerid(customer.getId());
                }
                String date=newDataTime.getDateYMD();
                order.setIntime(date);
                //新增订单号
                String num="";
                Cnd cnd1 = Cnd.NEW();
                cnd1.and("DATE_FORMAT( intime, '%Y-%m-%d')","=",date);
                cnd1.and("customerid","=",customer.getId());

                logistics_order lorder= logisticsOrderService.fetch(cnd1);

                List<logistics_order> logisticsOrders=logisticsOrderService.query(cnd1);
                ArrayList list = new ArrayList();
                if(logisticsOrders.size() == 0){
                    num=customer.getCusnum()+"-"+date+"-0001";
                }else{
                    for(logistics_order lo:logisticsOrders){
                        String _num=lo.getOrdernum().substring(lo.getOrdernum().lastIndexOf("-")+1,lo.getOrdernum().length());
                        String newStr = _num.replaceFirst("^0*", "");
                        list.add(newStr);
                    }
                    Collections.sort(list);
                    int n =Integer.parseInt(list.get(list.size()-1).toString());
                    num=String.format("%04d", n+1);
                    num=customer.getCusnum()+"-"+date+"-"+num;
                }
                order.setOrdernum(num);
                order.setAirportid(req.getSession().getAttribute("airportid").toString());

                logistics_order o = logisticsOrderService.insert(order);

                //2.5新增一条订单附加信息
                logistics_sendplan sendplan=new logistics_sendplan();
                sendplan.setOrderid(o.getId());
                sendplan.setPstatus(0);//0 已计划 1审批中 2 发货中 3 已交货 4 已完成
                if(!Strings.isBlank(transtype))
                sendplan.setTansporttype(transtype);

                sendplan.setSenddate(newDataTime.getDateYMDHMS());
                if(!Strings.isBlank(pricetype)){
                    sendplan.setCosttype(pricetype);
                }
                if(!Strings.isBlank(tempprice)){
                    sendplan.setTempprice(Float.parseFloat(tempprice));
                }
                if(!Strings.isBlank(protocol)){
                    sendplan.setProtocolnum(protocol);
                }
                if(!Strings.isBlank(free)){
                    sendplan.setFreenum(free);
                }
                sendplan.setPersonid(req.getSession().getAttribute("personid").toString());
                logistics_sendplan plan = logisticsSendplanService.insert(sendplan);
                o.setSendplanid(sendplan.getId());
                logisticsOrderService.updateIgnoreNull(o);

                //3将订单和运单对应
                logistics_orderdelivery orderdelivery=new logistics_orderdelivery();
                orderdelivery.setDeliveryorderid(deliver.getId());
                orderdelivery.setOrderid(o.getId());
                orderdeliveryService.insert(orderdelivery);




                for(int i=0;i<ids.length;i++){

                    //4生成一条合同订单对应数据
                    logistics_orderplan orderplan=new logistics_orderplan();
                    orderplan.setContractid(ids[i]);
                    orderplan.setOrderid(o.getId());
                    logisticsOrderplanService.insert(orderplan);

                    //将合同对应的订单和运单更新到orderplanentry
                    Cnd cn=Cnd.NEW();
                    cn.and("contractid","=",ids[i]);
                    List<logistics_orderplanentry> logistics_orderplanentries=logisticsOrderplanentryService.query(cn);
                    if(logistics_orderplanentries.size()>0){
                        for(int k=0;k<logistics_orderplanentries.size();k++){
                            logistics_orderplanentries.get(k).setDeliveryorderid(deliver.getId());
                            logistics_orderplanentries.get(k).setOrderid(o.getId());
                            logisticsOrderplanentryService.updateIgnoreNull(logistics_orderplanentries.get(k));
                        }
                    }
                    //将orderplanentry对应的已计划pstatus改为1
                    cn.and("step","=",pstatus);
                    logistics_orderplanentry orderplanentry=logisticsOrderplanentryService.fetch(cn);
                    orderplanentry.setPstatus(1);
                    orderplanentry.setOperater(req.getSession().getAttribute("personid").toString());
                    orderplanentry.setOperatetime(newDataTime.getDateYMDHMS());
                    logisticsOrderplanentryService.updateIgnoreNull(orderplanentry);


                    Cnd cnd=Cnd.NEW();
                    cnd.and("id","=",ids[i]);
                    List<logistics_sendgoods> sendgoods =logisticsSendgoodsService.query(cnd,"warehouse");
                    if(sendgoods.size()==1){
                        //更新合同相关数据
                        if(!Strings.isBlank(pstatus))
                        sendgoods.get(0).setPstatus(Integer.parseInt(pstatus));

                        if(!Strings.isBlank(senddate))
                        sendgoods.get(0).setSenddate(senddate);
//                        String transid=sysDictService.getIdByCode(transtype);//0空运 ，1陆运 ，2 上门自提
                        sendgoods.get(0).setTansporttype(transtype);
                        sendgoods.get(0).setPlanID(sendplan.getId());

                        logisticsSendgoodsService.updateIgnoreNull(sendgoods.get(0));

                        //更新包装表数据
                        logistics_unplan unplan=logisticsUnplanService.fetch(sendgoods.get(0).getPackID());
                        unplan.setTansporttype(transtype);
                        unplan.setPlanID(deliver.getId());
                        logisticsUnplanService.updateIgnoreNull(unplan);
                        number++;



                        if(i==0){
                            if(sendgoods.get(0).getWarehouse()!=null)
                            o.setWarehousename(sendgoods.get(0).getWarehouse().getWarehousename());
                            o.setReceivename(sendgoods.get(0).getReceivename());
                            logisticsOrderService.updateIgnoreNull(o);
                            plan.setReceivename(sendgoods.get(0).getReceivename());
                            plan.setReceiveaddress(sendgoods.get(0).getReceiveaddress());
                            plan.setTansporttype(sendgoods.get(0).getTansporttype());
                            logisticsSendplanService.updateIgnoreNull(plan);
                        }
                    }
                }

            }
            if(number==ids.length){
                return Result.success("运单生成成功");
            }
            else{
                return Result.error("运单生成错误");
            }
        }
        catch(Exception e){
            return Result.error("运单生成错误");
        }
    }

    @At("/insertExcel")
    @Ok("json")
    public Object insertExcel(@Param("contractcode") String contractcode,@Param("partnum") String partnum,
                              @Param("serialnum") String serialnum,@Param("number") String number,@Param("shelf") String shelf,
                              @Param("batchnum") String batchnum,@Param("issuetype") String issuetype,@Param("rough") String rough,
                              @Param("warehouse") String warehouse,@Param("priority") String priority,@Param("receivename") String receivename,
                              @Param("receiveaddress") String receiveaddress,HttpServletRequest req)
    {
        try{
            logistics_sendgoods sendgoods=new logistics_sendgoods();
            if(!Strings.isBlank(contractcode))
                sendgoods.setContractcode(contractcode);
            if(!Strings.isBlank(partnum))
                sendgoods.setPartnum(partnum);
            if(!Strings.isBlank(serialnum))
                sendgoods.setSerialnum(serialnum);
            if(!Strings.isBlank(number)){
                float n=0;
                try{
                    n=Float.parseFloat(number);
                }catch(Exception ex){

                }
                sendgoods.setNumber(n);
            }

            if(!Strings.isBlank(shelf))
                sendgoods.setShelf(shelf);
            if(!Strings.isBlank(batchnum))
                sendgoods.setBatchnum(batchnum);
            if(!Strings.isBlank(issuetype))
                sendgoods.setIssuetype(issuetype);
            if(!Strings.isBlank(rough))
                sendgoods.setRough(rough);
            if(!Strings.isBlank(warehouse))
            {
                Cnd cnd=Cnd.NEW();
                cnd.and("whnum","=",warehouse);
                base_warehouse wh=baseWarehouseService.fetch(cnd);
                if(wh!=null){
                    sendgoods.setWarehouseID(wh.getId());
                }
            }
            if(!Strings.isBlank(priority))
            {
                String priorityID = sysDictService.getIdByName(priority);
                sendgoods.setPrioritylv(priorityID);
            }
            if(!Strings.isBlank(receivename)){
                sendgoods.setReceivename(receivename);
            }
            if(!Strings.isBlank(receiveaddress)){
                sendgoods.setReceiveaddress(receiveaddress);
            }
            sendgoods.setPstatus(1);
            sendgoods.setAirportID(req.getSession().getAttribute("airportid").toString());
            sendgoods.setPersonid(req.getSession().getAttribute("personid").toString());
            sendgoods.setIntime(newDataTime.getDateYMDHMS());
            logistics_sendgoods s = logisticsSendgoodsService.insert(sendgoods);

            //新增orderplanentry
            for(int i=0;i<9;i++){
                logistics_orderplanentry orderplanentry=new logistics_orderplanentry();
                orderplanentry.setContractid(s.getId());
                switch(i){
                    case 0:
                        orderplanentry.setPstatus(1);
                        orderplanentry.setStep(1);
                        orderplanentry.setStepname("创建合同");
                        orderplanentry.setOperatetime(newDataTime.getDateYMDHMS());
                        orderplanentry.setOperater(req.getSession().getAttribute("personid").toString());
                        break;
                    case 1:
                        orderplanentry.setPstatus(0);
                        orderplanentry.setStep(2);
                        orderplanentry.setStepname("发料完成");
                        break;
                    case 2:
                        orderplanentry.setPstatus(0);
                        orderplanentry.setStep(3);
                        orderplanentry.setStepname("核料完成");
                        break;
                    case 3:
                        orderplanentry.setPstatus(0);
                        orderplanentry.setStep(4);
                        orderplanentry.setStepname("包装完成");
                        break;
                    case 4:
                        orderplanentry.setPstatus(0);
                        orderplanentry.setStep(5);
                        orderplanentry.setStepname("已计划");
                        break;
                    case 5:
                        orderplanentry.setPstatus(0);
                        orderplanentry.setStep(6);
                        orderplanentry.setStepname("审批中");
                        break;
                    case 6:
                        orderplanentry.setPstatus(0);
                        orderplanentry.setStep(7);
                        orderplanentry.setStepname("发货中");
                        break;
                    case 7:
                        orderplanentry.setPstatus(0);
                        orderplanentry.setStep(8);
                        orderplanentry.setStepname("已交货");
                        break;
                    case 8:
                        orderplanentry.setPstatus(0);
                        orderplanentry.setStep(9);
                        orderplanentry.setStepname("已完成");
                        break;
                }
                logisticsOrderplanentryService.insert(orderplanentry);
            }
            return Result.success("合同导入完成");
        }catch(Exception e){
            return null;
        }
    }

    /*@At("/getListbyPlanID")
    @Ok("jsonp:full")
    public Object getListbyPlanID(@Param("planid") String planid){
        try{
            if(!Strings.isBlank(planid)){
//                Cnd cnd=Cnd.NEW();
//                cnd.and("planID","=",planid);
//                List<logistics_sendgoods> sendgoodsList=logisticsSendgoodsService.query(cnd,"pack|transport");
//                return sendgoodsList;

                String sqlstr="select a.*,unplan.materielnum packnum,d.name packname,(case a.tansporttype when '0' then '空运' when '1' then '陆运' when '2' then '上门自提' else '' end) transname from logistics_sendgoods a left join logistics_unplan unplan on a.packID=unplan.id" +
                        " left join sys_dict d on unplan.packtype=d.id where  a.planID='"+planid+"' ";
                Sql sql= Sqls.queryRecord(sqlstr);

                dao.execute(sql);
                List<Record> res = sql.getList(Record.class);
                return res;
            }
            return null;
        }catch(Exception e){
            return null;
        }
    }*/

    @At("/getSendgoodsByPlanID")
    @Ok("json")
    public Object getSendgoodsByPlanID(@Param("planid") String planid){
        try{
            if(!Strings.isBlank(planid)){
//                String sqlstr="select a.*,unplan.materielnum packnum,d.name packname,e.warehousename,(case a.tansporttype when '0' then '空运' when '1' then '陆运' when '2' then '上门自提' else '' end) transname from logistics_sendgoods a left join logistics_unplan unplan on a.packID=unplan.id" +
//                        " left join sys_dict d on unplan.packtype=d.id left join base_warehouse e on a.warehouseID=e.id  where  a.planID='"+planid+"' ";
//                Sql sql= Sqls.queryRecord(sqlstr);
//
//                dao.execute(sql);
//                List<Record> res = sql.getList(Record.class);
//                return res;
                Cnd cnd =Cnd.NEW();
                cnd.and("planID","=",planid);
                return logisticsSendgoodsService.query(cnd,"airport|transport|warehouse|priority|person|pack|checker|hlperson");

            }
            return null;
        }catch(Exception e){
            return null;
        }
    }

}
