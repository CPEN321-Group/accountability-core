const {User,Accountant} = require('./models');

const user1 = new User({
  id: '1',
  subscribed: false,
  enabledNotifications: false,
  secretKey: 'abc123',
  profile: {
    firstname: 'Dean',
    lastname: 'Yang',
    email: 'test123@gmail.com'
  }
})
const accountant1 = new Accountant({
  id: '1',
  enabledNotifications: false,
  secretKey: 'abc123',
  profile: {
    firstname: 'Dean',
    lastname: 'Yang',
    email: 'test123@gmail.com'
  },
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