import axios from 'axios'

export default axios.create({
  baseURL: 'http://127.0.0.1:8080/',
  timeout: 3000,
  withCredentials: true
})
