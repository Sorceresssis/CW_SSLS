<template>
	<div class="layout">
		<Header></Header>
		<div class="main-content">
			<router-view class="content-wrap"></router-view>
		</div>
		<Footer></Footer>
	</div>
</template>

<script setup lang='ts'>
import { provide, ref } from 'vue'
import Header from './views/Header.vue'
import Footer from './views/Footer.vue'

// 全局依赖注入登陆信息，这样只要是子组件都可以得到登陆信息
// provide/inject类似与js的原型链，其本质也是用原型链实现的
const isLogin = ref<boolean>(false)
provide('isLogin', isLogin)
// 图片的基础路径
provide('imageBaseUrl', 'http://localhost:8080/SSLS/bookImg/')
</script>

<style scoped>
.layout {
	display: flex;
	flex-direction: column;
	width: 100%;
	height: 100%;
	overflow: hidden;
}

.main-content {
	flex: 1;
	padding: 20px 0;
	overflow: hidden;
}

.content-wrap {
	display: flex;
	overflow: hidden;
	height: 100%;
	justify-content: center;
	align-items: top;
	padding: 0 10%;
}
</style>