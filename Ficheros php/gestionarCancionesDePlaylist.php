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
$nombreUsuario = $_POST["nombreUsuario"];
$nombrePlaylist = $_POST["nombrePlaylist"];

#Canción que se quiere eliminar, y su autor:
$nombreCancion = $_POST["nombreCancion"];
$autorCancion = $_POST["autorCancion"];
	
#Eliminamos la canción de la tabla que relaciona las canciones con las Playlists
$resultado = mysqli_query($con, "DELETE FROM RelacionPlaylistsCanciones WHERE NombrePlaylist = '$nombrePlaylist' AND NombreUsuario = '$nombreUsuario' AND NombreCancion = '$nombreCancion' AND AutorCancion = '$autorCancion'");
	
#Comprobar si se ha ejecutado correctamente
if (!$resultado) {
	echo 'Ha ocurrido algún error: ' . mysqli_error($con);
}

		
#Hay que disminuir en una unidad el número de canciones de la playlist
#Lo cogemos de la base de datos
$resultado2 = mysqli_query($con, "SELECT NumCanciones FROM Playlists WHERE Usuario = '$nombreUsuario' AND Nombre = '$nombrePlaylist'");
	
#Comprobamos si se ha ejecutado correctamente
if (!$resultado2) {
	echo 'Ha ocurrido algún error: ' . mysqli_error($con);
}

#Acceder al resultado
$fila = mysqli_fetch_row($resultado2);

$numCancionesNuevo = --$fila[0];
	
#Actualizamos el número en la base de datos
$resultado3 = mysqli_query($con, "UPDATE Playlists SET NumCanciones='$numCancionesNuevo' WHERE Usuario = '$nombreUsuario' AND Nombre = '$nombrePlaylist'");
	
#Comprobamos si se ha ejecutado correctamente
if (!$resultado3) {
	echo 'Ha ocurrido algún error: ' . mysqli_error($con);
}
		
$devolver = 'cancionEliminada';


echo $devolver;

#Cerramos la conexión con la base de datos
mysqli_close($con);

?>