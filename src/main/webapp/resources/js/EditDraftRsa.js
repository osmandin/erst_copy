
// $Id: EditDraftRsa.js,v 1.23 2016-11-15 11:37:32-04 ericholp Exp $

var numcontacts = 0;
var numcopyrights = 0;
var numrestrictions = 0;
var numformats = 0;

function loadfunc() {
    numcontacts = $("#contactcnt").val();
    numcopyrights = $("#copyrightcnt").val();
    numrestrictions = $("#restrictioncnt").val();
    numformats = $("#formatcnt").val();

    var approved=$("input[name=approved]:checked").val();
    if(approved == "1"){
	$("#optapproved").css("border", "1px solid black");
	$("#optlegendapproved").show();
	$("#optapprovedspace").show();
	$("#optdraftspace").hide();
	$("#optdraft").css("border", "0 none");
	$("#optlegenddraft").hide();
    }else{
	$("#optdraft").css("border", "1px solid black");
	$("#optlegenddraft").show();
	$("#optdraftspace").show();
	$("#optapproved").css("border", "0 none");
	$("#optlegendapproved").hide();
	$("#optapprovedspace").hide();
    }
}

function confirm_delete( n ) {
    return confirm( "Warning: You are about to permanently delete this Draft Transfer Request." );
}
