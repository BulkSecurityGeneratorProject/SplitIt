(function() {
    'use strict';

    var jhiItemCount = {
        template: '<div class="info">' +
                    'Wyświetlono {{(($ctrl.page - 1) * $ctrl.itemsPerPage) == 0 ? 1 : (($ctrl.page - 1) * $ctrl.itemsPerPage + 1)}} - ' +
                    '{{($ctrl.page * $ctrl.itemsPerPage) < $ctrl.queryCount ? ($ctrl.page * $ctrl.itemsPerPage) : $ctrl.queryCount}} ' +
                    'z {{$ctrl.queryCount}} pozycji.' +
                '</div>',
        bindings: {
            page: '<',
            queryCount: '<total',
            itemsPerPage: '<'
        }
    };

    angular
        .module('splitItApp')
        .component('jhiItemCount', jhiItemCount);
})();
