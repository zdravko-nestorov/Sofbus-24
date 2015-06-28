$(document).ready(function() {

	// On change the type of the push notification
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
	
	// On click the send button (show the appropriate notification)
	var notificationStatus = '${notificationStatus}';
	var msgSuccess = document.getElementById('notif-msg-success'); 
	var msgFailed = document.getElementById('notif-msg-failed');
	
	switch (notificationStatus) {
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
	
	$("#reset").click(function() {
		setDefaultState();
		msgSuccess.setAttribute("style", "display:none");
		msgFailed.setAttribute("style", "display:none");
	});
	
	// Validations on over the send button
	var gcmDateInfo = "<spring:message code='gcm-send-message.date-info' javaScriptEscape='true' />";
	var gcmDateError = "<spring:message code='gcm-send-message.date-error' javaScriptEscape='true' />";
	var gcmTypeInfo = "<spring:message code='gcm-send-message.type-info' javaScriptEscape='true' />";
	var gcmTypeError = "<spring:message code='gcm-send-message.type-error' javaScriptEscape='true' />";
	var gcmDataInfo = "<spring:message code='gcm-send-message.data-info' javaScriptEscape='true' />";
	var gcmIdsInfo = "<spring:message code='gcm-send-message.ids-info' javaScriptEscape='true' />";
	var gcmDataError = "<spring:message code='gcm-send-message.data-error' javaScriptEscape='true' />";
	var gcmSuccess = "<spring:message code='gcm-send-message.success' javaScriptEscape='true' />";
	
	$("#submit").hover(function() {
		validate();
	}, function() {});
	
	// Set the form to the default state
	setDefaultState();
	
	// Actions over the input and hint fields
	function cssActions(inputId, hintText, returnState) {
		var hintId = "span_" + inputId;
		document.getElementById(hintId).innerHTML = hintText;
		
		$("#" + inputId).css("background-color", "");
		$("#" + hintId).show().removeClass("info").removeClass("error").removeClass("success");
		
		switch (returnState) {
		case 0:
			$("#" + hintId).addClass("info");
			break;
		case 1: 
			$("#" + inputId).css("background-color", "#F0FFE6");
			$("#" + hintId).addClass("success");
			break;
		case 2:
			$("#" + inputId).css("background-color", "#F3DDDD");
			$("#" + hintId).addClass("error");
			break;
		}
	}
	
	// Validation functions
	function checkIfEmpty(input) {
		if (input == null || input.trim().length == 0) {
			return false;
		}
		
		return true;
	}
	
	function checkIfTypeValid(input) {
		if (input == "NONE") {
			return false;
		}
		
		return true;
	}
	
	// Validate all fields one by one
	function validate() {
		var date = document.getElementById("gcmDate").value;
		var type = document.getElementById("gcmType").value;
		var data = document.getElementById("gcmData").value;
		var ids = document.getElementById("gcmIds").value;
		
		var dateCheck = checkIfEmpty(date);
		if (dateCheck) {
			cssActions("gcmDate", gcmSuccess, 1);	
		} else {
			cssActions("gcmDate", gcmDateError, 2);
		}
		
		var typeCheck = checkIfTypeValid(type);
		if (typeCheck) {
			cssActions("gcmType", gcmSuccess, 1);
		} else {
			cssActions("gcmType", gcmTypeError, 2);			
		}
		
		var dataCheck = checkIfEmpty(data);
		if (dataCheck) {
			cssActions("gcmData", gcmSuccess, 1);			
		} else {
			cssActions("gcmData", gcmDataError, 2);
		}
		
		cssActions("gcmIds", gcmSuccess, 1);
		
		if (dateCheck && typeCheck && dataCheck) {
			$("#submit").css("cursor", "pointer");
			$("#submit").unbind("click");
			$("#gcm-send-message").unbind("submit");
		} else {
			$("#submit").css("cursor", "not-allowed");
			$("#submit").click(function(event) {
			    event.preventDefault();
			});
		}
	}
	
	// Set the form in default state
	function setDefaultState() {
		cssActions("gcmDate", gcmDateInfo, 0);
		cssActions("gcmType", gcmTypeInfo, 0);
		cssActions("gcmData", gcmDataInfo, 0);
		cssActions("gcmIds", gcmIdsInfo, 0);
	}

})