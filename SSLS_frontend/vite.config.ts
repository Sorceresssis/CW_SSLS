import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'


// https://vitejs.dev/config/
export default defineConfig({
	base: './',
	root: resolve(__dirname, './src'),
	build: {
		minify: true,
		outDir: resolve(__dirname, './dist'),
		rollupOptions: {
			input: {
				index: resolve(__dirname, './src/index.html'),
			}
		}
	},
	plugins: [vue()],
	server: {
		proxy: {
			'/SSLS': {
				target: 'http://localhost:8080',
				changeOrigin: true,
			}
		}
	}
})

