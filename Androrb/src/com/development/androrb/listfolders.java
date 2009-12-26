/*
 * OrbAnd 0.1 Alpha
 * 
 * Copyright (C) 2009 Christian Albert Müller
 * http://www.christian-albert-mueller.com
 *
 * This Source Code is Freeware, OpenSource as you like to call it.
 * I do not take any responsibility for its usage and also I dont have
 * Time to explain the Source.
 * 
 * If you like to Develop this Version you are welcome to send me your
 * Updates and I will check them and keep the right to publish it for
 * other Users on my Page.
 * 
 * If you can make some Explanations for other Users may be helpful.
 * 
 * Should you distribute this Version you need a Commercial Key from Orb
 * 
 * I am not related with ORB and its not an official Work from ORB
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * 
 * You can extend this Copyright with your Changes and keep Responsible
 * for your Work.
 * 
 *
 * To start:
 * - go to: https://mycast.orb.com/orb/html/createAPIKey.html
 *   - Enter your ORB Login and request an Developer API Key
 *   - Enter your Login, Password and API Key into the Source Code Bellow
 *   - Compile the Source ... and Enjoy Orb on your Android Mobile :)
 *
 * Infos about Error Codes and XML Requests at: http://developer.orb.com
 *
 */

package com.development.androrb;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.EncodingUtils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.NumberKeyListener;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class listfolders extends ListActivity
{
	/** Called when the activity is first created. */

	private static final String TAG = "listfolders";
	String TextUpdate;
	String html;
//	String Username = "yourusername";
//	String Password = "yourpassword";
//	String apikey = "evcphjpgribp";
	String Username;
	String Password;
	String apikey = "2a7pft34xyma4";
	String sid;
	String maxcount;
	TextView splashtext;
	TextView username;
		int loggedin = 0;

	// Initializing the ListView
	private ArrayAdapter<String> mAdapter;
	private ArrayList<String> mStrings = new ArrayList<String>();

	// Just a small Status Line in the Top

	ImageView splashimage;

	// ArrayList for Orb Responses (MediaTitle, Url ...)
	ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
	HashMap<String, String> item = new HashMap<String, String>();

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listfolders);

		splashimage = (ImageView) findViewById(R.id.splashscreen);
		splashtext = (TextView) findViewById(R.id.splashload);

		mAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, mStrings);

		setListAdapter(mAdapter);
		ListView MyOrbList = getListView();
		

		
		
		//Load Preferences
	    SharedPreferences preferences = getPreferences(MODE_PRIVATE);
	    
	    Username = preferences.getString("UID", "");
	    Password = preferences.getString("PW", "");
	    //apikey   = preferences.getString("API", "");
	    maxcount = preferences.getString("MAX", "-");
	    
	    if (maxcount == "-")
	    {
	    	SharedPreferences.Editor editor = preferences.edit();
	    	editor.putString("MAX", "100"); editor.commit();
	    	maxcount = "100";
	    }
	  	  
	    /*
        if (apikey.length() == 0)
        {
			showDialog(2);			
        }
        */
        
        if (Username.length() == 0)
        {
			showDialog(1);			
        }
        
        if (Password.length() == 0)
        {
			showDialog(1);			
        }
        
        if (maxcount.length() == 0)
        {
			showDialog(3);			
        }
        
		splashimage.setOnClickListener(new OnClickListener()

		{
			@Override
			public void onClick(View arg0)
			{
				if (loggedin == 1)
				{
				splashimage.setVisibility(View.GONE);
				splashtext.setVisibility(View.GONE);
				}
				else
				{
					   toasti();
		               Intent mainIntent = new Intent(listfolders.this,listfolders.class);
		    			listfolders.this.startActivity(mainIntent);
		    			listfolders.this.finish();
				}
			}
		});

		MyOrbList.setOnItemClickListener(new OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id)
			{

					String rurl = "";
					Intent mainIntent = new Intent(listfolders.this,
							Orband.class);
					if (position == 0)
						rurl = "video";
					else if (position == 1)
						rurl = "audio.file";
					else if (position == 2)
						rurl = "audio.web";
					else if (position == 3)
						rurl = "photo";
					else if (position == 4)
						rurl = "document";

					mainIntent.putExtra("mediakind", rurl);
					mainIntent.putExtra("sid", sid);
					mainIntent.putExtra("start", "0");
					mainIntent.putExtra("max", maxcount);
					listfolders.this.startActivity(mainIntent);

			}
		});

		mAdapter.add("video files");
		mAdapter.add("audio files");
		mAdapter.add("web radios");
		mAdapter.add("photos");
		mAdapter.add("documents");

		checkUpdate.start();
		

		
 

	}

	private Thread checkUpdate = new Thread()
	{
		public void run()
		{
			try
			{
			
			do_main();

			} catch (Exception e)
			{
			}

		}

	};

	private void toasti()
	{
		Toast.makeText(this, "Reconnect!", Toast.LENGTH_SHORT).show();
	}
	
	private void do_main()
	{
		TextUpdate = "Login into Orb...";
		runOnUiThread(showTextUpdate);
		
		String Urli="http://api.orb.com/orb/xml/session.login?apiKey=" + apikey + "&l=" + Username + "&password=" + Password;
		html = loaddata(Urli);
		//Log.i(TAG, " *------ Load Data 2 -----*: "+html);

		int status = Integer.parseInt(GetXmlInnerNr("status", "code", html, 1));

		sid = GetXmlNr("orbSessionId", html, 1);

		if (status == 0)
		{
			TextUpdate = "Touch Screen to continue";
			runOnUiThread(showTextUpdate);
			loggedin = 1;

		} else
		{
			if (status == 1)
			{
			    TextUpdate="> Unhandled exception, unknown error <";
			}
			else if (status == 2)
			{
				TextUpdate="> Session expired <";	
			}
			else if (status == 3)
			{
				TextUpdate="> Invalid Parameters <";	
		    }
			else if (status == 4)
			{
				TextUpdate="> Permission denied <";	
		    }
			else if (status == 5)
			{
				TextUpdate="Wrong login/password - ";
				TextUpdate=TextUpdate+"Press MENU for Setup";
		    }
			else if (status == 6)
			{
				TextUpdate="Wrong API-Key - ";	
				TextUpdate=TextUpdate+"Press MENU for Setup";
		    }
			else if (status == 7)
			{
				TextUpdate="> Invalid Session <";	
		    }
			else if (status == 8)
			{
				TextUpdate="> User login already exists <";	
		    }
			else if (status == 9)
			{
			    TextUpdate="Invalid password - ";	
			    TextUpdate=TextUpdate+"Press MENU for Setup";
		    }
			else if (status != 0)
			{   
				TextUpdate = "> Login Error : " + status+" <";
			    TextUpdate=TextUpdate+"\n(see https://developer.orb.com/wiki/Error_codes)";	
		    }
			
			runOnUiThread(showTextUpdate);
		}
	}

	private Runnable showTextUpdate = new Runnable()
	{
		public void run()
		{
			splashtext.setText(TextUpdate);
		}
	};
	
	private String loaddata2(String Urli)
	{
		String html2="";
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(Urli);
		try
		{
		HttpResponse response = client.execute(request);
		html2 =  TextHelper.GetText(response);
		}
		catch(Exception ex)
		{
		//content.setText(”Fail!”);
		}
		return html2;
		}

	private String loaddata(String Urli)
	{
		try
		{		
			URLConnection conn;
			conn = new URL(Urli).openConnection();
			
			InputStream is = conn.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(is);
			ByteArrayBuffer baf = new ByteArrayBuffer(8192);
			
			// loading part
			int current = 0;
		
			while ((current = bis.read()) != -1)
			{
			
				baf.append((byte) current);
			}
			//Log.i(TAG, " *------ after while -----*: "+current);
			html = EncodingUtils.getString(baf.toByteArray(), "UTF-8");
			
			
		} catch (Exception e)
		{
			TextUpdate = "Ups, check your Connection ...";
			runOnUiThread(showTextUpdate);
			
			//Toast.makeText(this, "Shit, Loading Error!", Toast.LENGTH_SHORT).show();
		}

		return html;
	}

	public String GetXmlNr(String xtag, String xhtml, int pos)
	{
		// Log.i(TAG, " *xhtml: " + xhtml);
		String x1tag = "";
		String x2tag = "";
		String retstr = "";
		int xstart = 0;
		int dummy = 0;
		int xend = 0;
		int xcont = 0;
		int xcounter = 0;

		x1tag = "<" + xtag;
		dummy = xtag.indexOf(" ");
		if (dummy != -1)
			xtag = xtag.substring(0, dummy);
		x2tag = "</" + xtag + ">";

		do
		{
			xcounter++;
			xhtml = xhtml.substring(xcont);
			xstart = xhtml.indexOf(x1tag);
			if (xstart < 0)
				return "!NULL!";
			xstart = xhtml.indexOf(">", xstart);
			if (xstart < 0)
				return "!NULL!";
			xend = xhtml.indexOf(x2tag, xstart);
			if (xend < 0)
				return "!NULL!";

			retstr = xhtml.substring(xstart + 1, xend);

			xcont = xend + x2tag.length();

		} while (xcounter < pos);

		return retstr;
	}

	public String GetXmlInnerNr(String xtag, String Attrib, String xhtml,
			int pos)
	{

		String x1tag;
		int x1len;
		String x2tag;
		String retstr;
		int xstart;
		int xend;
		int xcont = 0;
		int xcounter = 0;

		do
		{
			xcounter++;
			xhtml = xhtml.substring(xcont);
			// Log.i(TAG, "* xhtml: " + xhtml);
			x1tag = "<" + xtag;
			x1len = x1tag.length();
			x2tag = ">";

			xstart = xhtml.indexOf(x1tag);
			if (xstart < 0)
				return "";

			xend = xhtml.indexOf(x2tag, xstart);
			if (xend < 0)
				return "";

			// Log.i(TAG, "* xstart: " + xstart + " xend: " + xend);

			String Innerhtml = xhtml.substring(xstart + x1len, xend);

			// now search in the innerarea
			x1tag = Attrib + "=\"";
			x2tag = "\"";
			x1len = x1tag.length();
			xstart = Innerhtml.indexOf(x1tag);

			if (xstart < 0)
				return "";

			xcont = xend + 1;

			xend = Innerhtml.indexOf(x2tag, xstart + x1len);
			if (xend < 0)
				return "";
			retstr = Innerhtml.substring(xstart + x1len, xend);
			;

		} while (xcounter < pos);

		return retstr;
	}
	
	
	
	
	
	
	
	//------------------------------------------------------
	
	
	public static final int ONE_ID = Menu.FIRST + 1;
	public static final int TWO_ID = Menu.FIRST + 2;
	public static final int BETA_ID = Menu.FIRST + 3;
	public static final int MAX_ID = Menu.FIRST + 4;
	
	public void onCreateContextMenu1(ContextMenu menu, View v,
			ContextMenu.ContextMenuInfo menuInfo)
	{
		populateMenu(menu);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		populateMenu(menu);

		return (super.onCreateOptionsMenu(menu));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		return (applyMenuChoice(item) || super.onOptionsItemSelected(item));
	}

	public boolean onContextItemSelected1(MenuItem item)
	{
		return (applyMenuChoice(item) || super.onContextItemSelected(item));
	}

	private void populateMenu(Menu menu)
	{
		menu.add(Menu.NONE, ONE_ID, Menu.NONE, "Login Setup");
		//menu.add(Menu.NONE, BETA_ID, Menu.NONE, "ApiKey (Dev Mode)");
		menu.add(Menu.NONE, MAX_ID, Menu.NONE, "Max Items p.Page");
		menu.add(Menu.NONE, TWO_ID, Menu.NONE, "Help / About");
	}


	private boolean applyMenuChoice(MenuItem item)
	{
		switch (item.getItemId())
		{
		case ONE_ID:
			showDialog(1);			
			return (true);

		case BETA_ID:
			showDialog(2);			
			return (true);
			
		case MAX_ID:
			showDialog(3);			
			return (true);

		case TWO_ID:
			Intent mainIntent = new Intent(listfolders.this,about.class);
			listfolders.this.startActivity(mainIntent);
			return (true);
		}

		return (false);
	}
	
	
	@Override
    protected Dialog onCreateDialog(int id) {
    	switch (id) {
    	 case 1:
    // This example shows how to add a custom layout to an AlertDialog
    LayoutInflater factory = LayoutInflater.from(this);
    final View textEntryView = factory.inflate(R.layout.login_dialog, null);
    
    EditText dusernamefield = (EditText) textEntryView.findViewById	(R.id.username_edit);
    dusernamefield.setText(Username);

    EditText dpasswordfield = (EditText) textEntryView.findViewById	(R.id.password_edit);
    dpasswordfield.setText(Password);

    
    return new AlertDialog.Builder(listfolders.this)
        .setIcon(R.drawable.alert_dialog_icon)
        .setTitle("Login Data")
        .setView(textEntryView)
        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            	Dialog curDialog = (Dialog) dialog;
            	EditText dusernamefield = (EditText) curDialog.findViewById	(R.id.username_edit);
            	EditText dpasswordfield = (EditText) curDialog.findViewById	(R.id.password_edit);
                String dusername = dusernamefield.getText().toString();
                String dpassword = dpasswordfield.getText().toString();
                              
                SharedPreferences preferences = getPreferences(MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("UID", dusername);editor.commit();
                editor.putString("PW", dpassword);editor.commit();
                
                Intent mainIntent = new Intent(listfolders.this,listfolders.class);
    			listfolders.this.startActivity(mainIntent);
    			listfolders.this.finish();
                /* User clicked OK so do some stuff */
            }
        })
        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                /* User clicked cancel so do some stuff */
            }
        })
        .create();
    	 case 2:
    		    // This example shows how to add a custom layout to an AlertDialog
    		    LayoutInflater factory2 = LayoutInflater.from(this);
    		    final View apiEntryView = factory2.inflate(R.layout.apikey, null);	    
    		    
    		    EditText dapikeyfield = (EditText) apiEntryView.findViewById	(R.id.api_edit);
    		    dapikeyfield.setText(apikey);
    		    
    		    return new AlertDialog.Builder(listfolders.this)
    		        .setIcon(R.drawable.alert_dialog_icon)
    		        .setTitle("API KEY")
    		        .setView(apiEntryView)
    		        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
    		            public void onClick(DialogInterface dialog, int whichButton) {

    		          	Dialog curDialog = (Dialog) dialog;
    		          	EditText dapikeyfield = (EditText) curDialog.findViewById	(R.id.api_edit);
    		            	
    		                String dapikey   = dapikeyfield.getText().toString();
    		                              
    		                SharedPreferences preferences = getPreferences(MODE_PRIVATE);
    		                SharedPreferences.Editor editor = preferences.edit();

    		                editor.putString("API", dapikey); editor.commit();
    		                
    		                Intent mainIntent = new Intent(listfolders.this,listfolders.class);
    		    			listfolders.this.startActivity(mainIntent);
    		    			listfolders.this.finish();
    		                
    		                /* User clicked OK so do some stuff */
    		            }
    		        })
    		        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
    		            public void onClick(DialogInterface dialog, int whichButton) {

    		                /* User clicked cancel so do some stuff */
    		            }
    		        })
    		        .create();
    	 case 3:
 		    // This example shows how to add a custom layout to an AlertDialog
 		    LayoutInflater factory3 = LayoutInflater.from(this);
 		    final View maxEntryView = factory3.inflate(R.layout.maxcount, null);	    
 		    
 		    EditText dmaxkeyfield = (EditText) maxEntryView.findViewById	(R.id.maxcount_edit);
 		    dmaxkeyfield.setText(maxcount);
          	dmaxkeyfield.setKeyListener(new NumberKeyListener(){
	          	   @Override
	          	   protected char[] getAcceptedChars() {
	          	      char[] numberChars = {'1','2','3','4','5','6','7','8','9','0'};
	          	      return numberChars;
	          	   }

				@Override
				public int getInputType() {					
					return InputType.TYPE_CLASS_NUMBER;
				}
	          	});	
          	
 		    return new AlertDialog.Builder(listfolders.this)
 		        .setIcon(R.drawable.alert_dialog_icon)
 		        .setTitle("Max Item p.Page")
 		        .setView(maxEntryView)
 		        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
 		            public void onClick(DialogInterface dialog, int whichButton) {

 		          	Dialog curDialog = (Dialog) dialog;
 		          	EditText dmaxkeyfield = (EditText) curDialog.findViewById	(R.id.maxcount_edit);
 		          	
 
 		          	
 		                String dmaxcount   = dmaxkeyfield.getText().toString();
 		                int idmaxcount = Integer.parseInt(dmaxcount);
 		                 if (idmaxcount < 10) dmaxcount="10";
 		                 if (idmaxcount > 500) dmaxcount="500";
            
 		                SharedPreferences preferences = getPreferences(MODE_PRIVATE);
 		                SharedPreferences.Editor editor = preferences.edit();

 		                editor.putString("MAX", dmaxcount); editor.commit();
 		                
 		                Intent mainIntent = new Intent(listfolders.this,listfolders.class);
 		    			listfolders.this.startActivity(mainIntent);
 		    			listfolders.this.finish();
 		                
 		                /* User clicked OK so do some stuff */
 		            }
 		        })
 		        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
 		            public void onClick(DialogInterface dialog, int whichButton) {

 		                /* User clicked cancel so do some stuff */
 		            }
 		        })
 		        .create();   		    
    }
    return null;
}
	

}