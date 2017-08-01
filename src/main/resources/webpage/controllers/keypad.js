var alarm = angular.module('alarm', []);
alarm.controller('KeypadController', ['$scope', '$http', '$timeout', function ($scope, $http, $timeout) {

    $scope.secure = false;
    $scope.alarmed = false;
    $scope.code = "";
    $scope.loaded = false;
    $scope.message = "";

    $scope.getStatus = function () {
        $http({
            method: 'GET',
            url: '/alarm/status'
        }).then(updateStatus, errorStatus);
    }

    $scope.addCode = function (number) {
        $scope.code = $scope.code + number;

        if ($scope.code.length >= 4) {
            $timeout(function () {
                $http.post("/alarm/auth", $scope.code, updateScreen)
                $scope.refresh();
            },500);
        }
    }

    $scope.refresh = function () {
        $scope.code = "";
        $scope.message = "";
        $scope.getStatus();
    }

    function updateStatus(response) {
        var alarmStatus = response.data.alarmStatus;
        if(alarmStatus == 'Invalid Code') {
            message = "Invalid Code";
        } else {
            $scope.secure = alarmStatus == 'Secure' || alarmStatus == 'Alarmed';
            $scope.alarmed = alarmStatus == 'Alarmed';
            $scope.code = "";
            $scope.message = "";
        }
    }

    function errorStatus(errorResponse) {
        $scope.message = "An Error happened contacting Salutem Server";
    }

    function updateScreen(response) {
        $scope.message = ""
    }


    $scope.armSystem = function () {
        $http({
            method: "POST",
            url: '/alarm/arm',
            datatype: "json"
        }).then(updateStatus,errorStatus);
    }
    $scope.init = function () {
        $scope.loaded = true;
        $scope.refresh();
    }
}
]);