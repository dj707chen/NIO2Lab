package async;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @see <a href="https://www.baeldung.com/java-nio2-async-socket-channel">A Guide to NIO2 Asynchronous Socket Channel</a>
 * @see <a href="https://www.baeldung.com/java-nio2-async-socket-channel#the-client">The Client</a>
 */
public class ClientWithFuture {
    public static void main(String[] args) throws Exception {
        new ClientWithFuture().run();
    }

    void run() throws Exception {
        System.out.println("Hello, NIO2 client!");

        // An instance of AsynchronousServerSocketChannel is created by calling the static open API on its class:
        AsynchronousSocketChannel client = AsynchronousSocketChannel.open();
        InetSocketAddress hostAddress = new InetSocketAddress("localhost", 4555);
        Future<Void> future = client.connect(hostAddress);

        // do some computation

        future.get();

        String serverResp = sendMessage(client, "Hello from ClientWithFuture!");

        System.out.println("Server response: <<" + serverResp + ">>");
    }

    public String sendMessage(AsynchronousSocketChannel client, String message) throws ExecutionException, InterruptedException {
        byte[] byteMsg = new String(message).getBytes();
        ByteBuffer buffer = ByteBuffer.wrap(byteMsg);
        Future<Integer> writeResult = client.write(buffer);

        // do some computation

        writeResult.get();

        buffer.flip();
        Future<Integer> readResult = client.read(buffer);

        // do some computation

        System.out.println("Waiting for msg1");
        readResult.get();
        String echo = new String(buffer.array()).trim();
        System.out.println("echo=" + echo);
        buffer.clear();

        ByteBuffer buffer2 = ByteBuffer.allocate(128);
        Future<Integer> readResult2 = client.read(buffer2);
        System.out.println("Waiting for msg2");
        readResult2.get();
        String echo2 = new String(buffer2.array()).trim();
        System.out.println("echo2=" + echo2);
        buffer2.clear();

        return echo + echo2;
    }
}

/* Testing
Client console
--------------
curl localhost:4999
curl: (1) Received HTTP/0.9 when not allowed

Sever console
-------------
/Users/dj.chen/Library/Java/JavaVirtualMachines/temurin-17.0.1/Contents/Home/bin/java --enable-preview -javaagent:/Applications/IntelliJ IDEA CE.app/Contents/lib/idea_rt.jar=58069:/Applications/IntelliJ IDEA CE.app/Contents/bin -Dfile.encoding=UTF-8 -classpath /Users/dj.chen/repoMy/NIO2Lab/target/classes async.AyncWithCompletionHandler
Hello, NIO2 !
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