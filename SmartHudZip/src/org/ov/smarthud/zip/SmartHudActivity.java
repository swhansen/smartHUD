package org.ov.smarthud.zip;

// smartHUD Zipcar prototype
// version  0.2
//
// Added Extend Reservations Panel
//
//
//
//

import java.util.List;

import com.google.android.maps.MapActivity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.telephony.SmsManager;
import android.text.TextWatcher;
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


import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

public class SmartHudActivity extends Activity implements OnInitListener  {
	
	public int currentPanelIndex;                      // index of the current panel
	public int[] currentButtonOnPanel = new int[6];    // current button on each panel	
	public int[] buttonsOnPanel = {3, 3, 3, 3, 3, 3};  // number of buttons on each panel
	

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
//	private int musicButton = 2;	
	private int[] musicButtonsFocused = new int[4];
	private int[] musicButtonsNormal = new int[4];		
	public int[] musicPanelButtonList = new int[4];
	
	public int currentChatButtonRes;
//	private int chatButton = 3;	
	private int[] chatButtonsFocused = new int[4];
	private int[] chatButtonsNormal = new int[4];		
	public int[] chatPanelButtonList = new int[4];
	
	
	public int currentZipButtonRes;
	private int zipButton = 3;	
	private int[] zipButtonsFocused = new int[4];
	private int[] zipButtonsNormal = new int[4];		
	public int[] zipPanelButtonList = new int[4];
	
	public int currentZipExtendButtonRes;
	private int zipExtendButton = 3;	
	private int[] zipExtendButtonsFocused = new int[4];
	private int[] zipExtendButtonsNormal = new int[4];		
	public int[] zipExtendPanelButtonList = new int[4];
	
	public int currentPanel;

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
 
        
     // set the default panel buttons on launch   
    	currentButtonOnPanel[0] = 2;
    	currentButtonOnPanel[2] = 3;
    	
              
        musicButtonsFocused[3] = R.drawable.music_up_focused;
        musicButtonsFocused[2] = R.drawable.music_play_focused;
        musicButtonsFocused[1] = R.drawable.music_skip_focused;
        musicButtonsFocused[0] = R.drawable.music_down_focused;
        
        musicButtonsNormal[3] = R.drawable.music_up_normal;
        musicButtonsNormal[2] = R.drawable.music_play_normal;
        musicButtonsNormal[1] = R.drawable.music_skip_normal;
        musicButtonsNormal[0] = R.drawable.music_down_normal;                
        
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
            
     	chatPanelButtonList[3] = R.id.button_chat_speaker;
     	chatPanelButtonList[2] = R.id.button_chat_custom1; 
     	chatPanelButtonList[1] = R.id.button_chat_custom2;
     	chatPanelButtonList[0] = R.id.button_chat_driving;
 
	// the xml buttons for the Zipcar panel
     		
		zipPanelButtonList[3] = R.id.button_zip_speaker;
		zipPanelButtonList[2] = R.id.button_zip_custom1; 
		zipPanelButtonList[1] = R.id.button_zip_custom2;
		zipPanelButtonList[0] = R.id.button_zip_driving;
				
		zipButtonsFocused[3] = R.drawable.zipcar_focused;
        zipButtonsFocused[2] = R.drawable.contact_focused;;
        zipButtonsFocused[1] = R.drawable.extend_focused;
        zipButtonsFocused[0] = R.drawable.chat_driving_focused;
        
        zipButtonsNormal[3] = R.drawable.zipcar_normal;
        zipButtonsNormal[2] = R.drawable.contact_normal;
        zipButtonsNormal[1] = R.drawable.extend_normal;
        zipButtonsNormal[0] = R.drawable.chat_driving_normal;                
 	
     // the xml buttons for the Zipcar Extend panel
        
     	zipExtendPanelButtonList[3] = R.id.button_zipextend_1;
     	zipExtendPanelButtonList[2] = R.id.button_zipextend_2; 
     	zipExtendPanelButtonList[1] = R.id.button_zipextend_3;
     	zipExtendPanelButtonList[0] = R.id.button_zipextend_call;
     		     		
     	zipExtendButtonsFocused[3] = R.drawable.zipcar_30_focused;
     	zipExtendButtonsFocused[2] = R.drawable.zipcar_60_focused;
     	zipExtendButtonsFocused[1] = R.drawable.zipcar_90_focused;
     	zipExtendButtonsFocused[0] = R.drawable.contact_focused;
             
