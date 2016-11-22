
// $Id: EditSsa.js,v 1.83 2016-11-15 11:35:27-04 ericholp Exp $

var numcontacts = 0;
var numcopyrights = 0;
var numrestrictions = 0;
var numformats = 0;

function loadfunc(){
    numcontacts = $("#contactcnt").val();
    numcopyrights = $("#copyrightcnt").val();
    numrestrictions = $("#restrictioncnt").val();
    numformats = $("#formatcnt").val();

    var approved=$("input[name=approved]:checked").val();
    if(approved){
	$("#optapproved").css("border", "1px solid black");
	$("#optlegendapproved").show();
	$("#optdraftspace").hide();
	$("#optdraft").css("border", "0 none");
	$("#optlegenddraft").hide();
    }else{
	$("#optdraft").css("border", "1px solid black");
	$("#optlegenddraft").show();
	$("#optdraftspace").show();
	$("#optapproved").css("border", "0 none");
	$("#optlegendapproved").hide();
    }
}

function print( ssaid ){
    try {
	var previp = getCookie("printerip");
	var ip = prompt("Please enter a printer IP and hit 'OK' to print.", previp);

	if(ip == null || ip == ""){
	    alert("Print cancelled.");
	    return;
	}
	
	var ippatt = /^[1-9][0-9]*\.[1-9][0-9]*\.[1-9][0-9]*\.[1-9][0-9]*$/;
	if(ippatt.test(ip)){
	    setCookie("printerip", ip, 365*10);
	    $.ajax({
		method: "POST",
		url: root + "PrintPdf",
		data: { ssaid: ssaid, ip: ip }
	    }).done(function( response ){
		if(!response.match(/.*good.*/)){
		    alert("Status: " + response);
		}else{
		    alert("Printing...");
		}
	    });
	}else{
	    alert("invalid IP: " + ip);
	}
    }catch(ex){
	alert("print: " + ex);
    }
}

function getCookie(cname) {
    var name = cname + "=";
    var ca = document.cookie.split(';');
    for(var i=0; i<ca.length; i++) {
        var c = ca[i];
        while (c.charAt(0)==' ') c = c.substring(1);
        if (c.indexOf(name) == 0) return c.substring(name.length,c.length);
    }
    return "";
} 

function setCookie(cname, cvalue, exdays) {
    var d = new Date();
    d.setTime(d.getTime() + (exdays*24*60*60*1000));
    var expires = "expires="+d.toUTCString();
    document.cookie = cname + "=" + cvalue + "; " + expires;
} 

function confirm_delete( n ) {
    return confirm( "Warning: You are about to permanently delete this Submission Agreement." );
}
