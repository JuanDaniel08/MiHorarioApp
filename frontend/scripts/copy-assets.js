import fs from 'fs';
import path from 'path';
import { fileURLToPath } from 'url';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

const srcDir = path.resolve(__dirname, '../dist');
const destDir = path.resolve(__dirname, '../../src/main/resources/static');

// Helper to recursively copy directories
function copyFolderRecursiveSync(source, target) {
  let files = [];

  const targetFolder = path.join(target, path.basename(source));
  if (!fs.existsSync(targetFolder)) {
    fs.mkdirSync(targetFolder, { recursive: true });
  }

  if (fs.lstatSync(source).isDirectory()) {
    files = fs.readdirSync(source);
    files.forEach(function (file) {
      const curSource = path.join(source, file);
      if (fs.lstatSync(curSource).isDirectory()) {
        copyFolderRecursiveSync(curSource, targetFolder);
      } else {
        fs.copyFileSync(curSource, path.join(targetFolder, file));
      }
    });
  }
}

// Clean target directory completely
function cleanDirectory(directory) {
  if (fs.existsSync(directory)) {
    const files = fs.readdirSync(directory);
    for (const file of files) {
      const curPath = path.join(directory, file);
      if (fs.lstatSync(curPath).isDirectory()) {
        fs.rmSync(curPath, { recursive: true, force: true });
      } else {
        fs.unlinkSync(curPath);
      }
    }
  }
}

try {
  console.log('Clearing old static files in Spring Boot static/ resources directory...');
  cleanDirectory(destDir);
  
  console.log('Copying built assets from dist to Spring Boot static/ resources...');
  if (fs.existsSync(srcDir)) {
    const files = fs.readdirSync(srcDir);
    files.forEach(file => {
      const srcPath = path.join(srcDir, file);
      const destPath = path.join(destDir, file);
      if (fs.lstatSync(srcPath).isDirectory()) {
        fs.mkdirSync(destPath, { recursive: true });
        const subfiles = fs.readdirSync(srcPath);
        subfiles.forEach(sub => {
          const subSrc = path.join(srcPath, sub);
          const subDest = path.join(destPath, sub);
          if (fs.lstatSync(subSrc).isDirectory()) {
            copyFolderRecursiveSync(subSrc, destPath);
          } else {
            fs.copyFileSync(subSrc, subDest);
          }
        });
      } else {
        fs.copyFileSync(srcPath, destPath);
      }
    });
    console.log('Assets successfully copied!');
  } else {
    console.error('Error: dist directory does not exist. Run npm run build first.');
  }
} catch (error) {
  console.error('Error during assets copying:', error);
  process.exit(1);
}
