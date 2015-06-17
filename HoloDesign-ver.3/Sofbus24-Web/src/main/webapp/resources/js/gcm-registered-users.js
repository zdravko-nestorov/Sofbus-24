$(function() {
	/* For zebra striping */
	$("table tr:nth-child(odd)").addClass("odd-row");
	
	/* For cell text alignment */
	$("table td:first-child, table th:first-child").addClass("first");
});