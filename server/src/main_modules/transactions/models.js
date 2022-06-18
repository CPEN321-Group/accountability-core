const mongoose = require('mongoose');
const transactionDB = mongoose.createConnection('mongodb://localhost/transactionDB')
const {r_string,r_bool,r_num,r_date} = require.main.require('./utils/types/mongoRequired')

const transactionSchema = new mongoose.Schema({
  id: r_string,
  title: r_string,
  category: r_string,
  date: r_date,
  amount: r_num,
  isIncome: r_bool,
  receipt: String
})

const userTransactionSchema = new mongoose.Schema({
  userId: r_string,
  transactions: [transactionSchema]
})

const UserTransaction = new mongoose.model('UserTransaction',userTransactionSchema)


module.exports = {UserTransaction, transactionSchema};