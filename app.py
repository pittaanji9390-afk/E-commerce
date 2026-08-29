"""
Enterprise Multi-Vendor E-Commerce Platform - Entry Point & Health Monitor
"""
import os
import sys
import subprocess

def main():
    print("====================================================")
    print(" Enterprise Multi-Vendor E-Commerce Platform v1.0.0 ")
    print("====================================================")
    print("Available components:")
    print(" 1. Backend: Spring Boot 3.4.2 Java 21 REST API (/api/v1)")
    print(" 2. Frontend: React 18 / TypeScript / Tailwind CSS Vite SPA")
    print(" 3. Database: PostgreSQL 16 + Redis 7 Caching")
    print(" 4. Documentation: Swagger UI (/swagger-ui.html)")
    print("\nTo start all services with Docker:")
    print("   docker-compose up -d")
    print("\nTo run tests:")
    print("   npm test")
    print("====================================================")

if __name__ == "__main__":
    main()
