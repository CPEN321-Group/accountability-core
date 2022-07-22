const { getDefinedFields, fieldsAreNotNull } = require("../../utils/checks/get-defined-fields");
const { Conversation, Message } = require("./models");

module.exports = {
  findConversation: async (account1Id,account2Id,callback) => {
    if(callback);
    try {
      if (!fieldsAreNotNull({account1Id,account2Id})) {
        return callback(null,400,'missing params');
      }
      const members = {$all: [account1Id, account2Id]};
      const conversation = await Conversation.findOne(
        {members}
      );
      if (!conversation) {
        return callback(null,404, 'conversation not found');
      }
      return callback(null,200, conversation);
    } catch (err) {
      return callback(null,400,err);
    }
  },
  createConversation: async (account1Id,account2Id,callback) => {
    if(callback);
    try {
      if (!fieldsAreNotNull({account1Id,account2Id})) {
        return callback(null,400,'missing params');
      }
      const newConversation = new Conversation({
        members: [account1Id,account2Id],
      })
      const foundConversation = await Conversation.findOne({members: { $all: [account1Id, account2Id]}});
      if (foundConversation) {
        return callback(null,400, 'conversation already exists');
      }
      const savedConversation = await newConversation.save();
      return callback(null,200,savedConversation);
    } catch (err) {
      return callback(null,400,err);
    }
  },
  findConversationsInAccount: async (accountId,callback) => {
    if(callback);
    try {
      const conversations = await Conversation.find({members: { $in: [accountId]}});
      if (!conversations) { //impossible path as an empty list will be returned
        return callback(null,404,'conversations not found');
      }
      return callback(null,200,conversations);
    } catch(err) {
      return callback(null,400,err);
    }
  },
  findMessages: async (conversationId, callback) => {
    if(callback);
    try {
      const messages = await Message.find({conversationId})
      if (!messages) {
        return callback(null,404,'messages not found');
      }
      return callback(null,200,messages);
    } catch (err) {
      return callback(null,400,err);
    }
  },
  createMessage: async (conversationId,fields,callback) => {
    if(callback);
    try {
      const df = getDefinedFields(fields);
      const {sender,text} = df;
      if (!fieldsAreNotNull({conversationId,sender,text})) {
        return callback(null,400, 'missing params');
      }
      const newMessage = new Message({
        conversationId,
        ...fields
      });
      const savedMessage = await newMessage.save();
      return callback(null,200,savedMessage);
    } catch (err) {
      return callback(null,400,err);
    }
  },
  deleteMessages: async (conversationId,callback) => {
    if(callback);
    try {
      await Message.deleteMany({conversationId});
      return callback(null,200,'messages deleted')
    } catch (err) {
      return callback(null,400,err);
    }
  },
  updateIsFinished: async (conversationId,isFinished,callback) => {
    if(callback);
    try {
      if (!fieldsAreNotNull({isFinished})) {
        return callback(null,400,'missing params');
      }
      const isFin = (isFinished === 'true');
      const conversation = await Conversation.findOneAndUpdate(
        {_id: conversationId},
        {isFinished: isFin},
        {returnDocument: 'after', runValidators: true}
      );
      if (!conversation) {
        return callback(null,404,'conversation not found');
      }
      return callback(null,200, conversation);
    } catch(err) {
      return callback(null,400,err);
    }
  }
}