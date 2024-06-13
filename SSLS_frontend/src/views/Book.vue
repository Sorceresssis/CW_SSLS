<template>
    <div>
        <div class=" list">
            <div class="book">
                <div class="img-wrap">
                    <img loading="lazy"
                         :src="`${imageBaseUrl}${book.imageUrl}`"
                         @error="($event.target as HTMLImageElement).src = '../assets/images/nodata.png'"
                         style="width: 100%; height: 100%; object-fit: cover;">
                </div>
                <div class="book-info">
                    <div>{{ book.name }}</div>
                    <div>{{ book.authors }}</div>
                    <div>{{ book.description }}</div>
                    <div>出版日期：{{ book.publishDate }}</div>
                    <div>出版社：{{ book.press }}</div>
                    <div>分类：{{ book.categoryName }}</div>
                </div>
            </div>
            <div class="operator">
                <div v-if="book.status != '在库'"
                     class="warning">
                    此书不在库，无法加入书架
                </div>
                <el-button @click="addToBookBag(book.id)"
                           type="primary"
                           :disabled="!canAddToBag">{{ buttonMsg }}</el-button>
            </div>
        </div>
    </div>
</template>

<script setup lang='ts'>
import { ref, onMounted, Ref, inject } from 'vue'
import hRequest from '../utils/HRequest';
import { useRoute } from 'vue-router';
import { useUserStore } from '../store/userStore';
import { ElMessage } from 'element-plus';


type bookInfo = {
    id: number,
    name: string,
    authors: string,
    categoryName: string,
    description: string,
    publishDate?: string,
    imageUrl: string,
    press: string,
    status: string,
}

const route = useRoute()

const isLogin = inject<Ref<Boolean>>('isLogin') as Ref<Boolean>
const imageBaseUrl = inject('imageBaseUrl')

const book = ref<bookInfo>({
    id: 0, name: '',
    authors: '',
    categoryName: '',
    description: '',
    publishDate: '',
    imageUrl: '',
    press: '',
    status: '',
})
// 是否可以加入书架
const canAddToBag = ref<boolean>(true)
const buttonMsg = ref<string>('加入书架')

onMounted(async () => {
    book.value = (await hRequest.post(`BookServlet?id=${route.query.id}`)).data.data
    // 改变页面标题
    document.title = book.value.name + 'Librarium'
    canAddToBag.value = book.value.status == '在库'
    if (isLogin.value) {
        // 判读是否已经在书架上
        if ((await hRequest.post(`user/IsInBookBagServlet?bookId=${route.query.id}`)).data.data) {
            buttonMsg.value = '已加入书架'
            canAddToBag.value = false
        }
    } else {
        buttonMsg.value = '请先登录,才能加入书架'
    }
})

const user = useUserStore()
const addToBookBag = async (bookId: number) => {
    if ((await hRequest.post(`user/AddToBookBagServlet?bookId=${bookId}`)).data.code == 1) {
        // 添加成功，发送提示
        ElMessage.success('添加成功')
        // 按钮不可用，提示用户已经在书架中
        canAddToBag.value = false
        buttonMsg.value = '已加入书架'
        // 修改书架的书籍数量
        user.setBookBagCount((await hRequest.post("user/BookBagCountServlet")).data.data)
    } else {
        // 添加失败， 说明用户在查看书籍的期间，书籍已经提前被借走了，慢人一步
        ElMessage.error('添加失败')
    }
}
</script>

<style scoped>
.book {
    display: flex;
    /* height: 300px; */
}

.img-wrap {
    width: 200px;
    height: 100%;
    margin-right: 20px;
}

.book-info {
    flex: 1
}

.operator {
    display: flex;
    flex-direction: column;
    padding: 30px 0;
}
</style>