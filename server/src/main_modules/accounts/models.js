const mongoose = require('mongoose');
const accountDB = mongoose.createConnection('mongodb://localhost/accountDB')

const {r_string,r_bool,r_num} = require.main.require('./utils/types/mongoRequired')

const userSchema = new mongoose.Schema({
  firstname: r_string,
  lastname: r_string,
  email: r_string,
  id: r_string,
  subscribed: r_bool,
  secretKey: r_string,
})

const accountantSchema = new mongoose.Schema({
  firstname: r_string,
  lastname: r_string,
  email: r_string,
  id: r_string,
  secretKey: r_string,
  reviews: [{
    authorId: r_string,
    rating: r_num,
    content: r_string
  }]
})

const User = accountDB.model('User', userSchema);
const Accountant = accountDB.model('Accountant', accountantSchema);

module.exports = {User,Accountant};