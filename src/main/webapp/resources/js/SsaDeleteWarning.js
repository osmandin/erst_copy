// $Id: SsaDeleteWarning.js,v 1.1 2016/02/29 22:18:38 ericholp Exp $

function confirm_delete_rsas(form) {
    var n = 0;
    var controls = form.elements;
    for (var i = 0; i < controls.length; i++) {
        var control = controls.item(i);
        if (control.name == 'rsa' && control.checked)
            n++;
    }
    if (n > 0)
        return confirm('Warning: You are about to permanently delete ' +
            n + ' Records Transfer Request' + ( n == 1 ? '' : 's' ) + '.');
    return true;
}
