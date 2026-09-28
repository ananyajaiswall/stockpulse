@echo off
echo ========================================
echo StockPulse — AI Inventory & Dynamic Pricing Advisor
echo ========================================
echo.
echo This script will help you run both the backend and frontend.
echo.
echo PREREQUISITES:
echo - Java 17+ installed
echo - Maven 3.8+ installed  
echo - Node.js 16+ installed
echo.
echo INSTRUCTIONS:
echo 1. Keep this window open
echo 2. Open a new command prompt for the backend
echo 3. Open another new command prompt for the frontend
echo.
echo To start BACKEND:
echo   cd backend
echo   mvn spring-boot:run
echo.
echo To start FRONTEND:
echo   cd frontend  
echo   npm install
echo   npm run dev
echo.
echo ACCESS POINTS:
echo   Backend API: http://localhost:8080
echo   Frontend UI: http://localhost:5173
echo   H2 Console: http://localhost:8080/h2-console (JDBC URL: jdbc:h2:mem:testdb)
echo.
echo For detailed instructions, see HOW_TO_RUN.md
echo.
echo Press any key to continue...
pause >nul