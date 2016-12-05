	function validateForm() {
		var x = document.forms["form"]["files"].value;
		if (x == null || x == "") {
			return false;
		}
	}
	function handleFileSelect(evt) {
		var files = evt.target.files; // FileList object
		// files is a FileList of File objects. List some properties.
		var output = [];
		handleReset();
		for (var i = 0, f; f = files[i]; i++) {
			output.push('<li><strong>', escape(f.name), '</strong> (', f.type
					|| 'n/a', ') - ', f.size, ' bytes, last modified: ',
					f.lastModifiedDate ? f.lastModifiedDate
							.toLocaleDateString() : 'n/a', '</li>');
		}
		document.getElementById('list').innerHTML = '<ul>' + output.join('')
				+ '</ul>';
	}
	function handleSubmit() {
		if (document.getElementById("files").value == "") {
			setTimeout(function() {
				document.getElementById("submit").innerHTML = "";
			}, 3000);
			document.getElementById("submit").innerHTML = "Select some files...";

		}
	}
	function handleReset() {
		document.getElementById('list').innerHTML = "";
		document.getElementById('progressbox').setAttribute("style","display:none");
		document.getElementById('progressbox').style.display='none';
		document.getElementById('progressbar').setAttribute("style","width:0px");
		document.getElementById('progressbar').style.width='0px';
		document.getElementById('message').innerHTML = "";
	}
	