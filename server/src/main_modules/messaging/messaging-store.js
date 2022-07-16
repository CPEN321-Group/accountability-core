const { getDefinedFields, fieldsAreNotNull } = require("../../utils/get-defined-fields");
const { Conversation, Message } = require("./models");

module.exports = {
  findConversation: async (account1Id,account2Id,callback) => {
    try {
      const conversation = await Conversation.findOne(
        {members: { $all: [account1Id, account2Id]}}
      );
      if (!conversation) {
        return callback(404, 'conversation not found');
      }
      return callback(200, conversation);
    } catch (err) {
      console.log(err)
      return callback(400,err);
    }
  },
  createConversation: async (account1Id,account2Id,callback) => {
    try {
      const newConversation = new Conversation({
        members: [account1Id,account2Id],
      })
      const foundConversation = await Conversation.findOne({members: { $all: [account1Id, account2Id]}});
      if (foundConversation) {
        return callback(400, 'conversation already exists');
      }
      const savedConversation = await newConversation.save();
      return callback(200,savedConversation);
    } catch (err) {
      console.log(err)
      return callback(400,err);
    }
  },
  findConversationsInAccount: async (accountId,callback) => {
    try {
      const conversations = await Conversation.find({members: { $in: [accountId]}});
      if (!conversations) { //impossible path as an empty list will be returned
        return callback(404,'conversations not found');
      }
      return callback(200,conversations);
    } catch(err) {
      console.log(err)
      return callback(400,err);
    }
  },
  findMessages: async (conversationId, callback) => {
    try {
      const messages = await Message.find({
        conversationId: conversationId
      })
      if (!messages) {
        return callback(404,'messages not found');
      }
      return callback(200,messages);
    } catch (err) {
      console.log(err)
      return callback(400,err);
    }
  },
  createMessage: async (conversationId,fields,callback) => {
    try {
      const df = getDefinedFields(fields);
      const {sender,text} = df;
      if (!fieldsAreNotNull({conversationId,sender,text})) {
        return callback(400, 'missing params');
      }
      const newMessage = new Message({
        conversationId: conversationId,
        ...fields
      });
      const savedMessage = await newMessage.save();
      return callback(200,savedMessage);
    } catch (err) {
      console.log(err)
      return callback(400,err);
    }
  },
  deleteMessages: async (conversationId,callback) => {
    try {
      await Message.deleteMany({conversationId: conversationId});
      return callback(200,'messages deleted')
    } catch (err) {
      console.log(err)
      return callback(400,err);
    }
  },
  updateIsFinished: async (conversationId,isFinished,callback) => {
    try {
      const isFin = (isFinished === 'true');
      const conversation = await Conversation.findOneAndUpdate(
        {_id: conversationId},
        {isFinished: isFin},
        {returnDocument: 'after'}
      );
      if (!conversation) {
        return callback(404,'conversation not found');
      }
      return callback(200, conversation);
    } catch(err) {
      console.log(err)
      return callback(400,err);
    }
  }
}