const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: true,
  pluginOptions: {
    html: {
      title: 'Ther Florist'
    }
  }
})
