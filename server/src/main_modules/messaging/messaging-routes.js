const { Conversation, Message } = require("./models");

module.exports = function(app) {
  app.route('/messaging/conversation')
    .get(async (req,res) => {
      const {account1Id,account2Id} = req.query;
      try {
        const conversation = await Conversation.findOne({members: { $all: [account1Id, account2Id]}});
        res.status(200).json(conversation)
      } catch (err) {
        res.status(400).json(err);
      }

    })
    .post(async (req,res,next) => {
      const {account1Id,account2Id} = req.query;
      
      const newConversation = new Conversation({
        members: [account1Id,account2Id],
      })

      try {
        const foundConversation = await Conversation.findOne({members: { $all: [account1Id, account2Id]}});
        if (foundConversation) {
          return res.status(400).send('conversation already exists')
        }
        const savedConversation = await newConversation.save();
        res.status(200).send(savedConversation);
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

  app.route('/messaging/message/:conversationId') //requires conversationId, sender, text
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
    .post(async (req,res) => {
      const newMessage = new Message({
        conversationId: req.params.conversationId,
        ...req.query
      });

      try {
        const savedMessage = await newMessage.save();
        res.status(200).json(savedMessage);
      } catch(err) {
        res.status(400).json(err)
      }
    })


  app.route('/messaging/conversation/finished/:conversationId')
    .put(async (req,res) => {
      try {
        const isFinished = (req.query.isFinished === 'true');
        const conversation = await Conversation.findOneAndUpdate(
          {_id: req.params.conversationId},
          {isFinished: isFinished},
          {returnDocument: 'after'}
        );
        console.log(conversation);
        res.status(200).json(conversation);
      } catch(err) {
        res.status(400).json(err);
      }
    })
}