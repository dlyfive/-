function getStudScoreList (params) {
    return $axios({
        // url: '/employee/page',
        url: '/stud/studScore',
        method: 'get',
        params
    })
}