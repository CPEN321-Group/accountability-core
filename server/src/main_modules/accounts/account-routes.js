require('dotenv').config();
const { getDefinedFields } = require.main.require("./utils/get-defined-fields");
const { fieldsAreNotNull } = require("../../utils/get-defined-fields");
const { generateToken, authenticate } = require("./account-auth");
const { createAccount, findAccountById, updateProfile, createReview, deleteAccount, createSubscription, updateSubscription } = require("./account-store");
const { Account } = require('./models');

const _ = require.main.require('./utils/tests/model-samples')

module.exports = function(app) {
  app.route('/accounts')
    .post(async (req,res,next) => {
      const df = getDefinedFields(req.body);
      const {accountId,avatar,firstname,lastname,email,age,profession,isAccountant} = df;
      if (!fieldsAreNotNull({accountId,firstname,lastname,email,age,profession,isAccountant})) {
        return next(new Error('missing params'));
      }
      await createAccount({
        accountId,
        profile: { avatar,firstname,lastname,email,age,profession},
        isAccountant
      }, (err,newAccount) => {
        if (err) { return next(err); }
        res.json(newAccount)
        
      })
    });

  app.get('/accounts/accountants',async (req,res,next) => { //fetch all accountants
    try {
      const foundAccounts = await Account.find({isAccountant: true});
      res.status(200).json(foundAccounts);
    } catch (err) {
      res.status(500).json(err);
    }
  })

  app.route('/accounts/:accountId')
    .get((req,res,next) => {
      Account.findOne({accountId: req.params.accountId},(err,account) => {
        if (err) return next(err);
        if (!account) return res.status(404).end('account not found');
        return res.json(account);
      })
    })
    .put((req,res,next) => {
      updateProfile(req.params.accountId,req.body, (err,account) => {
        if (err) return next(err);
        if (!account) return res.status(404).end('account not found');
        return res.json(account)
      })
    })
    .delete(async (req,res,next) => {
      const deletedAccount = await deleteAccount(req.params.accountId);
      if (!deletedAccount) return res.status(404).end('account not found')
      return res.end('account deleted');
    })

  app.route('/reviews/:accountantId')
    .post((req,res,next) => {
      const {accountantId} = req.params;
      const df = getDefinedFields(req.body);
      const {authorId,date,rating,title,content} = df;
      if (!fieldsAreNotNull({authorId,date,rating,title})) { return next(new Error('missing params'))}
      createReview(accountantId,df, (err,account) => {
        if (err) return next(err);
        if (!account) return res.status(404).end('account not found');
        return res.json(account)
      })
    })
  app.route('/subscription/:accountId')
    .post((req,res,next) => {
      const {accountId} = req.params;
      createSubscription(accountId,req.body, (err,account) => {
        if (err) return next(err);
        if (!account) return res.status(404).end('account not found');
        return res.json(account)
      })
    })
    .put((req,res,next) => {
      const {accountId} = req.params;
      updateSubscription(accountId,req.body, (err,account) => {
        if (err) return next(err);
        if (!account) return res.status(404).end('account not found');
        return res.json(account)
      })
    })
}