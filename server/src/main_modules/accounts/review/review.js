const mongoose = require('mongoose');
const {r_string,r_bool,r_num, r_date} = require.main.require('./utils/types/mongo-required')

const reviewSchema = new mongoose.Schema({
  authorId: r_string,
  accountantId: r_string,
  date: r_date,
  rating: r_num,
  title: r_string,
  content: String
})


module.exports = {reviewSchema}