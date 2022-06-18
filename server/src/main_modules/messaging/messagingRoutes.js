module.exports = function(app) {
  app.route('/messaging/:accountId')
    .get((req,res) => {
      res.send(req.params);
    })

  app.route('/messaging/:accountId/:chatId') //requires query params 'sender' and 'receiver'
    .post((req,res) => {
      const {chatId,accountId} = req.params;
      const {token,content} = req.query;

      res.send(content);
    })
    .delete((req,res) => {
      const {chatId,accountId} = req.params;
      const {token} = req.query;
      res.send(chatId);
    })
}