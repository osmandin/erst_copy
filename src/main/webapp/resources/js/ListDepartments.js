// $Id: ListDepartments.js,v 1.1 2016/02/29 22:17:31 ericholp Exp $

function validate() {
    try {

        var oneinvalid = 0;

        var patt1 = /^\s*$/;
        var patt2 = /^.{1,60}$/;
        if (patt1.test($("#departmentname").val()) || !patt2.test($("#departmentname").val())) {
            oneinvalid = 1;
            $("#departmentnameerr").show();
            $("#departmentname").css("border", "solid 2px red");
        } else {
            $("#departmentnameerr").hide();
            $("#departmentname").css("border", "");
        }

        if (oneinvalid) {
            $("#alert_box_top").show();
            $("#alert_box_bottom").show();
        } else {
            $("#form").submit();
        }
    } catch (ex) {
        alert("validate: " + ex);
    }
}
