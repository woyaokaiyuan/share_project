import type { Directive, DirectiveBinding } from 'vue'
import { useUserStore } from '@/store'

/** 判断当前用户是否拥有指定权限 */
function hasPermission(value: string | string[]): boolean {
  const permissions = useUserStore().userInfo?.permissions || []
  if (permissions.includes('*:*:*')) return true
  const required = Array.isArray(value) ? value : [value]
  return required.some((code) => permissions.includes(code))
}

function apply(el: HTMLElement, binding: DirectiveBinding<string | string[]>) {
  el.style.display = hasPermission(binding.value) ? '' : 'none'
}

/**
 * 按钮级权限指令。
 * 用法：v-permission="'system:user:add'" 或 v-permission="['system:user:edit', 'system:user:add']"
 */
export const permission: Directive<HTMLElement, string | string[]> = {
  mounted: apply,
  updated: apply
}
