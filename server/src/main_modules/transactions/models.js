const mongoose = require('mongoose');
const transactionDB = mongoose.createConnection((process.env.MONGO_BASE_URL || 'mongodb://localhost') + '/transactionDB')
const {r_string,r_bool,r_num,r_date} = require.main.require('./utils/types/mongo-required')

const transactionSchema = new mongoose.Schema({
  title: r_string,
  category: r_string,
  date: {...r_date, default: new Date()},
  amount: r_num,
  isIncome: {...r_bool,default: false},
  receipt: String,
  plaidTransactionId: String
})

const userTransactionSchema = new mongoose.Schema({
  userId: r_string,
  transactions: {type: [transactionSchema], default: []}
})

const UserTransaction = transactionDB.model('UserTransaction',userTransactionSchema)


module.exports = {UserTransaction, transactionSchema};
