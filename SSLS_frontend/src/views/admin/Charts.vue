<template>
    <div>
        <div class="list thumbnail">
            <!-- 图书统计 -->
            <div ref="booksStatus"
                 class="chart"></div>
            <!-- 图书分类 -->
            <div ref="booksCategory"
                 class="chart"></div>
            <!-- 借阅统计 -->
            <div ref="borrowingHot"
                 class="chart"></div>
            <!-- 每个月新增的罚款 -->
            <div ref="FineTotalOfMonth"
                 class="chart"></div>
        </div>
    </div>
</template>
<script setup lang="ts">
import * as echarts from 'echarts';
import { onMounted, ref } from 'vue';
import hRequest from '../../utils/HRequest';

const booksStatus = ref<HTMLElement>();
let booksStatusOption = {
    title: {
        text: '书籍状态统计',
        x: 'center'
    },
    tooltip: {
        trigger: 'item',
        formatter: '{b}:{c}({d}%)'
    },
    legend: {
        type: 'scroll',
        orient: 'vertical',
        left: 0,
        top: 30,
        height: 150,
        data: ['在库', '未在库', '外借']
    },
    color: ['#61adf2', '#56dae8', '#efa49e', '#7ea1ed', '#5ae05a', '#f2d2a2', '#5881e8', '#63d6c0', '#edc29f', '#5b97d3', '#3eceb3', '#6a96ed', '#426ed1', '#65d18b'],
    series: [
        {
            name: '状态统计',
            type: 'pie',
            radius: '40%',
            center: ['50%', '50%'],
            data: null,
            emphasis: {
                itemStyle: {
                    shadowBlur: 10,
                    shadowOffsetX: 0,
                    shadowColor: 'rgba(0,0,0,0.5)'
                }
            },
            label: {
                show: true,
                position: 'outside',
                formatter: '{b}:{c}({d}%)'
            }
        }
    ]
}
const booksCategory = ref<HTMLElement>();
let booksCategoryOption = {
    title: {
        text: '分类统计',
        x: 'center'
    },
    tooltip: {
        trigger: 'item'
    },
    legend: {
        data: ['图书量',],
        orient: 'vertical',
        right: 60,
        top: 20
    },
    xAxis: {
        data: null
    },
    yAxis: {},
    color: ['#c38bef'],
    series: [
        {
            name: '图书量',
            type: 'bar',
            data: null,
            barWidth: '20',
            label: {
                show: true,
                position: 'top'
            }
        }
    ]
}
const borrowingHot = ref<HTMLElement>();
const borrowingHotOption = {
    title: {
        text: '借阅热度',
        x: 'center'
    },
    tooltip: {
        trigger: 'item'
    },
    legend: {
        data: '被借阅次数',
        orient: 'vertical',
        right: 60,
        top: 20
    },
    xAxis: {
        data: null,
        axisLabel: {
            rotate: -45 // 设置旋转角度，单位为度
        }
    },
    yAxis: {},
    color: ['#c38bef'],
    series: [
        {
            name: '被借阅次数',
            type: 'bar',
            data: null,
            barWidth: '20',
            label: {
                show: true,
                position: 'top'
            }
        }
    ]
}
// 最近12个月，每个月新增的罚款
const FineTotalOfMonth = ref<HTMLElement>();
const FineTotalOfMonthOption = {
    title: {
        text: '月新增罚款总额',
        x: 'center'
    },
    tooltip: {
        trigger: 'item'
    },
    xAxis: {
        data: null,
    },
    yAxis: {},
    color: ['#c38bef'],
    series: [
        {
            name: '罚款金额',
            type: 'bar',
            data: [9, 18, 12, 9, 6],
            barWidth: '20',
            label: {
                show: true,
                position: 'top'
            }
        }
    ]
}

type NameValue = {
    name: string,
    value: any
}

onMounted(async () => {
    const data = (await hRequest.post('admin/ChartsServlet')).data.data
    // 图书状态统计
    booksStatusOption.series[0].data = data[0]

    // 图书分类统计
    booksCategoryOption.xAxis.data = data[1].map((item: NameValue) => item.name)
    booksCategoryOption.series[0].data = data[1].map((item: NameValue) => item.value)

    // 借阅热度
    borrowingHotOption.xAxis.data = data[2].map((item: NameValue) => item.name)
    borrowingHotOption.series[0].data = data[2].map((item: NameValue) => item.value)

    // 最近12个月，每个月新增的罚款
    FineTotalOfMonthOption.xAxis.data = data[3].map((item: NameValue) => item.name)
    FineTotalOfMonthOption.series[0].data = data[3].map((item: NameValue) => item.value)

    // 初始化全部图表
    echarts.init(booksStatus.value!).setOption(booksStatusOption)
    echarts.init(booksCategory.value!).setOption(booksCategoryOption)
    echarts.init(borrowingHot.value!).setOption(borrowingHotOption)
    echarts.init(FineTotalOfMonth.value!).setOption(FineTotalOfMonthOption)
});
</script>

<style scoped>
.list {
    flex: 1;
}

.thumbnail {
    padding: 0 20px;
    grid-template-columns: repeat(auto-fill, 450px);
}

.chart {
    width: 450px;
    height: 400px;
}
</style>