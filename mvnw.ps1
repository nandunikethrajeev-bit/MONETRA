$currentDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$env:JAVA_HOME = "$currentDir\.tools\jdk"
$env:M2_HOME = "$currentDir\.tools\mvn"
$env:PATH = "$env:JAVA_HOME\bin;$env:M2_HOME\bin;$env:PATH"
& "$env:M2_HOME\bin\mvn.cmd" @args

