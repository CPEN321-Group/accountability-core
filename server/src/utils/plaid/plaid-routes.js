
const {PlaidUser} = require('./models');
const { createTransaction } = require('../../main_modules/transactions/transaction-store');
const { fieldsAreNotNull } = require('../get-defined-fields');
const fx = require('money');
const { UserTransaction } = require('../../main_modules/transactions/models');
fx.base = "USD";
fx.rates = {//other rates need to be defined if we want to support other currencies
  "CAD": 1.29,
}
function saveTransactions(recently_added,userId, token, next) {
  for (let t of recently_added) {
    // console.log(t)
    let {amount,category,date,name,iso_currency_code,transaction_id} = t;
    let isIncome,convertedAmount;
    if (amount < 0) isIncome = true;
    // console.log(iso_currency_code);
    convertedAmount = Math.abs(Math.round(fx(amount).from(iso_currency_code).to("CAD")*100)/100); //round to .2d
    if (!fieldsAreNotNull({name,category: category[0],convertedAmount,isIncome})) { 
      return next(new Error('missing params'))
    }
    findTransaction(userId,transaction_id,(err,foundTransaction)=> {
      if (err) console.log(err);
      if (!foundTransaction) {
        createTransaction(userId,{
          title: name,
          category: category[0],
          date,
          amount: convertedAmount,
          isIncome,
          plaidTransactionId: transaction_id
        },(err,foundTransactions) => {
          if (err) return next(err);
        })
      }
    })

  }
}
function findTransaction(userId,plaidTransactionId, callback) {
  const idMatch = { plaidTransactionId };
  const transactionsMatch = { $elemMatch: idMatch};
  UserTransaction.findOne(
    {$and:[
      {userId}, 
      {transactions: transactionsMatch }
    ]}, 
    (err,foundTransaction) => callback(err,foundTransaction))
}

