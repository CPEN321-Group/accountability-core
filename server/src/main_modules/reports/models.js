const mongoose = require('mongoose');
const reportDB = mongoose.createConnection('mongodb://localhost/reportDB')
const {r_string,r_bool,r_num,r_date} = require.main.require('./utils/types/mongoRequired')

const reportSchema = new mongoose.Schema({
  type: r_string,
  data: mongoose.Schema.Types.Mixed
})

const userReportSchema = new mongoose.Schema({
  userId: r_string,
  reports: [reportSchema]
})

const UserReport = new mongoose.model('UserReport', userReportSchema);

module.exports = {UserReport};