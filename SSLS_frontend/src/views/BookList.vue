<template>
    <div class="flex-autoWidth--col">
        <div class="pagination">
            <el-pagination layout="prev, pager, next, jumper, total"
                           :page-size="props.pageSize"
                           :total="total"
                           :current-page="Number.parseInt(route.query.page as string || '1')"
                           @current-change="(value: number) => emit('turnPage', value)" />
        </div>
        <div v-if="books.length == 0"
             class="empty">
            没有找到图书哦
        </div>
        <div v-else
             class="books thumbnail list">
            <div v-for="book in props.books"
                 @click="router.push({
                     path: clickPath,
                     query: {
                         id: book.id
                     }
                 })"
                 class="book">
                <!-- 如果book的状态是外借或者未在库，生成遮罩层 提示用户不可借 -->
                <div v-if="book.status == '外借' || book.status == '未在库'"
                     class="book-overlay">
                    <span class="rotate-text">{{ book.status }}</span>
                </div>
                <div>
                    <div style="height: 316px;">
                        <img loading="lazy"
                             class="img--cover"
                             :src="`${imageBaseUrl}${book.imageUrl}`"
                             @error="($event.target as HTMLImageElement).src = '../assets/images/nodata.png'">
                    </div>
                    <div>{{ book.name }}</div>
                    <div>{{ book.authors }}</div>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup lang='ts'>
import { inject } from 'vue'
import { useRoute, useRouter } from 'vue-router';

const imageBaseUrl = inject('imageBaseUrl')

const route = useRoute()
const router = useRouter()

// 接收books,总数据量,每页的数据量，并设置pageSize默认值为20
const props = withDefaults(defineProps<{
    total: number,
    books: bookProfile[],
    pageSize: number,
    clickPath: string,
}>(), {
    clickPath: '/book',
    pageSize: 20
})
// 向父组件发送换页事件
const emit = defineEmits<{
    (e: 'turnPage', pageNo: number): void
}>()
</script>

<style scoped>
.books {
    grid-template-columns: repeat(auto-fill, 220px);
    padding: 0 10px;
}

.book {
    position: relative;
}

.book-overlay {
    position: absolute;
    width: 100%;
    height: 100%;
    background-color: #ffffffaf;
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 100;
}

.rotate-text {
    /* 灰色 */
    color: #aeaeae;
    transform: rotate(-30deg);
    font-size: 30px;
    font-weight: 700;
}
</style>