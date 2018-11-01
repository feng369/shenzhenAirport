package cn.wizzer.test;

import cn.wizzer.app.base.modules.models.base_person;
import cn.wizzer.app.base.modules.services.BaseAirmeterialService;
import cn.wizzer.app.base.modules.services.BaseCnctobjService;
import cn.wizzer.app.base.modules.services.BasePersonService;
import cn.wizzer.app.base.modules.services.BasePlaceService;
import cn.wizzer.app.logistics.modules.services.*;
import cn.wizzer.app.sys.modules.models.*;
import cn.wizzer.app.sys.modules.services.*;
import cn.wizzer.app.web.commons.base.Globals;
import cn.wizzer.app.web.modules.controllers.open.api.Datatime.newDataTime;
import com.alibaba.fastjson.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import javax.xml.bind.ValidationException;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.CountDownLatch;

@RunWith(MyNutTestRunner.class)
@IocBean// 必须有
public class SimpleTest extends Assert {

    private static final Log log = Logs.get();

    @Inject("refer:$ioc")
    protected Ioc ioc;

    @Inject
    protected Dao dao;
    @Inject
    private BasePersonService basePersonService;

    /*@Test
    public void test_user() {
        String orderno = "1145202";
        RPCServiceClient serviceClient = null;
        try {
            String wurl ="http://jw_test.shenzhenair.com:8090/MeWebService/services/JIWUService?wsdl";
            String nameSpace = "http://service.peanuts.smartme.szair.com";
            serviceClient = new RPCServiceClient();
            Options options = serviceClient.getOptions();
            // 这一步指定了该服务的提供地址
            EndpointReference targetEPR = new EndpointReference(wurl);
            // 将option绑定到该服务地址
            options.setTo(targetEPR);
            options.setManageSession(true);
            options.setProperty(HTTPConstants.REUSE_HTTP_CLIENT, true);
            options.setTimeOutInMilliSeconds(600000L);
            // 添加具体要调用的方法，这个可以从该服务的wsdl文件中得知
            // 第一个参数是该服务的targetNamespace，第二个为你所要调用的operation名称
            QName namespace = new QName(nameSpace, "zh_material_dispatching");
            // 输入参数
            Long l_orderno = Long.valueOf(orderno);
            Object[] param = new Object[] {l_orderno};
            // 指定返回的数据类型
            Class[] clazz = new Class[] { String.class };
            // 设置返回值类型
            Object[] res;
            res = serviceClient.invokeBlocking(namespace, param, clazz);
            log.info("消息接口返回结果:" + res[0]);
            *//*String rest =  "[{\"ORDERNO\":\"1466449\",\"ITEMNO\":\"1\",\"ORDERSTATE\":\"OPEN\",\"PN_L\":\"0-112-0016-2000\",\"PN_R\":\"0\",\"SN\":\"0\",\"BN\":\"0\",\"QTY\":\"1\"},{\"ORDERNO\":\"1466449\",\"ITEMNO\":\"2\",\"ORDERSTATE\":\"OPEN\",\"PN_L\":\"10-60751-1\",\"PN_R\":\"0\",\"SN\":\"0\",\"BN\":\"0\",\"QTY\":\"1\"},{\"ORDERNO\":\"1466449\",\"ITEMNO\":\"3\",\"ORDERSTATE\":\"OPEN\",\"PN_L\":\"101660-205\",\"PN_R\":\"0\",\"SN\":\"0\",\"BN\":\"0\",\"QTY\":\"1\"},{\"ORDERNO\":\"1466449\",\"ITEMNO\":\"4\",\"ORDERSTATE\":\"OPEN\",\"PN_L\":\"9059110-1\",\"PN_R\":\"9059110-1\",\"SN\":\"UNKM1778\",\"BN\":\"1447148\",\"QTY\":\"1\"},{\"ORDERNO\":\"1466449\",\"ITEMNO\":\"5\",\"ORDERSTATE\":\"OPEN\",\"PN_L\":\"8450B5\",\"PN_R\":\"8450B5\",\"SN\":\"8450B5-233\",\"BN\":\"1428418\",\"QTY\":\"1\"},{\"ORDERNO\":\"1466449\",\"ITEMNO\":\"7\",\"ORDERSTATE\":\"OPEN\",\"PN_L\":\"3291238-2\",\"PN_R\":\"0\",\"SN\":\"0\",\"BN\":\"0\",\"QTY\":\"1\"},{\"ORDERNO\":\"1466454\",\"ITEMNO\":\"1\",\"ORDERSTATE\":\"ISSUED\",\"PN_L\":\"3291238-2\",\"PN_R\":\"3291238-2\",\"SN\":\"10383\",\"BN\":\"890129\",\"QTY\":\"1\"}]";
             HashMap[] maps = Json.fromJsonAsArray(HashMap.class,rest);*//*
            List<HashMap> lists = Json.fromJsonAsList(HashMap.class,res[0].toString());
            List<Object> onoList = new ArrayList<Object>();//存放需求单号
            Map<String,List<Object>> dataMap = new HashMap<String,List<Object>>();
            for(int i=0;i<lists.size();i++){
                HashMap map =lists.get(i);
                String ono = (String) map.get("ORDERNO");
                String state = (String) map.get("ORDERSTATE");
                //过滤掉已经领料的数据
                if(!"OPEN".equals(state)){
                    lists.remove(map);
                    i--;
                    continue;
                }
                //按需求单号进行归类
                if(dataMap.containsKey(ono)){
                    List<Object> lm = dataMap.get(ono);
                    lm.add(map);
                    dataMap.put(ono,lm);
                }else{
                    onoList.add(ono);
                    List<Object> lm = new ArrayList<Object>();
                    lm.add(map);
                    dataMap.put(ono,lm);
                }
            }
            dataMap.put("OrderNoList",onoList);
            System.out.println(onoList);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            if(e instanceof AxisFault){
                new Exception("ERP系统接口查询失败："+e.getMessage());
            }
            e.printStackTrace();
            System.out.println(e.toString());
        }finally {
            try {
                if(serviceClient!=null)
                    serviceClient.cleanupTransport();
            } catch (AxisFault axisFault) {
                axisFault.printStackTrace();
            }
        }
    }*/


