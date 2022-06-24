const mongoose = require('mongoose');
const { profileSchema } = require('./profile/profile');
const { reviewSchema } = require('./review/review');
const { subscriptionSchema } = require('./subscription/subscription');
const accountDB = mongoose.createConnection('mongodb://localhost/accountDB')

const {r_string,r_bool,r_num, r_date} = require.main.require('./utils/types/mongo-required')
const accountSchema = new mongoose.Schema({
  profile: {type: profileSchema, required: true},
  isAccountant: r_bool,
  isAuthenticated: {...r_bool, default: false},
  authenticateExpiryDate: {...r_date, default: '2022'},
  reviews: {type: [reviewSchema], default: []},
  subscription: subscriptionSchema
})

const Account = accountDB.model('Account', accountSchema);

module.exports = {Account};