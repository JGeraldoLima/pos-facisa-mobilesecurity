var express = require('express'),
    _       = require('lodash'),
    config  = require('./config'),
    jwt     = require('jsonwebtoken'),
    userRepository = require("./userRepository");

var app = module.exports = express.Router();

// XXX: This should be a database of users :).


function createIdToken(user) {
  return jwt.sign(_.omit(user, 'password'), config.secret, { expiresIn: 60*60*5 });
}

function createAccessToken(user) {
  return jwt.sign({
    user:user,
    iss: config.issuer,
    aud: config.audience,
    exp: Math.floor(Date.now() / 1000) + (60 * 60),
    scope: 'full_access',
    sub: "lalaland|gonto",
    jti: genJti(), // unique identifier for the token
    alg: 'HS256'
  }, config.secret);
}

// Generate Unique Identifier for the access token
function genJti() {
  let jti = '';
  let possible = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
  for (let i = 0; i < 16; i++) {
      jti += possible.charAt(Math.floor(Math.random() * possible.length));
  }
  
  return jti;
}

function getUserScheme(req) {
  
  var username;
  var type;
  var userSearch = {};

  // The POST contains a username and not an email
  if(req.body.username) {
    username = req.body.username;
    type = 'username';
    userSearch = { username: username };
  }
  // The POST contains an email and not an username
  else if(req.body.email) {
    username = req.body.email;
    type = 'email';
    userSearch = { email: username };
  }

  return {
    username: username,
    type: type,
    userSearch: userSearch
  }
}

app.post('/users', function(req, res) {
  
  var userScheme = getUserScheme(req);  

  if (!userScheme.username || !req.body.password) {
    return res.status(400).send({errors:["You must send the username and the password"]});
  }

  if (_.find(userRepository, userScheme.userSearch)) {
   return res.status(400).send({errors:["A user with that username already exists"]});
  }

  var profile = _.pick(req.body, userScheme.type, 'password', 'extra');
  profile.id = _.max(userRepository, 'id').id + 1;

  userRepository.push(profile);

  res.status(201).send({
    id_token: createIdToken(profile),
    access_token: createAccessToken(profile)
  });
});

app.post('/sessions/create', function(req, res) {

  var userScheme = getUserScheme(req);

  if (!userScheme.username || !req.body.password) {
    return res.status(400).send({errors:["You must send the username and the password"]});
  }

  var user = _.find(userRepository, userScheme.userSearch);
  
  if (!user) {
    return res.status(401).send({errors:["The username or password don't match"]});
  }

  if (user.password !== req.body.password) {
    return res.status(401).send({errors:["The username or password don't match 1"]});
  }

  res.status(201).send({
    id_token: createIdToken(user),
    access_token: createAccessToken(user)
  });
});
