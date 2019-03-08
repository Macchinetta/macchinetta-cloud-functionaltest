<title>UploadForm</title>
<link rel="stylesheet"
    href="${pageContext.request.contextPath}/resources/app/css/styles.css">
<h1>ファイルアップロード</h1>
<script type="text/javascript"
    src="${pageContext.request.contextPath}/resources/vendor/jquery/jquery-3.3.1.js"></script>
<p class="guide">
アップロードファイルを選択し、[登録]を押してください。
</p>

<table>
    <tr>
        <th colspan="2"><Div Align="left">アップロードするファイルを選択してください。</Div></th>
    </tr>
    <tr>
        <td class="requireLine">
            <label for="file">ファイル</label>
        </td>
        <td>
            <input type="file" id="file" name="file" onclick="$('#message').text('');"/>
        </td>
    </tr>
</table>

<div class="navi-forward">
    <input type="checkbox" id="cbox1" value="0">有効期限切れテスト<br>
    <button type="button" id="uploadFile" class="forward" onclick="">登録</button>
</div>
  
<div id="message"></div>
  
<form:form action="${pageContext.request.contextPath}/logout">
    <div align="right"><button type="submit" id="logout">Logout</button></div>
</form:form>

<script type="text/javascript">
    $("#uploadFile").on('click', function(){
        var file = $('#file').prop('files')[0];

        var getAjax = function () {
            var uploadFileName = file.name;
            var dfd = new $.Deferred();

            $.ajax({
                url: '${pageContext.request.contextPath}/upload?info&filename=' + uploadFileName,
                type: 'GET'
            }).then(function(data) {
                if($("#cbox1:checked").val()=='0'){sleep(5000);}
                dfd.resolve(data);
            }).catch(function(jqXHR, textStatus, errorThrown) {
                dfd.reject(jqXHR, textStatus, errorThrown);
            });
            return dfd.promise();
        }

        var postAjax = function (getresult) {
            var dfd = new $.Deferred();
            var formData = new FormData();
            formData.append('key', getresult.objectKey);
            formData.append('x-amz-credential', getresult.credential);
            formData.append('acl', getresult.acl);
            formData.append('x-amz-security-token',getresult.securityToken);
            formData.append('x-amz-algorithm', getresult.algorithm);
            formData.append('x-amz-date', getresult.date);
            formData.append('x-amz-meta-filename', getresult.rawFileName);
            formData.append('policy', getresult.policy);
            formData.append('x-amz-signature', getresult.signature);
            formData.append('file',file);

            $.ajax({
                url: getresult.targetUrl,
                type: 'POST',
                data: formData,
                contentType: false,
                processData: false
            }).then(function(data, textStatus, jqXHR) {
                console.log("Upload " + textStatus);
                $('#message').text('アップロードに成功しました。');
                $('#file').val('');
                dfd.resolve()
            }).catch(function(jqXHR, textStatus, errorThrown) {
                dfd.reject(jqXHR, textStatus, errorThrown, getresult);
            });
            return dfd.promise();
        }

        if (file) {
            getAjax()
            .then(postAjax)
            .catch(function(jqXHR, textStatus, errorThrown, getresult) {
                console.log("Error xhr.status: " + jqXHR.status);
                console.log("Error xhr.statusText: " + jqXHR.statusText);
                console.log("Error status: " + textStatus);
                console.log("Error error: " + errorThrown);
                res = jqXHR.responseText;
                if(res){
                    var parser = new DOMParser();
                    var dom = parser.parseFromString(res, 'text/xml');
                    var errorCode = dom.getElementsByTagName('Code')[0].textContent;
                    if(errorCode == 'EntityTooLarge'){
                        $('#message').text('アップロードできるファイルは'+ getresult.fileSizeLimit +'バイトまでです。');
                    }else{
                        $('#message').text('アップロードに失敗しました。');
                    }
                }else{
                    $('#message').text('アップロードに失敗しました。');
                }
            });
        } else {
            $('#message').text('ファイルを選択してください。');
        }
        return false;
    });

    function sleep(waitMsec) {
        var startMsec = new Date();
        while (new Date() - startMsec < waitMsec);
    }
</script>
