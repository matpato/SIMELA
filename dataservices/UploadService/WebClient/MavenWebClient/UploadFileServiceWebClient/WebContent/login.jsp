<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" lang="pt">

<head>
<meta http-equiv="Content-Type"
	content="text/html; charset=windows-1252">
<title>Sistema para monitorizar ALS</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<link rel="stylesheet" href="css/alsmon.css" type="text/css">
</head>

<body>
	<div id="wrap">
		<div id="header">
			<h1 id="logo">
				ALS <span class="gray"> Mon</span>
			</h1>
			<h2 id="slogan">Hello, please log in:</h2>

			<ul>
				<li id="current"><a href="index.jsp"><span>Home</span></a></li>
				<li><a href="suporte.jsp"><span>Support</span></a></li>
				<li><a href="about.jsp"><span>About</span></a></li>
			</ul>

		</div>
		<div id="content-wrap">

			<div id="main">

   <form method="POST" action="j_security_check">
      <table border="0">
      <tr>
      <td>Username:</td>
      <td><input type="text" name="j_username" size="25"></td>
      </tr>
      <tr>
      <td>Password:</td>
      <td><input type="password" size="25" name="j_password"></td>
      </tr>
      </table>
      <input type="submit" value="Login">
       <input type="reset" value="Reset">
   </form>
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


