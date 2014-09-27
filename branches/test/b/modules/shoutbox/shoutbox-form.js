// $id $

 if (typeof(Drupal) == "undefined" || !Drupal.shoutbox) {    
    Drupal.shoutbox = {};
 }

/*
 * Submit shout with javascript.
 *
 */

Drupal.shoutbox.attachShoutAddForm = function () {
		// initial color to use for first post
		// tell server what color to use
	$("input[@name='nextcolor']").val(Drupal.shoutbox.color);	
	var options = {
	resetForm: true,
	beforeSubmit: Drupal.shoutbox.validate,
	success: Drupal.shoutbox.success
    };

    $("#shoutbox-add-form").ajaxForm(options);
}

/**
  * Display response text and update the color
  * field. Remove top message if we are over 
  * the max count. 
  */

Drupal.shoutbox.success = function (responseText) {
	
    if(Drupal.shoutbox.shownAmount >= Drupal.shoutbox.showAmount) {
		$('#shoutbox-posts').children().eq(0).remove();		
	}
	else {
		Drupal.shoutbox.shownAmount += 1;		
	}
		//update color
	Drupal.shoutbox.color = (Drupal.shoutbox.color) ? 0 : 1;
	if(Drupal.shoutbox.ascending) {
		$('#shoutbox-posts').prepend(responseText);
	}
	else {		
		$('#shoutbox-posts').append(responseText);
	}
	
		// tell server what color to use
	$("input[@name='nextcolor']").val(Drupal.shoutbox.color);		
}

/**
  * Attach focus handling code to the form
  * fields 
  *
  */ 
Drupal.shoutbox.attachForm = function() {
    $('input#edit-nick').focus(
	function() {
	    if( $(this).val() == Drupal.shoutbox.defaultNick) {
		$(this).val("");
	    }
	}
    );
    $('input#edit-message').focus(
	function() {
	    if( $(this).val() == Drupal.shoutbox.defaultMsg) {
		$(this).val("");
	    }
	}
    );
    $('input#edit-url').focus(
	function() {
	    if( $(this).val() == Drupal.shoutbox.defaultUrl) {
		$(this).val("");
	    }
	}
    );
}

/**
 * Creates a timer that triggers every delay seconds.
 */
Drupal.shoutbox.startTimer = function(delay) {
	Drupal.shoutbox.interval = setInterval("Drupal.shoutbox.loadShouts()",delay);	
}

/**
 * Reloads all shouts from the server.
 */
Drupal.shoutbox.loadShouts = function() {
	$("#shoutbox-posts").load("/shoutbox/js/view");
}

/**
 * Validate input before submitting.
 * Don't accept default values or empty strings.
 */

	Drupal.shoutbox.validate = function (formData, jqForm, options) {
		var form = jqForm[0];		
		if ((form.nick.value == Drupal.shoutbox.defaultNick) ||
			(!form.nick.value)) {
			alert('Enter a valid Name/Nick');
			return false;		
		}
		if ((!form.message.value) ||
			(form.message.value == Drupal.shoutbox.defaultMsg)) {
			alert('Enter a valid Message');
			return false;		
		}
			// tell server we are using ajax
		for (var i=0; i < formData.length; i++) { 
			if (formData[i].name == 'ajax') { 
				formData[i].value = 1;      
			}
		}
		return true;	
}
	
		
	
	
if (Drupal.jsEnabled) {
    $(document).ready(function() {
			Drupal.shoutbox.shownAmount = shoutboxSettings['shownAmount'];
			Drupal.shoutbox.ascending = shoutboxSettings['ascending'];
			Drupal.shoutbox.showAmount = shoutboxSettings['showAmount'];
			Drupal.shoutbox.color = (shoutboxSettings['shownAmount'] + 1)%2;
			Drupal.shoutbox.defaultNick = (shoutboxSettings['defaultNick']);
			Drupal.shoutbox.defaultMsg = (shoutboxSettings['defaultMsg']);
			Drupal.shoutbox.defaultUrl = (shoutboxSettings['defaultUrl']);
			Drupal.shoutbox.attachForm();
			Drupal.shoutbox.attachShoutAddForm();
			if( shoutboxSettings['refreshDelay'] > 0) {
				Drupal.shoutbox.startTimer(shoutboxSettings['refreshDelay']);
			}	
		}
	);
};
