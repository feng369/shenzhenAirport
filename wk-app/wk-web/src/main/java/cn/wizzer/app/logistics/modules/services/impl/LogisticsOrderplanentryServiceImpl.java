package cn.wizzer.app.logistics.modules.services.impl;

import cn.wizzer.app.logistics.modules.models.*;
import cn.wizzer.app.logistics.modules.services.*;
import cn.wizzer.app.web.modules.controllers.open.api.Datatime.newDataTime;
import cn.wizzer.framework.base.Result;
import cn.wizzer.framework.base.service.BaseServiceImpl;
import org.nutz.aop.interceptor.ioc.TransAop;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;

import java.util.List;

@IocBean(args = {"refer:dao"})
public class LogisticsOrderplanentryServiceImpl extends BaseServiceImpl<logistics_orderplanentry> implements LogisticsOrderplanentryService {
    public LogisticsOrderplanentryServiceImpl(Dao dao) {
        super(dao);
    }
    @Inject
    private LogisticsSendplanService logisticsSendplanService;
    @Inject
    private LogisticsOrderService logisticsOrderService;
    @Inject
    private LogisticsDeliveryorderService logisticsDeliveryorderService;
    @Inject
    private LogisticsOrderplanService logisticsOrderplanService;
    @Inject
    private LogisticsSendgoodsService logisticsSendgoodsService;
    @Inject
    private LogisticsOrderplanentryService logisticsOrderplanentryService;

    @Aop(TransAop.READ_COMMITTED)
    public Result updateDelivergoods(String personid, String id, String deliverynum, String packnum, String weight, String flightnum, String thirdname, String drivername, String carnum) {
        if(!Strings.isBlank(id)){
            logistics_sendplan sendplan=logisticsSendplanService.fetch(id);
            if(sendplan!=null){
                logisticsSendplanService.updateSendPlan(sendplan,deliverynum,packnum,weight,flightnum,thirdname,drivername,carnum,4);
                //订单状态改为7完成
                logistics_order order=logisticsOrderService.fetch(sendplan.getOrderid());
                if(order!=null){
                    order.setPstatus(7);
                    logisticsOrderService.updateIgnoreNull(order);
                    //配送单状态改为3
                    logistics_Deliveryorder deliveryorder=logisticsDeliveryorderService.fetch(order.getDeliveryorderid());
                    if(deliveryorder!=null){
                        deliveryorder.setPstatus(3);
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
                                sendgoods.setPstatus(9);
                                sendgoods.setBillnum(deliverynum);
                                logisticsSendgoodsService.updateIgnoreNull(sendgoods);

                                //orderplanentry合同ID，step9的pstatus改为1
                                Cnd c=Cnd.NEW();
                                c.and("contractid","=",contractid);
                                c.and("step","=","9");
                                logistics_orderplanentry logistics_orderplanentry=logisticsOrderplanentryService.fetch(c);
                                if(logistics_orderplanentry!=null){
                                    logisticsOrderplanentryService.updateIgnoreNull(logistics_orderplanentry);
                                }
                            }
                        }
                        return Result.success("保存成功");
                    }
                }
            }
        }
        return Result.error("syste.error");
    }
}
