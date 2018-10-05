function byte_to_KMG(bytes) {


    var str;
    var g2 = bytes / Math.pow(1024, 3);
    var g2_ = Math.floor(g2);
    if (g2_ > 0) {
        g2 = g2.toFixed(2);
        str = g2 + " GB"
    } else {
        var m2 = bytes / Math.pow(1024, 2);
        var m2_ = Math.floor(m2);
        if (m2_ > 0) {
            m2 = m2.toFixed(2);
            str = m2 + " MB";
        } else {
            var k2 = (bytes / 1024).toFixed(2);
            str = k2 + " KB";
        }
    }
    return str;
}


function isEmpty(arg) {

    return arg === undefined && arg == null && arg === "";
}