     	zipExtendButtonsNormal[3] = R.drawable.zipcar_30_normal;
     	zipExtendButtonsNormal[2] = R.drawable.zipcar_60_normal;
     	zipExtendButtonsNormal[1] = R.drawable.zipcar_90_normal;
     	zipExtendButtonsNormal[0] = R.drawable.contact_normal;      
				
					        
    //    prefs = PreferenceManager.getDefaultSharedPreferences(this);
    //    prefs.registerOnSharedPreferenceChangeListener(this);
                           	
        player = MediaPlayer.create(this, R.raw.come_with_me);
		player.setLooping(false); // Set looping
		
		   // the perimeter up button on the music panel 
		
        Button buttonNextMusic = (Button) findViewById(R.id.Button_next_music);       
        buttonNextMusic.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	Log.d("TAG", "Next Button Click" );   	
            	viewFlipper.setInAnimation(slideRightIn);
                viewFlipper.setOutAnimation(slideRightOut);               
            	viewFlipper.showNext();
        }
        });
        
        Button buttonPreviousMusic = (Button) findViewById(R.id.Button_previous_music);
        buttonPreviousMusic.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {            	               
            	viewFlipper.setInAnimation(slideLeftIn);
                viewFlipper.setOutAnimation(slideLeftOut);              
            	viewFlipper.showPrevious();
        }
        });
                
        Button buttonUpMusic = (Button) findViewById(R.id.Button_up_music);
        buttonUpMusic.setFocusable(true);               
        buttonUpMusic.setOnClickListener(new View.OnClickListener() {        	
            public void onClick(View view) {             	
            	currentPanelIndex=viewFlipper.getDisplayedChild();
            	
            	Button oldMusicButtonid = (Button)findViewById(musicPanelButtonList[currentButtonOnPanel[currentPanelIndex]]);           	
            	oldMusicButtonid.setBackgroundDrawable(getResources().getDrawable(musicButtonsNormal[currentButtonOnPanel[currentPanelIndex]])); 
            	
            	currentButtonOnPanel[currentPanelIndex] = changeButtonTest(buttonsOnPanel[currentPanelIndex], currentButtonOnPanel[currentPanelIndex], 1, currentPanelIndex);  
            	
            	Button currentMusicButtonid = (Button)findViewById(musicPanelButtonList[currentButtonOnPanel[currentPanelIndex]]);            	            	                              	            	            	
            	currentMusicButtonid.setBackgroundDrawable(getResources().getDrawable(musicButtonsFocused[currentButtonOnPanel[currentPanelIndex]]));            	                                               
        }
        });
                     
        Button buttonDownMusic = (Button) findViewById(R.id.Button_down_music);
        buttonDownMusic.setFocusable(true);        
        buttonDownMusic.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {         
            	     	
                currentPanelIndex=viewFlipper.getDisplayedChild();
                            	
            	Button oldMusicButtonid = (Button)findViewById(musicPanelButtonList[currentButtonOnPanel[currentPanelIndex]]);           	
            	oldMusicButtonid.setBackgroundDrawable(getResources().getDrawable(musicButtonsNormal[currentButtonOnPanel[currentPanelIndex]])); 
            	            	
            //	musicButton = changeButtonTest(3, musicButton, -1, currentPanelIndex); 
            //	currentButtonOnPanel[currentPanelIndex] = musicButton;
            	   	
            	currentButtonOnPanel[currentPanelIndex] = changeButtonTest(buttonsOnPanel[currentPanelIndex], currentButtonOnPanel[currentPanelIndex], -1, currentPanelIndex);
            	
                Button currentMusicButtonid = (Button)findViewById(musicPanelButtonList[currentButtonOnPanel[currentPanelIndex]]);                     	                      	           	                                  	            	            	
                currentMusicButtonid.setBackgroundDrawable(getResources().getDrawable(musicButtonsFocused[currentButtonOnPanel[currentPanelIndex]]));
                 
            //     currentButtonOnPanel[currentPanelIndex] = musicButton;
             //    Toast.makeText(SmartHudActivity.this, "Panel Index: " + currentPanelIndex + "Button:" + currentButtonOnPanel[currentPanelIndex], Toast.LENGTH_LONG).show();
                 
        }
        });
                
        // Chat Perimeter buttons     
                   
        Button buttonNextChat = (Button) findViewById(R.id.Button_next_chat);
        buttonNextChat.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	viewFlipper.setInAnimation(slideRightIn);
                viewFlipper.setOutAnimation(slideRightOut);
            	viewFlipper.showNext();
        }
        });
        
        Button buttonPreviousChat = (Button) findViewById(R.id.Button_previous_chat);
        buttonPreviousChat.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	viewFlipper.setInAnimation(slideLeftIn);
                viewFlipper.setOutAnimation(slideLeftOut);
            	viewFlipper.showPrevious();
        }
        });
        
        Button buttonUpChat = (Button) findViewById(R.id.Button_up_chat);
        buttonUpChat.setOnClickListener(new View.OnClickListener() {
        	 public void onClick(View view) {     
        		 
        		 currentPanelIndex=viewFlipper.getDisplayedChild();
        		 
                    	Button oldChatButtonid = (Button)findViewById(chatPanelButtonList[currentButtonOnPanel[currentPanelIndex]]);          	
                    	oldChatButtonid.setBackgroundDrawable(getResources().getDrawable(chatButtonsNormal[currentButtonOnPanel[currentPanelIndex]]));    
                    	
                    //	chatButton = changeButton(3, chatButton, 1);   
                    	currentButtonOnPanel[currentPanelIndex] = changeButtonTest(buttonsOnPanel[currentPanelIndex], currentButtonOnPanel[currentPanelIndex], 1, currentPanelIndex);
                    	
                    	Button currentChatButtonid = (Button)findViewById(chatPanelButtonList[currentButtonOnPanel[currentPanelIndex]]);        	            	                              	            	            	
                    	currentChatButtonid.setBackgroundDrawable(getResources().getDrawable(chatButtonsFocused[currentButtonOnPanel[currentPanelIndex]]));         	                                    
                }
                });
        
        Button buttonDownChat = (Button) findViewById(R.id.Button_down_chat);
        buttonDownChat.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View view) {  	
        		
        		 currentPanelIndex=viewFlipper.getDisplayedChild();
                   	Button oldChatButtonid = (Button)findViewById(chatPanelButtonList[currentButtonOnPanel[currentPanelIndex]]);       	
                   	oldChatButtonid.setBackgroundDrawable(getResources().getDrawable(chatButtonsNormal[currentButtonOnPanel[currentPanelIndex]]));  
                   	
                  // 	chatButton = changeButton(3, chatButton, -1); 
                   	currentButtonOnPanel[currentPanelIndex] = changeButtonTest(buttonsOnPanel[currentPanelIndex], currentButtonOnPanel[currentPanelIndex], -1, currentPanelIndex);
                   	
                   	Button currentChatButtonid = (Button)findViewById(chatPanelButtonList[currentButtonOnPanel[currentPanelIndex]]);         	            	                              	            	            	
                   	currentChatButtonid.setBackgroundDrawable(getResources().getDrawable(chatButtonsFocused[currentButtonOnPanel[currentPanelIndex]]));            	                                  
               }
               });
        
     // Zip Perimeter buttons     
        
        Button buttonNextZip = (Button) findViewById(R.id.Button_next_zip);
        buttonNextZip.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {         
            	viewFlipper.setInAnimation(slideRightIn);
                viewFlipper.setOutAnimation(slideRightOut);
            	viewFlipper.showNext();
        }
        });
        
        Button buttonPreviousZip = (Button) findViewById(R.id.Button_previous_zip);
        buttonPreviousZip.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	viewFlipper.setInAnimation(slideLeftIn);
                viewFlipper.setOutAnimation(slideLeftOut);      
            	viewFlipper.showPrevious();
        }
        });
        
        Button buttonUpZip = (Button) findViewById(R.id.Button_up_zip);
        buttonUpZip.setOnClickListener(new View.OnClickListener() {
        	 public void onClick(View view) {      	           	     	         	
                    	Button oldZipButtonid = (Button)findViewById(zipPanelButtonList[zipButton]);           	
                    	oldZipButtonid.setBackgroundDrawable(getResources().getDrawable(zipButtonsNormal[zipButton]));               
                    	zipButton = changeButton(3, zipButton, 1);             	                                             	            	                                  	            
                 //   	currentChatButtonRes = chatButtonsFocused[chatButton];    // musicButtons[1] = R.drawable.music_skip_focused;                      	                                           	
                    	Button currentZipButtonid = (Button)findViewById(zipPanelButtonList[zipButton]);            	            	                              	            	            	
                    	currentZipButtonid.setBackgroundDrawable(getResources().getDrawable(zipButtonsFocused[zipButton]));            	                                   
                }
                });
        
        Button buttonDownZip = (Button) findViewById(R.id.Button_down_zip);
        buttonDownZip.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View view) {        	         	
                   	Button oldZipButtonid = (Button)findViewById(zipPanelButtonList[zipButton]);           	
                   	oldZipButtonid.setBackgroundDrawable(getResources().getDrawable(zipButtonsNormal[zipButton]));                 
                   	zipButton = changeButton(3, zipButton, -1);             	                                          	            	                                 	                            	                                           	
                   	Button currentZipButtonid = (Button)findViewById(zipPanelButtonList[zipButton]);            	            	                              	            	            	
                   	currentZipButtonid.setBackgroundDrawable(getResources().getDrawable(zipButtonsFocused[zipButton]));            	                                   
               }
               });
        
      
        
