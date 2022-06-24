const mongoose = require('mongoose');
const { getDefinedFields } = require('../../../utils/get-defined-fields');
const {r_string,r_bool,r_num, r_date} = require.main.require('./utils/types/mongo-required')

const profileSchema = new mongoose.Schema({
  firstname: r_string,
  lastname: r_string,
  email: r_string,
  age: r_num,
  profession: r_string,
  numberOfClients: Number,
  hasAccountant: {...r_bool, default: false},
})

/**
 * Parses profile data into the format required for updating
 * profile using mongoose using $set. Automatically removes unset fields and empty strings.
 * @param {object} fields - some or all of the fields in the profile schema
 */
function parseProfileData(fields) {
  const {firstname,lastname,email,age,profession,hasAccountant} = fields;
  const df = getDefinedFields({firstname,lastname,email,age,profession,hasAccountant});
  // console.log(`definedFields:`);
  // console.log(definedFields);
  const fieldsToUpdate = {
    ...(df.firstname && {"profile.firstname": df.firstname}),
    ...(df.lastname && {"profile.lastname": df.lastname}),
    ...(df.email && {"profile.email": df.email}),
    ...(df.age && {"profile.age": df.age}),
    ...(df.profession && {"profile.profession": df.profession}),
    ...(df.hasAccountant && {"profile.hasAccountant": df.hasAccountant}),
  }
  return fieldsToUpdate;
}

module.exports = {
  profileSchema, parseProfileData
}


