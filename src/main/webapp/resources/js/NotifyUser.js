
// $Id: NotifyUser.js,v 1.2 2016-09-15 00:56:05-04 ericholp Exp $

function validate(){
    try {

	var oneinvalid=0;

	var patt1 = /^\s*$/;
	var patt2 = /^.{1,60}$/;
	if(patt1.test($("#username").val()) || !patt2.test($("#username").val())){
	    oneinvalid=1;
	    $("#usernameerr").show();
	    $("#username").css("border", "solid 2px red");
	}else{
	    $("#usernameerr").hide();
	    $("#username").css("border", "");
	}

  	var patt = /^[A-Za-z]+$/;
	if(!patt.test($("#firstname").val())){
	    oneinvalid=1;
	    $("#firstnameerr").show();
	    $("#firstname").css("border", "solid 2px red");
	}else{
	    $("#firstnameerr").hide();
	    $("#firstname").css("border", "");
	}

  	var patt = /^[A-Za-z]+$/;
	if(!patt.test($("#lastname").val())){
	    oneinvalid=1;
	    $("#lastnameerr").show();
	    $("#lastname").css("border", "solid 2px red");
	}else{
	    $("#lastnameerr").hide();
	    $("#lastname").css("border", "");
	}

	var empatt1 = /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,4}$/;
	var empatt2 = /^\"[A-Za-z\s\&]+\"\s*\<[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,4}\>$/;
	if(!empatt1.test($("#from").val()) && !empatt2.test($("#from").val())){
	    oneinvalid=1;
	    $("#fromemailerr").show();
	    $("#fromemail").css("border", "solid 2px red");
	}else{
	    $("#fromemailerr").hide();
	    $("#fromemail").css("border", "");
	}

	var empatt1 = /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,4}$/;
	var empatt2 = /^\"[A-Za-z\s\&]+\"\s*\<[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,4}\>$/;
	if(!empatt1.test($("#to").val()) && !empatt2.test($("#to").val())){
	    oneinvalid=1;
	    $("#toemailerr").show();
	    $("#toemail").css("border", "solid 2px red");
	}else{
	    $("#toemailerr").hide();
	    $("#toemail").css("border", "");
	}

	var patt1 = /^\s*$/;
	var patt2 = /^.{1,60}$/;
	if(patt1.test($("#subject").val()) || !patt2.test($("#subject").val())){
	    oneinvalid=1;
	    $("#emailsubjecterr").show();
	    $("#emailsubject").css("border", "solid 2px red");
	}else{
	    $("#emailsubjecterr").hide();
	    $("#emailsubject").css("border", "");
	}

	var patt1 = /^\s*$/;
	var patt2 = /^[\S\s]{1,2000}$/;
	if(patt1.test($("#body").val()) || !patt2.test($("#body").val())){
	    oneinvalid=1;
	    $("#emailtexterr").show();
	    $("#emailtext").css("border", "solid 2px red");
	}else{
	    $("#emailtexterr").hide();
	    $("#emailtext").css("border", "");
	}

	if(oneinvalid){
	    $("#alert_box_top").show();
	    $("#alert_box_bottom").show();
	}else{
	    $("#form").submit();
	}
    }catch(ex){
	alert("validate: " + ex);
    }
}
