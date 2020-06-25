$(function() {
    const $img = $('#imgFromCloudFront');
    var originSrc = $img.attr('src');
    $img.attr('src', '')

    // コールバックを設定
    var imgLoadState = $img.on('load', function() {
        console.log('Image loading is complete.');
        $('#imgLoadState').text('Image loading is complete');
    });

    // 画像読み込み開始
    $img.attr("src", originSrc);
});
