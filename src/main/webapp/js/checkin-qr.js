var id  = getParamDic()["id"];
var lat = getParamDic()["lat"];
var lon = getParamDic()["lon"];
var checkpoint = getCheckpoint(id);

$(function() {
	$("#btn-next").click(function() {
		location.href = getTaskURL(checkpoint) + "&lat=" + lat + "&lon=" + lon;
	});
});

function handleFiles(files) {
	if (files == null || files.length == 0 || files[0] == null) {
		alert("画像を取得できませんでした。");
		return;
	}
	var file = files[0];
	var fileReader = new FileReader();
	fileReader.onload = function(event) {
		$("#img-preview").attr('src', event.target.result);
		qrcode.decode(event.target.result);
		// decodeQR(event.target.result);
	};
	fileReader.readAsDataURL(file);
}

qrcode.callback = function(res) {
	$("#btn-next").prop("disabled", true);
	if (res == 'error decoding QR Code') {
    	alert('QRコードの解析に失敗');
	} else {
		console.log('Success to decode qr-code : ' + res);
		if (res == checkpoint.checkin.answerQr) {
			$("#btn-next").prop("disabled", false);
		} else {
			alert("誤ったQRコードを読み込んでいます。別のQRコードを探して下さい。");
		}
	}
};

function decodeQR(data) {
    var img = new Image();
    img.src = data;
    img.onload = function() {
    	var canvas = document.createElement("canvas");
    	var limitSize = 400;
    	var resizedWidth = img.width;
    	var resizedHeight = img.height;
    	if (resizedWidth > limitSize || resizedHeight > limitSize) {
    		var s;
    		if (resizedWidth > resizedHeight) {
    			s = limitSize / resizedWidth;
    		} else {
    			s = limitSize / resizedHeight;
    		}
    		resizedWidth *= s;
    		resizedHeight *= s;
    	}
    	canvas.width = limitSize;
    	canvas.height = limitSize;
    	if (canvas.style.width > canvas.style.height) {
    		canvas.style.width = resizedWidth;
    		canvas.style.height = resizedHeight;
    	} else {
    		canvas.style.width = resizedWidth;
    		canvas.style.height = resizedHeight;
    	}
    	var mpImg = new MegaPixImage(img);
    	mpImg.render(canvas, { width: canvas.width, height: canvas.height });
    	binarization(canvas, 110);
    	var resized_data = canvas.toDataURL("image/png");
    	qrcode.decode(resized_data);
    };
}

function binarization(canvas, blackBorder) {
	var ctx = canvas.getContext("2d");
	var src = ctx.getImageData(0, 0, canvas.width, canvas.height);
	var dst = ctx.createImageData(canvas.width, canvas.height);
	for (var i = 0; i < src.data.length; i += 4) {
		var v = src.data[i] + src.data[i+1] + src.data[i+2];
		var c;
		if (v <= blackBorder * 3) {
		c = 0;
		} else {
		c = 255;
		}
		dst.data[i] = c;
		dst.data[i+1] = c;
		dst.data[i+2] = c;
		dst.data[i+3] = src.data[i+3];
	}
	ctx.putImageData(dst, 0, 0);
}
