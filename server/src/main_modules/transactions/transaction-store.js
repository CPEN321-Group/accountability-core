const { fieldsAreNotNull } = require('../../utils/get-defined-fields');
const { getItemFromList } = require('../../utils/get-from-list');
const {UserTransaction} = require('./models');
const { getDefinedFields } = require.main.require('./utils/get-defined-fields');
const moment = require('moment');

module.exports = {
  parseTransactionData: (fields) => {
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
  },
  formatTransaction: (transaction) => {
    return {
      title: transaction.title,
      category: transaction.category,
      date: moment(transaction.date).format('YYYY-MM-DD'),
      amount: transaction.amount,
      isIncome: transaction.isIncome,
      _id: transaction._id,
      receipt: transaction.receipt
    }
  },
  formatTransactions: (transactions) => {
    let formattedTransactions = [];
    for (const t of transactions) {
      console.log(t);
      formattedTransactions.push(formatTransaction(t));
    }
    return formattedTransactions;
  },
  
  createUserTransaction: (userId,transactions,callback) => {
    const newUserTransaction = new UserTransaction({userId,transactions: transactions});
    newUserTransaction.save((err,createdUserTransaction) => {
      callback(err,createdUserTransaction)
    })
  },
  //functions used by routes
  findTransactions: (accountId,callback) => {
    UserTransaction.findOne({userId: accountId},(err,usertransaction) => {
      if (!usertransaction) return callback(new Error('account not found'),null);
      callback(err,formatTransactions(usertransaction.transactions))
    })
  },
  findTransaction: (accountId,transactionId,callback) => {
    UserTransaction.findOne({userId:accountId},(err,usertransaction) => {
      if (!usertransaction) return callback(new Error('account not found'),null);
      const transaction = getItemFromList(usertransaction.transactions,transactionId);
      if (transaction) return callback(err,formatTransaction(transaction));
      return callback(new Error('transaction not found'),null);
    })
  },
  createTransaction: (accountId,data,callback) => {
    const {title,category,date,amount,isIncome,receipt,plaidTransactionId} = data;
    let newTransaction;
    if (!fieldsAreNotNull({title,category,date,amount,isIncome}) && receipt) {
      return next(new Error('missing params')) //will change later to parse receipt
    } else
      newTransaction = {title,category,date,amount: Math.abs(amount),isIncome,receipt,plaidTransactionId};
    
    UserTransaction.findOneAndUpdate({userId: accountId},{ $push: { transactions: newTransaction } },
      {returnDocument: 'after'},
      (err,usertransaction) => {
        let transaction;
        if (!usertransaction) return callback(new Error('account not found'),null);
        transaction = usertransaction.transactions[usertransaction.transactions.length - 1]
        
        if (transaction) return callback(err,formatTransaction(transaction));
        return callback(new Error('transaction creation unsuccessful'),null);
      }
    )
  },
  updateTransaction: (accountId,transactionId,data,callback) => {
    const {title,category,date,amount,isIncome,receipt} = data;
    const fieldsToUpdate = parseTransactionData({title,category,date,amount,isIncome,receipt});

    UserTransaction.findOneAndUpdate({$and:[{userId: accountId}, {
        transactions: { $elemMatch: { _id: transactionId }}
      }]},
      {$set: fieldsToUpdate},
      {returnDocument: 'after'},
      (err,usertransaction) => {
        if (!usertransaction) return callback(new Error('account/transaction not found'),null);
        const transaction = getItemFromList(usertransaction.transactions,transactionId);
        if (transaction) return callback(err,formatTransaction(transaction));
        return callback(new Error('transaction update unsuccessful'),null);
      }
    )
  },
  deleteTransactions: (accountId,callback) => {
    UserTransaction.findOneAndUpdate({userId: accountId}, {transactions: []},{returnDocument: 'after'},(err,usertransaction) => {
      if (err) return callback(err);
      if (!usertransaction) return callback(new Error('account not found'))
      return callback(err);
    })
  },
  deleteTransaction: (accountId,transactionId,callback) => {
    UserTransaction.findOneAndUpdate({userId: accountId},{$pull: {transactions: {_id: transactionId}}},
      {returnDocument: 'after'},
      (err,usertransaction) => {
        if (!usertransaction) return callback(new Error('account not found'));
        const transaction = getItemFromList(usertransaction.transactions,transactionId);
        if (transaction) callback(new Error('transaction deletion unsuccessful'));
        return callback(err);
      }
    )
  },
}