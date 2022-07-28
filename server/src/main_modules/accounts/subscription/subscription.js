const mongoose = require('mongoose');
const { getDefinedFields } = require('../../../utils/get-defined-fields');
const {r_date} = require('../../../utils/types/mongo-required')

const subscriptionSchema = new mongoose.Schema({
  subscriptionDate: {...r_date, default: '2022'},
  expiryDate: {...r_date, default: '2022'}
})

function parseSubscriptionData(fields) {
  const df = getDefinedFields({
    subscriptionDate: fields.subscriptionDate,
    expiryDate: fields.expiryDate
  });

  const fieldsToUpdate = {
    ...(df.subscriptionDate && {"subscription.subscriptionDate": df.subscriptionDate}),
    ...(df.expiryDate && {"subscription.expiryDate": df.expiryDate}),
  }
  return fieldsToUpdate;
}


module.exports = {subscriptionSchema,parseSubscriptionData}