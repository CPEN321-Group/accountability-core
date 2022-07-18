// const { createSubscription: setAccountSubscription } = require('../../main_modules/accounts/account-store');

//setup based on https://www.youtube.com/watch?v=rPR2aJ6XnAc
require('dotenv').config();
const stripe = require('stripe')(process.env.STRIPE_SECRET);

// const PRICE_ID = 'price_1LFSrxGjSjq6aykFZNOKR1JP';

// function getExpiry(subscriptionDate,validFor = 31) {
//   let expiry = subscriptionDate;
//   expiry.setDate(expiry.getDate() + validFor);
//   return expiry;
// }
// async function createSession(userId, customerId) {
//   const validFor = 31;
//   const session = await stripe.checkout.sessions.create({
//     success_url: "http://localhost:8000/stripe/order/success",
//     success_url: "http://localhost:8000/stripe/order/success?session_id={CHECKOUT_SESSION_ID}",
//     cancel_url: 'http://localhost:8000/stripe/order/cancel',
//     line_items: [{price: PRICE_ID, quantity: 1}],
//     metadata: { userId, validFor },
//     mode: 'subscription',
//     customer: customerId,

//   });
//   return session;
// }

// function findUser(userId, callback) {
//   Account.findOne({accountId:userId}, (err,foundUser) => {
//     if (err || !foundUser) 
//       return callback(new Error('user not found'))

//     callback(err,foundUser)
//   })
// }
// async function createCustomerForUser(user) {
//   const { firstname, lastname, email } = user.profile;
//   const newCustomer = await stripe.customers.create({
//     email: email,
//     metadata: { userId: user.id },
//     name: `${firstname} ${lastname}`
//   });
//   console.log(newCustomer);
//   return newCustomer;
// }

// async function createSubscriptionForCustomer(customer) {
//   const newSubscription = await stripe.subscriptions.create({
//     customer: customer.id,
//     items: [ {price: PRICE_ID}],
//     payment_behavior: 'default_incomplete'
//   })
//   console.log(newSubscription);
//   return newSubscription;
// }

// function subscriptionIsActive(user) {
//   if (!user.subscription || !user.subscription.expiryDate)
//     return false;
    
//   const {expiryDate} = user.subscription;
//   const today = new Date();
//   if (expiryDate && expiryDate instanceof Date && !isNaN(expiryDate.valueOf()))
//     return today.getTime() < expiryDate.getTime();
//   else
//     return false;
// }

