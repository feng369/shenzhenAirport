package cn.wizzer.app.web.modules.controllers.platform.logistics;

import cn.wizzer.framework.base.Result;
import cn.wizzer.app.web.commons.slog.annotation.SLog;
import cn.wizzer.framework.page.datatable.DataTableColumn;
import cn.wizzer.framework.page.datatable.DataTableOrder;
import cn.wizzer.framework.util.StringUtil;
import cn.wizzer.app.logistics.modules.models.logistics_sendapproval;
import cn.wizzer.app.logistics.modules.services.LogisticsSendapprovalService;
import cn.wizzer.app.logistics.modules.services.LogisticsSendplanService;
import cn.wizzer.app.logistics.modules.models.logistics_sendplan;
import cn.wizzer.app.logistics.modules.models.logistics_sendgoods;
import cn.wizzer.app.logistics.modules.services.LogisticsSendgoodsService;
import cn.wizzer.app.logistics.modules.services.LogisticsOrderService;
import cn.wizzer.app.logistics.modules.models.logistics_order;
import cn.wizzer.app.logistics.modules.models.logistics_orderplanentry;
import cn.wizzer.app.logistics.modules.services.LogisticsOrderplanentryService;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import cn.wizzer.app.web.modules.controllers.open.api.Datatime.newDataTime;
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
@At("/platform/logistics/sendapproval")
public class LogisticsSendapprovalController{
    private static final Log log = Logs.get();
    @Inject
    private LogisticsSendapprovalService logisticsSendapprovalService;
    @Inject
    private LogisticsSendplanService logisticsSendplanService;
    @Inject
    private LogisticsSendgoodsService logisticsSendgoodsService;
    @Inject
    private LogisticsOrderService logisticsOrderService;
    @Inject
    private LogisticsOrderplanentryService logisticsOrderplanentryService;

    @At("")
    @Ok("beetl:/platform/logistics/sendapproval/index.html")
    @RequiresPermissions("platform.logistics.sendapproval")
    public void index() {
    }

    @At("/data")
    @Ok("json")
    @RequiresPermissions("platform.logistics.sendapproval")
    public Object data(@Param("pstatus") String pstatus,@Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns) {
		Cnd cnd = Cnd.NEW();
        cnd.and("pstatus","=",Integer.parseInt(pstatus));
    	return logisticsSendapprovalService.data(length, start, draw, order, columns, cnd, "plan|person|apply");
    }

    @At("/add")
    @Ok("beetl:/platform/logistics/sendapproval/add.html")
    @RequiresPermissions("platform.logistics.sendapproval")
    public void add() {

    }

