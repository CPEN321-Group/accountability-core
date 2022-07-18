const { fieldsAreNotNull } = require('../../utils/get-defined-fields');
const { findGoals, deleteGoals, findGoal, updateGoal, deleteGoal,createGoal } = require('./goal-store');
const { UserGoal } = require('./models');
const { getDefinedFields } = require.main.require("./utils/get-defined-fields");
const { authenticate } = require.main.require("./main_modules/accounts/account-auth");


module.exports = function(app) {
  app.route('/goals/:accountId')
    .get(async (req,res) => {
      await findGoals(req.params.accountId, (err,status,returnData) => {
        res.status(status).json(returnData);
      })
    })
    .post(async (req,res) => {
      await createGoal(
        req.params.accountId,
        req.query,
        (err,status,returnData) => {
          res.status(status).json(returnData);
        })
    })
    .delete(async (req,res,next) => {
      await deleteGoals(req.params.accountId, (err,status,returnData) => {
        res.status(status).json(returnData);
      })
    })

  app.route('/goals/:accountId/:goalId')
    .get(async (req,res,next) => {
      const {accountId,goalId} = req.params;
      await findGoal(accountId,goalId, (err,status,returnData) => {
        res.status(status).json(returnData);
      })
    })
    .put(async (req,res,next) => {
      const {accountId,goalId} = req.params;
      await updateGoal(accountId,goalId,req.query, (err,status,returnData) => {
        res.status(status).json(returnData);
      })
    })
    .delete(async (req,res,next) => {
      const {accountId,goalId} = req.params;
      await deleteGoal(accountId,goalId,(err,status,returnData) => {
        res.status(status).json(returnData);
      })
    })
}