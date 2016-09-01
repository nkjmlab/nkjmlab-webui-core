var id = getParamDic()["id"];
var checkpoint = getCheckpoint(id);
document.title = checkpoint.name; // タイトルの変更
var cPos; // 現在地
var ePos; // チェックポイント
var map;
var watchID;
var compassElem;
var defaultOrientation;

$(function() {
	var nextBtnText = "",
		nextBtnHref = "";

	$("#activity-title").text(checkpoint.name+"でのアクティビティ");

	// チェックインorタスク
	switch (checkpoint.checkin.taskType) {
	case TaskType.Photo:
	case TaskType.QR:
		nextBtnText = "チェックイン";
		nextBtnHref = getCheckinURL(checkpoint);
		break;
	default:
		nextBtnText = "タスク";
		nextBtnHref = getTaskURL(checkpoint);
		break;
	}
	$("#btn-next").text(nextBtnText);
	$("#btn-next").click(function() {
		location.href = nextBtnHref + "&lat=" + cPos.lat() + "&lon=" + cPos.lng();
	});

	// コンパス画像の要素
	compassElem = $("#compass");
	// 端末の向きを取得
	defaultOrientation = (screen.width > screen.height) ? "landscape" : "portrait";
	// 電子コンパスイベントの取得
	window.addEventListener("deviceorientation", onHeadingChange);
	getEventsByWebsocket();

});

function getEventsByWebsocket() {
	var wsUrl = getActivityPublisherUrl()+"/"+getCheckpointGroupId()+"/"+checkpoint.id+"/"+getUserId();
	var connection = new WebSocket(wsUrl);
	connection.onmessage = function(e) {
		var messages = JSON.parse(e.data);
		for (var i = 0; i < messages.length; i++) {
			var a = messages[i];
			$('#notification').append($('<li>').text(toFormatedDate(a.created)+" "+a.userId+"さんがチェックインしました．"));
		}
	};
	return {
		abort : function() {
			connection.close();
		}
	};
}

function initMap() {
	var center = {lat: checkpoint.lat, lng: checkpoint.lon};
	map = new google.maps.Map(document.getElementById('map'), {
		center: center,
		zoom: 15
	});
	// マーカーの追加
	var marker = new google.maps.Marker({
	    position: center,
	    map: map,
	});

	// 目的地の設定&位置情報の連続取得
	ePos = new google.maps.LatLng(checkpoint.lat, checkpoint.lon);
	watchCurrentPosition();
}

/* 位置情報を連続取得する */
function watchCurrentPosition() {
	if (!navigator || !navigator.geolocation) {
		alert('GPSが使用できません');
	}
	watchID = window.navigator.geolocation.watchPosition(
		function(pos) {
			cPos = new google.maps.LatLng(pos.coords.latitude, pos.coords.longitude);
			console.log("currentPosition: " + pos.coords.latitude + ", " + pos.coords.longitude);
			showDistance();
		},
		function(error){
			alert('位置情報の取得に失敗しました');
			navigator.geolocation.clearWatch(watchID);
		},
		{
			enableHighAccuracy: true,
			timeout: 1000 * 60 * 60 * 2,
			maximumAge: 1000 * 60,
		}
	);
}

/* 残り距離を表示 */
function showDistance() {
	if (cPos == null || ePos == null) {
		return;
	}
	var distance = google.maps.geometry.spherical.computeDistanceBetween(cPos, ePos);
	$("#distance").text(getFormattedDistance(distance));
}

function getFormattedDistance(distance) {
	if (distance >= 1000 * 5) { // 5km以上
		return String(floatFormat(distance / 1000, 1)) + "km";
	} else {
		var distanceStr = String(Math.round(distance));
		if (distanceStr.length >= 4) {
			distanceStr = distanceStr.slice(0, 1) + "," + distanceStr.slice(1, distanceStr.length);
		}
		return distanceStr + "m";
	}
}

function getBrowserOrientation() {
	var orientation;
	if (screen.orientation && screen.orientation.type) {
		orientation = screen.orientation.type;
	} else {
		orientation = screen.orientation || screen.mozOrientation || screen.msOrientation;
	}
	/*
		'portait-primary':			for (screen width < screen height, e.g. phone, phablet, small tablet)
															device is in 'normal' orientation
														for (screen width > screen height, e.g. large tablet, laptop)
															device has been turned 90deg clockwise from normal

		'portait-secondary':		for (screen width < screen height)
															device has been turned 180deg from normal
														for (screen width > screen height)
															device has been turned 90deg anti-clockwise (or 270deg clockwise) from normal

		'landscape-primary':		for (screen width < screen height)
															device has been turned 90deg clockwise from normal
														for (screen width > screen height)
															device is in 'normal' orientation

		'landscape-secondary':	for (screen width < screen height)
															device has been turned 90deg anti-clockwise (or 270deg clockwise) from normal
														for (screen width > screen height)
															device has been turned 180deg from normal
	*/
	return orientation;
}

function onHeadingChange(event) {
	var cHeading = event.alpha;
	if (typeof event.webkitCompassHeading !== "undefined") {
		cHeading = event.webkitCompassHeading; //iOS non-standard
	}
	var orientation = getBrowserOrientation();
	if (typeof cHeading == "undefined" || cHeading == null) { // && typeof orientation !== "undefined") {
		alert('方向を検出できません');
	}

	// we have a browser that reports device cHeading and orientation
	// what adjustment we have to add to rotation to allow for current device orientation
	var adjustment = 0;
	if (defaultOrientation === "landscape") {
		adjustment -= 90;
	}
	if (typeof orientation !== "undefined") {
		var currentOrientation = orientation.split("-");
		if (defaultOrientation !== currentOrientation[0]) {
			if (defaultOrientation === "landscape") {
				adjustment -= 270;
			} else {
				adjustment -= 90;
			}
		}
		if (currentOrientation[1] === "secondary") {
			adjustment -= 180;
		}
	}

	cHeading += adjustment;
	var userAgent = window.navigator.userAgent.toLowerCase();
	if (userAgent.indexOf('android') >= 0) {
		cHeading = 360 - cHeading;
	}
	while(cHeading < 0) cHeading += 360;
	while(cHeading > 360) cHeading -= 360;

	showCompass(cHeading);
}

/* コンパスを回転 */
function showCompass(heading) {
	if (cPos == null || ePos == null) {
		return;
	}
	var absoluteAngle = google.maps.geometry.spherical.computeHeading(cPos, ePos);
	// apply rotation to compass
	if (compassElem.css("transform")) {
		compassElem.css("transform", 'rotate('+ (absoluteAngle - heading) +'deg)');
	} else if (compassElem.css("webkitTransform")) {
		compassElem.css("webkitTransform", 'rotate('+ (absoluteAngle - heading) +'deg)');
	}
}
