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
  createAccount: async (data,callback) => {
    const foundAccount = await Account.findOne({accountId: data.accountId});
    if (foundAccount) return callback(new Error('account already exists'),foundAccount);

    const isAccountant = (data.isAccountant === 'true');
    const account = new Account({...data, isAccountant}); //cast to boolean as http can only send string
    
    account.save((err,account) => {
      if (!isAccountant && !err) {
        console.log('creating goals/transaction/reports document')
        const userTransaction = new UserTransaction({userId: data.accountId});
        const userGoal = new UserGoal({userId: data.accountId});
        const userReport = new UserReport({userId: data.accountId})
        userTransaction.save(err => err && console.log(err));
        userGoal.save(err => err && console.log(err));
        userReport.save(err => err && console.log(err));
      }
      
      callback(err,account);
    });
  },
  /**
   * Wrapper for mongoose findOneAndUpdate
   * @param {string} id 
   * @param {object} data - must contain fields: firsname,lastname,email,age,profession,hasAccountant
   * @param {*} callback 
   */
  updateProfile:(id,data,callback) => {
    const {avatar,firstname,lastname,email,age,profession} = data;
    
    const fieldsToUpdate = parseProfileData({avatar,firstname,lastname,email,age,profession})
    Account.findOneAndUpdate({accountId: id},{$set: fieldsToUpdate},
      {returnDocument: 'after'},
      (err,account) => {
        callback(err,account)
      }
    );
  },
  deleteAccount: async (id,callback) => {
    const account = await Account.findOne({accountId: id});
    await Account.deleteOne({accountId: id});
    await UserGoal.deleteOne({userId: id})
    await UserTransaction.deleteOne({userId: id});
    await UserReport.deleteOne({userId:id});
    return account;
  },

  createReview: (accountantId,data,callback) => {
    const {authorId,date,rating,title,content} = data;
    
    const newReview = {authorId,date,rating,title,content};

    Account.findOneAndUpdate({accountId: accountantId},{$push: {reviews: newReview}},
      {returnDocument: 'after'},
      (err,account) => {
        if(err) console.log(err);
        callback(err,account)
      }
    );
  },
  createSubscription: (id,data,callback) => {
    const {subscriptionDate,expiryDate} = data;
    const fieldsToUpdate = parseSubscriptionData({subscriptionDate,expiryDate})
    Account.findOneAndUpdate({accountId: id},{$set: fieldsToUpdate},
      {returnDocument: 'after'},
      (err,foundAccount) => {
        if(err) console.log(err);
        callback(err,foundAccount)
      }
    );
  },
  updateSubscription: (id,data,callback) => {
    const {expiryDate} = data;
    const fieldsToUpdate = parseSubscriptionData({expiryDate})
    Account.findOneAndUpdate({accountId: id},{$set: fieldsToUpdate},
      {returnDocument: 'after'},
      (err,foundAccount) => {
        if(err) console.log(err);
        callback(err,foundAccount)
      }
    );
  }
  
}