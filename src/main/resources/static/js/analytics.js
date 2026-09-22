// ============================================
// 数据大屏逻辑
// ============================================

const API_BASE = '/api/analytics';

let charts = {};

// 初始化所有图表
function initCharts() {
    charts.dailyVotes = echarts.init(document.getElementById('chartDailyVotes'));
    charts.topicHot = echarts.init(document.getElementById('chartTopicHot'));
    charts.typeDist = echarts.init(document.getElementById('chartTypeDist'));
    charts.userRank = echarts.init(document.getElementById('chartUserRank'));
    charts.sentiment = echarts.init(document.getElementById('chartSentiment'));
    charts.hourlyVotes = echarts.init(document.getElementById('chartHourlyVotes'));
    
    // 窗口自适应
    window.addEventListener('resize', () => {
        Object.values(charts).forEach(chart => chart.resize());
    });
}

// 通用请求
async function fetchData(url) {
    try {
        const res = await axios.get(`${API_BASE}${url}`);
        return res.data;
    } catch (e) {
        console.error(`请求失败: ${url}`, e);
        return [];
    }
}

// 加载汇总数据
async function loadSummary() {
    const data = await fetchData('/summary');
    if (data) {
        document.getElementById('summaryTotalVotes').textContent = data.totalVotes || 0;
        document.getElementById('summaryTotalUsers').textContent = data.totalUsers || 0;
        document.getElementById('summaryTotalTopics').textContent = data.totalTopics || 0;
        document.getElementById('summaryAnalysisTime').textContent = data.analysisTime || '--';
    }
}

// 每日投票趋势（折线面积图）
async function loadDailyVotes() {
    const data = await fetchData('/daily-votes');
    const dates = data.map(item => item.voteDate);
    const counts = data.map(item => item.voteCount);
    
    charts.dailyVotes.setOption({
        tooltip: {
            trigger: 'axis',
            backgroundColor: 'rgba(11, 28, 61, 0.9)',
            borderColor: '#409eff',
            textStyle: { color: '#fff' }
        },
        grid: { left: '3%', right: '4%', bottom: '3%', top: '10%', containLabel: true },
        xAxis: {
            type: 'category',
            data: dates,
            axisLine: { lineStyle: { color: 'rgba(64, 158, 255, 0.3)' } },
            axisLabel: { color: '#a0cfff' }
        },
        yAxis: {
            type: 'value',
            axisLine: { show: false },
            axisLabel: { color: '#a0cfff' },
            splitLine: { lineStyle: { color: 'rgba(64, 158, 255, 0.1)' } }
        },
        series: [{
            data: counts,
            type: 'line',
            smooth: true,
            areaStyle: {
                color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                    { offset: 0, color: 'rgba(64, 158, 255, 0.5)' },
                    { offset: 1, color: 'rgba(64, 158, 255, 0.05)' }
                ])
            },
            lineStyle: { color: '#409eff', width: 2 },
            itemStyle: { color: '#409eff' }
        }]
    });
}

// 热门话题 TOP10（横向柱状图）
async function loadTopicHot() {
    const data = await fetchData('/topic-hot');
    const names = data.map(item => item.topicName).reverse();
    const counts = data.map(item => item.voteCount).reverse();
    
    charts.topicHot.setOption({
        tooltip: {
            trigger: 'axis',
            backgroundColor: 'rgba(11, 28, 61, 0.9)',
            borderColor: '#409eff',
            textStyle: { color: '#fff' }
        },
        grid: { left: '3%', right: '8%', bottom: '3%', top: '5%', containLabel: true },
        xAxis: {
            type: 'value',
            axisLine: { show: false },
            axisLabel: { color: '#a0cfff' },
            splitLine: { lineStyle: { color: 'rgba(64, 158, 255, 0.1)' } }
        },
        yAxis: {
            type: 'category',
            data: names,
            axisLine: { lineStyle: { color: 'rgba(64, 158, 255, 0.3)' } },
            axisLabel: { color: '#a0cfff' }
        },
        series: [{
            data: counts,
            type: 'bar',
            barWidth: 12,
            itemStyle: {
                color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
                    { offset: 0, color: '#409eff' },
                    { offset: 1, color: '#67c23a' }
                ]),
                borderRadius: [0, 4, 4, 0]
            }
        }]
    });
}

// 话题类型分布（环形饼图）
async function loadTypeDist() {
    const data = await fetchData('/type-dist');
    
    charts.typeDist.setOption({
        tooltip: {
            trigger: 'item',
            backgroundColor: 'rgba(11, 28, 61, 0.9)',
            borderColor: '#409eff',
            textStyle: { color: '#fff' }
        },
        legend: {
            bottom: '0%',
            textStyle: { color: '#a0cfff' }
        },
        series: [{
            type: 'pie',
            radius: ['40%', '70%'],
            center: ['50%', '45%'],
            avoidLabelOverlap: false,
            itemStyle: {
                borderRadius: 6,
                borderColor: '#0b1c3d',
                borderWidth: 2
            },
            label: {
                show: false,
            },
            emphasis: {
                label: {
                    show: true,
                    fontSize: 14,
                    fontWeight: 'bold',
                    color: '#fff'
                }
            },
            data: data
        }]
    });
}

