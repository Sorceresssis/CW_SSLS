<template>
    <div>
        <BookList :total="bookTotal"
                  :books="books"
                  @turn-page="(value: number) => pageNo = value"></BookList>
    </div>
</template>

<script setup lang='ts'>
import { ref, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import hRequest from '../utils/HRequest'
import BookList from './BookList.vue'
const route = useRoute()

onMounted(async () => {
    // 获取搜索词
    getBookProfilesByKeyWord(route.query.keyWord as string, 1, 20)
})

// 监听搜索词变化。当搜索词变化时，重新获取数据
watch(() => route.query.keyWord, (newValue) => {
    getBookProfilesByKeyWord(newValue as string, 1, 20)
})

const pageNo = ref<number>(0)
const bookTotal = ref<number>(0)
const books = ref<bookProfile[]>([])

// 获取bookProfile数据
const getBookProfilesByKeyWord = async (keyWord: string, pageNo: number, pageSize: number) => {
    let param = new URLSearchParams()
    param.append('keyWord', `${keyWord}`)
    param.append('pageNo', `${pageNo}`)
    param.append('pageSize', `${pageSize}`)
    const data = (await hRequest.post('BookProfilesSearchServlet', param)).data.data
    bookTotal.value = data.total
    books.value = data.bookProfiles
}
</script>

<style scoped></style>