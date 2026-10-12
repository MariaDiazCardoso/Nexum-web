# Arranca el backend de Nexum en local usando la base de datos de Neon.
# No guarda claves en ningun archivo: te las pide al ejecutarlo.
# Uso (desde la carpeta Nexum-web):
#   powershell -ExecutionPolicy Bypass -File .\arrancar-local.ps1

$ErrorActionPreference = "Stop"

function Leer-Clave($mensaje) {
    # Entrada visible a proposito: evita errores al teclear sin ver. Se quitan espacios sobrantes.
    $valor = (Read-Host $mensaje).Trim()
    Write-Host ("  -> " + $valor.Length + " caracteres recibidos") -ForegroundColor DarkGray
    return $valor
}

if (-not (Test-Path ".\mvnw.cmd")) {
    Write-Host "Ejecuta este script dentro de la carpeta Nexum-web (donde esta mvnw.cmd)." -ForegroundColor Red
    exit 1
}

# 1. Buscar un JDK (con javac). Se prefiere Adoptium/Temurin (Java 21) si existe.
$candidatos = @()
if ($env:JAVA_HOME) { $candidatos += $env:JAVA_HOME }
foreach ($raiz in @("C:\Program Files\Eclipse Adoptium", "C:\Program Files\Java", "C:\Program Files\Microsoft")) {
    if (Test-Path $raiz) {
        $candidatos += (Get-ChildItem $raiz -Directory |
            Where-Object { $_.Name -like "jdk*" } |
            Sort-Object Name -Descending |
            ForEach-Object { $_.FullName })
    }
}
$jdk = $candidatos | Where-Object { $_ -and (Test-Path (Join-Path $_ "bin\javac.exe")) } | Select-Object -First 1

if (-not $jdk) {
    Write-Host "No encontre un JDK. Instala Java 21 con:" -ForegroundColor Red
    Write-Host "  winget install EclipseAdoptium.Temurin.21.JDK"
    Write-Host "Luego cierra y abre PowerShell y ejecuta este script otra vez."
    exit 1
}
$env:JAVA_HOME = $jdk
Write-Host "Usando JDK: $jdk" -ForegroundColor Green

# 2. Base de datos (Neon)
$env:DB_URL  = "jdbc:postgresql://ep-jolly-block-b483h7ic.c-6.us-east-2.aws.neon.tech/neondb?sslmode=require"
$env:DB_USER = "neondb_owner"
$env:DB_PASSWORD = Leer-Clave "Clave de la base de datos Neon (16 caracteres, empieza por npg_)"

# 3. Administrador inicial
$env:ADMIN_EMAIL = "yoshitomochizaki0@gmail.com"
$env:ADMIN_PASSWORD = Leer-Clave "Clave que quieres para tu cuenta de administrador"

# 4. Clave JWT nueva en cada ejecucion
$bytes = New-Object byte[] 32
(New-Object Security.Cryptography.RNGCryptoServiceProvider).GetBytes($bytes)
$env:JWT_SECRET = [Convert]::ToBase64String($bytes)

# 5. Arrancar el backend
Write-Host "Arrancando el backend en http://localhost:8081 (Ctrl+C para detenerlo)..." -ForegroundColor Green
.\mvnw.cmd spring-boot:run