package cn.wizzer.app.sys.modules.services.impl;

import cn.wizzer.framework.base.service.BaseServiceImpl;
import cn.wizzer.app.sys.modules.models.Sys_version;
import cn.wizzer.app.sys.modules.services.SysVersionService;
import org.nutz.aop.interceptor.ioc.TransAop;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;

import javax.xml.bind.ValidationException;
import java.util.List;

@IocBean(args = {"refer:dao"})
public class SysVersionServiceImpl extends BaseServiceImpl<Sys_version> implements SysVersionService {
    public SysVersionServiceImpl(Dao dao) {
        super(dao);
    }

    @Aop(TransAop.READ_COMMITTED)
    public void editVersion(String name, String newVersion, boolean wgt) throws ValidationException {
        List<Sys_version> sysVersions = this.query(Cnd.where("name", "=", name));
        if(Strings.isBlank(name)){
            throw new ValidationException("传入版本名称不能为空");
        }
        if(Strings.isBlank(newVersion)){
            throw new ValidationException("传入新版本号不能为空");
        }
        if(sysVersions.size() != 1){
            throw new ValidationException("无法确定版本对象");
        }
        Sys_version sysVersion = sysVersions.get(0);
            //沒传wgt(false)
            //this.update(Chain.make("version",newVersion),Cnd.where("id","=",sysVersion.getId()));
            //传了wgt
            this.update(Chain.make("version", newVersion).add("wgt", wgt), Cnd.where("id", "=", sysVersion.getId()));

    }
}
