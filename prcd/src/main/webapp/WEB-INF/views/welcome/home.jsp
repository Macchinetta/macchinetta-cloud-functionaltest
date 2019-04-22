<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>Home</title>
<link rel="stylesheet"
    href="${pageContext.request.contextPath}/resources/app/css/styles.css">

<script type="text/javascript">
    var start = new Date();

    //初期化
    var hour = 0;
    var min = 0;
    var sec = 0;
    var now = 0;
    var datet = 0;

    function disp() {

        now = new Date();

        datet = parseInt((now.getTime() - start.getTime()) / 1000);

        hour = parseInt(datet / 3600);
        min = parseInt((datet / 60) % 60);
        sec = datet % 60;

        if (hour < 10) {
            hour = "0" + hour;
        }
        if (min < 10) {
            min = "0" + min;
        }
        if (sec < 10) {
            sec = "0" + sec;
        }

        var timer = hour + ':' + min + ':' + sec;

        document.getElementById('timer').value = timer;
        setTimeout("disp()", 1000);

    }
    var src;

    function reloadObject() {
        console.log('reloadObject()');
        var obj = document.getElementById('cloudfrontContents');
        if (!src) {
            src = obj.getAttribute('src');
        }
        obj.setAttribute('src', src + Math.random());
    }

    function ImageToBase64(img, mime_type) {
        console.log('ImageToBase64(img, mime_type)');
        var canvas = document.createElement('canvas');
        canvas.width = img.width;
        canvas.height = img.height;
        var ctx = canvas.getContext('2d');
        ctx.drawImage(img, 0, 0);
        return canvas.toDataURL(mime_type);
    }

    function loadImage(url, callback) {
        console.log('loadImage(url, callback)');
        var xhr = new XMLHttpRequest();
        xhr.onload = function() {
            var reader = new FileReader();

            reader.readAsDataURL(xhr.response);
            reader.onloadend = callback;
        };
        xhr.withCredentials = true;
        xhr.responseType = 'blob';
        xhr.open("GET", url, true);
        xhr.send();
    }

    function loadObjectAjax(src) {
        console.log('loadObjectAjax(src)');
        console.log(src);
        document.getElementById('cloudFrontResult').value = '';
        loadImage(
                src + Math.random(),
                function(e) {
                    console.log(e.target.result);
                    document.getElementById('cloudFrontResult').value = e.target.result;
                });
    }

    function loadObjectForVerificationAjax(src) {
        console.log('loadObjectForVerificationAjax(src)');
        console.log(src);
        document.getElementById('appResult').value = '';
        loadImage(src, function(e) {
            console.log(e.target.result);
            document.getElementById('appResult').value = e.target.result;
        });
    }

    function onErrorAtObject(self) {
        console.log('onErrorAtObject(self)');
        document.getElementById('reject').innerHTML = 'access denied'

    }
</script>

