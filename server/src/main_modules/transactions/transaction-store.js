const { fieldsAreNotNull } = require('../../utils/get-defined-fields');
const { getItemFromList } = require('../../utils/get-from-list');
const {UserTransaction} = require('./models');
const { getDefinedFields } = require.main.require('./utils/get-defined-fields');

function parseTransactionData(fields) {
  const {title,category,date,amount,isIncome,receipt} = fields;
  const df = getDefinedFields({title,category,date,amount,isIncome,receipt});

  const fieldsToUpdate = {
    ...(df.title && {"transactions.$.title": df.title}),
    ...(df.category && {"transactions.$.category": df.category}),
    ...(df.date && {"transactions.$.date": df.date}),
    ...(df.amount && {"transactions.$.amount": df.amount}),
    ...(df.isIncome && {"transactions.$.isIncome": df.isIncome}),
    ...(df.receipt && {"transactions.$.receipt": df.receipt}),
  }
  return fieldsToUpdate;
}

function createUserTransaction(userId,transactions,callback) {
  const newUserTransaction = new UserTransaction({userId,transactions: transactions});
  newUserTransaction.save((err,createdUserTransaction) => {
    callback(err,createdUserTransaction)
  })
}
module.exports = {
  findTransactions: (accountId,callback) => {
    UserTransaction.findOne({userId: accountId},(err,foundUserTransaction) => callback(err,foundUserTransaction))
  },
  findTransaction: (accountId,transactionId,callback) => {
    UserTransaction.findOne({userId:accountId},(err,foundUserTransaction) => {
      const transaction = getItemFromList(foundUserTransaction.transactions,transactionId);
      if (transaction) return callback(err,transaction);
      return callback(new Error('transaction not found'),null);
    })
  },
  createTransaction: (accountId,data,callback) => {
    const {title,category,date,amount,isIncome,receipt} = data;
    let newTransaction;
    if (!fieldsAreNotNull(title,category,date,amount,isIncome) && receipt) {
      parsePhysicalReceipt(receipt);
    } else
      newTransaction = {title,category,date,amount,isIncome,receipt};
    
    UserTransaction.findOneAndUpdate({userId: accountId},{ $push: { transactions: newTransaction } },
      {returnDocument: 'after'},
      (err,foundUserTransaction) => {
        let transaction;
        if (!foundUserTransaction) {
          createUserTransaction(accountId,[newTransaction],(err,createdUserTransaction) => {
            console.log('creating user transaction...');
            transaction = createdUserTransaction.transactions[0];
            if (transaction) return callback(err,transaction);
          });
        } else { 
          transaction = foundUserTransaction.transactions[foundUserTransaction.transactions.length - 1] }
        
        if (transaction) return callback(err,transaction);
      }
    )
  },
  updateTransaction: (accountId,transactionId,data,callback) => {
    const {title,target,current,deadline} = data;
    const fieldsToUpdate = parseTransactionData({title,target,current,deadline});

    UserTransaction.findOneAndUpdate({$and:[{userId: accountId}, {
        transactions: { $elemMatch: { _id: transactionId }}
      }]},
      {$set: fieldsToUpdate},
      {returnDocument: 'after'},
      (err,foundUserTransaction) => {
        if (!foundUserTransaction) return callback(new Error('transaction not found'),null);
        const transaction = getItemFromList(foundUserTransaction.transactions,transactionId);
        if (transaction) return callback(err,transaction);
        return callback(new Error('transaction update unsuccessful'),null);
      }
    )
  },
  deleteTransactions: (accountId,callback) => {
    UserTransaction.deleteOne({userId: accountId}, (err) => {
      if (err) console.log(err);
      callback(err);
    })
  },
  deleteTransaction: (accountId,transactionId,callback) => {
    UserTransaction.findOneAndUpdate({userId: accountId},{$pull: {transactions: {_id: transactionId}}},
      {returnDocument: 'after'},
      (err,foundUserTransaction) => {
        const transaction = getItemFromList(foundUserTransaction.transactions,transactionId);
        if (transaction) callback(new Error('transaction deletion unsuccessful'));
        return callback(err);
      }
    )
  },
}