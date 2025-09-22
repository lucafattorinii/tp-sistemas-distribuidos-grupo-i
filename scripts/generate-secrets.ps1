# Generate a secure JWT secret
$jwtSecret = [Convert]::ToBase64String([Security.Cryptography.RandomNumberGenerator]::Create().GetBytes(48))

# Generate a secure password for MySQL
$mysqlRootPassword = -join ((65..90) + (97..122) + (48..57) | Get-Random -Count 32 | ForEach-Object {[char]$_})
$mysqlUserPassword = -join ((65..90) + (97..122) + (48..57) | Get-Random -Count 32 | ForEach-Object {[char]$_})

Write-Host "# Copy these values to your .env file:"
Write-Host "JWT_SECRET=$jwtSecret"
Write-Host "MYSQL_ROOT_PASSWORD=$mysqlRootPassword"
Write-Host "MYSQL_PASSWORD=$mysqlUserPassword"

# Create .env file if it doesn't exist
if (-not (Test-Path .\.env)) {
    Copy-Item .\.env.example -Destination .\.env
    Write-Host "\nCreated .env file from .env.example"
    
    # Update the .env file with generated values
    (Get-Content .\.env) | ForEach-Object {
        $_ -replace 'JWT_SECRET=.*', "JWT_SECRET=$jwtSecret" `
          -replace 'MYSQL_ROOT_PASSWORD=.*', "MYSQL_ROOT_PASSWORD=$mysqlRootPassword" `
          -replace 'MYSQL_PASSWORD=.*', "MYSQL_PASSWORD=$mysqlUserPassword"
    } | Set-Content .\.env
    
    Write-Host "Updated .env with generated secrets"
} else {
    Write-Host "\n.env file already exists. Please update it with the values above."
}
