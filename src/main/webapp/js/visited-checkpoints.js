$(function() {
	new JsonRpcClient(new JsonRpcRequest(getBaseUrl(), "getActivityLogs", [ getUserId() ], function(data) {
		showCheckpoints(data.result)
	})).rpc();
});

function showCheckpoints(activities) {
	activities.forEach(function(activity) {
		// TODO
		console.log(activity.taskId);
	});
}