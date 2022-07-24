const mongoose = require('mongoose');
const { r_string } = require('../../utils/types/mongo-required');
const messagingDB = mongoose.createConnection((process.env.MONGO_BASE_URL || 'mongodb://localhost') + '/chatDB')

const messageSchema = new mongoose.Schema({
  conversationId: r_string,
  sender: r_string,
  text: r_string,
}, {timestamps: true} );

const conversationSchema = new mongoose.Schema({
  members: [String],
  isFinished: {type: Boolean, default: false}
})


const Conversation = messagingDB.model('Conversation', conversationSchema);
const Message = messagingDB.model('Message', messageSchema);

module.exports = {Conversation, Message};
