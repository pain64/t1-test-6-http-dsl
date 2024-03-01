import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream

@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE)
@DslMarker
annotation class HttpDsl

enum class HttpMethod { Get, Post }

interface Request {
    val method: HttpMethod
    val path: String
    val headers: Map<String, String>
    val inputStream: InputStream
}

interface Response {
    val statusCode: Int
    val headers: Map<String, String>
    fun write(out: OutputStream)
}

@HttpDsl
interface HandlerScope {
    object Nil : HandlerScope
}

data class HttpHandler(
    val method: HttpMethod, val path: String, val op: HandlerScope.(Request) -> Response
)

@HttpDsl
class HttpServerConfig(
    var port: Int = 8080,
    val handlers: MutableList<HttpHandler> = mutableListOf()
) {
    fun get(path: String, op: HandlerScope.(Request) -> Response) {
        handlers.add(HttpHandler(HttpMethod.Get, path, op))
    }

    fun post(path: String, op: HandlerScope.(Request) -> Response) {
        handlers.add(HttpHandler(HttpMethod.Post, path, op))
    }
}

fun findHandler(request: Request, handlers: List<HttpHandler>): HttpHandler? =
    handlers.find { it.method == request.method && request.path.endsWith(it.path) }

class StringResponse(
    private val s: String,
    override val statusCode: Int = 200,
    override val headers: Map<String, String> = mapOf()
) : Response {
    override fun write(out: OutputStream) {
        out.write(s.encodeToByteArray())
    }
}

data class Data(val bytes: List<Byte>) {
    override fun toString(): String = TODO()
}

val String.asData
    get() = Data(this.encodeToByteArray().toList())
val ByteArray.asData
    get() = Data(this.toList())

data class InMemoryRequest(
    override val method: HttpMethod,
    override val path: String,
    override val headers: Map<String, String>,
    val data: Data,
) : Request {
    override val inputStream: InputStream
        get() = ByteArrayInputStream(data.bytes.toByteArray())
}

data class InMemoryResponse(
    val statusCode: Int,
    val headers: Map<String, String>,
    val data: Data
)

class InMemoryHttpServer(
    configBuilder: HttpServerConfig.() -> Unit
) {

    private val config = HttpServerConfig().also {
        it.configBuilder()
    }

    fun handle(request: InMemoryRequest): InMemoryResponse {
        TODO()
    }
}