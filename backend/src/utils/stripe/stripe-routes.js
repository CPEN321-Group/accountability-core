// const { createSubscription: setAccountSubscription } = require('../../main_modules/accounts/account-store');

const { Account } = require('../../main_modules/accounts/account-models');
const { isPastDate } = require('../checks/date-check');
const { NotFoundError, ValidationError } = require('../errors');

//setup based on https://www.youtube.com/watch?v=rPR2aJ6XnAc
require('dotenv').config();
const stripe = require('stripe')(process.env.STRIPE_SECRET);

module.exports = function(app) {
    app.post('/stripe/checkout/:userId', async (req,res) => {
      if(req);
      try {
        const user = await Account.findOne({accountId: req.params.userId, isAccountant: false});
        if (!user) { return res.status(404).json(new NotFoundError('account not found'))}
        if (!isPastDate(user.subscription.expiryDate)) {
          return res.status(400).json(new ValidationError('account already subscribed'));
        }
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
          res.status(200).json({
            paymentIntent: paymentIntent.client_secret,
            ephemeralKey: ephemeralKey.secret,
            customer: customerId,
            publishableKey: process.env.STRIPE_PUBLIC_KEY
          });
      } catch (err) {
        res.status(400).json(err);
      }
    })
}