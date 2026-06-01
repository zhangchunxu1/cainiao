$env:JAVA_HOME = "C:\Program Files\Java\jdk1.8.0_201"
$port = 8080

$processId = (Get-NetTCPConnection -LocalPort $port -ErrorAction SilentlyContinue).OwningProcess
if ($processId) {
    Write-Host ">>> Port $port is in use by PID $processId, killing..." -ForegroundColor Yellow
    taskkill /F /PID $processId | Out-Null
    Start-Sleep -Seconds 2
    Write-Host ">>> Port $port released" -ForegroundColor Green
} else {
    Write-Host ">>> Port $port is free, starting..." -ForegroundColor Green
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "   Starting Backend Server..." -ForegroundColor Cyan
Write-Host "   Port: $port" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

cd d:\xiangmu\cainiao\employee-system\backend
mvn spring-boot:run
