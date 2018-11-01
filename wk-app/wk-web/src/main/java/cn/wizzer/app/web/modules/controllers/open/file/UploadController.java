package cn.wizzer.app.web.modules.controllers.open.file;

import cn.wizzer.app.web.commons.base.Globals;
import cn.wizzer.framework.base.Result;
import cn.wizzer.framework.util.DateUtil;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Files;
import org.nutz.lang.Strings;
import org.nutz.lang.random.R;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.*;
import org.nutz.mvc.impl.AdaptorErrorContext;
import org.nutz.mvc.upload.TempFile;
import org.nutz.mvc.upload.UploadAdaptor;
import cn.wizzer.app.logistics.modules.services.LogisticsDeliveryorderentryService;
import cn.wizzer.app.logistics.modules.models.logistics_Deliveryorderentry;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

/**
 * Created by Wizzer on 2016/7/5.
 */
@IocBean
@At("/open/file/upload")
public class UploadController {
    private static final Log log = Logs.get();

    @Inject
    private LogisticsDeliveryorderentryService deliveryorderentryService;

    @AdaptBy(type = UploadAdaptor.class, args = {"ioc:imageUpload"})
    @POST
    @At
    @Ok("json")
    @RequiresAuthentication
    //AdaptorErrorContext必须是最后一个参数
    public Object image(@Param("Filedata") TempFile tf, HttpServletRequest req, AdaptorErrorContext err) {
        try {
            if (err != null && err.getAdaptorErr() != null) {
                return NutMap.NEW().addv("code", 1).addv("msg", "文件不合法");
            } else if (tf == null) {
                return Result.error("空文件");
            } else {
                String p = Globals.AppRoot;
                String f = Globals.AppUploadPath + "/image/" + DateUtil.format(new Date(), "yyyyMMdd") + "/" + R.UU32() + tf.getSubmittedFileName().substring(tf.getSubmittedFileName().indexOf("."));
                Files.write(new File(p + f), tf.getInputStream());
                return Result.success("上传成功", Globals.AppBase+f);
            }
        } catch (Exception e) {
            return Result.error("系统错误");
        } catch (Throwable e) {
            return Result.error("图片格式错误");
        }
    }

    @AdaptBy(type = UploadAdaptor.class, args = {"ioc:imageUpload"})
    @POST
    @At("/img")
    @Ok("jsonp:full")
    //AdaptorErrorContext必须是最后一个参数
    public Object img(@Param("filename") String filename, @Param("base64") String base64,@Param("orderid") String orderid,@Param("stepname") String stepname,HttpServletRequest req, AdaptorErrorContext err) {
        byte[] buffer;
        try {
            if (err != null && err.getAdaptorErr() != null) {
                return NutMap.NEW().addv("code", 1).addv("msg", "文件不合法");
            } else if (base64 == null) {
                return Result.error("空文件");
            } else {
                String p = Globals.AppRoot;
                String fn=R.UU32()+filename.substring(filename.lastIndexOf("."));
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
                /********************
                 * 将上传的文件路径修改到数据库中
                 * 1.先将数据库中的此行得到
                 * 2.得到picpath及picname、oldpicname
                 * 3.将新的合并到这些字段
                 * 4.修改
                 *******************/
                Cnd cnd=Cnd.NEW();
                cnd.and("orderid","=",orderid);
                cnd.and("stepname","=",stepname);
                logistics_Deliveryorderentry deliveryorderentry=deliveryorderentryService.fetch(cnd);
                if(!deliveryorderentry.equals(null)){
//                    String entryID=deliveryorderentry.getId();
                    String picpath=deliveryorderentry.getPicpath();
                    String picname=deliveryorderentry.getPicname();
                    String oldpicname=deliveryorderentry.getOldpicname();
                    if(!Strings.isBlank(picpath)){
                        picpath+=","+path;
                    }else{
                        picpath=path;
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
                    deliveryorderentry.setPicpath(picpath);
                    deliveryorderentry.setPicname(picname);
                    deliveryorderentry.setOldpicname(oldpicname);
                    deliveryorderentryService.updateIgnoreNull(deliveryorderentry);

                }

                return Result.success("上传成功", Globals.AppBase+f);
            }
        } catch (Exception e) {
            return Result.error("系统错误");
        } catch (Throwable e) {
            return Result.error("图片格式错误");
        }
    }

    @At("/delUpload")
    @Ok("jsonp:full")
    public Object delUpload(@Param("filename") String filename,@Param("orderid") String orderid,@Param("stepname") String stepname){
        if(!Strings.isBlank(filename)){
            Cnd cnd= Cnd.NEW();
            cnd.and("orderid","=",orderid);
            cnd.and("stepname","=",stepname);
            logistics_Deliveryorderentry deliveryorderentry= deliveryorderentryService.fetch(cnd);
            if(!deliveryorderentry.equals(null)){
                String oldpicname=deliveryorderentry.getOldpicname();
                String picname=deliveryorderentry.getPicname();
                String picpath=deliveryorderentry.getPicpath();
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
                        if((ppArray.get(i).toString()+pnArray.get(i).toString()).equals(filename)){
                            number=i;
                            File file=new File(Globals.AppRoot+ppArray.get(i)+pnArray.get(i));
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

                        deliveryorderentry.setOldpicname(newoldpn);
                        deliveryorderentry.setPicname(newpn);
                        deliveryorderentry.setPicpath(newpp);
                        deliveryorderentryService.updateIgnoreNull(deliveryorderentry);
                        return Result.success("已删除");
                    }
                }
                else{
                    if((picpath+picname).equals(filename)){
                        File file=new File(Globals.AppRoot+picpath+picname);
                        if(file.exists()){
                            file.delete();
                            /**************
                             * 修改字段
                             * picpath,picname,oldpicname
                             */
                            deliveryorderentry.setOldpicname("");
                            deliveryorderentry.setPicname("");
                            deliveryorderentry.setPicpath("");
                            deliveryorderentryService.updateIgnoreNull(deliveryorderentry);
                            return Result.success("已删除");
                        }
                    }
                }



            }
        }
        return Result.error("没有此文件");
    }

}
