const { findConversation, createConversation, findConversationsInAccount, findMessages, createMessage, deleteMessages, updateIsFinished } = require("./messaging-store");
const { Conversation, Message } = require("./models");

module.exports = function(app) {
  app.route('/messaging/conversation')
    .get(async (req,res) => {
      const {account1Id,account2Id} = req.query;
      await findConversation(account1Id,account2Id,(err,status,returnData) => {
        if (err) console.log(err);
        res.status(status).json(returnData);
      })
    })
    .post(async (req,res,next) => {
      const {account1Id,account2Id} = req.query;
      await createConversation(account1Id,account2Id,(err,status,returnData) => {
        if (err) console.log(err);
        res.status(status).json(returnData);
      })
    })
  app.route('/messaging/conversation/:accountId')
    .get(async (req,res) => {
      await findConversationsInAccount(req.params.accountId,(err,status,returnData) => {
        if (err) console.log(err);
        res.status(status).json(returnData);
      })
    })

  app.route('/messaging/message/:conversationId') //requires conversationId, sender, text
    .get(async (req,res) => {
      await findMessages(req.params.conversationId,(err,status,returnData) => {
        if (err) console.log(err);
        res.status(status).json(returnData);
      })
    })
    .post(async (req,res) => {
      await createMessage(req.params.conversationId,req.query,(err,status,returnData) => {
        if (err) console.log(err);
        res.status(status).json(returnData);
      })
    })
    .delete(async (req,res) => {
      await deleteMessages(req.params.conversationId,(err,status,returnData) => {
        if (err) console.log(err);
        res.status(status).json(returnData);
      })
    })


  app.route('/messaging/conversation/finished/:conversationId')
    .put(async (req,res) => {
      await updateIsFinished(
        req.params.conversationId,
        req.query.isFinished,
        (err,status,returnData) => {
          if (err) console.log(err);
          res.status(status).json(returnData);
        })
    })
}