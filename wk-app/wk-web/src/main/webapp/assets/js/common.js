/**
 * Created by xl on 2017/6/20.
 */
// 下拉框选择
function bindVehicleDDL(name, code, opname, hidname) {
    $.ajaxSetup({ async:false });
    var url = "/platform/sys/dict/bindVehicleDDL";
    var param = {"name":name,"code":code};
    $.post(url, param, function (d) {
        $("#" + opname).append("<option value='' selected = 'selected'>请选择</option>");
        for(var i = 0; i < d.length; i++){
            $("#" + opname).append("<option value=" + d[i].id + ">" + d[i].name + "</option>");
        }
        $("#" + hidname).val($("#" + opname + " option:selected").val());
    });
}
// 下拉框选择
function bindVehicleDDLREL(parentId, opname, hidname) {
    //$.ajaxSetup({ async:false });
    var url = "/platform/sys/dict/bindVehicleDDLREL";
    var param = {"parentid": parentId};
    $.post(url, param, function (d) {
        $("#" + opname).empty();
        $("#" + opname).append("<option value='' selected = 'selected'>请选择</option>");
        for (var i = 0; i < d.length; i++) {
            $("#" + opname).append("<option value=" + d[i].id + ">" + d[i].name + "</option>");
        }
        $("#" + hidname).val($("#" + opname + " option:selected").val());
    });
}

// 下拉框选择
function bindVehicleDDL(name, code, opname, hidname, id) {
    //$.ajaxSetup({ async:false });
    var url = "/platform/sys/dict/bindVehicleDDL";
    var param = {"name": name, "code": code};
    $.post(url, param, function (d) {
        $("#" + opname).append("<option value='' selected = 'selected'>请选择</option>");
        for (var i = 0; i < d.length; i++) {
            if (id == d[i].id) {
                $("#" + opname).append("<option value=" + d[i].id + " selected>" + d[i].name + "</option>");
            } else {
                $("#" + opname).append("<option value=" + d[i].id + " >" + d[i].name + "</option>");
            }
        }
        $("#" + hidname).val($("#" + opname + " option:selected").val());
    });
}

function bindVehicleDDLBySelector(name, code, opname, hidname, selector) {
    //$.ajaxSetup({ async:false });
    var url = "/platform/sys/dict/bindVehicleDDL";
    var param = {"name": name, "code": code};
    $.post(url, param, function (d) {
        if (code != "emergency") {
            $("#" + opname).append("<option value='' selected = 'selected'>请选择</option>");
        }
        for (var i = 0; i < d.length; i++) {
            // alert(selector);
            if (selector == d[i].name) {
                $("#" + opname).append("<option value=" + d[i].id + " selected>" + d[i].name + "</option>");
            } else {
                $("#" + opname).append("<option value=" + d[i].id + " >" + d[i].name + "</option>");
            }
        }
        $("#" + hidname).val($("#" + opname + " option:selected").val());
    });
}


$.ajaxSetup({
    contentType: "application/x-www-form-urlencoded; charset=utf-8"
});
var DataDeal = {
//将从form中通过$('#form').serialize()获取的值转成json
    formToJson: function (data) {
        data = data.replace(/&/g, "\",\"");
        data = data.replace(/=/g, "\":\"");
        data = "{\"" + data + "\"}";
        return data;
    },
};
$.fn.tableinputtoJson = function () {
    var dataJson = [];
    $(this).find('tr:not(:first)').each(function () {
        var o = {};
        $(this).find("input").each(function () {
            o[this.name] = this.value;
        });
        if (!$.isEmptyObject(o)) {
            dataJson.push(o);
        }
    });
    return dataJson;
};

function getDisStstus(data) {
    var status = "";
    if (data == "0") {
        status = "保存";
    } else if (data == "1") {
        status = "订单提交，等待派单";
    } else if (data == "2") {
        status = "已派单，待配送员接单";
    } else if (data == "3") {
        status = "配送员已接单";
    } else if (data == "4") {
        status = "仓库备料中";
    } else if (data == "5") {
        status = "仓库备料完成";
    } else if (data == "6") {
        status = "配送中";
    } else if (data == "7") {
        status = "送达";
    } else if (data == "8") {
        status = "订单完成";
    } else if (data == "99") {
        status = "配送员拒接";
    }
    return status;
}

function getDeliveryStstus(data) {
    var status = "";
    if (data == "0") {
        status = "待接单";
    } else if (data == "1") {
        status = "配送中";
    } else if (data == "2") {
        status = "送达未完成";
    } else if (data == "3") {
        status = "已完成";
    }
    return status;
}

$('body').on('hidden.bs.modal', '.modal', function () {
    $(this).removeData('bs.modal');
});


function isOverTime(data) {
    if (data == 0) {
        return "否";
    } else if (data == 1) {
        return "是";
    } else {
        return "";
    }
}
function getSDtime(data) {
    if(data){
        $.get("${base!}/platform/logistics/Deliveryorderentry/getSDtime",{orderid:data},function (data) {
             if(data){
                 return data;
             }
             return "";
        });
    }
    return "";
}
