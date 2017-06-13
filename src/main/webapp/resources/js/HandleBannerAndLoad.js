// $Id: HandleBannerAndLoad.js,v 1.2 2016-11-15 12:08:47-04 ericholp Exp $

window.onload = function () {
    try {
        var d = new Date();
        var seconds = Math.round(d.getTime() / 1000);
        var bannernum = seconds % 6 + 1;
        $("#thebanner").removeClass("banner1").removeClass("banner2").removeClass("banner3").removeClass("banner4").removeClass("banner5").removeClass("banner6").addClass("banner" + bannernum);
        if (typeof loadfunc !== 'undefined' && $.isFunction(loadfunc)) {
            loadfunc();
        }
    } catch (ex) {
        alert("HandleBannerAndLoad: " + ex);
    }
};
