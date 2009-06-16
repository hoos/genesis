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
    rem # echo %1 %2 %3 %4 %5 %6 %7 %8 %9 %10 %11 %12 %13 %14 %15 %16
    ant -Denterprise=%2  -Ddatacentre=%4 -Drack=%6 -Dnode=%8 -Dservice=%10 -Denvironment=%12 -Dapplication=%14 %14:%16

:END

ENDLOCAL
