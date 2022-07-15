const { UserGoal } = require("../goals/models");
const { UserTransaction } = require("../transactions/models");
const { UserReport } = require("../reports/models");
const { Account, Review } = require("./models");
const { parseProfileData } = require("./profile/profile");
const { parseSubscriptionData } = require("./subscription/subscription");
const { getDefinedFields, fieldsAreNotNull } = require("../../utils/get-defined-fields");

/**
 * Interface between endpoints and mongodb database. Each function defined will perform a CRUD operation on the accountDB
 */
module.exports = {
  /**
   * @param {object} fields - requires accountId,firstname,lastname,email,age,profession,isAccountant to be defined and of the right type
   * @param {function} callback - is called with response status and data
   */
  createAccount: async (fields,callback) => {
    try {
      const df = getDefinedFields(fields);
      const {accountId,avatar,firstname,lastname,email,age,profession,isAccountant} = df;
      if (!fieldsAreNotNull({accountId,firstname,lastname,email,age,profession,isAccountant})) {
        return callback(400,'missing params');
      }
      const isAct = (isAccountant === 'true');

      const foundAccount = await Account.findOne({accountId: accountId});
      if (foundAccount) {
        return callback(400,'account already exists');
      }
      
      const newAccount = new Account({
        accountId,
        profile: { avatar,firstname,lastname,email,age,profession},
        isAccountant: isAct //cast to boolean as query can only send string
      }); 
      await newAccount.save();
      
      if (!isAct) {
        console.log('creating goals/transaction/reports document')
        const userTransaction = new UserTransaction({userId: newAccount.accountId});
        const userGoal = new UserGoal({userId: newAccount.accountId});
        const userReport = new UserReport({userId: newAccount.accountId});
        const t = await userTransaction.save();
        const g = await userGoal.save();
        const r = await userReport.save();
      }
      return callback(200,newAccount);
    } catch (err) {
      console.log(err);
      return callback(400, err);
    }
    
  },
  /**
   * @param {string} accountId - account id string
   * @param {function} callback - is called with response status and data
   */
  findAccount: async (accountId,callback) => {
    try {
      const account = await Account.findOne({accountId: accountId});
      if (!account) return callback(404,'account not found');
      return callback(200,account);
    } catch (err) {
      console.log(err);
      return callback(400, err);
    }
    
  },
  /**
   * @param {function} callback - is called with response status and data
   */
  findAccountants: async (callback) => {
    try {
      const foundAccounts = await Account.find({isAccountant: true});
      callback(200,foundAccounts)
    } catch (err){
      console.log(err);
      callback(400,err);
    }
  },
  /**
   * @param {string} id - account id
   * @param {object} data - assume defined data fields (avatar,firstname,lastname,email,age,profession)
   * are not-empty and valid strings, valid age
   * @param {function} callback - is called with response status and data
   */
  updateProfile: async (id,data,callback) => {    
    try {
      const {avatar,firstname,lastname,email,age,profession} = data;

      const fieldsToUpdate = parseProfileData({avatar,firstname,lastname,email,age,profession});
      const account = await Account.findOneAndUpdate(
        {accountId: id},
        {$set: fieldsToUpdate},
        {returnDocument: 'after'}
      );
      if (!account) return callback(404,'account not found');
      return callback(200,account);
    } catch (err) {
      console.log(err);
      return callback(400,err);
    }
    
  },
  /**
   * @param {string} id - account id
   * @param {function} callback - is called with response status and data
   */
  deleteAccount: async (id,callback) => {
    try {
      const account = await Account.findOneAndDelete({accountId: id});
      if (!account) return callback(404,'account not found');

      await UserGoal.deleteOne({userId: id})
      await UserTransaction.deleteOne({userId: id});
      await UserReport.deleteOne({userId:id});
      return callback(200,'account deleted');
    } catch (err) {
      console.log(err);
      return callback(400,err);
    }
  },
  /**
   * @param {string} accountantId - account id
   * @param {object} fields - assume required fields (authorId,date,rating,title) are defined and are not-empty and valid strings
   * @param {function} callback - is called with response status and data
   */
  createReview: async (accountantId,fields,callback) => {
    try {
      const df = getDefinedFields(fields);
      const {authorId,rating,date,title,content} = df;
      if (!fieldsAreNotNull({authorId,date,rating,title})) { 
        return callback(400,'missing params');
      }
      const newReview = new Review({
        authorId,accountantId,date,rating,title,content
      });
  
      const account = await Account.findOneAndUpdate(
        {$and:[{accountId: accountantId}, {isAccountant: true}]},
        {$push: {reviews: newReview}},
        {returnDocument: 'after'},
      );
      if (!account) return callback(404,'accountant not found');
      return callback(200,account);
    } catch (err) {
      console.log(err);
      return callback(400,err);
    }
  },
  /**
   * Set the subcription and expiry date of the subcription
   * @param {string} id - account id
   * @param {object} fields - assume required fields (expiryDate,subscriptionDate) are defined and are not-empty and valid strings
   * @param {function} callback - is called with response status and data
   */
  createSubscription: async (id,fields,callback) => {
    try {
      const {subscriptionDate,expiryDate} = fields;
      if (!fieldsAreNotNull({subscriptionDate,expiryDate})) {
        return callback(400,'missing params');
      }
      const fieldsToUpdate = parseSubscriptionData({subscriptionDate,expiryDate});
  
      const account = await Account.findOneAndUpdate(
        {$and:[{accountId: id}, {isAccountant: false}]},
        {$set: fieldsToUpdate},
        {returnDocument: 'after'},
      );
      if (!account) return callback(404,'account not found');
      return callback(200,account);
    } catch (err) {
      console.log(err);
      return callback(400,err);
    }
  },
  /**
   * Modify the expiry date of the current subscription.
   * @param {string} id - account id
   * @param {object} fields - assume required fields (expiryDate) are defined and are not-empty and valid strings
   * @param {function} callback - is called with response status and data
   */
  updateSubscription: async (id,fields,callback) => {
    try {
      const {expiryDate} = fields;
      if (!fieldsAreNotNull({expiryDate})) {
        return callback(400,'missing params');
      }
      const fieldsToUpdate = parseSubscriptionData({expiryDate})
      const account = await Account.findOneAndUpdate(
        {$and:[{accountId: id}, {isAccountant: false}]},
        {$set: fieldsToUpdate},
        {returnDocument: 'after'},
      );
      if (!account) return callback(404,'account not found');
      return callback(200,account);
    } catch (err) {
      console.log(err);
      return callback(400,err);
    }
  }
  
}