module.exports = function(app) {
  'use strict';

  // read env vars from .env file
  require('dotenv').config();
  const { Configuration, PlaidApi, PlaidEnvironments } = require('plaid');
  // const util = require('util');
  const bodyParser = require('body-parser');
  const moment = require('moment');
  const cors = require('cors');
  
  const PLAID_CLIENT_ID = process.env.PLAID_CLIENT_ID;
  const PLAID_SECRET = process.env.PLAID_SECRET;
  const PLAID_ENV = process.env.PLAID_ENV || 'sandbox';
  
  // PLAID_PRODUCTS is a comma-separated list of products to use when initializing
  // Link. Note that this list must contain 'assets' in order for the app to be
  // able to create and retrieve asset reports.
  const PLAID_PRODUCTS = (process.env.PLAID_PRODUCTS || 'transactions').split(
    ',',
  );
  
  // PLAID_COUNTRY_CODES is a comma-separated list of countries for which users
  // will be able to select institutions from.
  const PLAID_COUNTRY_CODES = (process.env.PLAID_COUNTRY_CODES || 'US').split(
    ',',
  );
  
  // Parameters used for the OAuth redirect Link flow.
  //
  // Set PLAID_REDIRECT_URI to 'http://localhost:3000'
  // The OAuth redirect flow requires an endpoint on the developer's website
  // that the bank website should redirect to. You will need to configure
  // this redirect URI for your client ID through the Plaid developer dashboard
  // at https://dashboard.plaid.com/team/api.
  const PLAID_REDIRECT_URI = process.env.PLAID_REDIRECT_URI || '';
  
  // Parameter used for OAuth in Android. This should be the package name of your app,
  // e.g. com.plaid.linksample
  const PLAID_ANDROID_PACKAGE_NAME = process.env.PLAID_ANDROID_PACKAGE_NAME || '';
  
  // // We store the access_token in memory - in production, store it in a secure
  // // persistent data store
  // let ACCESS_TOKEN = null;
  // let ITEM_ID = null;
  // // The payment_id is only relevant for the UK Payment Initiation product.
  // // We store the payment_id in memory - in production, store it in a secure
  // // persistent data store
  // let PAYMENT_ID = null;
  // // The transfer_id is only relevant for Transfer ACH product.
  // // We store the transfer_id in memory - in production, store it in a secure
  // // persistent data store
  // let TRANSFER_ID = null;
  
  // Initialize the Plaid client
  // Find your API keys in the Dashboard (https://dashboard.plaid.com/account/keys)
  
  const configuration = new Configuration({
    basePath: PlaidEnvironments[PLAID_ENV],
    baseOptions: {
      headers: {
        'PLAID-CLIENT-ID': PLAID_CLIENT_ID,
        'PLAID-SECRET': PLAID_SECRET,
        'Plaid-Version': '2020-09-14',
      },
    },
  });
  
  const client = new PlaidApi(configuration);
  
  app.use(
    bodyParser.urlencoded({
      extended: false,
    }),
  );
  app.use(bodyParser.json());
  app.use(cors());
  
  app.post('/plaid/:userId/info', function (request, response, next) {
    getTokens(request.params.userId, (accessToken, itemId) => {
      response.json({
        item_id: itemId,
        access_token: accessToken,
        products: PLAID_PRODUCTS,
      });
    });
  });
  
  // Create a link token with configs which we can then use to initialize Plaid Link client-side.
  // See https://plaid.com/docs/#create-link-token
  app.post('/plaid/:userId/create_link_token', function (request, response, next) {
    Promise.resolve()
      .then(async function () {
        const configs = {
          user: {
            // This should correspond to a unique id for the current user.
            client_user_id: 'user-id',
          },
          client_name: 'Plaid Quickstart',
          products: PLAID_PRODUCTS,
          country_codes: PLAID_COUNTRY_CODES,
          language: 'en',
        };
  
        if (PLAID_REDIRECT_URI !== '') {
          configs.redirect_uri = PLAID_REDIRECT_URI;
        }
  
        if (PLAID_ANDROID_PACKAGE_NAME !== '') {
          configs.android_package_name = PLAID_ANDROID_PACKAGE_NAME;
        }
        const createTokenResponse = await client.linkTokenCreate(configs);
        prettyPrintResponse(createTokenResponse);
        response.json(createTokenResponse.data);
      })
      .catch(next);
  });
  
  // Create a link token with configs which we can then use to initialize Plaid Link client-side.
  // See https://plaid.com/docs/#payment-initiation-create-link-token-request
  app.post(
    '/plaid/:userId/create_link_token_for_payment',
    function (request, response, next) {
      Promise.resolve()
        .then(async function () {
          const createRecipientResponse =
            await client.paymentInitiationRecipientCreate({
              name: 'Harry Potter',
              iban: 'GB33BUKB20201555555555',
              address: {
                street: ['4 Privet Drive'],
                city: 'Little Whinging',
                postal_code: '11111',
                country: 'GB',
              },
            });
          const recipientId = createRecipientResponse.data.recipient_id;
          prettyPrintResponse(createRecipientResponse);
  
          const createPaymentResponse =
            await client.paymentInitiationPaymentCreate({
              recipient_id: recipientId,
              reference: 'paymentRef',
              amount: {
                value: 1.23,
                currency: 'GBP',
              },
            });
          prettyPrintResponse(createPaymentResponse);
          const paymentId = createPaymentResponse.data.payment_id;
          const fieldsToSet = {
            "data.paymentId": paymentId, 
          }
          PlaidUser.findOneAndUpdate({userId: request.params.userId}, {$set: fieldsToSet}, {returnDocument: 'after'},(err,foundUser)=> {
            if (err) {
              console.log(err)
            } 
            if (!foundUser) creatPlaidUser(request.params.userId,null,null,null, paymentId);
          })
          const configs = {
            user: {
              // This should correspond to a unique id for the current user.
              client_user_id: 'user-id',
            },
            client_name: 'Plaid Quickstart',
            products: PLAID_PRODUCTS,
            country_codes: PLAID_COUNTRY_CODES,
            language: 'en',
            payment_initiation: {
              payment_id: paymentId,
            },
          };
          if (PLAID_REDIRECT_URI !== '') {
            configs.redirect_uri = PLAID_REDIRECT_URI;
          }
          const createTokenResponse = await client.linkTokenCreate(configs);
          prettyPrintResponse(createTokenResponse);
          response.json(createTokenResponse.data);
        })
        .catch(next);
    },
  );
  
  // Exchange token flow - exchange a Link public_token for
  // an API access_token
  // https://plaid.com/docs/#exchange-token-flow
  app.post('/plaid/:userId/set_access_token', function (request, response, next) {
    let publicToken = request.body.public_token;
    Promise.resolve()
      .then(async function () {
        const tokenResponse = await client.itemPublicTokenExchange({
          public_token: publicToken,
        });
        prettyPrintResponse(tokenResponse);
        
        let transferId;
        // let accessToken, itemId;
        if (PLAID_PRODUCTS.includes('transfer')) {
          transferId = await authorizeAndCreateTransfer(tokenResponse.data.access_token);
        }
        const fieldsToSet = {
          "data.accessToken": tokenResponse.data.access_token, 
          "data.itemId": tokenResponse.data.item_id,
          "data.transferId": transferId
        }
        PlaidUser.findOneAndUpdate({userId: request.params.userId}, {$set: fieldsToSet}, {returnDocument: 'after'},(err,foundUser)=> {
          if (!err) {
            accessToken = foundUser.data.accessToken;
            itemId = foundUser.data.itemId;
          }
          else {
            creatPlaidUser(tokenResponse.data.access_token, tokenResponse.data.item_id, transferId);
          }
          // console.log('access token set.')
        })

        response.json({
          access_token: tokenResponse.data.access_token,
          item_id: tokenResponse.data.item_id,
          error: null,
        });
      })
      .catch(next);
  });
  
  // Retrieve ACH or ETF Auth data for an Item's accounts
  // https://plaid.com/docs/#auth
  app.get('/plaid/:userId/auth', function (request, response, next) {
    getTokens(request.params.userId, (accessToken) => {
      Promise.resolve()
        .then(async function () {
          const authResponse = await client.authGet({
            access_token: accessToken,
          });
          prettyPrintResponse(authResponse);
          response.json(authResponse.data);
        })
        .catch(next);
    });
  });
  
  // Retrieve Transactions for an Item
  // https://plaid.com/docs/#transactions
  app.get('/plaid/:userId/transactions', function (request, response, next) {
    getTokens(request.params.userId, (accessToken) => {
      Promise.resolve()
      .then(async function () {
        // Set cursor to empty to receive all historical updates
        let cursor = null;
  
        // New transaction updates since "cursor"
        let added = [];
        let modified = [];
        // Removed transaction ids
        let removed = [];
        let hasMore = true;
        // Iterate through each page of new transaction updates for item
        while (hasMore) {
          const request = {
            access_token: accessToken,
            cursor,
          };
          const res = await client.transactionsSync(request)
          const data = res.data;
          // Add this page of results
          added = added.concat(data.added);
          modified = modified.concat(data.modified);
          removed = removed.concat(data.removed);
          hasMore = data.has_more;
          // Update cursor to the next cursor
          cursor = data.next_cursor;
          prettyPrintResponse(res);
        }
  
        const compareTxnsByDateAscending = (a, b) => (a.date > b.date) - (a.date < b.date);
        // Return the 100 most recent transactions
        const recently_added = [...added].sort(compareTxnsByDateAscending).slice(-100);
        saveTransactions(recently_added,request.params.userId,request.query.token,next);
        // console.log(recently_added);
        response.json({latest_transactions: recently_added});
      })
      .catch(next);
    });
  });
  
  // Retrieve Investment Transactions for an Item
  // https://plaid.com/docs/#investments
  app.get('/plaid/:userId/investments_transactions', function (request, response, next) {
    getTokens(request.params.userId, (accessToken) => {
      Promise.resolve()
      .then(async function () {
        const startDate = moment().subtract(30, 'days').format('YYYY-MM-DD');
        const endDate = moment().format('YYYY-MM-DD');
        const configs = {
          access_token: accessToken,
          start_date: startDate,
          end_date: endDate,
        };
        const investmentTransactionsResponse =
          await client.investmentsTransactionsGet(configs);
        prettyPrintResponse(investmentTransactionsResponse);
        response.json({
          error: null,
          investments_transactions: investmentTransactionsResponse.data,
        });
      })
      .catch(next);
    });
  });
  
  // Retrieve Identity for an Item
  // https://plaid.com/docs/#identity
  app.get('/plaid/:userId/identity', function (request, response, next) {
    getTokens(request.params.userId, (accessToken) => {
      Promise.resolve()
      .then(async function () {
        const identityResponse = await client.identityGet({
          access_token: accessToken,
        });
        prettyPrintResponse(identityResponse);
        response.json({ identity: identityResponse.data.accounts });
      })
      .catch(next);
    });
    
  });
  
  // Retrieve real-time Balances for each of an Item's accounts
  // https://plaid.com/docs/#balance
  app.get('/plaid/:userId/balance', function (request, response, next) {
    getTokens(request.params.userId, (accessToken) => {
      Promise.resolve()
      .then(async function () {
        const balanceResponse = await client.accountsBalanceGet({
          access_token: accessToken,
        });
        prettyPrintResponse(balanceResponse);
        response.json(balanceResponse.data);
      })
      .catch(next);
    });
  });
  
  // Retrieve Holdings for an Item
  // https://plaid.com/docs/#investments
  app.get('/plaid/:userId/holdings', function (request, response, next) {
    getTokens(request.params.userId, (accessToken) => {
      Promise.resolve()
      .then(async function () {
        const holdingsResponse = await client.investmentsHoldingsGet({
          access_token: accessToken,
        });
        prettyPrintResponse(holdingsResponse);
        response.json({ error: null, holdings: holdingsResponse.data });
      })
      .catch(next);
    });
  });
  
  // Retrieve Liabilities for an Item
  // https://plaid.com/docs/#liabilities
  app.get('/plaid/:userId/liabilities', function (request, response, next) {
    getTokens(request.params.userId, (accessToken) => {
      Promise.resolve()
      .then(async function () {
        const liabilitiesResponse = await client.liabilitiesGet({
          access_token: accessToken,
        });
        prettyPrintResponse(liabilitiesResponse);
        response.json({ error: null, liabilities: liabilitiesResponse.data });
      })
      .catch(next);
    });
  });
  
  // Retrieve information about an Item
  // https://plaid.com/docs/#retrieve-item
  app.get('/plaid/:userId/item', function (request, response, next) {
    getTokens(request.params.userId, (accessToken) => {
      Promise.resolve()
      .then(async function () {
        // Pull the Item - this includes information about available products,
        // billed products, webhook information, and more.
        const itemResponse = await client.itemGet({
          access_token: accessToken,
        });
        // Also pull information about the institution
        const configs = {
          institution_id: itemResponse.data.item.institution_id,
          country_codes: ['US'],
        };
        const instResponse = await client.institutionsGetById(configs);
        prettyPrintResponse(itemResponse);
        response.json({
          item: itemResponse.data.item,
          institution: instResponse.data.institution,
        });
      })
      .catch(next);
    });
  });
  
  // Retrieve an Item's accounts
  // https://plaid.com/docs/#accounts
  app.get('/plaid/:userId/accounts', function (request, response, next) {
    getTokens(request.params.userId, (accessToken) => {
      Promise.resolve()
      .then(async function () {
        const accountsResponse = await client.accountsGet({
          access_token: accessToken,
        });
        prettyPrintResponse(accountsResponse);
        response.json(accountsResponse.data);
      })
      .catch(next);
    });
  });
  
  // Create and then retrieve an Asset Report for one or more Items. Note that an
  // Asset Report can contain up to 100 items, but for simplicity we're only
  // including one Item here.
  // https://plaid.com/docs/#assets
  app.get('/plaid/:userId/assets', function (request, response, next) {
    getTokens(request.params.userId, (accessToken) => {
      Promise.resolve()
      .then(async function () {
        // You can specify up to two years of transaction history for an Asset
        // Report.
        const daysRequested = 10;
  
        // The `options` object allows you to specify a webhook for Asset Report
        // generation, as well as information that you want included in the Asset
        // Report. All fields are optional.
        const options = {
          client_report_id: 'Custom Report ID #123',
          // webhook: 'https://your-domain.tld/plaid-webhook',
          user: {
            client_user_id: 'Custom User ID #456',
            first_name: 'Alice',
            middle_name: 'Bobcat',
            last_name: 'Cranberry',
            ssn: '123-45-6789',
            phone_number: '555-123-4567',
            email: 'alice@example.com',
          },
        };
        const configs = {
          access_tokens: [accessToken],
          days_requested: daysRequested,
          options,
        };
        const assetReportCreateResponse = await client.assetReportCreate(configs);
        prettyPrintResponse(assetReportCreateResponse);
        const assetReportToken =
          assetReportCreateResponse.data.asset_report_token;
        const getResponse = await getAssetReportWithRetries(
          client,
          assetReportToken,
        );
        const pdfRequest = {
          asset_report_token: assetReportToken,
        };
  
        const pdfResponse = await client.assetReportPdfGet(pdfRequest, {
          responseType: 'arraybuffer',
        });
        prettyPrintResponse(getResponse);
        prettyPrintResponse(pdfResponse);
        response.json({
          json: getResponse.data.report,
          pdf: pdfResponse.data.toString('base64'),
        });
      })
      .catch(next);
    });
  });
  
  app.get('/plaid/:userId/transfer', function (request, response, next) {
    getTokens(request.params.userId, (accessToken,itemId,transferId) => {
      Promise.resolve()
      .then(async function () {
        const transferGetResponse = await client.transferGet({
          transfer_id: transferId,
        });
        prettyPrintResponse(transferGetResponse);
        response.json({
          error: null,
          transfer: transferGetResponse.data.transfer,
        });
      })
      .catch(next);
    })
  });
  
  // This functionality is only relevant for the UK Payment Initiation product.
  // Retrieve Payment for a specified Payment ID
  app.get('/plaid/:userId/payment', function (request, response, next) {
    getTokens(request.params.userId, (accessToken,itemId,transferId,paymentId) => {
      Promise.resolve()
      .then(async function () {
        const paymentGetResponse = await client.paymentInitiationPaymentGet({
          payment_id: paymentId,
        });
        prettyPrintResponse(paymentGetResponse);
        response.json({ error: null, payment: paymentGetResponse.data });
      })
      .catch(next);
    })
  });
  
  //TO-DO: This endpoint will be deprecated in the near future
  app.get('/plaid/:userId/income/verification/paystubs', function (request, response, next) {
    getTokens(request.params.userId, (accessToken) => {
      Promise.resolve()
      .then(async function () {
        const paystubsGetResponse = await client.incomeVerificationPaystubsGet({
          access_token: accessToken
        });
        prettyPrintResponse(paystubsGetResponse);
        response.json({ error: null, paystubs: paystubsGetResponse.data})
      })
      .catch(next);
    });
  })
  
  // app.use('/plaid', function (error, request, response, next) {
  //   prettyPrintResponse(error.response);
  //   response.json(formatError(error.response));
  // });
  
  
  const prettyPrintResponse = (response) => {
    // console.log(util.inspect(response.data, { colors: true, depth: 4 }));
  };
  
  // This is a helper function to poll for the completion of an Asset Report and
  // then send it in the response to the client. Alternatively, you can provide a
  // webhook in the `options` object in your `/asset_report/create` request to be
  // notified when the Asset Report is finished being generated.
  
  const getAssetReportWithRetries = (
    plaidClient,
    asset_report_token,
    ms = 1000,
    retriesLeft = 20,
  ) =>
    new Promise((resolve, reject) => {
      const request = {
        asset_report_token,
      };
  
      plaidClient
        .assetReportGet(request)
        .then(resolve)
        .catch(() => {
          setTimeout(() => {
            if (retriesLeft === 1) {
              reject('Ran out of retries while polling for asset report');
              return;
            }
            getAssetReportWithRetries(
              plaidClient,
              asset_report_token,
              ms,
              retriesLeft - 1,
            ).then(resolve);
          }, ms);
        });
    });
  
  // const formatError = (error) => {
  //   return {
  //     error: { ...error.data, status_code: error.status },
  //   };
  // };
  
  // This is a helper function to authorize and create a Transfer after successful
  // exchange of a public_token for an access_token. The TRANSFER_ID is then used
  // to obtain the data about that particular Transfer.
  
  const authorizeAndCreateTransfer = async (accessToken) => {
    // We call /accounts/get to obtain first account_id - in production,
    // account_id's should be persisted in a data store and retrieved
    // from there.
    const accountsResponse = await client.accountsGet({
      access_token: accessToken,
    });
    const accountId = accountsResponse.data.accounts[0].account_id;
  
    const transferAuthorizationResponse =
      await client.transferAuthorizationCreate({
        access_token: accessToken,
        account_id: accountId,
        type: 'credit',
        network: 'ach',
        amount: '1.34',
        ach_class: 'ppd',
        user: {
          legal_name: 'FirstName LastName',
          email_address: 'foobar@email.com',
          address: {
            street: '123 Main St.',
            city: 'San Francisco',
            region: 'CA',
            postal_code: '94053',
            country: 'US',
          },
        },
      });
    prettyPrintResponse(transferAuthorizationResponse);
    const authorizationId = transferAuthorizationResponse.data.authorization.id;
  
    const transferResponse = await client.transferCreate({
      idempotency_key: '1223abc456xyz7890001',
      access_token: accessToken,
      account_id: accountId,
      authorization_id: authorizationId,
      type: 'credit',
      network: 'ach',
      amount: '12.34',
      description: 'Payment',
      ach_class: 'ppd',
      user: {
        legal_name: 'FirstName LastName',
        email_address: 'foobar@email.com',
        address: {
          street: '123 Main St.',
          city: 'San Francisco',
          region: 'CA',
          postal_code: '94053',
          country: 'US',
        },
      },
    });
    prettyPrintResponse(transferResponse);
    return transferResponse.data.transfer.id;
  };

  const getTokens = (userId, callback, userToken = null) => {
    // console.log("fetching access token...")
    PlaidUser.findOne({userId}, (err,foundUser) => {
      if (err) console.log(err)
      if(!foundUser) creatPlaidUser(userId,null,null);
      else {
        callback(foundUser.data.accessToken,foundUser.data.itemId,foundUser.data.transferId,foundUser.data.paymentId);
      }
    })
  }
  const creatPlaidUser = (userId, accessToken, itemId, transferId = null, paymentId = null) => {
    // console.log("initializing new user...");
    const newUser = new PlaidUser({
      userId,
      data: {
        accessToken,
        itemId,
        transferId,
        paymentId
      }
    })
    newUser.save((err, foundUser) => {
      if (!err) {
        // console.log("newUser saved")
        const returnData = {
          accessToken: foundUser.data.accessToken,
          itemId: foundUser.data.itemId,
          transferId: foundUser.data.transferId,
          paymentId: foundUser.data.paymentId
        }
        return returnData;
      }
      else console.log(err);
    })
  }
  
}

