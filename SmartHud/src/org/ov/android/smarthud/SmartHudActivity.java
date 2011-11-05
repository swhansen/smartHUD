package org.ov.android.smarthud;

// smartHUD prototype
// version  xxxx
//
//
//
//
//
//

import java.util.List;

import com.google.android.maps.MapActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import android.widget.Button;


import android.widget.Toast;
import android.widget.ViewFlipper;

public class SmartHudActivity extends Activity implements OnInitListener  {

    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH_HORIZ = 250;
    private static final int SWIPE_MAX_OFF_PATH_VERT = 150;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;
	private static final String String = null;
	private GestureDetector gestureDetector;
	View.OnTouchListener gestureListener;
	private Animation slideLeftIn;
	private Animation slideLeftOut;
	private Animation slideRightIn;
    private Animation slideRightOut;
    private ViewFlipper viewFlipper;
          
    private int MY_DATA_CHECK_CODE = 0;	
	private TextToSpeech tts;
	
	MediaPlayer player;	
	
// variables for the button selection
	public int currentMusicButtonRes;
	private int musicButton = 2;	
	private int[] musicButtonsFocused = new int[4];
	private int[] musicButtonsNormal = new int[4];		
	public int[] musicPanelButtonList = new int[4];
	
	public int currentChatButtonRes;
	private int chatButton = 2;	
	private int[] chatButtonsFocused = new int[4];
	private int[] chatButtonsNormal = new int[4];		
	public int[] chatPanelButtonList = new int[4];

