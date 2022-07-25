const { getDefinedFields, fieldsAreNotNull } = require("../../utils/checks/get-defined-fields");
const { NotFoundError, ValidationError } = require("../../utils/errors");
const { findAccount } = require("../accounts/account-store");
const { Conversation, Message } = require("./messaging-models");


async function conversationExists(conversationId) {
  const conversation = await Conversation.findOne({_id: conversationId});
  if (!conversation) {
    return false;
  }
  return true;
}
module.exports = {
  findConversation: async (account1Id,account2Id,callback) => {
    
    try {
      if (!fieldsAreNotNull({account1Id,account2Id})) {
        throw new ValidationError('missing params');
      }
      const members = {$all: [account1Id, account2Id]};
      const conversation = await Conversation.findOne(
        {members}
      );
      if (!conversation) {
        return callback(null,404, new NotFoundError('conversation not found'));
      }
      return callback(null,200, conversation);
    } catch (err) {
      return callback(null,400,err);
    }
  },
  createConversation: async (account1Id,account2Id,callback) => {
    try {
      if (!fieldsAreNotNull({account1Id,account2Id})) {
        throw new ValidationError('missing params');
      }
      const foundConversation = await Conversation.findOne({members: { $all: [account1Id, account2Id]}});
      if (foundConversation) {
        throw new ValidationError('conversation already exists');
      }
      let account1,account2;
      await findAccount(account1Id,(err,status,returnData) => {
        account1 = returnData.accountId;
      });
      await findAccount(account2Id,(err,status,returnData) => {
        account2 = returnData.accountId;
      });
      if (!account1 || !account2) {
        return callback(null, 404, new NotFoundError('at least one of specified accounts do not exist.')) 
      }
      const newConversation = new Conversation({
        members: [account1Id,account2Id],
      })
      const savedConversation = await newConversation.save();
      return callback(null,200,savedConversation);
    } catch (err) {
      return callback(null,400,err);
    }
  },
  findConversationsInAccount: async (accountId,callback) => {
    try {
      let foundAccount;
      await findAccount(accountId, (err,status,returnData) => {
        if (status !== 200) {
          foundAccount = false;
        } else foundAccount = true;
      })
      if (!foundAccount) {
        return callback(null,404,new NotFoundError('account not found'));
      }
      const conversations = await Conversation.find({members: { $in: [accountId]}});
      
      return callback(null,200,conversations);
    } catch(err) {
      return callback(null,400,err);
    }
  },
  findMessages: async (conversationId, callback) => {
    try {
      if (!await conversationExists(conversationId)) {
        return callback(null,404, new NotFoundError('conversation not found'));
      }
      const messages = await Message.find({conversationId})

      return callback(null,200,messages);
    } catch (err) {
      return callback(null,400,err);
    }
  },
  createMessage: async (conversationId,fields,callback) => {
    try {
      if (!await conversationExists(conversationId)) {
        return callback(null,404, new NotFoundError('conversation not found'));
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
    try {
      if (!await conversationExists(conversationId)) {
        return callback(null,404, new NotFoundError('conversation not found'));
      }
      await Message.deleteMany({conversationId});
      return callback(null,200,'messages deleted')
    } catch (err) {
      return callback(null,400,err);
    }
  },
  updateIsFinished: async (conversationId,isFinished,callback) => {
    try {
      if (!await conversationExists(conversationId)) {
        return callback(null,404, new NotFoundError('conversation not found'));
      }
      if (!fieldsAreNotNull({isFinished})) {
        throw new ValidationError('missing params');
      }
      const isFin = (isFinished === true || isFinished === 'true');
      const conversation = await Conversation.findOneAndUpdate(
        {_id: conversationId},
        {isFinished: isFin},
        {returnDocument: 'after', runValidators: true}
      );
      if (!conversation) {
        return callback(null,404,new NotFoundError('conversation not found'));
      }
      return callback(null,200, conversation);
    } catch(err) {
      return callback(null,400,err);
    }
  }
}