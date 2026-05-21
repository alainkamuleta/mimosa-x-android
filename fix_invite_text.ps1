$path = "libraries\ui-strings\src\main\res\values-fr\translations.xml"
$content = [System.IO.File]::ReadAllText($path, [System.Text.Encoding]::UTF8)
$content = $content.Replace('Salut, parle-moi sur %1$s : %2$s', 'Rejoins-moi sur Mimosa, la messagerie souveraine Convergence : %2$s')
[System.IO.File]::WriteAllText($path, $content, [System.Text.Encoding]::UTF8)
Write-Host "Done."
