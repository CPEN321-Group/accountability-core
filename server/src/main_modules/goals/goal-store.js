const { fieldsAreNotNull, getDefinedFields } = require('../../utils/checks/get-defined-fields');
const { NotFoundError, ValidationError } = require('../../utils/errors');
const { getItemFromList } = require('../../utils/get-from-list');
const {UserGoal, Goal} = require('./goal-models');

function parseGoalData(fields) {
  const {title,target,current,deadline} = fields;
  const df = getDefinedFields({title,target,current,deadline});

  const fieldsToUpdate = {
    ...(df.title && {"goals.$.title": df.title}),
    ...(df.target && {"goals.$.target": df.target}),
    ...(df.current && {"goals.$.current": Math.abs(df.current)}),
    ...(df.deadline && {"goals.$.deadline": df.deadline}),
  }
  return fieldsToUpdate;
}

module.exports = {
  findGoals: async (accountId,callback) => {
    
    try {
      const usergoal = await UserGoal.findOne({userId: accountId});
      if (!usergoal) {
        return callback(null,404,new NotFoundError('account not found'));
      }
      return callback(null,200, usergoal.goals);
    } catch (err) {
      return callback(null,400,err);
    }
  },
  createGoal: async (accountId,data,callback) => {
    try {
      const df = getDefinedFields(data);
      const {title,target,current,deadline} = df;

      const goal = new Goal({title,target,current: current,deadline});
      const pushItem = { goals: goal };
      const usergoal = await UserGoal.findOneAndUpdate(
        {userId: accountId},
        { $push: pushItem },
        {returnDocument: 'after', runValidators: true}
      )
      if (!usergoal) {
        return callback(null,404,new NotFoundError('account not found'));
      }
      return callback(null,200,goal)
    } catch (err) {
      return callback(null,400,err);
    }
  },
  deleteGoals: async (accountId,callback) => {
    
    try {
      const usergoal = await UserGoal.findOneAndUpdate(
        {userId: accountId}, 
        {goals: []},
        {returnDocument:'after', runValidators: true}
      );
      if (!usergoal) {
        return callback(null,404,new NotFoundError('account not found'));
      }
      return callback(null,200, 'goals deleted');
    } catch (err) {
      return callback(null,400,err);
    }
  },
  findGoal: async (accountId,goalId,callback) => {
    
    try {
      const usergoal = await UserGoal.findOne({userId:accountId});
      if (!usergoal) {
        return callback(null,404, new NotFoundError('account not found'));
      }
        const goal = getItemFromList(usergoal.goals,goalId);
        if (!goal) {
          return callback(null,404, new NotFoundError('goal not found'));
        }
        return callback(null,200, goal);
    } catch (err) {
      return callback(null,400,err);
    }
  },
  updateGoal: async (accountId,goalId,data,callback) => {
    
    try {
      const {title,target,current,deadline} = data;
      const fieldsToUpdate = parseGoalData({title,target,current,deadline});
      
  
      const usergoal = await UserGoal.findOneAndUpdate(
        {$and:[{userId: accountId}, {goals: { $elemMatch: { _id: goalId }}}]},
        {$set: fieldsToUpdate},
        {returnDocument: 'after', runValidators: true}
      )
      if (!usergoal) {
        return callback(null,404, new NotFoundError('account/goal not found'));
      }
      const goal = getItemFromList(usergoal.goals,goalId);

      return callback(null,200, goal);
    } catch (err) {
      return callback(null,400,err);
    }
  },
  deleteGoal: async (accountId,goalId,callback) => {
    
    try {
      const goalMatch = {_id: goalId};
      const pullItem = {goals: goalMatch};
      const usergoal = await UserGoal.findOneAndUpdate(
        {userId: accountId},
        {$pull: pullItem},
      )
      if (!usergoal) { 
        return callback(null,404, new NotFoundError('account not found'))
      }
      const goal = getItemFromList(usergoal.goals,goalId);
      if (!goal) {
        return callback(null,404, new NotFoundError('goal not found'));
      }
      return callback(null,200, 'goal deleted');
    } catch (err) {
      return callback(null,400,err);
    }
  },
}