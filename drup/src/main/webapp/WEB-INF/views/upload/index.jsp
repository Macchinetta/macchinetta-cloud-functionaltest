<title>UploadForm</title>
<link rel="stylesheet"
    href="${pageContext.request.contextPath}/resources/app/css/styles.css">
<h1>ファイルアップロード</h1>
<script type="text/javascript"
    src="${pageContext.request.contextPath}/resources/vendor/jquery/jquery-3.2.1.js"></script>
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
        
        if (file) {
            var uploadFileName = file.name;
            $.ajax({
                url: '${pageContext.request.contextPath}/upload?info&filename=' + uploadFileName,
                type: 'GET'
            }).done(function(data, textStatus, jqXHR) {
                if($("#cbox1:checked").val()=='0'){sleep(5000);}
                var formData = new FormData();
                formData.append('key', data.objectKey);
                formData.append('x-amz-credential', data.credential);
                formData.append('acl', data.acl);
                formData.append('x-amz-security-token',data.securityToken);
                formData.append('x-amz-algorithm', data.algorithm);
                formData.append('x-amz-date', data.date);
                formData.append('x-amz-meta-filename', data.rawFileName);
                formData.append('policy', data.policy);
                formData.append('x-amz-signature', data.signature);
                formData.append('file',file);
                $.ajax({
                    url: data.targetUrl,
                    type: 'POST',
                    data: formData,
                    contentType: false,
                    processData: false
                }).done(function(data, textStatus, jqXHR) {
                    console.log("Upload " + textStatus);
                    
                    $('#message').text('アップロードに成功しました。');
                    $('#file').val('');
                }).fail(function(jqXHR, textStatus, errorThrown) {
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
                            $('#message').text('アップロードできるファイルは'+ data.fileSizeLimit +'バイトまでです。');
                        }else{
                            $('#message').text('アップロードに失敗しました。');
                        }
                    }else{
                        $('#message').text('アップロードに失敗しました。');
                    }
                });
            }).fail(function(jqXHR, textStatus, errorThrown) {
                console.log("Error xhr.status: " + jqXHR.status);
                console.log("Error xhr.statusText: " + jqXHR.statusText);
                console.log("Error status: " + textStatus);
                console.log("Error error: " + errorThrown);
                $('#message').text('アップロードに失敗しました。');
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
