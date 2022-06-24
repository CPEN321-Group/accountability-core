const { Account } = require("./models");
const jwt = require('jsonwebtoken');


/**
 * Authenticates the user if provided token is valid.
 * @param {string} token 
 * @param {string} accountId 
 * @returns the account if successfully authenticated, otherwise profile only
 */
const authenticate = (token, accountId, callback) => {
  Account.findById(accountId, (err,foundAccount) => {
    if (err || !foundAccount) return callback(err,null);
    if (!tokenIsValid(token,foundAccount.id)) {
      //disable requirement for token whiles testing
      // return callback(err,foundAccount);
      return callback(new Error('invalid token'),{profile: foundAccount.profile});
    } else {
      return callback(err,foundAccount);
    }
  }) 
}

/**
 * Generate a JWT token with data as payload. Token expires in 1 hour for now.
 * @param {any} data 
 * @returns generated token string
 */
const generateToken = (data) => {
  const token = jwt.sign({ 
    data: data,
  },process.env.JWT_SECRET, { expiresIn: '7d' });
  return token;
}
/**
 * @param {string} token 
 * @param {any} data 
 * @returns true if token is valid, else false
 */
const tokenIsValid = (token,data) => {
  // console.log(`token: ${token} \ndata: ${data}`)
  try {
    const payload = jwt.verify(token, process.env.JWT_SECRET);
    return payload.data === data;
  } catch(err) {
    return false;
  }
}
module.exports = {authenticate,generateToken,tokenIsValid}