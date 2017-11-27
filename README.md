# Shopping
河马速购，一款使用组件式开发及MVP架构的导购类APP
## 预览：
![Shopping](screen_record/Shopping.gif)

## 参考:
- [AndroidComponent](https://github.com/luojilab/DDComponentForAndroid) 一个组件式开发框架，在原有基础上修改了路由方法，简化了组件注册方法。
- [MVPArt](https://github.com/JessYanCoding/MVPArt) MVP轻量级框架，在原有基础上引入官方的ViewModel组件，替换掉了callback的交互方式，
同时使用反射及模板的方式简化了ViewModel(V)与Repository(M)之间的关联动作。
- [StackLayout](https://github.com/fashare2015/StackLayout) 卡片堆叠动画实现，在原有基础上做了较大改动，只保留了view堆叠及加载部分，加入了上下滑动动画

## 配置:
* Android 4.4+ (API 19)
* 使用时需要将./app/build.gradle下的签名配置替换为你自己的
```gradle
   signingConfigs {
        release {
            storeFile file('your keystore')
            keyAlias 'alias'
            keyPassword '******'
            storePassword '******'
        }
    }
```
* [App下载链接](http://www.homabuy.com/install/homabuy.apk)
## About me
[xinayida](http://www.jianshu.com/u/d5568bd64017)
## License

   Copyright 2017  xinayida

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