    @Inject
    private LogisticsOrderService logisticsOrderService;

    @Test
    public void testGoods() {

    }
/*11	唐秀周	H22910	13612955883	深圳港玄		WSTLY1234
12	刘炳汉	H22912	13631585537	深圳港玄		wxid_znhawsdqgigp21
13	谢正南	H22911	15012618389	深圳港玄		A15012618389
14	陈海亮	H22894	13510652127	深圳港玄		chl84181070
15	肖寿杰	H22895	13925294804	深圳港玄		xrj452110105
16	张深顺	H22897	13600199416	深圳港玄		s616773613
17	艾文奎	H22900	13428989197	深圳港玄		wxid_zwklflbivs5n11
18	聂刚	H22899	13628116408	深圳港玄		wxid_bb2aca9webw822
19	周京强	H22898	13352999126	深圳港玄		zjq540523880
20	莫崇杰	H22896	18923717882	深圳港玄		junfeng_2223
21	尹亮		13537733799	深圳港玄		yl13537733799
22	袁海波		17727447362	深圳港玄		wxid_x70am9lt2i9h22
23	苏水清		16675351214	深圳港玄		ssq053
24	谢火腾		15016723263	深圳港玄		wxid_kst466d0fqkj12
25	刘先成		13724260690	深圳港玄		benqyediaoxian
26	叶文君		13360980102	深圳港玄		jun343688782
27	唐境		18121949991	深圳港玄		yhnnxz
28	姚奇		15915468611	深圳港玄		wxid_kdw8wloyvhno21
29	朱少伟		13662298863	深圳港玄		a23314791
30	林伟		18948776124	深圳港玄		LniuniuO888
31	蒋承霖		13544060633	深圳港玄		j1004092671
32	丁海东		13532865195	深圳港玄		AAA13532865195
33	袁明浩		15012622334	深圳港玄		wxid_f2uqgq0d2u0w22
34	晏钦		13923474277	深圳港玄		sz_jysc
35	吴潭		13823610245	深圳港玄		wxid_cvjya79bghw222
36	张志远		15969479229	深圳港玄		RIDVEN
37	卓宇晨		13510945552	深圳港玄		zhuoyc5552
38	刘际德		18719045585	深圳港玄		wxid_g8rwtysl0ea022
39	徐勇		18607659825	深圳港玄		wxid_xflo9c97kgok22
40	万天保		15172767339	深圳港玄		wxid_fod32bqzrm9q22
41	龚国钢		13922933015	深圳港玄		gg_71062670
42	许斌		13724320406	深圳港玄		wxid_ese1w9h8c4ox21
43	温国全		13923486773	深圳港玄		wen13923486773
44	杨能茂		17503018187	深圳港玄		wxid_c59mv7mlap6222
45	刘必胜		13972281123	深圳港玄		wxid_3mskb158upd452
46	何忠权		13600251039	深圳港玄		wxid_pp68ae7d9sjd22
*/

