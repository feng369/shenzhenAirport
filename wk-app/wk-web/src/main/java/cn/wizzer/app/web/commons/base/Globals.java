package cn.wizzer.app.web.commons.base;

import cn.wizzer.app.base.modules.models.base_customer;
import cn.wizzer.app.base.modules.services.BaseCustomerService;
import cn.wizzer.app.sys.modules.models.Sys_config;
import cn.wizzer.app.sys.modules.models.Sys_route;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.lang.Strings;
import org.nutz.lang.Times;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wizzer on 2016/12/19.
 */
public class Globals {
    //项目路径
    public static String AppRoot = "";
    //项目目录
    public static String AppBase = "";
    //项目名称
    public static String AppName = "航空一体化保障系统";
    //项目短名称
    public static String AppShrotName = "NutzWk";
    //项目域名
    public static String AppDomain = "127.0.0.1";
    //文件上传路径
    public static String AppUploadPath = "/upload";

    // 是否启用了队列
    public static boolean RabbitMQEnabled = false;
    //系统自定义参数
    public static Map<String, String> MyConfig = new HashMap<>();
    //自定义路由
    public static Map<String, Sys_route> RouteMap = new HashMap<>();

    //订单获取数刷新时间（秒）
    public static String OrderCountRef = "20";

    //地图刷新时间（分）
    public static String MapRef = "1";

    //API请求token有效期(秒)
    public static String APITokenExpire="7200";

    //20180601zhf1817
    //H5AppZoom地图缩放比例
    public static String H5AppZoom = "10";
    //20180601zhf1817
    //App地图缩放比例
    public static String AppZoom = "16.2";

    //需求端公司ID  对应深航
    public static String unitid = "0d4ab811fa3c4c0999fc28f68260e48c";

    //需求端客户ID 对应深航
    public static String customerid = "e34dddbfdc894db087734a475e9475c1";
    public static String airportId = "936546e4f4474ac3bdc9a2495a0adcee";

    public static String  WxCorpID = "ww3be210429b202e35";
    public static  String WXExpire = "7200";

    //订单送达时限(单位:s)
    public static int normal = 3600;
    public static int AOG = 1200;
    public static int emergent = 1800;

    //下载Excel位置
    public static String ExcelPath = "/document";

    public static void initSysConfig(Dao dao) {
        Globals.MyConfig.clear();
        List<Sys_config> configList = dao.query(Sys_config.class, Cnd.NEW());
        for (Sys_config sysConfig : configList) {
            switch (sysConfig.getConfigKey()) {
                case "AppName":
                    Globals.AppName = sysConfig.getConfigValue();
                    break;
                case "AppShrotName":
                    Globals.AppShrotName = sysConfig.getConfigValue();
                    break;
                case "AppDomain":
                    Globals.AppDomain = sysConfig.getConfigValue();
                    break;
                case "AppUploadPath":
                    Globals.AppUploadPath = sysConfig.getConfigValue();
                    break;
                case "OrderCountRef":
                    Globals.OrderCountRef = sysConfig.getConfigValue();
                    break;
                case "MapRef":
                    Globals.MapRef = sysConfig.getConfigValue();
                    break;
                case "APITokenExpire":
                    Globals.APITokenExpire=sysConfig.getConfigValue();
                    break;
                case "H5AppZoom":
                    Globals.H5AppZoom=sysConfig.getConfigValue();
                    break;
                case "AppZoom":
                    Globals.AppZoom=sysConfig.getConfigValue();
                    break;
                case "normal":
                        Globals.normal=Integer.parseInt(sysConfig.getConfigValue());
                    break;
                case "emergent":
                        Globals.emergent=Integer.parseInt(sysConfig.getConfigValue());
                    break;
                case "AOG":
                        Globals.AOG=Integer.parseInt(sysConfig.getConfigValue());
                    break;
                case "WxCorpID":
                        Globals.WxCorpID=sysConfig.getConfigValue();
                    break;
                case "WXExpire":
                        Globals.WXExpire=sysConfig.getConfigValue();
                    break;
                default:
                    Globals.MyConfig.put(sysConfig.getConfigKey(), sysConfig.getConfigValue());
                    break;
            }
        }
    }

    public static void initRoute(Dao dao) {
        Globals.RouteMap.clear();
        List<Sys_route> routeList = dao.query(Sys_route.class, Cnd.where("disabled", "=", false));
        for (Sys_route route : routeList) {
            Globals.RouteMap.put(route.getUrl(), route);
        }
    }


}
