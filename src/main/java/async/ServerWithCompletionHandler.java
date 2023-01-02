package async;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * @see <a href="https://www.baeldung.com/java-nio2-async-socket-channel">A Guide to NIO2 Asynchronous Socket Channel</a>
 * @see <a href="https://www.baeldung.com/java-nio2-async-socket-channel#the-server-with-completionhandler">The Server With CompletionHandler</a>
 */
public class ServerWithCompletionHandler {
    public static void main(String[] args) throws Exception {
        new ServerWithCompletionHandler().run();
    }

    void run() throws Exception {
        System.out.println("Hello, NIO2 async with CompletionHandler!");

        // An instance of AsynchronousServerSocketChannel is created by calling the static open API on its class:
        AsynchronousServerSocketChannel serverChannel = AsynchronousServerSocketChannel.open();

        // A newly created asynchronous server socket channel is open but not yet bound,
        // so we must bind it to a local address and optionally choose a port:
        serverChannel.bind(new InetSocketAddress("127.0.0.1", 4555));

        // Next, still inside the constructor, we create a while loop within which we accept any incoming connection from a client.
        // This while loop is used strictly to prevent the server from exiting before establishing a connection with a client.
        // To prevent the loop from running endlessly, we call System.in.read() at its end to block execution until an
        // incoming connection is read from the standard input stream:
        while (true) {
            serverChannel.accept(null, new CompletionHandler<AsynchronousSocketChannel, Object>() {

                @Override
                public void completed(AsynchronousSocketChannel result, Object attachment) {
                    if (serverChannel.isOpen()) {
                        serverChannel.accept(null, this);
                    }

                    AsynchronousSocketChannel clientChannel = result;
                    if ((clientChannel != null) && (clientChannel.isOpen())) {
                        ReadWriteHandler handler = new ReadWriteHandler(clientChannel);
                        ByteBuffer buffer = ByteBuffer.allocate(32);

                        Map<String, Object> readInfo = new HashMap<>();
                        readInfo.put("action", "read");
                        readInfo.put("buffer", buffer);

                        clientChannel.read(buffer, readInfo, handler);
                    }
                }

                @Override
                public void failed(Throwable exc, Object attachment) {
                    System.err.println("Accepting clientChannel failed");
                }
            });
            System.in.read();
        }

    }
}

class ReadWriteHandler implements CompletionHandler<Integer, Map<String, Object>> {
    private AsynchronousSocketChannel clientChannel;

    public ReadWriteHandler(AsynchronousSocketChannel clientChannel) {
        this.clientChannel = clientChannel;
    }

    @Override
    public void completed(Integer result, Map<String, Object> attachment) {
        Map<String, Object> actionInfo = attachment;
        String action = (String) actionInfo.get("action");

        if ("read".equals(action)) {
            ByteBuffer buffer = (ByteBuffer) actionInfo.get("buffer");
            buffer.flip();
            actionInfo.put("action", "write");

            clientChannel.write(buffer, actionInfo, this);
            buffer.clear();

            ByteBuffer buffer2 = ByteBuffer.allocate(128);
            buffer2.put(0, ", Server: ServerWithFurure".getBytes());
            clientChannel.write(buffer2, actionInfo, this);

        } else if ("write".equals(action)) {
            ByteBuffer buffer = ByteBuffer.allocate(32);

            actionInfo.put("action", "read");
            actionInfo.put("buffer", buffer);

            clientChannel.read(buffer, actionInfo, this); // <----- error here
        }
    }

    @Override
    public void failed(Throwable exc, Map<String, Object> attachment) {
        System.err.println("Reading clientChannel failed");
        exc.printStackTrace();
    }
}

