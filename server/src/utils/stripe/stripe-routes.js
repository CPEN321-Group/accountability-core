const { authenticate } = require('../../main_modules/accounts/account-auth');
const { createSubscription } = require('../../main_modules/accounts/account-store');

//setup based on https://www.youtube.com/watch?v=rPR2aJ6XnAc
require('dotenv').config();
const stripe = require('stripe')(process.env.STRIPE_SECRET);

function getExpiry(subscriptionDate,validFor = 31) {
  let expiry = subscriptionDate;
  expiry.setDate(expiry.getDate() + validFor);
  return expiry;
}
async function createSession(userId) {
  const validFor = 31;
  const session = await stripe.checkout.sessions.create({
    success_url: "http://localhost:8000/stripe/order/success",
    success_url: "http://localhost:8000/stripe/order/success?session_id={CHECKOUT_SESSION_ID}",
    cancel_url: 'http://localhost:8000/stripe/order/cancel',
    line_items: [
      {price: 'price_1LFSrxGjSjq6aykFZNOKR1JP', quantity: 1},
    ],
    metadata: { userId, validFor },
    mode: 'subscription'
  });
  return session;
}

module.exports = function(app) {
    //stripe success redirect page

    app.get('/stripe/public-keys', (req,res) => {
      res.send({key: process.env.STRIPE_PUBLIC_KEY })
    })
    app.post('/stripe/checkout/sessions/:userId', async (req,res,next) => {
      const {userId} = req.params;
      const {token} = req.query;
      authenticate(token,userId, async (err,foundAccount) => {
        if (err) return next(err);
        if (!foundAccount) return next(new Error('account not found'));

        const session = await createSession(userId);
        res.send(session);
      })
    })
    app.post('/stripe/webhook', (req,res) => {
      const event = req.body;
      switch(event.type) {
        case 'checkout.session.completed':
          const session = event.data.object;
          console.log("Checkout Session ID: ", session.id);
          break;
        case 'payment_intent.created': 
          const paymentIntent = event.data.object;
          console.log("PaymentIntent created ", paymentIntent.id);
          break;
        default: 
          console.log('Unknown event type: ' + event.type);
      }
    })
    app.get('/stripe/order/success', async (req, res,next) => {
      try {
        const session = await stripe.checkout.sessions.retrieve(req.query.session_id);
        const customer = await stripe.customers.retrieve(session.customer);

        const {userId,validFor} = session.metadata;
        const subscriptionDate = new Date();
        console.log(`${subscriptionDate} ${validFor}`);
        let expiryDate = getExpiry(new Date(),parseInt(validFor,10));
        const subscription = {subscriptionDate,expiryDate};
        createSubscription(userId,subscription, (err,foundAccount) => {
          if (err) return next(err);
          // return res.json({account: foundAccount, session,customer})
          res.end('Success!')
        })
      } catch (err) {
        console.log(err)
        res.send('provide a valid session_id');
      }
    });
}