$(function() {

	$("li").removeClass("current");
	var url = document.URL;

	if (url.indexOf("index") != -1) {
		$("#home").addClass("current");
	}

	if (url.indexOf("gcm/send") != -1) {
		$("#gcm_push_notifications").addClass("current");
	}

	if (url.indexOf("gcm/users") != -1) {
		$("#gcm_push_notifications").addClass("current");
	}

	if (url.indexOf("about") != -1) {
		$("#about").addClass("current");
	}

	if (url.indexOf("contact-us") != -1) {
		$("#contact_us").addClass("current");
	}

	if (url.indexOf("logout") != -1) {
		$("#logout").addClass("current");
	}

});