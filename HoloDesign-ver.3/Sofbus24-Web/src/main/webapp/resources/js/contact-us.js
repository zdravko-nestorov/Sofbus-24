$(document).ready(function() {

	// Validations on over the send button
	$("#submit").hover(function() {
		validate();
	}, function() {});
	
	// Set the form to the default state
	setDefaultState();
	
	// Actions over the input and hint fields
	function cssActions(inputId, returnState) {
		switch (returnState) {
		case 0:
			$("#" + inputId).css("background-color", "");
			break;
		case 1: 
			$("#" + inputId).css("background-color", "#F0FFE6");
			break;
		case 2:
			$("#" + inputId).css("background-color", "#F3DDDD");
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
	
	function checkIfEmailValid(email) {
		if (!email.match("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")) {
			return false;
		}
		
		return true;
	}
	
	// Validate all fields one by one
	function validate() {
		var name = document.getElementById("name").value;
		var email = document.getElementById("email").value;
		var subject = document.getElementById("subject").value;
		var msg = document.getElementById("msg").value;
		
		var nameCheck = checkIfEmpty(name);
		cssActions("name", nameCheck ? 1 : 2);
		
		var emailCheck = checkIfEmailValid(email);
		cssActions("email", emailCheck ? 1 : 2);
		
		var subjectCheck = checkIfEmpty(subject);
		cssActions("subject", subjectCheck ? 1 : 2);
		
		var msgCheck = checkIfEmpty(msg);
		cssActions("msg", msgCheck ? 1 : 2);	
		
		if (nameCheck && emailCheck && subjectCheck && msgCheck) {
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
		cssActions("name", 0);
		cssActions("email", 0);
		cssActions("subject", 0);
		cssActions("msg", 0);
	}

})