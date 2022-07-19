const { fieldsAreNotNull } = require('../../utils/get-defined-fields');
const { getItemFromList } = require('../../utils/get-from-list');
const {UserTransaction, Transaction} = require('./models');
const { getDefinedFields } = require.main.require('./utils/get-defined-fields');
const moment = require('moment');

const formatTransactions = (transactions) => {
  let formattedTransactions = [];
  for (const t of transactions) {
    // console.log(t);
    formattedTransactions.push(formatTransaction(t));
  }
  return formattedTransactions;
};

const formatTransaction = (transaction) => {
  return {
    title: transaction.title,
    category: transaction.category,
    date: moment(transaction.date).format('YYYY-MM-DD'),
    amount: transaction.amount,
    isIncome: transaction.isIncome,
    _id: transaction._id,
    receipt: transaction.receipt
  }
};

const parseTransactionData = (fields) => {
  const {title,category,date,amount,isIncome,receipt} = fields;
  const df = getDefinedFields({title,category,date,amount,isIncome,receipt});

  const fieldsToUpdate = {
    ...(df.title && {"transactions.$.title": df.title}),
    ...(df.category && {"transactions.$.category": df.category}),
    ...(df.date && {"transactions.$.date": df.date}),
    ...(df.amount && {"transactions.$.amount": Math.abs(df.amount)}),
    ...(df.isIncome && {"transactions.$.isIncome": df.isIncome}),
    ...(df.receipt && {"transactions.$.receipt": df.receipt}),
  }
  return fieldsToUpdate;
};

module.exports = {
  formatTransaction, 
  formatTransactions,
  createUserTransaction: (userId,transactions,callback) => {
    const newUserTransaction = new UserTransaction({userId,transactions});
    newUserTransaction.save((err,createdUserTransaction) => {
      callback(null,err,createdUserTransaction)
    })
  },
  //functions used by routes
  findTransactions: async (accountId,callback) => {
    try {
      const usertransaction = await UserTransaction.findOne({userId: accountId});
      if (!usertransaction) {
        return callback(null,404,'account not found');
      }
      return callback(null,200,usertransaction.transactions);
    } catch (err) {
      console.log(err);
      return callback(null,400, err);
    }
  },
  createTransaction: async (accountId,fields,callback) => {
    try {
      const df = getDefinedFields(fields);
      const {title,category,date,amount,isIncome,receipt,plaidTransactionId} = df;
      if (!fieldsAreNotNull({title,category,amount,isIncome})) {
        return callback(null,400, 'missing params');
      }
  
      const newTransaction = new Transaction({
        title,category,date,
        amount: Math.abs(amount),
        isIncome,receipt,plaidTransactionId
      });
      const pushItem = { transactions: newTransaction };
      const usertransaction = await UserTransaction.findOneAndUpdate(
        {userId: accountId},
        { $push:  pushItem},
        {returnDocument: 'after'},
      );
      if (!usertransaction) {
        return callback(null,404, 'account not found');
      }
      return callback(null,200, newTransaction);
    } catch (err) {
      console.log(err);
      return callback(null,400, err);
    }
  },
  deleteTransactions: async (accountId,callback) => {
    try {
      const usertransaction = await UserTransaction.findOneAndUpdate(
        {userId: accountId}, 
        {transactions: []},
        {returnDocument: 'after'}
      );
      if (!usertransaction) {
        return callback(null,404, 'account not found');
      }
      return callback(null,200, 'transactions deleted');
    } catch (err) {
      console.log(err);
      return callback(null,400, err);
    }
  },
  findTransaction: async (accountId,transactionId,callback) => {
    try {
      const usertransaction = await UserTransaction.findOne({userId:accountId});
      if (!usertransaction) {
        return callback(null,404, 'account not found');
      }
      const transaction = getItemFromList(usertransaction.transactions,transactionId);
      if (!transaction) {
        return callback(null,404, 'transaction not found');
      }
      return callback(null,200,transaction);
    } catch (err) {
      console.log(err);
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
        {returnDocument: 'after'},
      )
      if (!usertransaction) {
        return callback(null,404,'account not found');
      }
      const transaction = getItemFromList(usertransaction.transactions,transactionId);
      if (!transaction) {
        return callback(null,404,'transaction not found');
      }
      return callback(null,200,transaction);
    } catch (err) {
      console.log(err);
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
        return callback(null,404, 'account not found');
      }
      const transaction = getItemFromList(usertransaction.transactions,transactionId);
      if (!transaction) {
        return callback(null,404, 'transaction not found');
      }
      return callback(null,200, 'transaction deleted');
    } catch (err) {
      console.log(err);
      return callback(null,400, err);
    }
  },
}