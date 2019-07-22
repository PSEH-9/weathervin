<!DOCTYPE html>
<html>
<script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.6.9/angular.min.js"></script>
<body>

<div ng-app="myApp" ng-controller="myCtrl"> 

<h1>Weather Forecast</h1>


<table>
  <tbody ng-repeat="i in weather">
    <tr><td>{{i}}</td></tr>
  </tbody>
</table>

</div>

<script>
var app = angular.module('myApp', []);
app.controller('myCtrl', function($scope, $http) {
  $http.get("/weathervin/weather")
  .then(function(response) {
      $scope.weather = response.data;
  });
});
</script>

</body>
</html>