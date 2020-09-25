package com.mockmock.mail;

import com.google.common.eventbus.Subscribe;
import com.mockmock.Settings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ListIterator;

@SuppressWarnings("unchecked")
@Service
public class MailQueue
{
    private static ArrayList<MockMail> mailQueue;
    private static File listFile;
    private Settings settings;
    
    static{
    	listFile = new File("mail.bin");
    	Object obj = readObj(listFile);
    	if(obj != null && obj instanceof ArrayList<?>){
    		mailQueue = (ArrayList<MockMail>) obj;
    		System.out.println("Reading existing Mail Queue: " + mailQueue.size() + " items found");
    	}else{
    		mailQueue = new ArrayList<MockMail>();
    		System.out.println("Cannot find existing Mail Queue: using new queue");
    	}
    	Runtime.getRuntime().addShutdownHook(new Thread() {
    		@Override
    		public void run() {
    			System.out.println("Writing Mail Queue to file: " + (writeObj(listFile, mailQueue) ? "suceeded" : "failed"));
    		}
    	});
    }
    
    public static Object readObj(File file){
    	Object obj = null;
    	try{
    		FileInputStream fileIn = new FileInputStream(file);
    		ObjectInputStream in = new ObjectInputStream(fileIn);
    		obj = in.readObject();
    		in.close();
			fileIn.close();		
    	}catch(ClassNotFoundException | IOException e){
    		obj = null;
    		System.err.println("Error reading Object");
    	}
    	return obj;
    }
    
    public static boolean writeObj(File file, Object obj){
    	boolean result = true;
    	try{
    		FileOutputStream fileOut = new FileOutputStream(file);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(obj);
			out.close();
			fileOut.close();	
    	}catch(IOException e){
    		result = false;
    		System.err.println("Error writing Object");
    	}
    	return result;
    }
    
    /**
     * Add a MockMail to the queue. Queue is sorted and trimmed right after it.
     * @param mail The MockMail object to add to the queue
     */
    @Subscribe
    public void add(MockMail mail)
    {
        mailQueue.add(mail);
        Collections.sort(mailQueue);
        Collections.reverse(mailQueue);

        trimQueue();
    }

    /**
     * @return Returns the complete mailQueue
     */
    public ArrayList<MockMail> getMailQueue()
    {
        return mailQueue;
    }

    /**
     * Returns the MockMail that belongs to the given ID
     * @param id The id of the mail mail that needs to be retrieved
     * @return Returns the MockMail when found or null otherwise
     */
    public MockMail getById(long id)
    {
        for(MockMail mockMail : mailQueue)
        {
            if(mockMail.getId() == id)
            {
                return mockMail;
            }
        }

        return null;
    }

    /**
     * Returns the MockMail that was last send.
     *
     * @return  Returns the MockMail when found or null otherwise
     */
    public MockMail getLastSendMail()
    {
        if (mailQueue.size() == 0)
            return null;

        return mailQueue.get(0);
    }

    /**
     * Removes all mail in the queue
     */
    public void emptyQueue()
    {	
        mailQueue.clear();
        mailQueue.trimToSize();
    }

	/**
	 * Removes the mail with the given id from the queue
	 * @param id long
	 * @return boolean
	 */
	public boolean deleteById(long id)
	{
		for(MockMail mockMail : mailQueue)
		{
			if(mockMail.getId() == id)
			{
				mailQueue.remove(mailQueue.indexOf(mockMail));
				return true;
			}
		}

		return false;
	}

    /**
     * Trims the mail queue so there aren't too many mails in it.
     */
    private void trimQueue()
    {
        if(mailQueue.size() > settings.getMaxMailQueueSize())
        {
            for (ListIterator<MockMail> iter = mailQueue.listIterator(mailQueue.size()); iter.hasPrevious();)
            {
                iter.previous();

                if(mailQueue.size() <= settings.getMaxMailQueueSize())
                {
                    break;
                }
                else
                {
                    iter.remove();
                }
            }
        }

        mailQueue.trimToSize();
    }

    @Autowired
    public void setSettings(Settings settings) {
        this.settings = settings;
    }
}
