// $Id: ListSsas.js,v 1.5 2016-07-29 09:59:38-04 ericholp Exp $

function confirm_delete_ssas(form) {
    var n = 0;
    var controls = form.elements;
    for (var i = 0; i < controls.length; i++) {
        var control = controls.item(i);
        if (control.name == 'ssa' && control.checked)
            n++;
    }
    if (n == 0) {
        alert("Please select one or more Submission Agreements to delete");
        return false;
    } else {
        return confirm('Warning: You are about to permanently delete ' + n + ' Submission Agreements' + ( n == 1 ? '' : 's' ) + '.');
    }
    return true;
}
