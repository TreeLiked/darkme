// noinspection SpellCheckingInspection
function uploadFile(atta, destination) {
    var file = document.getElementById("file").files[0];
    if (!file) return;

    var bringNo = "";
    var bucketNo = "";
    var days = "";

    var filename = file.name;
    var suffix = filename.substring(filename.indexOf(".") + 1);
    if (filename.indexOf(".") > 20) {
        filename = filename.substring(0, 20);
        filename = filename + "." + suffix;
    }
    $.get({
        url: "generateFileNo",
        cache: false,
        async: false,
        dataType: "text",
        data: {
            fileName: filename
        },
        success: function (nos) {

            if (nos !== "0") {

                var arr = nos.split("/");
                var file_no = arr[0];
                var file_bucket_no = arr[1];

                var $info_area = $(".right_info_area");
                var $onProgressAlert = createAlertDiv("alert-info", "正在上传文件: " + file.name, true, true);
                $info_area.append($onProgressAlert);
                $onProgressAlert.fadeIn(1000);

                var $bar_parent = $(".progress");
                $bar_parent.show();
                var $bar_parent_width = $bar_parent.width();

                var $bar = $(".progress-bar");
                //初始化bar
                $bar.css("width", "0px");
                $bar.text(0 + "%");

                var $detail_info = createAlertDiv("alert-warning", "", true, true);
                $info_area.append($detail_info);
                $detail_info.fadeIn(1000);
                var detailArray = new Array(3);

                var haveCreatedSuccessDiv = false;

                var Bucket;
                if ($.session.get("un") !== undefined) {
                    Bucket = Bucket60;
                } else {
                    Bucket = Bucket1;
                }
                getCos().sliceUploadFile({
                    Bucket: Bucket,
                    Region: Region,
                    Key: file_bucket_no,
                    Body: file,
                    TaskReady: function (tid) {
                        TaskId = tid;
                    },
                    onProgress: function (progressData) {
                        var flag = false;
                        $.each(progressData, function (name, value) {
                            // var a = parseInt(value);
                            if (name === "loaded") {
                                detailArray[0] = byte_to_KMG(value);
                            }

                            if (!flag) {
                                if (name === "total") {
                                    detailArray[1] = byte_to_KMG(value);
                                    flag = true;
                                }
                            }
                            if (name === "speed") {
                                var str3;
                                var m3 = Math.floor(value / Math.pow(1024, 2));
                                if (m3 > 0) {
                                    m3 = m3.toFixed(2);
                                    str3 = m3 + " MB/s"
                                } else {
                                    var k3 = (value / Math.pow(1024, 1)).toFixed(2);
                                    str3 = k3 + " KB/s";
                                }
                                detailArray[2] = str3;
                            }
                            if (name === "percent") {
                                var percent = value.toString().charAt(0);
                                if (percent !== "1") {
                                    // $("#up_percent").text("上传百分比:\t" + percent + "%");
                                    $bar.css("width", value * $bar_parent_width + "px");
                                    percent = value.toFixed(2) * 100;
                                    $bar.text(percent + "%");

                                } else {
                                    $bar.css("width", $bar_parent_width + "px");
                                    $bar.text(100 + "%");
                                    $bar.removeClass("active");
                                    $bar_parent.fadeOut(1000);

                                    $onProgressAlert.slideUp(1500);
                                    $detail_info.slideUp(1500);
                                    if (!haveCreatedSuccessDiv) {

                                        $.post({
                                            url: "generateNewFile",
                                            cache: false,
                                            async: false,
                                            dataType: "text",
                                            data: {
                                                fileNo: file_no,
                                                fileName: filename,
                                                attachment: atta,
                                                destination: destination,
                                                fileSize: byte_to_KMG(file.size)
                                            },
                                            success: function (nos) {

                                                if (nos !== "0") {
                                                    var strings = nos.split(" ");
                                                    bringNo = strings[0];
                                                    bucketNo = strings[1];
                                                    days = strings[2];
                                                    var $success_info = createAlertDivClosed("alert-success", file.name, "上传成功（编号：" + bringNo + "，" + days + "天内有效）");
                                                    $info_area.append($success_info);
                                                    $success_info.show(1000);
                                                    haveCreatedSuccessDiv = true;
                                                    parent.showAmountFlags();
                                                } else {
                                                    showBannerInfo("alert-danger", 0, "SOMETHING WENT WRONG", 2000, 1000);
                                                }
                                                // mod_btn_upload(1);
                                            }
                                        });

                                        // parent.showAmountFlags();
                                    }
                                    return false;
                                }
                            }
                        });
                        $detail_info.html("速度: " + detailArray[2] + "   已经上传: " + detailArray[0] + "   总计大小: " + detailArray[1]);
                    }
                }, function (err, data) {
                    console.log(err, data);
                    // mod_btn_upload(1);
                });
            }
        }
    });
}

