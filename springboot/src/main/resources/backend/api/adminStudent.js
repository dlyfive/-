function getStudentList (params) {
    return $axios({
        // url: '/employee/page',
        url: '/stud/page',
        method: 'get',
        params
    })
}

// 新增---添加学生
function addStudent (params) {
    return $axios({
        url: '/stud',
        method: 'post',
        data: { ...params }
    })
}

// 修改---修改学生
function editStudent (params) {
    return $axios({
        url: '/stud',
        method: 'put',
        data: { ...params }
    })
}

// 修改页面反查详情接口
function queryStudentById (id) {
    return $axios({
        url: `/stud/${id}`,
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
function enableOrDisableStudent (params) {
    return $axios({
        url: '/stud',
        method: 'put',
        data: { ...params }
    })
}