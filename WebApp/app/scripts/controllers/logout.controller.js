/**
 * Created by Azhar on 2016/10/12.
 */

'use strict';

angular.module('eVotingWebApp')

  .controller('LogoutCtrl',function($location, $scope, $localStorage){
    alert("clear");
    $localStorage.$reset();
    $location.path('/');
  });
