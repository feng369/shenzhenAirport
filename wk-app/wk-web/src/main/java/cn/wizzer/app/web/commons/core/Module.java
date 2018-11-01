package cn.wizzer.app.web.commons.core;


import cn.wizzer.framework.shiro.ShiroSessionProvider;
import org.beetl.ext.nutz.BeetlViewMaker;
import org.nutz.mvc.annotation.*;
import org.nutz.mvc.ioc.provider.ComboIocProvider;
import org.nutz.plugins.apidoc.ApidocUrlMapping;
import org.nutz.plugins.apidoc.annotation.Manual;
import org.nutz.plugins.view.pdf.PdfViewMaker;


/**
 * Created by wizzer on 2016/6/21.
 */
@Modules(scanPackage = true, packages = "cn.wizzer")
@Ok("json:full")
@Fail("http:500")
@IocBy(type = ComboIocProvider.class, args = {"*json", "config/ioc/", "*anno", "cn.wizzer","org.nutz.apidoc", "*jedis", "*tx", "*quartz", "*async", "*rabbitmq"})
@Localization(value = "locales/", defaultLocalizationKey = "zh_CN")
@Encoding(input = "UTF-8", output = "UTF-8")
@Views({BeetlViewMaker.class, PdfViewMaker.class})
@SetupBy(value = Setup.class)
@ChainBy(args = "config/chain/nutzwk-mvc-chain.json")
@SessionBy(ShiroSessionProvider.class)
@UrlMappingBy(ApidocUrlMapping.class)
@Manual(name="API接口文档",description="机场物流API接口文档",author="BV-CLOUD",email="hwadmin@hangweigroup.com",homePage="http://www.bv-cloud.com",copyRight="&copy; 2018 BV All Right XXX")
public class Module {
}
