package tarda;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import javax.jms.BytesMessage;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import org.apache.activemq.ActiveMQConnectionFactory;

/**
 * Thanks to Cameron Bird for the original code, you really saved my life buddy
 * Here is his project: Signal Maps https://signalmaps.co.uk/
 */
public class TARDA
{
    // D3330988e8-b79a-4b36-bced-7a62cd7bcad1 - My Queue
    // D347e5ed73-3403-4cc3-ae0f-ac366be7c923 - Doug's Queue(Broken)
    private static final String HOST  = "datafeeds.nationalrail.co.uk";
    private static final int    PORT  = 61616;
    private static final String QUEUE = "D3330988e8-b79a-4b36-bced-7a62cd7bcad1"; // Replace

    private static boolean stop = false;
    
    //MY CODE
    XMLReader reader = new XMLReader();

    public static void main(String[] args)
    {
        System.out.println("Starting...");

        Thread brokerThread = new Thread(new PushPortConsumer());
        brokerThread.setName("Consumer");
        brokerThread.setDaemon(false);
        brokerThread.start();
        
        Thread commandInput = new Thread(() ->
        {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in)))
            {
                String line;
                while ((line = br.readLine()) != null)
                {
                    if (line.contains("\n") || line.isEmpty())
                        break;
                }
                stop = true;
                brokerThread.interrupt();
            }
            catch (IOException ex) { ex.printStackTrace(); }
        });
        commandInput.setName("Command Input");
        commandInput.setDaemon(true);
        commandInput.start();
    }

    public static class PushPortConsumer implements Runnable, ExceptionListener 
    {
        @Override
        public void run()
        {
            try
            {
                ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://" + HOST + ":" + PORT + "?jms.clientID=NRDP&wireFormat.maxInactivityDuration=30000");
                javax.jms.Connection connection = connectionFactory.createConnection("d3user","d3password");
                connection.start();
                connection.setExceptionListener(this);

                Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
                Destination destination = session.createQueue(QUEUE);
                MessageConsumer consumer = session.createConsumer(destination);
                    
                    while (!stop)
                    {
                        try
                        {
                            Message message = consumer.receive();
                            
                            if (message instanceof BytesMessage)
                            {
                                final BytesMessage bMessage = (BytesMessage) message;
                                
                                byte[] sourceBytes;
                                sourceBytes = new byte[(int) bMessage.getBodyLength()];
                                bMessage.readBytes(sourceBytes);
                                
                                GZIPInputStream in = null;
                                ByteArrayOutputStream baos = null;
                                try
                                {
                                    in = new GZIPInputStream(new ByteArrayInputStream(sourceBytes));
                                    baos = new ByteArrayOutputStream();
                                    byte[] buf = new byte[8192];
                                    int len;
                                    while ((len = in.read(buf)) > 0)
                                        baos.write(buf, 0, len);
                                    XMLReader.test(new ByteArrayInputStream(baos.toByteArray()));
                                    //String messageText = new String(baos.toByteArray());
                                    //System.out.println(messageText);
                                    //XMLStack.test(messageText);
                                    //if(messageText.contains("toc=\"LE\"")) {
//                                    if(messageText.contains("requestSource=\"at12\"")) { // request source at12: Greater Anglia
//                                        ArrayList<String> msgArr = new ArrayList<>(Arrays.asList(messageText.split("><|<|>")));
//                                        msgArr.remove(0);
//                                        msgArr.remove(0);
//                                        msgArr.remove(0);
//                                        msgArr.remove(msgArr.size()-1);
//                                        msgArr.remove(msgArr.size()-1);
//                                        msgArr.remove(msgArr.size()-1);
//                                        msgArr.forEach(output -> System.out.printf("%s\n", output));
//                                        System.out.print('\n');
//                                    }
                                    
                                    //System.out.println("Message received & added to processing queue (" + messageQueue.size() + ") (" + messageText.substring(messageText.indexOf("ts=\"")+4, messageText.indexOf("\"", messageText.indexOf("ts=\"")+4)) + ")");
                                    
                                    message.acknowledge();
                                }
                                catch (IOException e) { e.printStackTrace(); }
                                finally
                                {
                                    if (in != null)
                                        try { in.close(); }
                                        catch (IOException ignore) {}
                                    if (baos != null)
                                        try { baos.close(); }
                                        catch (IOException ignore) {}
                                }
                            }
                        }
                        catch (JMSException e) { if (stop && !(e.getLinkedException() instanceof InterruptedException)) e.printStackTrace(); }
                    }
                //});
                
                //t.start();
                //System.in.read();
                //stop = true;

                consumer.close();
                session.close();
                connection.close();
            }
            catch (JMSException | RuntimeException e)
            {
                System.err.println("Caught: " + e.toString());
                e.printStackTrace();
            }
//            } catch (IOException ex) {
//                Logger.getLogger(TARDA.class.getName()).log(Level.SEVERE, null, ex);
//            }
        }

        @Override
        public void onException(JMSException e)
        {
            if (e.getMessage().contains("Channel was inactive for too "))
                // ??
            System.err.println("JMS Exception (" + e.toString()+ ") occured. Shutting down client.");
            e.printStackTrace();
        }
    }
}