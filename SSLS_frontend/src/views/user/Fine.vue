<template>
    <div>
        <div v-if="fineInfos.length == 0"
             class="empty">
            没有罚款记录哦
        </div>
        <div v-else
             class="thumbnail list">
            <div v-for="fineInfo in fineInfos"
                 :key="fineInfo.id"
                 style="display: flex; border-bottom: 1px dashed #ccc; padding: 10px 0;">
                <div>
                    <div style="margin-right: 10px; width: 150px;">
                        <img loading="lazy"
                             :src="`${imageBaseUrl}${fineInfo.imageUrl}`"
                             @error="($event.target as HTMLImageElement).src = '../assets/images/nodata.png'"
                             style="width: 100%; height: 100%; object-fit: cover;">
                    </div>
                    <div>
                        <div>{{ fineInfo.bookName }}</div>
                        <div>{{ fineInfo.authors }}</div>
                    </div>
                </div>
                <div style="flex: 1;">
                    <div style="height: 150px;">
                        <div>借阅时间：{{ fineInfo.borrowDate }}</div>
                        <div>应还时间：{{ fineInfo.dueDate }}</div>
                        <div>实还时间：{{ fineInfo.returnDate }}</div>
                        <div>逾期：{{ fineInfo.overdue }}天</div>
                    </div>
                    <div>
                        <div>应缴纳罚款：{{ fineInfo.amount }}元</div>
                        <div v-if="fineInfo.status == 0">
                            <el-button @click="payFine(fineInfo.id)"
                                       type="danger">缴纳</el-button>
                        </div>
                        <div v-else
                             class="allow">
                            缴纳日期：{{ fineInfo.date }}
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup lang='ts'>
import { ref, onMounted, inject } from 'vue'
import hRequest from '../../utils/HRequest'
import { throttle } from '../../utils/throttle'
import { ElMessage } from 'element-plus'

// 启动时加载罚款信息
onMounted(async () => {
    fineInfos.value = (await hRequest.post('user/FineListServlet')).data.data
})

const imageBaseUrl = inject('imageBaseUrl')

const fineInfos = ref<FineInfo[]>([])

// 缴纳罚款
const payFine = throttle(async (fineId: number) => {
    let resp = (await hRequest.post(`user/PayFineServlet?fineId=${fineId}`)).data as Result
    if (resp.code == 1) {
        ElMessage.success(`缴纳成功,余额剩余${resp.data}元`)
    } else {
        ElMessage.error(resp.msg)
    }
    fineInfos.value = (await hRequest.post('user/FineListServlet')).data.data;
}, 500)
</script>

<style scoped>
.list {
    grid-template-columns: repeat(auto-fill, 400px);
    padding: 0 10px;
    column-gap: 40px;
}
</style>