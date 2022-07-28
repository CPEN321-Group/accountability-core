require('dotenv').config();
const stripe = require('stripe')(process.env.STRIPE_SECRET); 

stripe.products.create({
  name: 'Monthy Subscription',
  description: '$50/Month subscription',
}).then(product => {
  stripe.prices.create({
    unit_amount: 5000,
    currency: 'cad',
    recurring: {
      interval: 'month',
    },
    product: product.id,
  }).then(price => {
    console.log('Success! Here is your subscription product id: ' + product.id);
    console.log('Success! Here is your subscription price id: ' + price.id);
  });
});