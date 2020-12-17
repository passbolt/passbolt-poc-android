fastlane documentation
================
# Installation

Make sure you have the latest version of the Xcode command line tools installed:

```
xcode-select --install
```

Install _fastlane_ using
```
[sudo] gem install fastlane -NV
```
or alternatively using `brew install fastlane`

# Available Actions
## Android
### android detekt
```
fastlane android detekt
```
Runs static analysis with detekt
### android lint_debug
```
fastlane android lint_debug
```
Runs static analysis with SDK lint
### android owasp_dependency_check
```
fastlane android owasp_dependency_check
```
Runs OWASP dependency analysis
### android unit_tests_debug
```
fastlane android unit_tests_debug
```
Runs run unit tests
### android ui_tests_debug
```
fastlane android ui_tests_debug
```
Runs run UI tests
### android build_debug
```
fastlane android build_debug
```
Build debug version
### android deploy_debug
```
fastlane android deploy_debug
```
Deploy debug version

----

This README.md is auto-generated and will be re-generated every time [fastlane](https://fastlane.tools) is run.
More information about fastlane can be found on [fastlane.tools](https://fastlane.tools).
The documentation of fastlane can be found on [docs.fastlane.tools](https://docs.fastlane.tools).
