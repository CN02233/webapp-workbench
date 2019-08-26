'use strict'
const merge = require('webpack-merge')
const prodEnv = require('./prod.env')

module.exports = merge(prodEnv, {
  NODE_ENV: '"development"',
  // BASE_API:'"http://10.10.10.41:9080/service/"'
  BASE_API:'"http://10.10.0.48:8080/service"'
})
