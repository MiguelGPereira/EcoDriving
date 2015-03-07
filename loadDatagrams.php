<?php
	echo "EcoDriving</br>"; 

	$servername = "127.0.0.1";
	$username = "root";
	$password = "";

	// Create connection
	$conn = new mysqli($servername, $username, $password);

	// Check connection
	if ($conn->connect_error) {
	    die("Connection failed: " . $conn->connect_error);
	} 
	echo "Connected successfully</br>";

	echo "Loading Datagrams...</br>";

	$dir    = 'datagrams';
	$files = scandir($dir);	
	$nrfiles = count($files)-2;
	$percentage = 0;
	echo $percentage.'%<br />';
	for ($k=200; $k < count($files); $k++) { 
		$txt_file = file_get_contents('datagrams/'.$files[$k]);
		$rows        = explode("\n", $txt_file);
		array_shift($rows);
		foreach($rows as $row => $data)
		{
		    //get row data
		    $row_data = preg_split('/\s+/', $data);//explode(' ', $data);

		    $query = "insert into test.Datagrams (`timestamp`,`comboio`,`maquinista`,`absorvida`,`recuperada`,`consumida`,`kms`,`horas`,`comunicador`,`ucc_master`,`manutencao`,`standby`,`washing`,`baterias`,`workshop`,`em_estacao`,`servico_regular`,`master`,`num_composicoes`,`esta_parado`,`valida_parado`,`portas_libertas`,`ordem_shutdown`,`velocidade`,`pesagem`,`modo`,`estado_marcha`,`offset_marcha`,`alterou_n_maq`) values(";

		    for ($i=0; $i <count($row_data) ; $i++) { 
		    		if($i==0){//datetime - needs formating
		    			$row_data[$i] = substr($row_data[$i], 0, 4) . "-" .  substr($row_data[$i], 4, 2) . "-" . substr($row_data[$i], 6, 2)
		    			. " " . substr($row_data[$i], 9, 2) . ":" . substr($row_data[$i], 11, 2) . ":" . substr($row_data[$i], 13, 2);
					}
					$query .= "'" . $row_data[$i] . "',";
		    	}
		    $query = substr($query, 0, -1);
		    $query .= ");";

			if (!$conn->query($query)) {/*skip line*/}
		}
		$new_percentage = $k * 100 / $nrfiles;
		if($new_percentage - $percentage >= 5){
			$percentage = floor($new_percentage);
			echo $percentage.'%<br />';
		}
	}
	echo '100%<br />';
	echo "Loading finished!</br>";

	$conn->close();
	echo "Connection closed</br>";
	
?>