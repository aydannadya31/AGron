# AGron - Android TV Web Tarayıcısı Uygulama Planı

AGron, Android TV Box ve akıllı televizyonlar için özel olarak optimize edilmiş; uzaktan kumanda (D-pad ve sanal fare) ile zahmetsizce gezilebilen, entegre reklam engelleyiciye, sesli aramaya, yer imi yöneticisine ve gizli moda sahip hafif ve hızlı bir web tarayıcısıdır.

---

### Kullanıcı Tercihleri ve Kararları

- **Uygulama Adı:** **AGron** (sistem genelinde, manifest ve platform meta verisinde güncellenmiştir).
- **Görsel Kimlik & Boş Sayfa:** Kullanıcı tarafından sağlanan kurumsal amblem/vektörel logo (`agron_logo.png`), uygulama başlatıcı simgesi (App Icon) ve yeni sekme / ana ekran arkaplanında şık ve transparan bir kompozisyon olarak kullanılacaktır.
- **Kumanda Gezintisi:** D-pad ile sezgisel odak atlama ve gerektiğinde kumanda orta tuşu / yön tuşlarıyla serbestçe hareket ettirilebilen **Sanal Mouse (Air-Mouse / Virtual Pointer)** modu hibrit olarak sunulacaktır.
- **Masaüstü & TV Modu:** TV ekranlarına uygun yüksek okunabilirlik ve gerektiğinde YouTube/sitelerin tam masaüstü veya TV arayüzlerini açabilmesi için tek tuşla değiştirilebilir User-Agent desteği.
- **Reklam & Takipçi Engelleyici:** Ağ istekleri düzeyinde (WebView request interception) yaygın reklam ağlarını, popup pencereleri ve izleyicileri engelleyen yerleşik hızlı içerik filtresi.
- **Gizli Sekme (Incognito):** Sayfa kapatıldığında çerezleri, geçmişi, form verilerini ve web önbelleğini tamamen temizleyen sıfır-iz modu.

---

### 1. Genel Bakış ve Temel Konsept

- **Hedef Kitle & Kullanım Senaryosu:** Salon ortamında, televizyona bağlı Android TV Box veya Android TV kullanan, koltuktan 2.5 - 4 metre mesafede standart kumanda ile internette video izlemek, haber okumak veya arama yapmak isteyen kullanıcılar.
- **Temel Değer Önerisi:** Karmaşık fare/klavye gerektirmeyen 10-foot (TV) odaklı arayüz, sinematik tam ekran video oynatıcı, sesli arama ile hızlı giriş ve reklamların televizyon deneyimini yavaşlatmasını engelleyen hafif mimari.

---

### 2. Kullanıcı Deneyimi ve Görsel Tasarım

#### Kullanıcı Akışları
1. **Ana Ekran (Hızlı Başlangıç):**
   - Transparan **AGron** logosu merkezli modern koyu tema arka plan.
   - Odaklanabilir akıllı adres/arama çubuğu (URL girişi veya Google/DuckDuckGo araması).
   - Kumanda mikrofon tuşu veya ekrandaki mikrofon butonu ile **Sesli Arama (Speech-to-Text)** tetikleme.
   - Sık ziyaret edilenler ve yer imleri için büyük, kumanda dostu **Hızlı Erişim Kartları (Speed Dials)**: YouTube, Google, Wikipedia, Haberler ve kullanıcının eklediği siteler.
   - Üst Menü Çubuğu: Geri/İleri, Yenile, Sekmeler, Yer İmleri, Reklam Engelleyici Durumu, Sanal Fare Geçişi, Gizli Mod ve Ayarlar.

2. **Web Gezintisi & Sanal Fare:**
   - D-pad yön tuşlarıyla sayfa kaydırma veya ekranda beliren sanal fare imleci ile bağlantılara doğrudan tıklama imkanı.
   - Sayfa yükleme ilerleme çubuğu (ince, zarif üst neon mavi şerit).
   - Masaüstü görünümü / TV görünümü geçiş düğmesi.

3. **Tam Ekran Video Oynatma:**
   - Web sayfalarındaki HTML5 videolar için `WebChromeClient.onShowCustomView` desteği ile sinematik tam ekran geçişi ve kumanda geri tuşu ile sayfaya sorunsuz dönüş.

4. **Sekme & Yer İmi Yönetimi:**
   - Hızlı sekme geçiş çekmecesi / çubuğu (Normal sekmeler ve Gizli sekmeler ayrımı).
   - Tek tuşla geçerli sayfayı yer imlerine ekleme, yer imi düzenleme, klasörleme ve silme.

5. **Kişiselleştirilebilir Ayarlar:**
   - Varsayılan Arama Motoru seçimi (Google, Yandex, Bing, DuckDuckGo).
   - Reklam engelleyici hassasiyet derecesi.
   - Varsayılan Yakınlaştırma (Text Zoom %100 - %150) TV metin okunurluğu için.
   - Tek tıkla "Geçmişi, Çerezleri ve Önbelleği Temizle" eylemi.
   - JavaScript, Açılır Pencereler (Pop-ups) ve Güvenli Gezinti izin anahtarları.

