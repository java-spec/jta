let GRADLE="!clear && printf '\ec' && ./gradlew test --console rich"

map <C-F10> <Esc>:wa<CR>:execute GRADLE<CR>
map <S-F10> <Esc>:wa<CR>:execute GRADLE." --tests ".g:tests<CR>
map <C-S-F10> <Esc>:let g:tests="*.".expand("%:t:r")<CR><S-F10>
