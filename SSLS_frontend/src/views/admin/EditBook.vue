<template>
    <div>
        <button class="button"
                @click="router.go(-1)"
                style="margin-bottom: 10px; width: 100px;"><span>返回</span></button>
        <div class="list">
            <el-form ref="editFormRef"
                     :model="editForm"
                     :rules="editFormRules"
                     label-width="120px"
                     label-position="right"
                     status-icon>
                <el-form-item label="图片">
                    <div style="display: flex; flex-direction: column;">
                        <div style="height:300px; margin-bottom: 10px;">
                            <!-- @error捕获错误，加载nodata.png提示用户 -->
                            <img :src="editForm.imageUrl"
                                 class="img--contain"
                                 @error="($event.target as HTMLImageElement).src = '../assets/images/nodata.png'">
                        </div>
                        <div>
                            <input ref="coverImage"
                                   type="file"
                                   @change="selectImage"
                                   accept="image/jpg,image/jpeg,image/png"
                                   clearable />
                            <button class="button"
                                    @click.prevent="resetImage"><span>重置</span></button>
                        </div>
                    </div>
                </el-form-item>
                <el-form-item label="书名"
                              prop="name"
                              required>
                    <el-input v-model="editForm.name"
                              clearable
                              placeholder="输入书名" />
                </el-form-item>
                <el-form-item label="作者"
                              prop="authors"
                              required>
                    <el-input v-model="editForm.authors"
                              type="text"
                              clearable
                              placeholder="作者" />
                </el-form-item>
                <el-form-item label="介绍"
                              prop="description">
                    <el-input v-model="editForm.description"
                              type="textarea"
                              :autosize="{ minRows: 4 }"
                              resize="none"
                              clearable
                              placeholder="介绍" />
                </el-form-item>
                <el-form-item label="出版日期"
                              prop="publishDate">
                    <el-date-picker v-model="editForm.publishDate"
                                    type="date"
                                    placeholder="出版日期"
                                    format="YYYY-MM-DD"
                                    value-format="YYYY-MM-DD" />
                </el-form-item>
                <el-form-item label="出版社"
                              prop="press">
                    <el-input v-model="editForm.press"
                              clearable
                              type="text" />
                </el-form-item>
                <el-form-item label="分类"
                              prop="categoryId"
                              required>
                    <el-select v-model="editForm.categoryId"
                               class="m-2"
                               placeholder="选择分类"
                               size="large">
                        <el-option v-for="item in categoryList"
                                   :key="item.id"
                                   :label="item.name"
                                   :value="item.id" />
                    </el-select>
                </el-form-item>
                <el-form-item label="状态"
                              prop="categoryId">
                    <el-select v-model="editForm.status"
                               class="m-2"
                               placeholder="书的状态"
                               size="large">
                        <el-option v-for="item in bookStatus"
                                   :key="item.id"
                                   :label="item.value"
                                   :value="item.value" />
                    </el-select>
                </el-form-item>
                <el-form-item class="space-between">
                    <button class="button"
                            @click.prevent="submitForm(editFormRef)">
                        {{ formSubmitButton }}
                    </button>
                </el-form-item>
            </el-form>
        </div>
    </div>
</template>

<script setup lang='ts'>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import hRequest from '../../utils/HRequest'
import { throttle } from '../../utils/throttle'
import { useRouter, useRoute } from 'vue-router'

// 路由工具
const router = useRouter()
const route = useRoute()

// 分类信息, 用作表单的select选择器
const categoryList = ref<category[]>([])
// 书籍状态, 用作表单的select选择器
const bookStatus = [{ id: 1, value: '在库' }, { id: 2, value: '未在库' }, { id: 3, value: '外借' }]
// 表单提交按钮的文字
const formSubmitButton = ref<string>('')
// 表单实例
const editFormRef = ref<FormInstance>()
// input:file的DOM对象
const coverImage = ref<HTMLInputElement | null>(null)
// 最本的封面图片
let oldCoverImage = ''
// 编辑书籍的表单数据
const editForm = reactive({
    name: '',
    authors: '',
    description: '',
    imageUrl: '',
    press: '',
    publishDate: '',
    status: '在库',
    categoryId: 1
})
// 表单验证规则
const editFormRules = reactive<FormRules>({
    name: [
        { required: true, message: '请输入书名', trigger: 'blur' },
        { max: 100, message: '书名不能超过100个字符', trigger: 'blur' }
    ],
    authors: [
        { required: true, message: '请输入作者', trigger: 'blur' },
        { max: 100, message: '作者不能超过100个字符', trigger: 'blur' }
    ]
})

// 把选择的图片转换成blob展示在页面上，不急着上传。
const selectImage = () => {
    // 把本地图片转换成blob，用于展示在页面上
    editForm.imageUrl = window.URL.createObjectURL((coverImage.value?.files as FileList)[0])
}
// 重置图片
const resetImage = () => {
    // 把正在展示的图片重置成原来的图片
    editForm.imageUrl = oldCoverImage;
    // 清空 input:file 表单
    (coverImage.value as unknown as HTMLInputElement).value = ''
}
// 提交表单
const submitForm = throttle(async (formEl: FormInstance | undefined) => {
    // 如果没有传入表单实例，直接返回
    if (!formEl) return
    // 验证表单
    await formEl.validate(async (valid, fields) => {
        if (valid) {
            // 准备数据
            const formData = new FormData()
            formData.append('bookId', route.query.id as string || '0')
            formData.append('coverImage', (coverImage.value?.files as FileList)[0])
            formData.append('name', editForm.name)
            formData.append('authors', editForm.authors)
            formData.append('description', editForm.description)
            formData.append('press', editForm.press)
            formData.append('publishDate', editForm.publishDate)
            formData.append('status', editForm.status)
            formData.append('categoryId', editForm.categoryId.toString())
            // 提交表单
            const resp = (await hRequest.post('admin/EditBookServlet', formData, {
                headers: {
                    'Content-Type': 'multipart/form-data'
                }
            })).data
            if (resp.code === 1) {
                // 成功，提示并返回上一页
                ElMessage.success(resp.msg)
                setTimeout(() => router.go(-1), 1000)
            } else {
                ElMessage.error(resp.msg)
            }
        }
        else {
            ElMessage.error('请检查输入')
        }
    })
}, 500)


onMounted(async () => {
    // 如果是修改书籍，获取书籍信息
    if (route.query.id) {
        const data = (await hRequest.post(`BookServlet?id=${route.query.id}`)).data.data
        editForm.name = data.name
        editForm.authors = data.authors
        editForm.description = data.description
        editForm.press = data.press
        editForm.publishDate = data.publishDate
        oldCoverImage = editForm.imageUrl = `http://localhost:8080/SSLS/bookImg/${data.imageUrl}`
        editForm.status = data.status
        editForm.categoryId = data.categoryId
    }
    formSubmitButton.value = route.query.id ? '修改' : '添加'
    categoryList.value.push(...(await hRequest.post('CategoryListServlet')).data.data)
})

</script>

<style scoped>
.list {
    flex: 1;
    padding: 0 20px;
}

.form-wrap {
    width: 400px;
    margin: 0 auto;
}
</style>