// Zip Extend Perimeter buttons     
        
        Button buttonNextZipExtend = (Button) findViewById(R.id.Button_next_zipe);
        buttonNextZipExtend.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	viewFlipper.setInAnimation(slideRightIn);
                viewFlipper.setOutAnimation(slideRightOut);
            	viewFlipper.showNext();
        }
        });
        
        Button buttonPreviousZipExtend = (Button) findViewById(R.id.Button_previous_zipe);
        buttonPreviousZipExtend.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	viewFlipper.setInAnimation(slideLeftIn);
                viewFlipper.setOutAnimation(slideLeftOut);
            	viewFlipper.showPrevious();
        }
        });      
        
        
        Button buttonUpZipExtend = (Button) findViewById(R.id.Button_up_zipe);
        buttonUpZipExtend.setOnClickListener(new View.OnClickListener() {
        	 public void onClick(View view) {        	         	
                    	Button oldZipExtendButtonid = (Button)findViewById(zipExtendPanelButtonList[zipExtendButton]);           	
                    	oldZipExtendButtonid.setBackgroundDrawable(getResources().getDrawable(zipExtendButtonsNormal[zipExtendButton]));               
                    	zipExtendButton = changeButton(3, zipExtendButton, 1);             	         	
                    	Button currentZipExtendButtonid = (Button)findViewById(zipExtendPanelButtonList[zipExtendButton]);            	            	                              	            	            	
                    	currentZipExtendButtonid.setBackgroundDrawable(getResources().getDrawable(zipExtendButtonsFocused[zipExtendButton]));            	                                 
                }
                });
        
        Button buttonDownZipExtend = (Button) findViewById(R.id.Button_down_zipe);
        buttonDownZipExtend.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View view) {
                   	Button oldZipExtendButtonid = (Button)findViewById(zipExtendPanelButtonList[zipExtendButton]);           	
                   	oldZipExtendButtonid.setBackgroundDrawable(getResources().getDrawable(zipExtendButtonsNormal[zipExtendButton]));                 
                   	zipExtendButton = changeButton(3, zipExtendButton, -1);             	                                             	            	        	
                   	Button currentZipExtendButtonid = (Button)findViewById(zipExtendPanelButtonList[zipExtendButton]);            	            	                              	            	            	
                   	currentZipExtendButtonid.setBackgroundDrawable(getResources().getDrawable(zipExtendButtonsFocused[zipExtendButton]));            	                                    
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
            	viewFlipper.setInAnimation(slideLeftIn);
                viewFlipper.setOutAnimation(slideLeftOut);
                Log.d("TAG", "Fired Left Out" );
            	viewFlipper.showPrevious();
        }
        });
        
       
         
        final Button buttonUpMusicTrack = (Button)findViewById(R.id.button_up_music_track);
        findViewById(R.id.button_up_music_track).setFocusable(true);
        buttonUpMusicTrack.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            }
        });
        
        final Button buttonDownMusicTrack = (Button)findViewById(R.id.button_down_music_track);
        buttonDownMusicTrack.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            }
        });
             
        final Button buttonMusicPlay = (Button)findViewById(R.id.button_music_play);
        buttonMusicPlay.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if(player.isPlaying()) {
                	player.pause();          		     		
          		  buttonMusicPlay.setBackgroundDrawable(getResources().getDrawable( R.drawable.music_play_focused));
          		} else {
          			player.start();
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
            	String foo = getResources().getString(R.string.chat_response);
        //    	String foo = "Dinner at Steves around 7. Bring something yummy";
            	String text = foo.toString();
       //     	String text = inputText.getText().toString();
				if (text!=null && text.length()>0) {		
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
             	String foo = getResources().getString(R.string.chat_custom_1);           	
             	String text = foo.toString();
         //    	String smsNumber = getResources().getString(R.string.chat_number);
             	
        //     	String text = inputText.getText().toString();
 				if (text!=null && text.length()>0) {
 					tts.speak("Responsed to sender" + text, TextToSpeech.QUEUE_ADD, null);
 					 					
 			//		SmsManager sm = SmsManager.getDefault();						
				//	String number = "9787715233";
				//	sm.sendTextMessage(smsNumber, null, text, null, null);
 				}                                     	
             }
        	         	
         });
             
        final Button buttonChatCustom2 = (Button)findViewById(R.id.button_chat_custom2);
        buttonChatCustom2.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {                                                  
        		String foo = getResources().getString(R.string.chat_custom_2);
            	String text = foo.toString();
            	String smsNumber = getResources().getString(R.string.chat_number);
            	         	
       //     	String text = inputText.getText().toString();
				if (text!=null && text.length()>0) {
					tts.speak("Responsed to sender" + text, TextToSpeech.QUEUE_ADD, null);
					
					
				//	SmsManager sm = SmsManager.getDefault();						
				//	String number = "9787715233";
				//	sm.sendTextMessage(smsNumber, null, text, null, null);										
					
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
          
        		String foo = getResources().getString(R.string.chat_driving);
            	String text = foo.toString();
         //   	String smsNumber = getResources().getString(R.string.chat_number);
     
		//		if (text!=null && text.length()>0) {
		//			Toast.makeText(SmartHudActivity.this, "Saying: " + text, Toast.LENGTH_LONG).show();
					tts.speak("Responsed to sender" + text, TextToSpeech.QUEUE_ADD, null);
					
			//		SmsManager sm = SmsManager.getDefault();						
			//		String number = "9787715233";
			//		sm.sendTextMessage(smsNumber, null, text, null, null);
	//			}                                      	
            }
        });
        
        
        
        
        // Zip Buttons  
        //
        // Play TTS incoming text nessage
        // Call Zip
        // Extend
        // Get me there 
            
        final Button buttonZipSpeaker = (Button)findViewById(R.id.button_zip_speaker); 
        buttonZipSpeaker.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
           	
            	String foo = getResources().getString(R.string.zip_response);
        //    	String foo = "Dinner at Steves around 7. Bring something yummy";
            	String text = foo.toString();
       //     	String text = inputText.getText().toString();
				if (text!=null && text.length()>0) {
		//			Toast.makeText(SmartHudActivity.this, "Saying: " + text, Toast.LENGTH_LONG).show();
					tts.speak(text, TextToSpeech.QUEUE_ADD, null);
				}                                   	
            }
        });
        
   //     Intent checkIntent = new Intent();
	//	checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
	//	startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);  
  		
        final Button buttonZipCustom1 = (Button)findViewById(R.id.button_zip_custom1);
        buttonZipCustom1.setOnClickListener(new OnClickListener() {
        	 public void onClick(View v) {           		               		 
        	//	 try {       			 
        	//		    String url = "tel:9784483152";
        	//		    Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse(url));

        		      //  Intent callIntent = new Intent(Intent.ACTION_CALL);
        		     //   callIntent.setData(Uri.parse("tel:9784483152"));
        	//	       startActivity(callIntent);
        	//	    } catch (ActivityNotFoundException e) {
        		//        Log.e("helloandroid dialing example", "Call failed", e);
        	//	    }
        		 
   		 
        		 // Toast.makeText(Main.this, "Button Event", Toast.LENGTH_SHORT).show();                                                     
             	String foo = getResources().getString(R.string.zip_contact);           	
             	String text = foo.toString();
          //   	String smsNumber = getResources().getString(R.string.chat_number);
             	
           //  	String text = inputText.getText().toString();
 				if (text!=null && text.length()>0) {
 				//	Toast.makeText(SmartHudActivity.this, "Saying: " + text, Toast.LENGTH_LONG).show();
 					tts.speak( text, TextToSpeech.QUEUE_ADD, null);
 					 					
 		//			SmsManager sm = SmsManager.getDefault();						
				//	String number = "9787715233";
		//			sm.sendTextMessage(smsNumber, null, text, null, null);
 				}                                     	
             }
         });
             
        final Button buttonZipCustom2 = (Button)findViewById(R.id.button_zip_custom2);
            buttonZipCustom2.setOnClickListener(new OnClickListener() {
        	   public void onClick(View v) {
                // Perform action on clicks
                // Toast.makeText(Main.this, "Button Event", Toast.LENGTH_SHORT).show();                                   		
        	    // Toast.makeText(Main.this, "Button Event", Toast.LENGTH_SHORT).show();                      
        	    //   viewFlipper.setDisplayedChild(viewFlipper.indexOfChild(findViewById(R.id.nav)));        		
        	    //Switch to the Nav panel (zero based)  
        	    		
        	        	//	currentPanel=4;
        		        //  currentPanel = viewFlipper.getCurrentView().getId();
        		        
        	        	//	Toast.makeText(SmartHudActivity.this, "Current Panel Panel " + currentPanel, Toast.LENGTH_LONG).show();  
        	                viewFlipper.setDisplayedChild(4);
        	                
        	        
        	                                                       
        	        //		String foo = getResources().getString(R.string.chat_driving);
        	         //   	String text = foo.toString();
        	        //    	String smsNumber = getResources().getString(R.string.chat_number);
        	     
        			//		if (text!=null && text.length()>0) {
        			//			Toast.makeText(SmartHudActivity.this, "Saying: " + text, Toast.LENGTH_LONG).show();
        			//			tts.speak("Responsed to sender" + text, TextToSpeech.QUEUE_ADD, null);
        						
        			//			SmsManager sm = SmsManager.getDefault();						
        				//		String number = "9787715233";
        			//			sm.sendTextMessage(smsNumber, null, text, null, null);
        				//	}                                      	
        	            }
        	        });
             
        final Button buttonZipDriving = (Button)findViewById(R.id.button_zip_driving);
        buttonZipDriving.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {                		
                viewFlipper.setDisplayedChild(1);         		
            }
        });
              
        
  // Zipcar Extend Reservations Buttons      
                
        final Button buttonZipExtend1 = (Button)findViewById(R.id.button_zipextend_1);
        buttonZipExtend1.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {            
        		tts.speak("Reservation extended 30 minutes", TextToSpeech.QUEUE_ADD, null);
                viewFlipper.setDisplayedChild(3);                           
            }
        });
                       
        final Button buttonZipExtend2 = (Button)findViewById(R.id.button_zipextend_2);
        buttonZipExtend2.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {  
        		tts.speak("Reservation extended 60 minutes", TextToSpeech.QUEUE_ADD, null);
                viewFlipper.setDisplayedChild(3);                                                                                                  	
            }
        });
        
        final Button buttonZipExtend3 = (Button)findViewById(R.id.button_zipextend_3);
        buttonZipExtend3.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {  
        		tts.speak("Reservation extended 90 minutes", TextToSpeech.QUEUE_ADD, null);
                viewFlipper.setDisplayedChild(3);                                                                                                  	
            }
        });
        
        
        final Button buttonZipExtendCall = (Button)findViewById(R.id.button_zipextend_call);
        buttonZipExtendCall.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {                		
        		tts.speak("Call Zipcar", TextToSpeech.QUEUE_ADD, null);                                                                                              	
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
    
 // Change the Panel button number based on a button change event (swipe, button)  
    public int changeButtonTest(int numofbuttons, int current, int inc, int panel){    	
//   buttons are integers 0 to n    	    	
    	int minbutton = 0;
    	int newbutton = current + inc;
    	
    	if (newbutton > numofbuttons) newbutton = numofbuttons;
    	if (newbutton < minbutton) newbutton = minbutton;   
    	
    	currentButtonOnPanel[panel] = newbutton;
        Toast.makeText(SmartHudActivity.this, "Index:" + currentPanelIndex + "  Button:" + currentButtonOnPanel[currentPanelIndex], Toast.LENGTH_LONG).show();
    	return newbutton;    	 	
    }

	
    public class phonecalls extends Activity {
        /** Called when the activity is first created. */
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.main);
            call();
        }
     
    private void call() {
        try {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:9784483152"));
            startActivity(callIntent);
        } catch (ActivityNotFoundException activityException) {
   //          Log.e("helloandroid dialing example", "Call failed", e);
        }
    }
     
    }
    
   
         
}
