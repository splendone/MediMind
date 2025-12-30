import request from './request'

// 添加健康记录
export function addHealthRecord(data) {
  return request({
    url: '/health/record',
    method: 'post',
    data
  })
}

// 获取健康记录列表
export function getHealthRecords(params) {
  return request({
    url: '/health/records',
    method: 'get',
    params
  })
}

// 获取健康记录详情
export function getHealthRecord(id) {
  return request({
    url: `/health/record/${id}`,
    method: 'get'
  })
}

// 更新健康记录
export function updateHealthRecord(id, data) {
  return request({
    url: `/health/record/${id}`,
    method: 'put',
    data
  })
}

// 删除健康记录
export function deleteHealthRecord(id) {
  return request({
    url: `/health/record/${id}`,
    method: 'delete'
  })
}

// 获取健康统计
export function getStatistics(days = 30) {
  return request({
    url: '/health/statistics',
    method: 'get',
    params: { days }
  })
}

// 获取趋势数据
export function getTrendData(indicator, days = 30) {
  return request({
    url: '/health/trend',
    method: 'get',
    params: { indicator, days }
  })
}
