function showAmountFlags() {

    // var un = Session.get("un");
    // var un = Cookies.get("un");
    // if (!isEmpty(un)) {
    //     alert(un);
    $.get({
        url: "countAll",
        data: "text",
        cache: false,
        success: function (result) {
            if (result !== "-1") {
                result = result.split(" ");
                $("#file_amount_flag").html(result[0]);
                $("#memo_amount_flag").html(result[1]);
                $("#msg_amount_flag").html(result[2]);
            }
        }
    });
    // }
}