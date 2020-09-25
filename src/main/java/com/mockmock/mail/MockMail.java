package com.mockmock.mail;

import java.io.Serializable;
import java.util.List;
import java.util.LinkedList;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

public class MockMail implements Comparable<MockMail>, Serializable
{
	private static final long serialVersionUID = 1L;
		
	private long id;
    private String from;
    private String to;
    private String subject;
    private String body;
    private String bodyHtml;
    private String rawMail;
    
    private long receivedTime;
    
    private List<Attachment> attachments = new LinkedList<>();

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public String getFrom()
    {
        return from;
    }
    
    public void setFrom(String from)
    {
        this.from = from;
    }
    
    public String getTo()
    {
        return to;
    }

    public void setTo(String to)
    {
        this.to = to;
    }
    
    public String getSubject() 
    {
        return subject;
    }
    
    public void setSubject(String subject)
    {
        this.subject = subject;
    }

    public String getBody()
    {
        return body;
    }

    public void setBody(String body)
    {
        this.body = body;
    }

    public String getRawMail()
    {
        return rawMail;
    }

    public void setRawMail(String rawMail)
    {
        this.rawMail = rawMail;
    }

    public String getBodyHtml() 
    {
        return bodyHtml;
    }

    public void setBodyHtml(String bodyHtml) 
    {
        this.bodyHtml = bodyHtml;
    }

    @Override
    public int compareTo(MockMail o)
    {
        long receivedTime = this.getReceivedTime();
        long receivedTime2 = o.getReceivedTime();

        long diff = receivedTime - receivedTime2;
        return (int) diff;
    }

    public long getReceivedTime()
    {
        return receivedTime;
    }

    public void setReceivedTime(long receivedTime)
    {
        this.receivedTime = receivedTime;
    }
    
    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }
    
    public List<Attachment> getAttachments() {
        return attachments;
    }
    
    public boolean addAttachment(String fileName, String contentType, byte[] data){
    	return attachments.add(new Attachment(fileName, contentType, data));
    }
    
    public boolean hasAttachment(){
    	return attachments != null && !attachments.isEmpty();
    }
    
    public static class Attachment implements Serializable{
		private static final long serialVersionUID = 1L;
		
		private String fileName;
    	private String contentType;
    	private byte[] data;
    	
    	public Attachment(String fileName, String contentType, byte[] data){
    		this.fileName = fileName;
    		this.contentType = contentType;
    		this.data = data;
    	}
    	
    	public void setFileName(String fileName){
    		this.fileName = fileName;
    	}
    	
    	public String getFileName(){
    		return fileName;
    	}
    	
    	public void setContentType(String contentType){
    		this.contentType = contentType;
    	}
    	
    	public String getContentType(){
    		return contentType;
    	}
    	
    	public void setData(byte[] data){
    		this.data = data;
    	}
    	
    	public byte[] getData(){
    		return data;
    	}
    }
    
    private MimeMessageSerializableWrapper mimeMessageWrapper;
    
    public MimeMessage getMimeMessage()
    {
        return (mimeMessageWrapper != null) ? mimeMessageWrapper.getMimeMessage() : null;
    }
    
    public MimeMessageSerializableWrapper getMimeMessageWrapper()
    {
        return mimeMessageWrapper;
    }

    public void setMimeMessageWrapper(MimeMessageSerializableWrapper mimeMessageWrapper)
    {
        this.mimeMessageWrapper = mimeMessageWrapper;
    }
    
    public void setMimeMessage(MimeMessage mimeMessage) throws MessagingException
    {
		this.mimeMessageWrapper = new MimeMessageSerializableWrapper(mimeMessage);
    }    
}
