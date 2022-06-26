const { getItemFromList } = require('../../utils/get-from-list');
const {UserGoal} = require('./models');
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
  findGoals: (accountId,callback) => {
    UserGoal.findOne({userId: accountId},(err,foundUserGoal) => callback(err,foundUserGoal))
  },
  findGoal: (accountId,goalId,callback) => {
    UserGoal.findOne({userId:accountId},(err,foundUserGoal) => {
      const goal = getItemFromList(foundUserGoal.goals,goalId);
      if (goal) return callback(err,goal);
      return callback(new Error('goal not found'),null);
    })
  },
  createGoal: (accountId,data,callback) => {
    const {title,target,current,deadline} = data;
    const newGoal = {title,target,current,deadline};
    UserGoal.findOneAndUpdate({userId: accountId},{ $push: { goals: newGoal } },
      {returnDocument: 'after'},
      (err,foundUserGoal) => {
        let goal;
        if (!foundUserGoal) {
          createUserGoal(accountId,[newGoal],(err,fug) => {
            // console.log('creating user goal...');
            goal = fug.goals[0];
            if (goal) return callback(err,goal);
          });
        } else { 
          goal = foundUserGoal.goals[foundUserGoal.goals.length - 1] }
        // console.log('goal created');
        
        if (goal) return callback(err,goal);
        // return callback(new Error('goal creation unsuccessful'),null);
      }
    )
  },
  updateGoal: (accountId,goalId,data,callback) => {
    const {title,target,current,deadline} = data;
    const fieldsToUpdate = parseGoalData({title,target,current,deadline});

    UserGoal.findOneAndUpdate({$and:[{userId: accountId}, {
        goals: { $elemMatch: { _id: goalId }}
      }]},
      {$set: fieldsToUpdate},
      {returnDocument: 'after'},
      (err,foundUserGoal) => {
        if (!foundUserGoal) return callback(new Error('goal not found'),null);
        const goal = getItemFromList(foundUserGoal.goals,goalId);
        if (goal) return callback(err,goal);
        return callback(new Error('goal update unsuccessful'),null);
      }
    )
  },
  deleteGoals: (accountId,callback) => {
    UserGoal.deleteOne({userId: accountId}, (err) => {
      if (err) console.log(err);
      callback(err);
    })
  },
  deleteGoal: (accountId,goalId,callback) => {
    UserGoal.findOneAndUpdate({userId: accountId},{$pull: {goals: {_id: goalId}}},
      {returnDocument: 'after'},
      (err,foundUserGoal) => {
        const goal = getItemFromList(foundUserGoal.goals,goalId);
        if (goal) callback(new Error('goal deletion unsuccessful'));
        return callback(err);
      }
    )
  },
}