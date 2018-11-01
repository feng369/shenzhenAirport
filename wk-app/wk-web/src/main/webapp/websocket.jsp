<%--
  Created by IntelliJ IDEA.
  User: zhf
  Date: 2018/8/21
  Time: 10:29
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
    <script type="application/javascript">
        // 首先,需要创建一个WebSocket连接
        var ws;
        var WS_URL = window.location.host + ${base} + "/websocket"
        // 如果页面是https,那么必须走wss协议, 否则走ws协议
        if (location.protocol == 'http:') {
            ws = new WebSocket("ws://"+WS_URL);
        } else {
            ws = new WebSocket("wss://"+WS_URL);
        }
        // 连接成功后,会触发onopen回调
        ws.onopen = function(event) {
            console.log("websocket onopen ...");
            // 加入home房间
            ws.send(JSON.stringify({room:'home',"action":"join"}));
        };
        // 收到服务器发来的信息时触发的回调
        ws.onmessage = function(event) {
            console.log("websocket onmessage", event.data);
            var re = JSON.parse(event.data);
            if (re.action == "notify") {
                // 弹个浏览器通知
            } else if (re.action == "msg") {
                // 插入到聊天记录中
            }
        };

        // 定时发个空消息,避免服务器断开连接
        function ws_ping() {
            if (ws) {
                ws.send("{}"); // TODO 断线重连.
            }
        }
        setInterval("ws_ping()", 25000); // 25秒一次就可以了
    </script>
</head>
<body>

</body>
</html>
