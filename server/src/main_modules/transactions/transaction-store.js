const { getDefinedFields } = require('../../utils/checks/get-defined-fields');
const { NotFoundError } = require('../../utils/errors');
const { getItemFromList } = require('../../utils/get-from-list');
const {UserTransaction, Transaction} = require('./transaction-models');

const parseTransactionData = (fields) => {
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
};

module.exports = {
  //functions used by routes
  findTransactions: async (accountId,callback) => {
    
    try {
      const usertransaction = await UserTransaction.findOne({userId: accountId});
      if (!usertransaction) {
        return callback(null,404,new NotFoundError('account not found'));
      }
      return callback(null,200,usertransaction.transactions);
    } catch (err) {
      return callback(null,400, err);
    }
  },
  createTransaction: async (accountId,fields,callback) => {
    
    try {
      const df = getDefinedFields(fields);
      const {title,category,date,amount,isIncome,receipt,plaidTransactionId} = df;
  
      const newTransaction = new Transaction({
        title,category,date,amount,
        isIncome,receipt,plaidTransactionId
      });
      const pushItem = { transactions: newTransaction };
      const usertransaction = await UserTransaction.findOneAndUpdate(
        {userId: accountId},
        { $push:  pushItem},
        {returnDocument: 'after', runValidators: true},
      );
      if (!usertransaction) {
        return callback(null,404, new NotFoundError('account not found'));
      }
      return callback(null,200, newTransaction);
    } catch (err) {
      return callback(null,400, err);
    }
  },
  deleteTransactions: async (accountId,callback) => {
    
    try {
      const usertransaction = await UserTransaction.findOneAndUpdate(
        {userId: accountId}, 
        {transactions: []},
        {returnDocument: 'after', runValidators: true}
      );
      if (!usertransaction) {
        return callback(null,404, new NotFoundError('account not found'));
      }
      return callback(null,200, 'transactions deleted');
    } catch (err) {
      return callback(null,400, err);
    }
  },
  findTransaction: async (accountId,transactionId,callback) => {
    
    try {
      const usertransaction = await UserTransaction.findOne({userId:accountId});
      if (!usertransaction) {
        return callback(null,404, new NotFoundError('account not found'));
      }
      const transaction = getItemFromList(usertransaction.transactions,transactionId);
      if (!transaction) {
        return callback(null,404, new NotFoundError('transaction not found'));
      }
      return callback(null,200,transaction);
    } catch (err) {
      return callback(null,400, err);
    }
  },
  updateTransaction: async (accountId,transactionId,data,callback) => {
    
    try {
      const {title,category,date,amount,isIncome,receipt} = data;
      const fieldsToUpdate = parseTransactionData({title,category,date,amount,isIncome,receipt});
  
      const usertransaction = await UserTransaction.findOneAndUpdate(
        {$and:[{userId: accountId}, {transactions: { $elemMatch: { _id: transactionId }}}]},
        {$set: fieldsToUpdate},
        {returnDocument: 'after', runValidators: true},
      )
      if (!usertransaction) {
        return callback(null,404,new NotFoundError('account/transaction not found'));
      }
      const transaction = getItemFromList(usertransaction.transactions,transactionId);
      return callback(null,200,transaction);
    } catch (err) {
      return callback(null,400, err);
    }
  },
  deleteTransaction: async (accountId,transactionId,callback) => {
    
    try {
      const transactionsMatch = {_id: transactionId};
      const pullItem = { transactions: transactionsMatch};
      const usertransaction = await UserTransaction.findOneAndUpdate(
        {userId: accountId},
        {$pull: pullItem},
      )
      if (!usertransaction) {
        return callback(null,404, new NotFoundError('account not found'));
      }
      const transaction = getItemFromList(usertransaction.transactions,transactionId);
      if (!transaction) {
        return callback(null,404, new NotFoundError('transaction not found'));
      }
      return callback(null,200, 'transaction deleted');
    } catch (err) {
      return callback(null,400, err);
    }
  },
}