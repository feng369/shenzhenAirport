package cn.wizzer.app.web.modules.controllers.open.api.mobile;

import cn.wizzer.app.base.modules.models.base_personpool;
import cn.wizzer.app.base.modules.services.BasePersonpoolService;
import cn.wizzer.app.logistics.modules.models.*;
import cn.wizzer.app.logistics.modules.services.*;
import cn.wizzer.app.web.commons.base.Globals;
import cn.wizzer.app.web.commons.filter.TokenFilter;
import cn.wizzer.app.web.commons.slog.annotation.SLog;
import cn.wizzer.app.web.modules.controllers.open.api.Datatime.newDataTime;
import cn.wizzer.framework.base.Result;
import cn.wizzer.framework.page.datatable.DataTableColumn;
import cn.wizzer.framework.page.datatable.DataTableOrder;
import cn.wizzer.framework.util.DateUtil;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Record;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.util.cri.Static;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.lang.random.R;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.annotation.*;
import org.nutz.mvc.filter.CrossOriginFilter;
import org.nutz.mvc.impl.AdaptorErrorContext;
import org.nutz.plugins.apidoc.annotation.Api;
import org.nutz.plugins.apidoc.annotation.ApiMatchMode;
import org.nutz.plugins.apidoc.annotation.ApiParam;
import org.nutz.plugins.apidoc.annotation.ReturnKey;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@IocBean
@At("/open/mobile/send")
@Filters({@By(type = TokenFilter.class)})
@Api(name = "提发货", match = ApiMatchMode.ALL, description = "提发货相关api接口")
public class MobileSendController {


    @Inject
    Dao dao;
    @Inject
    private LogisticsSendgoodsService logisticsSendgoodsService;
    @Inject
    private LogisticsOrderService logisticsOrderService;
    @Inject
    private LogisticsDeliveryorderService logisticsDeliveryorderService;
    @Inject
    private LogisticsOrderdeliveryService logisticsOrderdeliveryService;
    @Inject
    private LogisticsOrderentryService logisticsOrderentryService;
    @Inject
    private LogisticsDeliveryorderentryService logisticsDeliveryorderentryService;
    @Inject
    private BasePersonpoolService basePersonpoolService;
    @Inject
    private LogisticsEntourageService logisticsEntourageService;
    @Inject
    private LogisticsOrderstepService logisticsOrderstepService;
    @Inject
    private LogisticsOrderrejectService logisticsOrderrejectService;
    @Inject
    private LogisticsSendplanService logisticsSendplanService;
    @Inject
    private  LogisticsOrderplanService logisticsOrderplanService;
    @Inject
    private LogisticsOrderplanentryService logisticsOrderplanentryService;
    @Inject
    private LogisticsUnplanService logisticsUnplanService;

    @At("/mdata")
    @Ok("json")
    @Api(name = "提发货待核料与待包装列表"
            , params = {
            @ApiParam(name = "pstatus", type = "String", description = "2 待核料，3 待包装"),
            @ApiParam(name = "airportid", type = "String", description = "机场ID"), @ApiParam(name = "pagenumber", type = "Integer", description = "当前页")
            , @ApiParam(name = "pagesize", type = "Integer", description = "默认返回10条",optional = true)
    },
            ok = {
            @ReturnKey(key = "contractcode", description = "合同号"),
            @ReturnKey(key = "receivename", description = "收货方"),
            @ReturnKey(key = "id", description = "发货对象id"),
            @ReturnKey(key = "packID", description = "包装ID"),
    }
    )
    @Filters({@By(type = CrossOriginFilter.class), @By(type = TokenFilter.class)})
    public Object mdata(@Param("pstatus") String pstatus,@Param("airportid") String airportid,@Param("pagenumber")Integer pagenumber,@Param("pagesize")Integer pagesize){
        try {
            if(!Strings.isBlank(pstatus)&&!Strings.isBlank(airportid)){
                return Result.success("system.success",logisticsSendgoodsService.mdata(pstatus,airportid,pagenumber,pagesize));
            }
            return Result.error("system.error");
        }catch (Exception e){
            e.printStackTrace();
            return Result.error("system.error");
        }
    }


