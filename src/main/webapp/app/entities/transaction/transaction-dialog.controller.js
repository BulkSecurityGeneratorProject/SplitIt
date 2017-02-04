(function() {
    'use strict';

    angular
        .module('splitItApp')
        .controller('TransactionDialogController', TransactionDialogController);

    TransactionDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Transaction', 'User', 'UserGroup', 'Principal'];

    function TransactionDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Transaction, User, UserGroup, Principal) {
        var vm = this;

        var copyAccount = function (account) {
            return {
                activated: account.activated,
                email: account.email,
                firstName: account.firstName,
                langKey: account.langKey,
                lastName: account.lastName,
                login: account.login,
                id: account.id,
                authorities: account.authorities
            };
        };

         vm.loggedInAccount = null;

         var promise = new Promise(
         function(resolve, reject) {
            Principal.identity().then(function(account) {
                vm.loggedInAccount = copyAccount(account);
            });
             resolve();
          }
          );

        promise.then(function(){
          vm.myGroups = UserGroup.myGroups.query({ login: vm.loggedInAccount.login});
        });

        vm.transaction = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;
        vm.users = User.query();
        vm.usergroups = UserGroup.groups.query();
        vm.validateDebitorCreditorError = null;
        vm.validateDebitorCreditor = function(){

            if(! vm.transaction.debitor || !vm.transaction.creditor){
                vm.validateDebitorCreditorError = null;
                return false;
            } else if(angular.equals(vm.transaction.debitor,vm.transaction.creditor)){
                vm.validateDebitorCreditorError = 'splitItApp.transaction.error.debitorCreditorEqual';
                return true;
            } else if (!angular.equals(vm.transaction.debitor.login,vm.loggedInAccount.login) && !angular.equals(vm.transaction.creditor.login,vm.loggedInAccount.login)) {
                vm.validateDebitorCreditorError = 'splitItApp.transaction.error.debitorCreditorNotCurrentUser';
                return true;
            } else {
                vm.validateDebitorCreditorError = null;
                return false;
            }
        }

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function loadMyGroups () {
            UserGroup.myGroups.query({ login: vm.loggedInAccount.login}, onSuccessGroups, onError);

            function onError(error) {
                AlertService.error(error.data.message);
            }

            function onSuccessGroups(data, headers) {
                vm.myGroups = data;
            }
        }

        function save () {
            vm.isSaving = true;
            if (vm.transaction.id !== null) {
                Transaction.transactions.update(vm.transaction, onSaveSuccess, onSaveError);
            } else {
                Transaction.transactions.save(vm.transaction, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('splitItApp:transactionUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.date = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
