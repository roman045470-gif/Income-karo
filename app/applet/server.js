const http = require("http");
const fs = require("fs");
const path = require("path");

const PORT = 3000;
const WEBSITE_DIR = path.join(__dirname, "website");

const MIME_TYPES = {
  ".html": "text/html; charset=utf-8",
  ".css": "text/css; charset=utf-8",
  ".js": "application/javascript; charset=utf-8",
  ".json": "application/json; charset=utf-8",
  ".png": "image/png",
  ".jpg": "image/jpeg",
  ".jpeg": "image/jpeg",
  ".svg": "image/svg+xml",
  ".ico": "image/x-icon",
  ".apk": "application/vnd.android.package-archive"
};

const server = http.createServer((req, res) => {
  let reqPath = req.url.split("?")[0];

  // Route root and index
  if (reqPath === "/" || reqPath === "/index.html") {
    reqPath = "/index.html";
  }

  // APK download shortcuts: /download, /apk, /Taka_Income_v1.0.apk, /app-debug.apk
  if (
    reqPath === "/download" ||
    reqPath === "/apk" ||
    reqPath === "/Taka_Income_v1.0.apk" ||
    reqPath === "/app-debug.apk" ||
    reqPath.endsWith(".apk")
  ) {
    const apkCandidates = [
      path.join(WEBSITE_DIR, "Taka_Income_v1.0.apk"),
      path.join(WEBSITE_DIR, "app-debug.apk"),
      path.join(__dirname, "APK_DOWNLOAD", "app-debug.apk"),
      path.join(__dirname, ".build-outputs", "app-debug.apk"),
      path.join(__dirname, "app", "build", "outputs", "apk", "debug", "app-debug.apk")
    ];

    let foundApk = null;
    for (const cand of apkCandidates) {
      if (fs.existsSync(cand)) {
        foundApk = cand;
        break;
      }
    }

    if (foundApk) {
      const stat = fs.statSync(foundApk);
      res.writeHead(200, {
        "Content-Type": "application/vnd.android.package-archive",
        "Content-Length": stat.size,
        "Content-Disposition": "attachment; filename=\"Taka_Income_v1.0.apk\"",
        "Cache-Control": "no-cache",
        "Access-Control-Allow-Origin": "*"
      });
      fs.createReadStream(foundApk).pipe(res);
      return;
    } else {
      res.writeHead(404, { "Content-Type": "text/plain; charset=utf-8" });
      res.end("APK file not found on server.");
      return;
    }
  }

  // Serve static files from website directory
  const filePath = path.join(WEBSITE_DIR, reqPath);

  // Security check: prevent directory traversal
  if (!filePath.startsWith(WEBSITE_DIR)) {
    res.writeHead(403, { "Content-Type": "text/plain" });
    res.end("Forbidden");
    return;
  }

  if (fs.existsSync(filePath) && fs.statSync(filePath).isFile()) {
    const ext = path.extname(filePath).toLowerCase();
    const contentType = MIME_TYPES[ext] || "application/octet-stream";
    res.writeHead(200, {
      "Content-Type": contentType,
      "Access-Control-Allow-Origin": "*"
    });
    fs.createReadStream(filePath).pipe(res);
  } else {
    // Fallback to index.html for SPA-like navigation
    const indexPath = path.join(WEBSITE_DIR, "index.html");
    if (fs.existsSync(indexPath)) {
      res.writeHead(200, {
        "Content-Type": "text/html; charset=utf-8",
        "Access-Control-Allow-Origin": "*"
      });
      fs.createReadStream(indexPath).pipe(res);
    } else {
      res.writeHead(404, { "Content-Type": "text/plain" });
      res.end("404 Not Found");
    }
  }
});

server.listen(PORT, "0.0.0.0", () => {
  console.log(`Taka Income Web Server running on port ${PORT}`);
});