	//Preferences Stuff
		
//	SharedPreferences prefs;
	
// called first time user clicks menu		
		@Override
	  public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater(); 
	    inflater.inflate(R.menu.menu, menu); 
	    return true; 
	  }
	 
	 @Override
	  public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) { 
	    case R.id.itemPrefs:
	      startActivity(new Intent(this, PrefsActivity.class)); 
	      break;
	    }
	    return true; 
	  }
		 	 	 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
       
        
        
        
    //Declare the viewFlippers
        viewFlipper = (ViewFlipper)findViewById(R.id.flipper);
        slideLeftIn = AnimationUtils.loadAnimation(this, R.anim.slide_left_in);
        slideLeftOut = AnimationUtils.loadAnimation(this, R.anim.slide_left_out);
        slideRightIn = AnimationUtils.loadAnimation(this, R.anim.slide_right_in);
        slideRightOut = AnimationUtils.loadAnimation(this, R.anim.slide_right_out);

        musicButtonsFocused[3] = R.drawable.music_up_focused;
        musicButtonsFocused[2] = R.drawable.music_play_focused;
        musicButtonsFocused[1] = R.drawable.music_skip_focused;
        musicButtonsFocused[0] = R.drawable.music_down_focused;
        
        musicButtonsNormal[3] = R.drawable.music_up_normal;
        musicButtonsNormal[2] = R.drawable.music_play_normal;
        musicButtonsNormal[1] = R.drawable.music_skip_normal;
        musicButtonsNormal[0] = R.drawable.music_down_normal;                
 //or...       
     //   String uri = "drawable/music_play_focused";	
	//	musicButtons[2] = getResources().getIdentifier(uri, null, getPackageName());
				
	// the xml buttons for the music panel
		musicPanelButtonList[3] = R.id.button_up_music_track;
		musicPanelButtonList[2] = R.id.button_music_play; 
		musicPanelButtonList[1] = R.id.button_music_skip;
		musicPanelButtonList[0] = R.id.button_down_music_track;
								
        chatButtonsFocused[3] = R.drawable.chat_speaker_focused;
        chatButtonsFocused[2] = R.drawable.chat_custom1_focused;
        chatButtonsFocused[1] = R.drawable.chat_custom2_focused;
        chatButtonsFocused[0] = R.drawable.chat_driving_focused;
        
        chatButtonsNormal[3] = R.drawable.chat_speaker_normal;
        chatButtonsNormal[2] = R.drawable.chat_custom1_normal;
        chatButtonsNormal[1] = R.drawable.chat_custom2_normal;
        chatButtonsNormal[0] = R.drawable.chat_driving_normal;                
 
	// the xml buttons for the chat panel
		chatPanelButtonList[3] = R.id.button_chat_speaker;
		chatPanelButtonList[2] = R.id.button_chat_custom1; 
		chatPanelButtonList[1] = R.id.button_chat_custom2;
		chatPanelButtonList[0] = R.id.button_chat_driving;
					        
    //    prefs = PreferenceManager.getDefaultSharedPreferences(this);
    //    prefs.registerOnSharedPreferenceChangeListener(this);
                           	
        player = MediaPlayer.create(this, R.raw.come_with_me);
		player.setLooping(false); // Set looping
		
		
        Button buttonNextMusic = (Button) findViewById(R.id.Button_next_music);       
        buttonNextMusic.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	Log.d("TAG", "Next Button Click" );
                // Get the ViewFlipper from the layout
            	viewFlipper.setInAnimation(slideRightIn);
                viewFlipper.setOutAnimation(slideRightOut);
                Log.d("TAG", "Fired Left Out" );
       //         Toast.makeText(SmartHudActivity.this, "Left Swipe", Toast.LENGTH_SHORT).show();
            	viewFlipper.showNext();
        }
        });
        
        Button buttonPreviousMusic = (Button) findViewById(R.id.Button_previous_music);
        buttonPreviousMusic.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	Log.d("TAG", "Previous Button Click" );
                // Get the ViewFlipper from the layout
            	viewFlipper.setInAnimation(slideLeftIn);
                viewFlipper.setOutAnimation(slideLeftOut);
                Log.d("TAG", "Fired Left Out" );
      //          Toast.makeText(SmartHudActivity.this, "Left Swipe", Toast.LENGTH_SHORT).show();
            	viewFlipper.showPrevious();
        }
        });
        
   // the perimeter up button on the music panel         
        Button buttonUpMusic = (Button) findViewById(R.id.Button_up_music);
        buttonUpMusic.setFocusable(true);
                
        buttonUpMusic.setOnClickListener(new View.OnClickListener() {        	
            public void onClick(View view) {
                  	          	                     	           	
         //   	Toast.makeText(Main.this, "Music Up Fired", Toast.LENGTH_SHORT).show(); 
            	
           // Change the current button to normal           	         	
            	Button oldMusicButtonid = (Button)findViewById(musicPanelButtonList[musicButton]);           	
            	oldMusicButtonid.setBackgroundDrawable(getResources().getDrawable(musicButtonsNormal[musicButton]));
       
            	musicButton = changeButton(3, musicButton, 1);  //button int            	
           // 	Toast.makeText(Main.this, "Changed Music Button to " + musicButton, Toast.LENGTH_LONG).show();
                       	            	
           //get the current button drawable res from array
            	            
           // 	currentMusicButtonRes = musicButtonsFocused[musicButton];    // musicButtons[1] = R.drawable.music_skip_focused;   
            	                
          // define the current button based on the increment             	
            	Button currentMusicButtonid = (Button)findViewById(musicPanelButtonList[musicButton]);            	            	                              	            	            	
            	currentMusicButtonid.setBackgroundDrawable(getResources().getDrawable(musicButtonsFocused[musicButton]));            	      
                    	            	           	           	         	          	
            	Log.d("TAG", "Fired Music Pane Up button" );                               
        }
        });
                 
   //perimeter down button     
        Button buttonDownMusic = (Button) findViewById(R.id.Button_down_music);
        buttonDownMusic.setFocusable(true);
        
        buttonDownMusic.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	          	
            	Button oldMusicButtonid = (Button)findViewById(musicPanelButtonList[musicButton]);           	
            	oldMusicButtonid.setBackgroundDrawable(getResources().getDrawable(musicButtonsNormal[musicButton]));           	                             
            	
           // 	Toast.makeText(Main.this, "Music Down Fired", Toast.LENGTH_SHORT).show();               	
            	musicButton = changeButton(3, musicButton, -1);              	
           // 	Toast.makeText(Main.this, "Change Music Button to " + musicButton, Toast.LENGTH_LONG).show();
    
           // 	currentMusicButtonRes = musicButtonsFocused[musicButton];    // musicButtons[1] = R.drawable.music_skip_focused;   
                
          // define the current button based on the increment             	
                 Button currentMusicButtonid = (Button)findViewById(musicPanelButtonList[musicButton]);                     	                      	           	                                  	            	            	
                 currentMusicButtonid.setBackgroundDrawable(getResources().getDrawable(musicButtonsFocused[musicButton]));
                      	           	            	           	           	         	          	
                 Log.d("TAG", "Fired Music Pane Down button" ); 
        }
        });
                
        // Chat Perimeter buttons     
                   
        Button buttonNextChat = (Button) findViewById(R.id.Button_next_chat);
        buttonNextChat.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	Log.d("TAG", "Next Button Click" );
                // Get the ViewFlipper from the layout
            	viewFlipper.setInAnimation(slideRightIn);
                viewFlipper.setOutAnimation(slideRightOut);
                Log.d("TAG", "Fired Left Out" );
       //         Toast.makeText(SmartHudActivity.this, "Right Region", Toast.LENGTH_SHORT).show();
            	viewFlipper.showNext();
        }
        });
        
        Button buttonPreviousChat = (Button) findViewById(R.id.Button_previous_chat);
        buttonPreviousChat.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	Log.d("TAG", "Previous Button Click" );
                // Get the ViewFlipper from the layout
            	viewFlipper.setInAnimation(slideLeftIn);
                viewFlipper.setOutAnimation(slideLeftOut);
                Log.d("TAG", "Fired Left Out" );
       //         Toast.makeText(SmartHudActivity.this, "Left Region", Toast.LENGTH_SHORT).show();
            	viewFlipper.showPrevious();
        }
        });
        
        Button buttonUpChat = (Button) findViewById(R.id.Button_up_chat);
        buttonUpChat.setOnClickListener(new View.OnClickListener() {
        	 public void onClick(View view) {
      	           	
                 //   	Toast.makeText(Main.this, "Music Up Fired", Toast.LENGTH_SHORT).show();                    	
                 // Change the current button to normal           	         	
                    	Button oldChatButtonid = (Button)findViewById(chatPanelButtonList[chatButton]);           	
                    	oldChatButtonid.setBackgroundDrawable(getResources().getDrawable(chatButtonsNormal[chatButton]));
               
                    	chatButton = changeButton(3, chatButton, 1);             	
                 //   	Toast.makeText(Main.this, "Changed Chat Button to " + chatButton, Toast.LENGTH_LONG).show();                               	            	
                                  	            
                 //   	currentChatButtonRes = chatButtonsFocused[chatButton];    // musicButtons[1] = R.drawable.music_skip_focused;                      	                
                 // define the current button based on the increment             	
                    	Button currentChatButtonid = (Button)findViewById(chatPanelButtonList[chatButton]);            	            	                              	            	            	
                    	currentChatButtonid.setBackgroundDrawable(getResources().getDrawable(chatButtonsFocused[chatButton]));            	      
                            	            	           	           	         	          	
                    	Log.d("TAG", "Fired Chat Pane Up button" );                               
                }
                });
        
        Button buttonDownChat = (Button) findViewById(R.id.Button_down_chat);
        buttonDownChat.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View view) {
  	           	
                //   Toast.makeText(Main.this, "Music Up Fired", Toast.LENGTH_SHORT).show();                    	
                // Change the current button to normal           	         	
                   	Button oldChatButtonid = (Button)findViewById(chatPanelButtonList[chatButton]);           	
                   	oldChatButtonid.setBackgroundDrawable(getResources().getDrawable(chatButtonsNormal[chatButton]));
                 
                   	chatButton = changeButton(3, chatButton, -1);             	
                //   	Toast.makeText(Main.this, "Changed Chat Button to " + chatButton, Toast.LENGTH_LONG).show();                              	            	
                                 	            
               //    	currentChatButtonRes = chatButtonsFocused[chatButton];    // musicButtons[1] = R.drawable.music_skip_focused;                     	                
                // define the current button based on the increment             	
                   	Button currentChatButtonid = (Button)findViewById(chatPanelButtonList[chatButton]);            	            	                              	            	            	
                   	currentChatButtonid.setBackgroundDrawable(getResources().getDrawable(chatButtonsFocused[chatButton]));            	      
                           	            	           	           	         	          	
                   	Log.d("TAG", "Fired Music Pane Up button" );                               
               }
               });
        
 // Navigation Panel Perimeter Buttons      
        
        Button buttonNextNav = (Button) findViewById(R.id.Button_next_nav);
        buttonNextNav.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	Log.d("TAG", "Next Button Click" );
                // Get the ViewFlipper from the layout
            	viewFlipper.setInAnimation(slideRightIn);
                viewFlipper.setOutAnimation(slideRightOut);
                Log.d("TAG", "Fired Left Out" );
       //         Toast.makeText(SmartHudActivity.this, "Right Region", Toast.LENGTH_SHORT).show();
            	viewFlipper.showNext();
        }
        });
        
        Button buttonPreviousNav = (Button) findViewById(R.id.Button_previous_nav);
        buttonPreviousNav.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	Log.d("TAG", "Previous Button Click" );
                // Get the ViewFlipper from the layout
            	viewFlipper.setInAnimation(slideLeftIn);
                viewFlipper.setOutAnimation(slideLeftOut);
                Log.d("TAG", "Fired Left Out" );
      //          Toast.makeText(SmartHudActivity.this, "Left Region", Toast.LENGTH_SHORT).show();
            	viewFlipper.showPrevious();
        }
        });
        
      
        
        
        
        
        
        
         
        final Button buttonUpMusicTrack = (Button)findViewById(R.id.button_up_music_track);
        findViewById(R.id.button_up_music_track).setFocusable(true);
        buttonUpMusicTrack.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Perform action on clicks
      //          Toast.makeText(SmartHudActivity.this, "Button Event", Toast.LENGTH_SHORT).show();
            }
        });
        
        final Button buttonDownMusicTrack = (Button)findViewById(R.id.button_down_music_track);
        buttonDownMusicTrack.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Perform action on clicks
     //           Toast.makeText(SmartHudActivity.this, "Button Event", Toast.LENGTH_SHORT).show();
            }
        });
             
        final Button buttonMusicPlay = (Button)findViewById(R.id.button_music_play);
        buttonMusicPlay.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Perform action on clicks
     //           Toast.makeText(SmartHudActivity.this, "Play/Pause Music Button Fired", Toast.LENGTH_SHORT).show();     
               
                if(player.isPlaying()) {
                	player.pause();
          		//  musicPause();       		
          		  buttonMusicPlay.setBackgroundDrawable(getResources().getDrawable( R.drawable.music_play_focused));
          		} else {
          			player.start();
          //		  musicPlay();
          		  buttonMusicPlay.setBackgroundDrawable(getResources().getDrawable( R.drawable.music_pause_focused));
          		}                                
            }
        });
                
        // Chat Buttons  
        //
        // Play TTS incoming text nessage
        // Driving - Standard message, repeat TTS, SMS
        // Custom 1 - Custom message, repeat TTS, SMS
        // Custom 2 - Custom message, repeat TTS, SMS     
            
        final Button buttonChatSpeaker = (Button)findViewById(R.id.button_chat_speaker); 
        buttonChatSpeaker.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Perform action on clicks
              // Toast.makeText(Main.this, "Button Event", Toast.LENGTH_SHORT).show();       
                  
            	
            	
            	String foo = getResources().getString(R.string.chat_response);
        //    	String foo = "Dinner at Steves around 7. Bring something yummy";
            	String text = foo.toString();
       //     	String text = inputText.getText().toString();
				if (text!=null && text.length()>0) {
		//			Toast.makeText(SmartHudActivity.this, "Saying: " + text, Toast.LENGTH_LONG).show();
					tts.speak(text, TextToSpeech.QUEUE_ADD, null);
				}                                   	
            }
        });
        
        Intent checkIntent = new Intent();
		checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
		startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);  
  
		
        final Button buttonChatCustom1 = (Button)findViewById(R.id.button_chat_custom1);
        buttonChatCustom1.setOnClickListener(new OnClickListener() {
        	 public void onClick(View v) {    
        		               		 
         // Toast.makeText(Main.this, "Button Event", Toast.LENGTH_SHORT).show();                                                     
             	String foo = getResources().getString(R.string.chat_custom_1);           	
             	String text = foo.toString();
             	String smsNumber = getResources().getString(R.string.chat_number);
             	
        //     	String text = inputText.getText().toString();
 				if (text!=null && text.length()>0) {
 		//			Toast.makeText(SmartHudActivity.this, "Saying: " + text, Toast.LENGTH_LONG).show();
 					tts.speak("Responsed to sender" + text, TextToSpeech.QUEUE_ADD, null);
 					 					
 					SmsManager sm = SmsManager.getDefault();						
				//	String number = "9787715233";
					sm.sendTextMessage(smsNumber, null, text, null, null);
 				}                                     	
             }
         });
             
        final Button buttonChatCustom2 = (Button)findViewById(R.id.button_chat_custom2);
        buttonChatCustom2.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
                // Perform action on clicks
              // Toast.makeText(Main.this, "Button Event", Toast.LENGTH_SHORT).show();       
                                             
        		String foo = getResources().getString(R.string.chat_custom_2);
            	String text = foo.toString();
            	String smsNumber = getResources().getString(R.string.chat_number);
            	
            	
       //     	String text = inputText.getText().toString();
				if (text!=null && text.length()>0) {
		//			Toast.makeText(SmartHudActivity.this, "Saying: " + text, Toast.LENGTH_LONG).show();
					tts.speak("Responsed to sender" + text, TextToSpeech.QUEUE_ADD, null);
					
					
					SmsManager sm = SmsManager.getDefault();						
				//	String number = "9787715233";
					sm.sendTextMessage(smsNumber, null, text, null, null);										
					
			//		Intent i = new Intent(Intent.ACTION_SEND);
			//		i.setType("text/plain");
			//		i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"sw_hansen@obliquevision.org"});
			//		i.putExtra(Intent.EXTRA_SUBJECT, "I'm Driving");
			//		i.putExtra(Intent.EXTRA_TEXT   , "I'm driving at the moment. I'll contact you when it's safe");
			//		try {
			//		    startActivity(Intent.createChooser(i, "Send mail..."));
			//		} catch (android.content.ActivityNotFoundException ex) {
				//	    Toast.makeText(MyActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
			//		}
											
				}                                     	
            }
        });
             
        final Button buttonChatDriving = (Button)findViewById(R.id.button_chat_driving);
        buttonChatDriving.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
                // Perform action on clicks
              // Toast.makeText(Main.this, "Button Event", Toast.LENGTH_SHORT).show();       
                                             
        		String foo = getResources().getString(R.string.chat_driving);
            	String text = foo.toString();
            	String smsNumber = getResources().getString(R.string.chat_number);
     
				if (text!=null && text.length()>0) {
		//			Toast.makeText(SmartHudActivity.this, "Saying: " + text, Toast.LENGTH_LONG).show();
					tts.speak("Responsed to sender" + text, TextToSpeech.QUEUE_ADD, null);
					
					SmsManager sm = SmsManager.getDefault();						
			//		String number = "9787715233";
					sm.sendTextMessage(smsNumber, null, text, null, null);
				}                                      	
            }
        });
                
        gestureDetector = new GestureDetector(new MyGestureDetector());
        gestureListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (gestureDetector.onTouchEvent(event)) {              
                	return true;                   
                }
                return false;
            }
        };
    }
    
    
    
    
    
    
    
    private int getIdentifier(int musicUpFocused) {
		// TODO Auto-generated method stub
		return 0;
	}

	private int getDrawable(int musicPlayNormal) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	
	
	class MyGestureDetector extends SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {         	
            	
                if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH_HORIZ){
                	
        //possible vertical swipe	
                	
           //    if(e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {               
           // move the button up               	
           //    }  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {                	
           // move the button down                	
           //     }
                	
                	Log.d("TAG", "invalid swipe" );
                	return false;
                    
                }
                // right to left swipe
                if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                	viewFlipper.setInAnimation(slideLeftIn);
                    viewFlipper.setOutAnimation(slideLeftOut);
                    Log.d("TAG", "Fired Left Out" );
        //            Toast.makeText(SmartHudActivity.this, "Left Swipe", Toast.LENGTH_SHORT).show();
                	viewFlipper.showNext();
                }  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                	viewFlipper.setInAnimation(slideRightIn);
                    viewFlipper.setOutAnimation(slideRightOut);
       //             Toast.makeText(SmartHudActivity.this, "Right Swipe", Toast.LENGTH_SHORT).show();
                    Log.d("TAG", "Fired Right Out" );
                	viewFlipper.showPrevious();
                }
                
                
            } catch (Exception e) {
                // nothing
            }
            return false;
        }
    }
    
    
    
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (gestureDetector.onTouchEvent(event))
        {
        	//Log.d("TAG", "onTouchEvent is TRUE" );
	        return true;     
        }
        
	    else
	    {
	    	//Log.d("TAG", "onTouchEvent is False" );
	    	return false;
	    }
    }
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == MY_DATA_CHECK_CODE) {
			if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
				// success, create the TTS instance
				tts = new TextToSpeech(this, this);
			} 
			else {
				// missing data, install it
				Intent installIntent = new Intent();
				installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
				startActivity(installIntent);
			}
		}

	}
      
    
 
    
// Initialize the TTS    
    
    @Override
	public void onInit(int status) {		
		if (status == TextToSpeech.SUCCESS) {
			Toast.makeText(SmartHudActivity.this, 
					"Text-To-Speech engine is initialized", Toast.LENGTH_LONG).show();
		}
		else if (status == TextToSpeech.ERROR) {
			Toast.makeText(SmartHudActivity.this, 
					"Error occurred while initializing Text-To-Speech engine", Toast.LENGTH_LONG).show();
		}
	}
    
    
    
// Change the Panel button number based on a button change event (swipe, button)  
    public int changeButton(int numofbuttons, int current, int inc){    	
//   buttons are integers 0 to n    	    	
    	int minbutton = 0;
    	int newbutton = current + inc;
    	
    	if (newbutton > numofbuttons) newbutton = numofbuttons;
    	if (newbutton < minbutton) newbutton = minbutton;   	
    	return newbutton;    	 	
    }

	
    
    
   
         
}
