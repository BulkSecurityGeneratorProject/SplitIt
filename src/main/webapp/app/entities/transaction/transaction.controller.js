(function() {
    'use strict';

    angular
        .module('splitItApp')
        .controller('TransactionController', TransactionController);

    TransactionController.$inject = ['$scope', '$state', 'Transaction', 'UserGroup', 'ParseLinks', 'AlertService', 'pagingParams', 'paginationConstants','Principal', 'Auth'];

    function TransactionController ($scope, $state, Transaction, UserGroup, ParseLinks, AlertService, pagingParams, paginationConstants, Principal, Auth) {
        var vm = this;

        vm.currentState = $state.current.name;
        vm.loadPage = loadPage;
        vm.predicate = pagingParams.predicate;
        vm.reverse = pagingParams.ascending;
        vm.transition = transition;
        vm.itemsPerPage = paginationConstants.itemsPerPage;


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
        if($state.current.name == 'myTransactions'){
            loadAll(Transaction.myTransactions);
        } else {
            loadAll(Transaction.transactions);
        }
        });

        function loadAll (resource) {
            resource.query({
                login : vm.loggedInAccount.login,
                page: pagingParams.page - 1,
                size: vm.itemsPerPage,
                sort: sort()
            }, onSuccess, onError);

            function sort() {
                var result = [vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc')];
                if (vm.predicate !== 'id') {
                    result.push('id');
                }
                return result;
            }
            function onSuccess(data, headers) {
                vm.links = ParseLinks.parse(headers('link'));
                vm.totalItems = headers('X-Total-Count');
                vm.queryCount = vm.totalItems;
                vm.transactions = data;
                vm.page = pagingParams.page;
            }

            function onError(error) {
                AlertService.error(error.data.message);
            }
        }

        function loadPage (page) {
            vm.page = page;
            vm.transition();
        }

        function transition () {
            $state.transitionTo($state.$current, {
                page: vm.page,
                sort: vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc'),
                search: vm.currentSearch
            });
        }
    }
})();
