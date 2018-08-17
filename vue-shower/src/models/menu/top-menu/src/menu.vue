<template>
    <router-link :to="menuClick()">
      <div class="work-menu">
        {{menu.name}}
      </div>
    </router-link>


</template>

<script>
  export default {
    name: 'WorkTopMenu',
    data() {
        return {}
    },
    props:{
      menu:{
        type: Object
      }
    },
    methods: {
      hasChildren:function(){
        let hasChildren = false;
        if(this.menu.children&&this.menu.children.length>0){
          hasChildren = true;
        }
        this.menu.hasChildren = hasChildren;
        return hasChildren;
      },
      menuClick:function(){
        if(!this.menu.hasChildren){
          this.menu.hasClick = true;
          console.log(this.menu.name)
          return this.menu.url;
        }
      }
    }
  }
</script>

<style rel="stylesheet/scss" lang="scss" scoped>

  .work-menu{
    height:100%;
    margin:0 10px 0 10px;
    padding:0 10px 0 10px;
    color:#ffffff;
    font-size:16px ;
    float: left;
    cursor: pointer;
    min-width: 100px;
    text-align: center;
    &:hover{
      background-color: #0000fe;
    }
  }

  .work-menu-click{
    background-color: #0000fe;
  }

</style>
