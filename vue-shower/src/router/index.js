import Vue from 'vue'
import VueRouter from 'vue-router'

// 引入组件
import login from '@/models/login.vue'
import casLogin from '@/models/casLogin.vue'
import home from '@/models/home/homeBridging.vue'

// 要告诉 vue 使用 vueRouter
Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    // redirect: 'casLogin'
    redirect: 'login'
  },
  {
    path: `/login`,
    component: login
  },
  {
    path: `/casLogin`,
    component: casLogin
  },
  {
    path: `/home`,
    component: home,
    // props: { menuAlign: 'top' },//left==>菜单在左侧 top==>菜单在上方
    props: { menuAlign: 'left' },//left==>菜单在左侧 top==>菜单在上方
    children: [
      {
        path: '/infoInput',
        component: () => import("@/models/infoManage/infoInput")
      },
      {
        path: `/welcome`,
        component: () => import("@/models/home/welcome")
      },
      {
        path: '/infoList',
        component: () => import('@/models/infoManage/infoList')
      },
      {
        name: 'auth',
        path: '/auth',
        component: () => import('@/models/sys/main'),
        children:[
          {
            name: 'menu',
            path: '/sys/menu',
            component: () => import('@/models/sys/menu/menuMain')
          },
          {
            name: 'user',
            path: '/sys/user',
            component: () => import('@/models/sys/user/userMain')
          },
          {
            name: 'role',
            path: '/sys/role',
            component: () => import('@/models/sys/role/roleMain')
          }

        ]
      },
      {
        name: 'record',
        path: '/record',
        component: () => import('@/models/record/main'),
        children:[
          {
            name: 'report',
            path: '/record/report',
            component: () => import('@/models/record/report/reportMain'),
            children:[]
          },
          {
            name: 'template',
            path: '/record/template',
            component: () => import('@/models/record/template/templateMain'),
            children:[]
          },
          {
            name:'reportCreate',
            path: '/record/report/edit',
            component: () => import('@/models/record/report/reportEdit')
          }
        ]
      }
    ]
  }
]

var router =  new VueRouter({
  routes
})
export default router
