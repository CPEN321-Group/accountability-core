const mongoose = require('mongoose');
const accountDB = mongoose.createConnection('mongodb://localhost/accountDB')

const {r_string,r_bool,r_num, r_date} = require.main.require('./utils/types/mongoRequired')

const profileSchema = new mongoose.Schema({
  firstname: r_string,
  lastname: r_string,
  email: r_string,
})
const reviewSchema = new mongoose.Schema({
  authorId: r_string,
  date: r_date,
  rating: r_num,
  content: r_string
})

const userSchema = new mongoose.Schema({
  id: r_string,
  subscribed: r_bool,
  enabledNotifications: r_bool,
  secretKey: r_string,
  profile: profileSchema
})

const accountantSchema = new mongoose.Schema({
  id: r_string,
  secretKey: r_string,
  enabledNotifications: r_bool,
  reviews: [reviewSchema],
  profile: profileSchema
})

const User = accountDB.model('User', userSchema);
const Accountant = accountDB.model('Accountant', accountantSchema);

module.exports = {User,Accountant};