@echo off
echo Starting StockPulse Backend...
echo ========================================
echo Make sure you have Java 17+ installed
echo ========================================
cd backend
call mvnw.cmd spring-boot:run
pause