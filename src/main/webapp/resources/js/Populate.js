// $Id: Populate.js,v 1.13 2016-10-30 20:11:41-04 ericholp Exp $

function heredoc(f) {
    return f.toString().match(/\/\*\s*([\s\S]*?)\s*\*\//m)[1].replace(/(\/\*[\s\S]*?\*) \//g, '$1/');
}

var vars = {};

vars.contactblock = heredoc(function () {/*
 <div class="contact">

 <input type="hidden" name="${prefix}ssaContactsForms[0].idx" value="0"/>
 <input type="hidden" name="${prefix}ssaContactsForms[0].ssasForm.id" value="${ssaid}" />

 <div>
 <label for="ssacontactname">Name</label>
 <input type="text" class="wide" id="name" name="${prefix}ssaContactsForms[0].name"/>
 </div>

 <div>
 <label for="ssacontactphone">Phone Number</label>
 <input type="text" class="wide" id="phone" name="${prefix}ssaContactsForms[0].phone"/>
 </div>

 <div>
 <label for="ssacontactemail">Email</label>
 <input type="text" class="wide" id="email" name="${prefix}ssaContactsForms[0].email"/>
 </div>

 <div>
 <label for="ssacontactaddress">Campus Address</label>
 <input type="text" class="wide" id="address" name="${prefix}ssaContactsForms[0].address"/>
 </div>

 <div>
 <input type="button" value="-" onclick="removeit( this, 'contact' )"/>
 </div>

 </div>
 */
});

vars.copyrightblock = heredoc(function () {/*
 <div class="copyright">
 <input type="hidden" name="${prefix}ssaCopyrightsForms[0].idx" value="0"/>
 <input type="hidden" name="${prefix}ssaCopyrightsForms[0].ssasForm.id" value="${ssaid}" />
 <div>
 <span>
 <input type="text" id="copyright" name="${prefix}ssaCopyrightsForms[0].copyright"/>
 <span><input type="button" value="-" onclick="removeit( this, 'copyright' )"/></span>
 </span>
 </div>
 </div>
 */
});

vars.restrictionblock = heredoc(function () {/*
 <div class="restriction">
 <input type="hidden" name="${prefix}ssaAccessRestrictionsForms[0].idx" value="0"/>
 <input type="hidden" name="${prefix}ssaAccessRestrictionsForms[0].ssasForm.id" value="${ssaid}" />
 <div>
 <span>
 <input type="text" id="restriction" name="${prefix}ssaAccessRestrictionsForms[0].restriction"/>
 <span><input type="button" value="-" onclick="removeit( this, 'restriction' )"/></span>
 </span>
 </div>
 </div>
 */
});

vars.formatblock = heredoc(function () {/*
 <div class="format">
 <input type="hidden" name="ssaFormatTypesForms[0].idx" value="0"/>
 <input type="hidden" name="ssaFormatTypesForms[0].ssasForm.id" value="${ssaid}" />
 <div>
 <span>
 <input type="text" name="ssaFormatTypesForms[0].formattype"/>
 <span><input type="button" value="-" onclick="removeit( this, 'format' )"/></span>
 </span>
 </div>
 </div>
 */
});

function addblock(blockobj, blockid) {
    var ssaid = $("#ssaid").val();
    blockobj = blockobj.replace(/\$\{ssaid\}/g, ssaid);
    blockobj = blockobj.replace(/\$\{prefix\}/g, prefix);
    jQuery(blockobj, {}).appendTo($("#" + blockid));
}

function createit(button, thetype) {
    try {

        var cnt = parseInt($("#" + thetype + "cnt").val());

        if (cnt == 0) {
            cnt++;
        }

        cnt++;

        $("#" + thetype + "cnt").val(cnt);

        if (thetype == "contact") {
            numcontacts++;
        } else if (thetype == "copyright") {
            numcopyrights++;
        } else if (thetype == "restriction") {
            numrestrictions++;
        } else if (thetype == "format") {
            numformats++;
        }

        addblock(vars[thetype + "block"], thetype + "s");

    } catch (ex) {
        alert("createit:\n" + ex + "\n\n at line number=" + ex.lineNumber);
    }
}

function removeit(button, thetype) {
    try {
        if (thetype == "contact") {
            numcontacts--;
        } else if (thetype == "copyright") {
            numcopyrights--;
        } else if (thetype == "restriction") {
            numrestrictions--;
        } else if (thetype == "format") {
            numformats--;
        }

        var cnt = parseInt($("#" + thetype + "cnt").val());

        if (cnt > 0) {
            cnt--;
        }

        $("#" + thetype + "cnt").val(cnt);

        var field = button.parentNode.parentNode;
        var block = field.parentNode;
        block.removeChild(field);

        if (cnt == 0) {
            addblock(vars[thetype + "block"], thetype + "s");
        }

    } catch (ex) {
        alert("removeit:\n" + ex + "\n\n at line number=" + ex.lineNumber);
    }
}

function addback(targetindex, type, replacement) {
    try {
        var index = -1;

        $("div").each(function () {
            if ($(this).attr('class') == type) {
                var inputs = $(this).find("input");
                for (var i = 0; i < inputs.length; i++) {
                    var input = inputs[i];
                    if (input.type != "button") {
                        var elname = input.name;
                        var elvalue = input.value;

                        if (index == targetindex) {
                            if (elname.match(/idx/)) {
                                input.name = prefix + "ssa" + type + "Forms[" + index + "].idx";
                            } else if (elname.match(/Form.id/)) {
                                input.name = prefix + "ssa" + replacement + "Forms[" + index + "].ssasForm.id";
                            } else if (elname.match(/id/)) {
                                input.name = prefix + "ssa" + replacement + "Forms[" + index + "].id";
                            } else if (elname.match(/name/)) {
                                input.name = prefix + "ssa" + replacement + "Forms[" + index + "].name";
                            } else if (elname.match(/phone/)) {
                                input.name = prefix + "ssa" + replacement + "Forms[" + index + "].phone";
                            } else if (elname.match(/email/)) {
                                input.name = prefix + "ssa" + replacement + "Forms[" + index + "].email";
                            } else if (elname.match(/address/)) {
                                input.name = prefix + "ssa" + replacement + "Forms[" + index + "].address";
                            } else if (elname.match(/copyright/)) {
                                input.name = prefix + "ssa" + replacement + "Forms[" + index + "].copyright";
                            } else if (elname.match(/restriction/)) {
                                input.name = prefix + "ssa" + replacement + "Forms[" + index + "].restriction";
                            } else if (elname.match(/formattype/)) {
                                input.name = prefix + "ssa" + replacement + "Forms[" + index + "].formattype";
                            }
                        }
                    }
                }
            }
        });
    } catch (ex) {
        alert("addback: " + ex.lineNumber + ": " + ex);
    }
}

function blankoutnames(targetindex, type) {
    try {
        var index = -1;

        $("div").each(function () {
            if ($(this).attr('class') == type) {
                index++;
                var inputs = $(this).find("input");
                for (var i = 0; i < inputs.length; i++) {
                    var input = inputs[i];
                    if (input.type != "button") {
                        var elname = input.name;
                        var elvalue = input.value;

                        if (index == targetindex) {
                            if (elname.match(/idx/)) {
                                input.name = "idx";
                            } else if (elname.match(/ssasForm/)) {
                                input.name = "ssasFormid";
                            } else if (elname.match(/\.id$/)) {
                                input.name = "id";
                            } else if (elname.match(/name/)) {
                                input.name = "name";
                            } else if (elname.match(/phone/)) {
                                input.name = "phone";
                            } else if (elname.match(/email/)) {
                                input.name = "email";
                            } else if (elname.match(/address/)) {
                                input.name = "address";
                            } else if (elname.match(/copyright/)) {
                                input.name = "copyright";
                            } else if (elname.match(/restriction/)) {
                                input.name = "restriction";
                            } else if (elname.match(/formattype/)) {
                                input.name = "formattype";
                            }
                        }
                    }
                }
            }
        });
    } catch (ex) {
        alert("blankoutnames; " + ex.lineNumber + ": " + ex);
    }
}

function renumberBlankoutAndRename(type) {
    try {
        var index = -1;
        var blank = []; // for each index
        var addback = []; // for each index

        $("div").each(function () {
            if ($(this).attr('class') == type) {
                index++;
                blank[index] = 1;
                addback[index] = 0;

                var inputs = $(this).find("input");
                for (var i = 0; i < inputs.length; i++) {
                    var input = inputs[i];
                    if (input.type != "button") {
                        var elname = input.name;
                        var eltype = input.type;
                        var elvalue = input.value;

                        // renumber 0, 1, ..., x
                        var newinputname = elname.replace(/[0-9]+/, index);
                        //alert(newinputname + " " + elvalue);
                        input.name = newinputname;

                        if (eltype == "text") {
                            if (elname.match(/name/)) {
                                if (elname.match(/^name/)) {
                                    addback[index] = 1;
                                }
                                if (!elvalue.match(/^\s*$/)) {
                                    blank[index] = 0;
                                }
                            } else if (elname.match(/copyright/)) {
                                if (elname.match(/^copyright/)) {
                                    addback[index] = 1;
                                }
                                if (!elvalue.match(/^\s*$/)) {
                                    blank[index] = 0;
                                }
                            } else if (elname.match(/restriction/)) {
                                if (elname.match(/^restriction/)) {
                                    addback[index] = 1;
                                }
                                if (!elvalue.match(/^\s*$/) && elvalue != defaultrestriction) {
                                    blank[index] = 0;
                                }
                            } else if (elname.match(/formattype/)) {
                                if (elname.match(/^formattype/)) {
                                    addback[index] = 1;
                                }
                                if (!elvalue.match(/^\s*$/)) {
                                    blank[index] = 0;
                                }
                            } else if (elname.match(/phone/)) {
                                if (!elvalue.match(/^\s*$/)) {
                                    blank[index] = 0;
                                }
                            } else if (elname.match(/email/)) {
                                if (!elvalue.match(/^\s*$/)) {
                                    blank[index] = 0;
                                }
                            } else if (elname.match(/address/)) {
                                if (!elvalue.match(/^\s*$/)) {
                                    blank[index] = 0;
                                }
                            }
                        }
                    }
                }
            }
        });

        var replacement = "";
        if (type == "contact") {
            replacement = "Contacts";
        } else if (type == "copyright") {
            replacement = "Copyrights";
        } else if (type == "restriction") {
            replacement = "AccessRestrictions";
        } else if (type == "format") {
            replacement = "FormatTypes";
        }

        for (var i = 0; i < blank.length; i++) {
            if (blank[i]) {
                //alert("blank out " + type + " for index=" + i);
                blankoutnames(i, type);
            } else if (addback[i]) {
                //alert("add back " + type + " for index=" + i);
                addback(i, type, replacement);
            }
        }
    } catch (ex) {
        alert("renumberBlankoutAndRename: " + ex.lineNumber + ": " + ex);
    }
}
