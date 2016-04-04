var alarm = angular.module('alarm', []);
alarm.controller('KeypadController', ['$scope', '$http', function ($scope, $http) {


    $scope.alarmed = true;


    $scope.greeting = "hello";
    $scope.code = "123";


    $scope.getStatus = function() {
        $http.get("/alarm/status").then(updateStatus(),errorStatus())
    }


    function updateStatus(data) {
        $scope.code = data;
        $scope.alarmed = !$scope.alarmed;
    }
    function errorStatus() {

    }
}
]);