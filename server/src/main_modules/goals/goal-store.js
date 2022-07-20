const { fieldsAreNotNull, getDefinedFields } = require('../../utils/get-defined-fields');
const { getItemFromList } = require('../../utils/get-from-list');
const {UserGoal, Goal} = require('./models');

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
function isPastDate(date) {
  const today = new Date();
  return today.getTime() > date.getTime();
}

module.exports = {
  findGoals: async (accountId,callback) => {
    try {
      const usergoal = await UserGoal.findOne({userId: accountId});
      if (!usergoal) {
        return callback(null,404,'account not found');
      }
      return callback(null,200, usergoal.goals);
    } catch (err) {
      console.log(err);
      return callback(null,400,err);
    }
  },
  createGoal: async (accountId,data,callback) => {
    try {
      const df = getDefinedFields(data);
      const {title,target,current,deadline} = df;
      if (!fieldsAreNotNull({title,target,current,deadline})) {
        return callback(null,400,'missing params');
      }
      if (isPastDate(new Date(deadline))) {
        return callback(null,400,'goal deadline cannot be in the past');
      }
      const goal = new Goal({title,target,current: Math.abs(current),deadline});
      const pushItem = { goals: goal };
      const usergoal = await UserGoal.findOneAndUpdate(
        {userId: accountId},
        { $push: pushItem },
        {returnDocument: 'after'}
      )
      if (!usergoal) {
        return callback(null,404,'account not found');
      }
      return callback(null,200,goal)
    } catch (err) {
      console.log(err);
      return callback(null,400,err);
    }
  },
  deleteGoals: async (accountId,callback) => {
    try {
      const usergoal = await UserGoal.findOneAndUpdate(
        {userId: accountId}, 
        {goals: []},
        {returnDocument:'after'});
      if (!usergoal) {
        return callback(null,404,'account not found');
      }
      return callback(null,200, 'goals deleted');
    } catch (err) {
      console.log(err);
      return callback(null,400,err);
    }
  },
  findGoal: async (accountId,goalId,callback) => {
    try {
      const usergoal = await UserGoal.findOne({userId:accountId});
      if (!usergoal) {
        return callback(null,404, 'account not found');
      }
        const goal = getItemFromList(usergoal.goals,goalId);
        if (!goal) {
          return callback(null,404, 'goal not found');
        }
        return callback(null,200, goal);
    } catch (err) {
      console.log(err);
      return callback(null,400,err);
    }
  },
  updateGoal: async (accountId,goalId,data,callback) => {
    try {
      const {title,target,current,deadline} = data;
      const fieldsToUpdate = parseGoalData({title,target,current,deadline});
      if (deadline && isPastDate(new Date(deadline))) {
        return callback(null, 400, 'goal deadline cannot be in the past');
      }
  
      const usergoal = await UserGoal.findOneAndUpdate(
        {$and:[{userId: accountId}, {goals: { $elemMatch: { _id: goalId }}}]},
        {$set: fieldsToUpdate},
        {returnDocument: 'after'}
      )
      if (!usergoal) {
        return callback(null,404, 'account/goal not found');
      }
      const goal = getItemFromList(usergoal.goals,goalId);
      if (!goal) {
        return callback(null,404,'goal not found');
      }
      return callback(null,200, goal);
    } catch (err) {
      console.log(err);
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
        return callback(null,404, 'account not found')
      }
      const goal = getItemFromList(usergoal.goals,goalId);
      if (!goal) {
        return callback(null,404, 'goal not found');
      }
      return callback(null,200, 'goal deleted');
    } catch (err) {
      console.log(err);
      return callback(null,400,err);
    }
  },
}