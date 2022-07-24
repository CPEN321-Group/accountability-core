const mongoose = require('mongoose');
const { r_string, r_num, r_date } = require('../../../utils/types/mongo-required');
const goalDB = mongoose.createConnection((process.env.MONGO_BASE_URL || 'mongodb://localhost') + '/goalDB')

const goalSchema = new mongoose.Schema({
  title: r_string,
  target: {
    ...r_num,
    min: 10
  },
  current: {
    ...r_num, 
    default: 0,
    min: 0
  },
  deadline: {
    ...r_date,
    validate: {
      validator: function(v) {
        return v.getTime() > Date.now()
      },
      message: "Deadline must be in the future"
    }
  },
},{timestamps:true});

const userGoalSchema = new mongoose.Schema({
  userId: r_string,
  goals: {type: [goalSchema], default: []}
})

const Goal = goalDB.model('Goal',goalSchema);
const UserGoal = goalDB.model('UserGoal',userGoalSchema);

module.exports = {UserGoal, goalSchema, Goal};
