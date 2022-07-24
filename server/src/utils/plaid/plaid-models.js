const mongoose = require('mongoose');
const { r_string } = require('../types/mongo-required');
const plaidDB = mongoose.createConnection((process.env.MONGO_BASE_URL || 'mongodb://localhost') + '/plaidDB')

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