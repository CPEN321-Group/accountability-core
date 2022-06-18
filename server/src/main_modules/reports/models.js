const mongoose = require('mongoose');
const { goalSchema } = require('../goals/models');
const { transactionSchema } = require('../transactions/models');
const reportDB = mongoose.createConnection('mongodb://localhost/reportDB')
const {r_string,r_bool,r_num,r_date} = require.main.require('./utils/types/mongoRequired')

const reportSchema = new mongoose.Schema({
  id: r_string,
  month: r_date,
  income: [transactionSchema],
  spendings: [transactionSchema],
  savings: r_num,
  goalsInProgress: [goalSchema],
  recommendations: r_string
})

const userReportSchema = new mongoose.Schema({
  userId: r_string,
  accountantId: r_string,
  reports: [reportSchema]
})

const UserReport = new mongoose.model('UserReport', userReportSchema);

module.exports = {UserReport};