package at.bitfire.cadroid;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class CertificateInfo implements Parcelable {
	private final static String TAG = "cadroid.CertificateInfo";
	
	String errorMessage;
	
	X509Certificate certificate;
	
	String hostName;
	boolean hostNameVerified;
	
	public boolean basicConstraintsValid() {
		return certificate.getBasicConstraints() >= 1;
	}
	
	public boolean currentlyValid() {
		try {
			certificate.checkValidity();
			return true;
		} catch (Exception e) {
			return false;
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
		} catch (CertificateEncodingException e) {
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
				CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
				ByteArrayInputStream is = new ByteArrayInputStream(cert);
				info.certificate = (X509Certificate)certFactory.generateCertificate(is);
			} catch (CertificateException e) {
				Log.e(TAG, "Couldn't get CertificateFactory", e);
			}

			return info;
		}
		
		public CertificateInfo[] newArray(int size) {
		    return new CertificateInfo[size];
		}
	};
}
