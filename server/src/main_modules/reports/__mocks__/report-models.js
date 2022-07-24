const mongoose = require('mongoose');
const {r_string,r_num,r_date} = require('../../../utils/types/mongo-required');
const { goalSchema } = require('../../goals/goal-models');
const { transactionSchema } = require('../../transactions/transaction-models');
const reportDB = mongoose.createConnection((process.env.MONGO_BASE_URL || 'mongodb://localhost') + '/reportDB')

const reportSchema = new mongoose.Schema({
  monthYear: r_date,
  income: [transactionSchema],
  spendings: [transactionSchema],
  savings: {
    ...r_num,
    min: 0
  },
  goalsInProgress: [goalSchema],
  recommendations: String
})

const userReportSchema = new mongoose.Schema({
  userId: r_string,
  accountantId: String,
  reports: {type: [reportSchema],default: []}
})

const Report = reportDB.model('Report', reportSchema);
const UserReport = reportDB.model('UserReport', userReportSchema);

module.exports = {UserReport,Report};