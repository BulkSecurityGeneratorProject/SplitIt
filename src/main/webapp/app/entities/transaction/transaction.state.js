(function() {
    'use strict';

    angular
        .module('splitItApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('transaction', {
            parent: 'admin',
            url: '/transaction?page&sort&search',
            data: {
                authorities: ['ROLE_ADMIN'],
                pageTitle: 'splitItApp.transaction.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/transaction/transactions.html',
                    controller: 'TransactionController',
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
                    $translatePartialLoader.addPart('transaction');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        }).state('transactionsByGroup', {
                parent: 'entity',
                url: '/transactionsByGroup',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'splitItApp.userGroup.home.transactionsByGroup'
                },
                views: {
                    'content@': {
                        templateUrl: 'app/entities/transaction/groups-transactions.html',
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
                        $translatePartialLoader.addPart('summary');
                        return $translate.refresh();
                    }]
                }
            }).state('transactionsByGroupView', {
                          parent: 'transactionsByGroup',
                          url: '/transactionsByGroupView/{id}',
                          data: {
                              authorities: ['ROLE_USER']
                          },
                          onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                              $uibModal.open({
                                  templateUrl: 'app/entities/transaction/group-transactions-dialog.html',
                                  controller: 'TransactionByGroupDialogController',
                                  controllerAs: 'vm',
                                  backdrop: 'static',
                                  size: 'lg',
                                  resolve: {
                                      translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                                          $translatePartialLoader.addPart('summary');
                                          $translatePartialLoader.addPart('global');
                                          return $translate.refresh();
                                      }]
                                  }
                              }).result.then(function() {
                                  $state.go('^', {}, { reload: false });
                              }, function() {
                                  $state.go('^');
                              });
                          }]
                      })
        .state('transactionsByUser', {
           parent: 'entity',
           url: '/transactionsByUser?page&sort',
           data: {
               authorities: ['ROLE_ADMIN'],
               pageTitle: 'userManagement.home.title'
           },
           views: {
               'content@': {
                   templateUrl: 'app/entities/transaction/users-transactions.html',
                   controller: 'UserManagementController',
                   controllerAs: 'vm'
               }
           },            params: {
               page: {
                   value: '1',
                   squash: true
               },
               sort: {
                   value: 'id,asc',
                   squash: true
               }
           },
           resolve: {
               pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                   return {
                       page: PaginationUtil.parsePage($stateParams.page),
                       sort: $stateParams.sort,
                       predicate: PaginationUtil.parsePredicate($stateParams.sort),
                       ascending: PaginationUtil.parseAscending($stateParams.sort)
                   };
               }],
               translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                   $translatePartialLoader.addPart('user-management');
                   return $translate.refresh();
               }]

           }}).state('transactionsByUserView', {
                        parent: 'transactionsByUser',
                        url: '/transactionsByUserView/{login2}',
                        data: {
                            authorities: ['ROLE_USER']
                        },
                        onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                            $uibModal.open({
                                templateUrl: 'app/entities/transaction/users-transactions-dialog.html',
                                controller: 'TransactionByUserDialogController',
                                controllerAs: 'vm',
                                backdrop: 'static',
                                size: 'lg',
                                resolve: {
                                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                                        $translatePartialLoader.addPart('summary');
                                        $translatePartialLoader.addPart('global');
                                        return $translate.refresh();
                                    }]
                                }
                            }).result.then(function() {
                                $state.go('^', {}, { reload: false });
                            }, function() {
                                $state.go('^');
                            });
                        }]
                    }).state('myTransactions', {
                      parent: 'entity',
                      url: '/myTransactions?page&sort&search',
                      data: {
                          authorities: ['ROLE_USER'],
                          pageTitle: 'splitItApp.transaction.home.title'
                      },
                      views: {
                          'content@': {
                              templateUrl: 'app/entities/transaction/transactions.html',
                              controller: 'TransactionController',
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
                              $translatePartialLoader.addPart('transaction');
                              $translatePartialLoader.addPart('global');
                              return $translate.refresh();
                          }]
                      }
                  })
        .state('transaction-detail', {
            parent: 'myTransactions',
            url: '/transaction/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'splitItApp.transaction.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/transaction/transaction-detail.html',
                    controller: 'TransactionDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('transaction');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Transaction', function($stateParams, Transaction) {
                    return Transaction.transactions.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'transaction',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('transaction-detail.edit', {
            parent: 'transaction-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/transaction/transaction-dialog.html',
                    controller: 'TransactionDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Transaction', function(Transaction) {
                            return Transaction.transactions.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('transaction.new', {
            parent: 'myTransactions',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/transaction/transaction-dialog.html',
                    controller: 'TransactionDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                name: null,
                                description: null,
                                date: null,
                                value: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('myTransactions', null, { reload: 'myTransactions' });
                }, function() {
                    $state.go('myTransactions');
                });
            }]
        })
        .state('transaction.edit', {
            parent: 'myTransactions',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/transaction/transaction-dialog.html',
                    controller: 'TransactionDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Transaction', function(Transaction) {
                            return Transaction.transactions.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('myTransactions', null, { reload: 'myTransactions' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('transaction.delete', {
            parent: 'myTransactions',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/transaction/transaction-delete-dialog.html',
                    controller: 'TransactionDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Transaction', function(Transaction) {
                            return Transaction.transactions.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('myTransactions', null, { reload: 'myTransactions' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
