(function() {
    'use strict';

    angular
        .module('splitItApp')
        .factory('HomeService', HomeService);

    HomeService.$inject = ['$uibModal'];

    function HomeService () {
        var service = {
            open: open
        };

        return service;

        function open (){
            $state.go('home');
        }
       /* function open () {
            if (modalInstance !== null) return;
            modalInstance = $uibModal.open({
                animation: true,
                templateUrl: 'app/components/login/login.html',
                controller: 'HomeController',
                controllerAs: 'vm',
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('login');
                        return $translate.refresh();
                    }]
                }
            });
            modalInstance.result.then(
                resetModal,
                resetModal
            );
        }*/
    }
})();
