package com.mockmock.mail;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

public class MimeMessageSerializableWrapper implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private MimeMessage mimeMessage;
	
	public MimeMessageSerializableWrapper(){
	}
	
	public MimeMessageSerializableWrapper(MimeMessage mimeMessage){
		this.mimeMessage = mimeMessage;
	}
	
	public MimeMessage getMimeMessage(){
		return mimeMessage;
	}
	
	private void writeObject(ObjectOutputStream oos) throws IOException {
		try {
			this.mimeMessage.writeTo(oos);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		System.err.println("Writing");
	}

	private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props);
		try {
			this.mimeMessage = new MimeMessage(session , ois);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		System.err.println("Reading");
	}

}
