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
        path: `/welcome`,
        component: () => import("@/models/home/welcome")
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
            name: 'supervisionUser',
            path: '/sys/supervisionUser',
            component: () => import('@/models/sys/user/supervisionUserMain')
          },
          {
            name: 'role',
            path: '/sys/role',
            component: () => import('@/models/sys/role/roleMain')
          },
          {
            name: 'origin',
            path: '/sys/origin',
            component: () => import('@/models/sys/origin/originMain')
          }

        ]
      },
      {
        name: 'record',
        path: '/record',
        component: () => import('@/models/record/main'),
        children: [
          {
            name: 'reportDefined',
            path: '/record/reportDefined',
            component: () => import('@/models/record/reportDefined'),
            children: [
              {
                name: 'reportStatements',
                path: '/record/reportStatements',
                component: () => import('@/models/record/reportDefined/unit/reportstatements'),
                children: []
              },
              {
                name: 'reportUnit',
                path: '/record/reportUnit',
                component: () => import('@/models/record/reportDefined/unit/reportunit'),
                children: []
              },
              {
                name: 'oneDimensionsStatic',
                path: '/record/reportDefined/oneDimensionsStatic',
                component: () => import('@/models//record/reportDefined/unit/oneDimensionsStatic/main'),
                children: []
              }
              ,
              {
                name: 'oneDimensionsDynamic',
                  path: '/record/reportDefined/oneDimensionsDynamic',
                component: () => import('@/models//record/reportDefined/unit/oneDimensionsDynamic/main'),
                children: []
              },
              {
                name: 'multDimensionsStatic',
                path: '/record/reportDefined/multDimensionsStatic',
                component: () => import('@/models//record/reportDefined/unit/multDimensionsStatic/main'),
                children: []
              },
              {
                name: 'treeMultDiensionsDynamic',
                path: '/record/reportDefined/treeMultDiensionsDynamic',
                component: () => import('@/models//record/reportDefined/unit/treeMultDiensionsDynamic/main'),
                children: []
              }
            ]
          },
          {
            name: 'reportFill',
            path: '/record/report/reportFill',
            component: () => import('@/models/record/report/reportFill'),
            children: [
              {
                name: 'oneDimensionsStaticRecord',
                path: '/record/onedim/onedimRecord',
                component: () => import('@/models//record/report/onedim/onedimRecord'),
              },
              {
                name: 'oneDimensionsStaticRecord',
                path: '/record/onedim/onedimRecord',
                component: () => import('@/models//record/report/onedim/onedimRecord'),
              },
              {
                name: 'treeDimensionsDynRecord',
                path: '/record/treedim/treedimRecord',
                component: () => import('@/models//record/report/treedim/treedimRecord'),
              },
              {
                name: 'oneDimensionsDynamicRecord',
                path: '/record/groupdim/groupdimRecord',
                component: () => import('@/models//record/report/groupdim/groupdimRecord'),
              },
              {
                name: 'multDimensionsStaticRecord',
                path: '/record/multdim/griddimRecord',
                component: () => import('@/models//record/report/multdim/griddimRecord'),
              }
            ]
          },
          {
            name: 'submitAuthority',
            path: '/record/submitAuthority',
            component: () => import('@/models/record/submitAuthority'),
            children: [
              {
                name: 'submitAUmanager',
                path: '/record/submitAUmanager',
                component: () => import('@/models/record/submitAuthority/submitAUmanager'),
                children: []
              },{
                name: 'administrative',
                path: '/record/administrative',
                component: () => import('@/models/record/submitAuthority/administrative'),
                children: []
              },{
                name: 'reportApproval',
                path: '/record/reportApproval',
                component: () => import('@/models/record/submitAuthority/reportapproval'),
                children: []
              },{
                name: 'reportReview',
                path: '/record/reportReview',
                component: () => import('@/models/record/submitAuthority/reportreview'),
                children: []
              },{
                name: 'reportSupervision',
                path: '/record/reportSupervision',
                component: () => import('@/models/record/submitAuthority/reportsupervision'),
                children: []
              }
            ]
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
