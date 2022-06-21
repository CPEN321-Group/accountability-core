const mongoose = require('mongoose');
const accountDB = mongoose.createConnection('mongodb://localhost/accountDB')

const {r_string,r_bool,r_num, r_date} = require.main.require('./utils/types/mongoRequired')

const plaidDataSchema = new mongoose.Schema({
  accessToken: r_string,
  publicToken: r_string,
  item_id: r_string,
  paymentId: String,
  transferId: String
})
const plaidUserSchema = new mongoose.Schema({
  userId: r_string,
  data: {type: plaidDataSchema, required: true}
})

const PlaidUser = new mongoose.model('PlaidUser', plaidUserSchema);

module.exports = {PlaidUser};