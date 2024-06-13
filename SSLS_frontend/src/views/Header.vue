<template>
    <div class="header">
        <div class="header-wrap">
            <nav class="nav">
                <a href="/">
                    <div class="nav-list">
                        <div class="logo-wrap">
                            <img class="img--cover"
                                 src="../assets/images/logo.png"
                                 alt="">
                        </div>
                        <div class="title">
                            Librarium
                        </div>
                    </div>
                </a>
                <div class="nav-list">
                    <div class="search">
                        <el-autocomplete v-model="keyWord"
                                         @keyup.enter="router.push({
                                             path: '/search',
                                             query: {
                                                 keyWord: keyWord
                                             }
                                         })"
                                         :fetch-suggestions="querySearch"
                                         placeholder="搜索"
                                         clearable
                                         value-key="name"
                                         :trigger-on-focus="false"
                                         onfocus="this.select()">
                            <template #suffix>
                                <i class="fa fa-search"></i>
                            </template>
                        </el-autocomplete>
                    </div>
                    <div v-if="isLogin"> <a class="link"
                           href="/bookBag">
                            书架({{ user.bookbagCount }})</a>
                    </div>
                    <div v-if="user.rule == 'admin'">
                        <a href="/admin"
                           class="link">管理</a>
                    </div>
                    <el-dropdown v-if="isLogin">
                        <a class="link"
                           href="/user">
                            <span class="el-dropdown-link">
                                {{ user.nickName }}
                            </span>
                        </a>
                        <template #dropdown>
                            <el-dropdown-menu>
                                <el-dropdown-item @click="router.push('/user')">
                                    个人中心
                                </el-dropdown-item>
                                <el-dropdown-item @click="router.push('/user/borrowed')">
                                    我的借阅
                                </el-dropdown-item>
                                <el-dropdown-item @click="router.push('/user/fine')">
                                    我的罚款
                                </el-dropdown-item>
                                <el-dropdown-item divided
                                                  @click="logout">
                                    退出登陆
                                </el-dropdown-item>
                            </el-dropdown-menu>
                        </template>
                    </el-dropdown>
                    <div v-else>
                        <a class="link"
                           @click.prevent="router.push('/auth')">
                            登录
                        </a>
                    </div>
                </div>
            </nav>
        </div>
    </div>
</template>

<script setup lang='ts'>
import { ref, inject, Ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../store/userStore'
import hRequest from '../utils/HRequest';

const user = useUserStore()
const router = useRouter()
onMounted(async () => {
    getUserInfo()
})


/* * * * * * * * * * autoComplete * * * * * * * * * */
const keyWord = ref<string>('')
const querySearch = async (queryString: string, cb: any) => {
    cb((await hRequest.post(`AutoCompleteBookNameServlet?keyWord=${queryString}`)).data.data)
}


/* * * * * * * * * * 获取用户信息 * * * * * * * * * */
type UserInfo = {
    nickName: string,
    gender: string,
    avatar: string,
    birthday: string
    balance: string
    rule: string
}
const isLogin = inject<Ref<Boolean>>('isLogin') as Ref<Boolean>

const getUserInfo = async () => {
    try {
        const data: Result = (await hRequest.post("user/InfoServlet")).data as Result
        if (data.code == 1) {
            user.setBookBagCount((await hRequest.post("user/BookBagCountServlet")).data.data)

            isLogin.value = true
            const { nickName, gender, avatar, birthday, balance, rule } = data.data as UserInfo
            user.setUserInfo(nickName, gender, avatar, birthday, rule)
            user.setBalance(balance)
        }
    } catch (e) {
    }
}
const logout = () => {
    localStorage.removeItem('jwtToken')
    isLogin.value = false
    user.setUserInfo('', '', '', '', '')
    // 返回首页
    router.push({
        path: '/',
    })
}
</script>

<style scoped>
.header {
    width: 100%;
    height: 64px;
}

.header-wrap {
    width: 100%;
    position: fixed;
    z-index: 1000;
    top: 0;
    background-color: #fff;
    box-shadow: 0 2px 4px #00000014;
}

.nav {
    height: 64px;
    padding: 0 5%;
    display: flex;
    justify-content: space-between;
}

.nav-list {
    display: flex;
    align-items: center;
}

.nav-list>div {
    margin: 0 10px;
}

.logo-wrap {
    width: 64px;
    height: 64px;
}

.title {
    font-size: 26px;
    font-weight: 200;
}

.search {
    width: 400px;
    margin-right: 40px !important;
}
</style>