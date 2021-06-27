REM fixes the variety of aliases I've had over the course of this project
REM Doesn't work in most GUIs; I highly recommend gitextensions though which does
git config --local log.mailmap true
REM set up githook to prevent commits with the text "no commit" (with no space) in a source file
git config --local core.hooksPath .githooks/
