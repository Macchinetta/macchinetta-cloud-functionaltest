<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>Home</title>
<link rel="stylesheet"
    href="${pageContext.request.contextPath}/resources/app/css/styles.css">
<script type="text/javascript"
    src="${pageContext.request.contextPath}/resources/vendor/jquery/jquery-3.3.1.js"></script>
</head>
<body>
<h1>ダウンロード</h1>
<p>
	<label><input type="radio" name="confirmKind" value="download">download</label>
	<label><input type="radio" name="confirmKind" value="show" checked>show direct</label>
</p>
<ul>
	<c:forEach var="key" items="${keys}">
		<li><a href="#" class="key" id="${key}">${key}</a></li>
	</c:forEach>
</ul>
<p>
<ul style="list-style:none">
	<li>Selected key:<span id="selectedKey"></span></li>
	<li>Load status:<span id="status">now loading...</span></li>
	<li>Pre-Signed URL:<span id="presignedUrl"></span></li>
	<li>S3 image:<span id="s3Image"></span></li>
	<li>Local image:<span id="localImage"></span></li>
	<li>S3 Base64:<span id="s3Base64"></span></li>
	<li>Local Base64:<span id="localBase64"></span></li>
</ul>
</p>
<script type="text/javascript">
    $(".key").click(function() {

        var statusView = $('#status').text('now loading...');

        var originUrl = '${pageContext.request.scheme}://${pageContext.request.serverName}:${pageContext.request.serverPort}/';
        var key = $(this).text();

        // 意図的にタイムアウトを発生させるため、キーに特定の文字列が含まれる場合は
        // タイムアウト値を設定する
        var timeout = (key.indexOf("expire") < 0) ? 0 : 40 * 1000;

        $.ajax({
            url: '${pageContext.request.contextPath}/download/url?key=' + key,
            type: 'GET'
        }).then(function(data) {
            var url = data.presignedUrl;
            $('#selectedKey').text(key);
            $('#presignedUrl').text(url);

            setTimeout(function() {
                if ($('input[name="confirmKind"]:checked').val() === 'show') {
                    showImage(
                        $('#s3Image').empty(),
                        $('#s3Base64').empty(),
                        url,
                        's3image-raw');
                    showImage($('#localImage').empty(),
                        $('#localBase64').empty(),
                        '${pageContext.request.contextPath}/resources/' + key,
                        'localImage-raw');
                } else {
                    download(url);
                }
            }, timeout);

            var showImage = function(image, base64, url, id) {
                $('<img>')
                    .attr({crossOrigin: originUrl})
                    .bind('load', function() {
                        $(this).appendTo(image);
                        imageToBase64(this, base64);
                        statusView.text('load complete.');
                    }).on('error', function () {
                    statusView.text('load failure.');
                }).attr({id: id, src: url});
            };

            var imageToBase64 = function(img, base64) {
                var canvas = $('<canvas>');
                canvas.appendTo(img);
                var context      = canvas[0].getContext('2d');
                canvas[0].width  = img.width;
                canvas[0].height = img.height;
                context.drawImage(img, 0, 0);
                base64.text(canvas[0].toDataURL('image/jpeg'));
                $(img).empty();
            };

            var download = function(url) {
                var a = document.createElement('a');
                document.body.appendChild(a);
                a.download = key;
                a.href = url;
                a.click();
            };
        }).catch(function(xhr, status, error) {
            console.log("Error xhr.status: " + xhr.status);
            console.log("Error xhr.statusText: " + xhr.statusText);
            console.log("Error status: " + status);
            console.log("Error error: " + error);
        });
    });
</script>
</body>
</html>
