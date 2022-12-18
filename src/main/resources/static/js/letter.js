$(function(){
	$("#sendBtn").click(send_letter);
	$(".closemessage").click(delete_msg);
});

function send_letter() {
	$("#sendModal").modal("hide");

	var toName = $("#recipient-name").val();
	var content = $("#message-text").val();
	$.post(
		CONTEXT_PATH + "/letter/send",
		{"toName":toName,"content":content},
		function (data){
			data = $.parseJSON(data);
			if(data.code == 0){
				$("#hintBody").text("发送成功！");
			}else {
				$("#hintBody").text(data.msg);
			}

			$("#hintModal").modal("show");
			setTimeout(function(){
				$("#hintModal").modal("hide");
				//重载当前页面
				location.reload();
			}, 2000);
		}
	);
}

function delete_msg() {
	var letterId = $("#letter-id").val();
	console.log(letterId);
	if(confirm("是否确认删除？")){
		$.post(
			CONTEXT_PATH + "/letter/delete",
			{"letterId":letterId},
			function (data){
				data = $.parseJSON(data);
				if(data.code == 0){
					// TODO 删除数据
					$(btn).parents(".media").remove();
				}else {
					alert(data.msg);
				}
			}
		);
	}


}