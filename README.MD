### 常见问题
* Unable to make {member} accessible: module {A} does not 'opens {package}' to {B}
```agsl
Java 9 及之后的版本对源码进行了模块化重构，public 不再意味着完全开放。此时使用反射试图访问，会报错。
解决方法添加 JVM参数：--add-opens {A}/{package}={B}
期中 {B} 通常是运行时才能确定的hash值， 所以 JVM 参数可写成 --add-opens {A}/{package}=ALL-UNNAMED 表示开放给所有未命名实例

eg: --add-opens java.base/java.lang=ALL-UNNAMED

```