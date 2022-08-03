const mongoose = require('mongoose');
const { r_date, r_bool, r_string,r_num } = require('../../../utils/types/mongo-required');
const transactionDB = mongoose.createConnection((process.env.MONGO_BASE_URL || 'mongodb://localhost') + '/transactionDB')

const transactionSchema = new mongoose.Schema({
  title: r_string,
  category: r_string,
  date: r_date,
  amount: r_num,
  isIncome: r_bool
})

const userTransactionSchema = new mongoose.Schema({
  userId: r_string,
  transactions: [transactionSchema]
})

const Transaction = transactionDB.model('Transaction',transactionSchema);
const UserTransaction = transactionDB.model('UserTransaction',userTransactionSchema)


module.exports = {UserTransaction, transactionSchema,Transaction};
