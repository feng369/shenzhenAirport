package cn.wizzer.app.logistics.modules.services;

import cn.wizzer.framework.base.Result;
import cn.wizzer.framework.base.service.BaseService;
import cn.wizzer.app.logistics.modules.models.logistics_Deliveryorderentry;
import org.nutz.dao.Cnd;
import org.nutz.mvc.impl.AdaptorErrorContext;

import javax.xml.bind.ValidationException;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

public interface LogisticsDeliveryorderentryService extends BaseService<logistics_Deliveryorderentry>{
    int upDventry(int pstatus, String content,String toreason, String optime,String personid, Cnd cnd) throws ValidationException;

    Map<String,Object> getDataByMobile(String orderid, String step);

    Map<String,Object> getStepData(String orderid, String step);

    Map img(String filename, String base64, String orderid, String step, AdaptorErrorContext err) throws FileNotFoundException, Exception;

    Result delUpload(String filename, String orderid, String step);

    List<Map<String,Object>> getDeliveryorderentryList(String orderid, Integer pagenumber, Integer pagesize) throws ParseException;

    List<logistics_Deliveryorderentry> getDeliveryOrderEntriesByStepNum(String stepNum, String orderid);
}
