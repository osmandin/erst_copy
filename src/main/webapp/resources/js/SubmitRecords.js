// $Id: SubmitRecords.js,v 1.2 2016/03/02 15:53:36 ericholp Exp $

function validate() {
    var e = document.getElementById("ssaid");
    var selectedIndex = $("#ssaid option:selected").index();
    var ssaid = e.options[e.selectedIndex].value;

    if (ssaid == 0) {
        $("#alert_box_top").show();
    } else {
        $("#form").submit();
    }
}

