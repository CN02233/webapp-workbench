import Vue from 'vue'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import VueRouter from "vue-router";
import { Message } from 'element-ui';
import {SemanticAnalysisRequest,SemanticAnalysisBaseRequest} from '@/utils'
import router from "./router"
import axios from 'axios'
import Vuelidate from 'vuelidate'
import App from './App.vue'
import '@/icons'
import Icon from 'vue-awesome/components/Icon'
Vue.component('icon', Icon)


import "@/styles/index.scss"

Vue.use(VueRouter)
Vue.use(ElementUI);
Vue.use(Vuelidate)
Vue.prototype.Message = Message
Vue.prototype.Request = SemanticAnalysisRequest
Vue.prototype.$http = axios
Vue.prototype.BaseRequest = SemanticAnalysisBaseRequest

// Vue.config.productionTip = false

new Vue({
  el: '#app',
  router,
  axios,
  render: h => h(App)
})