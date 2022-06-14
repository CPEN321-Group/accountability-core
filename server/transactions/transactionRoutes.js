module.exports = function(app) {
  app.route('/transactions/:userId')
    .get((req,res) => {
      res.send(req.params);
    })
    .post((req,res) => {
      res.send(req.params);
    })
    .delete((req,res) => {
      res.send(req.params);
    })

  app.route('/transactions/:userId/:transactionId')
    .get((req,res) => {
      res.send(req.params);
    })
    .put((req,res) => {
      res.send(req.params);
    })
    .delete((req,res) => {
      res.send(req.params);
    })
}