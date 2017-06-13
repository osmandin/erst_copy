// $Id: CreateSsa.js,v 1.17 2016-11-15 11:36:34-04 ericholp Exp $

var numcontacts = 0;
var numcopyrights = 0;
var numrestrictions = 0;
var numformats = 0;

function loadfunc() {
    numcontacts = $("#contactcnt").val();
    numcopyrights = $("#copyrightcnt").val();
    numrestrictions = $("#restrictioncnt").val();
    numformats = $("#formatcnt").val();

    var approved = $("input[name=approved]:checked").val();
    if (approved == "1") {
        $("#optapproved").css("border", "1px solid black");
        $("#optlegendapproved").show();
        $("#optdraftspace").hide();
        $("#optdraft").css("border", "0 none");
        $("#optlegenddraft").hide();
    } else {
        $("#optdraft").css("border", "1px solid black");
        $("#optlegenddraft").show();
        $("#optdraftspace").show();
        $("#optapproved").css("border", "0 none");
        $("#optlegendapproved").hide();
    }
}
