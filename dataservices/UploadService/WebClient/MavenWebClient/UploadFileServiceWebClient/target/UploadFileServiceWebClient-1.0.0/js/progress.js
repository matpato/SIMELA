$(document).ready(function() {
var options = {
        beforeSend : function() {
                $("#progressbox").show();
                // clear everything
                $("#progressbar").width('0%');
                $("#message").empty();
                //$("#percent").html("0%");
        },
        uploadProgress : function(event, position, total, percentComplete) {
        	    if (percentComplete > 99) percentComplete = 99;
                $("#progressbar").width(percentComplete + '%');
                $("#percent").html(percentComplete + '%');

                // change message text to red after 50%
                if (percentComplete > 50) {
                $("#message").html("<font color='maroon'>Files upload are in progress!</font>");
                $("#percent").html("<font color='white'>" + percentComplete + "%</font>"); 
                }
        },
        success : function() {
            $("#message").html("<font color='#4284b0'>Your files have been uploaded!</font>");
        },
        complete : function(response) {
            $("#progressbar").width('100%');
            $("#percent").html("<font color='white'>100%</font>");
        },
        error : function() {
        $("#message").html("<font color='maroon'>ERROR: unable to upload files!</font>");
        },
        resetForm : true
};
$("#form").ajaxForm(options);
});

