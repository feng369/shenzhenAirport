package cn.wizzer.app.sys.modules.services;

import cn.wizzer.framework.base.service.BaseService;
import cn.wizzer.app.sys.modules.models.Sys_mobile;

public interface SysMobileService extends BaseService<Sys_mobile>{

    Object getDeviceInfo(String deviceid);

    void setDeviceLogin(boolean insert, String deviceid, String deviceOS, String deviceModel, String userid, String expire, String pstatus, String account, String password);

    void setVehicle(String deviceid, String vehicleid);
}
