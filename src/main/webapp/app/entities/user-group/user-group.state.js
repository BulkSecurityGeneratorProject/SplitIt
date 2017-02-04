(function() {
    'use strict';

    angular
        .module('splitItApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('user-group', {
            parent: 'entity',
            url: '/user-group?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'splitItApp.userGroup.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/user-group/user-groups.html',
                    controller: 'UserGroupController',
                    controllerAs: 'vm'
                }
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'id,asc',
                    squash: true
                },
                search: null
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        search: $stateParams.search
                    };
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('userGroup');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('user-group-detail', {
            parent: 'entity',
            url: '/user-group/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'splitItApp.userGroup.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/user-group/user-group-detail.html',
                    controller: 'UserGroupDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('userGroup');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'UserGroup', function($stateParams, UserGroup) {
                    return UserGroup.groups.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'user-group',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('user-group-detail.edit', {
            parent: 'user-group-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/user-group/user-group-dialog.html',
                    controller: 'UserGroupDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['UserGroup', function(UserGroup) {
                            return UserGroup.groups.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('user-group.new', {
            parent: 'user-group',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/user-group/user-group-dialog.html',
                    controller: 'UserGroupDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                name: null,
                                isPrivate: false,
                                creationDate: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('user-group', null, { reload: 'user-group' });
                }, function() {
                    $state.go('user-group');
                });
            }]
        })
        .state('user-group.edit', {
            parent: 'user-group',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/user-group/user-group-dialog.html',
                    controller: 'UserGroupDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['UserGroup', function(UserGroup) {
                            return UserGroup.groups.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('user-group', null, { reload: 'user-group' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('user-group.delete', {
            parent: 'user-group',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/user-group/user-group-delete-dialog.html',
                    controller: 'UserGroupDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['UserGroup', function(UserGroup) {
                            return UserGroup.groups.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('user-group', null, { reload: 'user-group' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
