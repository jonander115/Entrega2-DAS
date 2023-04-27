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
$nombre = $_POST["nombre"];
$apellidos = $_POST["apellidos"];
$email = $_POST["email"];

#Vemos si existe un usuario con ese nombre de usuario
$resultado = mysqli_query($con, "SELECT Usuario FROM Usuarios WHERE Usuario = '$usuario'");

#Comprobar si se ha ejecutado correctamente
if (!$resultado) {
	echo 'Ha ocurrido algún error: ' . mysqli_error($con);
}


if (mysqli_num_rows($resultado) == 0){ #Si no se ha devuelto nada, es porque no existe un usuario con ese nombre de usuario
	#Se puede registrar el usuario
	
	#Generamos el hash de la contraseña para almacenar eso en la base de datos. Utilizamos el algoritmo bcrypt
	#https://www.php.net/manual/es/function.password-hash.php
	$passwordHash = password_hash($password, PASSWORD_DEFAULT);
		
	#Registrar el usuario
	$resultado2 = mysqli_query($con, "INSERT INTO Usuarios VALUES ('$usuario','$passwordHash','$nombre','$apellidos','$email','')");
		
	#Comprobar si se ha ejecutado correctamente
	if (!$resultado2) {
		echo 'Ha ocurrido algún error: ' . mysqli_error($con);
	}
	else{
		$registro = 'usuarioNuevoRegistrado';
	}
		
}
else{ #Se ha devuelto el usuario. Significa que este nombre de usuario ya está registrado, y habría que elegir otro
	$registro = 'usuarioYaExiste';
}


#Se devuelve un String que indica si se ha podido hacer el registro o no, junto con su circunstancia
echo $registro;

#Cerramos la conexión con la base de datos
mysqli_close($con);


?>