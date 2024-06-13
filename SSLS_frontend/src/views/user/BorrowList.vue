<template>
    <div>
        <div v-if="borrowInfos.length == 0"
             class="empty">
            没有正在借阅的书籍哦
        </div>
        <div v-else
             class="list thumbnail">
            <div v-for="info in borrowInfos">
                <div>
                    <img :src="`${imageBaseUrl}${info.imageUrl}`"
                         @error="($event.target as HTMLImageElement).src = '../assets/images/nodata.png'">
                </div>
                <div>
                    <div>{{ info.bookName }}</div>
                    <div>{{ info.authors }}</div>
                    <div>借阅：{{ info.borrowDate }}</div>
                    <div>归还：<span :class="[info.renew === '逾期' ? 'warning' : 'allow']">{{ info.dueDate }}</span> </div>
                    <div></div>
                    <div>
                        <el-button @click="returnBook(info.id)"
                                   type="primary">归还</el-button>
                        <el-button @click="renewBook(info.id)"
                                   :type="info.renew === '逾期' ? 'danger' : 'primary'"
                                   :disabled="info.renew !== '续借'">{{ info.renew }}</el-button>
                    </div>
                </div>
            </div>
        </div>
        <el-dialog v-model="isVisibleReturnResult"
                   align-center
                   title="还书结果"
                   width="400px"
                   class="dialog">
            <div class="dialog__body list">
                <div class="dialogCol">
                    <div class="col-title"
                         :class="[returnResult.code == 1 ? 'allow' : 'warning']">
                        {{ returnResult.code === 1 ? '还书成功' : '还书失败' }}
                    </div>
                    <div class="col-content">
                        罚款： {{ returnResult.data }}元
                    </div>
                </div>
            </div>
            <template #footer>
                <span class="dialog-footer">
                    <el-button type="primary"
                               @click="isVisibleReturnResult = false">
                        确认
                    </el-button>
                </span>
            </template>
        </el-dialog>
    </div>
</template>

<script setup lang='ts'>
import { ref, onMounted, inject } from 'vue'
import hRequest from '../../utils/HRequest';
import { throttle } from '../../utils/throttle';
import { ElMessage } from 'element-plus';

const borrowInfos = ref<BorrowInfo[]>([])
// 打开页面时获取借阅信息
onMounted(async () => {
    borrowInfos.value = (await hRequest.post('user/BorrowListServlet')).data.data
})


const imageBaseUrl = inject('imageBaseUrl')
const isVisibleReturnResult = ref(false)
const returnResult = ref()

// 还书 throttle节流,防止用户在响应期间多次点击
const returnBook = throttle(async (borrowId: number) => {
    returnResult.value = (await hRequest.post(`user/ReturnBookServlet?borrowId=${borrowId}`)).data
    borrowInfos.value = (await hRequest.post('user/BorrowListServlet')).data.data
    isVisibleReturnResult.value = true
}, 1000)

const renewBook = throttle(async (borrowId: number) => {
    if ((await hRequest.post(`user/RenewBookServlet?borrowId=${borrowId}`)).data.code == 1) {
        ElMessage.success('续借成功')
        borrowInfos.value = (await hRequest.post('user/BorrowListServlet')).data.data
    }
    else {
        ElMessage.error('续借失败')
    }
}, 1000)
</script>

<style scoped>
.list {
    grid-template-columns: repeat(auto-fill, 230px);
    padding: 0 10px;
}

.el-button {
    min-width: 100px;
}
</style>