$(function() {
  $('#login-btn').on('click', function() {
    for (var i = 0; i < $('input').size(); i++) {
      if (!$('input')[i].checkValidity()) {
        $('#submit-for-validation').trigger("click");
        return;
      }
    }
    var userId = $('#user-id').val();
    var groupId = $('#group-id').val();
    new JsonRpcClient(new JsonRpcRequest(getBaseUrl(), "login", [userId, groupId], function() {
      // userIdをセッション情報に保存
      setUserId(userId);
      location.href = "checkpoints.html";
    })).rpc();
  });
});
