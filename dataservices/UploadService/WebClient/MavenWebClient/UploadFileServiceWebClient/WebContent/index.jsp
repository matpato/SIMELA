<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" lang="pt">

<head>
<meta http-equiv="Content-Type"
	content="text/html; charset=windows-1252">
<title>Sistema para monitorizar ALS</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<link rel="stylesheet" href="css/alsmon.css" type="text/css">
<script src="js/main.js"></script>
<script src="js/jquery.js"></script>
<script src="js/jquery.form.js"></script>
<script src="js/progress.js"></script>
</head>

<body>
	<div id="wrap">
		<div id="header">
			<h1 id="logo">
				ALS <span class="gray"> Mon</span>
			</h1>
			<h2 id="slogan">Upload Files...</h2>

			<ul>
				<li id="current"><a href="index.jsp"><span>Home</span></a></li>
				<li><a href="suporte.jsp"><span>Support</span></a></li>
				<li><a href="about.jsp"><span>About</span></a></li>
			</ul>

		</div>
		<div id="content-wrap">

			<div id="main">

				<form action="uploadFile" name="form" id="form"
					onsubmit="return validateForm()" method="POST"
					enctype="multipart/form-data">
					<br>
					<p>
						Please specify a file, or a set of files:<br> <input
							type="file" id="files" name="files" multiple size="62" />
						<output id="list"></output>
						<script>
							document.getElementById('files').addEventListener(
									'change', handleFileSelect, false);
						</script>
					</p>
					<div id="form_submit">
						<input type="submit" name="upload" id="upload" value="Upload">
						<input type="reset" name="reset" id="reset" value="Reset">
						<output id="submit"></output>
						<script>
							document.getElementById("upload").addEventListener(
									"click", handleSubmit);
							document.getElementById("reset").addEventListener(
									"click", handleReset);
						</script>
					</div>
					<div id="progressbox">
						<div id="progressbar">
						<div id="percent"></div></div>
					</div>
					<div id="message"></div>
					<br />
				</form>
				<br><br><br><br><br><br><br>
			</div>
			<div id="rightbar">
				<h1>Wise Words</h1>
				<p>"Life is a dream for the wise, a game for the fool, a comedy
					for the rich, a tragedy for the poor."</p>
				<p class="align-right">- Sholom Aleichem</p>
			</div>
		</div>
		<div id="footer">
			<div class="footer-left">
				<p class="align-left">
					<strong>© 2015 Sistema para monitorizar ALS </strong>
				</p>
			</div>

		</div>
	</div>


</body>
</html>


