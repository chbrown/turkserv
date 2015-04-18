var url = require('url');
var amulet = require('amulet');
var Router = require('regex-router');
var Cookies = require('cookies');
var moment = require('moment');

var logger = require('loge');
var models = require('../models');

// router & actions for logging in
var R = new Router(function(req, res) {
  res.status(404).die('No resource at: ' + req.url);
});

/** GET /login
show login page for this user */
R.get(/^\/login\/?$/, function(req, res) {
  var urlObj = url.parse(req.url, true);
  res.status(401); // HTTP 401 Unauthorized
  // urlObj.query may have fields like: email, password, message
  amulet.stream(['layout.mu', 'login.mu'], urlObj.query).pipe(res);
});

/** POST /login
Try to login as user with email and password */
R.post(/^\/login$/, function(req, res) {
  req.readData(function(err, fields) {
    if (err) return res.die(err);

    // artificially slow to deflect brute force attacks
    setTimeout(function() {
      models.Administrator.authenticate(fields.email, fields.password, function(err, token) {
        if (err) {
          fields.message = err.toString();
          // we serve the login page from GET as well as failed login POSTs
          res.status(401);
          return amulet.stream(['layout.mu', 'login.mu'], fields).pipe(res);
        }

        logger.info('Authenticated successfully. Token = %s', token);

        var cookies = new Cookies(req, res);
        cookies.set('administrator_token', token, {
          path: '/',
          expires: moment().add(1, 'month').toDate(),
        });

        res.redirect('/admin');
      });
    }, 500);
  });
});

module.exports = R.route.bind(R);
