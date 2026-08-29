import os
import zipfile

def package_repo():
    zip_path = "ecommerce-marketplace.zip"
    if os.path.exists(zip_path):
        os.remove(zip_path)

    print("Creating ecommerce-marketplace.zip including .git directory...")
    
    excluded_dirs = {
        os.path.normpath("node_modules"),
        os.path.normpath("frontend/node_modules"),
        os.path.normpath("frontend/dist"),
        os.path.normpath("backend/target"),
        os.path.normpath(".system_generated"),
        os.path.normpath("dist"),
        os.path.normpath("target")
    }

    with zipfile.ZipFile(zip_path, "w", zipfile.ZIP_DEFLATED) as zipf:
        for root, dirs, files in os.walk("."):
            # Check exclusions
            rel_root = os.path.relpath(root, ".")
            norm_rel_root = os.path.normpath(rel_root)
            
            # Prune excluded directories
            skip = False
            for excl in excluded_dirs:
                if norm_rel_root == excl or norm_rel_root.startswith(excl + os.sep):
                    skip = True
                    break
            if skip:
                continue

            # Skip the zip file itself if in root
            for file in files:
                if file == "ecommerce-marketplace.zip" and rel_root == ".":
                    continue
                file_path = os.path.join(root, file)
                rel_file = os.path.relpath(file_path, ".")
                zipf.write(file_path, rel_file)

    size_mb = os.path.getsize(zip_path) / (1024 * 1024)
    print(f"ecommerce-marketplace.zip created successfully! File size: {size_mb:.2f} MB")

if __name__ == "__main__":
    package_repo()
