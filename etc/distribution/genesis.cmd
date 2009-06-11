@echo off

rem #
rem # genesis       A simple wrapper for ant.
rem #
rem # description:  Genesis is an environment provisioning tool capable  \  
rem #               of configuring the entire enterprise stack.
rem #
rem # author:       Hussein Badakhchani


SETLOCAL

IF NOT "%1" == "" ( goto :RUN )

:USAGE
    rem # Because the definegenesisarchetypes task runs out of a target it \
    rem # always executes and prints a warning, by running the usage task  \
    rem # and providing a valid enterprise argument it stops the warning.
    ant  usage -Denterprise=MyEnterprise
    goto :END	



:RUN
    echo %1 %2 %3 %4 %5 %6 %7
    ant -Denterprise=%1  -Ddatacentre=%2 -Drack=%3 -Dnode=%4 -Dservice=%5 -Denvironment=%6 -Dapplication=%7 %7:build

:END

ENDLOCAL