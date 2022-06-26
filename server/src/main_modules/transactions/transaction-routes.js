const { fieldsAreNotNull } = require('../../utils/get-defined-fields');
const { findTransactions, deleteTransactions, findTransaction, updateTransaction, deleteTransaction,createTransaction } = require('./transaction-store');
const { getDefinedFields } = require.main.require("./utils/get-defined-fields");
const { authenticate } = require.main.require("./main_modules/accounts/account-auth");

module.exports = function(app) {
  app.route('/transactions/:userId')
  .get((req,res,next) => {
    const {userId} = req.params;
    const {token} = req.query;
    authenticate(token,userId,(err,foundAccount) => {
      if (err) return next(err)
      if (!foundAccount) return next(new Error('account not found'));
      findTransactions(userId,(err,foundTransactions) => {
        if (err) return next(err);
        return res.json(foundTransactions);
      })
    })
  })
  .post((req,res,next) => {
    const {userId} = req.params;
    const df = getDefinedFields(req.query);
    const {title,category,date,amount,isIncome,receipt,token} = df;
    if (!fieldsAreNotNull({title,category,amount,isIncome,token})) {return next(new Error('missing params'))}

    authenticate(token,userId,(err,foundAccount) => {
      if (err) return next(err)
      if (!foundAccount) return next(new Error('account not found'));
      createTransaction(userId,{title,category,date,amount,isIncome,receipt},(err,foundTransactions) => {
        if (err) return next(err);
        return res.json(foundTransactions);
      })
    })
  })
  .delete((req,res,next) => {
    const {userId} = req.params;
    const {token} = req.query;
    authenticate(token,userId,(err,foundAccount) => {
      if (err) return next(err)
      if (!foundAccount) return next(new Error('account not found'));
      deleteTransactions(foundAccount.id, (err) => {
        if (err) return next(err);
        return res.end('transactions deleted');
      })
    })
  })

  app.route('/transactions/:userId/:transactionId')
  .get((req,res,next) => {
    const {userId,transactionId} = req.params;
    const {token} = req.query;
    authenticate(token,userId,(err,foundAccount) => {
      if (err) return next(err)
      if (!foundAccount) return next(new Error('account not found'));
      findTransaction(userId,transactionId,(err,foundTransaction) => {
        if (err) return next(err);
        return res.json(foundTransaction);
      })
    })
  })
  .put((req,res,next) => {
    const {userId,transactionId} = req.params;
    const {token} = req.query
    
    authenticate(token,userId,(err,foundAccount) => {
      if (err) return next(err)
      if (!foundAccount) return next(new Error('account not found'));
      updateTransaction(userId,transactionId,req.query,(err,foundTransaction) => {
        if (err) return next(err);
        return res.json(foundTransaction);
      })
    })
  })
  .delete((req,res,next) => {
    const {userId,transactionId} = req.params;
    const {token} = req.query;

    authenticate(token,userId,(err,foundAccount) => {
      if (err) return next(err)
      if (!foundAccount) return next(new Error('account not found'));
      deleteTransaction(userId,transactionId,(err) => {
        if (err) return next(err);
        return res.end('transaction deleted');
      })
    })
  })
}