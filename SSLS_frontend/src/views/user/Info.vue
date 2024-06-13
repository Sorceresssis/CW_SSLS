<template>
    <div>
        <div class="list">
            <div class="userItem">
                <h3>用户信息</h3>
                <div class="userCol ">
                    <div>
                        昵称
                    </div>
                    <div>
                        <div>{{ userInfo.nickName }}</div>
                    </div>
                </div>
                <div class="userCol">
                    <div>性别</div>
                    <div>{{ userInfo.gender }}</div>
                </div>
                <div class="userCol">
                    <div>生日</div>
                    <div>{{ userInfo.birthday }}</div>
                </div>
            </div>
            <div class="userItem">
                <h3>余额</h3>
                <div class="userCol ">
                    <div>
                        余额
                    </div>
                    <div>
                        <div>{{ userInfo.balance }}</div>
                    </div>
                </div>
                <div class="userCol">
                    <div>充值</div>
                    <div>
                        <el-input v-model="recharge"
                                  onfocus="this.select()"
                                  type="text"
                                  clearable
                                  :formatter="amountFormat"
                                  @keyup.enter="($event.target as HTMLInputElement)?.blur()"
                                  @blur="amountComplete"
                                  placeholder="输入金额"></el-input>
                    </div>
                </div>
                <div class="userCol">
                    <div> </div>
                    <div>
                        <el-button @click="rechargeMoney"
                                   type="primary">充值</el-button>
                    </div>
                </div>
            </div>
            <div class="userItem">
                <h3>修改密码</h3>
                <div class="userCol ">
                    <div>
                        旧密码
                    </div>
                    <div>
                        <el-input v-model="oldPsw"
                                  type="password"
                                  clearable
                                  show-password
                                  placeholder="输入旧的密码"></el-input>
                    </div>
                </div>
                <div class="userCol">
                    <div>新密码</div>
                    <div>
                        <el-input v-model="newPsw"
                                  type="password"
                                  clearable
                                  show-password
                                  placeholder="输入新的密码"></el-input>
                    </div>
                </div>
                <div class="userCol">
                    <div> </div>
                    <div>
                        <el-button @click="changePsw"
                                   type="primary">修改密码</el-button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup lang='ts'>
import { ref, onMounted } from 'vue'
import { useUserStore } from '../../store/userStore'
import JSEncrypt from 'jsencrypt/bin/jsencrypt'
import { ElMessage, ElMessageBox } from 'element-plus';
import hRequest from '../../utils/HRequest';

const userInfo = useUserStore();
// 获取用户信息，赋值给pinia的userInfo，全局可用
onMounted(async () => {
    const data: Result = (await hRequest.post("user/InfoServlet")).data as Result
    if (data.code == 1) {
        userInfo.setBookBagCount((await hRequest.post("user/BookBagCountServlet")).data.data)
        const { nickName, gender, avatar, birthday, balance, rule } = data.data
        userInfo.setUserInfo(nickName, gender, avatar, birthday, rule)
        userInfo.setBalance(balance)
    }
})
// 充值金额
const recharge = ref<string>('0.00')
// 金额格式化
const amountFormat = (value: string) => {
    return value.replace(/[^0-9.]/g, '')  // 只能输入数字和小数点
        .replace(/^([^.]*)\.(.*)\.(.*)$/, '$1.$2$3')  // 保证只有出现一个小数点
        .replace(/^0+(?=\d)/, '') // 只有小数点前面可以有0, eg:0.89, 0.99
        .replace(/^(\d{4})\d*(\.\d*)?$/, '$1$2')  // 没有小数点时只能输入4位整数
        .replace(/^(\d{4})\d*(\.\d{2}).*$/, '$1$2')   // 整数部分不能超过4位，小数不能超过2位
        .replace(/(\.\d{2})\d+/, '$1')
}
// 金额补齐
const amountComplete = () => {
    // 字符串为空时，赋值为0
    if (!recharge.value) {
        recharge.value = '0.00'
    } else {
        // 小数点后不足两位补0
        recharge.value = parseFloat(recharge.value).toFixed(2).toString()
    }
}
// 充值行为
const rechargeMoney = async () => {
    if (recharge.value == '0.00') {
        ElMessage.error('充值金额不能小于0');
        return;
    }
    // 确认充值
    ElMessageBox.confirm(
        `确定要充值${recharge.value}元吗？`,
        'Warning',
        {
            confirmButtonText: '确认',
            cancelButtonText: '取消',
            type: 'warning',
        }
    ).then(async () => {
        // 发送充值请求
        const resp = (await hRequest.post(`user/RechargeMoneyServlet?amount=${recharge.value}`)).data
        if (resp.code == 1) {
            ElMessage.success('充值成功')
            userInfo.balance = resp.data
        } else {
            ElMessage.error(resp.msg)
        }
        recharge.value = '0.00'
    }).catch(() => { return })
}

/* ************** 修改密码 ************* */
const oldPsw = ref<string>('');
const newPsw = ref<string>('');
const changePsw = () => {
    // 检查
    if (!oldPsw.value || !newPsw.value) {
        ElMessage.error('密码不能为空');
        return;
    }
    if (oldPsw.value === newPsw.value) {
        ElMessage.error('新旧密码不能相同');
        return;
    }
    // 确认修改
    ElMessageBox.confirm(
        '确定修改密码吗？',
        'Warning',
        {
            confirmButtonText: '确认',
            cancelButtonText: '取消',
            type: 'warning',
        }
    ).then(async () => {
        // 得到公钥
        const pubKey: string = (await hRequest.post('auth/GetPublicKeyServlet')).data.data
        // 创建实例
        const encrypt = new JSEncrypt();
        // 将 pubKey 设置为 encrypt加密key
        encrypt.setPublicKey(pubKey);
        // 加密用户名和密码，放入参数中
        let param = new URLSearchParams()
        param.append('oldPsd', encrypt.encrypt(oldPsw.value))
        param.append('newPsd', encrypt.encrypt(newPsw.value))
        const resp = (await hRequest.post('user/EditPsdServlet', param)).data
        if (resp.code == 1) {
            ElMessage.success('修改成功')
        } else {
            ElMessage.error(resp.msg)
        }
        oldPsw.value = ''
        newPsw.value = ''
    }).catch(() => { return })
}



</script>

<style scoped>
.list {
    flex: 1;
    padding: 0 20px;
}

.userItem {
    padding-bottom: 10px;
    border-bottom: 1px dashed #ccc;
    margin-bottom: 20px;
}

.userItem h3 {
    font-size: 20px;
    font-weight: 700;
    margin-bottom: 10px;
}

.userCol {
    display: flex;
    margin: 5px 0;
}

.userCol>div:nth-child(1) {
    width: 100px;
}

.userCol>div:nth-child(2) {
    flex: 1;
}

:deep(.el-input) {
    width: 400px;
}
</style>