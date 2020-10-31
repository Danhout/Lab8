package ru.itmo.s284719.network;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.itmo.s284719.network.commands.UserCommand;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 * Abstract class with functions for transmitting objects to channel.
 */
public abstract class ObjectSocketChannel {
    private static final Logger logger = LogManager.getLogger(ObjectSocketChannel.class);

    /**
     * Send an object to a channel.
     *
     * @author Kirill Shahow.
     * @param channel the channel for transmitting objects.
     * @param object the object for sending to the channel.
     */
    public static void sendObject(SocketChannel channel, Object object) throws IOException {
//        String strObject = null;
//        if (object != null && object.getClass().equals(UserCommand.class))
//            strObject = "command: " + ((UserCommand) object).getCommand();
//        logger.info("Try send object: " + strObject + " to SocketChannel: " + channel.getRemoteAddress() + '.');
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream objectOut = new ObjectOutputStream(byteOut);
        synchronized (objectOut) {
            objectOut.writeObject(object);
            objectOut.flush();
            objectOut.close();
        }
        channel.write(ByteBuffer.wrap(byteOut.toByteArray()));
//        logger.info("Send object: " + strObject + " to SocketChannel: " + channel.getRemoteAddress() + '.');
    }
    /**
     * Get an object from a channel.
     *
     * @author Kirill Shahow & Danhout.
     * @param channel the channel for transmitting objects.
     * @return an object for sending to the channel.
     */
    public static Object getObject(SocketChannel channel) throws IOException, ClassNotFoundException {
//        logger.info("Try get object from SocketChannel: " + channel.getRemoteAddress() + '.');
        int BUFF_SIZE = 8192;
        ByteBuffer buffer = ByteBuffer.allocate(BUFF_SIZE);
        Selector selector = Selector.open();
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_READ);
        while (selector.select() < 1);
        synchronized (channel) {
            channel.read(buffer);
        }
        ByteArrayInputStream byteIn = new ByteArrayInputStream(buffer.array());
        ObjectInputStream objectIn = new ObjectInputStream(byteIn);
        Object object = objectIn.readObject();
        objectIn.close();
//        logger.info("Get object: " + object + " from SocketChannel: " + channel.getRemoteAddress() + '.');
        return object;
    }
}