    @At("/planAndBeforeSendData")
    @Ok("json")
    @Api(name = "提发货计划中与待发货列表"
            , params = {
            @ApiParam(name = "pstatus", type = "String", description = "0和1 计划中，2 待交货"),
            @ApiParam(name = "airportid", type = "String", description = "机场ID"), @ApiParam(name = "pagenumber", type = "Integer", description = "当前页")
            , @ApiParam(name = "pagesize", type = "Integer", description = "默认返回10条",optional = true)
    },
            ok = {@ReturnKey(key = "res", description = "提发货计划中与待发货列表")}
    )
    @Filters({@By(type = CrossOriginFilter.class), @By(type = TokenFilter.class)})
    public Object planAndBeforeSendData(@Param("pstatus") String pstatus,@Param("airportid") String airportid,@Param("pagenumber")Integer pagenumber,@Param("pagesize")Integer pagesize){
        if(!Strings.isBlank(pstatus)&&!Strings.isBlank(airportid)){

            return Result.success("system.success",logisticsSendgoodsService.planAndBeforeSendData(pstatus,airportid,pagenumber,pagesize));
        }
        return Result.error("system.error");
    }

    @At("/updateDelivergoods")
    @Ok("json")
    @Api(name = "提交交货"
            , params = {
            @ApiParam(name = "spid", type = "String", description = "计划ID"),
            @ApiParam(name = "deliverynum", type = "String", description = "运单号"),
            @ApiParam(name = "packnum", type = "String", description = "件数"),
            @ApiParam(name = "weight", type = "String", description = "运单号"),
            @ApiParam(name = "flightnum", type = "String", description = "航班号"),
            @ApiParam(name = "thirdname", type = "String", description = "第三方名称"),
            @ApiParam(name = "drivername", type = "String", description = "取货司机"),
            @ApiParam(name = "carnum", type = "String", description = "车牌号"),
            @ApiParam(name = "personid", type = "String", description = "员工ID")
    },
            ok = {@ReturnKey(key = "code", description = "成功时code=0")}
    )
    @Filters({@By(type = CrossOriginFilter.class), @By(type = TokenFilter.class)})
    public Object updateDelivergoods(@Param("personid") String personid, @Param("spid") String id,@Param("deliverynum") String deliverynum,@Param("packnum") String packnum,@Param("weight") String weight,@Param("flightnum") String flightnum,@Param("thirdname") String thirdname,@Param("drivername") String drivername,@Param("carnum") String carnum){
        try{
           return  logisticsOrderplanentryService.updateDelivergoods(personid,id,deliverynum,packnum,weight,flightnum,thirdname,drivername,carnum);

        }catch(Exception e){
            return Result.error("syste.error");
        }
    }


