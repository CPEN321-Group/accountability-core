const { findGoals, deleteGoals, findGoal, updateGoal, deleteGoal,createGoal } = require('./goal-store');

module.exports = function(app) {
  app.route('/goals/:accountId')
    .get(async (req,res) => {
      await findGoals(req.params.accountId, (err,status,returnData) => {
        if (err) console.log(err);
        res.status(status).json(returnData);
      })
    })
    .post(async (req,res) => {
      await createGoal(
        req.params.accountId,
        req.query,
        (err,status,returnData) => {
          if (err) console.log(err);
          res.status(status).json(returnData);
        })
    })
    .delete(async (req,res) => {
      await deleteGoals(req.params.accountId, (err,status,returnData) => {
        if (err) console.log(err);
        res.status(status).json(returnData);
      })
    })

  app.route('/goals/:accountId/:goalId')
    .get(async (req,res) => {
      await findGoal(req.params.accountId,req.params.goalId, (err,status,returnData) => {
        if (err) console.log(err);
        res.status(status).json(returnData);
      })
    })
    .put(async (req,res) => {
      await updateGoal(req.params.accountId,req.params.goalId,req.query, (err,status,returnData) => {
        if (err) console.log(err);
        res.status(status).json(returnData);
      })
    })
    .delete(async (req,res) => {
      await deleteGoal(req.params.accountId,req.params.goalId,(err,status,returnData) => {
        if (err) console.log(err);
        res.status(status).json(returnData);
      })
    })
}