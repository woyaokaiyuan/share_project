export {}

declare module 'vue-router' {
  interface RouteMeta {
    title?: string
    icon?: string
    hidden?: boolean
    affix?: boolean
    keepAlive?: boolean
    permission?: string
  }
}
