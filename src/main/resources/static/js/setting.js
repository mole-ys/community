$(function (){
    //点击按钮触发表单提交事件时，由这个upload函数来处理
    $("#uploadForm").submit(upload);
});

function upload() {

    //processData: false 不要把表单内容转为字符串提交给服务器
    //contentType: false 不让jquery设置上传类型
    $.ajax({
        url: "http://upload.qiniup.com",
        method: "post",
        processData: false,
        contentType: false,
        data: new FormData($("#uploadForm")[0]),
        success: function (data) {
            if(data && data.code == 0){
                //更新头像访问路径
                $.post(
                    CONTEXT_PATH + "/user/header/url",
                    {"fileName":$("input[name='key']").val()},
                    function (data){
                        data = $.parseJSON(data);
                        if(data.code == 0){
                            window.location.reload();
                        }else {
                            alert(data.msg);
                        }
                    }
                );
            }else {
                alert("上传失败！")
            }
        }
    });

    //表示事件到此为止，不再提交表单
    return false;
}