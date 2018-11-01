package cn.wizzer.app.logistics.modules.services.impl;

import cn.wizzer.app.logistics.modules.services.LogisticsOrderstepService;
import cn.wizzer.app.web.commons.base.Globals;
import cn.wizzer.app.web.modules.controllers.open.api.Datatime.newDataTime;
import cn.wizzer.framework.base.Result;
import cn.wizzer.framework.base.service.BaseServiceImpl;
import cn.wizzer.app.logistics.modules.models.logistics_Deliveryorderentry;
import cn.wizzer.app.logistics.modules.services.LogisticsDeliveryorderentryService;
import cn.wizzer.framework.util.DateUtil;
import cn.wizzer.framework.util.StringUtil;
import org.nutz.aop.interceptor.ioc.TransAop;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.lang.random.R;
import org.nutz.mvc.impl.AdaptorErrorContext;

import javax.xml.bind.ValidationException;
import java.io.File;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@IocBean(args = {"refer:dao"})
public class LogisticsDeliveryorderentryServiceImpl extends BaseServiceImpl<logistics_Deliveryorderentry> implements LogisticsDeliveryorderentryService {
    public LogisticsDeliveryorderentryServiceImpl(Dao dao) {
        super(dao);
    }
    @Inject
    private LogisticsOrderstepService logisticsOrderstepService;
    public int upDventry(int pstatus, String content, String toreason, String optime, String personid, Cnd cnd) throws ValidationException {
        int re = 0;
        List<logistics_Deliveryorderentry> ld = dao().query(logistics_Deliveryorderentry.class, cnd);
        if (ld.size() > 0) {
            for (int i = 0; i < ld.size(); i++) {
                if (ld.get(i).getPstatus() < 1) {
                    ld.get(i).setOperatetime(optime);
                }
                if (pstatus != ld.get(i).getPstatus()) {
                    ld.get(i).setPstatus(pstatus);
                    ld.get(i).setSetpPersonId(personid);
                }
                if (content != null)
                    ld.get(i).setContent(content);
                if (!Strings.isBlank(toreason)){
                    //20180709zhf1507
                    //有超时
                    if(toreason.trim().length() == 0){
                        throw new ValidationException("送达超时原因不能都是空格");
                    }
                    ld.get(i).setOvertime(1);
                    ld.get(i).setTimeoutreason(toreason);
                }
                    re += dao().updateIgnoreNull(ld);
            }
        }
        return re;

    }

    public Map<String, Object> getDataByMobile(String orderid, String step) {
        Cnd cnd = Cnd.NEW();
        if (!Strings.isBlank(orderid) && !Strings.isBlank(step)) {
            cnd.and("orderid", "=", orderid);
            cnd.and("step", "=", step);
        }
        logistics_Deliveryorderentry logisticsDeliveryorderentry = this.fetch(cnd);
        Map<String, Object> map = new HashMap<>();
        if (logisticsDeliveryorderentry != null) {
            map.put("picname", logisticsDeliveryorderentry.getPicname());
            map.put("picpath", logisticsDeliveryorderentry.getPicpath());
        }
        return map;
    }

    public Map<String, Object> getStepData(String orderid, String step) {
        Cnd cnd = Cnd.NEW();
        Map<String, Object> map = new HashMap<>();
        cnd.and("orderid", "=", orderid);
        cnd.and("step", "=", step);
        cnd.and("pstatus", "=", "1");
        logistics_Deliveryorderentry logisticsDeliveryorderentry = this.fetch(cnd);
        if (logisticsDeliveryorderentry != null) {
            map.put("content", logisticsDeliveryorderentry.getContent());
        } else {
            map.put("content", "");
        }
        return map;
    }

