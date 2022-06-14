module.exports = function(app) {
  app.route('/messaging/:accountId')
    .get((req,res) => {
      res.send(req.params);
    })

  app.route('/messaging') //requires query params 'sender' and 'receiver'
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
}