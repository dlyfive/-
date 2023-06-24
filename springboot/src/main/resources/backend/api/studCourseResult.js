
function getCourseResultList (params) {
    return $axios({
        // url: '/employee/page',
        url: '/stud/pageCR',
        method: 'get',
        params
    })
}




