package cn.wizzer.app.sys.modules.services;

import cn.wizzer.framework.base.service.BaseService;
import cn.wizzer.app.sys.modules.models.Sys_wx;
import com.alibaba.fastjson.JSONObject;
import org.nutz.dao.Cnd;

import java.util.Set;

public interface SysWxService extends BaseService<Sys_wx> {

    String getTokenOrNew(Cnd cnd);

    /**
     * 发送文本微信信息给指定用户
     */
    String sendMessageToUser(String[] userIds, String appnum, String content);

    void sendWxMessageAsy(Set<String> userIds, String content);

    /**
     * 创建微信用户
     */
    String createWXUser(String username, String mobile,String jobNumber);

    /**
     *根据userid(微信号)得到微信用户对象
     */
    JSONObject getWxUser(String userid);
    void authWXUser(String userid);

}