#### Görsel Tema & Renk Paleti (Deep Cinema Dark)
- **Arka Plan:** Koyu antrasit / gece mavisi (`#0F1117`), parlamayan ve TV panellerinde siyah seviyesini koruyan mat yapı.
- **Yüzeyler & Kartlar:** Yarı saydam füme tonlar (`#1A1D27`), %12 opaklıkta neon kenarlıklar.
- **Odak & Vurgu (Focus Indicator):** TV kumandası odağını hemen belli eden canlı Elektrik Mavisi (`#3B82F6` ve `#60A5FA`) ile hafif ışıma efekti.
- **Tipografi:** Uzaktan kolayca okunabilen yüksek kontrastlı, geniş harf aralıklı modern tipografi.

---

### 3. Temel Ürün Kararları & Mimari Tercihler

| Karar Alanı | Seçilen Yaklaşım | Tercih Nedeni |
|---|---|---|
| **Kumanda Navigasyonu** | Hibrit D-pad + Sanal Fare Modu | Standart TV web siteleri D-pad ile gezilebilirken, masaüstü sitelerde tıklanması zor küçük menüler sanal fare imleci ile zahmetsizce yönetilir. |
| **Veri Saklama** | Android Room Database | Yer imleri, gezinti geçmişi ve hızlı erişim kartları çevrimdışı, hafif ve cihaz yerelinde tutulur; sıfır gecikme sağlar. |
| **Gizli Mod (Incognito)** | Bellek-içi WebView & Geçici Depolama | Gizli sekme kapatıldığı anda `CookieManager.removeAllCookies`, `clearHistory()` ve `clearCache(true)` çağrılarak cihazda veri bırakılmaz. |
| **Reklam Engelleyici** | `shouldInterceptRequest` Tabanlı Filtreleme | Bilinen reklam, izleyici ve popup alan adlarını (EasyList çekirdek kuralları) engeller; harici ağır kütüphanelere gerek kalmadan maksimum hız ve veri tasarrufu sağlar. |
| **Ses Tanıma** | Android `RecognizerIntent` / Sesli Arama | TV kumandalarındaki mikrofon veya sistem ses servisini doğrudan entegre ederek kumandadan harf girmeyi ortadan kaldırır. |

---

### 4. Teknik Mimari & Bileşen Hiyerarşisi

```
┌────────────────────────────────────────────────────────────────────────┐
│                        AGron Android TV Browser                        │
├────────────────────────────────────────────────────────────────────────┤
│                                                                        │
│   ┌────────────────────────────────────────────────────────────────┐   │
│   │ TV Top Bar (Adres, Arama, Sesli Arama, Sekmeler, Ayarlar)      │   │
│   └────────────────────────────────────────────────────────────────┘   │
│                                                                        │
│   ┌───────────────────────────────┐  ┌─────────────────────────────┐   │
│   │       Yeni Sekme / Ana        │  │       Aktif Web Görünümü    │   │
│   │    - AGron Amblem Arkaplanı   │  │   - Chromium WebView motoru │   │
│   │    - Hızlı Erişim Kartları    │  │   - Reklam Engelleyici Kalkan│  │
│   │    - Son Yer İmleri & Arama   │  │   - Sanal Fare / D-pad Odak │   │
│   └───────────────────────────────┘  └─────────────────────────────┘   │
│                                                                        │
│   ┌────────────────────────────────────────────────────────────────┐   │
│   │ Katmanlar: Tam Ekran Video Görünümü │ Ayarlar & Yer İmi Paneli │   │
│   └────────────────────────────────────────────────────────────────┘   │
│                                                                        │
├────────────────────────────────────────────────────────────────────────┤
│                        Durum ve Veri Katmanı                           │
│  - BrowserViewModel (Sekmeler, Navigasyon, Sanal Fare Konumu)          │
│  - AdBlockEngine (Domain Filtresi, Pop-up Engelleyici)                 │
│  - Room Database (BookmarksDao, HistoryDao)                            │
│  - BrowserPreferences (Arama Motoru, Yakınlaştırma, User-Agent)        │
└────────────────────────────────────────────────────────────────────────┘
```

---

### Uygulama Adımları

1. **Platform Meta & İsimlendirme:** `metadata.json` ve `strings.xml` dosyalarını **AGron** olarak güncelleme.
2. **Görsel Varlıklar:** Kullanıcının sağladığı amblemi (`agron_logo.png`) başlatıcı simgesi (ic_launcher) ve arkaplan bileşeni olarak yerleştirme.
3. **Veri Modelleri & Yerel Depolama:** Yer imleri ve geçmiş için Room Veritabanı yapılandırması.
4. **Reklam Engelleyici Motoru:** Yaygın reklam sunucularını ve izleyicileri filtreleyen `AdBlockEngine` oluşturma.
5. **WebView & Kumanda Entegrasyonu:** D-pad desteği, sanal fare imleci, HTML5 tam ekran video (`WebChromeClient`), masaüstü/mobil mod geçişi ve gizli sekme yönetimi.
6. **TV Arayüzü & Jetpack Compose:** 10-foot TV deneyimine uygun Hızlı Erişim Ana Sayfası, Adres/Arama Çubuğu, Sesli Arama, Sekme Çekmecesi, Yer İmi Yöneticisi ve Ayarlar Paneli.
7. **Derleme & Doğrulama:** `compile_applet` ile hatasız derlemeyi tamamlama.
