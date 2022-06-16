const {User,Accountant} = require('./models');

const user1 = new User({
  firstname: 'Dean',
  lastname: 'Yang',
  email: 'test123@gmail.com',
  id: '1',
  subscribed: false,
  secretKey: 'secret',
})
const accountant1 = new Accountant({
  firstname: 'Dean',
  lastname: 'Yang',
  email: 'test123@gmail.com',
  id: '2',
  // secretKey: '2',
  reviews: []
})

module.exports = function(app) {
  app.route('/users')
    .post((req,res) => {
      user1.save(e => {
        if(e) {res.send('save unsuccessful, check your fields');}
        else res.send('new user is saved');
      })
    });

  app.route('/users/:userId')
    .get((req,res) => {
      res.send(req.params);
    })
    .put((req,res) => {
      res.send(req.params);
    })
    .delete((req,res) => {
      res.send(req.params);
    })

  app.route('/accountants')
    .post((req,res) => {
      accountant1.save(e => {
        if(e) {res.send(e);}
        else res.send('new accountant is saved');
      })
    });

  app.route('/accountants/:accountantId')
    .get((req,res) => {
      res.send(req.params);
    })
    .put((req,res) => {
      res.send(req.params);
    })
    .delete((req,res) => {
      res.send(req.params);
    })

  app.route('/reviews/:accountantId')
    .get((req,res) => {
      res.send(req.params);
    })
    .post((req,res) => {
      res.send(req.params);
    })
}