    @Test
    public void testAddPerson() {
//        38	刘际德		18719045585	深圳港玄		wxid_g8rwtysl0ea022
      /*      base_person person = new base_person();
            person.setPersonnum("SZGX-0" +38);
            person.setPersonname("刘际德");
            person.setTel("18719045585");
            person.setWx("wxid_g8rwtysl0ea022");

            person.setPermitNumber("");
            person.setUnitid("ddb726791323403fafd0885ca55f234a");
            person.setAirportid("936546e4f4474ac3bdc9a2495a0adcee");
            base_person basePerson = basePersonService.insert(person);
            List<base_person> list = basePersonService.query(Cnd.where("personname", "=", basePerson.getPersonname()));
            System.out.println(list + "=====================================================================");
            System.out.println(list.size() + "<---------------------------");*/


    }

    @Test
    public void testDeletePerson() {
        base_person basePerson = basePersonService.fetch(Cnd.where("personname", "=", "刘际德"));
        basePersonService.delete(basePerson.getId());
    }

    @Inject
    private LogisticsOrderentryService logisticsOrderentryService;

    //物料清单
    @Test
    public void testEntity() {
        /*List<Map<String, Object>> entryByOrders = logisticsOrderentryService.getOrderEntryByOrderId("80c1355b29324d1bb9d751f88fcf9176", dataType, 1, null);
        for (Map<String, Object> entryByOrder : entryByOrders) {
            System.out.println(entryByOrder + "=================================================================");
        }*/
    }

    @Test
    public void testABC() {
 /*       Map map = logisticsOrderService.updateDvSteps("73c37ec47800483d989ae4b980ec0f57", "Ghgggg", "HL", "", "858c17e96e83445eb1770fee3d39e154");
        System.out.println(map);*/
    }

    @Inject
    private SysUserService sysUserService;
    @Inject
    private BaseCnctobjService baseCnctobjService;
    @Inject
    private SysRoleService roleService;

    @Test
    public void testAddUser() {
       /* List<base_person> basePersonList = basePersonService.query(Cnd.where("personnum", "LIKE", "%SZGX%"));
        for (base_person basePerson : basePersonList) {
            //1.用户表
            Sys_user user = new Sys_user();
            user.setLoginname(basePerson.getTel());
            user.setUsername(basePerson.getPersonname());
            user.setPassword("888888");
            RandomNumberGenerator rng = new SecureRandomNumberGenerator();
            String salt = rng.nextBytes().toBase64();
            String hashedPasswordBase64 = new Sha256Hash(user.getPassword(), salt, 1024).toBase64();
            user.setSalt(salt);
            user.setPassword(hashedPasswordBase64);
            user.setLoginPjax(true);
            user.setLoginCount(0);
            user.setLoginAt(0);
            sysUserService.insert(user);
            //2.关联对象
            base_cnctobj cnc = new base_cnctobj();
            cnc.setPersonId(basePerson.getId());
            cnc.setUserId(user.getId());
            cnc.setEmptypeId("a93d62c6f9be4882a1f63c386d85459c");
            baseCnctobjService.insert(cnc);
            //3.权限分配
            String menuIds = "fcacccf28101427295211522f7beb7cd";
            String roleid = "f6a5b8e17743437e80a7524ba8d20774";
            String[] ids = StringUtils.split(menuIds, ",");
            for (String s : ids) {
                if (!Strings.isEmpty(s)) {
                    roleService.insert("sys_user_role", org.nutz.dao.Chain.make("roleId", roleid).add("userId", s));
                }
            }
        }*/
    }

    @Inject
    private BasePlaceService basePlaceService;

    //
    @Test
    public void testAddPlace() {
        sysWxService.authWXUser("ZhaHuaFeng");
//        base_place basePlace = new base_place();
//    sysWxService.createWXUser("会会","18571532922");
        /*basePlace.setPlacecode("XK"+"210A");
        basePlace.setPlacename(     "2010A"+"复合材料钢索液压修理间");*/
      /*  basePlace.setPlacecode("XK"+"207");
        basePlace.setPlacename(     "207"+"复合材料无尘修理间");

        basePlace.setAirportId("936546e4f4474ac3bdc9a2495a0adcee");
        basePlace.setPosition("113.825757,22.631571");
        basePlace.setHasChildren(false);
        basePlace.setOpBy("ffdbc674b3f949f3bb28a98cd55a3946");
        basePlace.setCreater("ffdbc674b3f949f3bb28a98cd55a3946");
        basePlaceService.insert(basePlace);*/

    }

    @Inject
    private LogisticsDeliveryorderService deliveryorderService;
    @Inject
    private LogisticsSendtraceService logisticsSendtraceService;

