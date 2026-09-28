import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/products': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
      '/pricing-suggestions': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
      '/reorder-suggestions': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      }
    }
  }
})