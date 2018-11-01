package cn.wizzer.app.logistics.modules.services.impl;

import cn.wizzer.framework.base.service.BaseServiceImpl;
import cn.wizzer.app.logistics.modules.models.logistics_sendapproval;
import cn.wizzer.app.logistics.modules.services.LogisticsSendapprovalService;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean(args = {"refer:dao"})
public class LogisticsSendapprovalServiceImpl extends BaseServiceImpl<logistics_sendapproval> implements LogisticsSendapprovalService {
    public LogisticsSendapprovalServiceImpl(Dao dao) {
        super(dao);
    }
}
