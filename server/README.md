# Backend

#### Testing the endpoints
To continue with the last pull request, the accounts/goal/transactions endpoints and models are finalized.

Start the server.

#### Instructions to test plaid api setup
1. Clone https://github.com/nekopudding/quickstart and start the react frontend server and `npm i` to install deps. 
2. Add the .env file from our discord server under 'credentials' to /server folder under the test repo. 
3. create a new User account using postman (send a `create user` request under /accounts folder) - copy the obtained token and user_id  into /frontend/src/globals.tsx under the test repo (make sure the backend server is started)
4. Install deps using `npm i` and start the backend server using `npm start`

currently using the sandbox environment, so choose any bank service and the username is `user_good` and password is `pass_good`.

#### Instructions to test stripe api setup
1. make a post request using postman to /stripe/checkout/sessions/:userId with a valid token
2. The response should return a url, click on it to redirect to checkout page
3. fill in the payment details as followed https://stripe.com/docs/testing?numbers-or-method-or-token=card-numbers#visa, after submitting, the user should now be subscribed for the next 31 days.

**_NOTE:_** I'm not sure how renewing/cancelling subscriptions work so I haven't set it up. 

