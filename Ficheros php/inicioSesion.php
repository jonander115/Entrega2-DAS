<?php

$DB_SERVER="localhost"; #la dirección del servidor
$DB_USER="Xjlopezdeahumad0"; #el usuario para esa base de datos
$DB_PASS="N9tVbYiq"; #la clave para ese usuario
$DB_DATABASE="Xjlopezdeahumad0_BaseDeDatos"; #la base de datos a la que hay que conectarse

#Se establece la conexión:
$con = mysqli_connect($DB_SERVER, $DB_USER, $DB_PASS, $DB_DATABASE);

#Comprobamos la conexión:
if (mysqli_connect_errno()) {
	echo 'Error de conexion: ' . mysqli_connect_error();
	exit();
}

#Parámetros a utilizar
$usuario = $_POST["usuario"];
$password = $_POST["password"];

#Vemos si el usuario existe
$resultado = mysqli_query($con, "SELECT Usuario, Password FROM Usuarios WHERE Usuario = '$usuario'");

#Comprobamos si se ha ejecutado correctamente
if (!$resultado) {
	echo 'Ha ocurrido algún error: ' . mysqli_error($con);
}


if (mysqli_num_rows($resultado) == 0){ #Si no se ha devuelto nada, es porque no existe un usuario con ese nombre de usuario
	$devolver = 'usuarioNoExiste';
}
else{ #Se han devuelto el usuario y su contraseña

	#Accedemos al resultado
	$fila = mysqli_fetch_row($resultado);

	#Hay que comprobar que la contraseña introducida sea igual a la registrada en la base de datos
	#Obtenemos el hash de la contraseña, ya que eso es lo que está guardado en la base de datos.
	#Utilizamos la siguiente función para ello: https://www.php.net/manual/es/function.password-verify.php
	
	if (password_verify($password,$fila[1])) {
		$devolver = 'iniciarSesion';
	}
	else{
		$devolver = 'passwordIncorrecto';
	}
	
}


#Se devuelve un String que indica si se ha podido hacer el inicio de sesión o no, junto con su circunstancia
echo $devolver;

#Cerramos la conexión con la base de datos
mysqli_close($con);


?>