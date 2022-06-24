const { getItemFromList } = require('../../utils/get-from-list');
const {UserTransaction} = require('./models');


function parseTransactionData(fields) {
  const {title,category,date,amount,isIncome,receipt} = fields;
  const df = getDefinedFields({title,category,date,amount,isIncome,receipt});

  const fieldsToUpdate = {
    ...(df.title && {"goals.$.title": df.title}),
    ...(df.category && {"profile.category": df.category}),
    ...(df.date && {"profile.date": df.date}),
    ...(df.amount && {"profile.amount": df.amount}),
    ...(df.isIncome && {"profile.isIncome": df.isIncome}),
    ...(df.amount && {"profile.amount": df.amount}),
  }
  return fieldsToUpdate;
}

function createUserTransaction(userId,goals,callback) {
  const newUserGoal = new UserTransaction({userId,goals});
  newUserGoal.save((err,createdUserGoal) => {
    callback(err,createdUserGoal)
  })
}
module.exports = {
  findGoals: (accountId,callback) => {
    UserTransaction.findOne({userId: accountId},(err,foundUserGoal) => callback(err,foundUserGoal))
  },
  findGoal: (accountId,goalId,callback) => {
    UserTransaction.findOne({userId:accountId},(err,foundUserGoal) => {
      const goal = getItemFromList(foundUserGoal.goals,goalId);
      if (goal) return callback(err,goal);
      return callback(new Error('goal not found'),null);
    })
  },
  createGoal: (accountId,data,callback) => {
    const {title,target,current,deadline} = data;
    const newGoal = {title,target,current,deadline};
    UserTransaction.findOneAndUpdate({userId: accountId},{ $push: { goals: newGoal } },
      {returnDocument: 'after'},
      (err,foundUserGoal) => {
        let goal;
        if (!foundUserGoal) {
          createUserTransaction(accountId,[newGoal],(err,fug) => {
            console.log('creating user goal...');
            goal = fug.goals[0];
            if (goal) return callback(err,goal);
          });
        } else { 
          goal = foundUserGoal.goals[foundUserGoal.goals.length - 1] }
        console.log('goal created');
        
        if (goal) return callback(err,goal);
        // return callback(new Error('goal creation unsuccessful'),null);
      }
    )
  },
  updateGoal: (accountId,goalId,data,callback) => {
    const {title,target,current,deadline} = data;
    const fieldsToUpdate = parseTransactionData({title,target,current,deadline});

    UserTransaction.updateOne({userId: accountId, 'goals.id': goalId},{$set: fieldsToUpdate},
      {returnDocument: 'after'},
      (err,foundGoal) => {
        const goal = getItemFromList(foundUserGoal.goals,goalId);
        if (goal) return callback(err,goal);
        return callback(new Error('goal creation unsuccessful'),null);
      }
    )
  },
  deleteGoals: (accountId,callback) => {
    UserTransaction.deleteOne({userId: accountId}, (err) => {
      if (err) console.log(err);
      callback(err);
    })
  },
  deleteGoal: (accountId,goalId,callback) => {
    UserTransaction.updateOne({userId: accountId},{$pull: {goals: {_id: goalId}}},
      {returnDocument: 'after'},
      (err,foundUserGoal) => {
        const goal = getItemFromList(foundUserGoal.goals,goalId);
        if (goal) callback(new Error('goal deletion unsuccessful'));
        return callback(err);
      }
    )
  },
}