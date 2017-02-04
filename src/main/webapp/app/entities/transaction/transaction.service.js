(function() {
    'use strict';
    angular
        .module('splitItApp')
        .factory('Transaction', Transaction);

    Transaction.$inject = ['$resource', 'DateUtils'];

    function Transaction ($resource, DateUtils) {
        var resourceUrl =  'api/transactions/:id';
        var myTransactionsResourceUrl =  'api/users/:login/transactions';

        return { transactions : $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.date = DateUtils.convertLocalDateFromServer(data.date);
                    }
                    return data;
                }
            },
            'update': {
                method: 'PUT',
                transformRequest: function (data) {
                    var copy = angular.copy(data);
                    copy.date = DateUtils.convertLocalDateToServer(copy.date);
                    return angular.toJson(copy);
                }
            },
            'save': {
                method: 'POST',
                transformRequest: function (data) {
                    var copy = angular.copy(data);
                    copy.date = DateUtils.convertLocalDateToServer(copy.date);
                    return angular.toJson(copy);
                }
            }
        }), myTransactions : $resource(myTransactionsResourceUrl, {login : '@login'}, {
                 'query': { method: 'GET', isArray: true},
                 'get': {
                     method: 'GET',
                     transformResponse: function (data) {
                         if (data) {
                             data = angular.fromJson(data);
                             data.date = DateUtils.convertLocalDateFromServer(data.date);
                         }
                         return data;
                     }
         }}), userTr : $resource(myTransactionsResourceUrl, {login : '@login'}, {
                 'query': { method: 'GET', isArray: true},
                 'get': {
                     method: 'GET',
                     transformResponse: function (data) {
                         if (data) {
                             data = angular.fromJson(data);
                             data.date = DateUtils.convertLocalDateFromServer(data.date);
                         }
                         return data;
                     }
                 }})
                  }
    }
})();
