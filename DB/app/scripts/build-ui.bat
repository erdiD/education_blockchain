cd ..\ui
REM cp core-config.ts.sample core-config.ts
REM cd ..\..\..

del ..\ilv-server-all\rest-api\src\main\webapp\app\*.* /q
rd ..\ilv-server-all\rest-api\src\main\webapp\app\assets /s /q

npm install && ng build --base-href /ilv/app/ --prod --aot && rm -Rf ..\ilv-server-all\rest-api\src\main\webapp\app\* && cp -R dist/* ..\ilv-server-all\rest-api\src\main\webapp\app && cd ..\scripts
