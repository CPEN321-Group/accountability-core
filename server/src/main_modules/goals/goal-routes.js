const {UserGoal} = require('./models');



module.exports = function(app) {
  app.route('/goals/:userId')
    .get((req,res) => {
      res.send(req.params);
    })
    .post((req,res) => {
      const userGoal = new UserGoal({
        userId: req.params.userId,
        categories: [],
        estimatedIncomes: [],
        goals:[goal]
      })
      userGoal.save(e=> e ? res.send(e) : res.send('saved goal successfully'))
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
}