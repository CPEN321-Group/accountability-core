const routes = (app) => {
  app.route('/')
    .get((req, res) => {
      res.send('Hello World!')
    })

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

  app.route('/goals/:userId')
    .get((req,res) => {
      res.send(req.params);
    })
    .post((req,res) => {
      res.send(req.params);
    })
    .delete((req,res) => {
      res.send(req.params);
    })

  app.route('/goals/:userId/:goalId')
    .get((req,res) => {
      res.send(req.params);
    })
    .put((req,res) => {
      res.send(req.params);
    })
    .delete((req,res) => {
      res.send(req.params);
    })

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

  app.route('/messages/:accountId')
    .get((req,res) => {
      res.send(req.params);
    })

  app.route('/messages') //requires query params 'sender' and 'receiver'
    .post((req,res) => {
      let sender = req.query.sender;
      let receiver = req.query.receiver;
      res.send(sender + ' & ' + receiver);
    })
    .delete((req,res) => {
      let sender = req.query.sender;
      let receiver = req.query.receiver;
      res.send(sender + ' & ' + receiver);
    })

  app.route('/reports/:userId')
    .get((req,res) => {
      res.send(req.params);
    })
    .post((req,res) => {
      res.send(req.params);
    })
    .delete((req,res) => {
      res.send(req.params);
    })

  app.route('/transactions/:userId/reportId')
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
}

module.exports = routes;