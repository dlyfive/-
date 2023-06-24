const selectCourse = (ids) => {
    return $axios({
        url: '/stud/selectCourse',
        method: 'post',
        params:{ids}
    })
}


function getCourseList (params) {
    return $axios({
        // url: '/employee/page',
        url: '/course/page',
        method: 'get',
        params
    })
}

