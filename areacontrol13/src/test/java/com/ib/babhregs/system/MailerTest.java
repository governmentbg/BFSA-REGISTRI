package com.ib.babhregs.system;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import javax.activation.DataSource;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.util.ByteArrayDataSource;

import org.junit.BeforeClass;
import org.junit.Test;

import com.ib.system.exceptions.DbErrorException;
import com.ib.system.exceptions.InvalidParameterException;
import com.ib.system.mail.Mailer;
import com.ib.system.mail.Mailer.Content;
import com.ib.system.mail.MyMessage;
import com.ib.system.utils.FileUtils;

public class MailerTest {

	private static Integer ID_REGISTRATURE = 1;//1;
	private static final String MAILBOX="DEFAULT";//"DEFAULT";
//	private static final String USER="testuser";
//	private static final String PASS="Alab@la";
	
	
	// SOU L.Karavelov
//	private static final String USER="demosiscom@soukaravelov.com";
//	private static final String PASS="Yoy79595";
	// GARD Lukoil
//	private static final String USER="dokumentooborot@guardhms.com";
//	private static final String PASS="Lukoil@123";
	//NAPOO
//	private static final String USER="napoo@navet.government.bg";
//	private static final String PASS="2020Nav!!";
	//CORS Burgas
	private static final String USER="IvelinaDelovodstvo";
//	private static final String PASS="IvelinaDelovodstvo2022";
	private static final String PASS="rrmmyvxjryppadlz";
//	private static final String USER="krasig.test";
//	private static final String PASS="ffdrplapwhccesjg";
	static Properties props=new Properties();
	@BeforeClass
	public static void initialize() {
		
		try {
			SystemData sd = new SystemData();
		//	 props = sd.getMailProp(ID_REGISTRATURE, MAILBOX);
			
			props=Mailer.loadProps("system.properties");
		
		
//LUKOIL GARD
//		props.setProperty("mail.smtp.host","smtp.gmail.com");
//		props.setProperty("mail.imaps.host","imap.gmail.com");
//		props.setProperty("user.name","dokumentooborot@guardhms.com");
//		props.setProperty("user.password","Lukoil@123");
//		props.setProperty("mail.store.protocol","imaps");
//		props.setProperty("mail.imap.port","993");
//		props.setProperty("mail.from","dokumentooborot@guardhms.com");
//		props.setProperty("mail.imap.starttls.enable","true");
//				
//		props.setProperty("mail.transport.protocol","smtp");
//		//props.setProperty("mail.transport.protocol.send","smtp");
//		props.setProperty("mail.smtp.starttls.enable","true");
//		props.setProperty("mail.smtp.auth","true");
//		props.setProperty("mail.debug","true");
//		
//		props.setProperty("mail.imap.ssl.protocols","TLSV1.2");
		
		// NAPOO
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testGetMessageCount() throws Exception {
		Mailer mailer = new Mailer();
//		SystemData sd = new SystemData();
		
		try {
//			Properties props = sd.getMailProp(ID_REGISTRATURE, "DEFAULT");
			if (props == null) {
				System.out.println("Errrrrrrooooorrrrr");
				return;
			}
			
			if (!props.isEmpty()){
//				props.put("mail.imap.ssl.enable", "true");
				int messageCount = mailer.getMessageCount(props, USER, PASS,"Inbox",false);
				System.out.println("----------->"+messageCount);
				assertTrue(messageCount>0);
				
			}
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail(e.getMessage());
		}
		/*if (!props.isEmpty()){
			int messageCount = new Mailer3().getMessageCount(props, props.getProperty("user.name"), props.getProperty("user.password"),"Inbox",false);
			assertTrue(messageCount>0);
			System.out.println(messageCount);
		}*/
		
	}
	
	@Test
	public void testSent() {
		Mailer mailer = new Mailer();
//		SystemData sd = new SystemData();

		// mailer.connectForSend(props);
		try {
//			Properties props = sd.getMailProp(ID_REGISTRATURE, "DEFAULT");
			if (props == null) {
				System.out.println("Errrrrrrooooorrrrr");
				return;
			}
//			props.setProperty("mail.smtp.host", "10.20.0.150");
//
////			props.setProperty("mail.smtp.host", "10.29.1.165");
//			props.setProperty("mail.smpt.port", "123");
//			props.setProperty("mail.imap.port", "123");
//			props.setProperty("mail.debug", "true");
			
			
			mailer.sent(Content.PLAIN, props, props.getProperty("user.name"), props.getProperty("user.password"),
					props.getProperty("mail.from"), "My Name in Mail", "krasi@lirex.com", "test 1", "тестов емаил 1",
					null);
		} catch (NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		} catch (InvalidParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
//		} catch (DbErrorException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			fail();
		}
	}
//	@Test - файловете не са достъпни
	public void testSentAttachment() {
		Mailer mailer = new Mailer();
		SystemData sd = new SystemData();

		// mailer.connectForSend(props);
		try {
			byte[] fileBytes=null;
			String type=com.ib.system.utils.FileUtils.getMimeType(new File("/home/krasi/Desktop/index.png").toURI().toString()); 
			fileBytes = com.ib.system.utils.FileUtils.getBytesFromFile(new File("/home/krasi/Desktop/index.png") );
			System.out.println("Type is:"+type);
			ByteArrayDataSource ds=new ByteArrayDataSource(fileBytes, type);
			ArrayList<DataSource> attachments=new ArrayList<DataSource>();
			ds.setName("index.png");//"dbimport.out");
			attachments.add(ds);
			
			
			
			Properties props = sd.getMailProp(ID_REGISTRATURE, "DEFAULT");
			if (props == null) {
				System.out.println("Errrrrrrooooorrrrr");
				return;
			}
//			props.setProperty("mail.smtp.host", "10.20.0.150");
//
////			props.setProperty("mail.smtp.host", "10.29.1.165");
//			props.setProperty("mail.smpt.port", "123");
//			props.setProperty("mail.imap.port", "123");
//			props.setProperty("mail.debug", "true");
			
			
			mailer.sent(Content.PLAIN, props, props.getProperty("user.name"), props.getProperty("user.password"),
					props.getProperty("mail.from"), "My Name in Mail", "krasi@lirex.com", "test 1", "тестов емаил 1",
					attachments);
		} catch (NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		} catch (InvalidParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		} catch (DbErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}
	}
	static long messUID = 0;
	@Test
	public void testRead() throws Exception {

		Mailer mailer3 = new Mailer();
		SystemData sd = new SystemData();

		try {
			Properties props = sd.getMailProp(ID_REGISTRATURE, "DEFAULT");
			if (props == null) {
				System.out.println("Errrrrrrooooorrrrr");
				return;
			}

			ArrayList<MyMessage> msgs = mailer3.read(props, props.getProperty("user.name"),
					props.getProperty("user.password"), "INBOX", 1, 1, false);
			assertTrue(msgs.size() > 0);
			System.out.println("size=" + msgs.size());
			System.out.println("====================================");
			for (MyMessage myMessage : msgs) {
				messUID = myMessage.getMessUID();
				System.out.println(myMessage.getSubject() + "--UID=" + myMessage.getMessUID() + " DateSent:"
						+ myMessage.getSendDate() + " Date Recived:" + myMessage.getReceivedDate()+" from:"+myMessage.getFrom());// .getSendDate()+"
																									// "+myMessage.getFrom()+"
																									// "+myMessage.getSubject());
				System.out.println("Body:"+myMessage.getBody());
				FileUtils.writeBytesToFile("/home/krasig/MyApps/Tmp/Mail.txt", myMessage.getSubject().getBytes());
			}
			System.out.println("====================================");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}

	}
	
	@Test
	public void testForword() throws Exception {
		Mailer mailer3 = new Mailer();
		SystemData sd = new SystemData();

		try {
			Properties props = sd.getMailProp(ID_REGISTRATURE, "DEFAULT");
			props.setProperty("mail.smtp.auth", "true");
			if (props == null) {
				System.out.println("Errrrrrrooooorrrrr");
				return;
			}
			if (!props.isEmpty()){
				new Mailer().forward(props,messUID,"INBOX", "su:bject", "bodymess11");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}
	}
}
