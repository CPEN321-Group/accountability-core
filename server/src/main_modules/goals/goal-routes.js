const { fieldsAreNotNull } = require('../../utils/get-defined-fields');
const { findGoals, deleteGoals, findGoal, updateGoal, deleteGoal,createGoal } = require('./goal-store');
const { getDefinedFields } = require.main.require("./utils/get-defined-fields");
const { authenticate } = require.main.require("./main_modules/accounts/account-auth");


module.exports = function(app) {
  app.route('/goals/:userId')
    .get((req,res,next) => {
      const {userId} = req.params;
      const {token} = req.query;
      authenticate(token,userId,(err,foundAccount) => {
        if (err) return next(err)
        if (!foundAccount) return next(new Error('account not found'));
        findGoals(userId,(err,foundGoals) => {
          if (err) return next(err);
          return res.json(foundGoals);
        })
      })
    })
    .post((req,res,next) => {
      const {userId} = req.params;
      const df = getDefinedFields(req.query);
      const {title,target,current,deadline,token} = df;
      if (!fieldsAreNotNull(title,target,current,deadline,token)) {next(new Error('missing params'))}

      authenticate(token,userId,(err,foundAccount) => {
        if (err) return next(err)
        if (!foundAccount) return next(new Error('account not found'));
        createGoal(userId,{title,target,current,deadline},(err,foundGoals) => {
          if (err) return next(err);
          return res.json(foundGoals);
        })
      })
    })
    .delete((req,res,next) => {
      const {userId} = req.params;
      const {token} = req.query;
      authenticate(token,userId,(err,foundAccount) => {
        if (err) return next(err)
        if (!foundAccount) return next(new Error('account not found'));
        deleteGoals(foundAccount.id, (err) => {
          if (err) return next(err);
          return res.end('goals deleted');
        })
      })
    })

  app.route('/goals/:userId/:goalId')
    .get((req,res,next) => {
      const {userId,goalId} = req.params;
      const {token} = req.query;
      authenticate(token,userId,(err,foundAccount) => {
        if (err) return next(err)
        if (!foundAccount) return next(new Error('account not found'));
        findGoal(userId,goalId,(err,foundGoal) => {
          if (err) return next(err);
          return res.json(foundGoal);
        })
      })
    })
    .put((req,res,next) => {
      const {userId,goalId} = req.params;
      const {token} = req.query

      authenticate(token,userId,(err,foundAccount) => {
        if (err) return next(err)
        if (!foundAccount) return next(new Error('account not found'));
        updateGoal(userId,goalId,req.query,(err,foundGoal) => {
          if (err) return next(err);
          return res.json(foundGoal);
        })
      })
    })
    .delete((req,res,next) => {
      const {userId,goalId} = req.params;
      const {token} = req.query;

      authenticate(token,userId,(err,foundAccount) => {
        if (err) return next(err)
        if (!foundAccount) return next(new Error('account not found'));
        deleteGoal(userId,goalId,(err) => {
          if (err) return next(err);
          return res.end('goal deleted');
        })
      })
    })
}