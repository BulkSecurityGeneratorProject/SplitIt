(function() {
    'use strict';

    angular
        .module('splitItApp')
        .controller('UserGroupDialogController', UserGroupDialogController);

    UserGroupDialogController.$inject = ['$filter','$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'UserGroup', 'User', 'Principal', 'Auth'];

    function UserGroupDialogController ($filter, $timeout, $scope, $stateParams, $uibModalInstance, entity, UserGroup, User, Principal, Auth ) {
        var vm = this;


        vm.userGroup = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;
        vm.loggedInAccount = null;

        Principal.identity().then(function(account) {
            vm.loggedInAccount = copyAccount(account);
        });

        vm.users = User.query(function(){
        if(!vm.userGroup.owner){
            vm.userGroup.owner = $filter('filter')(vm.users, {id: vm.loggedInAccount.id })[0];
        }


        });


        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.userGroup.id !== null) {
                UserGroup.groups.update(vm.userGroup, onSaveSuccess, onSaveError);
            } else {
                UserGroup.groups.save(vm.userGroup, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('splitItApp:userGroupUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.creationDate = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }

        var copyAccount = function (account) {
            return {
                activated: account.activated,
                email: account.email,
                firstName: account.firstName,
                langKey: account.langKey,
                lastName: account.lastName,
                login: account.login,
                id: account.id
            };
        };




    }
})();
