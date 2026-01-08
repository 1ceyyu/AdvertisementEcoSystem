import { useState, useEffect } from 'react'
import axios from 'axios'
import './App.css'

function App() {
  const [ads, setAds] = useState([])
  const [loading, setLoading] = useState(true)

  // 定义硬编码的公网地址 (后端真实地址)
  const PUBLIC_HOST = 'http://175.24.232.219:8080'

  // 获取广告资源列表
  const fetchAds = async () => {
    try {
      setLoading(true)
      
      // 请求后端数据 (走 Vite 代理)
      const res = await axios.get('/api/ads')
      
      // --- 🚀 最终修正版逻辑 ---
      const fixedData = res.data.map(ad => {
        if (ad.media_url) {
          try {
            // 1. 尝试解析 URL，无论后端返回的是 localhost 还是内网 IP
            // 如果已经是完整 URL (http开头)
            let path = ad.media_url
            if (ad.media_url.startsWith('http')) {
               const urlObj = new URL(ad.media_url)
               path = urlObj.pathname // 只提取 "/uploads/xxx.png" 部分
            }

            // 2. 强制拼接成我们想要的公网 IP + 端口
            // 结果变成: http://175.24.232.219:8080/uploads/xxx.png
            const finalUrl = `${PUBLIC_HOST}${path}`

            return {
              ...ad,
              media_url: finalUrl
            }
          } catch (e) {
            console.warn("URL解析失败，保持原样:", ad.media_url)
            return ad
          }
        }
        return ad
      })
      // --- 逻辑结束 ---

      setAds(fixedData)
    } catch (error) {
      console.error("获取资源失败:", error)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchAds()
  }, [])

  return (
    <div className="container" style={styles.container}>
      <header style={styles.header}>
        <h1>🛰️ AdFlux 资源下发中台</h1>
        <p>联动腾讯云数据库与本地存储，当前已接入 {ads.length} 个资源</p>
        <button onClick={fetchAds} style={styles.refreshBtn}>刷新同步</button>
      </header>
      
      {loading ? (
        <div style={{textAlign: 'center', padding: '50px'}}>同步中...</div>
      ) : (
        <div style={styles.grid}>
          {ads.map((ad) => (
            <div key={ad.id} style={styles.card}>
              {/* 资源预览区 */}
              <div style={styles.mediaBox}>
                {ad.type === 'video' ? (
                  <video 
                    src={ad.media_url} 
                    controls 
                    style={styles.media} 
                    preload="metadata" // 优化加载
                  />
                ) : (
                  <img 
                    src={ad.media_url} 
                    alt={ad.title} 
                    style={styles.media} 
                    onError={(e) => {
                      // 图片加载失败时的兜底图
                      e.target.src='https://via.placeholder.com/300x180?text=Load+Failed'
                      e.target.style.objectFit = 'cover'
                    }}
                  />
                )}
              </div>

              {/* 资源信息区 */}
              <div style={styles.content}>
                <h3 style={styles.title}>{ad.title}</h3>
                <div style={styles.infoLine}>
                  <span style={styles.tag}>{ad.type}</span>
                  <span style={{fontSize: '12px', color: '#999'}}>ID: {ad.id}</span>
                </div>
                
                {/* 外部调用链接展示 */}
                <div style={styles.apiBox}>
                  <p style={styles.apiLabel}>外部调用链接 (API Resource):</p>
                  <code style={styles.code}>{ad.media_url}</code>
                </div>

                <div style={styles.footer}>
                  <a href={ad.target_url} target="_blank" rel="noreferrer" style={styles.link}>
                    测试跳转链接 &rarr;
                  </a>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}

// 纯 CSS-in-JS 样式
const styles = {
  container: { padding: '40px 20px', maxWidth: '1100px', margin: '0 auto', fontFamily: '-apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif' },
  header: { textAlign: 'center', marginBottom: '40px' },
  refreshBtn: { padding: '8px 20px', background: '#007bff', color: 'white', border: 'none', borderRadius: '20px', cursor: 'pointer', marginTop: '10px' },
  grid: { display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(320px, 1fr))', gap: '25px' },
  card: { background: '#fff', borderRadius: '12px', border: '1px solid #eaeaea', overflow: 'hidden', boxShadow: '0 4px 12px rgba(0,0,0,0.05)' },
  mediaBox: { width: '100%', height: '180px', background: '#000', display: 'flex', alignItems: 'center', justifyContent: 'center', overflow: 'hidden' },
  media: { width: '100%', height: '100%', objectFit: 'contain' },
  content: { padding: '20px' },
  title: { margin: '0 0 10px 0', fontSize: '18px', color: '#333' },
  infoLine: { display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '15px' },
  tag: { background: '#f0f2f5', padding: '2px 8px', borderRadius: '4px', fontSize: '11px', fontWeight: 'bold', textTransform: 'uppercase' },
  apiBox: { background: '#f8f9fa', padding: '12px', borderRadius: '8px', border: '1px solid #edf2f7', marginBottom: '15px' },
  apiLabel: { fontSize: '11px', color: '#666', margin: '0 0 5px 0' },
  code: { fontSize: '12px', color: '#d63384', wordBreak: 'break-all', display: 'block' },
  footer: { borderTop: '1px solid #eee', paddingTop: '15px', textAlign: 'right' },
  link: { color: '#007bff', textDecoration: 'none', fontSize: '14px' }
}

export default App