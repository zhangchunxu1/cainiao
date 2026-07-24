<script setup lang="ts">
import { onLaunch, onShow, onHide } from '@dcloudio/uni-app'
import { useAuthStore } from './store/auth'

onLaunch(() => {
  console.log('[App] App Launch')
  const authStore = useAuthStore()
  authStore.initFromStorage()
})

onShow(() => {
  console.log('[App] App Show')
})

onHide(() => {
  console.log('[App] App Hide')
})
</script>

<style lang="scss">
@import './app.scss';
</style>
