package cn.wizzer.app.sys.modules.services.impl;

import cn.wizzer.app.web.commons.base.Globals;
import cn.wizzer.framework.base.service.BaseServiceImpl;
import cn.wizzer.app.sys.modules.models.Sys_tempuser;
import cn.wizzer.app.sys.modules.services.SysTempuserService;
import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.nutz.aop.interceptor.ioc.TransAop;
import org.nutz.dao.Dao;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.lang.random.R;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Base64;

@IocBean(args = {"refer:dao"})
public class SysTempuserServiceImpl extends BaseServiceImpl<Sys_tempuser> implements SysTempuserService {
    public SysTempuserServiceImpl(Dao dao) {
        super(dao);
    }


    @Aop(TransAop.READ_COMMITTED)
    public Sys_tempuser addUserByMobile(String cutomerid, String username, String loginname, String password, String jobNumber, String tel,String wxNumber) {
        Sys_tempuser tempuser = new Sys_tempuser();
        tempuser.setCustomerid(cutomerid);
        tempuser.setUsername(username);
        tempuser.setLoginname(loginname);
        tempuser.setUnitid(Globals.unitid);
        //加密
        RandomNumberGenerator rng = new SecureRandomNumberGenerator();
        String salt = rng.nextBytes().toBase64();
        String hashedPasswordBase64 = new Sha256Hash(password, salt, 1024).toBase64();
        tempuser.setSalt(salt);
        tempuser.setPassword(hashedPasswordBase64);
        tempuser.setJobNumber(jobNumber);
        tempuser.setWxNumber(wxNumber);
        tempuser.setTel(tel);
         return  this.insert(tempuser);
    }

    public Sys_tempuser updateRoleCode(String tempuserId, Sys_tempuser tempuser) {
        Sys_tempuser sysTempuser = this.fetch(tempuserId);
        sysTempuser.setRoleCode(tempuser.getRoleCode());
        this.update(sysTempuser);
        return sysTempuser;
    }

}
