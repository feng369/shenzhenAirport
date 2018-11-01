package cn.wizzer.app.sys.modules.services;

import cn.wizzer.framework.base.service.BaseService;
import cn.wizzer.app.sys.modules.models.Sys_wx_user;
import com.alibaba.fastjson.JSONObject;

public interface SysWxUserService extends BaseService<Sys_wx_user>{
    /**
     * 下载微信用户信息
     */
    void download();
    /**
     * 从通讯录得到的微信用户插入到sys_wx_user表中
     */
    void insertWxUser(JSONObject jsonObject);
}
