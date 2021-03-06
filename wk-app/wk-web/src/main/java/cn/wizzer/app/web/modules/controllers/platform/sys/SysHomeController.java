package cn.wizzer.app.web.modules.controllers.platform.sys;

import cn.wizzer.app.base.modules.models.base_airport;
import cn.wizzer.app.base.modules.models.base_person;
import cn.wizzer.app.base.modules.models.base_place;
import cn.wizzer.app.base.modules.services.BaseAirportService;
import cn.wizzer.app.base.modules.services.BasePersonService;
import cn.wizzer.app.base.modules.services.BasePlaceService;
import cn.wizzer.app.logistics.modules.services.LogisticsOrderService;
import cn.wizzer.app.sys.modules.models.Sys_menu;
import cn.wizzer.app.sys.modules.services.SysMenuService;
import cn.wizzer.app.web.commons.base.Globals;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wizzer on 2016/6/23.
 */
@IocBean
@At("/platform/home")
public class SysHomeController {
    private static final Log log = Logs.get();
    @Inject
    private SysMenuService menuService;
    @Inject
    private BaseAirportService baseAirportService;
    @Inject
    private LogisticsOrderService logisticsOrderService;
    @Inject
    private BasePlaceService basePlaceService;

    @Inject
    private BasePersonService basePersonService;

    @At("")
    @Ok("beetl:/platform/sys/home.html")
    @RequiresAuthentication
    public void home(HttpServletRequest req) {
        //获取当前机场的地理位置并返回
        String position="";
        //获取当前登录人
        base_person person= basePersonService.getPersonInfo();
        if (!Strings.isBlank(person.getAirportid())) {
            base_airport baseairport = baseAirportService.fetch(person.getAirportid());
            position=baseairport.getPosition();
            req.setAttribute("obj", baseairport);
        }
            req.setAttribute("OrderCountRef",Globals.MapRef);
        //SecurityUtils.getSubject().getSession().setTimeout(1000);
    }



    @At
    @Ok("beetl:/platform/sys/left.html")
    @RequiresAuthentication
    public void left(@Param("url") String url, HttpServletRequest req) {
        String path = "";
        String perpath = "";
        if (!Strings.isBlank(Globals.AppBase)) {
            url = Strings.sBlank(url).substring(Globals.AppBase.length());
        }
        if (Strings.sBlank(url).indexOf("?") > 0)
            url = url.substring(0, url.indexOf("?"));
        Sys_menu menu = menuService.fetch(Cnd.where("href", "=", url));
        if (menu != null) {
            if (menu.getPath().length() > 8) {
                path = menu.getPath().substring(0, 8);
                perpath = menu.getPath().substring(0, 4);
            } else if (menu.getPath().length() == 8) {
                perpath = menu.getPath().substring(0, 4);
            }
            req.setAttribute("mpath", menu.getPath());
        }
        req.setAttribute("path", path);
        req.setAttribute("perpath", perpath);
    }

    @At
    @Ok("beetl:/platform/sys/left.html")
    @RequiresAuthentication
    public void path(@Param("url") String url, HttpServletRequest req) {
        if (Strings.sBlank(url).indexOf("//") > 0) {
            String[] u = url.split("//");
            String s = u[1].substring(u[1].indexOf("/"));
            if (Strings.sBlank(s).indexOf("?") > 0)
                s = s.substring(0, s.indexOf("?"));
            if (!Strings.isBlank(Globals.AppBase)) {
                s = s.substring(Globals.AppBase.length());
            }
            String[] urls = s.split("/");
            List<String> list = new ArrayList<>();
            if (urls.length > 5) {
                list.add("/" + urls[1] + "/" + urls[2] + "/" + urls[3] + "/" + urls[4] + "/" + urls[5]);
                list.add("/" + urls[1] + "/" + urls[2] + "/" + urls[3] + "/" + urls[4]);
                list.add("/" + urls[1] + "/" + urls[2] + "/" + urls[3]);
                list.add("/" + urls[1] + "/" + urls[2]);
                list.add("/" + urls[1]);
            } else if (urls.length == 5) {
                list.add("/" + urls[1] + "/" + urls[2] + "/" + urls[3] + "/" + urls[4]);
                list.add("/" + urls[1] + "/" + urls[2] + "/" + urls[3]);
                list.add("/" + urls[1] + "/" + urls[2]);
                list.add("/" + urls[1]);
            } else if (urls.length == 4) {
                list.add("/" + urls[1] + "/" + urls[2] + "/" + urls[3]);
                list.add("/" + urls[1] + "/" + urls[2]);
                list.add("/" + urls[1]);
            } else if (urls.length == 3) {
                list.add("/" + urls[1] + "/" + urls[2]);
                list.add("/" + urls[1]);
            } else if (urls.length == 2) {
                list.add("/" + urls[1]);
            } else list.add(url);
            String path = "";
            String perpath = "";
            Sys_menu menu = menuService.fetch(Cnd.where("href", "in", list).desc("href").desc("path"));
            if (menu != null) {
                if (menu.getPath().length() > 8) {
                    path = menu.getPath().substring(0, 8);
                    perpath = menu.getPath().substring(0, 4);
                } else if (menu.getPath().length() == 8) {
                    perpath = menu.getPath().substring(0, 4);
                }
                req.setAttribute("mpath", menu.getPath());
            }
            req.setAttribute("path", path);
            req.setAttribute("perpath", perpath);
        }
    }
    
    @At(value={"/", "/index"}, top=true)
    @Ok(">>:/sysadmin")
    public void index() {}
}
