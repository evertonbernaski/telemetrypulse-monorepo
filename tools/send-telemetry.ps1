param(
  [string]$BaseUrl = "http://localhost:8080",
  [int]$Count = 20
)

$vehicleIds = @("EV-001", "EV-002", "EV-003", "EV-004")

for ($i = 0; $i -lt $Count; $i++) {
  $vehicleId = Get-Random -InputObject $vehicleIds
  $body = @{
    vehicleId = $vehicleId
    batteryLevel = Get-Random -Minimum 8 -Maximum 95
    speed = Get-Random -Minimum 0 -Maximum 145
    motorTemperature = Get-Random -Minimum 45 -Maximum 95
    latitude = -23.5505 + ((Get-Random -Minimum -1000 -Maximum 1000) / 100000)
    longitude = -46.6333 + ((Get-Random -Minimum -1000 -Maximum 1000) / 100000)
    occurredAt = (Get-Date).ToUniversalTime().ToString("o")
  } | ConvertTo-Json

  Invoke-RestMethod -Method Post -Uri "$BaseUrl/api/telemetry" -Body $body -ContentType "application/json" | Out-Null
  Start-Sleep -Milliseconds 700
}
