const { fieldsAreNotNull } = require('../../utils/get-defined-fields');
const { findGoals, deleteGoals, findGoal, updateGoal, deleteGoal,createGoal } = require('./goal-store');
const { UserGoal } = require('./models');
const { getDefinedFields } = require.main.require("./utils/get-defined-fields");
const { authenticate } = require.main.require("./main_modules/accounts/account-auth");


module.exports = function(app) {
  app.route('/goals/:userId')
    .get((req,res,next) => {
      UserGoal.findOne({userId: req.params.userId},(err,userGoal) => {
        if (err) return next(err);
        if (!userGoal) return res.status(404).end('account not found');
        return res.json(userGoal.goals);
      })
    })
    .post((req,res,next) => {
      const df = getDefinedFields(req.body);
      const {title,target,current,deadline} = df;
      if (!fieldsAreNotNull({title,target,current,deadline})) {next(new Error('missing params'))}
      createGoal(req.params.userId,{title,target,current,deadline},(err,foundGoals) => {
        if (err) return next(err);
        return res.json(foundGoals);
      })
    })
    .delete((req,res,next) => {
      deleteGoals(req.params.userId, (err,usergoal) => {
        if (err) return next(err);
        if (!usergoal) return res.status(404).end('account not found');
        return res.end('goals deleted');
      })
    })

  app.route('/goals/:userId/:goalId')
    .get((req,res,next) => {
      const {userId,goalId} = req.params;
      findGoal(userId,goalId,(err,foundGoal) => {
        if (err) return next(err);
        return res.json(foundGoal);
      })
    })
    .put((req,res,next) => {
      const {userId,goalId} = req.params;
      
      updateGoal(userId,goalId,req.body,(err,goal) => {
        if (err) return next(err);
        return res.json(goal);
      })
    })
    .delete((req,res,next) => {
      const {userId,goalId} = req.params;
      deleteGoal(userId,goalId,(err) => {
        if (err) return next(err);
        return res.end('goal deleted');
      })
    })
}