// 用户活跃度（柱状图）
async function loadUserRank() {
    const data = await fetchData('/user-rank');
    const names = data.map(item => item.username || item.nickname);
    const counts = data.map(item => item.voteCount);
    
    charts.userRank.setOption({
        tooltip: {
            trigger: 'axis',
            backgroundColor: 'rgba(11, 28, 61, 0.9)',
            borderColor: '#409eff',
            textStyle: { color: '#fff' }
        },
        grid: { left: '3%', right: '4%', bottom: '3%', top: '10%', containLabel: true },
        xAxis: {
            type: 'category',
            data: names,
            axisLine: { lineStyle: { color: 'rgba(64, 158, 255, 0.3)' } },
            axisLabel: { color: '#a0cfff' }
        },
        yAxis: {
            type: 'value',
            axisLine: { show: false },
            axisLabel: { color: '#a0cfff' },
            splitLine: { lineStyle: { color: 'rgba(64, 158, 255, 0.1)' } }
        },
        series: [{
            data: counts,
            type: 'bar',
            barWidth: 20,
            itemStyle: {
                color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                    { offset: 0, color: '#409eff' },
                    { offset: 1, color: 'rgba(64, 158, 255, 0.3)' }
                ]),
                borderRadius: [4, 4, 0, 0]
            }
        }]
    });
}

// 评论情感分布（玫瑰图）
async function loadSentiment() {
    const data = await fetchData('/sentiment');
    
    charts.sentiment.setOption({
        tooltip: {
            trigger: 'item',
            backgroundColor: 'rgba(11, 28, 61, 0.9)',
            borderColor: '#409eff',
            textStyle: { color: '#fff' }
        },
        legend: {
            bottom: '0%',
            textStyle: { color: '#a0cfff' }
        },
        series: [{
            type: 'pie',
            radius: [20, '70%'],
            center: ['50%', '45%'],
            roseType: 'area',
            itemStyle: {
                borderRadius: 5,
                borderColor: '#0b1c3d',
                borderWidth: 2
            },
            label: {
                color: '#a0cfff'
            },
            data: data
        }]
    });
}

// 时段投票分布（柱状图）
async function loadHourlyVotes() {
    const data = await fetchData('/hourly-votes');
    const hours = data.map(item => `${item.hourOfDay}时`);
    const counts = data.map(item => item.voteCount);
    
    charts.hourlyVotes.setOption({
        tooltip: {
            trigger: 'axis',
            backgroundColor: 'rgba(11, 28, 61, 0.9)',
            borderColor: '#409eff',
            textStyle: { color: '#fff' }
        },
        grid: { left: '3%', right: '4%', bottom: '3%', top: '10%', containLabel: true },
        xAxis: {
            type: 'category',
            data: hours,
            axisLine: { lineStyle: { color: 'rgba(64, 158, 255, 0.3)' } },
            axisLabel: { color: '#a0cfff' }
        },
        yAxis: {
            type: 'value',
            axisLine: { show: false },
            axisLabel: { color: '#a0cfff' },
            splitLine: { lineStyle: { color: 'rgba(64, 158, 255, 0.1)' } }
        },
        series: [{
            data: counts,
            type: 'bar',
            barWidth: 16,
            itemStyle: {
                color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                    { offset: 0, color: '#e6a23c' },
                    { offset: 1, color: 'rgba(230, 162, 60, 0.3)' }
                ]),
                borderRadius: [4, 4, 0, 0]
            }
        }]
    });
}

// 更新时间
function updateTime() {
    const now = new Date();
    const str = now.getFullYear() + '-' + 
        String(now.getMonth() + 1).padStart(2, '0') + '-' + 
        String(now.getDate()).padStart(2, '0') + ' ' + 
        String(now.getHours()).padStart(2, '0') + ':' + 
        String(now.getMinutes()).padStart(2, '0') + ':' + 
        String(now.getSeconds()).padStart(2, '0');
    document.getElementById('currentTime').textContent = str;
}

// 加载所有数据
async function loadAllData() {
    await Promise.all([
        loadSummary(),
        loadDailyVotes(),
        loadTopicHot(),
        loadTypeDist(),
        loadUserRank(),
        loadSentiment(),
        loadHourlyVotes()
    ]);
}

// 初始化
window.onload = function() {
    initCharts();
    loadAllData();
    updateTime();
    setInterval(updateTime, 1000);
    
    // 每30秒刷新
    setInterval(loadAllData, 30000);
};
