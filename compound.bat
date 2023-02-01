@echo off

.\gradlew mpack
.\gradlew :core:jar

rem recompile to fix bugs with assets

.\gradlew mpack
.\gradlew :core:jar