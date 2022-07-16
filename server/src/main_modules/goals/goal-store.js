const { fieldsAreNotNull } = require('../../utils/get-defined-fields');
const { getItemFromList } = require('../../utils/get-from-list');
const {UserGoal, Goal} = require('./models');
const { getDefinedFields } = require.main.require('./utils/get-defined-fields');

function parseGoalData(fields) {
  const {title,target,current,deadline} = fields;
  const df = getDefinedFields({title,target,current,deadline});

  const fieldsToUpdate = {
    ...(df.title && {"goals.$.title": df.title}),
    ...(df.target && {"goals.$.target": df.target}),
    ...(df.current && {"goals.$.current": df.current}),
    ...(df.deadline && {"goals.$.deadline": df.deadline}),
  }
  return fieldsToUpdate;
}

function createUserGoal(userId,goals,callback) {
  const newUserGoal = new UserGoal({userId,goals});
  newUserGoal.save((err,createdUserGoal) => {
    callback(err,createdUserGoal)
  })
}
module.exports = {
  findGoals: async (accountId,callback) => {
    try {
      const usergoal = await UserGoal.findOne({userId: accountId});
      if (!usergoal) {
        return callback(404,'account not found');
      }
      return callback(200, usergoal.goals);
    } catch (err) {
      console.log(err);
      return callback(400,err);
    }
    
  },
  createGoal: async (accountId,data,callback) => {
    try {
      const df = getDefinedFields(data);
      const {title,target,current,deadline} = df;
      if (!fieldsAreNotNull({title,target,current,deadline})) {
        return callback(400,'missing params');
      }
  
      const goal = new Goal({title,target,current,deadline});
      const usergoal = await UserGoal.findOneAndUpdate(
        {userId: accountId},
        { $push: { goals: goal } },
        {returnDocument: 'after'}
      )
      if (!usergoal) {
        return callback(404,'account not found');
      }
      return callback(200,goal)
    } catch (err) {
      console.log(err);
      return callback(400,err);
    }
  },
  deleteGoals: async (accountId,callback) => {
    try {
      const usergoal = await UserGoal.findOneAndUpdate(
        {userId: accountId}, 
        {goals: []},
        {returnDocument:'after'});
      if (!usergoal) {
        return callback(404,'account not found');
      }
      return callback(200, 'goals deleted');
    } catch (err) {
      console.log(err);
      return callback(400,err);
    }
    
  },
  findGoal: async (accountId,goalId,callback) => {
    try {
      const usergoal = await UserGoal.findOne({userId:accountId});
      if (!usergoal) {
        return callback(404, 'account not found');
      }
        const goal = getItemFromList(usergoal.goals,goalId);
        if (!goal) {
          return callback(404, 'goal not found');
        }
        return callback(200, goal);
    } catch (err) {
      console.log(err);
      return callback(400,err);
    }
    
  },
  updateGoal: async (accountId,goalId,data,callback) => {
    try {
      const {title,target,current,deadline} = data;
      const fieldsToUpdate = parseGoalData({title,target,current,deadline});
  
      const usergoal = await UserGoal.findOneAndUpdate(
        {$and:[{userId: accountId}, {goals: { $elemMatch: { _id: goalId }}}]},
        {$set: fieldsToUpdate},
        {returnDocument: 'after'}
      )
      if (!usergoal) {
        return callback(404, 'account/goal not found');
      }
      const goal = getItemFromList(usergoal.goals,goalId);
      if (!goal) {
        return callback(404,'goal not found');
      }
      return callback(200, goal);
    } catch (err) {
      console.log(err);
      return callback(400,err);
    }
  },
  deleteGoal: async (accountId,goalId,callback) => {
    try {
      const usergoal = await UserGoal.findOneAndUpdate(
        {userId: accountId},
        {$pull: {goals: {_id: goalId}}},
        {returnDocument: 'after'},
      )
      if (!usergoal) { 
        return callback(404, 'account not found')
      }
      return callback(200, 'goal deleted');
    } catch (err) {
      console.log(err);
      return callback(400,err);
    }
    
  },
}