function searchFile(text) {
    $.get({
        url: "getObjectDetail",
        data: {bringNo: text},
        dataType: "text",
        cache: false,
        success: function (data) {

            switch (data) {
                case 'null':
                    showBannerInfo("alert-danger", 1, "不存在该编号的文件 || 该编号已经过期", 4000, 1000, true);
                    break;
                case "no":
                    showBannerInfo("alert-warning", 1, "您没有权限下载此文件", 4000, 1000, true);
                    break;
                default:
                    var obj = JSON.parse(data);
                    mod_style(1);
                    // noinspection JSUnresolvedVariable
                    // var $info_banner = createAlertDiv("alert-info", "<a href=\'doDownload?bucketId=" + obj.file_bucket_id + "&filename=" + obj.file_name + "'>" + "文件名：" + obj.file_name + "，大小：" + obj.file_size + "，备注： " + obj.file_attach + "，上传时间：" + obj.file_post_date + "，文件所有者：" + obj.file_post_author + "&emsp;&emsp;点击这条信息开始下载（10s后消失)</a>", false, true);

                    // noinspection JSUnresolvedVariable
                    showBannerInfo("alert-info", 0, "<a href='doDownload?bucketId=" + obj.file_bucket_id + "&filename=" + obj.file_name + "&file_save_days=" + obj.file_save_days + "'>" + "文件名：" + obj.file_name + "，大小：" + obj.file_size + "，备注： " + obj.file_attach + "，上传时间：" + obj.file_post_date + "，文件所有者：" + obj.file_post_author + "&emsp;&emsp;点击这条信息开始下载（10s后消失)</a>", 10000, 1000, true);
                    // noinspection JSUnresolvedVariable
                    // var $info_banner = createAlertDiv("alert-info", "<a onclick='downloadFile()'>" + "文件名：" + obj.file_name + "，大小：" + obj.file_size + "，备注： " + obj.file_attach + "，上传时间：" + obj.file_post_date + "，文件所有者：" + obj.file_post_author + "&emsp;&emsp;点击这条信息开始下载（10s后消失)</a>", false, true);
                    // $(".middle-info-div").append($info_banner);
                    // $.session.set("bucketId",obj.file_bucket_id);
                    // $.session.set("filename",obj.file_name);


                    // $info_banner.show(1000);
                    // $info_banner.bind("click", function () {
                    //     // noinspection JSUnresolvedVariable
                    //     downloadFile(obj.file_bucket_id, obj.file_name);
                    //     // $info_banner.slideUp(1000);
                    // });
                    // setTimeout(function () {
                    //     $info_banner.slideUp(1000);
                    // }, 10000);
                    break;
            }
        }
    });
}


function rmFile(id, bucket_id, name, author) {
    // alert(author);
    // if (author === null || author !== $.session.get("un")) {
    //     parent.parent.showBannerInfo("alert-warning", 0, "您没有权限删除该文件", 4000, 1000, true);
    // } else {
    $.get({
        url: "rmFile",
        data: {
            file_id: id,
            bucket_id: bucket_id
        },
        dataType: "text",
        success: function (result) {
            if (result === "success") {
                parent.parent.showBannerInfo("alert-success", 0, "文件 " + name + " 删除成功", 4000, 1000, true);
                window.location.reload();
                parent.showAmountFlags();
            } else {
                parent.parent.showBannerInfo("alert-danger", 0, "文件 " + name + " 删除失败", 4000, 1000, true);
            }
        }
    });
    // }
}

function downloadFile(bucketId, filename) {
    // alert($.session.get("bucketId"));
    // alert($.session.get("filename"));
    $.get({
        url: "doDownload",
        data: {
            bucketId: bucketId,
            filename: filename
        },
        dataType: "text",
        cache: false,
        async: false
    });
}

function getCos() {
    var cos = null;
    $.post({
        url: "aaa",
        dataType: "text",
        cache: false,
        async: false,
        success: function (data) {
            //     alert(data.substring(0, data.indexOf(",")));
            //     alert(data.substring(data.indexOf(".") + 1));
            cos = new COS({
                SecretId: data.substring(0, data.indexOf(",")),
                SecretKey: data.substring(data.indexOf(".") + 1)
            });
        },
        error: function () {
            showBannerInfo("alert-danger", 0, "初始化错误", 3000, 1000, true);
        }
    });
    return cos;
}