module.exports = function(app) {
    //stripe success redirect page

    app.get('/stripe/public-keys', (req,res) => {
      res.send({key: process.env.STRIPE_PUBLIC_KEY })
    })
    app.post('/stripe/checkout/:userId', async (req,res) => {
      try {
        // const {userId} = req.params;
        const newCustomer = await stripe.customers.create({
          email: 'test123@gmail.com',
          metadata: { userId: '1' },
          name: `Bob Jones`
        });
        let customerId = newCustomer.id;
          const ephemeralKey = await stripe.ephemeralKeys.create(
            {customer: customerId},
            {apiVersion: '2020-08-27'}
          );
          const paymentIntent = await stripe.paymentIntents.create({
            amount: 5000,
            currency: 'cad',
            customer: customerId,
            automatic_payment_methods: {
              enabled: true,
            },
          });
          res.json({
            paymentIntent: paymentIntent.client_secret,
            ephemeralKey: ephemeralKey.secret,
            customer: customerId,
            publishableKey: process.env.STRIPE_PUBLIC_KEY
          });
      } catch (err) {
        console.log(err);
        res.status(500).json(err);
      }
      
    })
    // app.post('/stripe/checkout/sessions/:userId', async (req,res,next) => {
    //   console.log('creating session...')
    //   const {userId} = req.params;
    //     findUser(userId, async (err,foundUser) => {
    //       let session;
    //       if (!foundUser.stripeCustomerId) {
    //         console.log('initializing new customer...')
    //         const newCustomer = await createCustomerForUser(foundUser);
    //         const newSubscription = await createSubscriptionForCustomer(newCustomer);
    //         Account.findOneAndUpdate({accountId: userId}, { stripeCustomerId: newCustomer.id,stripeSubscriptionId: newSubscription.id }, {returnDocument: 'after'}, async (err,updatedUser) => {
    //           if (err) { return next(err)}
                
    //           session = await createSession(userId,updatedUser.stripeCustomerId);
    //         });
    //       } else if (subscriptionIsActive(foundUser)) {
    //         return res.send('subscription already active');
    //       } else {
    //         session = await createSession(userId,foundUser.stripeCustomerId);
    //       }
    //       res.send(session.url);
    //     })
    // })
    // app.post('/stripe/portal/sessions/:userId', async (req,res,next) => {
    //   const returnUrl = 'http://localhost:8000/stripe/order/cancel';
    //   const portalSession = await stripe.billingPortal.sessions.create({
    //     customer: foundAccount.stripeCustomerId,
    //     return_url: returnUrl,
    //   });
    //   res.json(portalSession);
    // })

    // //listens for auto-billing to update subscription status in Account
    // app.post('/stripe/webhook', (req,res) => {
    //   const event = req.body;
    //   const data = event.data;
    //   // Check if webhook signing is configured.
    //   // const webhookSecret = process.env.STRIPE_WEBHOOK_SECRET;
    //   // if (webhookSecret) {
    //   //   // Retrieve the event by verifying the signature using the raw body and secret.
    //   //   let event;
    //   //   let signature = req.headers["stripe-signature"];
    
    //   //   try {
    //   //     event = stripe.webhooks.constructEvent(
    //   //       req.body,
    //   //       signature,
    //   //       webhookSecret
    //   //     );
    //   //   } catch (err) {
    //   //     console.log(`⚠️  Webhook signature verification failed.`);
    //   //     return res.sendStatus(400);
    //   //   }
    //   //   // Extract the object from the event.
    //   //   data = event.data;
    //   //   eventType = event.type;
    //   // } else {
    //   //   // Webhook signing is recommended, but if the secret is not configured in `config.js`,
    //   //   // retrieve the event data directly from the request body.
    //   //   data = req.body.data;
    //   //   eventType = req.body.type;
    //   // }
    
    //   switch (req.body.type) {
    //       case 'checkout.session.completed':
    //         console.log('checkout session completed')
    //         // Payment is successful and the subscription is created.
    //         // You should provision the subscription and save the customer ID to your database.
    //         console.log(data.object);
    //         break;
    //       case 'invoice.paid':
    //         // Continue to provision the subscription as payments continue to be made.
    //         // Store the status in your database and check when a user accesses your service.
    //         // This approach helps you avoid hitting rate limits.
    //         console.log('invoice paid');
    //         console.log(data.object);
    //         break;
    //       case 'invoice.payment_failed':
    //         // The payment failed or the customer does not have a valid payment method.
    //         // The subscription becomes past_due. Notify your customer and send them to the
    //         // customer portal to update their payment information.
    //         console.log('invoice payment failed');
    //         console.log(data.object);
    //         break;
    //       default:
    //         console.log(`Unhandled event type ${event.type}`);
    //     }
    
    //   res.sendStatus(200);
    // })
    // app.get('/stripe/order/success', async (req, res,next) => {
    //   try {
    //     const session = await stripe.checkout.sessions.retrieve(req.query.session_id);
    //     const customer = await stripe.customers.retrieve(session.customer);

    //     const {userId,validFor} = session.metadata;
    //     const subscriptionDate = new Date();
    //     console.log(`${subscriptionDate} ${validFor}`);
    //     let expiryDate = getExpiry(new Date(),parseInt(validFor,10));
    //     const subscription = {subscriptionDate,expiryDate};
    //     setAccountSubscription(userId,subscription, (err,foundAccount) => {
    //       if (err) return next(err);
    //       return res.json({account: foundAccount, session,customer})
    //       // res.end('Success!')
    //     })
    //   } catch (err) {
    //     console.log(err)
    //     res.send('provide a valid session_id');
    //   }
    // });
    // app.get('/stripe/order/cancel',(req,res) => {
    //   res.end('Order cancelled')
    // })
}