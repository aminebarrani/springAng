# Java Setup Script
Write-Host "Java Environment Setup Script" -ForegroundColor Green
Write-Host "=============================" -ForegroundColor Green

# Check if Java is installed
Write-Host "`nChecking for Java installation..." -ForegroundColor Yellow

$javaPaths = @(
    "C:\Program Files\Java\jdk-17*",
    "C:\Program Files\Java\jre-17*",
    "C:\Program Files\Eclipse Adoptium\jdk-17*",
    "C:\Program Files\Microsoft\jdk-17*"
)

$javaHome = $null
foreach ($path in $javaPaths) {
    $found = Get-ChildItem $path -ErrorAction SilentlyContinue | Select-Object -First 1
    if ($found) {
        $javaHome = $found.FullName
        Write-Host "Found Java at: $javaHome" -ForegroundColor Green
        break
    }
}

if (-not $javaHome) {
    Write-Host "Java not found in common locations!" -ForegroundColor Red
    Write-Host "Please install Java 17 first:" -ForegroundColor Yellow
    Write-Host "1. Go to https://adoptium.net/" -ForegroundColor Cyan
    Write-Host "2. Download Eclipse Temurin JDK 17 for Windows" -ForegroundColor Cyan
    Write-Host "3. Run the installer" -ForegroundColor Cyan
    Write-Host "4. Run this script again" -ForegroundColor Cyan
    exit
}

# Set JAVA_HOME for current session
$env:JAVA_HOME = $javaHome
Write-Host "`nSet JAVA_HOME for current session: $env:JAVA_HOME" -ForegroundColor Green

# Add Java to PATH for current session
$env:PATH = "$javaHome\bin;$env:PATH"
Write-Host "Added Java to PATH for current session" -ForegroundColor Green

# Test Java
Write-Host "`nTesting Java installation..." -ForegroundColor Yellow
try {
    $javaVersion = java -version 2>&1
    Write-Host "Java version:" -ForegroundColor Green
    Write-Host $javaVersion -ForegroundColor Cyan
} catch {
    Write-Host "Java test failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Instructions for permanent setup
Write-Host "`nTo make these changes permanent:" -ForegroundColor Yellow
Write-Host "1. Open System Properties (Win + R, type 'sysdm.cpl')" -ForegroundColor Cyan
Write-Host "2. Click 'Environment Variables'" -ForegroundColor Cyan
Write-Host "3. Under 'System Variables', click 'New'" -ForegroundColor Cyan
Write-Host "4. Variable name: JAVA_HOME" -ForegroundColor Cyan
Write-Host "5. Variable value: $javaHome" -ForegroundColor Cyan
Write-Host "6. Add '%JAVA_HOME%\bin' to your PATH variable" -ForegroundColor Cyan

Write-Host "`nSetup complete! You can now run: ./mvnw spring-boot:run" -ForegroundColor Green 