    @Test
    public void testOrder() throws ParseException {
      /*
        String personid = "dc068fb98e8c43ecb9b9985b0a1a06e6";
        List<Map<String, Object>> customer = logisticsOrderService.getOrderListOfCustomer(personid);
        for (Map<String, Object> stringObjectMap : customer) {
            System.out.println(stringObjectMap);
        }
        */
        /*Map map = logisticsOrderService.getOrderInfoOfCustomer("a1bf5751ada448198b2a572384eb2bd3");
        System.out.println(map);*/

//        logistics_order ordr = new logistics_order();
//        ordr.setId("a1bf5751ada448198b2a572384eb2bd3");
//        logistics_Deliveryorder deliveryorder = new logistics_Deliveryorder();
//        deliveryorder.setId("f638636340504f32aa0617d9525089c8");
 /*       logistics_order order = logisticsOrderService.fetch("a1bf5751ada448198b2a572384eb2bd3");
        logistics_Deliveryorder deliveryorder = deliveryorderService.fetch("f638636340504f32aa0617d9525089c8");

        logisticsSendtraceService.addOne(deliveryorder,order);*/

  /*  Map map = logisticsOrderService.getOrderInfoOfCustomer("a1bf5751ada448198b2a572384eb2bd3");
        System.out.println(map);*/
        /*Sys_dict sysDict = sysDictService.fetch(Cnd.where("name", "=", "航线运输"));
        if(sysDict != null){
            //订单类型：航线运输
            String stepnum = logisticsOrderstepService.getStepByStepnum("JD", sysDict.getId());
            System.out.println(stepnum);
        }*/

           /*
            Map map = logisticsOrderService.getOrderInfoOfCustomer("a1bf5751ada448198b2a572384eb2bd3");
            System.out.println(map);
        */


//        Sql sql = Sqls.queryRecord("SELECT * FROM sys_dict WHERE id = '36a3a69fab514290bd4256c5def05a48'");
      /*  Sql sql = Sqls.fetchRecord("SELECT * FROM sys_dict WHERE id = '36a3a69fab514290bd4256c5def05a48'");
        dao.execute(sql);
                Record record = sql.getObject(Record.class);
        System.out.println(record.getString("id"));*/
//        System.out.println(record.getString("id"));


        /*logisticsSendtraceService.insertSendTrace("a1bf5751ada448198b2a572384eb2bd3","123","123.11,22.34");*/


        /*List<Map<String, Object>> mapList = logisticsOrderService.getOrderListByPersonId("dc068fb98e8c43ecb9b9985b0a1a06e6", null, null);
        System.out.println(mapList);*/

  /*      Map<String, Object> map = logisticsOrderService.addOrderByMobile("28737b2b73c64cc6b405beacb4d97e51", "6e25c4c2ccfa44caa19580bad4a0e4f5", "8fd7dec8862841e3986acdc2ad4f5f87", "8cba479c3b824c97b7b2e2501c221ee8", "23ba2bd8f9174a87933774806fbf1cce", "2018-06-02+15:00:09", "e34dddbfdc894db087734a475e9475c1", "858c17e96e83445eb1770fee3d39e154", "ffdbc674b3f949f3bb28a98cd55a3946");
        System.out.println(map);*/
        /*Map<String, Object> map = logisticsOrderService.addOrderEntryByMobiile("8c5e529fb3244db3a92e6380e5f61bcd", "物料C", "sahjdagjhdksah", "aksdakjdsadkjasdskj", "akkl", "sajkdsadgjdakjdjsdakjdsj");*/
        /*System.out.println(map);
        logistics_order order = logisticsOrderService.fetch(Cnd.where("id", "=", "8c5e529fb3244db3a92e6380e5f61bcd"));
        System.out.println(order.getLogistics_orderentry());*/

       /* Sys_user curUser = sysUserService.fetch("fcacccf28101427295211522f7beb7cd");
        if(curUser != null){
            System.out.println(curUser);*/
        /*logisticsOrderentryService.editOrderEntriesByMobile("123ABC","ae9f204ccee44f67917ee1d9081c4afe,saghkjaj");*/
        /*List<Map<String, Object>> orderEntryList = logisticsOrderentryService.getOrderEntryList("356e2c5c444d49119c79aeeb88381188,ef792c8b86154d99967fa23ed9e019c3");
         *//*  for (Map<String, Object> stringObjectMap : orderEntryList) {
            System.out.println(stringObjectMap);
        }*//*

        List<Map<String, Object>> placeInfoByMobile = basePlaceService.getPlaceListByMobile("37", "936546e4f4474ac3bdc9a2495a0adcee");

        for (Map<String, Object> stringObjectMap : placeInfoByMobile) {
            System.out.println(stringObjectMap);
        }*/


        /*List<Map<String, Object>> deliveryorderentryList = logisticsDeliveryorderentryService.getDeliveryorderentryList("a1bf5751ada448198b2a572384eb2bd3", null, null);
        for (Map<String, Object> stringObjectMap : deliveryorderentryList) {
            System.out.println(stringObjectMap);
            */
        logisticsDeliveryorderService.haveSendingOrders("1391d418c3ed4e72bca74ce72b983de7");
    }

