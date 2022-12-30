$(function(){
    $("#topBtn").click(setTop);
    $("#wonderfulBtn").click(setWonderful);
    $("#deleteBtn").click(setDelete);
});

//btn表示当前点的按钮
function like(btn, entityType, entityId, entityUserId, postId) {
    $.post(
      CONTEXT_PATH + "/like",
        {"entityType":entityType,"entityId":entityId, "entityUserId":entityUserId, "postId":postId},
        function (data){
          data = $.parseJSON(data);
          if(data.code == 0){
              //通过btn可以得到下级标签，子节点
              //把子标签里的数据改成data返回的数据，也就是controller里面组装成的map（JSON字符串形式）
              $(btn).children("i").text(data.likeCount);
              $(btn).children("b").text(data.likeStatus==1?'已赞':'赞');
          }else {
              alert(data.msg);
          }
        }
    );
}

//置顶
function setTop(){
    $.post(
        CONTEXT_PATH + "/discuss/top",
        {"id":$("#postId").val()},
        function (data) {
            data = $.parseJSON(data);
            if (data.code == 0){
                $("#topBtn").attr("disabled","disabled")
            }else {
                alert(data.msg);
            }
        }
    );
}

//加精
function setWonderful(){
    $.post(
        CONTEXT_PATH + "/discuss/wonderful",
        {"id":$("#postId").val()},
        function (data) {
            data = $.parseJSON(data);
            if (data.code == 0){
                $("#wonderfulBtn").attr("disabled","disabled")
            }else {
                alert(data.msg);
            }
        }
    );
}

//删除
function setDelete(){
    $.post(
        CONTEXT_PATH + "/discuss/delete",
        {"id":$("#postId").val()},
        function (data) {
            data = $.parseJSON(data);
            if (data.code == 0){
                location.href = CONTEXT_PATH + "/index/page/1";
            }else {
                alert(data.msg);
            }
        }
    );
}