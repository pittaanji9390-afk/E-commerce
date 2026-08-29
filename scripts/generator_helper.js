const fs = require('fs');
const path = require('path');

function ensureDir(filePath) {
  const dir = path.dirname(filePath);
  if (!fs.existsSync(dir)) {
    fs.mkdirSync(dir, { recursive: true });
  }
}

function write(file, content) {
  ensureDir(file);
  fs.writeFileSync(file, content.trim() + '\n', 'utf8');
}

module.exports = { write, ensureDir };