    @Inject
    private LogisticsOrderstepService logisticsOrderstepService;

    @Inject
    private SysDictService sysDictService;
    @Inject
    private LogisticsDeliveryorderService logisticsDeliveryorderService;
    @Inject
    private LogisticsDeliveryorderentryService logisticsDeliveryorderentryService;

    @Inject
    private BaseAirmeterialService airmeterialService;
    @Inject
    private SysTempuserService sysTempuserService;

    @Test
    public void testSys() {
        /*sysUserService.addUserByMobile(Globals.customerid,"AAA","AAA","888888","aaa","15812341234");*/

      /*
        List<Map<String, Object>> list = basePlaceService.bindDDLByMobile("936546e4f4474ac3bdc9a2495a0adcee", null, null);
        for (Map<String, Object> stringObjectMap : list) {
            System.out.println(stringObjectMap);
        }
        */
     /*   try {
            logisticsOrderService.addOrderByMobile("c7fc2c3373254178ae3109ddb95a3944","df1e47b6e05e4f0b8ec86eec248a606d","7ff406d582594d6dace0a32a994c4f2e","23ba2bd8f9174a87933774806fbf1cce","f4ac901a6a944b85989c5f6dae533899","e34dddbfdc894db087734a475e9475c1","f61f152536ee48d8be7c85610a6987d4","");
        } catch (ParseException e) {
            e.printStackTrace();
        }


        Map<String, Object> idByMaterielNum = airmeterialService.getIdByMaterielNum("024147-000");
        System.out.println(idByMaterielNum);*/
      /*  Sys_tempuser tempuser = sysTempuserService.fetch(Cnd.where(Cnd.exps("username", "=", "哈哈").and("permitNumber", "=", "12345")).or("loginname", "=", "哈哈"));
        System.out.println(tempuser);*/

        /*
        List<Map<String, Object>> ss = logisticsOrderentryService.getOrderEntryByOrderId("2efeb614f4184ac5b7cf996c0741c4d2", null, null);
        for (Map<String, Object> s : ss) {
            System.out.println(s);
        }
        */

//        SELECT * FROM base_person WHERE  personnum = 'SZGX-034'
//        basePersonService.clear(Cnd.where("personnum","=","SZGX-034"));
    }

    @Inject
    private SysWxUserService sysWxUserService;
    @Inject
    private SysWxService sysWxService;
    @Inject
    private SysWxDeptService sysWxDeptService;


    @Test
    public void testWx() {
        sysWxUserService.download();
//         WHERE agentid='memo' AND corpid='ww60fc4401761a0e9f '
        /*
        List<Sys_wx> list = sysWxService.query(Cnd.where("agentid","=","memo").and("corpid","=","ww60fc4401761a0e9f"));
        System.out.println(list);
        System.out.println(list.size());*/
  /*      Set<String> sets = new HashSet<>();
        sets.add("38da70125db946709ea23c4dec886597");
        sets.add("cf7d7f34fa4e475e89533d7948542593");
        sets.add("a5ee5a274ef04561bb06102d6b0ac5d6");
        sysWxService.sendWxMessageAsy(sets,"您的订单已到达119机位,请及时签收!配送员[何中权],联系电话:13600251039");*/
       /*
        String userid = sysWxService.addWXUser("蔡巍", "18571532922");
        System.out.println(userid);
        */

    }

    @Test
    public void testWxP() {
      /*  String name = "测试";
        Sys_user sysUser = sysUserService.fetch(Cnd.where("username", "=", "测试"));
        System.out.println(sysUser.getId());*/

        Sys_wx_user user = new Sys_wx_user();
        user.setName("罗焱");
        user.setUserid("LuoYan");
        user.setDepartment("[1,4]");
        user.setGender("0");
        user.setMobile("18826258451");
        user.setStatus("1");
        sysWxUserService.insert(user);
    }

    //创建
    @Test
    public void testCreate() {
        //sysWxService.createWXUser("测试", "13812341234");
    }

    @Test
    public void testAdd() {
        JSONObject ob = sysWxService.getWxUser("szces");
        System.out.println(ob);
        sysWxUserService.insertWxUser(ob);
    }

    @Inject
    private SysVersionService sysVersionService;

