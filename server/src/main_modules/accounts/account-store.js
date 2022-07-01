const { UserGoal } = require("../goals/models");
const { UserTransaction } = require("../transactions/models");
const { UserReport } = require("../reports/models");
const { Account } = require("./models");
const { parseProfileData } = require("./profile/profile");
const { parseReviewData } = require("./review/review");
const { parseSubscriptionData } = require("./subscription/subscription");

/**
 * Note: callback is used instead of returning the account because Mongodb queries use promises.
 * Returning the account would return a promise or null.
 */
module.exports = {
  /**
   * Saves a new account into database.
   * 
   * @param {Object} data - should contain at min the required & non-defaulted parameters in the Account model
   * @param {function} callback - callback to run on the created account
   * - @param {Error} err - potential error thrown by mongoose
   * - @param {Account} createdAccount - the account that was successfully created
   */
  createAccount: (data,callback) => {
    const account = new Account({...data});
    
    account.save((err,createdAccount) => {
      const userTransaction = new UserTransaction({userId:createdAccount.id});
      const userGoal = new UserGoal({userId: createdAccount.id});
      const userReport = new UserReport({userId: createdAccount.id})
      userTransaction.save();
      userGoal.save();
      userReport.save();
      callback(err,createdAccount);
    });
  },
  /**
   * Wrapper for mongoose findById.
   * @param {string} id 
   * @param {function} callback - run on query result
   * - @param {Error} err - error thrown by mongoose
   * - @param {Account} foundAccount - account found by query
   */
  findAccountById:(id,callback) => {
    Account.findById(id, (err,foundAccount) => callback(err,foundAccount));
  },
  /**
   * Wrapper for mongoose findByIdAndUpdate
   * @param {string} id 
   * @param {object} data - must contain fields: firsname,lastname,email,age,profession,hasAccountant
   * @param {*} callback 
   */
  updateProfile:(id,data,callback) => {
    const {avatar,firstname,lastname,email,age,profession,hasAccountant} = data;
    
    const fieldsToUpdate = parseProfileData({avatar,firstname,lastname,email,age,profession,hasAccountant})
    Account.findByIdAndUpdate(id,{$set: fieldsToUpdate},
      {returnDocument: 'after'},
      (err,foundAccount) => {
        if(err) console.log(err);
        callback(err,foundAccount)
      }
    );
  },
  deleteAccount: (id,callback) => {
    Account.deleteOne({id: id}, (err) => {
      if (err) console.log(err);
      callback(err);
    });
  },

  createReview: (accountId,data,callback) => {
    const {authorId,date,rating,title,content} = data;
    
    const newReview = {authorId,date,rating,title,content};

    Account.findByIdAndUpdate(accountId,{$push: {reviews: newReview}},
      {returnDocument: 'after'},
      (err,foundAccount) => {
        if(err) console.log(err);
        callback(err,foundAccount)
      }
    );
  },
  createSubscription: (accountId,data,callback) => {
    const {subscriptionDate,expiryDate} = data;
    const fieldsToUpdate = parseSubscriptionData({subscriptionDate,expiryDate})
    Account.findByIdAndUpdate(accountId,{$set: fieldsToUpdate},
      {returnDocument: 'after'},
      (err,foundAccount) => {
        if(err) console.log(err);
        callback(err,foundAccount)
      }
    );
  },
  updateSubscription: (accountId,data,callback) => {
    const {expiryDate} = data;
    const fieldsToUpdate = parseSubscriptionData({expiryDate})
    Account.findByIdAndUpdate(accountId,{$set: fieldsToUpdate},
      {returnDocument: 'after'},
      (err,foundAccount) => {
        if(err) console.log(err);
        callback(err,foundAccount)
      }
    );
  }
  
}