package serialization

class SerializationException(message: String? = null, cause: Throwable? = null) : Throwable(message, cause)
class DeserializationException(message: String? = null, cause: Throwable? = null) : Throwable(message, cause)
