
package com.development.androrb;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class about extends Activity
{
	/** Called when the activity is first created. */

	private static final String TAG = "Orband";
	TextView about;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);


		about = (TextView) findViewById(R.id.about);
	
    	 CharSequence str = "" +
			"\nAndrOrb v0.3a\n\n" +
			"is designed to display and load your media data through the Orb Network. (www.orb.com)\n\n" +
			"***\n This product uses the Orb API but is not endorsed or certified by Orb.\n***\n\n" +
			"Help:\nFirst click MENU and place your Login Data for Orb (in the dev Version also place your Orb ApiKey). Than just choose what content you like view/listen and you get the List with your Items. Right now its ALL Media, later on I will implement your Playlist and other Features.\nIf there are more than 100 Items (you may change this Nr. also in the MENU), you will see at the bottom --More Items. When you choose one Item it needs a sec to get the Stream and your Media come loaded.\n\n"+
			"Limits:\nRight now only Video and Audio (inkl. Web Radio) Streaming works fine for me on a WIFI Network with an G1 RC33. I am still on to implement Photo and Documents!!\n\n"+
			"Info:\nI wrote this Application to learn Android Coding and to share my result with you. Therefore I hope you like it and this Application may be useful for you. For sure it is not perfect and there are many things we can improve. For now I am glad that I do not need to use the browser to use Orb :)\n\n"+
			"You may contact me through my Homepage if you have Ideas, Compliments, Advices and/or simple like to hire me.\n\n"+
			"Enjoy this Application!\n\n"+
			"Yours Christian\n\n\n"+
			"Disclaimer:\nTHE AUTHOR SHALL NOT, UNDER ANY CIRCUMSTANCES, BE LIABLE TO YOU FOR ANY INDIRECT, INCIDENTAL, CONSEQUENTIAL, SPECIAL OR EXEMPLARY DAMAGES ARISING OUT OF OR IN CONNECTION WITH USE OF APPLICATION, WHETHER BASED ON BREACH OF CONTRACT, BREACH OF WARRANTY, TORT (INCLUDING NEGLIGENCE, PRODUCT LIABILITY OR OTHERWISE), OR ANY OTHER PECUNIARY LOSS, WHETHER OR NOT THE AUTHOR HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. UNDER NO CIRCUMSTANCES SHALL THE AUTHOR BE LIABLE TO YOU FOR ANY AMOUNT. AND YOU WILL BE SOLELY RESPONSIBLE FOR ANY DAMAGE THAT RESULTS FROM THE USE OF THIS APPLICATION INCLUDING, BUT NOT LIMITED TO, ANY DAMAGE TO YOUR COMPUTER SYSTEM OR LOSS OF DATA.\n\n" +
			"\n" +
			"(c) 2009 www.Christian-Albert-Mueller.com\n\n";
		 about.setText(str);
	}

	


}