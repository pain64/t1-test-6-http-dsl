import io.kotest.core.spec.style.FunSpec
import java.io.OutputStream

class Test : FunSpec({
    val server = InMemoryHttpServer {
        // Just for test
        port = 3333

        get("/") {
            StringResponse("hello!")
        }
        post("/echo") { req ->
            // Write input to output prepending all headers in format `name` -> `value`
            object : Response {
                override val statusCode = 200
                override val headers = mapOf<String, String>()

                override fun write(out: OutputStream) {
                    for ((k, v) in req.headers)
                        out.write("$k -> $v\n".encodeToByteArray())

                    req.inputStream.transferTo(out)
                }
            }
        }
    }

    test("test simple get request") {
        TODO()
    }

    test("test route not found") {
        TODO()
    }

    test("test echo request") {
        TODO()
    }
})