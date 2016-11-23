
// $Id: ValidateRsa.js,v 1.6 2016-11-10 16:07:07-04 ericholp Exp $

function validate( submit = 1 ){
    try {
	var approved=$("input[name=approved]:checked").val();
	
	var oneinvalid=0;

	var yearpatt = /^[1-9][0-9][0-9][0-9]$/;
	if(!yearpatt.test($("#startyear").val())){
	    oneinvalid=1;
	    $("#startyearerr").show();
	    $("#startyear").css("border", "solid 2px red");
	}else{
	    $("#startyearerr").hide();
	    $("#startyear").css("border", "");
	}

	if(!yearpatt.test($("#endyear").val())){
	    oneinvalid=1;
	    $("#endyearerr").show();
	    $("#endyear").css("border", "solid 2px red");
	}else{
	    $("#endyearerr").hide();
	    $("#endyear").css("border", "");
	}
	
	if($("#creationdateyear").val() == "noselect"){
	    oneinvalid=1;
	    $("#creationdateyearerr").show();
	    $("#creationdateyear").css("border", "solid 2px red");
	}else{
	    $("#creationdateyearerr").hide();
	    $("#creationdateyear").css("border", "");
	}
	if($("#creationdatemonth").val() == "noselect"){
	    oneinvalid=1;
	    $("#creationdatemontherr").show();
	    $("#creationdatemonth").css("border", "solid 2px red");
	}else{
	    $("#creationdatemontherr").hide();
	    $("#creationdatemonth").css("border", "");
	}
	if($("#creationdateday").val() == "noselect"){
	    oneinvalid=1;
	    $("#creationdatedayerr").show();
	    $("#creationdateday").css("border", "solid 2px red");
	}else{
	    $("#creationdatedayerr").hide();
	    $("#creationdateday").css("border", "");
	}

	var patt1 = /^\s*$/;
	var patt2 = /^.{1,60}$/;
	if(patt1.test($("#departmenthead").val()) || !patt2.test($("#departmenthead").val())){
	    oneinvalid=1;
	    $("#departmentheaderr").show();
	    $("#departmenthead").css("border", "solid 2px red");
	}else{
	    $("#departmentheaderr").hide();
	    $("#departmenthead").css("border", "");
	}

	if(patt1.test($("#recordid").val()) || !patt2.test($("#recordid").val())){
	    oneinvalid=1;
	    $("#recordiderr").show();
	    $("#recordid").css("border", "solid 2px red");
	}else{
	    $("#recordiderr").hide();
	    $("#recordid").css("border", "");
	}

	
	if(patt1.test($("#recordstitle").val()) || !patt2.test($("#recordstitle").val())){
	    oneinvalid=1;
	    $("#recordstitleerr").show();
	    $("#recordstitle").css("border", "solid 2px red");
	}else{
	    $("#recordstitleerr").hide();
	    $("#recordstitle").css("border", "");
	}


	if(patt1.test($("#extent").val()) || !patt2.test($("#extent").val())){
	    oneinvalid=1;
	    $("#extenterr").show();
	    $("#extent").css("border", "solid 2px red");
	}else{
	    $("#extenterr").hide();
	    $("#extent").css("border", "");
	}

	if($("#transferdateyear").val() == "noselect"){
	    oneinvalid=1;
	    $("#transferdateyearerr").show();
	    $("#transferdateyear").css("border", "solid 2px red");
	}else{
	    $("#transferdateyearerr").hide();
	    $("#transferdateyear").css("border", "");
	}
	if($("#transferdatemonth").val() == "noselect"){
	    oneinvalid=1;
	    $("#transferdatemontherr").show();
	    $("#transferdatemonth").css("border", "solid 2px red");
	}else{
	    $("#transferdatemontherr").hide();
	    $("#transferdatemonth").css("border", "");
	}
	if($("#transferdateday").val() == "noselect"){
	    oneinvalid=1;
	    $("#transferdatedayerr").show();
	    $("#transferdateday").css("border", "solid 2px red");
	}else{
	    $("#transferdatedayerr").hide();
	    $("#transferdateday").css("border", "");
	}

	if(patt1.test($("#retentionperiod").val()) || !patt2.test($("#retentionperiod").val())){
	    oneinvalid=1;
	    $("#retentionperioderr").show();
	    $("#retentionperiod").css("border", "solid 2px red");
	}else{
	    $("#retentionperioderr").hide();
	    $("#retentionperiod").css("border", "");
	}

	if(patt1.test($("#descriptionstandards").val()) || !patt2.test($("#descriptionstandards").val())){
	    oneinvalid=1;
	    $("#descriptionstandardserr").show();
	    $("#descriptionstandards").css("border", "solid 2px red");
	}else{
	    $("#descriptionstandardserr").hide();
	    $("#descriptionstandards").css("border", "");
	}
	
    	$("input[type=text]")
	    .each(function() {  // first pass, create name mapping
		var id = $(this).attr("id");
		var val = $(this).val();

		if(id == "email"){
		    var empatt = /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,4}$/;
		    if(!empatt.test(val)){
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
		    if(!phpatt.test(val) && !phpatt1.test(val) && !phpatt2.test(val)){
			oneinvalid=1;
			$("#phoneerr").show();
			$(this).css("border", "solid 2px red");
		    }else{
			$("#phoneerr").hide();
			$(this).css("border", "");
		    }
		}else if(id == "name"){
		    var namepatt = /^[A-Za-z.-]{1,50}\s+[A-Za-z.-]{0,50}[A-Za-z.-\s]{1,50}$/;
		    if(!namepatt.test(val)){
			oneinvalid=1;
			$("#nameerr").show();
			$(this).css("border", "solid 2px red");
		    }else{
			$("#nameerr").hide();
			$(this).css("border", "");
		    }
		}else if(id == "address"){
		    var patt1 = /^\s*$/;
		    var patt2 = /^.{1,60}$/;
		    if(patt1.test(val) || !patt2.test(val)){
			oneinvalid=1;
			$("#addresserr").show();
			$(this).css("border", "solid 2px red");
		    }else{
			$("#addresserr").hide();
			$(this).css("border", "");
		    }
		}else if(id == "copyright"){
		    var patt1 = /^\s*$/;
		    var patt2 = /^.{1,60}$/;
		    if(patt1.test(val) || !patt2.test(val)){
			oneinvalid=1;
			$("#copyrighterr").show();
			$(this).css("border", "solid 2px red");
		    }else{
			$("#copyrighterr").hide();
			$(this).css("border", "");
		    }
		}else if(id == "restriction"){
		    var patt1 = /^\s*$/;
		    var patt2 = /^.{1,60}$/;
		    if(patt1.test(val) || !patt2.test(val)){
			oneinvalid=1;
			$("#accessrestrictionserr").show();
			$(this).css("border", "solid 2px red");
		    }else{
			$("#accessrestrictionserr").hide();
			$(this).css("border", "");
		    }
		}
		
	    });

	var patt1 = /^\s*$/;
	var patt2 = /^.{1,60}$/;
	if(approved == "1" && (patt1.test($("#accessionnumber").val()) || !patt2.test($("#accessionnumber").val()))){
	    oneinvalid=1;
	    $("#accessionnumbererr").show();
	    $("#accessionnumber").css("border", "solid 2px red");
	}else{
	    $("#accessionnumbererr").hide();
	    $("#accessionnumber").css("border", "");
	}

	if(approved == "1" && $("#effectivedateyear").val() == "noselect"){
	    oneinvalid=1;
	    $("#effectivedateyearerr").show();
	    $("#effectivedateyear").css("border", "solid 2px red");
	}else{
	    $("#effectivedateyearerr").hide();
	    $("#effectivedateyear").css("border", "");
	}
	if(approved == "1" && $("#effectivedatemonth").val() == "noselect"){
	    oneinvalid=1;
	    $("#effectivedatemontherr").show();
	    $("#effectivedatemonth").css("border", "solid 2px red");
	}else{
	    $("#effectivedatemontherr").hide();
	    $("#effectivedatemonth").css("border", "");
	}
	if(approved == "1" && $("#effectivedateday").val() == "noselect"){
	    oneinvalid=1;
	    $("#effectivedatedayerr").show();
	    $("#effectivedateday").css("border", "solid 2px red");
	}else{
	    $("#effectivedatedayerr").hide();
	    $("#effectivedateday").css("border", "");
	}

    	if(oneinvalid){
	    $("#success_box").hide();
	    $("#alert_box_top").show();
	    $("#alert_box_bottom").show();
	    $("#success_created").hide();
	    $("#success_modified").hide();
	    $("#success_deleted").hide();
	    return 0;
	}else{
	    if(submit){
		renumberBlankoutAndRename("contact");
		renumberBlankoutAndRename("copyright");
		renumberBlankoutAndRename("restriction");
		//alert("submit");
		$("#form").submit();
	    }
	    return 1;
	}
    }catch(ex){
	alert("validate: Error: " + ex);
	return 0;
    }
}

function setfieldset(){
    try {
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
    }catch(ex){
	alert("setfieldset: " + ex);
    }
}

function approvedchange(){
    try {
	setfieldset();
	validate(0);
    }catch(ex){
	alert("approvedchange: " + ex);
    }
}


