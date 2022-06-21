const { Account } = require("./models");

const _ = require.main.require('./utils/tests/modelSamples')

module.exports = function(app) {
  app.route('/users')
    .post((req,res) => {
      const {firstname,lastname,email,secretKey,age,profession} = req.query;
      _.users.forEach(user => {
        user.save(e => {
          if(e) {console.log(e);}
          else console.log('user2 saved');
        })
      })
      res.send('user saved');
    });

  app.route('/users/:userId')
    .get((req,res) => {
      const {userId} = req.params;
      const {token} = req.query;
      Account.findById(userId,(err,foundAccount)=> {
        if (err) console.log(err);
        res.send(foundAccount);
      });
    })
    .put((req,res) => {
      const {userId} = req.params;
      const {token,firstname,lastname,email,age,profession,hasAccountant} = req.query;
      res.send(req.params);
    })
    .delete((req,res) => {
      const {userId} = req.params;
      const {token} = req.query;
      res.send(req.params);
    })

  app.route('/accountants')
    .post((req,res) => {
      const {firstname,lastname,email,secretKey,age,profession} = req.query;
    });

  app.route('/accountants/:accountantId')
    .get((req,res) => {
      const {accountantId} = req.params;
      const {token} = req.query;
      res.send(req.params);
    })
    .put((req,res) => {
      const {accountantId} = req.params;
      const {token,firstname,lastname,email,age,profession} = req.query;
      res.send(req.params);
    })
    .delete((req,res) => {
      const {accountantId} = req.params;
      const {token} = req.query;
      res.send(req.params);
    })

  app.route('/reviews/:accountantId')
    .get((req,res) => {
      const {accountantId} = req.params;
      res.send(req.params);
    })
    .post((req,res) => {
      const {accountantId} = req.params;
      const {token,authorId,rating,title,content} = req.query;
      res.send(req.params);
    })
}