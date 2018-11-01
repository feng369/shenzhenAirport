package cn.wizzer.app.sys.modules.services.impl;

import cn.wizzer.app.base.modules.models.base_vehicle;
import cn.wizzer.app.base.modules.services.BaseVehicleService;
import cn.wizzer.app.web.modules.controllers.open.api.Datatime.newDataTime;
import cn.wizzer.framework.base.Result;
import cn.wizzer.framework.base.service.BaseServiceImpl;
import cn.wizzer.app.sys.modules.models.Sys_mobile;
import cn.wizzer.app.sys.modules.services.SysMobileService;
import org.nutz.aop.interceptor.ioc.TransAop;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@IocBean(args = {"refer:dao"})
public class SysMobileServiceImpl extends BaseServiceImpl<Sys_mobile> implements SysMobileService {
    public SysMobileServiceImpl(Dao dao) {
        super(dao);
    }
    @Inject
    private BaseVehicleService baseVehicleService;
    public Object getDeviceInfo(String deviceid) {
        Cnd cnd= Cnd.NEW();
        Map map = new HashMap<>();
        cnd.and("deviceid","=",deviceid).desc("createTime");
        Sys_mobile mobile=this.fetch(cnd);
        if(mobile!=null){
            if(!Strings.isBlank(mobile.getVehicleid())){
                base_vehicle baseVehicle = baseVehicleService.fetch(mobile.getVehicleid());
                if(baseVehicle != null){
                    map.put("vehicleid",baseVehicle.getId());
                    map.put("vehiclenum",baseVehicle.getVehiclenum());
                }
            }
            map.put("account",mobile.getAccount());
            map.put("password",mobile.getPassword());
            return Result.success("system.success",map);
        }
        return Result.error(2,"设备信息为空!");
    }
    @Aop({TransAop.READ_COMMITTED})
    public void setDeviceLogin(boolean insert, String deviceid, String deviceOS, String deviceModel, String userid, String expire, String pstatus, String account, String password) {
        Cnd cnd=Cnd.NEW();
        cnd.and("deviceid","=",deviceid);
        Sys_mobile mobile=this.fetch(cnd);
        if(mobile==null){
            mobile=new Sys_mobile();
            insert=true;
        }
        if(!Strings.isBlank(deviceid))
            mobile.setDeviceid(deviceid);
        if(!Strings.isBlank(deviceModel))
            mobile.setDeviceModel(deviceModel);
        if(!Strings.isBlank(deviceOS))
            mobile.setDeviceOS(deviceOS);
        if(!Strings.isBlank(pstatus))
            mobile.setPstatus(pstatus);
        if(!Strings.isBlank(userid))
            mobile.setUserid(userid);
        if(!Strings.isBlank(account))
            mobile.setAccount(account);
        if(!Strings.isBlank(password))
            mobile.setPassword(password);
        if(!Strings.isBlank(expire)){
            mobile.setStartDate(newDataTime.getDateYMDHMS());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.DATE, Integer.parseInt(expire));
            String endDate = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(calendar.getTime());
            mobile.setEndDate(endDate);
        }
        if(insert==false){
            this.update(mobile);
        }else{
            this.insert(mobile);
        }
    }

    public void setVehicle(String deviceid, String vehicleid) {
        Sys_mobile mobile = this.fetch(Cnd.where("deviceid","=",deviceid));
        if(mobile!=null){
            mobile.setVehicleid(vehicleid);
            this.updateIgnoreNull(mobile);
        }
    }
}
