<template>
    <div>
        <div class="flex-autoWidth--col list">
            <h2>书架</h2>
            <el-checkbox v-model="checkAll"
                         :indeterminate="isIndeterminate"
                         @change="handleCheckAllChange">全选可借的</el-checkbox>
            <el-checkbox-group v-model="checkedBooks"
                               @change="handleCheckedBooksChange"
                               class="flex-autoWidth--col">
                <el-checkbox v-for="(book, index) in bookBag"
                             :disabled="book.status !== '在库'"
                             class="book"
                             :key="book.id"
                             :label="book.id">
                    <template #default>
                        <div class="flex-autoWidth--row book-profile">
                            <div style="width: 100px; margin-right: 20px;">
                                <img loading="lazy"
                                     :src="`${imageBaseUrl}${book.imageUrl}`"
                                     @error="($event.target as HTMLImageElement).src = '../assets/images/nodata.png'"
                                     style="width: 100%; height: 100%; object-fit: cover;">
                            </div>
                            <div class="book-info">
                                <div style="font-size: 25px; font-weight: 700;">
                                    {{ book.name }}
                                </div>
                                <div>
                                    {{ book.authors }}
                                </div>
                                <div :class="[book.status !== '在库' ? 'warning' : 'allow']">
                                    {{ book.status }}
                                </div>
                            </div>
                            <div>
                                <button @click.prevent="removeBookFromBag(index)">移除</button>
                            </div>
                        </div>
                    </template>
                </el-checkbox>
            </el-checkbox-group>
            <div class="bag-footer">
                <el-button @click="borrowBook"
                           type="primary">确认借阅</el-button>
            </div>
        </div>
        <el-dialog v-model="isVisibleConfirmBorrow"
                   align-center
                   title="借阅结果"
                   width="400px"
                   class="dialog">
            <div class="dialog__body list">
                <div v-for="(result) in borrowResult"
                     class="dialogCol divider">
                    <div class="col-title"
                         :class="[result.code == 1 ? 'allow' : 'warning']">
                        {{ result.code === 1 ? '借阅成功' : '借阅失败' }}
                    </div>
                    <div class="col-content">
                        <div>
                            {{ result.bookName }} -- {{ result.authors }}
                        </div>
                        <div v-if="result.code == 1">
                            借阅：{{ result.borrowDate }}
                        </div>
                        <div v-if="result.code == 1">
                            归还：{{ result.dueDate }}
                        </div>
                    </div>
                </div>
            </div>
            <template #footer>
                <span class="dialog-footer">
                    <el-button type="primary"
                               @click="isVisibleConfirmBorrow = false">
                        确认
                    </el-button>
                </span>
            </template>
        </el-dialog>
    </div>
</template>

<script setup lang='ts'>
import { ref, onMounted, inject } from 'vue'
import hRequest from '../utils/HRequest';
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '../store/userStore'

const imageBaseUrl = inject('imageBaseUrl')

const user = useUserStore()

onMounted(async () => {
    queryBookBag()
})

// 所有添加到书架的书籍
const bookBag = ref<bookProfile[]>([])
// 可以被选中的书籍，即在库的书籍, 因为有些书在加入书架后没有即时借走，被其他人抢先一步
let canCheckedBooks: number[] = []
// 用户勾选的书籍
const checkedBooks = ref<number[]>([])
// 是否勾选了全部可选的书籍（canCheckedBooks） 不是bookBag
const checkAll = ref(false)
// 是否半选(选了一部分)
const isIndeterminate = ref(false)

// 获取bookBag列表
const queryBookBag = async () => {
    bookBag.value = (await hRequest.post('/user/BookBagServlet')).data.data
    // 初始化可借的书籍
    canCheckedBooks = []
    bookBag.value.forEach((book) => {
        if (book.status === '在库') {
            canCheckedBooks.push(book.id)
        }
    })
}
// 处理全选操作
const handleCheckAllChange = (val: boolean) => {
    // val为真全选，加为假全不选, 
    checkedBooks.value = val ? canCheckedBooks : []
    isIndeterminate.value = false
}
// 每次勾选书籍时，判断是否全选，是否半选
const handleCheckedBooksChange = (value: string[]) => {
    // vlaue为选中的书籍id,和checkedBooks相同
    checkAll.value = isSame(canCheckedBooks, checkedBooks.value)
    const checkedCount = value.length
    // 当所选中的个数在全选和全不选之间时，isIndeterminate为真
    isIndeterminate.value = checkedCount > 0 && checkedCount < canCheckedBooks.length
}

// 判断可选canCheckedBook和用户选择的完全一样
const isSame = (t: number[], s: number[]) => {
    if (t.length != s.length) { return false }
    // 建立s的集合
    let sSet = new Set(s)
    t.forEach((item) => {
        if (!sSet.has(item)) {
            return false
        }
    })
    return true
}

// 移除书籍
const removeBookFromBag = async (bookIndex: number) => {
    if ((await hRequest.post(`user/RemoveFromBookBagServlet?bookId=${bookBag.value[bookIndex].id}`)).data.code == 1) {
        ElMessage.success('移除成功')
        // 如果checkedBooks包含被移除的书籍，移除
        checkedBooks.value.find((item, index) => {
            if (item == bookBag.value[bookIndex].id) {
                checkedBooks.value.splice(index, 1)
            }
        })

        // 前端同步刷新书包
        await queryBookBag()
        // 判断是否全选
        checkAll.value = bookBag.value.length > 0 && isSame(canCheckedBooks, checkedBooks.value)
    } else {
        ElMessage.error('移除失败')
    }
    user.setBookBagCount((await hRequest.post("user/BookBagCountServlet")).data.data)
}

/* * * * * * * * * * 借阅 * * * * * * * * * */
const borrowResult = ref<BorrowResult[]>([])
const isVisibleConfirmBorrow = ref<boolean>(false)
const borrowBook = async () => {
    if (checkedBooks.value.length == 0) {
        ElMessage.warning('请选择要借阅的书籍')
        return
    }
    try {
        await ElMessageBox.confirm('确认借阅？', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
        })
        // 请求参数
        let param = new URLSearchParams()
        checkedBooks.value.forEach((item) => {
            param.append('bookIds', `${item}`)
        })
        const res = (await hRequest.post('user/ToBorrowBookServlet', param)).data as Result

        if (res.code == 1) {
            // 写入借书结果，是否成功，何时借阅，何时归还
            borrowResult.value = res.data
            // 显示对话框，显示借阅结果
            isVisibleConfirmBorrow.value = true
            // 清空勾选
            checkAll.value = false
        } else {
            ElMessage.error(res.msg)
        }
        // 刷新书包
        queryBookBag()
        // 刷新书包数量
        user.setBookBagCount((await hRequest.post("user/BookBagCountServlet")).data.data)
    } catch (e) {
        return
    }
}
</script>

<style scoped>
h2 {
    font-size: 30px;
    font-weight: 700;
    margin-bottom: 20px;
}

.book {
    height: auto;
    margin: 30px 0;
}

.book-profile {
    width: 100%;
    display: flex;
    padding: 0 40px;
}

.book-info {
    flex: 1;
}

.book-info>div {
    margin: 10px 0;
}

.bag-footer {
    display: flex;
    border-top: 2px solid #eee;
    padding: 20px;
    justify-content: center;
}

.dialog__body-wrap {
    padding: 15px 0;
}

.dialog__body {
    height: 200px;
    padding: 0 10px;
    overflow: auto;
}
</style>