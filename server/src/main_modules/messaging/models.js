const mongoose = require('mongoose');
const chatDB = mongoose.createConnection('mongodb://localhost/chatDB')
const {r_string,r_bool,r_num,r_date} = require.main.require('./utils/types/mongoRequired')

const messageSchema = new mongoose.Schema({
  accountId:r_string,
  timeStamp: r_date,
  content: r_string
})

const chatSchema = new mongoose.Schema({
  id: r_string,
  account1Id: r_string,
  account2Id: r_string,
  messages: [messageSchema]
})

const accountChatSchema = new mongoose.Schema({
  accountId: r_string,
  chatIdList: [r_string]
})

const AccountChat = chatDB.model('UserChat',accountChatSchema);
const Chat = chatDB.model('Chat', chatSchema);

module.exports = {UserChat: AccountChat,Chat};