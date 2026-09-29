# টাকা ইনকাম (Taka Income) - ওয়েবসাইট ও অ্যাপ নির্দেশনা

## ১. সরাসরি APK ডাউনলোড ও ব্যবহারের ফাইল
- ফাইল নাম: `Taka_Income_v1.0.apk` (সাইজ: ২৭ মেগাবাইট)
- লোকেশন: 
  - `website/Taka_Income_v1.0.apk`
  - `APK_DOWNLOAD/app-debug.apk`
  - `.build-outputs/app-debug.apk`

---

## ২. অফিসিয়াল ওয়েবসাইট (Landing Page & Download Portal)
- ফাইল লোকেশন: `website/index.html`
- এই ওয়েবসাইটে রয়েছে:
  - আকর্ষণীয় ব্যানার ও মোবাইল প্রিভিউ
  - সরাসরি ১-ক্লিকে `Taka_Income_v1.0.apk` ডাউনলোড বাটন
  - প্লে স্টোর ছাড়া কীভাবে ইনস্টল করবেন তার স্টেপ-বাই-স্টেপ গাইড
  - বিকাশ ও নগদ ক্যাশআউট নিয়ম ও চার্ট
  - সরাসরি হোয়াটসঅ্যাপ হেল্পলাইন বাটন (+880 1921-679459)
  - ইন্টারেক্টিভ FAQ সেকশন
  - মোবাইলের জন্য বটম ফ্লোটিং ডাউনলোড বার

---

## ৩. ওয়েবসাইট কীভাবে ফ্রি-তে লাইভ করবেন?

### অপশন ক: Firebase Hosting (সবচেয়ে সহজ ও দ্রুত)
আপনার যদি Firebase CLI ইনস্টল থাকে:
```bash
firebase login
firebase init hosting   # (public directory হিসেবে লিখুন: website)
firebase deploy --only hosting
```
মুহূর্তেই আপনার ওয়েবসাইট লাইভ হয়ে যাবে যেমন: `https://your-project-id.web.app`

### অপশন খ: GitHub Pages / Vercel / Netlify
- `website/` ফোল্ডারের ফাইলগুলো GitHub-এ আপলোড করে **GitHub Pages** বা **Netlify / Vercel**-এ দিলে সম্পূর্ণ বিনামূল্যে নিজস্ব ডোমেইন বা সাব-ডোমেইনে লাইভ হয়ে যাবে।

### অপশন গ: সরাসরি শেয়ার
আপনি চাইলে শুধু `APK_DOWNLOAD/app-debug.apk` ফাইলটি Google Drive, Mediafire বা WhatsApp-এ আপলোড করে যেকোনো বন্ধু বা ইউজারকে ডাউনলোড লিংক পাঠিয়ে দিতে পারেন।
