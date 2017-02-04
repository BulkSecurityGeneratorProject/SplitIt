(function() {
    'use strict';
    angular
        .module('splitItApp')
        .factory('Summary', Summary);

    Summary.$inject = ['$resource', 'DateUtils'];

    function Summary ($resource, DateUtils) {
        var mySummaryUrl =  'api/users/:login/summary';
        var userSummaryUrl =  'api/users/:login/summary/:login2';
        var groupSummaryUrl =  'api/users/:login/groups/:groupId/summary';

        return { groupSummaries : $resource(groupSummaryUrl, {groupId : '@groupId',login : '@login'}, {
            'query': { method: 'GET'},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.date = DateUtils.convertLocalDateFromServer(data.date);
                    }
                    return data;
                }
            }

        }), userSummaries : $resource(userSummaryUrl, {login : '@login', login2 : '@login2'}, {
                 'query': { method: 'GET'},
                 'get': {
                     method: 'GET',
                     transformResponse: function (data) {
                         if (data) {
                             data = angular.fromJson(data);
                             data.date = DateUtils.convertLocalDateFromServer(data.date);
                         }
                         return data;
                     }
         }}), mySummary : $resource(mySummaryUrl, {login : '@login'}, {
                   'query': { method: 'GET'},
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
