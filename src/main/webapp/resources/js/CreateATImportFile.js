
// $Id: CreateATImportFile.js,v 1.1 2016/02/29 22:15:30 ericholp Exp $

function doit() {
    var v = document.getElementById("accession").value;
    
    if(v == '' ){
	alert("Accession Number field should not be blank.");
    }else{
	var res = v.split(".");
	if(
	    res.length === undefined ||
		res.length != 3 ||
		res[0] == '' ||
		res[0] === undefined ||
		res[1] == '' ||
		res[1] === undefined ||
		res[2] == ''||
		res[2] === undefined
	){
	    alert("Assession Number must have the format: x.y.z");
	}else{
	    $("#form").submit();	    
	}
    }
}
