function getCourseResultList (params) {
    return $axios({
        // url: '/employee/page',
        url: '/teacher/pageCQ',
        method: 'get',
        params
    })
}

function getStudentCourseList (params) {
    return $axios({
        url: '/teacher/pageST',
        method: 'get',
        params
    })
}