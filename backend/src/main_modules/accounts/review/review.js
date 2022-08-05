const mongoose = require('mongoose');
const {r_string,r_date} = require('../../../utils/types/mongo-required')

const reviewSchema = new mongoose.Schema({
  authorId: r_string,
  accountantId: r_string,
  date: {
    ...r_date,
    validate: {
      validator: function(v) {
        return v.getTime() <= Date.now()
      },
      message: "Date cannot be in the future"
    }
  },
  rating: {
    type: Number,
    required: true,
    min: 0,
    max: 5
  },
  title: r_string,
  content: String
})


module.exports = {reviewSchema}