const {User,Accountant} = require('./models');
const _ = require.main.require('./utils/tests/modelSamples')

module.exports = function(app) {
  app.route('/users')
    .post((req,res) => {
      const {firstname,lastname,email,secretKey,age,profession} = req.query;
      // _.user0.save(e => {
      //   if(e) {res.send(e);}
      //   else res.send('user0 saved');
      // });
      // _.user1.save(e => {
      //   if(e) {res.send(e);}
      //   else res.send('user1 saved');
      // });
      _.user2.save(e => {
        if(e) {res.send(e);}
        else res.send('user2 saved');
      });
    });

  app.route('/users/:userId')
    .get((req,res) => {
      const {userId} = req.params;
      const {token} = req.query;
      res.send(req.params);
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
      _.accountant0.save(e => {
        if(e) {res.send(e);}
        else res.send('new accountant is saved');
      })
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