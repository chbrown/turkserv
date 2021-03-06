import {app} from '../app';
import {NotifyUI} from 'notify-ui';

app
.controller('admin.templates.table', function($scope, Template) {
  $scope.templates = Template.query();
  $scope.delete = function(template) {
    // is this really the best way?
    var promise = template.$delete().then(function() {
      $scope.templates.splice($scope.templates.indexOf(template), 1);
      return 'Deleted';
    });
    NotifyUI.addPromise(promise);
  };
})
.controller('admin.templates.edit', function($scope, $http, $stateParams, $state, $location, Template) {
  $scope.template = Template.get($stateParams);

  $scope.sync = function() {
    var promise = $scope.template.$save().then(function() {
      $state.go('.', {id: $scope.template.id}, {notify: false});
      return 'Saved template';
    });
    NotifyUI.addPromise(promise);
  };

  // the 'save' event is broadcast on rootScope when command+S is pressed
  $scope.$on('save', $scope.sync);

  $scope.clone = function() {
    $state.go('.', {id: 'new'}, {notify: false});
    $scope.template = new Template({
      name: $scope.template.name + ' copy',
      html: $scope.template.html,
    });
  };
});
