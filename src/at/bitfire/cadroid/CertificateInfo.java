package at.bitfire.cadroid;

import java.io.IOException;
import java.lang.reflect.Array;
import java.math.BigInteger;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.X509Extensions;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class CertificateInfo implements Parcelable {
	private final static String TAG = "cadroid.CertificateInfo";

	String hostName;
	boolean hostNameVerified;
	
	private String errorMessage;
	public String getErrorMessage() { return errorMessage; }
	public void setErrorMessage(String msg) { errorMessage = msg; }
	
	private String subject;
	public String getSubject() { return subject; }
	
	private LinkedList<String> altSubjectNames;
	public String[] getAltSubjectNames() { return altSubjectNames.toArray(new String[0]); }

	private BigInteger serialNumber;
	public BigInteger getSerialNumber() { return serialNumber; }
	
	private Date notBefore;
	public Date getNotBefore() { return notBefore; }
	
	private Date notAfter;
	public Date getNotAfter() { return notAfter; }
	
	private boolean currentlyValid;
	public boolean isCurrentlyValid() { return currentlyValid; }
	
	BigInteger pathLength;
	boolean isCA;

	protected X509CertificateHolder certificate;
	public byte[] getEncoded() throws IOException { return certificate.getEncoded(); }
	public void setCertificate(X509Certificate certificate) {
		try {
			subject = certificate.getSubjectDN().getName();
			serialNumber = certificate.getSerialNumber();
			
			altSubjectNames = new LinkedList<String>();
			if (certificate.getSubjectAlternativeNames() != null)
				for (List<?> altName : certificate.getSubjectAlternativeNames()) {
					int type = (Integer)altName.get(0);
					Object name = altName.get(1);
					if (name instanceof Array)
						altSubjectNames.add(new String((byte[])name));
					else
						altSubjectNames.add((String)name);
				}
			
			// use Bouncy Castle to parse some other details
			JcaX509CertificateHolder certHolder = new JcaX509CertificateHolder(certificate);
			this.certificate = certHolder;
			
			notBefore = certHolder.getNotBefore();
			notAfter = certHolder.getNotAfter();
			currentlyValid = certHolder.isValidOn(new Date());
			
			Extensions extensions = this.certificate.getExtensions();
		    BasicConstraints bc = BasicConstraints.fromExtensions(extensions);
		    isCA = bc.isCA();
		    pathLength = bc.getPathLenConstraint();
		} catch (CertificateEncodingException e) {
			Log.e(TAG, "Encoding error", e);
		} catch (CertificateParsingException e) {
			Log.e(TAG, "Parsing error", e);
		}
	}
	
	
	// Parcelable

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(errorMessage);
		
		dest.writeString(hostName);
		dest.writeByte(hostNameVerified ? (byte)1 : (byte)0);
		
		byte[] cert = null;
		try {
			cert = certificate.getEncoded();
		} catch (IOException e) {
			Log.e(TAG, "Couldn't encode certificate", e);
		}
		if (cert != null) {
			dest.writeInt(cert.length);
			dest.writeByteArray(cert);
		} else
			dest.writeInt(0);
	}

	public static final Parcelable.Creator<CertificateInfo> CREATOR = new Parcelable.Creator<CertificateInfo>() {
		public CertificateInfo createFromParcel(Parcel in) {
		    CertificateInfo info = new CertificateInfo();
		    
			info.errorMessage = in.readString();
			
			info.hostName = in.readString();
			info.hostNameVerified = in.readByte() != 0;
			
			byte[] cert = new byte[in.readInt()];
			in.readByteArray(cert);
			
			try {
				info.certificate = new X509CertificateHolder(cert);
			} catch (IOException e) {
				Log.e(TAG, "I/O exception", e);
			}

			return info;
		}
		
		public CertificateInfo[] newArray(int size) {
		    return new CertificateInfo[size];
		}
	};
}