/* Test using curl
Client console
--------------
curl localhost:4999
curl: (1) Received HTTP/0.9 when not allowed

Sever console
-------------
/Users/dj.chen/Library/Java/JavaVirtualMachines/temurin-17.0.1/Contents/Home/bin/java --enable-preview -javaagent:/Applications/IntelliJ IDEA CE.app/Contents/lib/idea_rt.jar=58069:/Applications/IntelliJ IDEA CE.app/Contents/bin -Dfile.encoding=UTF-8 -classpath /Users/dj.chen/repoMy/NIO2Lab/target/classes async.AyncWithCompletionHandler
Hello, NIO2 async with CompletionHandler !
Reading clientChannel failed
java.io.IOException: Broken pipe
	at java.base/sun.nio.ch.FileDispatcherImpl.write0(Native Method)
	at java.base/sun.nio.ch.SocketDispatcher.write(SocketDispatcher.java:62)
	at java.base/sun.nio.ch.IOUtil.writeFromNativeBuffer(IOUtil.java:132)
	at java.base/sun.nio.ch.IOUtil.write(IOUtil.java:97)
	at java.base/sun.nio.ch.IOUtil.write(IOUtil.java:60)
	at java.base/sun.nio.ch.UnixAsynchronousSocketChannelImpl.implWrite(UnixAsynchronousSocketChannelImpl.java:713)
	at java.base/sun.nio.ch.AsynchronousSocketChannelImpl.write(AsynchronousSocketChannelImpl.java:382)
	at java.base/sun.nio.ch.AsynchronousSocketChannelImpl.write(AsynchronousSocketChannelImpl.java:399)
	at java.base/java.nio.channels.AsynchronousSocketChannel.write(AsynchronousSocketChannel.java:582)
	at async.ReadWriteHandler.completed(AyncWithCompletionHandler.java:84)
	at async.ReadWriteHandler.completed(AyncWithCompletionHandler.java:67)
	at java.base/sun.nio.ch.Invoker.invokeUnchecked(Invoker.java:129)
	at java.base/sun.nio.ch.Invoker.invokeDirect(Invoker.java:160)
	at java.base/sun.nio.ch.UnixAsynchronousSocketChannelImpl.implRead(UnixAsynchronousSocketChannelImpl.java:573)
	at java.base/sun.nio.ch.AsynchronousSocketChannelImpl.read(AsynchronousSocketChannelImpl.java:276)
	at java.base/sun.nio.ch.AsynchronousSocketChannelImpl.read(AsynchronousSocketChannelImpl.java:297)
	at java.base/java.nio.channels.AsynchronousSocketChannel.read(AsynchronousSocketChannel.java:425)

	at async.ReadWriteHandler.completed(AyncWithCompletionHandler.java:93)
	at async.ReadWriteHandler.completed(AyncWithCompletionHandler.java:67)

	at java.base/sun.nio.ch.Invoker.invokeUnchecked(Invoker.java:129)
	at java.base/sun.nio.ch.Invoker.invokeDirect(Invoker.java:160)
	at java.base/sun.nio.ch.UnixAsynchronousSocketChannelImpl.implWrite(UnixAsynchronousSocketChannelImpl.java:759)
	at java.base/sun.nio.ch.AsynchronousSocketChannelImpl.write(AsynchronousSocketChannelImpl.java:382)
	at java.base/sun.nio.ch.AsynchronousSocketChannelImpl.write(AsynchronousSocketChannelImpl.java:399)
	at java.base/java.nio.channels.AsynchronousSocketChannel.write(AsynchronousSocketChannel.java:582)
	at async.ReadWriteHandler.completed(AyncWithCompletionHandler.java:84)
	at async.ReadWriteHandler.completed(AyncWithCompletionHandler.java:67)
	at java.base/sun.nio.ch.Invoker.invokeUnchecked(Invoker.java:129)
	at java.base/sun.nio.ch.Invoker.invokeDirect(Invoker.java:160)
	at java.base/sun.nio.ch.UnixAsynchronousSocketChannelImpl.implRead(UnixAsynchronousSocketChannelImpl.java:573)
	at java.base/sun.nio.ch.AsynchronousSocketChannelImpl.read(AsynchronousSocketChannelImpl.java:276)
	at java.base/sun.nio.ch.AsynchronousSocketChannelImpl.read(AsynchronousSocketChannelImpl.java:297)
	at java.base/java.nio.channels.AsynchronousSocketChannel.read(AsynchronousSocketChannel.java:425)
	at async.ReadWriteHandler.completed(AyncWithCompletionHandler.java:93)
	at async.ReadWriteHandler.completed(AyncWithCompletionHandler.java:67)
	at java.base/sun.nio.ch.Invoker.invokeUnchecked(Invoker.java:129)
	at java.base/sun.nio.ch.Invoker.invokeDirect(Invoker.java:160)
	at java.base/sun.nio.ch.UnixAsynchronousSocketChannelImpl.implWrite(UnixAsynchronousSocketChannelImpl.java:759)
	at java.base/sun.nio.ch.AsynchronousSocketChannelImpl.write(AsynchronousSocketChannelImpl.java:382)
	at java.base/sun.nio.ch.AsynchronousSocketChannelImpl.write(AsynchronousSocketChannelImpl.java:399)
	at java.base/java.nio.channels.AsynchronousSocketChannel.write(AsynchronousSocketChannel.java:582)
	at async.ReadWriteHandler.completed(AyncWithCompletionHandler.java:84)
	at async.ReadWriteHandler.completed(AyncWithCompletionHandler.java:67)
	at java.base/sun.nio.ch.Invoker.invokeUnchecked(Invoker.java:129)
	at java.base/sun.nio.ch.Invoker.invokeDirect(Invoker.java:160)
	at java.base/sun.nio.ch.UnixAsynchronousSocketChannelImpl.implRead(UnixAsynchronousSocketChannelImpl.java:573)
	at java.base/sun.nio.ch.AsynchronousSocketChannelImpl.read(AsynchronousSocketChannelImpl.java:276)
	at java.base/sun.nio.ch.AsynchronousSocketChannelImpl.read(AsynchronousSocketChannelImpl.java:297)
	at java.base/java.nio.channels.AsynchronousSocketChannel.read(AsynchronousSocketChannel.java:425)
	at async.AyncWithCompletionHandler$1.completed(AyncWithCompletionHandler.java:52)
	at async.AyncWithCompletionHandler$1.completed(AyncWithCompletionHandler.java:35)
	at java.base/sun.nio.ch.Invoker.invokeUnchecked(Invoker.java:129)
	at java.base/sun.nio.ch.Invoker$2.run(Invoker.java:221)
	at java.base/sun.nio.ch.AsynchronousChannelGroupImpl$1.run(AsynchronousChannelGroupImpl.java:113)
	at java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1136)
	at java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:635)
	at java.base/java.lang.Thread.run(Thread.java:833)
 */