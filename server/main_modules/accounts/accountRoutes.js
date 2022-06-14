module.exports = function(app) {
  app.route('/users')
    .post((req,res) => {
      res.send('posting to /users');
    })

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
      res.send(req.params);
    })

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