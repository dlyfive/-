function getTeacherList (params) {
    return $axios({
        // url: '/employee/page',
        url: '/teacher/page',
        method: 'get',
        params
    })
}

// 新增---添加学生
function addTeacher (params) {
    return $axios({
        url: '/teacher',
        method: 'post',
        data: { ...params }
    })
}

// 修改---修改学生
function editTeacher (params) {
    return $axios({
        url: '/teacher',
        method: 'put',
        data: { ...params }
    })
}

// 修改页面反查详情接口
function queryTeacherById (id) {
    return $axios({
        url: `/teacher/${id}`,
        method: 'get'
    })
}
// //删除接口
// const deleteCourse = (ids) => {
//     return $axios({
//         url: '/stud',
//         method: 'delete',
//         params: { ids }
//     })
// }
// 修改---启用禁用接口
function enableOrDisableTeacher (params) {
    return $axios({
        url: '/teacher',
        method: 'put',
        data: { ...params }
    })
}