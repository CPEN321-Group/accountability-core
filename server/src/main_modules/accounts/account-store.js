const { Account } = require("./models");
const { parseProfileData } = require("./profile/profile");

/**
 * Note: callback is used instead of returning the account because Mongodb queries use promises.
 * Returning the account would return a promise or null.
 */
module.exports = {
  /**
   * Saves a new account into database.
   * 
   * @param {Object} accountData - should contain at min the required & non-defaulted parameters in the Account model
   * @param {function} callback - callback to run on the created account
   * - @param {Error} err - potential error thrown by mongoose
   * - @param {Account} createdAccount - the account that was successfully created
   */
  createAccount: (accountData,callback) => {
    const account = new Account({...accountData});
    account.save((err,createdAccount) => callback(err,createdAccount));
  },
  /**
   * Wrapper for mongoose findById.
   * @param {string} id 
   * @param {function} callback - run on query result
   * - @param {Error} err - error thrown by mongoose
   * - @param {Account} foundAccount - account found by query
   */
  findAccountById:(id,callback) =>{
    Account.findById(id, (err,foundAccount) => callback(err,foundAccount));
  },
  /**
   * Wrapper for mongoose findByIdAndUpdate
   * @param {string} id 
   * @param {object} data - must contain fields: firsname,lastname,email,age,profession,hasAccountant
   * @param {*} callback 
   */
  updateProfile:(id,data,callback) =>{
    const {firstname,lastname,email,age,profession,hasAccountant} = data;
    
    const fieldsToUpdate = parseProfileData({firstname,lastname,email,age,profession,hasAccountant})
    Account.findByIdAndUpdate(id,{$set: fieldsToUpdate},
      {returnDocument: 'after'},
      (err,foundAccount) => {
        if(err) console.log(err);
        callback(err,foundAccount)
      }
    );
  },
  
}