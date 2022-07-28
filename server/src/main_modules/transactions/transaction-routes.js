const { findTransactions, deleteTransactions, findTransaction, updateTransaction, deleteTransaction,createTransaction } = require('./transaction-store');

module.exports = function(app) {
  app.route('/transactions/:userId')
  .get(async (req,res) => {
    await findTransactions(req.params.userId,(err,status,returnData) => {
      if (err) console.log(err);
      res.status(status).json(returnData);
    })
  })
  .post(async (req,res,next) => {
    await createTransaction(
      req.params.userId,
      req.query,
      (err,status,returnData) => {
        if (err) console.log(err);
        res.status(status).json(returnData);
      })
  })
  .delete(async (req,res,next) => {
    await deleteTransactions(req.params.userId, (err,status,returnData) => {
      if (err) console.log(err);
      res.status(status).json(returnData);
    })
  })

  app.route('/transactions/:userId/:transactionId')
  .get(async (req,res,next) => {
    await findTransaction(req.params.userId,req.params.transactionId,(err,status,returnData) => {
      if (err) console.log(err);
      res.status(status).json(returnData);
    })
  })
  .put(async (req,res,next) => {
    await updateTransaction(req.params.userId,req.params.transactionId,req.query,(err,status,returnData) => {
      if (err) console.log(err);
      res.status(status).json(returnData);
    })
  })
  .delete(async (req,res,next) => {
    await deleteTransaction(req.params.userId,req.params.transactionId,(err,status,returnData) => {
      if (err) console.log(err);
      res.status(status).json(returnData);
    })
  })
}