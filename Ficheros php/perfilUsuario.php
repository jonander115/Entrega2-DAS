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
$accion = $_POST["accion"];

if ($accion == "select"){ #Hay que pedir los datos del usuario

	$resultado = mysqli_query($con, "SELECT Nombre, Apellidos, Email FROM Usuarios WHERE Usuario = '$usuario'");
	
	#Comprobar si se ha ejecutado correctamente
	if (!$resultado) {
		echo 'Ha ocurrido algún error: ' . mysqli_error($con);
	}
	
	
	#Si se ha devuelto algo, la consulta es correcta
	if (mysqli_num_rows($resultado) != 0){
		
		#Acceder al resultado
		$fila = mysqli_fetch_row($resultado);
		
		# Generar el array con los resultados con la forma Atributo - Valor
		$arrayresultados = array(
		'nombre' => $fila[0],
		'apellidos' => $fila[1],
		'email' => $fila[2],
		);
		
		#Devolver el resultado
		echo json_encode($arrayresultados);
	
	}
}

else if ($accion == "update"){ #Hay que modificar los datos del usuario

	#Resto de parámetros necesarios que ha recibido el php
	$nombre = $_POST["nombre"];
	$apellidos = $_POST["apellidos"];
	$email = $_POST["email"];
	
	$resultado = mysqli_query($con, "UPDATE Usuarios SET Nombre='$nombre', Apellidos='$apellidos', Email='$email' WHERE Usuario = '$usuario'");

	#Comprobar si se ha ejecutado correctamente
	if ($resultado == true) {
		$devolver = 'usuarioModificado';
		echo $devolver;
	}
	
}

#Cerramos la conexión con la base de datos
mysqli_close($con);


?>