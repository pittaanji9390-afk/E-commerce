const { write } = require('./generator_helper');
const fs = require('fs');

console.log('Generating comprehensive enterprise marketplace codebase...');

// Run child generators
require('./gen_b2b');
require('./gen_wms');
require('./gen_subscriptions');
require('./gen_loyalty_ads_fraud');
require('./gen_enterprise_expansion');
require('./gen_massive_prod_loc');
require('./build_complete_enterprise_layers');

console.log('All individual generators executed successfully.');
`);
