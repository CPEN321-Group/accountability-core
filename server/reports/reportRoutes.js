module.exports = function(app) {
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

  app.route('/reports/:userId/reportId')
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