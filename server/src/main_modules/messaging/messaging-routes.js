const { Conversation, Message } = require("./models");

module.exports = function(app) {
  app.route('/messaging/conversation')
    .get(async (req,res) => {
      try {
        const conversation = await Conversation.findOne({members: { $all: [account1Id, account2Id]}});
        res.status(200).json(conversation)
      } catch (err) {
        res.status(400).json(err);
      }

    })
    .post(async (req,res,next) => {
      const {account1Id,account2Id} = req.body;
      
      const newConversation = new Conversation({
        members: [account1Id,account2Id],
        isAccepted: false
      })

      try {
        const foundConversation = await Conversation.findOne({members: { $all: [account1Id, account2Id]}});
        if (foundConversation) {
          return res.status(400).send('conversation already exists')
        }
        const savedConversation = await newConversation.save();
        res.status(200).send('conversation created');
      } catch(err) {
        res.status(400).json(err)
      }
    })

  app.route('/messaging/conversation/:accountId')
    .get(async (req,res) => {
      try {
        const conversations = await Conversation.find({members: { $in: [req.params.accountId]}});
        res.status(200).json(conversations);
      } catch(err) {
        res.status(400).json(err)
      }
    })

  app.route('/messaging/message') //requires conversationId, sender, text
    .post(async (req,res) => {
      const newMessage = new Message(req.body);

      try {
        const savedMessage = await newMessage.save();
        res.status(200).json(savedMessage);
      } catch(err) {
        res.status(400).json(err)
      }
    })

  app.route('/messaging/message/:conversationId')
    .get(async (req,res) => {
      try {
        const messages = await Message.find({
          conversationId: req.params.conversationId
        })
        res.status(200).json(messages);
      } catch (err) {
        res.status(400).json(err);
      }
    })
}