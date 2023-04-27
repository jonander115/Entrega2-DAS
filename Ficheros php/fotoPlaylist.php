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
$accion = $_POST["accion"];
$nombreUsuario = $_POST["nombreUsuario"];
$nombrePlaylist = $_POST["nombrePlaylist"];

if ($accion == "nuevaFoto"){ #Subir nueva foto al servidor

	$imagen = $_POST['imagen'];

	#Modificar campo Imagen en la tabla Playlists
	$sql = "UPDATE Playlists SET Imagen = '$imagen' WHERE Nombre = '$nombrePlaylist' AND Usuario = '$nombreUsuario'";
	$stmt = mysqli_prepare($con,$sql);
	mysqli_stmt_execute($stmt);
	if (mysqli_stmt_errno($stmt)!=0) {
		echo 'Error de sentencia: ' . mysqli_stmt_error($stmt);
	}
	else{
		$devolver = 'fotoActualizada';	
	}
	echo $devolver;

}
else if ($accion == "recogerFoto"){ #Recoger foto de una playlist
	
	$resultado = mysqli_query($con,"SELECT Imagen FROM Playlists WHERE Nombre = '$nombrePlaylist' AND Usuario = '$nombreUsuario'");
	
	if (!$resultado) {
		echo 'Ha ocurrido algún error: ' . mysqli_error($con);
		exit;
	}
	
	if (mysqli_num_rows($resultado) != 0) { #La playlist tiene imagen
		
		#Accedemos al resultado
		$fila = mysqli_fetch_row($resultado);
		
		# Generar el array con los resultados con la forma Atributo - Valor
		$arrayresultados = array(
			'imagen' => $fila[0]
		);
		
		#Devolver el resultado en formato JSON
		echo json_encode($arrayresultados);
	
	}
	else{ #La playlist no tiene imagen
		$devolver = 'noImagen';
		echo $devolver;
	}

}


#Cerramos la conexión con la base de datos
mysqli_close($con);

?>