<?php
header('Content-Type: application/json; charset=utf-8');

$con = mysqli_connect("localhost","wpkkcuac_root","Mmy_databaseH","wpkkcuac_my_database");

$sql = "SELECT * FROM user_table ORDER BY id DESC";

$result = mysqli_query($con,$sql);

$data = array();

foreach($result as $item){	
$userInfo['id'] = $item['id'];
$userInfo['name'] = $item['name'];
$userInfo['mobile'] = $item['mobile'];
$userInfo['email'] = $item['email'];
array_push($data,$userInfo);
}

echo json_encode($data);
?>
