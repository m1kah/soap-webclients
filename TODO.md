    val out = PipedOutputStream()
    val dataIn = PipedInputStream(out)
    return DataBufferUtils.write(
        it.body(BodyExtractors.toDataBuffers()),
        out)
        .doOnComplete { try { out.close() } catch (e: IOException ) {} }
        .map {dataIn}

https://stackoverflow.com/questions/46460599/how-to-correctly-read-fluxdatabuffer-and-convert-it-to-a-single-inputstream

But to subscribe no a stream inside a stream?
