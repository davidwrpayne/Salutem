var alarm = angular.module('alarm', []);
alarm.controller('KeypadController', ['$scope', function ($scope) {


    $scope.alarmed = true;


    $scope.greeting = "hello";



    function getStatus() {
        $http.get()
    }

}
]);