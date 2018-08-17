import Vue from 'vue'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import VueRouter from "vue-router";
import { Message } from 'element-ui';

Vue.use(VueRouter)


import App from './App.vue'

import router from "./router"

import axios from 'axios'

Vue.use(ElementUI);
Vue.prototype.$http = axios
Vue.prototype.Message = Message

// Vue.config.productionTip = false

new Vue({
  el: '#app',
  router,
  axios,
  render: h => h(App)
})