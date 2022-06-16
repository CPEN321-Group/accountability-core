const mongoose = require('mongoose');
const chatDB = mongoose.createConnection('mongodb://localhost/chatDB')
const {r_string,r_bool,r_num,r_date} = require.main.require('./utils/types/mongoRequired')

const messageSchema = new mongoose.Schema({
  snd: r_string,
  rcv: r_string,
  timeStamp: r_date
})

const chatSchema = new mongoose.Schema({
  id: r_string,
  accepted: r_bool,
  messages: [messageSchema]
})

const userChatSchema = new mongoose.Schema({
  userId: r_string,
  chatIdList: [r_string]
})

const UserChat = new mongoose.model('UserChat',userChatSchema);
const Chat = new mongoose.model('Chat', chatSchema);

module.exports = {UserChat,Chat};