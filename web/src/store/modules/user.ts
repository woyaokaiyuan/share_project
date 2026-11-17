import { defineStore } from 'pinia'
import { loginApi, getUserInfoApi, logoutApi } from '@/api/auth'
import { getToken, setToken, removeToken } from '@/utils/auth'
import type { LoginParams, UserInfo } from '@/types'

const isMock = () => import.meta.env.VITE_USE_MOCK === 'true'

const DEMO_USER: UserInfo = {
  id: '1',
  username: 'admin',
  nickname: '系统管理员',
  roles: ['admin'],
  permissions: ['*:*:*']
}

export const useUserStore = defineStore('user', {
  state: () => ({
    token: getToken() || '',
    userInfo: null as UserInfo | null
  }),
  getters: {
    nickname: (state) => state.userInfo?.nickname || state.userInfo?.username || '管理员'
  },
  actions: {
    async login(params: LoginParams) {
      if (isMock()) {
        setToken(`demo-token-${Date.now()}`)
        this.token = getToken() || ''
        this.userInfo = { ...DEMO_USER, username: params.username }
        return this.userInfo
      }
      const data = await loginApi(params)
      setToken(data.token)
      this.token = data.token
      const { token: _token, ...info } = data
      this.userInfo = info as UserInfo
      return this.userInfo
    },
    async getInfo() {
      if (isMock()) {
        if (!this.userInfo) this.userInfo = { ...DEMO_USER }
        return this.userInfo
      }
      const data = await getUserInfoApi()
      this.userInfo = data
      return data
    },
    async logout() {
      if (!isMock()) {
        try { await logoutApi() } catch { /* 忽略退出接口异常 */ }
      }
      removeToken()
      this.token = ''
      this.userInfo = null
    }
  }
})