    @Test
    public void testOrder1() throws ValidationException, ParseException {
        /*
        List<Map<String, Object>> mapList = logisticsOrderService.getOrderListOfCustomer("c7cfe65a83264bbabb54921c0b9bb2ce");
        System.out.println(mapList);*/
        /*  sysTempuserService.addUserByMobile("e34dddbfdc894db087734a475e9475c1","xxx","xxx","888","xxx","15807210192","xxx");*/
//        sysWxUserService.download();
        /*List<Sys_wx_user> sysWxUsers = sysWxUserService.query();
        if (sysWxUsers.size() > 0) {
            for (Sys_wx_user sysWxUser : sysWxUsers) {
                base_person person = basePersonService.fetch(Cnd.where("tel", "=", sysWxUser.getMobile()));
                //绑定企业微信
                if(person != null){
                    basePersonService.bindWxuser(person.getId(),sysWxUser.getUserid());
                }
            }
        }
        */

     /*   String userid = sysWxService.createWXUser("吕佳冀","13421816467","a10306");
        //下载到本地sys_wx_user
        if (!Strings.isBlank(userid)) {
            JSONObject jsonObject = sysWxService.getWxUser(userid);
            System.out.println(jsonObject);
            if (jsonObject != null) {
//                sysWxUserService.insertWxUser(jsonObject);
                //绑定企业微信
//                basePersonService.bindWxuser(person.getId(), userid);

            }
        }*/
 /*    Map map = new HashMap();
        String sd = logisticsOrderstepService.getStepByStepnum("SD", "72dd55d2c3cc42799c4e014745db2cdb");
        List<logistics_Deliveryorderentry> deliveryorderentries = logisticsDeliveryorderentryService.query(Cnd.where("orderid", "=", "211d635c4a1947429539220e3f9529e6").and("step","=",sd));
        if(deliveryorderentries.size() == 1){
            map.put("sdTimeoutReason",deliveryorderentries.get(0).getTimeoutreason());
        }
        System.out.println(map);*/
    /*    Map<String, Object> map = logisticsOrderService.isSDOrderOvertime("061e331343a646f5be33dd28ee8def5f", "c7cfe65a83264bbabb54921c0b9bb2ce");
        System.out.println(map);*/
        /*List<logistics_Deliveryorderentry> entriesByStepNum = logisticsDeliveryorderentryService.getDeliveryOrderEntriesByStepNum("SD", "061e331343a646f5be33dd28ee8def5f");
        System.out.println(entriesByStepNum);*/
//        sysVersionService.editVersion("android-PS","1.1.19","","1.1.18");
      /*  String hl = logisticsOrderstepService.getStepByStepnum("SD", "72dd55d2c3cc42799c4e014745db2cdb");
        System.out.println(hl);*/
      logisticsOrderService.isSDOrderOvertime("061e331343a646f5be33dd28ee8def5f");


    }

