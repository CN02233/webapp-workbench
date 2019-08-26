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
    props: { menuAlign: 'top' },//left==>菜单在左侧 top==>菜单在上方
    // props: { menuAlign: 'left' },//left==>菜单在左侧 top==>菜单在上方
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
        path: '/lexicalAnalyze',
        component: () => import('@/models/SemanticAnalysis/lexicalAnalyze/main'),
        children:[
          {
            name: 'baseModel',
            path: '/lexicalAnalyze/baseModel',
            component: () => import('@/models/SemanticAnalysis/lexicalAnalyze/baseModel')
          },
          {
            name: 'advancedModel',
            path: '/lexicalAnalyze/advancedModel',
            component: () => import('@/models/SemanticAnalysis/lexicalAnalyze/advancedModel'),
            children:[
              {
                name:'chooseDataSource',
                path: '/lexicalAnalyze/advancedModel/chooseDataSource',
                component: () => import('@/models/SemanticAnalysis/lexicalAnalyze/advancedModel/chooseDataSource')
              }, {
                name:'editWdMarketTmp',
                path: '/lexicalAnalyze/advancedModel/editWdMarketTmp',
                component: () => import('@/models/SemanticAnalysis/lexicalAnalyze/advancedModel/editWdMarketTmp')
              }, {
                name:'preSplitWord',
                path: '/lexicalAnalyze/advancedModel/preSplitWord',
                component: () => import('@/models/SemanticAnalysis/lexicalAnalyze/advancedModel/preSplitWord')
              }, {
                name:'updateWordMarket',
                path: '/lexicalAnalyze/advancedModel/updateWordMarket',
                component: () => import('@/models/SemanticAnalysis/lexicalAnalyze/advancedModel/updateWordMarket')
              }, {
                name:'validateSplitWord',
                path: '/lexicalAnalyze/advancedModel/validateSplitWord',
                component: () => import('@/models/SemanticAnalysis/lexicalAnalyze/advancedModel/validateSplitWord')
              }
            ]
          },
          {
            name:'baseModelResult',
            path: '/lexicalAnalyze/baseModelResult',
            component: () => import('@/models/SemanticAnalysis/lexicalAnalyze/baseModelResult')
          }
        ]
      },
      {
        path: '/contextExtract',
        component: () => import('@/models/SemanticAnalysis/contextExtract/main'),
        children:[
          {
            name: 'contextAbstract',
            path: '/contextExtract/contextAbstract',
            component: () => import('@/models/SemanticAnalysis/contextExtract/contextAbstract')
          },
          {
            name:'contextAbstractResult',
            path: '/contextExtract/contextAbstractResult',
            component: () => import('@/models/SemanticAnalysis/contextExtract/contextAbstractResult')
          },
          {
            path: '/contextExtract/sameTitle',
            component: () => import('@/models/SemanticAnalysis/contextExtract/sameTitle')
          }
        ]
      },
      {
        name:'systemManage',
        path: '/systemManage',
        component: () => import('@/models/SemanticAnalysis/systemManage/main'),
        children:[
          {
            name:'cnWordManage',
            path: '/systemManage/cnWordManage',
            component: () => import('@/models/SemanticAnalysis/systemManage/cnWordManage')
          },
          {
            name:'dataSourceManage',
            path: '/systemManage/dataSourceManage',
            component: () => import('@/models/SemanticAnalysis/systemManage/dataSourceManage')
          },{
            path: '/systemManage/enWordManage',
            component: () => import('@/models/SemanticAnalysis/systemManage/enWordManage')
          },{
            path: '/systemManage/groupRoleManage',
            component: () => import('@/models/SemanticAnalysis/systemManage/groupRoleManage')
          },{
            path: '/systemManage/localWordManage',
            component: () => import('@/models/SemanticAnalysis/systemManage/localWordManage')
          },{
            path: '/systemManage/synonymWordManage',
            component: () => import('@/models/SemanticAnalysis/systemManage/synonymWordManage')
          },{
            path: '/systemManage/sensitiveWordManage',
            component: () => import('@/models/SemanticAnalysis/systemManage/sensitiveWordManage')
          },{
            path: '/systemManage/sensitiveWordRule',
              component: () => import('@/models/SemanticAnalysis/systemManage/sensitiveWordRule')
          },{
            path: '/systemManage/sensitiveWarning',
              component: () => import('@/models/SemanticAnalysis/systemManage/sensitiveWarning')
          }
        ]
      },
      {
        path: '/sentimentAnalyze',
        component: () => import('@/models/SemanticAnalysis/sentimentAnalyze/main'),
      },
      {
        name:"sentimentAnalyzeResult",
        path:"/sentimentAnalyze/result",
          component: () => import('@/models/SemanticAnalysis/sentimentAnalyze/result')
      },
      {
        path: '/textGroup',
        component: () => import('@/models/SemanticAnalysis/textGroup/main'),
        children:[
          {
            name: 'roleGroup',
            path: '/textGroup/roleGroup',
            component: () => import('@/models/SemanticAnalysis/textGroup/roleGroup')
          },
          {
            name:'roleGroupResult',
            path: '/textGroup/roleGroupResult',
              component: () => import('@/models/SemanticAnalysis/textGroup/roleGroupResult')
          },
          {
            name: 'txtSum',
            path: '/textGroup/txtSum',
            component: () => import('@/models/SemanticAnalysis/textGroup/txtSum')
          },
          {
            name: 'moreTitleTxt',
            path: '/textGroup/moreTitleTxt',
            component: () => import('@/models/SemanticAnalysis/textGroup/moreTitleTxt')
          },
          {
            name: 'moreTitleTxtResult',
            path: '/textGroup/moreTitleTxtResult',
            component: () => import('@/models/SemanticAnalysis/textGroup/moreTitleTxtResult')
          },
          {
            name: 'textSimilarityResult',
              path: '/textGroup/textSimilarityResult',
            component: () => import('@/models/SemanticAnalysis/textGroup/textSimilarityResult')
          },
          {
            name: 'textSimilarity',
              path: '/textGroup/textSimilarity',
            component: () => import('@/models/SemanticAnalysis/textGroup/textSimilarity')
          },
          {
            name: 'txtSumResult',
            path: '/textGroup/txtSumResult',
            component: () => import('@/models/SemanticAnalysis/textGroup/txtSumResult')
          },
          {
            name: 'localModel',
              path: '/textGroup/localModel',
            component: () => import('@/models/SemanticAnalysis/textGroup/localModel')
          },
          {
            name: 'localModelResult',
              path: '/textGroup/localModelResult',
            component: () => import('@/models/SemanticAnalysis/textGroup/localModelResult')
          },
          {
            name: 'hotWordFind',
              path: '/textGroup/hotWordFind',
            component: () => import('@/models/SemanticAnalysis/textGroup/hotWordFind')
          }
        ]
      },
      {
        name:"modelManage",
        path:"/modelManage",
        component: () => import('@/models/SemanticAnalysis/modelManage/main'),
        children:[
          {
            name: 'splitWord',
            path: '/modelManage/splitWord',
            component: () => import('@/models/SemanticAnalysis/modelManage/splitWord'),
            children:[
              {
                name:'importWord',
                path: '/modelManage/splitWord/importWord',
                component: () => import('@/models/SemanticAnalysis/modelManage/splitWordSteps/importWord')
              },
              {
                name:'trainingWord',
                path: '/modelManage/splitWord/trainingWord',
                component: () => import('@/models/SemanticAnalysis/modelManage/splitWordSteps/trainingWord')
              },
              {
                name:'resultScore',
                  path: '/modelManage/splitWord/resultScore',
                component: () => import('@/models/SemanticAnalysis/modelManage/splitWordSteps/resultScore')
              },{
                name:'viewSimple',
                  path: '/modelManage/splitWord/viewSimple',
                  component: () => import('@/models/SemanticAnalysis/modelManage/splitWordSteps/viewSimple')
              }

            ]
          },
          {
            name: 'emotionModel',
            path: '/modelManage/emotionModel',
            component: () => import('@/models/SemanticAnalysis/modelManage/emotionModel'),
            children:[
              {
                name:'emotionImportWord',
                path: '/modelManage/emotionModel/importWord',
                component: () => import('@/models/SemanticAnalysis/modelManage/emotionModelSteps/importWord')
              },
              {
                name:'emotionTrainingWord',
                  path: '/modelManage/emotionModel/trainingWord',
                component: () => import('@/models/SemanticAnalysis/modelManage/emotionModelSteps/trainingWord')
              },
              {
                name:'emotionResultScore',
                  path: '/modelManage/emotionModel/resultScore',
                component: () => import('@/models/SemanticAnalysis/modelManage/emotionModelSteps/resultScore')
              }
            ]
          },
          {
            name: 'typeModel',
              path: '/modelManage/typeModel',
            component: () => import('@/models/SemanticAnalysis/modelManage/typeModel'),
            children:[
              {
                name:'typeModelImportWord',
                path: '/modelManage/typeModel/importWord',
                component: () => import('@/models/SemanticAnalysis/modelManage/typeModelSteps/importWord')
              },
              {
                name:'typeModelTrainingWord',
                path: '/modelManage/typeModel/trainingWord',
                component: () => import('@/models/SemanticAnalysis/modelManage/typeModelSteps/trainingWord')
              },
              {
                name:'typeModelResultScore',
                path: '/modelManage/typeModel/resultScore',
                component: () => import('@/models/SemanticAnalysis/modelManage/typeModelSteps/resultScore')
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
