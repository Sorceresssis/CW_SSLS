<template>
    <div>
        <div class="list">
            <div v-if="isLogin"
                 class="form-wrap">
                <h2>Sign in</h2>
                <form>
                    <div>
                        <el-input v-model="username_login"
                                  clearable
                                  onfocus="this.select()"
                                  placeholder="用户名/号码/邮箱" />
                    </div>
                    <div>
                        <el-input v-model="psd_login"
                                  type="password"
                                  clearable
                                  onfocus="this.select()"
                                  show-password
                                  placeholder="输入密码" />
                    </div>
                    <div>
                        <img ref="captchaRef"
                             src="http://localhost:8080/SSLS/auth/CaptchaServlet"
                             alt="验证码"
                             @click="refreshCaptcha($event)"
                             style="width: 100px ; height: 36px;">
                        <el-input v-model="captcha_login"
                                  type="text"
                                  clearable
                                  onfocus="this.select()"
                                  placeholder="验证码" />
                    </div>
                    <div>
                        <button @click.prevent="login()"
                                class="button"><span>登录</span></button>
                        <a class="link"
                           @click.prevent="isLogin = false">没有账号？去注册</a>
                    </div>
                </form>
            </div>
            <div v-else
                 class="form-wrap ">
                <h2>Sign Up</h2>
                <el-form ref="regFormRef"
                         :model="regForm"
                         :rules="regFormRules"
                         label-width="120px"
                         label-position="top"
                         class="demo-ruleForm"
                         status-icon>
                    <el-form-item label="用户名"
                                  prop="username"
                                  required>
                        <el-input v-model="regForm.username"
                                  clearable
                                  placeholder="输入用户名"
                                  onfocus="this.select()" />
                    </el-form-item>
                    <el-form-item label="密码"
                                  prop="psd"
                                  required>
                        <el-input v-model="regForm.psd"
                                  type="password"
                                  clearable
                                  onfocus="this.select()"
                                  show-password
                                  placeholder="输入密码" />
                    </el-form-item>
                    <el-form-item label="确认密码"
                                  prop="psdConfirm"
                                  required>
                        <el-input v-model="regForm.psdConfirm"
                                  type="password"
                                  clearable
                                  onfocus="this.select()"
                                  show-password
                                  placeholder="确认密码" />
                    </el-form-item>
                    <el-form-item label="号码"
                                  prop="phone">
                        <el-input v-model="regForm.phone"
                                  clearable
                                  type="number"
                                  onfocus="this.select()" />
                    </el-form-item>
                    <el-form-item label="邮箱"
                                  prop="email">
                        <el-input v-model="regForm.email"
                                  clearable
                                  onfocus="this.select()" />
                    </el-form-item>
                    <el-form-item label="验证码"
                                  prop="captcha"
                                  required>
                        <el-col :span="6">
                            <img ref="captchaRef"
                                 src="http://localhost:8080/SSLS/auth/CaptchaServlet"
                                 alt="验证码"
                                 @click="refreshCaptcha($event)"
                                 style="width: 100px ; height: 36px;">
                        </el-col>
                        <el-col :span="18">
                            <el-input v-model="regForm.captcha"
                                      type="text"
                                      clearable
                                      onfocus="this.select()"
                                      placeholder="验证码" />
                        </el-col>
                    </el-form-item>
                    <el-form-item class="space-between">
                        <el-col :span="6">
                            <button class="button"
                                    @click.prevent="submitForm(regFormRef)">注册</button>
                        </el-col>
                        <el-col :span="18"
                                style="text-align:right;">
                            <a class="link"
                               @click.prevent="isLogin = true">已经有账号？去登陆</a>
                        </el-col>
                    </el-form-item>
                </el-form>
            </div>
        </div>
    </div>
</template>

<script setup lang='ts'>
import { ref, reactive } from 'vue'
import JSEncrypt from 'jsencrypt/bin/jsencrypt'
import { throttle } from '../utils/throttle'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import hRequest from '../utils/HRequest'

// 获得验证码元素实例
const captchaRef = ref<HTMLImageElement>()
// 点击刷新验证码
const refreshCaptcha = (e: MouseEvent) => {
    (e.currentTarget as HTMLImageElement).src = "http://localhost:8080/SSLS/auth/CaptchaServlet"
}

