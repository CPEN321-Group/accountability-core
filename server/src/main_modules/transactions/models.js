const mongoose = require('mongoose');
const transactionDB = mongoose.createConnection('mongodb://localhost/transactionDB')
const {r_string,r_bool,r_num,r_date} = require.main.require('./utils/types/mongoRequired')
const Frequency = require.main.require('./utils/types/frequency');

const transactionSchema = new mongoose.Schema({
  id: r_string,
  goal: r_string,
  category: r_string,
  date: r_date,
  memo: r_string,
  frequency: {...r_string, enum: Frequency}
})

const userTransactionSchema = new mongoose.Schema({
  userId: r_string,
  transactions: [transactionSchema]
})

const UserTransaction = new mongoose.model('UserTransaction',userTransactionSchema)


module.exports = {UserTransaction};