<template>
    <div>
        <!-- 分类侧栏 -->
        <div class="sideBar list">
            <div>
                <ul>
                    <li v-for="category in categoryList"
                        :key="category.id"
                        :class="[Number.parseInt(route.query.category as string || '0') == category.id ? 'activeSideBarItem' : '']"
                        @click="switchCategory(category.id)">
                        {{ category.name }}
                    </li>
                </ul>
            </div>
        </div>
        <!-- 数据展示的子组件 -->
        <BookList :total="bookTotal"
                  :books="books"
                  :pageSize="pageSize"
                  @turn-page="switchPageNo"></BookList>
    </div>
</template>

<script setup lang='ts'>
import { ref, onMounted, watch } from 'vue'
import hRequest from '../utils/HRequest'
import { useRoute, useRouter } from 'vue-router'
import BookList from './BookList.vue'

// router是vue-router的实例可以用来跳转路由，route是当前路由的信息(url,query,params等)
const router = useRouter();
const route = useRoute();

// 分类信息
const categoryList = ref<category[]>([{ id: 0, name: '全部' }])
// 当前选中的分类
const activeCategory = ref<number>()

const bookTotal = ref<number>(0)
const books = ref<bookProfile[]>([])
const pageSize = 10

// 页面加载时请求数据
onMounted(async () => {
    // 请求分类数据
    categoryList.value.push(...(await hRequest.post('CategoryListServlet')).data.data)
    // 请求书籍数据
    getBooksByCategoryId()
})

// watch监听路由变化，更具变化后路由的categoryId,页面信息重新请求数据
watch(() => [route.query, route.query.category, route.query.page], (newValue) => {
    getBooksByCategoryId()
})

// 把分类信息写入路由
const switchCategory = async (categoryId: number) => {
    if (activeCategory.value == categoryId) return
    if (categoryId == 0) router.push('/')
    else router.push({ path: "/", query: { category: categoryId } })

    activeCategory.value = categoryId
}
// 接受子组件的换页事件，把页码信息写入路由
const switchPageNo = (pageNo: number) => {
    let query
    if (activeCategory.value == 0) query = { page: pageNo }
    else query = { category: activeCategory.value, page: pageNo }

    router.push({ path: '/', query: query })
}

// 获取bookProfile数据
const getBooksByCategoryId = async () => {
    // 请求书籍数据
    let param = new URLSearchParams()
    param.append('categoryId', route.query.category as string || '0')
    param.append('pageNo', route.query.page as string || '1')
    param.append('pageSize', `${pageSize}`)
    // 发送请求
    const data = (await hRequest.post('BookProfilesServlet', param)).data.data
    // 把数据赋值给响应式变量
    bookTotal.value = data.total
    books.value = data.bookProfiles
}
</script>

<style scoped></style>