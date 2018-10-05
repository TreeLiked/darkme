$(document).bind("ajaxError", function(xhr){
    doParentShowBanner("alert-danger", 0, "异常："+ xhr.status + ", " + xhr.statusText, 4000,1000,true);
});
function doParentShowBanner(alert_type, hide_type, str, show_time, hide_time, auto_hide) {

    parent.parent.showBannerInfo(alert_type, hide_type, str, show_time, hide_time, auto_hide)
}

function newMemo() {

    var $memo_title = $("#newMemoTitle");
    var $memo_content = $("#newMemoContent");
    var $memo_type = $("#newMemoType2");
    // alert($memo_title.val());

    if (($memo_title.val() !== null && $memo_title.val() !== "") || ($memo_content.val() !== null && $memo_content.val() !== "")) {
        $.post({
            url: "newMemo",
            dataType: "text",
            data: {
                u1: $memo_title.val(),
                u2: $memo_content.val(),
                u3: $memo_type.prop("checked")
            },
            cache: false,
            success: function (result) {
                if (result === "1") {
                    doParentShowBanner("alert-success", 0, "便签添加成功", 4000, 1000, true);
                } else {
                    doParentShowBanner("alert-warning", 0, "便签添加失败", 4000, 1000, true);
                }
                memo_nav_change(0);
                parent.showAmountFlags();
            }
        });
    }
}

function memo_nav_change(index) {

    var $memo_content_div = $(".memo-content-div");
    var $finished = $(".memo-nav-finished");
    var $unfinished = $(".memo-nav-unfinished");
    switch (index) {
        case 0:
            $unfinished.addClass("active");
            $finished.removeClass("active");
            $.get({

                url: "getMemo",
                data: {
                    memo_state: false
                },
                cache: false,
                success: function (result) {
                    // alert(data);

                    $memo_content_div.empty();
                    if (result !== "fail") {

                        // alert(result.length)
                        // var r = result.charAt(0);
                        // result = result.substring(1);
                        // if (r === "0") {
                        //     return;
                        // }
                        // if (r === "2") {
                        result = JSON.parse(result);
                        // }
                        $.each(result, function (index, obj) {
                            $memo_content_div.append(createSingleMemo(obj, false));

                        })
                    }

                }
            });
            break;
        case 1:
            $finished.addClass("active");
            $unfinished.removeClass("active");
            // alert("???");
            $.get({
                url: "getMemo",
                data: {
                    memo_state: true
                },
                cache: false,
                success: function (result) {
                    // alert("location.href+"+" .memo-content-div");
                    $memo_content_div.empty();
                    if (result !== "fail") {
                        // var r = result.charAt(0);
                        // result = result.substring(1);
                        // if (r === "0") {
                        //     return;
                        // }
                        // if (r === "2") {
                        result = JSON.parse(result);
                        // }
                        $.each(result, function (index, obj) {
                            // console.log(index);
                            // console.log(obj.memo_state);
                            // // noinspection JSUnresolvedVariable
                            // console.log(obj.memo_title);
                            // console.log(obj.memo_content);
                            $memo_content_div.append(createSingleMemo(obj, true));
                        })
                    }
                }
            });
            break;
        case 2:
            $("#newMemoTitle").focus();
            break;
        case 3:
            $("#search-memo-input").focus();
            break;
        default:
            break;
    }
}


function createSingleMemo(obj, isFinished) {


    // <div class="panel panel-warning" th:each="memo : ${#httpServletRequest.getAttribute('mMemoList')}" th:if="${memo.memo_type} == 0 and ${memo.memo_state} == 0">
    //
    //         <div class="panel-heading"><h3 class="panel-title" style="display: inline"  th:text="${memo.memo_title}">Memo title</h3>
    //     <button type="button" class="btn btn-default btn-sm pull-right" style="background-color: transparent">
    //         标记为完成<span class="glyphicon glyphicon-ok" aria-hidden="true"></span>
    //         </button></div>
    //     <div class="panel panel-body" th:text="${memo.memo_content}">
    //         Memo Detail
    //     </div>
    //     </div>

    var $divOut = $("<div>");
    $divOut.addClass("panel");
    if (!isFinished) {
        switch (obj.memo_type) {
            case 0:
                $divOut.addClass("panel-info");
                break;
            case 1:
                $divOut.addClass("panel-warning");
                break;

        }
    } else {
        $divOut.addClass("panel-success");
    }
    var $divIn1 = createDivIn1(obj, isFinished);
    var $divIn2 = createDivIn2(obj);
    $divOut.append($divIn1);
    $divOut.append($divIn2);
    return $divOut;
}

