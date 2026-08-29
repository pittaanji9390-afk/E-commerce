/**
 * Enterprise Marketplace Application Entry Point & Orchestrator
 */
const { spawn } = require('child_process');
const path = require('path');

console.log('====================================================');
console.log(' Enterprise Multi-Vendor E-Commerce Platform v1.0.0 ');
console.log('====================================================');

const isProd = process.env.NODE_ENV === 'production';
console.log(`Environment: ${isProd ? 'Production' : 'Development'}`);

function startBackend() {
  console.log('[Runner] Bootstrapping Spring Boot Backend service...');
  const mvn = process.platform === 'win32' ? 'mvn.cmd' : 'mvn';
  return spawn(mvn, ['spring-boot:run'], {
    cwd: path.join(__dirname, 'backend'),
    stdio: 'inherit',
    shell: true
  });
}

function startFrontend() {
  console.log('[Runner] Bootstrapping React Frontend dev server...');
  const npm = process.platform === 'win32' ? 'npm.cmd' : 'npm';
  return spawn(npm, ['run', 'dev'], {
    cwd: path.join(__dirname, 'frontend'),
    stdio: 'inherit',
    shell: true
  });
}

if (require.main === module) {
  console.log('[Runner] Starting Fullstack Marketplace services...');
  // Check command-line args or start default
  if (process.argv.includes('--backend-only')) {
    startBackend();
  } else if (process.argv.includes('--frontend-only')) {
    startFrontend();
  } else {
    console.log('[Runner] For full cluster deployment, run: docker-compose up');
  }
}

module.exports = { startBackend, startFrontend };
