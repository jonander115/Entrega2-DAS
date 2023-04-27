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


if ($accion == "nuevaPlaylist"){ #Crear nueva playlist

	#El nombre de la playlist es el nombre que se pondrá a la nueva playlist
	$nombrePlaylist = $_POST["nombrePlaylist"];
	
	#Primero hay que comprobar que el usuario no tiene una playlist con ese nombre
	$resultado = mysqli_query($con, "SELECT Nombre FROM Playlists WHERE Usuario = '$nombreUsuario' AND Nombre = '$nombrePlaylist'");
	
	#Comprobar si se ha ejecutado correctamente
	if (!$resultado) {
		echo 'Ha ocurrido algún error: ' . mysqli_error($con);
	}
	
	if (mysqli_num_rows($resultado) == 0){ #Si el usuario no tiene una playlist con ese nombre
	
		#Inserción de la playlist
		$resultado2 = mysqli_query($con, "INSERT INTO Playlists(Nombre,Usuario,Imagen) VALUES('$nombrePlaylist','$nombreUsuario','')");
	
		#Comprobar si se ha ejecutado correctamente
		if (!$resultado2) {
			echo 'Ha ocurrido algún error: ' . mysqli_error($con);
		}
		else{
			$devolver = 'playlistCreada';
		}
		
	}
	else{
		$devolver = 'yaExistePlaylist';
	}
	
	echo $devolver;
}
else if ($accion == "listarPlaylistsUsuario"){ #Listar las playlists del usuario
	
	$resultado = mysqli_query($con, "SELECT Nombre FROM Playlists WHERE Usuario = '$nombreUsuario'");
	
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
			'NombrePlaylist' => $fila[0]
			);
			$cont++;
		}
	}
	
	#Devolver el resultado
	echo json_encode($arrayresultados);
}

else if ($accion == "eliminarPlaylist"){ #Eliminar una playlist
	
	#Se invoca este servicio web por cada playlist que se elimina, ya que el usuario
	# elige las playlists que desee mediante un diálogo de selección múltiple
	
	#El nombre de la playlist es el nombre de la playlist a eliminar
	$nombrePlaylist = $_POST["nombrePlaylist"];	
	
	#Borrar la playlist de la tabla "Playlists"
	$resultado = mysqli_query($con, "DELETE FROM Playlists WHERE Nombre = '$nombrePlaylist' AND Usuario = '$nombreUsuario'");
	
	#Comprobar si se ha ejecutado correctamente
	if ($resultado == true) {
		
		#Borrar los registros de la playlist en "RelacionPlaylistsCanciones"
		$resultado2 = mysqli_query($con, "DELETE FROM RelacionPlaylistsCanciones WHERE NombrePlaylist = '$nombrePlaylist' AND NombreUsuario = '$nombreUsuario'");
		
		#Comprobar si se ha ejecutado correctamente
		if ($resultado2 == true) {
			$devolver = 'playlistEliminada';
		}
		
	}
	
	echo $devolver;
	
}

#Cerramos la conexión con la base de datos
mysqli_close($con);

?>