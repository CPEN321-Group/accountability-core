const mongoose = require('mongoose');
const {r_string,r_num, r_date} = require('../../../utils/types/mongo-required')

const reviewSchema = new mongoose.Schema({
  authorId: r_string,
  accountantId: r_string,
  date: r_date,
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