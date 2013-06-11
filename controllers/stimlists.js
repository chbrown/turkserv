'use strict'; /*jslint node: true, es5: true, indent: 2 */
var url = require('url');
var mechturk = require('mechturk');
var _ = require('underscore');
var amulet = require('amulet');
var models = require('../models');
var logger = require('../logger');
var Router = require('regex-router');

var R = new Router();
var authR = new Router();

// /stimlists
module.exports = function(m, req, res) {
  var workerId = req.cookies.get('workerId');
  var ticket = req.cookies.get('ticket');
  models.User.withTicket(workerId, ticket, function(err, user) {
    logger.maybe(err);
    if (user && user.superuser) {
      req.user = user;
      authR.route(req, res);
    }
    else {
      R.route(req, res);
    }
  });
};

// /stimlists/new -> create new Stimlist and redirect to edit it.
authR.get(/^\/stimlists\/new/, function(m, req, res) {
  // console.log(req.user, typeof(req.user._id));
  var stimlist = new models.Stimlist({creator: req.user._id});
  stimlist.save(function(err) {
    logger.maybe(err);
    // console.log('-->' + '/stimlists/' + stimlist._id + '/edit');
    res.redirect('/stimlists/' + stimlist._id + '/edit');
  });
});

// /stimlists/:stimlist_id/edit -> edit existing Stimlist
authR.get(/^\/stimlists\/(\w+)\/edit/, function(m, req, res) {
  models.Stimlist.findById(m[1], function(err, stimlist) {
    logger.maybe(err);
    // console.log("Rendering");
    amulet.stream(['layout.mu', 'admin/layout.mu', 'stimlists/edit.mu'], {stimlist: stimlist}).pipe(res);
  });
});

authR.default = function(m, req, res) {
  R.route(req, res);
};

R.get(/^\/stimlists\/(.+)/, function(m, req, res) {
  var urlObj = url.parse(req.url, true);
  var spreadsheet = m[1];
  // logger.info('request', {url: urlObj, headers: req.headers});
  // a normal turk request looks like: urlObj.query =
  // { assignmentId: '2NXNWAB543Q0EQ3C16EV1YB46I8620K',
  //   hitId: '2939RJ85OZIZ4RKABAS998123Q9M8NEW85',
  //   workerId: 'A9T1WQR9AL982W',
  //   turkSubmitTo: 'https://www.mturk.com' },
  var workerId = (urlObj.query.workerId || req.cookies.get('workerId') || '').replace(/\W+/g, '');
  var ctx = {
    assignmentId: urlObj.query.assignmentId,
    hitId: urlObj.query.hitId,
    workerId: workerId,
    host: urlObj.query.debug !== undefined ? '' : (urlObj.query.turkSubmitTo || 'https://www.mturk.com'),
    task_started: Date.now(),
  };
  req.cookies.set('workerId', workerId);

  // a preview request will be the same, minus workerId and turkSubmitTo,
  // and assignmentId will always then be 'ASSIGNMENT_ID_NOT_AVAILABLE'
  var allies = _.shuffle(names).slice(0, ctx.allies_per_scene).map(function(name) {
    return {
      title: 'Sgt.',
      name: name,
      reliability: random.range(0.0, 1.0) // maybe switch in a beta later
    };
  });

  models.User.findById(workerId, function(err, user) {
    logger.maybe(err);
    if (!user) {
      user = new User({_id: workerId});
      user.save(logger.maybe);
    }

    ctx.batches = _.range(ctx.batches_per_HIT).map(function(batch_index) {
      var batch = makeBatch(allies, ctx.scenes_per_batch);
      batch.id = batch_index + 1;
      return batch;
    });
  });
});
