
// $Id: DepartmentDeleteWarning.js,v 1.1 2016/02/29 22:15:57 ericholp Exp $

function confirm_delete_users( form ) {
    var n = 0;
    var controls = form.elements;
    for ( var i = 0; i < controls.length; i++ ) {
    	var control = controls.item( i );
	if ( control.name == 'userids' && control.checked )
	    n++;
    }
    if ( n > 0 )
        return confirm( 'Warning: You are about to permanently delete ' + 
    	       		n + ' User' + ( n == 1 ? '' : 's' ) + '.' );
    return true;
}

function confirm_delete_ssas( form ) {
    var n = 0;
    var controls = form.elements;
    for ( var i = 0; i < controls.length; i++ ) {
    	var control = controls.item( i );
	if ( control.name == 'ssaids' && control.checked )
	    n++;
    }
    if ( n > 0 )
        return confirm( 'Warning: You are about to permanently delete ' + 
    	       		n + ' Submission Agreement' + ( n == 1 ? '' : 's' ) + '.' );
    return true;
}