function createDivIn1(obj, isFinished) {
    var $divIn1 = $("<div>");
    $divIn1.addClass("panel-heading");

    var $h5 = $("<h5>");
    $h5.addClass("panel-title");
    $h5.css("display", "inline");
    $h5.text(obj.memo_title + "（ 创建于：" + obj.memo_post_date + " ）");
    $divIn1.append($h5);

    var $button_mod = $("<button>");
    var $button_del = $("<button>");
    //     <button type="button" class="btn btn-default btn-sm pull-right" style="background-color: transparent">
    //         标记为完成<span class="glyphicon glyphicon-ok" aria-hidden="true"></span>
    //         </button>
    $button_mod.attr("type", "button");
    $button_del.attr("type", "button");
    $button_mod.addClass("btn btn-default btn-sm pull-right");
    $button_del.addClass("btn btn-default btn-sm pull-right");
    $button_mod.css("background-color", "transparent");
    $button_del.css("background-color", "transparent");


    $button_del.html("删除<span class='glyphicon glyphicon-remove' aria-hidden='true'>" + "</span>");


    if (isFinished) {
        $button_mod.html("<span class='glyphicon glyphicon-chevron-left' aria-hidden='true'>" + "</span>取消完成");
        $button_mod.on("click", function () {
            mod_memo(obj.id, 0, false, 1);
        });
        $divIn1.append($button_del);
        $divIn1.append($button_mod);

        $button_del.on("click", function () {
            mod_memo(obj.id, -1, true, 1);
        });

    } else {
        $button_mod.html("标记完成<span class='glyphicon glyphicon-chevron-right' aria-hidden='true'>" + "</span>");
        $button_mod.on("click", function () {
            mod_memo(obj.id, 1, false, 0);
        });
        $divIn1.append($button_mod);
        $divIn1.append($button_del);


        $button_del.on("click", function () {
            mod_memo(obj.id, -1, true, 0);
        });
    }
    return $divIn1;
}

function createDivIn2(obj) {

    var $divIn2 = $("<div>");
    $divIn2.addClass("panel panel-body");
    $divIn2.html(obj.memo_content);
    return $divIn2;
}

function mod_memo(id, toState, isRemoved, index) {

    $.get({
        url: "modMyMemo",
        dataType: "text",
        data: {
            id: id,
            toState: toState,
            isRemoved: isRemoved
        },
        cache: false,
        success: function (result) {

            if (isRemoved) {
                if (result === "1") {
                    doParentShowBanner("alert-success", 1, "便签删除成功", 3000, 1000, true);
                    parent.showAmountFlags();

                    memo_nav_change(index);
                    // if (toState === 1) {
                    //     memo_nav_change(0);
                    // } else {
                    //     memo_nav_change(1);
                    // }
                } else {
                    doParentShowBanner("alert-danger", 1, "便签删除失败", 3000, 1000, true);
                }
            } else {
                if (result === "1") {
                    doParentShowBanner("alert-success", 1, "便签信息修改成功", 3000, 1000, true);
                    if (toState === 1) {
                        memo_nav_change(0);
                    } else {
                        memo_nav_change(1);
                    }
                } else {
                    doParentShowBanner("alert-danger", 1, "便签信息修改失败", 3000, 1000, true);
                }
            }
        }
    });
}

function search_memo() {
    var $search_input = $("#search-memo-input");
    if ($search_input.val() === null || $search_input.val() === ""){
        return;
    }
    var $memo_content_div = $(".memo-content-div");
    $memo_content_div.empty();
    $.get({
        url: "searchMemo",
        dataType: "text",
        data: {
            u1: $search_input.val()
        },
        cache: false,
        success: function (result) {
            if (result !== "0") {
                result = JSON.parse(result);
                $.each(result, function (index, obj) {
                    $memo_content_div.append(createSingleMemo(obj, true));
                });
                doParentShowBanner("alert-info", 0, "查询结束，共"+ result.length + "条记录。",3000,1000,true);
            } else {
                doParentShowBanner("alert-info", 0, "查询结束，无结果",3000,1000,true);
            }
        }
    });
}