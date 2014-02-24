<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link rel="stylesheet" media="screen" href="css/bootstrap.css">
<link rel="stylesheet" media="screen" href="css/styles.css">

<title>Client</title>
</head>
<body>

<h1 align="center">Initialize</h1>

<form method="POST" action="Initialize.do">
<center><input class="btn btn-danger" type="submit" value="Initialize"></center>
<br><br>

</form>
<hr>

<h1 align="center">Add</h1>

<form method="POST" action="InputKeyVal.do">

<table>
	<tr>
		<td><label>Key</label></td>
		<td><input name="key" type="text"></td>
	</tr>
	
	<tr>
		<td><label>Value</label></td>
		<td><input name="value" type="text"></td>
	</tr>
	
	<tr>
		<td></td>
		<td><input type="submit" value="submit" class="btn btn-danger"></td>
	</tr>
</table>

</form>

<hr>
<h1 align="center">Retrieve</h1>


<form method="POST" action="PullReq.do">

<table>
    <tr>
        <td><label>Key</label></td>
        <td><input name="key" type="text"></td>
    </tr>
    
    <tr>
        <td></td>
        <td><input type="submit" value="submit" class="btn btn-danger"></td>
    </tr>
</table>

</form>
<div>

	

</div>
</body>
</html>