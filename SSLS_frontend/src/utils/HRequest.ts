import axios from 'axios';

// 创建 Axios 实例
const hRequest = axios.create({
    baseURL: 'http://localhost:8080/SSLS/',
})

// 带上cookies
hRequest.defaults.withCredentials = true

// 添加请求拦截器 在请求头添加jwt令牌
hRequest.interceptors.request.use(function (request) {

    // 获取存储在本地的 JWT Token
    const token = localStorage.getItem('jwtToken');

    // 在请求头中添加 Authorization 字段，值为 Token
    if (token) {
        request.headers['Authorization'] = token;
    }

    return request;
},
    function (error) {
        return Promise.reject(error);
    }
)

// 添加响应拦截器 查询响应头中是有重定向要求
hRequest.interceptors.response.use(function (response) {
    // 调用headers的字段，要用小写，不是大写！！！，
    if (response.headers.redirect != null) {
        /** 
         * 在测试重定向功能时发现，会进入重定向的循环
         * 为了更好的用户体验，Header组件在一开始就自动会向后端发送带身份证明的请求来实现自动登陆。
         * 可是如果当用户凭证失效前端重定向后，Header组件依然会自动发送请求，这样就会导致页面进入重定向的循环，而无法显示登陆和注册组件
         */
        location.href = response.headers.redirect
    }
    return response;
})

export default hRequest