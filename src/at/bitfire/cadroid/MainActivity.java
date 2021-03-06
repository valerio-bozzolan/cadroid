package at.bitfire.cadroid;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends Activity {
	
	private final static String
		KEY_FRAGMENT_TAG = "fragment_tag",
		KEY_CERTIFICATE_INFO = "certificate_info";
	private String activeFragmentTag;
	
	private ListView titlesList;
	
	private CertificateInfo certificateInfo;
	void setCertificateInfo(CertificateInfo info) { certificateInfo = info; }
	CertificateInfo getCertificateInfo() { return certificateInfo; }
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);

		// set main fragment
		String fragmentType;
		if (savedInstanceState != null) {
			fragmentType = savedInstanceState.getString(KEY_FRAGMENT_TAG);
			certificateInfo = (CertificateInfo)savedInstanceState.getParcelable(KEY_CERTIFICATE_INFO);
		} else {
			fragmentType = IntroFragment.TAG;
			showFragment(fragmentType, false);
		}

		// prepare titles list (only on large screens)
		titlesList = (ListView)findViewById(R.id.titles_list);
		if (titlesList != null) {
			String[] titles = {
					getString(R.string.intro_title),
					"2 Fetch certificate",
					"3 Verify certificate",
					"4 Export",
					"5 Import"
			};
			titlesList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_activated_1, android.R.id.text1, titles));
			titlesList.setEnabled(false);
		}
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(KEY_FRAGMENT_TAG, activeFragmentTag);
		outState.putParcelable(KEY_CERTIFICATE_INFO, certificateInfo);
	}


	public void showFragment(String tag, boolean transition) {
		FragmentManager fm = getFragmentManager();
		
		Fragment nextFragment = null;
		if (IntroFragment.TAG.equals(tag))
			nextFragment = new IntroFragment();
		else if (FetchFragment.TAG.equals(tag))
			nextFragment = new FetchFragment();
		else if (VerifyFragment.TAG.equals(tag))
			nextFragment = new VerifyFragment();
		else if (ExportFragment.TAG.equals(tag))
			nextFragment = new ExportFragment();
		else if (ImportFragment.TAG.equals(tag))
			nextFragment = new ImportFragment();
		
		FragmentTransaction ft = fm.beginTransaction()
			.replace(R.id.fragment_container, nextFragment, tag);
		if (transition)
			ft = ft
				.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
				.addToBackStack(null);
		ft.commitAllowingStateLoss();
	}
	
	public void onShowFragment(String tag) {
		activeFragmentTag = tag;
		
		if (titlesList != null) {
			titlesList.clearChoices();
			
			int position = 0;
			/*if (IntroFragment.TAG.equals(tag))
				position = 0;
			else */ if (FetchFragment.TAG.equals(tag))
				position = 1;
			else if (VerifyFragment.TAG.equals(tag))
				position = 2;
			else if (ExportFragment.TAG.equals(tag))
				position = 3;
			else if (ImportFragment.TAG.equals(tag))
				position = 4;
			titlesList.setItemChecked(position, true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

	public void showHelp(MenuItem item) {
		startActivityForResult(new Intent(Intent.ACTION_VIEW, Uri.parse("http://cadroid.bitfire.at")), 0);
	}

}