    @At("/delSendImg")
    @Ok("json")
    @Api(name = "提发货计划照片删除"
            , params = {
            @ApiParam(name = "filename", type = "String", description = "文件名"),
            @ApiParam(name = "spid", type = "String", description = "计划ID"),
            @ApiParam(name = "step", type = "String", description = "阶段，1创建需求-2发料完成-3核料-4包装-5计划-6审批-7发货中-8交货-9完成")
    },
            ok = {@ReturnKey(key = "code", description = "成功code=0")}
    )
    @Filters({@By(type = CrossOriginFilter.class), @By(type = TokenFilter.class)})
    public Object delSendImg(@Param("filename") String filename,@Param("spid") String spid,@Param("step") String step){
        if(!Strings.isBlank(filename)){
            Cnd cnd=Cnd.NEW();
            cnd.and("orderid","=",spid);
            cnd.and("step","=","9");//???? step参数没用到
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

                        /**************
                         * 修改字段
                         * picpath,picname,oldpicname
                         */
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
                                /**************
                                 * 修改字段
                                 * picpath,picname,oldpicname
                                 */
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
    }

    @At("/getGoodsInfo")
    @Ok("json")
    @Api(name = "得到发货订单合同信息"
            , params = {
            @ApiParam(name = "hlid", type = "String", description = "订单ID")
            },
            ok = {
            @ReturnKey(key = "contractcode", description = "合同编号"),
            @ReturnKey(key = "receivename", description = "收货方名称"),
            @ReturnKey(key = "partnum", description = "件号"),
            @ReturnKey(key = "serialnum", description = "序号"),
            @ReturnKey(key = "number", description = "数量"),
            }
    )
    @Filters({@By(type = CrossOriginFilter.class), @By(type = TokenFilter.class)})
    public Object getGoodsInfo(@Param("hlid") String hlid){
        try{
            if(!Strings.isBlank(hlid)){
                return Result.success("system.success",logisticsSendgoodsService.getGoodsInfo(hlid));
            }
            return Result.error("system.error");
        }catch(Exception e){
            return Result.error("system.error");
        }
    }
    @At("/delCheckAndPackUpload")
    @Ok("json")
    @Api(name = "删除照片（核料、包装）"
            , params = {
            @ApiParam(name = "filename", type = "String", description = "文件名"),
            @ApiParam(name = "hlid", type = "String", description = "订单合同ID"),
            @ApiParam(name = "step", type = "String", description = "阶段")
 ,
            @ApiParam(name = "bzid", type = "String", description = "包装表ID")
    },
            ok = {@ReturnKey(key = "code", description = "成功code=0")}
    )
    @Filters({@By(type = CrossOriginFilter.class), @By(type = TokenFilter.class)})
    public Object delCheckAndPackUpload(@Param("filename") String filename,@Param("hlid") String hlid,@Param("bzid") String bzid,@Param("step") String step){
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

                        /**************
                         * 修改字段
                         * picpath,picname,oldpicname
                         */
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
                                /**************
                                 * 修改字段
                                 * picpath,picname,oldpicname
                                 */
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

                        /**************
                         * 修改字段
                         * picpath,picname,oldpicname
                         */
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
                                /**************
                                 * 修改字段
                                 * picpath,picname,oldpicname
                                 */
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

                        /**************
                         * 修改字段
                         * picpath,picname,oldpicname
                         */
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
                                /**************
                                 * 修改字段
                                 * picpath,picname,oldpicname
                                 */
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
    }

    //核料/包装
    @At("/updateSgStatus")
    @Ok("json")
    @Api(name = "进行核料/包装工作，并修改跟踪表"
            , params = {
            @ApiParam(name = "id", type = "String", description = "合同订单ID"),
            @ApiParam(name = "bzid", type = "String", description = "包装表ID"),
            @ApiParam(name = "note", type = "String", description = "备注")
            ,
            @ApiParam(name = "pstatus", type = "String", description = "阶段，3核料，4包装"),
            @ApiParam(name = "isAir", type = "String", description =  "是否禁航，true为是，false为否"),
            @ApiParam(name = "packtype", type = "String", description =  "包装方式"),
            @ApiParam(name = "airportid", type = "String", description =  "机场ID"),
            @ApiParam(name = "materielnum", type = "String", description =  "件数"),
    },
            ok = {@ReturnKey(key = "unplanid", description = "发货包装对象id")}
    )
    @Filters({@By(type = CrossOriginFilter.class), @By(type = TokenFilter.class)})
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
                    Map map = new HashMap();
                    map.put("unplanid",unplanid);
                    return Result.success("system.success",map);
                }
            }
            return Result.error("system.error");
        }catch(Exception e){
            return Result.error("system.error");
        }
    }

    @At("/getListbyPlanID")
    @Ok("json")
    @Api(name = "进行核料/包装工作，并修改跟踪表"
            , params = {
            @ApiParam(name = "planid", type = "String", description = "计划ID"),
            @ApiParam(name = "pagenumber", type = "Integer", description = "当前页")
            , @ApiParam(name = "pagesize", type = "Integer", description = "默认返回10条",optional = true)
            },
            ok = {
            @ReturnKey(key = "contractcode", description = "合同编号"),
            @ReturnKey(key = "id", description = "合同ID"),
            @ReturnKey(key = "packname", description = "包装名称"),
            @ReturnKey(key = "packnum", description = "包装编号"),
            @ReturnKey(key = "partnum", description = "件号"),
            @ReturnKey(key = "serialnum", description = "序号"),
    }
    )
    @Filters({@By(type = CrossOriginFilter.class), @By(type = TokenFilter.class)})
    public Object getListbyPlanID(@Param("planid") String planid,@Param("pagenumber")Integer pagenumber,@Param("pagesize")Integer pagesize){
        try{
            if(!Strings.isBlank(planid)){
                return Result.success("system.success",logisticsSendgoodsService.getListbyPlanID(planid,pagenumber,pagesize));
            }
            return Result.error("system.error");
        }catch(Exception e){
            return Result.error("system.error");
        }
    }

    @At("/updateDelivergoodsbyDone")
    @Ok("json")
    @SLog(tag = "进货", msg = "进货")
    @Api(name = "交货"
            , params = {
            @ApiParam(name = "spid", type = "String", description = "计划ID"),
            @ApiParam(name = "cids", type = "String", description = "合同号，以逗号分隔"),
            @ApiParam(name = "personid", type = "String", description = "员工ID"),
    },
            ok = {@ReturnKey(key = "code", description = "成功code=0")}
    )
    @Filters({@By(type = CrossOriginFilter.class), @By(type = TokenFilter.class)})
    public Object updateDelivergoodsbyDone(@Param("spid") String id,@Param("cids") String contractids,@Param("personid") String personid){
        //contractids是检验不合格的合同id
        try{
            if(!Strings.isBlank(id)){
                logistics_sendplan sendplan=logisticsSendplanService.fetch(id);
                if(!Strings.isEmpty(contractids)){
                    //将对应的合同状态改为4
                    //将合同的planID去掉
                    //将planID找到orderid和合同ID对应的orderplan表数据删除
                    String [] cids=contractids.split(",");
//                    String [] opids;
                    for(int i=0;i<cids.length;i++){
                        logistics_sendgoods sg=logisticsSendgoodsService.fetch(cids[i]);
                        sg.setPstatus(4);
                        sg.setPlanID("");
                        if(sendplan!=null){
                            Cnd c=Cnd.NEW();
                            c.and("orderid","=",sendplan.getOrderid());
                            c.and("contractid","=",cids[i]);
                            logistics_orderplan op=logisticsOrderplanService.fetch(c);
                            if(op!=null)
                            {
                                logisticsOrderplanService.delete(op.getId());
                            }
                        }

                        ///将此合同对应的orderplanentry中step7，6，5的pstatus，operater,operatetime，deliveryorderid,orderid全部去掉
                        Cnd cnd=Cnd.NEW();
                        cnd.and("contractid","=",cids[i]);
                        cnd.and("step","in","5,6,7");
                        List<logistics_orderplanentry> logistics_orderplanentries= logisticsOrderplanentryService.query(cnd);
                        if(logistics_orderplanentries.size()>0){
                            for(int k=0;k<logistics_orderplanentries.size();k++){
                                logistics_orderplanentries.get(k).setPstatus(0);
                                logistics_orderplanentries.get(k).setOperatetime("");
                                logistics_orderplanentries.get(k).setOperater("");
                                logistics_orderplanentries.get(k).setDeliveryorderid("");
                                logistics_orderplanentries.get(k).setOrderid("");
                                logisticsOrderplanentryService.updateIgnoreNull(logistics_orderplanentries.get(k));
                            }
                        }

                    }

                }


                if(sendplan!=null){
                    sendplan.setPstatus(3);
                    sendplan.setSender(personid);
                    logisticsSendplanService.updateIgnoreNull(sendplan);

                    //订单状态改为6 送达
                    logistics_order order=logisticsOrderService.fetch(sendplan.getOrderid());
                    if(order!=null){
                        order.setPstatus(6);
                        logisticsOrderService.updateIgnoreNull(order);
                        //配送单状态改为3
                        logistics_Deliveryorder deliveryorder=logisticsDeliveryorderService.fetch(order.getDeliveryorderid());
                        if(deliveryorder!=null){
                            deliveryorder.setPstatus(2);
                            deliveryorder.setPersonid(personid);
                            logisticsDeliveryorderService.updateIgnoreNull(deliveryorder);
                        }
                        //合同状态改为8
                        Cnd cnd=Cnd.NEW();
                        cnd.and("orderid","=",order.getId());
                        List<logistics_orderplan> orderplans = logisticsOrderplanService.query(cnd);
                        if(orderplans.size()>0){
                            for(int i=0;i<orderplans.size();i++){
                                String contractid = orderplans.get(i).getContractid();
                                if(contractid!=null){
                                    logistics_sendgoods sendgoods=logisticsSendgoodsService.fetch(contractid);
                                    sendgoods.setPstatus(8);
                                    sendgoods.setSender(personid);
                                    logisticsSendgoodsService.updateIgnoreNull(sendgoods);

                                    //orderplanentry合同ID，step8的pstatus改为1
                                    Cnd c=Cnd.NEW();
                                    c.and("contractid","=",contractid);
                                    c.and("step","=","8");
                                    logistics_orderplanentry logistics_orderplanentry=logisticsOrderplanentryService.fetch(c);
                                    if(logistics_orderplanentry!=null){
                                        logistics_orderplanentry.setPstatus(1);
                                        logistics_orderplanentry.setOperater(personid);
                                        logistics_orderplanentry.setOperatetime(newDataTime.getDateYMDHMS());
//                                        if(!Strings.isEmpty(contractids)){
//                                            logistics_orderplanentry.setContent("合同号: "+contractids+" 检验未通过");
//                                        }
                                        logisticsOrderplanentryService.updateIgnoreNull(logistics_orderplanentry);
                                    }
                                }
                            }

                            return Result.success("保存成功");
                        }
                    }
                }
            }
            return Result.error("system.error");
        }catch(Exception e){
            return Result.error("system.error");
        }
    }


    @At("/sendGooodsImg")
    @Ok("json")
    //AdaptorErrorContext必须是最后一个参数
    @Api(name = "发货计划照片提交,原url='/platform/logistics/orderplanentry/img'"
            , params = {
            @ApiParam(name = "filename", type = "String", description = "文件名"),
            @ApiParam(name = "spid", type = "String", description = "计划ID"),
            @ApiParam(name = "base64", type = "String", description = "图片"),
            @ApiParam(name = "step", type = "String", description = "阶段，1创建需求-2发料完成-3核料-4包装-5计划-6审批-7发货中-8交货-9完成")
    },
            ok = {@ReturnKey(key = "filePath", description = "图片路径+文件名")}
    )
    @Filters({@By(type = CrossOriginFilter.class), @By(type = TokenFilter.class)})
    public Object sendGooodsImg(@Param("filename") String filename, @Param("base64") String base64,@Param("spid") String spid,@Param("step") String step,HttpServletRequest req, AdaptorErrorContext err) {
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
                if(base64.indexOf(",")>=0){//兼容H5
                    buffer = Base64.getDecoder().decode(base64.split(",")[1]);
                }else{
//                    buffer = Base64.getDecoder().decode(base64);
                    buffer = org.apache.commons.codec.binary.Base64.decodeBase64(base64);
                }
                FileOutputStream out = new FileOutputStream(p + f);
                out.write(buffer);
                out.close();
                /********************
                 * 将上传的文件路径修改到数据库中
                 * 1.先将数据库中的此行得到
                 * 2.得到picpath及picname、oldpicname
                 * 3.将新的合并到这些字段
                 * 4.修改
                 *******************/
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
                    HashMap map = new HashMap();
                    map.put("filePath", Globals.AppBase+f);
                    return Result.success("上传成功", map);
                }

            }
            return Result.error("system.error");
        } catch (Exception e) {
            return Result.error("system.error");
        } catch (Throwable e) {
            return Result.error("图片格式错误");
        }
    }


    @At("/packageAndCheckImg")
    @Ok("json")
    @Api(name = "上传照片（核料、包装), 原url='/platform/logistics/sendgoods/img'"
            , params = {
            @ApiParam(name = "filename", type = "String", description = "文件名"),
            @ApiParam(name = "hlid", type = "String", description = "订单合同ID"),
            @ApiParam(name = "base64", type = "String", description = "图片"),
            @ApiParam(name = "step", type = "String", description = "阶段"),
            @ApiParam(name = "bzid", type = "String", description = "包装表ID")
    },
            ok = {@ReturnKey(key = "filePath", description = "图片路径+文件名")}
    )
    @Filters({@By(type = CrossOriginFilter.class), @By(type = TokenFilter.class)})
    //AdaptorErrorContext必须是最后一个参数
    public Object packageAndCheckImg(@Param("filename") String filename, @Param("base64") String base64,@Param("hlid") String hlid,@Param("bzid") String bzid,@Param("step") String step,HttpServletRequest req, AdaptorErrorContext err) {
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
                if(base64.indexOf(",")>=0){//兼容H5
                    buffer = Base64.getDecoder().decode(base64.split(",")[1]);
                }else{
//                    buffer = Base64.getDecoder().decode(base64);
                    buffer = org.apache.commons.codec.binary.Base64.decodeBase64(base64);
                }
                FileOutputStream out = new FileOutputStream(p + f);
                out.write(buffer);
                out.close();
                /********************
                 * 将上传的文件路径修改到数据库中
                 * 1.先将数据库中的此行得到
                 * 2.得到picpath及picname、oldpicname
                 * 3.将新的合并到这些字段
                 * 4.修改
                 *******************/
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

                HashMap map = new HashMap();
                map.put("filePath", Globals.AppBase+f);
                return Result.success("上传成功", map);
            }
        } catch (Exception e) {
            return Result.error("系统错误");
        } catch (Throwable e) {
            return Result.error("图片格式错误");
        }
    }

}


