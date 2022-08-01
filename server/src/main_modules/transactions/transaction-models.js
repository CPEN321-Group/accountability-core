const mongoose = require('mongoose');
const {r_string,r_num}= require('../../utils/types/mongo-required');
const transactionDB = mongoose.createConnection((process.env.MONGO_BASE_URL || 'mongodb://localhost') + '/transactionDB')

const transactionSchema = new mongoose.Schema({
  title: r_string,
  category: r_string,
  date: {
    type: Date, 
    required: true, 
    default: new Date(),
    validate: {
      validator: function(v) {
        return v.getTime() <= Date.now()
      },
      message: "Transaction date cannot be in the future"
    }
  },
  amount: {
    ...r_num,
    min: 0
  },
  isIncome: {type: Boolean, required: true,default: false},
  receipt: String,
  plaidTransactionId: String
})

const userTransactionSchema = new mongoose.Schema({
  userId: r_string,
  transactions: {type: [transactionSchema], default: []}
})

const Transaction = transactionDB.model('Transaction',transactionSchema);
const UserTransaction = transactionDB.model('UserTransaction',userTransactionSchema)


module.exports = {UserTransaction, transactionSchema,Transaction};
