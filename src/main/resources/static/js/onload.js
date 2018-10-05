$(function () {


    // alert(typeof Cookies.get("un"));
    // Cookies.set("un", "admin");
    // alert(Cookies.get("un"));
    // Cookies.remove("un");
    // alert(Cookies.get("un"));

    Bucket1 = 'public1-1253931949';
    Bucket60 = 'public60-1253931949';
    Region = "ap-shanghai";


    $(".search-btn").click(function () {
        chooseSearchWhat();
    });
    $(".search-box").keydown(function (e) {
        var x = e || event;
        var currKey = x.keyCode || x.which || x.charCode;//支持IE,FireFox
        if (currKey === 13) {
            chooseSearchWhat();
        }
    });

    var user_info_cookie = Cookies.get("user_info_cookie");
    if (user_info_cookie !== null) {
        $.get({
            url: "haveRememberMe",
            data: {
                u1: Cookies.get("user_info_cookie")
            },
            dataType: "text",
            cache: false,
            async: true,
            success: function (data) {
                var r = data.charAt(0);
                if (r === "1") {
                    var arr = data.split(" ");
                    $("#personal-center").html("欢迎您, " + arr[1] + "<span class='caret'></span>");
                    $("#li-personal-center").show();
                    $("#li-login").hide();
                    $("#li-login-out").show();
                    $("#li-register").hide();
                    $.session.set("un", arr[1]);
                }
            }
        });
    }

    $('#exampleModal').on('show.bs.modal', function () {
        // var button = $(event.relatedTarget);
        // var recipient = button.data('whatever');
        var modal = $(this);
        modal.find('.modal-title').text('文件上传选项');
        // modal.find('.modal-body input').val(recipient);
    });


    var file_upload_flag = true;
    $(".file-upload-destination").focusout(function () {
        // noinspection SpellCheckingInspection
        $.get({
            url: "trunie",
            data: {
                u1: this.value
            },
            dataType: "text",
            cache: false,
            success: function (data) {

                var $div_color = $("#file-destination");
                var $span_icon = $(".file-upload-dest-valid-icon");
                var $file_dest_input = $(".file-upload-destination");

                if ("0" === data) {
                    $div_color.removeClass("has-warning");
                    $div_color.addClass("has-success");
                    $span_icon.removeClass("glyphicon-warning-sign");
                    $span_icon.addClass("glyphicon-ok")
                } else {
                    if ($file_dest_input.val() !== null && $file_dest_input.val() !== "") {
                        $file_dest_input.attr("placeholder", "用户 [ " + $file_dest_input.val() + " ] 不存在，不指定请留空");
                        $file_dest_input.val("");
                        $div_color.removeClass("has-success");
                        $div_color.addClass("has-warning");
                        $span_icon.removeClass("glyphicon-ok");
                        $span_icon.addClass("glyphicon-warning-sign");
                    }
                }
            }
        });
    });

    $(".start-file-upload").click(function () {
        var $dest = $(".file-upload-destination");
        if (file_upload_flag || $dest.val() === null || $dest.val() === "") {
            goUpload();
        }
    });


    // $(".r-btn").keydown(function (e) {
    //     var x = e || event;
    //     var currKey = x.keyCode || x.which || x.charCode;//支持IE,FireFox
    //     if (currKey === 13) {
    //         alert("aaaa");
    //     }
    // });
    //
    //
    // $(".l-btn").keydown(function (e) {
    //     var x = e || event;
    //     var currKey = x.keyCode || x.which || x.charCode;//支持IE,FireFox
    //     if (currKey === 13) {
    //         // searchFile();
    //
    //         alert("aaaa");
    //     }
    // });
});

function goUpload() {
    uploadFile($(".file-upload-attach").val(), $(".file-upload-destination").val());
    $(".file-up-load-modal-close").click();
}

function chooseSearchWhat() {

    var $search_box = $(".search-box");
    var text = $search_box.val();
    if (!isEmpty(text)) {
        if (/^[A-Za-z0-9]{4}$/.test(text)) {
            searchFile(text);
            return;
        }
        if (text.charAt(0) === "f" || text.charAt(0) === "F") {
            doReallyFriAdd(text.substring(1));
            return;
        }
        showBannerInfo("alert-info", 1, "未搜索到任何内容", 2000, 1000, true);

    }
    $search_box.val("");
}

