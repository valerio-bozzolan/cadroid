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
		switch (tag) {
		case IntroFragment.TAG:
			nextFragment = new IntroFragment();
			break;
		case FetchFragment.TAG:
			nextFragment = new FetchFragment();
			break;
		case VerifyFragment.TAG:
			nextFragment = new VerifyFragment();
			break;
		case ExportFragment.TAG:
			nextFragment = new ExportFragment();
			break;
		case ImportFragment.TAG:
			nextFragment = new ImportFragment();
			break;
		}
		
		FragmentTransaction ft = fm.beginTransaction()
			.replace(R.id.fragment_container, nextFragment, tag);
		if (transition)
			ft = ft
				.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
				.addToBackStack(null);
		ft.commitAllowingStateLoss();
	}
	
	public void onShowFragment(String type) {
		activeFragmentTag = type;
		
		if (titlesList != null) {
			titlesList.clearChoices();
			
			int position = 0;
			switch (type) {
			/*case IntroFragment.TAG:
				position = 0;
				break;*/
			case FetchFragment.TAG:
				position = 1;
				break;
			case VerifyFragment.TAG:
				position = 2;
				break;
			case ExportFragment.TAG:
				position = 3;
				break;
			case ImportFragment.TAG:
				position = 4;
			}
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
