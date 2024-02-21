package com.ib.babhregs.rest;

import com.ib.babhregs.system.filters.LogClientRequestFilter;
import com.ib.babhregs.system.filters.LogClientResponseFilter;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.engines.ApacheHttpClient43Engine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import java.io.*;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;


/** 
 * Възможно най-простия цлиент, който да се ползва.
 * Целта му е да предостави достъп до CLIENT , което е heavy операцията.
 * НЯМА ДА ИМА НИКАКВИ СПЕЦИАЛНИ МЕТОДИ!!!!
 * Целта е да дава клиент и после всеки да си прави таргет-а, да подава параметри и т.н.
 * Използване:<pre> 
 * try {
 *	SimpleRestClient instance = SimpleRestClient.getInstance();
 *	WebTarget webTarget = instance.getClient().target(SRV_TARGET).path("/someservice");
 *	...
 *	}
 *	</pre>		
 * Аман от сложни универсални дивотии.
 * @author krasig
 *
 */
public class SimpleRestClient {
	static final Logger LOGGER = LoggerFactory.getLogger(SimpleRestClient.class);
	private static ResteasyClient client;
	private static SimpleRestClient					instance;
	
	public static SimpleRestClient getInstance()  {
		if (instance == null) {
			instance = new SimpleRestClient();
		}
		return instance;
	}
	
	public SimpleRestClient() {
		super();
		//String SRV_TARGET="http://localhost:8080/RestTests/rest/sample";
		try {
			
			int timeout = 15;

			
			RequestConfig config = RequestConfig.custom()
			.setConnectionRequestTimeout(timeout * 1000)
			.setConnectTimeout(timeout * 1000)
			.setSocketTimeout(timeout * 1000)
			.setRedirectsEnabled(true)
			.setRelativeRedirectsAllowed(true)
			.build();
			
			//=====  HTTP+HTTPS Start ======
			SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(
		            null, new TrustStrategy() {
		                @Override
		                public boolean isTrusted(X509Certificate[] arg0,
		                        String arg1) throws CertificateException {
		                    return true;
		                }
		            }).build();

			 Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder
			            .<ConnectionSocketFactory>create()
			            .register("http", new PlainConnectionSocketFactory())
			            .register("https", new SSLConnectionSocketFactory(/*sslContext*/getMyContext(), NoopHostnameVerifier.INSTANCE))//SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER))
					// .register("https",new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE))
			            .build();
				//=====  HTTP+HTTPS End ======			
			PoolingHttpClientConnectionManager cmPool = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
			cmPool.setMaxTotal(100); // Increase max total connection to 200
			cmPool.setDefaultMaxPerRoute(100); // Increase default max connection per route to 20

			CloseableHttpClient httpClient = HttpClientBuilder.create()
					.setDefaultRequestConfig(config)
					.setConnectionManager(cmPool)
					.build();// HttpClients.custom().setConnectionManager(cm).setDefaultRequestConfig(config).build();
			

			
			ApacheHttpClient43Engine engine = new ApacheHttpClient43Engine(httpClient);


			ResteasyClientBuilder resteasyClientBuilder = (ResteasyClientBuilder) ClientBuilder.newBuilder();
			//////////////////
//			KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
//			InputStream keyStoreData = new FileInputStream("/home/krasig/Downloads/test_babh_client.pfx");
//			keyStore.load(keyStoreData, "123456".toCharArray());
//			resteasyClientBuilder=resteasyClientBuilder.keyStore(keyStore,"123456");
			//////////////////
			client =resteasyClientBuilder.httpEngine(engine).build();



			//Това е за да логваме всяко извикване
			client.register(new LogClientRequestFilter());
			client.register(new LogClientResponseFilter());


		} catch (Exception e) {
		
			e.printStackTrace();
		}
	}

	
	
	public Client getClient() {
		return client;
	}

	private SSLContext getMyContext(){
		SSLContext sc=null;
		try
		{

			//TRUST
			// Create a trust manager that does not validate certificate chains
			TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return null;
				}
				public void checkClientTrusted(X509Certificate[] certs, String authType) {
				}
				public void checkServerTrusted(X509Certificate[] certs, String authType) {
				}
			}
			};

			//Key
			// First initialize the key and trust material.
				KeyStore ksKeys = KeyStore.getInstance("pkcs12");
			InputStream readStream = new FileInputStream(new File("/home/krasig/Downloads/test_babh_client.pfx"));
			ksKeys.load(readStream, "123456".toCharArray() );
// create an factory for key-managers
			KeyManagerFactory  kmf =KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			kmf.init(ksKeys, "123456".toCharArray());
//			SSLContext sslContext = SSLContext.getInstance("TLS");
//initialize the ssl-context
//			sslContext.init(kmf.getKeyManagers(),null,null);



			// Install the all-trusting trust manager
			sc = SSLContext.getInstance("SSL");
			sc.init(kmf.getKeyManagers(), trustAllCerts, new java.security.SecureRandom());


		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} catch (KeyStoreException e) {
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (CertificateException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (UnrecoverableKeyException e) {
            throw new RuntimeException(e);
        }
        return sc;
	}
}
