package my.mk.ns;

import lombok.extern.slf4j.Slf4j;
import my.mk.serial.ParamConfig;
import my.mk.serial.SerialPortUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
public class StateSender {
    private static SerialPortUtils serialPort;
    private InputStream inputStream;

    private volatile LinkedBlockingQueue<ScriptFrame> queue = new LinkedBlockingQueue<>();

    public void addToSend(ScriptFrame frame){
        try {
            queue.put(frame);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public StateSender() {
        // 实例化串口操作类对象
        serialPort = new SerialPortUtils();
        // 创建串口必要参数接收类并赋值，赋值串口号，波特率，校验位，数据位，停止位
        ParamConfig paramConfig = new ParamConfig("COM8", 19200, 0, 8, 1);
        // 初始化设置,打开串口，开始监听读取串口数据
        serialPort.init(paramConfig);
        Thread thread = new Thread(() -> {
            try {
                inputStream = serialPort.serialPort.getInputStream();
//                if (!sync()) {
//                    System.out.println("同步失败");
//                }
                ScriptFrame scriptFrame = null;

                while (true) {
                    if (scriptFrame != null && scriptFrame.time > 0) {
                        serialPort.send(scriptFrame.bytes);
                        scriptFrame.time--;
                    } else {
                        ScriptFrame newScriptFrame = queue.poll();
                        if (newScriptFrame != null) {
                            log.trace("got to send,{}",newScriptFrame.bytes);
                            scriptFrame = newScriptFrame;

                            serialPort.send(scriptFrame.bytes);
                            scriptFrame.time--;
                        }else {
                            serialPort.send( NSInputState.NONE.toBytes());
                        }
                    }
//                    int resp = inputStream.read();
//                    if (resp == 0x92 || resp == -1) {
//                        System.out.println("NACK");
//                        while (!sync()) {
//                            log.error("Unable to sync after NACK");
//                            sleep(1000);
//                        }
//                    } else if (resp != 0x90) {
//                        // Unknown response
//                        log.error("resp error:{}" , resp);
//                    }
                }
            } catch (Throwable ignore) {
            }
        });

        thread.setPriority(Thread.MAX_PRIORITY);
        thread.start();
    }

//    private boolean sync() throws IOException {
//        serialPort.send(new byte[]{(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff});
//
//        sleep(100);
//        byte[] readBuffer = new byte[inputStream.available()];
//        int ignore = inputStream.read(readBuffer);
//        serialPort.send(new byte[]{(byte) 0xff});
//        int resp = inputStream.read();
//        if (resp != 0xff)
//            return false;
//        sleep(100);
//        serialPort.send(new byte[]{(byte) 0x33});
//        resp = inputStream.read();
//        if (resp != 0xcc)
//            return false;
//        sleep(100);
//        serialPort.send(new byte[]{(byte) 0xcc});
//        resp = inputStream.read();
//        return resp == 0x33;
//    }

    private void sleep(int m) {
        try {
            Thread.sleep(m);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        serialPort.closeSerialPort();
    }
}
