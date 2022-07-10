const { UserGoal } = require("../goals/models");
const { UserTransaction } = require("../transactions/models");
const { UserReport } = require("../reports/models");
const { Account, Review } = require("./models");
const { parseProfileData } = require("./profile/profile");
const { parseSubscriptionData } = require("./subscription/subscription");
const { getDefinedFields, fieldsAreNotNull } = require("../../utils/get-defined-fields");

module.exports = {
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
      
      if (!isAccountant) {
        console.log('creating goals/transaction/reports document')
        const userTransaction = new UserTransaction({userId: newAccount.accountId});
        const userGoal = new UserGoal({userId: newAccount.accountId});
        const userReport = new UserReport({userId: newAccount.accountId});
        await userTransaction.save();
        await userGoal.save();
        await userReport.save();
      }
      return callback(200,newAccount);
    } catch (err) {
      console.log(err);
      return callback(400, err);
    }
    
  },
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
  findAccountants: async (callback) => {
    try {
      const foundAccounts = await Account.find({isAccountant: true});
      callback(200,foundAccounts)
    } catch (err){
      console.log(err);
      callback(400,err);
    }
  },
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

  createReview: async (accountantId,fields,callback) => {
    try {
      const df = getDefinedFields(fields);
      const {authorId,rating,date,title,content} = df;
      console.log(date);
      if (!fieldsAreNotNull({authorId,date,rating,title})) { 
        return callback(400,'missing params');
      }
      const newReview = new Review({
        authorId,accountantId,date,rating,title,content
      });
  
      const account = await Account.findOneAndUpdate(
        {accountId: accountantId},
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
  createSubscription: async (id,fields,callback) => {
    try {
      const {subscriptionDate,expiryDate} = fields;
      const fieldsToUpdate = parseSubscriptionData({subscriptionDate,expiryDate});
  
      const account = await Account.findOneAndUpdate(
        {accountId: id},
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
  updateSubscription: async (id,fields,callback) => {
    try {
      const {expiryDate} = fields;
      const fieldsToUpdate = parseSubscriptionData({expiryDate})
      const account = await Account.findOneAndUpdate(
        {accountId: id},
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