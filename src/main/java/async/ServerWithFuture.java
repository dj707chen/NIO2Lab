package async;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @see <a href="https://www.baeldung.com/java-nio2-async-socket-channel">A Guide to NIO2 Asynchronous Socket Channel</a>
 *
 * @see <a href="https://www.baeldung.com/java-nio2-async-socket-channel#the-server-with-future">The Server With Future</a>
 * */
public class ServerWithFuture {
    public static void main(String[] args) throws Exception {
        new ServerWithFuture().run();
    }

    void run() throws Exception {
        System.out.println("Hello, NIO2 with Future!");

        // An instance of AsynchronousServerSocketChannel is created by calling the static open API on its class:
        AsynchronousServerSocketChannel server = AsynchronousServerSocketChannel.open();

        // A newly created asynchronous server socket channel is open but not yet bound,
        // so we must bind it to a local address and optionally choose a port:
        server.bind(new InetSocketAddress("127.0.0.1", 4555));

        // Once bound, the accept API is used to initiate the accepting of connections to the channel's socket:
        // As it is with asynchronous channel operations, the above call returns right away and execution continues.
        Future<AsynchronousSocketChannel> acceptFuture = server.accept();

        //Next, we can use the get API to query for a response from the Future object:
        AsynchronousSocketChannel worker = acceptFuture.get();

        runServer(worker);
    }

    public void runServer(AsynchronousSocketChannel clientChannel) throws IOException, ExecutionException, InterruptedException {
        if ((clientChannel != null) && (clientChannel.isOpen())) {
            try (clientChannel) {
                while (true) {
                    ByteBuffer buffer1 = ByteBuffer.allocate(32);
                    Future<Integer> readResult = clientChannel.read(buffer1);

                    // perform other computations

                    readResult.get();

                    buffer1.flip();
                    Future<Integer> writeResult1 = clientChannel.write(buffer1);

                    // perform other computations

                    writeResult1.get();
                    buffer1.clear();

                    ByteBuffer buffer2 = ByteBuffer.allocate(128);
                    buffer2.put(0, ", Server: ServerWithFurure".getBytes());
                    Future<Integer> writeResult2 = clientChannel.write(buffer2);

                    // perform other computations

                    writeResult2.get();
                    buffer2.clear();
                }
            }
        }
    }
}

/* Test using curl
Client console
--------------
curl localhost:4555
curl: (1) Received HTTP/0.9 when not allowed

Sever console
-------------
 /Users/dj.chen/Library/Java/JavaVirtualMachines/temurin-17.0.1/Contents/Home/bin/java --enable-preview -javaagent:/Applications/IntelliJ IDEA CE.app/Contents/lib/idea_rt.jar=58083:/Applications/IntelliJ IDEA CE.app/Contents/bin -Dfile.encoding=UTF-8 -classpath /Users/dj.chen/repoMy/NIO2Lab/target/classes async.AyncWithFuture
Hello, NIO2 with Future !
Exception in thread "main" java.util.concurrent.ExecutionException: java.io.IOException: Broken pipe
	at java.base/sun.nio.ch.CompletedFuture.get(CompletedFuture.java:69)
	at async.AyncWithFuture.runServer(AyncWithFuture.java:56)
	at async.AyncWithFuture.run(AyncWithFuture.java:37)
	at async.AyncWithFuture.main(AyncWithFuture.java:17)
Caused by: java.io.IOException: Broken pipe
	at java.base/sun.nio.ch.FileDispatcherImpl.write0(Native Method)
	at java.base/sun.nio.ch.SocketDispatcher.write(SocketDispatcher.java:62)
	at java.base/sun.nio.ch.IOUtil.writeFromNativeBuffer(IOUtil.java:132)
	at java.base/sun.nio.ch.IOUtil.write(IOUtil.java:97)
	at java.base/sun.nio.ch.IOUtil.write(IOUtil.java:60)
	at java.base/sun.nio.ch.UnixAsynchronousSocketChannelImpl.implWrite(UnixAsynchronousSocketChannelImpl.java:713)
	at java.base/sun.nio.ch.AsynchronousSocketChannelImpl.write(AsynchronousSocketChannelImpl.java:382)
	at java.base/sun.nio.ch.AsynchronousSocketChannelImpl.write(AsynchronousSocketChannelImpl.java:387)
	at async.AyncWithFuture.runServer(AyncWithFuture.java:52)
	... 2 more

Process finished with exit code 1
 */