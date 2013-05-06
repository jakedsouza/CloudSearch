package com.cloudsearch.oauth;
import java.io.File;
import java.io.PrintWriter;
import java.util.List;
import java.util.Scanner;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.RequestTokenPair;
import com.dropbox.client2.session.Session.AccessType;
import com.dropbox.client2.session.WebAuthSession;


public class dropTest {

	private static final String APP_KEY = "b6okk9w844ztzs1";
    private static final String APP_SECRET = "zqavxrc252to9ht";
    final static private AccessType ACCESS_TYPE = AccessType.DROPBOX;
    private static DropboxAPI<WebAuthSession> mDBApi;
    
    public static void main(String[] args) throws Exception{
    	AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
        WebAuthSession session = new WebAuthSession(appKeys, ACCESS_TYPE);
        
     // Initialize DropboxAPI object
        mDBApi = new DropboxAPI<WebAuthSession>(session);
        DropboxAPI.Entry entry = new DropboxAPI.Entry();
        // Get ready for user input
        Scanner input = new Scanner(System.in);

        // Open file that stores tokens, MUST exist as a blank file
        File tokensFile = new File("TOKENS");

        System.out.println("Enter 'a' to authenticate, or 't' to test reauthentication: ");
        String command = input.next();

        if(command.equals("a")){

            try {

                // Present user with URL to allow app access to Dropbox account on
                System.out.println("Please go to this URL and hit \"Allow\": " + mDBApi.getSession().getAuthInfo().url);
                AccessTokenPair tokenPair = mDBApi.getSession().getAccessTokenPair();

                // Wait for user to Allow app in browser
                System.out.println("Finished allowing?  Enter 'next' if so: ");
                if(input.next().equals("next")){
                    RequestTokenPair tokens = new RequestTokenPair(tokenPair.key, tokenPair.secret);
                    mDBApi.getSession().retrieveWebAccessToken(tokens);
                    PrintWriter tokenWriter = new PrintWriter(tokensFile);
                    tokenWriter.println(session.getAccessTokenPair().key);
                    tokenWriter.println(session.getAccessTokenPair().secret);
                    tokenWriter.close();
                    System.out.println("Authentication Successful!");

                }
                List<Entry> entries = mDBApi.search("",".pdf", 1000,true);
                for (Entry entry2 : entries) {
					System.out.println(entry2.fileName());
				}
            
            
            } catch (DropboxException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        else if(command.equals("t")){

            Scanner tokenScanner = new Scanner(tokensFile);       // Initiate Scanner to read tokens from TOKEN file
            String ACCESS_TOKEN_KEY = tokenScanner.nextLine();    // Read key
            String ACCESS_TOKEN_SECRET = tokenScanner.nextLine(); // Read secret
            tokenScanner.close(); //Close Scanner 

            //Re-auth
            AccessTokenPair reAuthTokens = new AccessTokenPair(ACCESS_TOKEN_KEY, ACCESS_TOKEN_SECRET);
            mDBApi.getSession().setAccessTokenPair(reAuthTokens);
            System.out.println("Re-authentication Sucessful!");

            //Run test command
            System.out.println("Hello there, " + mDBApi.accountInfo().displayName);

        }
        
    }
	
	

}