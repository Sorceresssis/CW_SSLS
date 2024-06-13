<template>
    <div style="display: flex;flex: 1;">
        <div class="manageHeader">
            <el-select v-model="activeCategory"
                       class="m-2"
                       placeholder="选择分类"
                       size="large">
                <el-option v-for="item in categoryList"
                           :key="item.id"
                           :label="item.name"
                           :value="item.id" />
            </el-select>
            <button class="button"
                    @click="router.push('/admin/editBook')"><span>添加书籍</span></button>
        </div>
        <!-- 数据展示的子组件 -->
        <BookList :total="bookTotal"
                  :books="books"
                  :pageSize="pageSize"
                  :clickPath="'/admin/editBook'"
                  style="/* min-height的妙用重新计算高度 */min-height: 0;"
                  @turn-page="switchPageNo"></BookList>
    </div>
</template>

<script setup lang='ts'>
import { ref, onMounted, watch } from 'vue'
import hRequest from '../../utils/HRequest'
import { useRoute, useRouter } from 'vue-router'
import BookList from '../BookList.vue'

const router = useRouter();
const route = useRoute();

const activeCategory = ref<number>()   // 当前选中的分类
const categoryList = ref<category[]>([{ id: 0, name: '全部' }]) // 分类信息

const bookTotal = ref<number>(0)
const books = ref<bookProfile[]>([])
const pageSize = 10

// 把分类信息写入路由
watch(activeCategory, (newvalue) => {
    if (newvalue === 0) router.push('/admin/manageBooks')
    else router.push({ path: '/admin/manageBooks', query: { category: newvalue } })
})
// 接受子组件的换页事件，把页码信息写入路由
const switchPageNo = (pageNo: number) => {
    let query
    if (activeCategory.value == 0) query = { page: pageNo }
    else query = { category: activeCategory.value, page: pageNo }
    router.push({ path: '/admin/manageBooks', query: query })
}

watch(() => [route.query], () => {
    getBooksByCategoryId()
})

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

// 页面加载时请求数据
onMounted(async () => {
    // 请求分类数据
    categoryList.value.push(...(await hRequest.post('CategoryListServlet')).data.data)
    // 读取url中的分类信息
    activeCategory.value = Number(route.query.category) || 0
    // 请求书籍数据
    getBooksByCategoryId()
})
</script>

<style scoped>
.manageHeader {
    display: flex;
    height: 36px;
    padding: 0 20px;
    align-items: center;
    margin-bottom: 10px;
}

.manageHeader>* {
    margin: 0 20px;
}
</style>