App.controller('DayOffController', ['$scope', 'DayOffService', function ($scope, DayOffService) {
    $scope.dayOff = {id: null, name: '', startDate: '', endDate: '', rooms: ''};
    $scope.daysOff = [];
    $scope.rooms = [];
    $scope.check = true;

    $scope.getAllDaysOff = function () {
        DayOffService.getAllDaysOff()
            .success(function (result) {
                $scope.daysOff = result;
            })
            .error(function (err) {
                console.error('Error while fetching all Days Off' + err);
            });
    };

    $scope.getAllDaysOff();

    DayOffService.getAllRooms()
        .success(function (result) {
            $scope.rooms = result;
        })
        .error(function (err) {
            console.error('Error while fetching all rooms: ' + err);
        });

    $scope.getDayOff = function (id) {
        DayOffService.getDayOff(id)
            .success(function (result) {
                $scope.daysOff = result;
            })
            .error(function (err) {
                console.error('Error while fetching Day Off' + err);
            });
    };

    $scope.createDay = function (day) {
        $scope.inserted = {
            name: '',
            startDate: new Date().yyyymmdd(),
            endDate: new Date().yyyymmdd(),
            rooms: null
        };
        $scope.daysOff.unshift($scope.inserted);
    };

    $scope.saveDay = function (day) {
        if (day.id === undefined) {
            DayOffService.createDayOff(day)
                .success($scope.getAllDaysOff)
                .error(function (err) {
                    console.error('Error while creating Day Off' + err);
                });
        } else {
            DayOffService.updateDayOff(day.id, day)
                .success($scope.getAllDaysOff)
                .error(function (err) {
                    console.error('Error while updating Day Off' + err);
                });
        }
        $scope.dayOff.id = undefined;
    };

    $scope.deleteDay = function (id, index) {
        $scope.daysOff.splice(index, 1);

        DayOffService.deleteDayOff(id)
            .then(
                $scope.getAllDaysOff,
                function (errResponse) {
                    console.error('Error while deleting Day Off');
                }
            );
    };

    $scope.edit = function (id) {
        $scope.dayOff.id = id;
    };

    $scope.showAllRooms = function () {
        var selected = [];
        angular.forEach($scope.rooms, function (s) {
            if ($scope.dayOff.rooms.indexOf(s.value) >= 0) {
                selected.push(s.text);
            }
        });
        return selected.length ? selected.join(', ') : 'Not set';
    };

    $scope.checkForData = function (day) {
        if (day.name == '') {
            $scope.daysOff.splice(0, 1);
        }
    };

    $scope.checkName = function (data) {
        if (!data) {
            return "Enter a name";
        }
    };

    $scope.isRoomChecked = function (dayOffRooms, room) {
        if (dayOffRooms != null){
            return dayOffRooms
                .map(function (room) {
                    return room.id
                })
                .includes(room.id);
        }

    };

    $scope.assignRoom = function ($event, day, room) {
        $event.target.checked ?
            day.rooms.push(room) :
            day.rooms.splice(day.rooms.indexOf(room));

    };

    $scope.checkRooms = function(id) {
        if (id == $scope.dayOff.id) {
            return false;
        }
        return true;
    }

    Date.prototype.yyyymmdd = function () {
        var mm = this.getMonth() + 1;
        if (mm.toString().charAt(0) != '1') {
            mm = '0' + mm.toString();
        }
        else {
            mm = mm.toString();
        }
        var dd = this.getDate().toString();
        var yyyy = this.getFullYear().toString();

        return [this.getFullYear(), mm, dd].join('-');
    };

}]);
