$("#btnCancelRegister").click(function () {
    window.location.replace("index.html");
});

$("#btnCancelLogin").click(function () {
    window.location.replace("index.html");
});

$("#btnRegisterDone").click(function () {
    window.location.replace("index.html");
});

$("#btnLogin").click(function () {
    $('#msgLoginFailed').hide();
    $.post("/authenticate/" + $("#login").val() + "/" + $("#password").val(), function (data, status) {
        if (data == 'AUTHENTICATED') {
            window.location.replace("secured.html");
        } else if (data == "REQUIRE_TOKEN_CHECK") {
            $("#modalLoginCheckToken").modal('show');
        } else {
            $('#msgLoginFailed').show();
        }
    }).fail(function(){
        $('#msgLoginFailed').show();
    });
});

$("#btnRegister").click(function () {
    $.post("/usuario/cadastro/" + $("#login").val() + "/" + $("#password").val(), function (data, status) {
       if (status == 'success'&& data.length !== 0) {
            $("#tokenQr").attr("src", "https://zxing.org/w/chart?cht=qr&chs=250x250&chld=M&choe=UTF-8&chl="+ data.qrCodeUrl);
            $("#tokenValue").text(data.secretKey);
            $("#modalRegister").modal('show');
        } else{
        window.location.replace("index.html")
        }
    });
});

$("#btnTokenVerify").click(function () {
    $('#msgTokenCheckFailed').hide();
    $.post("/authenticate/token/" + $("#login").val() + "/" + $("#password").val() + "/" + $("#loginToken").val(), function (data, status) {
        if (data == 'AUTHENTICATED') {
            window.location.replace("secured.html");
        } else {
            $('#msgTokenCheckFailed').show();
        }
    }).fail(function(){
        $('#msgTokenCheckFailed').show();
    });
});

$("#btnLogout").click(function () {
    $.post("/authenticate/logout", function (data, status) {
        window.location.replace("index.html")
    });
});