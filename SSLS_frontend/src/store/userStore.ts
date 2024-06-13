import { defineStore } from 'pinia'
import { StoreId } from './storeId'


export const useUserStore = defineStore(StoreId.USER, {
    state: () => {
        return {
            nickName: '',
            gender: '',
            avatar: '',
            birthday: '',
            bookbagCount: 0,
            balance: '',
            rule: ''
        }
    },
    actions: {
        setUserInfo(nickName: string, gender: string, avatar: string, birthday: string, rule: string) {
            this.nickName = nickName
            this.gender = gender
            this.avatar = avatar
            this.birthday = birthday
            this.rule = rule
        },
        setBookBagCount(count: number) {
            this.bookbagCount = count
        },
        setBalance(balance: string) {
            let temp: string = balance.toString()
            this.balance = (temp.includes('.')) ? temp : `${temp}.00`
        }
    }
})