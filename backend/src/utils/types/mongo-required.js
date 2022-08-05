

module.exports = {
  r_string: {type: String, required: true},
  r_bool:{type: Boolean, required: true},
  r_num: {type: Number, required: true},
  r_date: {type: Date, require: true},
  letter_string: {
    type: String,
    match: /^[a-zA-Z ]+$/
  },
  email_string: {
    type: String,
    match: [
      /\b[\w.%+-]+@[\w.-]+\.[A-Za-z]{2,4}\b/,
      "Please fill a valid email address",
    ]
  }
}