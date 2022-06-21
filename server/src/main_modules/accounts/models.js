const mongoose = require('mongoose');
const accountDB = mongoose.createConnection('mongodb://localhost/accountDB')

const {r_string,r_bool,r_num, r_date} = require.main.require('./utils/types/mongoRequired')

const profileSchema = new mongoose.Schema({
  firstname: r_string,
  lastname: r_string,
  email: r_string,
  age: r_num,
  profession: r_string
})
const reviewSchema = new mongoose.Schema({
  authorId: r_string,
  date: r_date,
  rating: r_num,
  title: r_string,
  content: r_string
})

const accountSchema = new mongoose.Schema({
  secretKey: r_string,
  profile: profileSchema,
  type: {
    type: String,
    enum: ['user', 'accountant'],
    default: 'user'
  },
  subscribed: Boolean,
  subscriptionDate: String,
  hasAccountant: Boolean,
  reviews: [reviewSchema]
})

const Account = accountDB.model('Account', accountSchema);

module.exports = {Account};