    @At("/addDo")
    @Ok("json")
    @RequiresPermissions("platform.logistics.sendapproval.add")
    @SLog(tag = "logistics_sendapproval", msg = "${args[0].id}")
    public Object addDo(@Param("..")logistics_sendapproval logisticsSendapproval, HttpServletRequest req) {
		try {
			logisticsSendapprovalService.insert(logisticsSendapproval);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }

    @At("/edit/?")
    @Ok("beetl:/platform/logistics/sendapproval/edit.html")
    @RequiresPermissions("platform.logistics.sendapproval")
    public void edit(String id,HttpServletRequest req) {
		req.setAttribute("obj", logisticsSendapprovalService.fetch(id));
    }

    @At("/editDo")
    @Ok("json")
    @RequiresPermissions("platform.logistics.sendapproval.edit")
    @SLog(tag = "logistics_sendapproval", msg = "${args[0].id}")
    public Object editDo(@Param("..")logistics_sendapproval logisticsSendapproval, HttpServletRequest req) {
		try {
            logisticsSendapproval.setOpBy(StringUtil.getUid());
			logisticsSendapproval.setOpAt((int) (System.currentTimeMillis() / 1000));
			logisticsSendapprovalService.updateIgnoreNull(logisticsSendapproval);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }

    @At({"/delete/?", "/delete"})
    @Ok("json")
    @RequiresPermissions("platform.logistics.sendapproval.delete")
    @SLog(tag = "logistics_sendapproval", msg = "${req.getAttribute('id')}")
    public Object delete(String id, @Param("ids")  String[] ids, HttpServletRequest req) {
		try {
			if(ids!=null&&ids.length>0){
				logisticsSendapprovalService.delete(ids);
    			req.setAttribute("id", org.apache.shiro.util.StringUtils.toString(ids));
			}else{
				logisticsSendapprovalService.delete(id);
    			req.setAttribute("id", id);
			}
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }

    @At("/detail/?")
    @Ok("beetl:/platform/logistics/sendapproval/detail.html")
    @RequiresPermissions("platform.logistics.sendapproval")
	public void detail(String id, HttpServletRequest req) {
		if (!Strings.isBlank(id)) {
            req.setAttribute("obj", logisticsSendapprovalService.fetch(id));
		}else{
            req.setAttribute("obj", null);
        }
    }

    @At("/setApproval")
    @Ok("json")
    public Object setApproval(@Param("ids") String [] ids,@Param("aresult") String aresult,@Param("suggest") String suggest,HttpServletRequest req){
        try{
            int num=0;
            if(ids.length>0){
                for(int i=0;i<ids.length;i++){
                    logistics_sendapproval sendapproval=logisticsSendapprovalService.fetch(ids[i]);
                    if(sendapproval!=null)
                    {
                        //审批状态改变
                        sendapproval.setApprovaler(req.getSession().getAttribute("personid").toString());
                        sendapproval.setAresult(aresult);
                        sendapproval.setSuggest(suggest);
                        String currentDate=newDataTime.getDateYMDHMS();
                        sendapproval.setApprovaldate(currentDate);
                        sendapproval.setPstatus(1);
                        logisticsSendapprovalService.updateIgnoreNull(sendapproval);
                        num++;

                        //如果审批通过， 则将sendplan 状态改为2 否则改为0
                        //如果审批通过，则将发货表改为7 否则改为5
                        logistics_sendplan sendplan=logisticsSendplanService.fetch(sendapproval.getPlanID());
                        if(aresult.equals("1")){//通过
                            if(sendplan!=null){
                                sendplan.setPstatus(2);
                                logisticsSendplanService.updateIgnoreNull(sendplan);
                                //如果为通过，则将对应订单的状态从98改为6，否则不变。
                                logistics_order order = logisticsOrderService.fetch(sendplan.getOrderid());
                                order.setPstatus(6);
                                logisticsOrderService.updateIgnoreNull(order);
                                Cnd cnd=Cnd.NEW();
                                cnd.and("planID","=",sendplan.getId());
                                List<logistics_sendgoods> sendgoodsList=logisticsSendgoodsService.query(cnd);
                                if(sendgoodsList.size()>0){
                                    for(int p=0;p<sendgoodsList.size();p++){
                                        sendgoodsList.get(p).setPstatus(7);
                                        logisticsSendgoodsService.updateIgnoreNull(sendgoodsList.get(p));
                                    }
                                }
                                //审批通过，将orderplanentry对应contract的step7的pstatus改为1
                                Cnd c=Cnd.NEW();
                                c.and("orderid","=",order.getId());
                                c.and("step","=","7");
                                List<logistics_orderplanentry> logistics_orderplanentries = logisticsOrderplanentryService.query(c);
                                if(logistics_orderplanentries.size()>0){
                                    for(int k=0;k<logistics_orderplanentries.size();k++){
                                        logistics_orderplanentries.get(k).setPstatus(1);
                                        logistics_orderplanentries.get(k).setOperater(req.getSession().getAttribute("personid").toString());
                                        logistics_orderplanentries.get(k).setOperatetime(newDataTime.getDateYMDHMS());
                                        logisticsOrderplanentryService.updateIgnoreNull(logistics_orderplanentries.get(k));
                                    }
                                }

                            }
                        }
                        else{
                            if(sendplan!=null){
                                sendplan.setPstatus(0);
                                logisticsSendplanService.updateIgnoreNull(sendplan);
                                Cnd cnd=Cnd.NEW();
                                cnd.and("planID","=",sendplan.getId());
                                List<logistics_sendgoods> sendgoodsList=logisticsSendgoodsService.query(cnd);
                                if(sendgoodsList.size()>0){
                                    for(int p=0;p<sendgoodsList.size();p++){
                                        sendgoodsList.get(p).setPstatus(5);
                                        logisticsSendgoodsService.updateIgnoreNull(sendgoodsList.get(p));
                                    }
                                }

                                //审批不通过，将orderplanentry对应contract的step6的pstatus改为0
                                Cnd c=Cnd.NEW();
                                c.and("orderid","=",sendplan.getOrderid());
                                c.and("step","=","6");
                                List<logistics_orderplanentry> logistics_orderplanentries = logisticsOrderplanentryService.query(c);
                                if(logistics_orderplanentries.size()>0){
                                    for(int k=0;k<logistics_orderplanentries.size();k++){
                                        logistics_orderplanentries.get(k).setPstatus(0);
                                        logistics_orderplanentries.get(k).setOperater("");
                                        logistics_orderplanentries.get(k).setOperatetime("");
                                        logisticsOrderplanentryService.updateIgnoreNull(logistics_orderplanentries.get(k));
                                    }
                                }
                            }
                        }
                    }
                }
                if(num==ids.length)
                {return num;}
            }
            return null;
        }catch(Exception e){
            return null;
        }
    }

}
