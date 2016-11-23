
// $Id: ValidateSsa.js,v 1.13 2016-11-10 23:53:10-04 ericholp Exp $

function validate( submit = 1 ) {
    try {
	var approved=$("input[name=approved]:checked").val();

	var oneinvalid=0;
	
	if(!$("#departmentid").length){
	    oneinvalid=1;
	    $("#departmentmissingerr").show();
	    $("#departmentiderr").hide();
	    $("#departmentid").css("border", "");
	}else{
	    $("#departmentmissingerr").hide();
	    if($("#departmentid").val() == ""){
		oneinvalid=1;
		$("#departmentiderr").show();
		$("#departmentid").css("border", "solid 2px red");
	    }else{
		$("#departmentiderr").hide();
		$("#departmentid").css("border", "");
	    }
	}

	var patt1 = /^\s*$/;
	var patt2 = /^.{1,60}$/;

	if(patt1.test($("#recordstitle").val()) || !patt2.test($("#recordstitle").val())){
	    oneinvalid=1;
	    $("#recordstitleerr").show();
	    $("#recordstitle").css("border", "solid 2px red");
	}else{
	    $("#recordstitleerr").hide();
	    $("#recordstitle").css("border", "");
	}

	$("input[type=text]")
	    .each(function() {
		var id = $(this).attr("id");
		var val = $(this).val();
		if(id == "email"){
		    var empatt = /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,4}$/;
		    if(approved && !empatt.test(val)){
			oneinvalid=1;
			$("#emailerr").show();
			$(this).css("border", "solid 2px red");
		    }else{
			$("#emailerr").hide();
			$(this).css("border", "");
		    }
		}else if(id == "phone"){
		    var phpatt = /^[0-9()-]{7,13}$/;
		    var phpatt1 = /^[0-9]\-[0-9][0-9][0-9][0-9]$/;
		    var phpatt2 = /^[0-9][0-9][0-9][0-9][0-9]$/;
		    if(approved && !phpatt.test(val) && !phpatt1.test(val) && !phpatt2.test(val)){
			oneinvalid=1;
			$("#phoneerr").show();
			$(this).css("border", "solid 2px red");
		    }else{
			$("#phoneerr").hide();
			$(this).css("border", "");
		    }
		}else if(id == "name"){
		    var namepatt = /^[A-Za-z.-]{1,50}\s+[A-Za-z.-]{0,50}[A-Za-z.-\s]{1,50}$/;
		    if(approved && !namepatt.test(val)){
			oneinvalid=1;
			$("#nameerr").show();
			$(this).css("border", "solid 2px red");
		    }else{
			$("#nameerr").hide();
			$(this).css("border", "");
		    }
		}else if(id == "address"){
		    if(approved && (patt1.test(val) || !patt2.test(val))){
			oneinvalid=1;
			$("#addresserr").show();
			$(this).css("border", "solid 2px red");
		    }else{
			$("#addresserr").hide();
			$(this).css("border", "");
		    }
		}else if(id == "copyright"){
		    if(approved && (patt1.test(val) || !patt2.test(val))){
			oneinvalid=1;
			$("#copyrighterr").show();
			$(this).css("border", "solid 2px red");
		    }else{
			$("#copyrighterr").hide();
			$(this).css("border", "");
		    }
		}else if(id == "restriction"){
		    if(approved && (patt1.test(val) || !patt2.test(val))){
			oneinvalid=1;
			$("#accessrestrictionserr").show();
			$(this).css("border", "solid 2px red");
		    }else{
			$("#accessrestrictionserr").hide();
			$(this).css("border", "");
		    }
		}
	    });
	
	
	if(approved && (patt1.test($("#departmenthead").val()) || !patt2.test($("#departmenthead").val()))){
	    oneinvalid=1;
	    $("#departmentheaderr").show();
	    $("#departmenthead").css("border", "solid 2px red");
	}else{
	    $("#departmentheaderr").hide();
	    $("#departmenthead").css("border", "");
	}

	if(approved && (patt1.test($("#recordid").val()) || !patt2.test($("#recordid").val()))){
	    oneinvalid=1;
	    $("#recordiderr").show();
	    $("#recordid").css("border", "solid 2px red");
	}else{
	    $("#recordiderr").hide();
	    $("#recordid").css("border", "");
	}

	if(approved && $("#creationdateyear").val() == "noselect"){
	    oneinvalid=1;
	    $("#creationdateyearerr").show();
	    $("#creationdateyear").css("border", "solid 2px red");
	}else{
	    $("#creationdateyearerr").hide();
	    $("#creationdateyear").css("border", "");
	}
	if(approved && $("#creationdatemonth").val() == "noselect"){
	    oneinvalid=1;
	    $("#creationdatemontherr").show();
	    $("#creationdatemonth").css("border", "solid 2px red");
	}else{
	    $("#creationdatemontherr").hide();
	    $("#creationdatemonth").css("border", "");
	}
	if(approved && $("#creationdateday").val() == "noselect"){
	    oneinvalid=1;
	    $("#creationdatedayerr").show();
	    $("#creationdateday").css("border", "solid 2px red");
	}else{
	    $("#creationdatedayerr").hide();
	    $("#creationdateday").css("border", "");
	}

	if(approved && $("#effectivedateyear").val() == "noselect"){
	    oneinvalid=1;
	    $("#effectivedateyearerr").show();
	    $("#effectivedateyear").css("border", "solid 2px red");
	}else{
	    $("#effectivedateyearerr").hide();
	    $("#effectivedateyear").css("border", "");
	}
	if(approved && $("#effectivedatemonth").val() == "noselect"){
	    oneinvalid=1;
	    $("#effectivedatemontherr").show();
	    $("#effectivedatemonth").css("border", "solid 2px red");
	}else{
	    $("#effectivedatemontherr").hide();
	    $("#effectivedatemonth").css("border", "");
	}
	if(approved && $("#effectivedateday").val() == "noselect"){
	    oneinvalid=1;
	    $("#effectivedatedayerr").show();
	    $("#effectivedateday").css("border", "solid 2px red");
	}else{
	    $("#effectivedatedayerr").hide();
	    $("#effectivedateday").css("border", "");
	}

	if(approved && (patt1.test($("#retentionschedule").val()) || !patt2.test($("#retentionschedule").val()))){
	    oneinvalid=1;
	    $("#retentionscheduleerr").show();
	    $("#retentionschedule").css("border", "solid 2px red");
	}else{
	    $("#retentionscheduleerr").hide();
	    $("#retentionschedule").css("border", "");
	}

	if(approved && (patt1.test($("#retentionperiod").val()) || !patt2.test($("#retentionperiod").val()))){
	    oneinvalid=1;
	    $("#retentionperioderr").show();
	    $("#retentionperiod").css("border", "solid 2px red");
	}else{
	    $("#retentionperioderr").hide();
	    $("#retentionperiod").css("border", "");
	}

	if(approved && (patt1.test($("#descriptionstandards").val()) || !patt2.test($("#descriptionstandards").val()))){
	    oneinvalid=1;
	    $("#descriptionstandardserr").show();
	    $("#descriptionstandards").css("border", "solid 2px red");
	}else{
	    $("#descriptionstandardserr").hide();
	    $("#descriptionstandards").css("border", "");
	}

	$(".success_box").hide();
	
	if(oneinvalid){
	    $("#alert_box_top").show();
	    $("#alert_box_bottom").show();
	    $("#success_created").hide();
	    $("#success_modified").hide();
	    $("#success_deleted").hide();
	    return 0;
	}else{
	    if(submit == 1){
		renumberBlankoutAndRename("contact");
		renumberBlankoutAndRename("copyright");
		renumberBlankoutAndRename("restriction");
		renumberBlankoutAndRename("format");
		//alert("submit");
		$("#form").submit();
		return 1;
	    }
	    return 0;
	}
    }catch(ex){
	alert("validate: Error: " + ex.lineNumber + ": " + ex);
	return 0;
    }    
}

function approvedchange(){
    try {
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
	validate(0);
    }catch(ex){
	alert("approvedchange: " + ex.lineNumber + ": " + ex);
    }
}
