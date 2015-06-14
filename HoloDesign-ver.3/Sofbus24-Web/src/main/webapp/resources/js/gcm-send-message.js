$(document).ready(function() { 
	
	var sharedStatus = '${sharedStatus}';
	var msgSuccess = document.getElementById('notif-msg-success'); 
	var msgFailed = document.getElementById('notif-msg-failed');
	
	switch (sharedStatus) {
	case 'INIT':
		msgSuccess.setAttribute("style", "display:none");
		msgFailed.setAttribute("style", "display:none");
		break;
	case 'SUCCESS':
		msgSuccess.removeAttribute("style");
		msgFailed.setAttribute("style", "display:none");
		break;
	case 'FAILED':
		msgSuccess.setAttribute("style", "display:none");
		msgFailed.removeAttribute("style");
		break;
	}
	
	$("#gcmType").change(function() {

		var gcmType = document.getElementById("gcmType").value;
		var gcmData;
		switch (gcmType) {
		case "UPDATE_APP":
			gcmData = "0.0 (0)";
			break;
		case "UPDATE_DB":
			gcmData = "0";
			break;
		case "RATE_APP":
			gcmData = "Rate the application";
			break;
		case "INFO":
			gcmData = "{ \"bg\": \"\u0411\u044A\u043B\u0433\u0430\u0440\u0441\u043A\u0438\", \"en\": \"English\" }";
			break;
		default:
			gcmData = "";
			break;
		}

		document.getElementById("gcmData").value = gcmData;
	})
})