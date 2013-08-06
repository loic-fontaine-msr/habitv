set BUILDER_DIR=D:\workspaces\habiTv2\builder
set DEPLOY_DIR=C:\tools\habiTv

del /Q %DEPLOY_DIR%\lib\*
del /Q %DEPLOY_DIR%\downloader\*
del /Q %DEPLOY_DIR%\exporter\*
del /Q %DEPLOY_DIR%\provider\*

xcopy /Y %BUILDER_DIR%\lib\* %DEPLOY_DIR%\lib
xcopy /Y %BUILDER_DIR%\pub\lib\* %DEPLOY_DIR%\lib
xcopy /Y %BUILDER_DIR%\pub\downloader\* %DEPLOY_DIR%\downloader
xcopy /Y %BUILDER_DIR%\pub\exporter\* %DEPLOY_DIR%\exporter
xcopy /Y %BUILDER_DIR%\pub\provider\* %DEPLOY_DIR%\provider

pause