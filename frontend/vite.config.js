import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      // 只要是 /api 开头的请求，都转发给后端 3001
      '/api': {
        target: 'http://localhost:8080', // 你的后端地址
        changeOrigin: true,
        
      }
    }
  }
})