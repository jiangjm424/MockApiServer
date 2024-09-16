后台 api mock 数据
> 我们在开发应用的时候，经常有需求需要与后台对接，那么在这种情况下，app端需要的数据直接依赖于后台，但是这个时候后台也没有开发
> 完成，所以我们急需的到一种方式来进行数据的mock，常用的我们会在开发代码的过程中，直接将数据写死在代码中，等app开发完成后提交
> 代码时将其删除；还有的公司可能是与后台对接后，后台会提供一个mock数据的平台。
> 这两种方式都有各自的缺点，第一种代码侵入式太高，容易导致代码提交时出现一些低级错误，而第二种方式则容易受后台限制，而且返回
> 的数据随机性也可能影响app的调试结果,比如我们要的是一个时间的字符串，那么后台返回字符的随机值不一定会是时间戳。


好了前面的背景说完了，那么接下来就出现这个库，解决了上面我们存在的问题。直接在本地可以进行数据的mock调试，并且不对我们的代码进行任何的侵入

使用方式：
1 引入依赖 io.github.jiangjm424:mock-server-api:+

```kts
debugImplementation("io.github.jiangjm424:mock-server-api:+")
```

2 将retrofit中使用的域名修改为（可以通过buildType来区分）： https://localhost:6878， 即：

```kotlin
Retrofit.Builder().baseUrl("https://localhost:6878").build()
```

3 建立我们后台的请求接口

```kotlin
interface DemoApi {
    @POST("com/jm/test")
    suspend fun test(@Body req: MockReq): MockResp

    @POST("com/jm/test2")
    suspend fun test2(@Body req: MockReq): MockResp
}
```

4 根据接口名，生成对应请求的类，比如我们实现一个demoApi.test2的方法,
这里注意几个点：

- **注意要继承，IMockResponse**
- 每个接口对应一个类，并且类名是按路径名生成，包名使用小写，类名第一个字母是大写

```kotlin
//路径为：com/jm/test2
package com.jm

import jm.droid.lib.mock.server.core.IMockResponse
import jm.droid.lib.mock.server.core.MockResponseWrapper
import jm.droid.lib.mock.server.core.toBuffer
import jm.droid.lib.mock.server.corpus.ICorpus
import jm.droid.sample.mockserver.dto.MockResp
import jm.droid.sample.mockserver.dto.TestData
import okio.Buffer

class Test2 : IMockResponse {
    override fun generateResponse(params: Buffer, iCorpus: ICorpus): MockResponseWrapper? {
        val res = MockResp(
            ret = 1,
            errMsg = "ok",
            data = TestData(
                icon = iCorpus.icon(),
                nick = iCorpus.nickName(),
                des = iCorpus.describe()
            )
        )
        return MockResponseWrapper(buffer = res.toBuffer())
    }
}
```

5 验收，最后我们在app调用并且得到回包：

```kotlin

private val api by lazy {
    NetApiHelper().cc(DemoApi::class.java)
}
fun test() {
    viewModelScope.launch {
        try {
            val bb = api.test2(MockReq("id-mock-req"))
            Log.i("jiang", bb.toString())
        } catch (e: Exception) {
            Log.i("jiang", "bbb")
            e.printStackTrace()
        }
    }

}

```

好了，以上就是我们这个库的简单接入方式了，是不是比之前的两种方式方便很多呢，哈哈。

当然这里我们发现是不是对app的代码有侵入，这个不能忍啊，那么解决方法呢这里也提供了两个
1、 在java目录下新建一个debug目录，将我们的mock数据代码写到这个目录下，这样正式打包时也是不影响的
2、 新建一个module专门来生成我们的mock数据，app模块下使用 debugImplementation 的方式依赖该模块

以上两种方式个人认为都可以，但是还是建议使用方式2会好一点。

好了，这里面我们简单的使用方法已经讲完了，但是可能眼尖的同学会发 在生成mock数据的接口中有一个类：ICorpus，他是干啥用的呢，
这个是个好问题，简单来说，我在这个库里预定义了一些数据，可以方方便大家直接调用啊。另外，也提供了自定义的方式，可以通过以下步骤实现，
1、实现 ICorpusSets， 可以参考：DefaultCorpusSets
```kotlin

class CustomCorpusSet:ICorpusSets {
    override fun nickNames(): List<String>? {
        return listOf("")
    }

    override fun icons(): List<String>? {
        TODO("Not yet implemented")
    }

    override fun images(): List<String>? {
        TODO("Not yet implemented")
    }

    override fun titles(): List<String>? {
        TODO("Not yet implemented")
    }

    override fun tags(): List<String>? {
        TODO("Not yet implemented")
    }

    override fun codes(): List<Int>? {
        TODO("Not yet implemented")
    }

    override fun describes(): List<String>? {
        TODO("Not yet implemented")
    }
}
```

2、将该类写到 AndroidManifest.xml  ， 其中name=mock.corpus是固定的
```xml
        <provider
            android:name="jm.droid.lib.mock.server.MockInitProvider"
            android:authorities="${applicationId}.MockInitProvider"
            android:enabled="true"
            android:exported="false"
            tools:node="merge">
            <meta-data
                android:name="mock.corpus"
                android:value="CustomCorpusSet的全路径名" />
        </provider>
```

这样就可以优先使用自己定义的字库了，可能还有小伙伴感觉里面的内容不够用，那么这个时候呢，还有一种方法，那就是直接在**IMockResponse**使用自己的方式来返回对应的mock数据了

## 1.0.3版本加入支持部分接口mock能力
以上方式接入时，会将域名替换成mock服务器，这个在项目从0-1的阶段是非常友好的，但是好多小伙伴遇难的情况是我现在项目已经跑了N个版本了，难道我接入时需要将之前的接口全部再写一下吗？这个太难了吧。。。。
针对这个场景，103版本中加入了针对指定接口进行mock的能力，具体的的步骤如下：
1. 将demo app 中的类MockServerTool加入到工程的网络模块中
```kotlin
object MockServerTool {
    private const val class_name = "jm.droid.lib.mock.server.MockServerDebug"
    private val supported = try {
        Class.forName(class_name)
        true
    } catch (ex: Exception) {
        false
    }

    fun config(builder: OkHttpClient.Builder) = builder.takeIf { supported }?.apply {
        try {
            val clazz = Class.forName(class_name)
            val configOkHttpClient =
                clazz.getMethod("configOkHttpClient", OkHttpClient.Builder::class.java).apply {
                    isAccessible = true
                }
            val mockServerDebug = clazz.newInstance()
            configOkHttpClient.invoke(mockServerDebug, builder)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
```
2. 对OkHttpClient进行配置
```kotlin
private val httpClient by lazy {
        val builder = OkHttpClient.Builder()
            ...
        MockServerTool.config(builder)
            ...
        builder.build()
    }
```
3. 在指定接口中加上header
```kotlin
    @POST("com/jm/test2")
    @Headers(
        value = [
            "mock:true"
        ]
    )
    suspend fun test2(@Body req: MockReq): MockResp
```
经验以上几步后，该方法即会将域名切换成本地的mock服务中
