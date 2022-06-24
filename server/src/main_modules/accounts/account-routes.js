const { getDefinedFields } = require("../../utils/get-defined-fields");
const { generateToken, tokenIsValid, authenticate } = require("./account-auth");
const { createAccount, findAccountById, updateProfile } = require("./account-store");
const { Account } = require("./models");

const _ = require.main.require('./utils/tests/model-samples')

/**
 * Status Codes: 
 * 400: bad request
 * 200: success
 * 204: no content is to be sent back
 * 404: not found
 * 408: request timeout
 * 501: not implemented (endpoint)
 * 500: internal server error
 */
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
        return res.send(foundAccount);
      });
    })
    .put((req,res,next) => {
      const {accountId} = req.params;
      const {token} = req.query;
      
      authenticate(token,accountId, (err,foundAccount) => {
        if (err) return next(err);
        updateProfile(foundAccount.id,req.query, (err,foundAccount) => {
          if (err) return next(err);
          return res.send(foundAccount)
        })
      })
    })
    .delete((req,res) => {
      const {accountId} = req.params;
      const {token} = req.query;
      res.send(req.params);
    })

  app.route('/reviews/:accountId')
    .get((req,res) => {
      const {accountId} = req.params;
      res.send(req.params);
    })
    .post((req,res) => {
      const {accountId} = req.params;
      const {token,authorId,rating,title,content} = req.query;
      res.send(req.params);
    })
}