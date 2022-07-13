require('dotenv').config();
const { createAccount, updateProfile, createReview, deleteAccount, createSubscription, updateSubscription, findAccountants, findAccount } = require("./account-store");

module.exports = function(app) {
  app.route('/accounts')
    .post(async (req,res,next) => {
      await createAccount(req.query, (status,returnData) => {
        res.status(status).json(returnData);
      })
    });

  app.get('/accounts/accountants', async (req,res) => { //fetch all accountants
    await findAccountants((status,returnData) => {
      res.status(status).json(returnData);
    })
  })

  app.route('/accounts/:accountId')
    .get(async (req,res) => {
      await findAccount(req.params.accountId, (status,returnData) => {
        res.status(status).json(returnData);
      })
    })
    .put(async (req,res) => {
      await updateProfile(req.params.accountId, req.query, (status,returnData) => {
        res.status(status).json(returnData);
      })
    })
    .delete(async (req,res) => {
      await deleteAccount(req.params.accountId, (status,returnData) => {
        res.status(status).json(returnData);
      })
    })

  app.route('/reviews/:accountantId')
    .post(async (req,res) => {
      await createReview(
        req.params.accountantId, req.query, 
        (status,returnData) => {
          res.status(status).json(returnData);
        })
    })
  app.route('/subscription/:accountId')
    .post(async (req,res) => {
      await createSubscription(
        req.params.accountId, req.query, 
        (status,returnData) => {
          res.status(status).json(returnData);
        })
    })
    .put(async (req,res) => {
      await updateSubscription(req.params.accountId,req.query, 
        (status,returnData) => {
          res.status(status).json(returnData);
        })
    })
}