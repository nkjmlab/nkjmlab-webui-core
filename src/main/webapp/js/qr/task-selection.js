var id = getParamDic()["id"];
var checkpoint = getCheckpoint(id);

$(function() {
	var task = checkpoint.task;
	$("#label").text(task.label);
	
	task.selections.forEach(function(selection, i) {
		var selectionElem =
			'<div class="selection">' + 
				'<label><input type="radio" name="selection" class="input-radio" value="' + i + '">' + selection + '</label>'
			'</div>';
		$(".form-group").append(selectionElem);
	});
	
	$("[name=selection]").click(function() {
		$("#btn-next").prop("disabled", false);
	});
	
	$("#btn-next").click(function() {
		var value = $("input[name=selection]:checked").val();
		if (task.answer_index == value) {
			alert("正解です。タスク完了！");
		} else {
			alert("不正解です。もう一度調査しなおして下さい。");
		}
	});
});