// 登录和注册切换
const isLogin = ref(true)
// 登录表单
const username_login = ref<string>('')
const psd_login = ref<string>('')
const captcha_login = ref<string>('')
// 检查登录表单
const checkForm_login = () => {
    if (username_login.value == '') {
        ElMessage.error('用户名不能为空！')
        return false
    }
    if (psd_login.value == '') {
        ElMessage.error('密码不能为空！')
        return false
    }
    if (captcha_login.value == '') {
        ElMessage.error('验证码不能为空！')
        return false
    }
    return true
}
const login = throttle(async () => {
    if (!checkForm_login()) return
    try {
        // 得到公钥
        const pubKey: string = (await hRequest.post('auth/GetPublicKeyServlet')).data.data
        // 创建实例
        const encrypt = new JSEncrypt();
        // 将 pubKey 设置为 encrypt加密key
        encrypt.setPublicKey(pubKey);
        // 加密用户名和密码，放入参数中
        let param = new URLSearchParams()
        param.append('username', encrypt.encrypt(username_login.value))
        param.append('psd', encrypt.encrypt(psd_login.value))
        param.append('captcha', encrypt.encrypt(captcha_login.value))
        const loginRes = (await hRequest.post('auth/LoginServlet', param)).data
        if (loginRes.code == 1) {
            ElMessage.success('登录成功！ 正在跳转到主页')
            // 将 token 存入 localStorage
            localStorage.setItem('jwtToken', loginRes.data)
            // 跳转到主页
            setTimeout(() => {
                window.location.href = '/'
            }, 500)
        } else {
            // 登陆错误刷新验证码
            captchaRef.value?.click()
            ElMessage.error(loginRes.msg)
        }
    } catch (e) {
        ElMessage.error('服务器错误，请稍后再试！')
    }
}, 1000)


// 注册表单DOM实例
const regFormRef = ref<FormInstance>()
// 注册表单数据
const regForm = reactive({
    username: '',
    psd: '',
    psdConfirm: '',
    phone: '',
    email: '',
    captcha: '',
})
// 注册表单规则
const regFormRules = reactive<FormRules>({
    username: [
        { required: true, message: '用户名不能为空', trigger: 'blur' },
        // 只能是A~Z a~z 0~9 _ -
        { pattern: /^[A-Za-z0-9_-]+$/, message: '用户名只能是A~Z a~z 0~9 _ -', trigger: 'blur' },
        { max: 20, message: '用户名长度不能超过20', trigger: 'blur' },
    ],
    psd: [
        { required: true, message: '密码不能为空', trigger: 'blur', },
        // 只能是A~Z a~z 0~9 .
        { pattern: /^[A-Za-z0-9.]+$/, message: '密码只能是A~Z a~z 0~9 .', trigger: 'blur' },
        { min: 6, max: 20, message: '密码长度6~20', trigger: 'blur' },
    ],
    psdConfirm: [
        { required: true, message: '请确认密码', trigger: 'blur', },
        {
            validator: (rule, value, callback) => {
                value !== regForm.psd ? callback('两次输入密码不一致') : callback()
            },
            trigger: 'blur'
        },
    ],
    phone: [
        { pattern: /^[0-9]+$/, message: '请输入正确的号码', trigger: 'blur' },
        { min: 11, max: 11, message: '手机号长度11', trigger: 'blur' },
    ],
    email: [
        // 邮箱正则
        { pattern: /^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(.[a-zA-Z0-9_-])+/, message: '请输入正确的邮箱', trigger: 'blur' },
    ],
    captcha: [
        { required: true, message: '请输入验证码', trigger: 'blur', },
    ],
})
// 注册表单提交
const submitForm = throttle(async (formEl: FormInstance | undefined) => {
    if (!formEl) return
    await formEl.validate(async (valid, fields) => {
        if (valid) {
            // 得到公钥
            const pubKey: string = (await hRequest.post('auth/GetPublicKeyServlet')).data.data
            // 创建实例
            const encrypt = new JSEncrypt();
            // 将 pubKey 设置为 encrypt加密key
            encrypt.setPublicKey(pubKey);
            // 加密用户名和密码，放入参数中
            // let data = new URLSearchParams()
            let data = new FormData()
            data.append('username', encrypt.encrypt(regForm.username))
            data.append('psd', encrypt.encrypt(regForm.psd))
            data.append('phone', encrypt.encrypt(regForm.phone))
            data.append('email', encrypt.encrypt(regForm.email))
            data.append('captcha', encrypt.encrypt(regForm.captcha))
            const regRes = (await hRequest.post('auth/RegServlet', data, {
            })).data as Result
            if (regRes.code == 1) {
                ElMessage.success('注册成功！可以去登录了')
                // 把注册成功的用户名和密码放入登录表单
                username_login.value = regForm.username
                psd_login.value = regForm.psd
                isLogin.value = true
            } else {
                // 登陆错误刷新验证码
                captchaRef.value?.click()
                ElMessage.error(regRes.msg)
            }
        } else {
            ElMessage.error('请正确输入信息')
        }
    })
}, 500)
</script>

<style scoped>
.list {
    flex: 1;
}

.form-wrap {
    width: 400px;
    margin: 0 auto;
}

h2 {
    margin: 40px 0;
    font-size: 32px;
    text-align: center;
    font-weight: 400;
}

form {
    width: 100%;
}

form>div {
    display: flex;
    justify-content: space-between;
    margin-bottom: 20px;
}

a {
    line-height: 36px;
}

a::after {
    content: "\f14c";
    font-family: 'FontAwesome';
}
</style>