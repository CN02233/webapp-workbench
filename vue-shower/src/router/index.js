import Vue from 'vue'
import VueRouter from 'vue-router'

// 引入组件
import login from '@/models/login.vue'
// import home from '@/models/home.vue'
import home from '@/models/menuTopHome.vue'

// 要告诉 vue 使用 vueRouter
Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    redirect: 'login'
  },
  {
    path: `/login`,
    component: login
  },
  {
    path: `/home`,
    component: home,
    children: [
      { path: '/infoInput', component: () => import('@/models/infoManage/infoInput')},
      { path: '/infoInput1', component: () => import('@/models/infoManage/infoInput')},
    ]
  }
]

var router =  new VueRouter({
  routes
})
export default router
