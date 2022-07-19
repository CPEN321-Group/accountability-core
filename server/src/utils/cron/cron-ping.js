//uses node-cron to schedule creation of monthly reports
const cron = require('node-cron');

cron.schedule('0 0 1 * *', function() {
  console.log('running a task every minute');
});