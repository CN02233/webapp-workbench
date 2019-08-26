'use strict'
const merge = require('webpack-merge')
const prodEnv = require('./prod.env')

module.exports = merge(prodEnv, {
  NODE_ENV: '"development"',
  BASE_API:'"http://localhost:8080/service/"'
  // BASE_API:'"http://192.168.43.10:8080/service"'
})
