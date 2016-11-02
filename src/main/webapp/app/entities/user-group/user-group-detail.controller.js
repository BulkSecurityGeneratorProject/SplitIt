(function() {
    'use strict';

    angular
        .module('splitItApp')
        .controller('UserGroupDetailController', UserGroupDetailController);

    UserGroupDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'UserGroup', 'User'];

    function UserGroupDetailController($scope, $rootScope, $stateParams, previousState, entity, UserGroup, User) {
        var vm = this;

        vm.userGroup = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('splitItApp:userGroupUpdate', function(event, result) {
            vm.userGroup = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