    @Aop(TransAop.READ_COMMITTED)
    public Map img(String filename, String base64, String orderid, String step, AdaptorErrorContext err) throws Exception {
        byte[] buffer;
        String p = Globals.AppRoot;
        String fn = R.UU32() + filename.substring(filename.lastIndexOf("."));
        String path = Globals.AppUploadPath + "/image/" + DateUtil.format(new Date(), "yyyyMMdd") + "/";
        String f = Globals.AppUploadPath + "/image/" + DateUtil.format(new Date(), "yyyyMMdd") + "/" + fn;
        File file = new File(p + Globals.AppUploadPath + "/image/" + DateUtil.format(new Date(), "yyyyMMdd"));
        if (!file.exists()) {
            file.mkdirs();
        }
        if (base64.indexOf(",") >= 0) {//兼容H5
            buffer = Base64.getDecoder().decode(base64.split(",")[1]);
        } else {
            //buffer = Base64.getDecoder().decode(base64);
            //buffer = com.alibaba.druid.util.Base64.base64ToByteArray(base64);
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
        Cnd cnd = Cnd.NEW();
        cnd.and("orderid", "=", orderid);
        cnd.and("step", "=", step);
        logistics_Deliveryorderentry deliveryorderentry = this.fetch(cnd);
        if (deliveryorderentry != null) {
//                    String entryID=deliveryorderentry.getId();
            String picpath = deliveryorderentry.getPicpath();
            String picname = deliveryorderentry.getPicname();
            String oldpicname = deliveryorderentry.getOldpicname();
            if (!Strings.isBlank(picpath)) {
                picpath += "," + path + fn;
            } else {
                picpath = path + fn;
            }
            if (!Strings.isBlank(picname)) {
                picname += "," + fn;
            } else {
                picname = fn;
            }
            if (!Strings.isBlank(oldpicname)) {
                oldpicname += "," + filename;
            } else {
                oldpicname = filename;
            }
            deliveryorderentry.setPicpath(picpath);
            deliveryorderentry.setPicname(picname);
            deliveryorderentry.setOldpicname(oldpicname);
            this.updateIgnoreNull(deliveryorderentry);
        }
        HashMap map = new HashMap();
        map.put("filePath", Globals.AppBase + f);
        return map;
    }

    @Aop(TransAop.READ_COMMITTED)
    public Result delUpload(String filename, String orderid, String step) {
        if (!Strings.isBlank(filename)) {
            Cnd cnd = Cnd.NEW();
            cnd.and("orderid", "=", orderid);
            cnd.and("step", "=", step);
            logistics_Deliveryorderentry deliveryorderentry = this.fetch(cnd);
            if (deliveryorderentry != null) {
                String oldpicname = deliveryorderentry.getOldpicname();
                String picname = deliveryorderentry.getPicname();
                String picpath = deliveryorderentry.getPicpath();
                if (oldpicname.indexOf(",") > -1) {
                    String[] op = oldpicname.split(",");
                    List<String> oplist = java.util.Arrays.asList(op);
                    ArrayList opArray = new ArrayList<>(oplist);
                    String[] pn = picname.split(",");
                    List<String> pnlist = java.util.Arrays.asList(pn);
                    ArrayList pnArray = new ArrayList<>(pnlist);
                    String[] pp = picpath.split(",");
                    List<String> pplist = java.util.Arrays.asList(pp);
                    ArrayList ppArray = new ArrayList<>(pplist);
                    int number = -1;
                    for (int i = 0; i < opArray.size(); i++) {
                        if (ppArray.get(i).toString().equals(filename)) {
                            number = i;
                            File file = new File(Globals.AppRoot + ppArray.get(i));
                            if (file.exists()) {
                                file.delete();
                                break;
                            }
                        }
                    }

                    /**************
                     * 修改字段
                     * picpath,picname,oldpicname
                     */
                    if (number > -1) {
                        String newoldpn = "";
                        String newpn = "";
                        String newpp = "";
                        opArray.remove(number);
                        pnArray.remove(number);
                        ppArray.remove(number);
                        newoldpn = org.apache.commons.lang.StringUtils.join(opArray.toArray(), ",");
                        newpn = org.apache.commons.lang.StringUtils.join(pnArray.toArray(), ",");
                        newpp = org.apache.commons.lang.StringUtils.join(ppArray.toArray(), ",");

                        deliveryorderentry.setOldpicname(newoldpn);
                        deliveryorderentry.setPicname(newpn);
                        deliveryorderentry.setPicpath(newpp);
                        this.updateIgnoreNull(deliveryorderentry);
                        return Result.success("已删除");
                    }
                } else {
                    if ((picpath).equals(filename)) {
                        File file = new File(Globals.AppRoot + picpath);
                        if (file.exists()) {
                            file.delete();
                            /**************
                             * 修改字段
                             * picpath,picname,oldpicname
                             */
                            deliveryorderentry.setOldpicname("");
                            deliveryorderentry.setPicname("");
                            deliveryorderentry.setPicpath("");
                            this.updateIgnoreNull(deliveryorderentry);
                            return Result.success("已删除");
                        }
                    }
                }
            }
        }
        return Result.error("system.error");
    }


    public List<Map<String, Object>> getDeliveryorderentryList(String orderid, Integer pagenumber, Integer pagesize) throws ParseException {
        List<Map<String, Object>> list = new ArrayList<>();
        List<logistics_Deliveryorderentry> deliveryorderentries = this.query(Cnd.where("orderid", "=", orderid).orderBy("step", "ASC"));
        if (deliveryorderentries.size() > 0) {
            for (logistics_Deliveryorderentry deliveryorderentry : deliveryorderentries) {
                if(deliveryorderentry.getStep() == 0){
                    continue;
                }
                Map<String, Object> map = new HashMap<>();
                map.put("pstatus", deliveryorderentry.getPstatus());

                String opTime = deliveryorderentry.getOperatetime();
                SimpleDateFormat df = new SimpleDateFormat("MM月dd日 HH:mm");
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                if (!Strings.isBlank(opTime)) {
                    Date parse = sdf.parse(opTime);
                    opTime = df.format(parse);
                }
                map.put("operatetime", Strings.isBlank(opTime) ? "" : opTime);
                map.put("step", deliveryorderentry.getStep());
                map.put("stepname", deliveryorderentry.getStepname());
                list.add(map);
            }
        }
        return list;
    }

    public List<logistics_Deliveryorderentry> getDeliveryOrderEntriesByStepNum(String stepNum, String orderid) {
        String step = logisticsOrderstepService.getStepByStepnum(stepNum, "72dd55d2c3cc42799c4e014745db2cdb");
        List<logistics_Deliveryorderentry> deliveryorderentries = this.query(Cnd.where("orderid", "=", orderid).and("step","=",step));
        //得到该订单相应状态下的所有步骤对象
        return deliveryorderentries;
    }

  /*  public static void main(String[] args) throws ParseException {


        SimpleDateFormat df = new SimpleDateFormat("MM月dd日 HH:mm");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date parse = sdf.parse("2018-09-03 04:05:05");
        System.out.println(df.format(parse));


        //        df.format()
        String str = new newDataTime().getCurrentDateStr("MM月dd日 HH:mm");
        System.out.println(str);
    }*/
}
