const mongoose = require('mongoose');
const { goalSchema } = require('../goals/models');
const { transactionSchema } = require('../transactions/models');
const reportDB = mongoose.createConnection('mongodb://localhost/reportDB')
const {r_string,r_bool,r_num,r_date} = require.main.require('./utils/types/mongo-required')

const reportSchema = new mongoose.Schema({
  monthYear: r_date,
  income: [transactionSchema],
  spendings: [transactionSchema],
  savings: r_num,
  goalsInProgress: [goalSchema],
  recommendations: String
})

const userReportSchema = new mongoose.Schema({
  userId: r_string,
  accountantId: String,
  reports: {type: [reportSchema],default: []}
})

const UserReport = reportDB.model('UserReport', userReportSchema);

module.exports = {UserReport};