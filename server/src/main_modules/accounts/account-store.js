const { UserGoal } = require("../goals/goal-models");
const { UserTransaction } = require("../transactions/transaction-models");
const { UserReport } = require("../reports/report-models");
const { Account, Review } = require("./account-models");
const { parseProfileData } = require("./profile/profile");
const { parseSubscriptionData } = require("./subscription/subscription");
const { getDefinedFields, fieldsAreNotNull } = require("../../utils/checks/get-defined-fields");
const { ValidationError, NotFoundError } = require("../../utils/errors");

/**
 * Interface between endpoints and mongodb database. Each function defined will perform a CRUD operation on the accountDB
 */
module.exports = {
  /**
   * @param {object} fields - requires accountId,firstname,lastname,email,age,profession,isAccountant to be defined and of the right type
   * @param {function} callback - is called with response status and data
   */
  createAccount: async (fields,callback) => {
    const df = getDefinedFields(fields);
    const {accountId,avatar,firstname,lastname,email,age,profession,isAccountant} = df;

    const isAct = (isAccountant === true || isAccountant === 'true');

    try {
      if (await Account.findOne({accountId})) {
        throw new ValidationError('account already exists');
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
        await userTransaction.save();
        await userGoal.save();
        await userReport.save();
      }
      return callback(null,200,newAccount);
    } catch (err) {
      console.log(err);
      return callback(null,400, err);
    }

  },
  /**
   * @param {string} accountId - account id string
   * @param {function} callback - is called with response status and data
   */
  findAccount: async (accountId,callback) => {
    try {
      const account = await Account.findOne({accountId});
      if (!account) return callback(null,404,new NotFoundError('account not found'));
      return callback(null,200,account);
    } catch (err) {
      return callback(null,400, err);
    }
    
  },
  /**
   * @param {function} callback - is called with response status and data
   */
  findAccountants: async (callback) => {
    try {
      const foundAccounts = await Account.find({isAccountant: true});
      callback(null,200,foundAccounts)
    } catch (err){
      callback(null,400,err);
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
        {returnDocument: 'after', runValidators: true}
      );
      if (!account) return callback(null,404,new NotFoundError('account not found'));
      return callback(null,200,account);
    } catch (err) {
      return callback(null,400,err);
    }
    
  },
  /**
   * @param {string} id - account id
   * @param {function} callback - is called with response status and data
   */
  deleteAccount: async (id,callback) => {
    
    try {
      const account = await Account.findOneAndDelete({accountId: id});
      if (!account) return callback(null,404,new NotFoundError('account not found'));

      await UserGoal.deleteOne({userId: id})
      await UserTransaction.deleteOne({userId: id});
      await UserReport.deleteOne({userId:id});
      return callback(null,200,'account deleted');
    } catch (err) {
      return callback(null,400,err);
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

      const newReview = new Review({
        authorId,accountantId,date,rating,title,content
      });
      const pushItem = {reviews: newReview};
      const account = await Account.findOneAndUpdate(
        {$and:[{accountId: accountantId}, {isAccountant: true}]},
        {$push: pushItem},
        {returnDocument: 'after', runValidators: true},
      );
      if (!account) return callback(null,404,new NotFoundError('accountant not found'));
      return callback(null,200,account);
    } catch (err) {
      return callback(null,400,err);
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
        throw new ValidationError('missing params');
      }
      const fieldsToUpdate = parseSubscriptionData({subscriptionDate,expiryDate});
  
      const account = await Account.findOneAndUpdate(
        {$and:[{accountId: id}, {isAccountant: false}]},
        {$set: fieldsToUpdate},
        {returnDocument: 'after', runValidators: true},
      );
      if (!account) return callback(null,404,new NotFoundError('account not found'));
      return callback(null,200,account);
    } catch (err) {
      return callback(null,400,err);
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
        throw new ValidationError('missing params');
      }
      const fieldsToUpdate = parseSubscriptionData({expiryDate})
      const account = await Account.findOneAndUpdate(
        {$and:[{accountId: id}, {isAccountant: false}]},
        {$set: fieldsToUpdate},
        {returnDocument: 'after', runValidators: true},
      );
      if (!account) return callback(null,404,new NotFoundError('account not found'));
      return callback(null,200,account);
    } catch (err) {
      return callback(null,400,err);
    }
  }
  
}