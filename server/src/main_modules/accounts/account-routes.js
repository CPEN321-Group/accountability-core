const { getDefinedFields } = require("../../utils/get-defined-fields");
const { generateToken, tokenIsValid, authenticate } = require("./account-auth");
const { createAccount, findAccountById, updateProfile, createReview, deleteAccount, createSubscription, updateSubscription } = require("./account-store");
const { Account } = require("./models");

const _ = require.main.require('./utils/tests/model-samples')

module.exports = function(app) {
  app.route('/accounts')
    .post((req,res,next) => {
      const df = getDefinedFields(req.query);
      const {firstname,lastname,email,age,profession,isAccountant} = df;
      if (!firstname || !lastname || !email || !age || !profession || !isAccountant) {
        return next(err);
      }
      createAccount({
        profile: { firstname,lastname,email,age,profession},
        isAccountant: isAccountant
      }, (err,newAccount) => {
        if (err) { return next(err); }
        res.json({
          token: generateToken(newAccount.id), //will be changed so frontend generates token
          data: newAccount
        });
      })
    });

  app.route('/accounts/:accountId')
    .get((req,res,next) => {
      const {accountId} = req.params;
      const {token} = req.query;
      authenticate(token,accountId, (err,foundAccount) => {
        if (err) return next(err)
        if (!foundAccount) return next(new Error('account not found'));
        return res.json(foundAccount);
      });
    })
    .put((req,res,next) => {
      const {accountId} = req.params;
      const {token} = req.query;
      
      authenticate(token,accountId, (err,foundAccount) => {
        if (err) return next(err);
        if (!foundAccount)  return next(new Error('account not found'))
        updateProfile(foundAccount.id,req.query, (err,foundAccount) => {
          if (err) return next(err);
          return res.json(foundAccount)
        })
      })
    })
    .delete((req,res,next) => {
      const {accountId} = req.params;
      const {token} = req.query;
      authenticate(token,accountId, (err,foundAccount) => {
        if (err) return next(err);
        if(!foundAccount) return next(new Error('account not found'));
        deleteAccount(foundAccount.id, (err) => {
          if (err) return next(err);
          return res.end('account deleted');
        })
      })
    })

  app.route('/reviews/:accountantId')
    .post((req,res,next) => {
      const {accountantId} = req.params;
      const {token,authorId,date,rating,title,content} = req.query;
      const df = getDefinedFields({accountantId: accountantId,authorId,date,rating,title,content})
      if (!df.authorId || !df.date || !df.rating || !df.title) { return next(err)}
      authenticate(token,authorId, (err,foundAccount) => {
        if (err) return next(err);
        if (!foundAccount)  return next(new Error('accountant not found'))
        createReview(foundAccount.id,{accountId: accountantId,...df}, (err,foundAccount) => {
          if (err) return next(err);
          return res.json(foundAccount)
        })
      })
    })
  app.route('/subscription/:accountId')
    .post((req,res,next) => {
      const {accountId} = req.params;
      const {token, subscriptionDate,expiryDate} = req.query;
      authenticate(token,accountId, (err,foundAccount) => {
        if (err) return next(err);
        if (!foundAccount) return next(new Error('account not found'))
        createSubscription(foundAccount.id,req.query, (err,foundAccount) => {
          if (err) return next(err);
          return res.json(foundAccount)
        })
      })
    })
    .put((req,res,next) => {
      const {accountId} = req.params;
      const {token, expiryDate} = req.query;
      authenticate(token,accountId, (err,foundAccount) => {
        if (err) return next(err);
        if (!foundAccount) return next(new Error('account not found'))
        updateSubscription(foundAccount.id,req.query, (err,foundAccount) => {
          if (err) return next(err);
          return res.json(foundAccount)
        })
      })
    })
}