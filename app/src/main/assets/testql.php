<?php
// Make an HTTP request to the GraphQL server

$user_id = 1;

$url = 'http://192.168.0.107:4000/graphql';
$query = '{
    getUserById(id: ' . $user_id . ') {
      user_id
      name
      email
    }
  }';

$ch = curl_init();
curl_setopt($ch, CURLOPT_URL, $url);
curl_setopt($ch, CURLOPT_POST, 1);
curl_setopt($ch, CURLOPT_POSTFIELDS, 'query=' . urlencode($query));
curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
$response = curl_exec($ch);
curl_close($ch);

// Handle the response
$data = json_decode($response, true);
$user = $data['data']['getUserById'];

// Output the user data in a table
echo '<table>';
echo '<tr><th>User ID</th><th>Name</th></tr>';
echo '<tr><td>' . $user['user_id'] . '</td><td>' . $user['name'] . '</td></tr>';
echo '</table>';
?>
