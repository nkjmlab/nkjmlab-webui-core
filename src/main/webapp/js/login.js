$(function() {
	$('#login-btn').on(
			'click',
			function() {
				for (var i = 0; i < $('input').size(); i++) {
					if (!$('input')[i].checkValidity()) {
						$('#submit-for-validation').trigger("click");
						return;
					}
				}
				var checkpointGroupId = parseUri(location).anchor;
				var userId = $('#user-id').val();
				var groupId = $('#group-id').val();
				new JsonRpcClient(new JsonRpcRequest(getBaseUrl(), "login", [
						checkpointGroupId, userId, groupId ], function() {
					// userIdをセッション情報に保存
					setUserId(userId);
					setCheckpointGroupId(checkpointGroupId);
					location.href = "checkpoints.html";
				})).rpc();
			});
});
