<template>
  <div class="cerify-code-panel">
    <div class="verify-code"
         @click="setCode"
         :style="{
                    'background-color': containerBackgroundColor,
                    'color': containerColor
                }">
      <!-- 显示字符串 -->
      <span :style="code.style" v-for="code in codeShow">
                {{code.char || code}}
            </span>
    </div>
  </div>
</template>
<script type="text/babel">
  /**
   * Code
   * @description 常规的图片文字识别或者数字计算
   * */

  export default {
    name: 'WorkVerifyCode',
    data() {
      return {
        isEnd: false,
        // 输入的值
        inputValue: '',
        // 颜色
        containerBackgroundColor: '#fff',
        containerColor: '#fff',
        code_color1 : ['#fffff0', '#f0ffff', '#f0fff0', '#fff0f0'],
        code_color2 : ['#FF0033', '#006699', '#993366', '#FF9900', '#66CC66', '#FF33CC'],
        code_chars : [1, 2, 3, 4, 5, 6, 7, 8, 9, 'a', 'b', 'c', 'd', 'e', 'f', 'g',
        'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
        'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
        'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'],
        codeChose: '', // 应该输入的code
        codeShow: [] // 显示用的
      }
    },
    methods: {
      init() {
        this.setCode()
      },

      /**
       * setCode
       * @description 设置验证码
       * */
      setCode() {
        if (this.isEnd == false) {

          this.containerBackgroundColor = this.code_color1[Math.floor(Math.random() * 3)]
          this.containerColor = this.code_color2[Math.floor(Math.random() * 5)]

          this.inputValue = ''

          this.codeShow = []
          this.codeChose = ''


          this.BaseRequest({
            url:"/cqnyUser/getValidateCode",
            method:'get',
            params:{"user_name":"admin"}
          }).then(response=>{
            // this.$emit("refreshSaveLoading",this.unitId,"保存成功")
            // this.$emit("checkStepAndSave",this.unitId,this.saveFlag)
            for (let i = 0; i < response.length; i++) {
              // let charNum = Math.floor(Math.random() * 52)
              let charNum = response.substring(i,(i+1))
              let tmpStyle = (charNum % 2 == 0) ? "font-style:italic;margin-right: 10px" : "font-weight:bolder"
              tmpStyle += (Math.floor(Math.random() * 2) == 1) ? "font-weight:bolder" : ""

              this.codeChose += charNum
              this.codeShow.push({
                style: tmpStyle,
                char: charNum
              })
            }
          }).catch(error => {
          });

          // for (let i = 0; i < 4; i++) {
          //   let charNum = Math.floor(Math.random() * 52)
          //   let tmpStyle = (charNum % 2 == 0) ? "font-style:italic;margin-right: 10px" : "font-weight:bolder"
          //   tmpStyle += (Math.floor(Math.random() * 2) == 1) ? "font-weight:bolder" : ""
          //
          //   this.codeChose += this.code_chars[charNum]
          //   this.codeShow.push({
          //     style: tmpStyle,
          //     char: this.code_chars[charNum]
          //   })
          // }
        }
      },
      refresh() {
        this.isEnd = false
        this.inputValue = ''
        this.setCode()
      }
    },
    mounted:function(){
      this.init()

    },
    i18n: {
      messages: {
        'en-US': {},
        'zh-CN': {}
      }
    }
  }
</script>

<style scoped>
  .verify-code{
    height:100%;
    height:100%;
  }
</style>
