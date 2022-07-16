const mongoose = require('mongoose');
const goalDB = mongoose.createConnection((process.env.MONGO_BASE_URL || 'mongodb://localhost') + '/goalDB')
const {r_string,r_bool,r_num,r_date} = require.main.require('./utils/types/mongo-required')

const goalSchema = new mongoose.Schema({
  title: r_string,
  target: r_num,
  current: {...r_num, default: 0},
  deadline: r_date,
},{timestamps:true});

const userGoalSchema = new mongoose.Schema({
  userId: r_string,
  goals: {type: [goalSchema], default: []}
})

const UserGoal = goalDB.model('UserGoal',userGoalSchema);

module.exports = {UserGoal, goalSchema};
