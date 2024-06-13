<template>
    <div class="flex-autoWidth--col">
        <div class="pagination">
            <el-pagination layout="prev, pager, next, jumper, total"
                           :page-size="pageSize"
                           :current-page="Number.parseInt(route.query.page as string || '1')"
                           :total="total"
                           @current-change="(value: number) => router.push({
                               path: '/user/borrowHistory',
                               query: { page: value }
                           })" />
        </div>
        <div v-if="borrowHistory.length == 0"
             class="empty">
            没有归还记录哦
        </div>
        <div v-else
             class="historys thumbnail list">
            <div v-for="history in borrowHistory"
                 :key="history.id">
                <div>
                    <div>
                        <img loading="lazy"
                             :src="`${imageBaseUrl}${history.imageUrl}`"
                             @error="($event.target as HTMLImageElement).src = '../assets/images/nodata.png'"
                             style="width: 100%; height: 100%; object-fit: cover;">
                    </div>
                    <div>{{ history.bookName }}</div>
                    <div>{{ history.authors }}</div>
                    <div>借阅时间：{{ history.borrowDate }}</div>
                    <div>还书时间：{{ history.returnDate }}</div>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup lang='ts'>
import { ref, onMounted, watch, inject } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import hRequest from '../../utils/HRequest';

const imageBaseUrl = inject('imageBaseUrl')

const route = useRoute()
const router = useRouter()

const pageSize = 10
const total = ref<number>(0)
const borrowHistory = ref<BorrowHistoryInfo[]>([])

onMounted(async () => {
    // 启动时获取第一页数据
    await getBorrowHistory(route.query.page as string || '1')
})

// 监听路由中页码参数变化
watch(() => route.query.page, (newValue) => {
    getBorrowHistory(newValue as string)
})

// 获取借阅历史
const getBorrowHistory = async (pageNo: string) => {
    let param = new URLSearchParams()
    param.append('pageNo', pageNo)
    param.append('pageSize', `${pageSize}`)
    const data = (await hRequest.post('user/BorrowHistoryServlet', param)).data.data
    total.value = data.total
    borrowHistory.value = data.borrowHistoryInfos
}
</script>

<style scoped>
.historys {
    grid-template-columns: repeat(auto-fill, 250px);
    padding: 0 10px;
}
</style>