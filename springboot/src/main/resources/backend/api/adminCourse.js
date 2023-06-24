function getCourseList (params) {
    return $axios({
        // url: '/employee/page',
        url: '/course/page',
        method: 'get',
        params
    })
}

// 新增---添加课程
function addCourse (params) {
    return $axios({
        url: '/course',
        method: 'post',
        data: { ...params }
    })
}

// 修改---修改课程
function editCourse (params) {
    return $axios({
        url: '/course',
        method: 'put',
        data: { ...params }
    })
}

// 修改页面反查详情接口
function queryCourseById (id) {
    return $axios({
        url: `/course/${id}`,
        method: 'get'
    })
}
//删除课程接口
const deleteCourse = (ids) => {
    return $axios({
        url: '/course',
        method: 'delete',
        params: { ids }
    })
}