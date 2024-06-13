import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'


const routes: Array<RouteRecordRaw> = [
    {
        path: "/",
        component: () => import('../views/Index.vue'),
    },
    {
        path: "/user",
        component: () => import('../views/User.vue'),
        children: [
            {
                path: "",
                component: () => import('../views/user/Info.vue')
            },
            {
                path: "borrowed",
                component: () => import('../views/user/BorrowList.vue')
            },
            {
                path: "fine",
                component: () => import('../views/user/Fine.vue')
            },
            {
                path: "borrowHistory",
                component: () => import('../views/user/BorrowHistory.vue')
            }
        ]
    },
    {
        path: "/admin",
        component: () => import('../views/Admin.vue'),
        children: [
            {
                path: '',
                component: () => import('../views/admin/Charts.vue')
            },
            {
                path: "manageBooks",
                component: () => import('../views/admin/ManageBooks.vue')
            },
            {
                path: "editBook",
                component: () => import('../views/admin/EditBook.vue')
            }
        ]
    },
    // 书籍详情
    {
        path: "/book",
        component: () => import('../views/Book.vue')
    },
    // 登录注册
    {
        path: "/auth",
        component: () => import('../views/Auth.vue')
    },
    // 搜索
    {
        path: "/search",
        component: () => import('../views/Search.vue')
    },
    // 书架
    {
        path: "/bookBag",
        component: () => import('../views/BookBag.vue')
    },
    // 当用户到了错误的路由，就会跳转到这个路由
    {
        path: "/:pathMatch(.*)",
        component: () => import('../views/NotFound.vue')
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

export default router