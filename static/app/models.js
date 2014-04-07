/*jslint browser: true */ /*globals app, p, toMap */

app.service('AccessToken', function($resource) {
  return $resource('/api/access_tokens/:id', {
    id: '@id',
  });
});

app.service('Administrator', function($resource) {
  // map: {'id': 'email'}
  return $resource('/api/administrators/:id', {
    id: '@id',
  });
});

app.service('AWSAccount', function($resource) {
  // map: {'id': 'name'}
  // this.hosts = [{name: 'deploy'}, {name: 'sandbox'}];
  return $resource('/api/aws_accounts/:id', {
    id: '@id',
  });
});

app.service('AWSAccountAdministrator', function($resource) {
  return $resource('/api/administrators/:administrator_id/aws_accounts/:aws_account_id', {
    administrator_id: '@administrator_id',
    aws_account_id: '@aws_account_id',
  });
});

app.service('Experiment', function($resource) {
  return $resource('/api/experiments/:id', {
    id: '@id',
  });
});

app.service('Participant', function($resource) {
  return $resource('/api/participants/:id', {
    id: '@id',
  });
});

app.service('Stim', function($resource) {
  // map: {'id': 'name'}
  var Stim = $resource('/api/experiments/:experiment_id/stims/:id', {
    experiment_id: '@experiment_id',
    id: '@id',
  });

  return Stim;
});

app.service('Template', function($resource) {
  // map: {'id': 'name'}
  return $resource('/api/templates/:id', {
    id: '@id',
  });
  // }, {'delete': {method:'DELETE'} });
});
