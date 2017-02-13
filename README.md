# Sofia2 Android API

*Read this in other languages: [English](README.md), [Spanish](README.es.md).*

## Copyright notice

© 2013-15 Indra Sistemas S.A.

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.

## API documentation

Before using the SSAP API for the first time, we strongly recommend that you learn the main concepts of the Sofia2 platform. They have been included in the Sofia2 developer documentation, which can be downloaded from http://sofia2.com/desarrollador_en.html.

## Build instructions

The Android API is distributed as a maven project. Its artifact can be built and installed in the local maven repository using the following commands:

```
cd <root of your copy of the repository>
mvn clean install [-Dmaven.test.skip]
```

## Android Studio dependencies

Upon build, place the resulting .jar under the libs directory of your Android project. Then you need to update your build.gradle file inside the app directory with these lines:
```
  repositories {
      mavenCentral()
  }
  dependencies {
      compile 'org.apache.commons:commons-lang3:3.5'
      compile 'org.fusesource.hawtbuf:hawtbuf:1.11'
      compile 'org.fusesource.hawtdispatch:hawtdispatch:1.22'
      compile 'org.fusesource.hawtdispatch:hawtdispatch-transport:1.22'
      compile 'org.fusesource.mqtt-client:mqtt-client:1.14'
      compile 'com.fasterxml.jackson.jaxrs:jackson-jaxrs-json-provider:2.8.6'
      compile 'com.fasterxml.jackson.core:jackson-core:2.8.6'
      compile 'com.fasterxml.jackson.core:jackson-databind:2.8.6'
      compile 'com.fasterxml.jackson.core:jackson-annotations:2.8.6'
      compile files('libs/ssap-android-1.1.0.jar')
  }
```
## Contact information

If you need support from us, please feel free to contact us at [plataformasofia2@indra.es](mailto:plataformasofia2@indra.es) or at www.sofia2.com. 

And if you want to contribute, send us a pull request.
