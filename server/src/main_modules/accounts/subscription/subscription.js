const mongoose = require('mongoose');
const { getDefinedFields } = require('../../../utils/get-defined-fields');
const {r_string,r_bool,r_num, r_date} = require('../../../utils/types/mongo-required')

const subscriptionSchema = new mongoose.Schema({
  subscriptionDate: {...r_date, default: '2022'},
  expiryDate: {...r_date, default: '2022'}
})

function parseSubscriptionData(fields) {
  const {subscriptionDate,expiryDate} = fields;
  const df = getDefinedFields({subscriptionDate,expiryDate});

  const fieldsToUpdate = {
    ...(df.subscriptionDate && {"subscription.subscriptionDate": df.subscriptionDate}),
    ...(df.expiryDate && {"subscription.expiryDate": df.expiryDate}),
  }
  return fieldsToUpdate;
}


module.exports = {subscriptionSchema,parseSubscriptionData}