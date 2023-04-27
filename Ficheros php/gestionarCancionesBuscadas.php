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
$nombreCancion = $_POST["nombreCancion"];

#Recoger información de las canciones que se llaman igual que lo introducido en la barra de búsqueda
$resultado = mysqli_query($con, "SELECT Nombre, Autor FROM Canciones WHERE Nombre = '$nombreCancion'");

#Comprobamos si se ha ejecutado correctamente
if (!$resultado) {
	echo 'Ha ocurrido algún error: ' . mysqli_error($con);
}

$arrayresultados = array();

#Recorrer el resultado
if (mysqli_num_rows($resultado) != 0){
	$cont = 0;
	while ($fila = mysqli_fetch_row($resultado)){
		$arrayresultados[$cont] = array(
		'NombreCancion' => $fila[0],
		'AutorCancion' => $fila[1]
		);
		$cont++;
	}
}

#Devolver el resultado
echo json_encode($arrayresultados);


#Cerramos la conexión con la base de datos
mysqli_close($con);

?>