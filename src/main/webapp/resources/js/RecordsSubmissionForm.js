
// $Id: RecordsSubmissionForm.js,v 1.1 2016/02/29 22:18:30 ericholp Exp $

function validate() {
    try {
	var oneinvalid=0;
	
	var patt = /^[0-9][0-9][0-9][0-9]$/;
	if(!patt.test($("#startyear").val())){
	    oneinvalid=1;
	    $("#alert_box_top").show();
	    $("#alert_box_bottom").show();
	    $("#startyear_error").show();
	    $("#startyear").css("border", "solid 2px red");
	}else{
	    $("#startyear_error").hide();
	    $("#startyear").css("border", "");
	}
	
	var endyear = $("#endyear").val();
	var patt2 = /^\s*$/;
	if(endyear != null && !patt2.test(endyear)){
	    if(!patt.test(endyear)){
		oneinvalid=1;
		$("#alert_box_top").show();
		$("#alert_box_bottom").show();
		$("#endyear_error").show();
		$("#endyear").css("border", "solid 2px red");
	    }else{
		$("#endyear_error").hide();
		$("#endyear").css("border", "");
	    }
	}else{
	    $("#endyear_error").hide();
	}
	
	if(!oneinvalid){
      	    $("#form").submit();	    
	}

    }catch(ex){
	alert("validate: " + ex);
    }
}
