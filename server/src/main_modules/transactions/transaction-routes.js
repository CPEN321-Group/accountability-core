const { fieldsAreNotNull } = require('../../utils/get-defined-fields');
const { findTransactions, deleteTransactions, findTransaction, updateTransaction, deleteTransaction,createTransaction } = require('./transaction-store');
const { getDefinedFields } = require.main.require("./utils/get-defined-fields");
const { authenticate } = require.main.require("./main_modules/accounts/account-auth");

module.exports = function(app) {
  app.route('/transactions/:userId')
  .get(async (req,res) => {
    await findTransactions(req.params.userId,(err,status,returnData) => {
      res.status(status).json(returnData);
    })
  })
  .post(async (req,res,next) => {
    await createTransaction(
      req.params.userId,
      req.query,
      (err,status,returnData) => {
        res.status(status).json(returnData);
      })
  })
  .delete(async (req,res,next) => {
    await deleteTransactions(req.params.userId, (err,status,returnData) => {
      res.status(status).json(returnData);
    })
  })

  app.route('/transactions/:userId/:transactionId')
  .get(async (req,res,next) => {
    const {userId,transactionId} = req.params;
    await findTransaction(userId,transactionId,(err,status,returnData) => {
      res.status(status).json(returnData);
    })
  })
  .put(async (req,res,next) => {
    const {userId,transactionId} = req.params;
    await updateTransaction(userId,transactionId,req.query,(err,status,returnData) => {
      res.status(status).json(returnData);
    })
  })
  .delete(async (req,res,next) => {
    const {userId,transactionId} = req.params;
    await deleteTransaction(userId,transactionId,(err,status,returnData) => {
      res.status(status).json(returnData);
    })
  })
}