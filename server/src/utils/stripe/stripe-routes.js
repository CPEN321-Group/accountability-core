const { authenticate } = require('../../main_modules/accounts/account-auth');
const { createSubscription: setAccountSubscription } = require('../../main_modules/accounts/account-store');
const { Account } = require('../../main_modules/accounts/models');

//setup based on https://www.youtube.com/watch?v=rPR2aJ6XnAc
require('dotenv').config();
const stripe = require('stripe')(process.env.STRIPE_SECRET);

const PRICE_ID = 'price_1LFSrxGjSjq6aykFZNOKR1JP';

function getExpiry(subscriptionDate,validFor = 31) {
  let expiry = subscriptionDate;
  expiry.setDate(expiry.getDate() + validFor);
  return expiry;
}
async function createSession(userId, customerId) {
  const validFor = 31;
  const session = await stripe.checkout.sessions.create({
    success_url: "http://localhost:8000/stripe/order/success",
    success_url: "http://localhost:8000/stripe/order/success?session_id={CHECKOUT_SESSION_ID}",
    cancel_url: 'http://localhost:8000/stripe/order/cancel',
    line_items: [{price: PRICE_ID, quantity: 1}],
    metadata: { userId, validFor },
    mode: 'subscription',
    customer: customerId,

  });
  return session;
}

function findUserById(userId, callback) {
  Account.findById(userId, (err,foundUser) => {
    if (err || !foundUser) 
      return callback(new Error('user not found'))

    callback(err,foundUser)
  })
}
async function createCustomerForUser(user) {
  const { firstname, lastname, email } = user.profile;
  const newCustomer = await stripe.customers.create({
    email: email,
    metadata: { userId: user.id },
    name: `${firstname} ${lastname}`
  });
  console.log(newCustomer);
  return newCustomer;
}

async function createSubscriptionForCustomer(customer) {
  const newSubscription = await stripe.subscriptions.create({
    customer: customer.id,
    items: [ {price: PRICE_ID}],
    payment_behavior: 'default_incomplete'
  })
  console.log(newSubscription);
  return newSubscription;
}

function subscriptionIsActive(user) {
  const today = new Date();
  return today.getTime() < user.subscription.expiryDate.getTime();
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
        findUserById(userId, async (err,foundUser) => {
          let session;
          if (!foundUser.stripeCustomerId) {
            console.log('initializing new customer...')
            const newCustomer = await createCustomerForUser(foundUser);
            const newSubscription = await createSubscriptionForCustomer(newCustomer);
            Account.findByIdAndUpdate(foundUser.id, { stripeCustomerId: newCustomer.id,stripeSubscriptionId: newSubscription.id }, async (err,updatedUser) => {
              if (err) { return next(err)}
                
              session = await createSession(userId,updatedUser.stripeCustomerId);
            });
          } else if (subscriptionIsActive(foundUser)) {
            res.send('subscription already active');
          } else {
            session = await createSession(userId,foundUser.stripeCustomerId);
          }
          res.send(session);
        })
      })
    })
    app.post('/stripe/portal/sessions/:userId', async (req,res,next) => {
      const returnUrl = 'http://localhost:8000/stripe/order/cancel';
      authenticate(req.query.token,req.params.userId, async (err,foundAccount) => {
        const portalSession = await stripe.billingPortal.sessions.create({
          customer: foundAccount.stripeCustomerId,
          return_url: returnUrl,
        });
        res.json(portalSession);
      })
    })

    //listens for auto-billing to update subscription status in Account
    app.post('/stripe/webhook', (req,res) => {
      const event = req.body;
      const data = event.data;
      // Check if webhook signing is configured.
      // const webhookSecret = process.env.STRIPE_WEBHOOK_SECRET;
      // if (webhookSecret) {
      //   // Retrieve the event by verifying the signature using the raw body and secret.
      //   let event;
      //   let signature = req.headers["stripe-signature"];
    
      //   try {
      //     event = stripe.webhooks.constructEvent(
      //       req.body,
      //       signature,
      //       webhookSecret
      //     );
      //   } catch (err) {
      //     console.log(`⚠️  Webhook signature verification failed.`);
      //     return res.sendStatus(400);
      //   }
      //   // Extract the object from the event.
      //   data = event.data;
      //   eventType = event.type;
      // } else {
      //   // Webhook signing is recommended, but if the secret is not configured in `config.js`,
      //   // retrieve the event data directly from the request body.
      //   data = req.body.data;
      //   eventType = req.body.type;
      // }
    
      switch (req.body.type) {
          case 'checkout.session.completed':
            console.log('checkout session completed')
            // Payment is successful and the subscription is created.
            // You should provision the subscription and save the customer ID to your database.
            console.log(data.object);
            break;
          case 'invoice.paid':
            // Continue to provision the subscription as payments continue to be made.
            // Store the status in your database and check when a user accesses your service.
            // This approach helps you avoid hitting rate limits.
            console.log('invoice paid');
            console.log(data.object);
            break;
          case 'invoice.payment_failed':
            // The payment failed or the customer does not have a valid payment method.
            // The subscription becomes past_due. Notify your customer and send them to the
            // customer portal to update their payment information.
            console.log('invoice payment failed');
            console.log(data.object);
            break;
          default:
            console.log(`Unhandled event type ${event.type}`);
        }
    
      res.sendStatus(200);
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
        setAccountSubscription(userId,subscription, (err,foundAccount) => {
          if (err) return next(err);
          return res.json({account: foundAccount, session,customer})
          // res.end('Success!')
        })
      } catch (err) {
        console.log(err)
        res.send('provide a valid session_id');
      }
    });
    app.get('/stripe/order/cancel',(req,res) => {
      res.end('Order cancelled')
    })
}