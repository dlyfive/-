function loginApi(data) {
  return $axios({
    'url': '/common/login',
    'method': 'post',
    data
  })
}

function logoutApi(){
  return $axios({
    'url': '/common/logout',
    'method': 'post',
  })
}
