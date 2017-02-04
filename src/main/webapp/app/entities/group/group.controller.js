(function() {
    'use strict';

    angular
        .module('splitItApp')
        .controller('UserGroupController', UserGroupController);

    UserGroupController.$inject = ['$filter','$scope', '$state', 'UserGroup', 'ParseLinks', 'AlertService', 'pagingParams', 'paginationConstants', 'Principal', 'Auth'];

    function UserGroupController ($filter,$scope, $state, UserGroup, ParseLinks, AlertService, pagingParams, paginationConstants, Principal, Auth) {
        var vm = this;

        vm.loadPage = loadPage;
        vm.predicate = pagingParams.predicate;
        vm.reverse = pagingParams.ascending;
        vm.transition = transition;
        vm.itemsPerPage = paginationConstants.itemsPerPage;
        vm.loggedInAccount = null;

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


        vm.$state = $state;

        loadAll();

         vm.isUserInGroup = function(account,userGroup){

             return $filter('filter')(userGroup.users, {id: account.id })[0];
         }

        function loadAll () {
            UserGroup.groups.query({
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
                vm.userGroups = data;
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


        vm.isAdmin =  function(account){
            var index = account.authorities.indexOf('ROLE_ADMIN');
            if(index == -1)
                return false;
            else
                return true;
        }

        vm.joinGroup = function(account,userGroup){
            vm.isSaving = true;
            userGroup.users.push(account);
            UserGroup.groups.update(userGroup, onSaveSuccess, onSaveError);
        }

        function onSaveSuccess (result) {
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }
    }
})();