    @Test
    public void testAAA() throws ValidationException,ParseException {
       /* System.out.println(basePlaceService.bindDDLByMobile("航线","936546e4f4474ac3bdc9a2495a0adcee",null,null));*/
     /*   Map<String, Object> orderInfo = logisticsOrderService.getOrderInfo("061e331343a646f5be33dd28ee8def5f");
        System.out.println(orderInfo);*/
        /*Date requestDate = newDataTime.getSdfByPattern(null).parse("2018-07-16 10:49:00");
        Date testDate = newDataTime.getSdfByPattern(null).parse("2018-07-16 10:30:00");
        System.out.println(requestDate.compareTo(testDate));*/
//        logisticsDeliveryorderService.orderexe("061e331343a646f5be33dd28ee8def5f");
//        System.out.println(sysUserService.fetch("01a566af3443403386e73d2ace73567d"));
        String customerid = Globals.customerid;
        String intime = newDataTime.getDateYMDHMS();
        System.out.println(logisticsOrderService.getOrderNum(customerid,intime));
        int count = 10;
        CountDownLatch cdl = new CountDownLatch(count);
        for (int i = 0; i < 100; i++) {
            new Thread(){
                public void run() {
                    try {
                        System.out.println(logisticsOrderService.getOrderNum(customerid,intime));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();
            cdl.countDown();
        }
        try {
            //使线程在锁存器倒计数至零之前一直等待
            cdl.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void testBBB() throws ValidationException,ParseException {
 /*List<Record> deliveryOrderList = logisticsDeliveryorderService.getDeliveryOrderList("4804cad4ebdc475c8535513d5549d986", "1", "", null, null);
        for (Record record : deliveryOrderList) {
            System.out.println(record);
        }*/
        /*List<Map<String, Object>> mapList = logisticsDeliveryorderService.haveSendingOrders("4804cad4ebdc475c8535513d5549d986");
        for (Map<String, Object> map : mapList) {
            System.out.println(map);
        }*/
        /*Map countByMobile = logisticsOrderService.getOrderCountByMobile("65083015958e4b50be577ec9bc78930b", false);
        System.out.println(countByMobile);*/
       /*
       List<Map<String, Object>> orderList = logisticsOrderService.getOrderList("858c17e96e83445eb1770fee3d39e154", "2", "", "", "", "28737b2b73c64cc6b405beacb4d97e51", "", 1, null);
        for (Map<String, Object> map : orderList) {
            System.out.println(map);
        }
        */
        /*
        int todayOrders = logisticsOrderService.count("logistics_order",
                Cnd.where("otype", "=", "72dd55d2c3cc42799c4e014745db2cdb")
                        .and("createTime", ">=", newDataTime.getIntegerByDate(newDataTime.startOfToday()))
                        .and("createTime", "<=", newDataTime.getIntegerByDate(newDataTime.endOfToday()))
                        .and("airportid", "=", "936546e4f4474ac3bdc9a2495a0adcee").and("delFlag","=",0));
        System.out.println(todayOrders);
        */
//        logisticsOrderService.getOrderInfo("");
        /*Map countByMobile = logisticsOrderService.getOrderCountByMobile("4804cad4ebdc475c8535513d5549d986", false);
        System.out.println(countByMobile);*/
      /*  Cnd cnd = Cnd.where(new Static("Date(cptime)='" + newDataTime.getCurrentDateStr("yyyy-MM-dd") + "'")).and("pstatus", "=", 8).and("personid", "=", "4804cad4ebdc475c8535513d5549d986").and("delFlag", "=", 0);
        System.out.println(cnd);
        int todayOrders = logisticsOrderService.count(cnd);
        System.out.println(todayOrders);*/

    }
    @Test
    public void testOrder9() throws ValidationException,ParseException {
       /* List<Map<String, Object>> list = logisticsOrderService.getOrderList("c7cfe65a83264bbabb54921c0b9bb2ce", "2", "", "", "", "", "", 1, null, "DE1151293");
        for(Map<String, Object> map : list) {
            System.out.println(map);
        }*/
        /*

        List<Map<String, Object>> entryByOrderId = logisticsOrderentryService.getOrderEntryByOrderId("ac8fd16f62544817ba792d248bcb564e", 1, 10);
        for (Map<String, Object> stringObjectMap : entryByOrderId) {
            System.out.println(stringObjectMap);
        }
        */
//        sysVersionService.editVersion("ios_xq","1.1.7",false);
        /*List<Map<String, Object>> deliveryOrderList = logisticsDeliveryorderService.getDeliveryOrderList("4804cad4ebdc475c8535513d5549d986", "3", "", 1, null);
        for (Map<String, Object> stringObjectMap : deliveryOrderList) {
            System.out.println(stringObjectMap);
        }*/
        /*logisticsOrderService.getOrderList("4804cad4ebdc475c8535513d5549d986","","","","","","",1,null,"");*/
       /* logisticsOrderService.getOrderList("c7cfe65a83264bbabb54921c0b9bb2ce","2","","","","","",1,null,"");*/


    }
    @Test
    public void testOrder10() throws ValidationException,ParseException {
      /*
       List<Map<String, Object>> deliveryOrderList = logisticsDeliveryorderService.getDeliveryOrderList("858c17e96e83445eb1770fee3d39e154", "8", "", 1, null);
        for (Map<String, Object> stringObjectMap : deliveryOrderList) {
            System.out.println(stringObjectMap);
        }
        */
        /*
        Map<String, Object> orderInfo = logisticsOrderService.getOrderInfoOfCustomer ("f95a9ec7d893477484471389571d9707");
        System.out.println(orderInfo);
        */
    /*
    List<Map<String, Object>> deliveryOrderList = logisticsDeliveryorderService.getDeliveryOrderList("4804cad4ebdc475c8535513d5549d986", "3", "", 1, null);
        for (Map<String, Object> stringObjectMap : deliveryOrderList) {
            System.out.println(stringObjectMap);
        }
        */
        /*SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar c = Calendar.getInstance();
        Sys_dict sysDict = sysDictService.fetch("6e25c4c2ccfa44caa19580bad4a0e4f5");
        if ("emergency.nomal".equals(sysDict.getCode())) {
            c.add(Calendar.SECOND, Globals.normal);
            System.out.println(df.format(c.getTime()));
        } else if ("emergency.urgent".equals(sysDict.getCode())) {
            c.add(Calendar.SECOND, Globals.emergent);
            System.out.println(df.format(c.getTime()));
        } else if ("emergency.aog".equals(sysDict.getCode())) {
            c.add(Calendar.SECOND, Globals.AOG);
            System.out.println(df.format(c.getTime()));
        } else {

        }*/

       /*
       Map<String, Object> unreceivedCount = logisticsOrderService.getUnreceivedCount("4804cad4ebdc475c8535513d5549d986");
        System.out.println(unreceivedCount);
        */
     /*
       Map<String, Object> listOfCustomer = logisticsOrderService.getOrderListOfCustomer("4804cad4ebdc475c8535513d5549d986");
        System.out.println(listOfCustomer);
        */

    }
    @Test
    public void testOrder11() throws ValidationException,ParseException {
        /*
        List<Map<String, Object>> orderListByPersonId = logisticsOrderService.getOrderListByPersonId("4804cad4ebdc475c8535513d5549d986", 1, null);
        for (Map<String, Object> stringObjectMap : orderListByPersonId) {
            System.out.println(stringObjectMap);
        }
        */
        /*
        List<Map<String, Object>> orderList = logisticsOrderService.getOrderListByPersonId("4804cad4ebdc475c8535513d5549d986", 1, null);
        for (Map<String, Object> stringObjectMap : orderList) {
            System.out.println(stringObjectMap);
        }
        */

   /*
        List<Map<String, Object>> list = logisticsOrderService.getOrderListByPersonId("4804cad4ebdc475c8535513d5549d986", 1, null);
        for (Map<String, Object> stringObjectMap : list) {
            System.out.println(stringObjectMap);
        }
        */
        /*List<Map<String, Object>> orderList = logisticsOrderService.getOrderListByPersonId("4804cad4ebdc475c8535513d5549d986", 1, null);
        for (Map<String, Object> stringObjectMap : orderList) {
            System.out.println(stringObjectMap);
        }*/
        List<Map<String, Object>> list = logisticsOrderService.getOrderListByPersonId("4804cad4ebdc475c8535513d5549d986", 1, null);
        for (Map<String, Object> stringObjectMap : list) {
            System.out.println(stringObjectMap);
        }
    }
    @Test
    public void testOrder12() throws ValidationException,ParseException {
      /*
        List<Map<String, Object>> list = logisticsOrderService.getOrderListByPersonId("4804cad4ebdc475c8535513d5549d986", 1, null);
        for (Map<String, Object> stringObjectMap : list) {
            System.out.println(stringObjectMap);
        }
       */
     /*   List<Map<String, Object>> list = logisticsOrderService.getOrderListByPersonId("4804cad4ebdc475c8535513d5549d986", 1, null);
        for (Map<String, Object> stringObjectMap : list) {
            System.out.println(stringObjectMap);
        }*/
       /* List<Map<String, Object>> orderEntryByOrderId = logisticsOrderentryService.getOrderEntryByOrderId("d67a036a53884fe3827d0adb2de06a87", "getAllMateriaData", 1, null);
        for (Map<String, Object> stringObjectMap : orderEntryByOrderId) {
            System.out.println(stringObjectMap);
        }*/
       /* List<Map<String, Object>> listByPersonId = logisticsOrderService.getOrderListByPersonId("eb6a0022bc75484a9f9a4c71c939a86f", 1, null);
        for (Map<String, Object> stringObjectMap : listByPersonId) {
            System.out.println(stringObjectMap);
        }*/
       /* List<Map<String, Object>> listByPersonId = logisticsOrderService.getOrderListByPersonId("eb6a0022bc75484a9f9a4c71c939a86f", 1, null);
        System.out.println(listByPersonId);*/
       /* Map<String, Object> orderListOfCustomer = logisticsOrderService.getOrderListOfCustomer("4804cad4ebdc475c8535513d5549d986");
        System.out.println(orderListOfCustomer);*/

    }
    @Inject
    private LogisticsRecordtraceService logisticsRecordtraceService;
    @Test
    public void testOrder13() throws ValidationException, ParseException, InterruptedException {
        /*    logisticsSendtraceService.insertSendTrace("056316825f3a45f4abb02b58fff63af1","4804cad4ebdc475c8535513d5549d986","123.123,23.234");*/
            /*System.out.println(logisticsRecordtraceService.query(Cnd.where("orderid","=","056316825f3a45f4abb02b58fff63af1")));*/
        /*Map<String, Object> orderListOfCustomer = logisticsOrderService.getOrderListOfCustomer("4804cad4ebdc475c8535513d5549d986");
        System.out.println(orderListOfCustomer);*/
      /*  List<Map<String, Object>> list = logisticsOrderService.getOrderListByPersonId("4804cad4ebdc475c8535513d5549d986", 1, null);
        for (Map<String, Object> stringObjectMap : list) {
            System.out.println(stringObjectMap);
        }*/

        /*List<Map<String, Object>> list = logisticsOrderService.getOrderListByPersonId("4804cad4ebdc475c8535513d5549d986", 1, null);
        for (Map<String, Object> stringObjectMap : list) {
            System.out.println(stringObjectMap);
        }*/

    }
}