</head>
<sec:authentication property="principal.account" var="account" />
<body onLoad="disp()">
    <div id="wrapper">
        <h1>Hello world!</h1>
        <p>The time on the server is ${serverTime}.</p>
        <h2>User Information</h2>
        <p>Welcome ${f:h(account.firstName)} ${f:h(account.lastName)} !!</p>

        <p>
            会員区分：
            <c:choose>
                <c:when test="${account.authorities == '[PAID]'}">有料会員</c:when>
                <c:when test="${account.authorities == '[UPAY]'}">無料会員</c:when>
            </c:choose>
        </p>

        <h2>content</h2>

        <c:choose>
            <c:when test="${account.authorities == '[PAID]'}">有料会員さまは、「課金コンテンツ」をお楽しみいただけます。<br>
                <br>
            </c:when>
            <c:when test="${account.authorities == '[UPAY]'}">無料会員は購読できません。<br>
                <br>
            </c:when>
        </c:choose>

        <spring:eval var="protocol"
            expression="@environment.getProperty('functionaltest.cf.signature.protocol')" />
        <spring:eval var="distributionDomain"
            expression="@environment.getProperty('functionaltest.cf.signature.distributionDomain')" />
        <spring:eval var="path"
            expression="@environment.getProperty('functionaltest.cf.signature.cookiePath')" />

        <spring:eval var="allowedIpRange"
            expression="@environment.getProperty('functionaltest.cf.signature.allowedIpRange')" />

        <spring:eval var="timeToActive"
            expression="@environment.getProperty('functionaltest.cf.signature.timeToActive')" />
        <spring:eval var="timeToExpire"
            expression="@environment.getProperty('functionaltest.cf.signature.timeToExpire')" />



        <ul>

            <li><p>課金コンテンツ</p>
                <ul>
                    <li>目視確認用
                        (※アクセスが可能であればコンテンツの内容が表示されるが失敗した場合には、その旨メッセージが表示される。)
                        <p>
                        <div>
                            <span style="vertical-align: top;">比較用画像：</span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                            <div style="border: 1px solid red; width: 100px; height: 100px;">
                                <img alt="" width="100px" height="100px"
                                    src="${pageContext.request.contextPath}/resources/app/image/2.png" />
                            </div>
                        </div> <br>
                        <div>
                            <span style="vertical-align: top;">CloudFront画像：</span>&nbsp;
                            <div style="border: 1px solid red; width: 100px; height: 100px;">
                                <img id="cloudfrontContents" alt="" width="100px" height="100px"
                                    src="${protocol}://${distributionDomain}${path}/paid/2.png?userId=${f:h(account.userId)}" />
                            </div>

                        </div>
                        <p>
                            <label>経過時間&nbsp;<input type="text" id="timer" size="8"></label><br>
                            <button id="reload" type="button" onclick="reloadObject();">再取得</button>
                            <br /> <span style="color: red;">※署名付きCookieが発行されている場合は、ポリシーで設定した署名付きCookieの制限で取得可否がかわるので再取得をする。</span>
                            現在の設定では
                            <input type="text" size="3" readonly="readonly" value="${timeToActive}" />分後に有効、
                            <input type="text" size="3" readonly="readonly" value="${timeToExpire}" />分後に無効、
                            IP範囲は&nbsp;<input type="text" size="10" readonly="readonly" value="${allowedIpRange}" />

                            &nbsp;となる。
                        </p> <br />





                        </p>
                    </li>
                    <div id="verificationContent" style="display: none;">
                        <li>Ajaxで取得した自動判定用のコンテンツ値 <br /> <span
                            style="vertical-align: top;">比較用画像：</span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                            <textarea name="comment_a" cols="110" rows="55" id="appResult"
                                readonly="readonly"></textarea> <br /> <br /> <span
                            style="vertical-align: top;">CloudFront画像：</span>&nbsp; <textarea
                                name="comment_b" cols="110" rows="55" id="cloudFrontResult"
                                readonly="readonly"></textarea> <br /> <br />

                        </li>
                    </div>
                </ul>

                <p>
                    <button id="loadVerificationContent" type="button"
                        onclick="document.getElementById('verificationContent').style.display='block';loadObjectAjax('${protocol}://${distributionDomain}${path}/paid/2.png?userId=${f:h(account.userId)}');loadObjectForVerificationAjax('${pageContext.request.contextPath}/resources/app/image/2.png');">Load
                        verification content</button>
                </p></li>
            <li><p>アクセス拒否コンテンツ</p>
                <ul>
                    <li>以下は、アクセスが可能であれば画像が表示されるがアクセス不可に設定してあるため、その旨メッセージが表示される。
                        <p id="reject">
                            <img alt=""
                                src="${protocol}://${distributionDomain}${path}/reject/1.png"
                                onerror="onErrorAtObject(this);">
                        </p>
                    </li>
                </ul></li>

        </ul>


        <p>
            <form:form action="${pageContext.request.contextPath}/disableCookie">
                <button id="disableCookie" type="submit">disableCookie</button>
            </form:form>
        </p>
        <p>
            <form:form action="${pageContext.request.contextPath}/logout">
                <button id="logout" type="submit">Logout</button>
            </form:form>
        </p>
    </div>
</body>
</html>
