const mongoose = require('mongoose');
const { profileSchema } = require('./profile/profile');
const { reviewSchema } = require('./review/review');
const { subscriptionSchema } = require('./subscription/subscription');
const accountDB = mongoose.createConnection((process.env.MONGO_BASE_URL || 'mongodb://localhost') + '/accountDB')

const {r_string,r_bool} = require('../../utils/types/mongo-required')
const accountSchema = new mongoose.Schema({
  accountId: r_string,
  profile: {type: profileSchema, required: true},
  isAccountant: r_bool,
  reviews: {type: [reviewSchema], default: []},
  subscription: {type: subscriptionSchema, default: {subscriptionDate: '2022', expiryDate: '2022'}},
  stripeCustomerId: String,
  stripeSubscriptionId: String
})

const Account = accountDB.model('Account', accountSchema);
const Review = accountDB.model('Review', reviewSchema);

module.exports = {Account,Review};