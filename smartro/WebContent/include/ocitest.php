<?php
$dbuser="IFOU";
$dbpass="1";

$dbsid = "(
  DESCRIPTION =
  (ADDRESS_LIST = 
   (ADDRESS = 
    (PROTOCOL = TCP)
    (HOST = 175.207.13.19)
    (PORT = 1521)
   )
  )

  (CONNECT_DATA =
   (SERVER = DEDICATED)
   (SERVICE_NAME = orcl)
  )
) ";

$conn = @oci_connect($dbuser,$dbpass,$dbsid);

if(!$conn) {
  echo "No Connection ".oci_error();
  exit;
} else {
 echo "Connect Success!";
}

$query = 'select * from dual';
$stmt = oci_parse($conn,$query);
oci_execute($stmt);

while($row = oci_fetch_assoc($stmt))
{
    print_r($row);
}

oci_free_statement($stmt);
oci_close($conn); 
?>
