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
$nombreCancion = $_POST["nombreCancion"];
$autorCancion = $_POST["autorCancion"];

#Primero tenemos que ver si la playlist que ha introducido el usuario permite que se inserte ahí la canción
	
#Esta consulta es algo compleja, porque una canción no puede estar más de una vez en la misma playlist.
#Debemos recoger aquellas playlists que sean del usuario y que en ningún registro de la tabla que relaciona las
# playlists con sus canciones esté presente el nombre de la playlist con el nombre de la canción (porque eso
# significaría que la canción ya había sido añadida a la playlist con anterioridad)
#Además, como distintos autores han podido publicar canciones con el mismo nombre, he incluido el autor en la consulta
#De este modo al buscar una canción podrán aparecer dos canciones con ese nombre que serán distintas porque son de distintos autores
	
#En la variable "resultado" tendremos todas las playlists a las que es posible añadir la canción
$resultado = mysqli_query($con, "SELECT Nombre FROM Playlists WHERE Usuario = '$nombreUsuario' AND Nombre NOT IN (SELECT NombrePlaylist FROM RelacionPlaylistsCanciones WHERE NombreCancion='$nombreCancion' AND NombreUsuario='$nombreUsuario' AND AutorCancion='$autorCancion')");
	
#Comprobamos si se ha ejecutado correctamente
if (!$resultado) {
	echo 'Ha ocurrido algún error: ' . mysqli_error($con);
}

$arrayresultados = array();
$estaPlaylist = FALSE;

#Recorrer el resultado
if (mysqli_num_rows($resultado) != 0){
	
	$cont = 0;
	while ($fila = mysqli_fetch_row($resultado)){
		$arrayresultados[$cont] = array(
		'NombrePlaylist' => $fila[0]
		);
		if ($fila[0] == $nombrePlaylist){
			$estaPlaylist = TRUE;
		}
		$cont++;
	}

#Si el nombre de la playlist introducida por el usuario está entre las playlists a las que se puede añadir la canción
if ($estaPlaylist == TRUE){
	
	#Añadimos la canción a la playlist
	$resultado = mysqli_query($con, "INSERT INTO RelacionPlaylistsCanciones VALUES('$nombrePlaylist','$nombreCancion','$nombreUsuario','$autorCancion')");
	
	#Comprobar si se ha ejecutado correctamente
	if (!$resultado) {
		echo 'Ha ocurrido algún error: ' . mysqli_error($con);
	}
		
	#Hay que aumentar en una unidad el número de canciones de la playlist
	#Lo cogemos de la base de datos
	$resultado2 = mysqli_query($con, "SELECT NumCanciones FROM Playlists WHERE Usuario = '$nombreUsuario' AND Nombre = '$nombrePlaylist'");
	
	#Comprobamos si se ha ejecutado correctamente
	if (!$resultado2) {
		echo 'Ha ocurrido algún error: ' . mysqli_error($con);
	}
		
	#Acceder al resultado
	$fila2 = mysqli_fetch_row($resultado2);

	$numCancionesNuevo = ++$fila2[0];
	
	#Actualizamos el número en la base de datos
	$resultado3 = mysqli_query($con, "UPDATE Playlists SET NumCanciones='$numCancionesNuevo' WHERE Usuario = '$nombreUsuario' AND Nombre = '$nombrePlaylist'");
	
	#Comprobamos si se ha ejecutado correctamente
	if (!$resultado3) {
		echo 'Ha ocurrido algún error: ' . mysqli_error($con);
	}
	else{
		$devolver = 'cancionAñadida';
	}
	
	
}

else{
	$devolver = 'noPosible';
}
}
else{
	$devolver = 'noPosible';
}

#Devolver el resultado
echo $devolver;

#Cerramos la conexión con la base de datos
mysqli_close($con);



?>