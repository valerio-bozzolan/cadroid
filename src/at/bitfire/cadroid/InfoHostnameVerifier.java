package at.bitfire.cadroid;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import org.apache.http.conn.ssl.BrowserCompatHostnameVerifier;

public class InfoHostnameVerifier implements HostnameVerifier {
	
	private static HostnameVerifier defaultVerifier = new BrowserCompatHostnameVerifier();
	
	
	CertificateInfo info;
	
	InfoHostnameVerifier(CertificateInfo info) {
		super();
		this.info = info;
	}

	@Override
	public boolean verify(String hostName, SSLSession session) {
		info.hostName = hostName;
		info.hostNameVerified = defaultVerifier.verify(hostName, session);
		return true;
	}
	
}
