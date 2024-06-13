<template>
    <div>
        <div class="sideBar list">
            <div>
                <ul>
                    <li v-for="(menuItem ) in userMenu"
                        :key="menuItem.id"
                        :class="[menuItem.path == route.fullPath.split('?')[0] ? 'activeSideBarItem' : '']"
                        @click="router.push(menuItem.path)">
                        {{ menuItem.title }}
                    </li>
                </ul>
            </div>
        </div>
        <router-view class="flex-autoWidth--col"></router-view>
    </div>
</template>

<script setup lang='ts'>
import { shallowReactive } from 'vue';
import { useRouter, useRoute } from 'vue-router';

const router = useRouter()
const route = useRoute()

const userMenu = shallowReactive<any[]>([
    { id: 1, title: '统计分析', path: '/admin' },
    { id: 2, title: '管理图书', path: '/admin/manageBooks' },
])


</script>

<style scoped></style>