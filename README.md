# AGron - Android TV Web Tarayıcısı

<p align="center">
  <img src="agron_logo.png" width="128" height="128" alt="AGron Logo" style="border-radius: 20px;">
</p>

<p align="center">
  <strong>Android TV Box ve Akıllı Televizyonlar için Hafif, Hızlı, Reklam Engelleyicili ve Uzaktan Kumanda Destekli Web Tarayıcısı</strong>
</p>

<p align="center">
  <a href="agron-tv.apk"><b>[ Hemen İndir (agron-tv.apk) ]</b></a> • 
  <a href="https://aydannadya31.github.io/AGron/"><b>[ Canlı İndirme Sayfası ]</b></a>
</p>

---

## 🌟 Öne Çıkan Özellikler

- **🎮 Kumanda ve Hibrit Sanal Fare (Virtual Mouse):**
  - Standart TV kumandası D-pad yön tuşlarıyla kesintisiz gezinme ve odak takibi.
  - Tıklaması zor web siteleri ve menüler için tek tuşla açılan **Sanal Fare İmleci**.
- **🛡️ Entegre Reklam Engelleyici (AdBlock Engine):**
  - Pop-up, yönlendirme, takipçi ve video reklamlarını ağ katmanında engeller.
  - Sayfa açılış hızını artırır ve bellek tüketimini azaltır.
- **🎙️ Sesli Arama (Speech-to-Text):**
  - Kumandadan harf girmeden arama kutusuna konuşarak anında Google/web araması yapma.
- **🕵️ Sıfır-İz Gizli Mod (Incognito):**
  - Gizli sekme kapatıldığı an çerezler, web önbelleği ve form verileri tamamen temizlenir.
- **🎬 Sinematik Tam Ekran HTML5 Video:**
  - Web sitelerindeki video oynatıcıları televizyonun tam ekranına taşır; kumanda geri tuşu ile sayfaya döner.
- **⭐ Akıllı Sekme & Yer İmleri (Speed Dial):**
  - Birden fazla sekme arasında hızlı geçiş.
  - YouTube, Google, Wikipedia, Haberler gibi popüler sitelere tek tıkla erişim ve özel yer imi ekleme.
- **⚙️ Kişiselleştirilebilir Ayarlar:**
  - Arama motoru seçimi (Google, Yandex, DuckDuckGo, Bing).
  - TV Metin Yakınlaştırma (%100 - %150).
  - Masaüstü Chrome veya Android TV modu geçişi.
  - Tek tıkla "Geçmişi ve Verileri Temizle".

---

## 📱 İndirme & QR Kod ile Kurulum

**Canlı site:** https://aydannadya31.github.io/AGron/  (GitHub Pages, `main` / `/docs`)

Bu depoda yer alan **`index.html`** ve **`docs/index.html`** web sayfası, GitHub Pages üzerinden canlı yayınlandığında:
1. **İndir Butonu:** `agron-tv.apk` dosyasını doğrudan indirir.
2. **QR Kod:** Butonun hemen altındaki QR kodu telefonunuz veya Android TV'nizin kamerası/tarayıcısıyla okutarak APK'yı anında indirebilirsiniz.

### Android TV'ye Kurulum Adımları:
1. **Yöntem (QR Kod & Send Files to TV):**
   - Web sitesindeki QR kodu telefonunuza okutup APK'yı indirin.
   - Telefon ve TV'deki *Send Files to TV* uygulaması ile TV'nize gönderip kurun.
2. **Yöntem (Downloader Uygulaması):**
   - Android TV'de *Downloader by AFTVnews* uygulamasını açın.
   - Bu sayfanın veya APK dosyasının doğrudan bağlantısını yazarak indirin.
3. **Yöntem (USB Bellek):**
   - `agron-tv.apk` dosyasını USB belleğe kopyalayın ve TV Box'ınıza takıp dosya yöneticisiyle yükleyin.

---

## 🌐 GitHub Pages Canlı Yayın Durumu

Site **yayında**: https://aydannadya31.github.io/AGron/

Kaynak: `main` dalı, `/docs` klasörü (GitHub Pages → Settings → Pages → Deploy from a branch → `main` / `/docs`). Klasördeki `index.html`, `agron_logo.png` ve `agron-tv.apk` otomatik olarak yayınlanır; QR kodu `agron-qr.png` dosyasıdır.

---

## 🛠️ Kaynak Koddan Derleme

Android Studio veya Gradle CLI ile:

```bash
# Debug APK derleme
gradle :app:assembleDebug

# Birim testlerini çalıştırma
gradle :app:testDebugUnitTest
```

Derlenen APK: `app/build/outputs/apk/debug/app-debug.apk`

---

© 2026 **AGron** - Android TV & TV Box Web Tarayıcısı.
