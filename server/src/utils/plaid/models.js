const mongoose = require('mongoose');
const plaidDB = mongoose.createConnection('mongodb://localhost/plaidDB')

const {r_string,r_bool,r_num, r_date} = require.main.require('./utils/types/mongo-required')

const plaidDataSchema = new mongoose.Schema({
  accessToken: String,
  itemId: String,
  paymentId: String,
  transferId: String
})
const plaidUserSchema = new mongoose.Schema({
  userId: r_string,
  data: {type: plaidDataSchema, required: true}
})

const PlaidUser = plaidDB.model('PlaidUser', plaidUserSchema);

module.exports = {PlaidUser};