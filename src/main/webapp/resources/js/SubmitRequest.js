// $Id: SubmitRequest.js,v 1.21 2016-11-15 11:38:57-04 ericholp Exp $

function loadfunc() {
    try {
        //$("#cap").attr("data-sitekey", publickey);
        $("#captcha1").attr("data-sitekey", publickey);
        $("#captcha2").attr("data-sitekey", publickey);

        $.getScript("https://www.google.com/recaptcha/api.js")
            .done(function (script, textStatus) {
                //alert( textStatus );
            })
            .fail(function (jqxhr, settings, exception) {
                alert("Triggered ajaxError handler.");
            });
    } catch (ex) {
        alert("setkey: " + ex);
    }
}

function validateforsave() {
    $("#form").attr("action", root + "VerifySave");
    $("#form").submit();
    $("#form").attr("action", root + "SubmitRequest");

    $("#container").hide();
    $("#captcha1").show();
    $("#captcha1").addClass("g-recaptcha")

    $.getScript("https://www.google.com/recaptcha/api.js")
        .done(function (script, textStatus) {
            //alert( textStatus );
        })
        .fail(function (jqxhr, settings, exception) {
            alert("Triggered ajaxError handler.");
        });
}

function createpdf() {
    try {
        $("#container").css("display", "block");

        $("#captcha1").removeClass("g-recaptcha");
        $("#captcha1").hide();

        $.getScript("https://www.google.com/recaptcha/api.js")
            .done(function (script, textStatus) {
                //alert( textStatus );
            })
            .fail(function (jqxhr, settings, exception) {
                alert("Triggered ajaxError handler.");
            });
    } catch (ex) {
        alert("createpdf: " + ex);
    }
    return false;
}

function cancelcaptcha() {
    try {
        $("#container").hide();
        $("#captcha1").show();
        $("#captcha1").addClass("g-recaptcha")

        $.getScript("https://www.google.com/recaptcha/api.js")
            .done(function (script, textStatus) {
                //alert( textStatus );
            })
            .fail(function (jqxhr, settings, exception) {
                alert("Triggered ajaxError handler.");
            });
    } catch (ex) {
        alert("cancel: " + ex);
    }
}

function validate() {
    try {
        var oneinvalid = 0;

        var patt1 = /^\s*$/;
        var patt2 = /^.{1,60}$/;
        if (patt1.test($("#department").val()) || !patt2.test($("#department").val())) {
            oneinvalid = 1;
            $('#departerr').show();
        } else {
            $('#departerr').hide();
        }

        if (patt1.test($("#address").val()) || !patt2.test($("#address").val())) {
            oneinvalid = 1;
            $('#addresserr').show();
        } else {
            $('#addresserr').hide();
        }

        var namepatt = /^[A-Za-z.-]{1,50}\s+[A-Za-z.-]{0,50}[A-Za-z.-\s]{1,50}$/;
        //var namepatt = /^[A-Za-z.-]{1,50}[A-Za-z.-\s]{1,50}$/;
        if (!namepatt.test($("#name").val())) {
            oneinvalid = 1;
            $('#nameerr').show();
        } else {
            $('#nameerr').hide();
        }

        var empatt = /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,4}$/;
        if (!empatt.test($("#email").val())) {
            oneinvalid = 1;
            $('#emailerr').show();
        } else {
            $('#emailerr').hide();
        }

        var phpatt = /^[0-9()-]{7,13}$/;
        if (!phpatt.test($("#phone").val())) {
            oneinvalid = 1;
            $('#phoneerr').show();
        } else {
            $('#phoneerr').hide();
        }

        if (patt1.test($("#departmenthead").val()) || !patt2.test($("#departmenthead").val())) {
            oneinvalid = 1;
            $('#departmentheaderr').show();
        } else {
            $('#departmentheaderr').hide();
        }

        if (patt1.test($("#signature").val()) || !patt2.test($("#signature").val())) {
            oneinvalid = 1;
            $('#signatureerr').show();
        } else {
            $('#signatureerr').hide();
        }

        if (oneinvalid) {
            $('#alert_box_top').show();
            $('#alert_box_bottom').show();
        } else {
            $("#form").submit();
        }
    } catch (ex) {
        alert("validate: " + ex);
    }
}
