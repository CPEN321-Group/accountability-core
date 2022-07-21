const mongoose = require('mongoose');
const { getDefinedFields } = require('../../../utils/checks/get-defined-fields');
const {r_string, letter_string,email_string, r_num} = require('../../../utils/types/mongo-required')

const profileSchema = new mongoose.Schema({
  avatar: String,
  firstname: letter_string,
  lastname: letter_string,
  email: {required: true, ...email_string},
  age: {
    ...r_num,
    min: 0,
    max: 200
  },
  profession: letter_string,
})

/**
 * Parses profile data into the format required for updating
 * profile using mongoose using $set. Automatically removes unset fields and empty strings.
 * @param {object} fields - some or all of the fields in the profile schema
 */
function parseProfileData(fields) {
  const {avatar,firstname,lastname,email,age,profession} = fields;
  const df = getDefinedFields({avatar,firstname,lastname,email,age,profession});

  const fieldsToUpdate = {
    ...(df.avatar && {"profile.avatar": df.avatar}),
    ...(df.firstname && {"profile.firstname": df.firstname}),
    ...(df.lastname && {"profile.lastname": df.lastname}),
    ...(df.email && {"profile.email": df.email}),
    ...(df.age && {"profile.age": df.age}),
    ...(df.profession && {"profile.profession": df.profession}),
  }
  return fieldsToUpdate;
}

module.exports = {
  profileSchema, parseProfileData
}


