$(pageInit);
function pageInit() {
    $('#txtContent').xheditor({

        upLinkUrl: "~/upload.aspx", upLinkExt: "zip,rar,txt",
        upImgUrl: "upload.aspx", upImgExt: "jpg,jpeg,gif,png",
        upFlashUrl: "~/upload.aspx", upFlashExt: "swf",
        upMediaUrl: "~/upload.aspx", upMediaExt: "wmv,avi,wma,mp3,mid",
        shortcuts: { 'ctrl+enter': submitForm }
    });
}
function insertUpload(arrMsg) {
    var i, msg;
    for (i = 0; i < arrMsg.length; i++) {
        msg = arrMsg[i];
        $("#uploaDList").append('<option value="' + msg.id + '">' + msg.localname + '</option>');
    }
}
function submitForm() { $('#form1').submit(); }