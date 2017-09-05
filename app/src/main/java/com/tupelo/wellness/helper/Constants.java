package com.tupelo.wellness.helper;


/**
 * Created by Abhishek Singh Arya on 11-09-2015.
 */

public class Constants {

    /*---------------TEST SERVER CONSTANTS---------------*/

    /*---------------MYMO CONSTANTS---------------*/
    /*public static final String BASE_URL = "https://challengeyourself.cpc.gov.ae/?q=services/json";

    public static final String API_KEY = "1a5319ad7f07c97c31963e0933aafe9a";

    public static final String DOMAIN = "https://challengeyourself.cpc.gov.ae";
    */


    public static final String MYMO_TYPE = "101";


    //public static final String MYMO_SERIAL = "mymo_serial";

    /*---------------PADOMETER CONSTANTS---------------*/

    /*---------------FITBIT CONSTANTS---------------*/
    //public static final String FITBIT_CLIENT_ID = "229WH9"; //original
    /*---------------JAWBONE CONSTANTS---------------*/
    public enum JawboneAuthScope {
        BASIC_READ,
        EXTENDED_READ,
        LOCATION_READ,
        FRIENDS_READ,
        MOOD_READ,
        MOOD_WRITE,
        MOVE_READ,
        MOVE_WRITE,
        SLEEP_READ,
        SLEEP_WRITE,
        MEAL_READ,
        MEAL_WRITE,
        WEIGHT_READ,
        WEIGHT_WRITE,
        CARDIAC_READ,
        CARDIAC_WRITE,
        GENERIC_EVENT_READ,
        GENERIC_EVENT_WRITE,
        ALL;
    }

    /*---------------PARSE DUMMY CONSTANTS---------------*/

    /*---------------PARSE CONSTANTS---------------

    public static final String PARSE_APPLICATION_ID = "wWemKpsDTZvogfkQa8GzAFbROO7xcbvRLy6cCEMp";
    public static final String PARSE_CLIENT_KEY = "7DF2E12lsR6ACh47FtdcLbggjfHd2OAneZJ0x4Yw";*/



    //pubnub related chat constants
    //development
    /*
    public static final String PUBLISH_KEY   = "pub-c-7903f61c-c97b-4cbf-aa75-1f76f93fceb1";
    public static final String SUBSCRIBE_KEY = "sub-c-b8e4634e-3c64-11e6-9c7c-0619f8945a4f";*/


/*
* backup
* Publish Keypub-c-7903f61c-c97b-4cbf-aa75-1f76f93fceb1
* Subscribe Keysub-c-b8e4634e-3c64-11e6-9c7c-0619f8945a4f
*
* */
/*shah
    public static final String PUBLISH_KEY   = "pub-c-1706e931-e680-4759-838b-1e5476b4f355";
    public static final String SUBSCRIBE_KEY = "sub-c-81727c0c-3cff-11e6-82fe-0619f8945a4f";
*/
//public static final String PUBLISH_KEY   = "pub-c-7903f61c-c97b-4cbf-aa75-1f76f93fceb1";
//    public static final String SUBSCRIBE_KEY = "sub-c-b8e4634e-3c64-11e6-9c7c-0619f8945a4f";
/*

    public static final String PUBLISH_KEY   = "pub-c-7903f61c-c97b-4cbf-aa75-1f76f93fceb1";
    public static final String SUBSCRIBE_KEY = "sub-c-b8e4634e-3c64-11e6-9c7c-0619f8945a4f";

*/
    public static final String PUBLISH_KEY   = "pub-c-1706e931-e680-4759-838b-1e5476b4f355";
    public static final String SUBSCRIBE_KEY = "sub-c-81727c0c-3cff-11e6-82fe-0619f8945a4f";

/*
    public static final String PUBLISH_KEY   = "pub-c-d5a619e8-3d04-4589-8bdb-4f903cd36948";
    public static final String SUBSCRIBE_KEY = "sub-c-e947244a-48ee-11e6-82fe-0619f8945a4f";*/

    /*pub-c-1706e931-e680-4759-838b-1e5476b4f355
     [04/08/16, 5:54:13 PM] rtancio: Subscribe Key:
      sub-c-81727c0c-3cff-11e6-82fe-0619f8945a4f*//*

    public static final String SENDER_ID = "681560273407";*/
    public static final String SENDER_ID = "870866257814";
    public static final String CHAT_PREFS    = "com.tupelo.SHARED_PREFS";
    public static final String CHAT_USERNAME = "com.tupelo.SHARED_PREFS.USERNAME";
    public static final String CHAT_ROOM     = "com.tupelo.CHAT_ROOM";

    public static final String JSON_GROUP = "groupMessage";
    public static final String JSON_DM    = "directMessage";
    public static final String JSON_USER  = "chatUser";
    public static final String JSON_MSG   = "chatMsg";
    public static final String JSON_TIME  = "chatTime";

    public static final String PROPERTY_REG_ID  = "PROPERTY_REG_ID";

    public static final String STATE_LOGIN = "loginTime";

    public static final String GCM_REG_ID    = "gcmRegId";
    //public static final String GCM_SENDER_ID = "709361095668"; // Get this from
    public static final String GCM_SENDER_ID = "10060648773";
    public static final String GCM_POKE_FROM = "gcmPokeFrom"; // Get this from
    public static final String GCM_CHAT_ROOM = "gcmChatRoom"; // Get this from
    public final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    //pubnub related chat constants ends
/*
   public static final String BASE_URL = "http://46.101.194.172/?q=services/json";

    public static final String API_KEY = "6f9ba3f49289ac7055a3941b97aa0587";

    public static final String DOMAIN = "46.101.194.172";

*/

    public static final String BASE_URL = "http://46.101.194.172/?q=services/json";

    public static final String API_KEY = "6f9ba3f49289ac7055a3941b97aa0587";

    public static final String DOMAIN = "46.101.194.172";



/*    public static final String BASE_URL = "http://mymolife.com/?q=services/json";

    public static final String API_KEY = "7715e1cced0d201554e1416967696bf1";

    public static final String DOMAIN = "mymolife.com";*/





 /*---------------MYMO CONSTANTS---------------
    public static final String BASE_URL = "http://mymolife.com/?q=services/json";

    public static final String API_KEY = "7715e1cced0d201554e1416967696bf1";

    public static final String DOMAIN = "mymolife.com";

    public static final String MYMO_TYPE = "101";*/

    public static final String KILL_ACTIVITY = "kill_activity";

    //public static final String MYMO_SERIAL = "mymo_serial";

     /*---------------S HEALTH CONSTANTS---------------*/

    public static final String SHEALTH_TAG = "S Health Tag";
    public static final String SHEALTH = "shealth";
    public static final String SHEALTH_STEP_DAILY_TREND = "com.samsung.shealth.step_daily_trend";

    /*---------------PADOMETER CONSTANTS---------------*/
    public static final int REQUEST_OAUTH = 124;

    public static final String PADOMETER = "padometer";

    public static final String IS_RECORDING_STARTED = "is_started";

    public static final String GOOGLE_FIT_MARKET_LINK = "market://details?id=com.google.android.apps.fitness";


    /*---------------FITBIT CONSTANTS---------------*/
    public static final String FITBIT_CLIENT_ID = "227VP3";

    public static final String FITBIT_lOGIN_URL = "https://www.fitbit.com/oauth2/authorize?response_type=token&client_id="+ FITBIT_CLIENT_ID +"&scope=activity%20nutrition%20heartrate%20location%20nutrition%20profile%20settings%20sleep%20social%20weight&expires_in=2592000";

    public static final String FITBIT_ACTIVITY_STEPS_URL = "https://api.fitbit.com/1/user/-/activities/tracker/steps/date/today/7d.json";

    /*---------------JAWBONE CONSTANTS---------------*/
    public static final String URI_SCHEME = "https";
    public static final String AUTHORITY = "jawbone.com";

    public static final String API_VERSION = "version";
    public static final String API_VERSION_STRING = "v.1.1";
    public static final String XID = "xid";

    public static final String AUTH_URI = "auth_uri";
    public static final String CLIENT_SECRET = "client_secret";
    public static final String API_URL = "https://jawbone.com";

    public static final String CLIENT_ID = "G1lEtOmqh_0";
    public static final String CLIENT_SECRET_KEY = "082f30388d53351dfa9c38a394d8b9efd1e83c75";
    public static final String OAUTH_CALLBACK_URL = "http://www.tupelolife.com";



    /*---------------PARSE DUMMY CONSTANTS---------------*/

    public static final String PARSE_APPLICATION_ID = "VIWbuC4NQP5CTgMYzW79f1UL4DYfmQA5qapYAXdw";
    public static final String PARSE_CLIENT_KEY = "bD8ZPX0sVLVkeSfckM747gMDDrXuSXPWVW4bX6I1";

    /*---------------PARSE CONSTANTS---------------

    public static final String PARSE_APPLICATION_ID = "wWemKpsDTZvogfkQa8GzAFbROO7xcbvRLy6cCEMp";
    public static final String PARSE_CLIENT_KEY = "7DF2E12lsR6ACh47FtdcLbggjfHd2OAneZJ0x4Yw";*/

    /*---------------APP CONSTANTS---------------*/
    public static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    public static final String MY_PREFERENCES = "Walking_challenge_Prefrences";

    public static final String CALLBACK = "callback";
    public static final String GET_AUTH_TOKEN = "getAuthToken";

    public static final String SWIPE_SHOWCASE = "swipe_showcase";
    public static final String CARD_SHOWCASE = "card_showcase";

    public static final String BITMAP = "bitmap";
    public static final String EULA_KEY = "eula_key";
    public static final String SERVICE = "service";
    public static final String IS_SIGNUP_COMPLETE = "is_signup_complete";

    public static final String TUPELO_MARKET_LINK = "market://details?id=com.tupelo";
    public static final String HELP_LINK = "http://www.tupelolife.com/wellnessapp-help";


    public static final String EULA = "<h1>End-User Licence Agreement</h1>\n" +
            "<p> </p>\n" +
            "<p><u><strong>USER AGREEMENT FOR USE OF AHA SESSIONS WALKING CHALLENGE APPLICATION</strong></u></p>\n" +
            "<p><strong>READ THIS AGREEMENT CAREFULLY BEFORE CLICKING &quot;I AGREE&quot; OR USING THIS APPLICATION. CLICKING &quot;I AGREE&quot;, MEANS YOU ACCEPT THESE TERMS, EVEN IF YOU HAVE NOT READ THEM. IF YOU DO NOT AGREE TO ALL OF THESE TERMS, CLICK &quot;I DO NOT ACCEPT,&quot; DO NOT USE THE AHA SESSIONS WALKING CHALLENGE APPLICATION, AND DELETE IT IF YOU HAVE ALREADY DOWNLOADED AHA SESSIONS WALKING CHALLENGE APPLICATION. THESE TERMS MAY CHANGE FROM TIME TO TIME, SO IT IS YOUR RESPONSIBILITY TO AGREE TO THE TERMS IN THIS VERSION, EVEN IF YOU AGREED TO PRIOR VERSIONS OF THIS AGREEMENT.</strong></p>\n" +
            "<P>THIS IS A TEMPORARY APPLICATION, FOR YOUR USE DURING THE SESSIONS WALKING CHALLENGE ONLY.  THE APP IS NOT AUTHORIZED OR INTENDED FOR USE BY ANY PERSON UNDER THE AGE OF 18 OR FOR USE OUTSIDE OF THE UNITED STATES.</p>\n" +
            "<p>Welcome to AHA Sessions Walking Challenge Application (the &quot;App&quot;).  The App is provided by The American Heart Association, a New York not for profit corporation (the &quot;AHA&quot;) and hosted by TupeloLife Services, LLC, a Texas limited liability corporation (&quot;TLS&quot;) (&quot;TLS,&quot; together with &quot;AHA&quot;, &quot;We&quot; or &quot;Us&quot; or &quot;Our&quot;).  This Agreement is between the AHA, TLS and you, or, if you are using the App on behalf of a company or other entity, AHA, TLS and such company or other entity (&quot;You&quot; or &quot;Your&quot;).  Please carefully read these terms.</p>\n" +
            "<p>1. <u><strong>Content.</strong></u> The App and the entire contents of the App, including, but not limited to, text, files, images, graphics, health sessions, health information, illustrations, audio, video, and photographs on or offered through the App (collectively, &quot;Content&quot;) are protected by intellectual property rights, including, as applicable and without limitation, copyrights, trademarks, patents, and other proprietary and intellectual property rights (&quot;Intellectual Property Rights&quot;).</p>\n" +
            "<p>2. <u><strong>Ownership.</strong></u> AHA Sessions Walking Challenge App Content and the logos, service marks, page design, images, written information, audio, video, animations, software functions and features provided through it, except those owned exclusively by TLS, (&quot;the Services&quot;) belong to the AHA. Except as stated in this Agreement, You are not granted a license or right, whether by implication, estoppel, or otherwise, in or to the App or Content, or to any Intellectual Property Rights in or related to them. You may not modify, reproduce, perform, display, create derivative works from, republish, post, transmit, participate in the transfer or sale of, distribute, or in any way exploit any portion of the App or Content. You agree that the Services are provided to you for the limited purpose of helping you manage personal information and that We retain all rights, title and interests, including copyright and other proprietary rights, in the Services and all material, including but not limited to text, images, and other multimedia data on the site unless expressly indicated as belonging to another.</p>\n" +
            "<p>3. <strong><u>Trademarks.</u></strong> Unless otherwise labeled, all trademarks, service marks, logos, photos, images, avatars, banners, page headers, and any other branding elements displayed on this App (collectively, the &quot;Marks&quot;) are the property of AHA, TLS or their Affiliates.  Except as expressly set forth in this Agreement, You may not display, link to, or otherwise use the Marks.  Additionally, AHA trademarks, including the following registered trademarks of AHA--American Heart Association, Learn and Live, and the Heart and Torch symbol, together with any revisions, updates or registered alternative marks&mdash;may not be used except with the express, prior written consent of AHA. </p>\n" +
            "<p>4. <strong><u>Privacy.</u></strong> AHA’s <a href=\"http://www.heart.org/HEARTORG/General/Privacy-Policy_UCM_300371_Article.jsp\">Privacy Policy</a>, available at this link, governs Use of the Services. The terms of AHA’s Privacy Policy are incorporated into this Agreement. You agree that We may use all information provided by You on this web site for the purpose for which You provide the information, such as to complete a transaction or to register You for a program or communication at Your request.  AHA is not required to secure any personal information You enter into the Services and You assume all risk of disclosure for any information entered, including unintended disclosure such as due to unauthorized access or monitoring of Your activities within the Services.  You are free to delete Your personal information from the Services and stop using the Services at any time.</p>\n" +
            "<p>Further, if You provide information to third parties on or through the App (including, to third party providers of products, software, services or content on the App), You acknowledge such third parties are not be bound by AHA’s Privacy Policy and that such third parties may or may not have restrictions on use of such information.  You acknowledge We are not responsible or liable for the use by any third party of Your information, including personal or confidential information.</p>\n" +
            "<p>5. <strong><u>De-Identification of Data.</u></strong>  We reserve the right to remove those elements of data that might be used to associate data with You or any other individual, or to &ldquo;de-identify&rdquo; the data We collect, and use and share such de-identified data for our business purposes.  Aggregated, de-identified data may also be used for health trend analysis, disease control, in assessing the effectiveness of various health programs and for other data analytics and business purposes. Depending on the circumstances, We may or may not charge third parties for this de-identified data. By using the App, you grant us the right to accept payment or other remuneration for such aggregated, de-identified data. We require parties with whom we share aggregated, de-identified data to agree that they will not try to make this information personally identifiable.</p>\n" +
            "<p>6. <strong><u>Restrictions on Use.</u></strong> You agree not to copy, license, sell, transfer, make available or otherwise distribute the Services to any entity or person without prior written authorization from Us. You agree to use Your best efforts to stop any unauthorized copying or distribution immediately after such unauthorized use becomes known to You. Without limiting its remedies for breach of this Agreement, We reserve the right to end any relationship with You if You violate these terms and may or may not issue refunds of any amounts paid to AHA for the Services or any other products or services.  We reserve the right at any time in Our sole discretion to modify, suspend, or discontinue the App (or any portion thereof) with or without notice. You warrant that all information provided to Us through the Services are accurate and that the person providing the information and making application is over the age of 18 years, legally able to enter this Agreement, authorized by the entity You represents to enter into this Agreement, and that you are permitted under the laws of Your local jurisdiction to do so.</p>\n" +
            "<p>We reserve the right to not post or to remove without prior notice any of Your content that, in our sole discretion, violates this Agreement. You hereby represent and warrant to Us and our Affiliates that You own all right, title and interest in and to any content that You provide or upload to the App, or that You have sufficient rights, whether by implication, estoppel, or otherwise, to grant Us the rights discussed in this Section. By providing or uploading any content to the App, You grant Us a nonexclusive, royalty-free, perpetual, irrevocable, and fully sublicensable right to use, copy, store, reproduce, modify, display, adapt, publish, translate, create derivative works from, distribute, and display such content throughout the world in any form, media, software, or technology of any kind. In addition, You waive all moral and economic rights in the content and warrant that all moral rights applicable to such content have been waived. You also grant AHA and TLS the right to use Your name in connection with the reproduction or distribution of such material. </p>\n" +
            "<p>7. <u><strong>Representations and Warranties.</strong></u> THE SERVICES ARE PROVIDED BY US ON AN &quot;AS IS&quot; BASIS WITH NO WARRANTIES OF ANY KIND. We make every effort to verify the information and functionality provided but makes no warranties regarding the completeness, accuracy, reliability or availability of the Services. We are not responsible for the availability, reliability, accuracy or information provided on any web sites or in any materials that may be linked to this App that are not controlled by Us.  Because You determine what information to enter into the Services, We are not responsible for the accuracy of user-created data or any functionality that works on the user-created data. We expressly disclaim all warranties, whether express or implied, regarding the App, including, without limitation, all warranties of title, noninfringement, merchantability, and fitness for a particular purpose.</p>\n" +
            "<p>You represent, warrant, and covenant for the benefit of AHA, TLS and each's Affiliates that: (a) You have the legal right and authority to enter into this Agreement, and, if You are accepting this Agreement on behalf of a company or other entity, to bind the company or other entity to the terms of this Agreement; (b) You have the legal right and authority to perform Your obligations under this Agreement and to grant the rights and licenses described in this Agreement and in any applicable additional agreement You enter into in connection with the App; and (c) all information You provide to Us in connection with this Agreement and Your access to the App is correct and current.  </p>\n" +
            "<p>8. <strong><u>Limitation of Liability.</u></strong> In no event is AHA or TLS liable to you or to any user of the Services or to any other person or entity for any direct, indirect, special, exemplary, or consequential damages, including lost profits.  This limitation is effective whether based on breach of warranty, contract, negligence, strict liability, lost opportunity, or otherwise, arising under this Agreement or any performance under this Agreement, whether or not You had any knowledge, actual or constructive, that such damages might be incurred. In the event You are dissatisfied with, or dispute these terms of this Agreement, the App or the Content, Your sole right and exclusive remedy is to terminate Your use of the App and Content, even if that right or remedy is deemed to fail of its essential purpose.  You confirm that We have no other obligation, liability or responsibility to You or any other party.</p>\n" +
            "<p>9. <u><strong>Indemnification.</strong></u> You will indemnify, defend and hold harmless Us and Our affiliates, officers, volunteers, employees, and agents (&ldquo;Affiliates&rdquo;), against any claim, damages, loss, liability, suits or expense arising out of the Your use of the Services.</p>\n" +
            "<p>10. <strong><u>Conflict with Other Agreements.</u></strong> This Agreement applies solely to the limited purposes for which the Services are provided. You may be required to agree to other terms in other documents for other purposes in order to do business with or receive products or services from AHA or TLS not provided in this particular Service. Nothing in this Agreement is intended to conflict with or supersede the provisions of any other contract with AHA or TLS.</p>\n" +
            "<p>11. <strong><u>Registration.</u></strong>  To access the App, You must become a registered user (a &ldquo;Registered User&rdquo;) of the App.  Your approval as a Registered User is at Our discretion.  Upon approval as a Registered User, You will be asked to create a password-protected account (an &ldquo;Account&rdquo;).  You agree to keep Your Account information and password confidential.  You may not share Your Account information or password with a third party.  You agree to notify Us immediately of any actual or suspected unauthorized use of Your Account.  You are solely responsible for all activities that occur through Your Account.  We will not be responsible for any loss to You caused by Your failure to comply with these obligations.  In connection with Your application to become a Registered User, You will be asked to submit certain information about yourself (&ldquo;Registration Information&rdquo;).  You represent and warrant that: (a) all Registration Information You provide is true, accurate, current, and complete; and (b) You will maintain and promptly update the Registration Information to keep it true, accurate, current, and complete.  As part of the registration process, You may be assigned or permitted to create a user ID for use in identifying Your Account (a &ldquo;User ID&rdquo;).  You may not: (i) select or use a User ID of another person with the intent to impersonate that person; (ii) use a User ID in which another person has rights without such person&rsquo;s authorization; or (iii) use a User ID that We deem offensive.  Failure to comply with the foregoing will constitute a breach of this Agreement, which may result in immediate termination of Your Account.  </p>\n" +
            "<p>12. <u><strong>Miscellaneous.</strong></u> This Agreement contains the entire agreement relating to the Services. No waiver or failure to enforce Our rights under this Agreement is a waiver or bar to enforcing any other of Our rights. This Agreement is governed by the laws of the State of Texas without regard to its conflicts of laws provisions. The parties specifically exclude from application to the Agreement the United Nations Convention on Contracts for the International Sale of Goods and the Uniform Computer Information Transactions Act. You hereby irrevocably waive the right to a trial by jury.  In any such dispute, the prevailing party will be entitled to recover its reasonable attorneys’ fees and expenses from the other party.  Regardless of any statute or law to the contrary but to the extent this limitation is permitted by law, any claim or cause of action arising out of or related to Your use of the App must be filed by You within one (1) year after such claim or cause of action arose or be forever barred. Your obligations under paragraphs 2-9 survive termination of this Agreement.</p>\n" +
            "<p>12.1 <u>Third-Party Beneficiaries.</u>  Affiliates are intended third-party beneficiaries under this Agreement with the right to enforce the provisions that directly concern Content to which they have rights.  </p>\n" +
            "<p>12.2 <u>Nonassignment.</u>  You may not assign or transfer any of Your rights under this Agreement, and any attempt to do so is null and void.  </p>\n" +
            "<p>12.3 <u>Integration.</u> This Agreement sets forth the entire understanding of the parties and supersedes any and all prior oral and written agreements or understandings between the parties regarding the subject matter of this Agreement.  This Agreement may not be modified except upon written changes made by Us.  The waiver by either party of a breach of any provision of this Agreement will not operate or be interpreted as a waiver of any other or subsequent breach.  </p>\n" +
            "<p>12.4 <u>Severability.</u> If for any reason any provision of this Agreement is held invalid or unenforceable in whole or in part in any jurisdiction, such provision will, as to such jurisdiction, be ineffective to the extent of such invalidity or unenforceability, without in any manner affecting the validity or enforceability thereof in any other jurisdiction or the remaining provisions hereof in any jurisdiction. Should any part of this Agreement be declared unlawful, void, or unenforceable, the remaining parts will remain in effect and be enforceable.</p>\n" +
            "<p>12.5 <u>Headings and References.</u>  All references in this Agreement to Sections, paragraphs, and other subdivisions refer to the Sections, paragraphs, and other subdivisions of this Agreement unless expressly provided otherwise.  Titles and headings appearing at the beginning of any subdivision are for convenience only and do not constitute any part of any such subdivision and will be disregarded in construing the language contained in this Agreement.  The word &ldquo;or&rdquo; is not exclusive.  Words in the singular form will be construed to include the plural and words in the plural form will be construed to include the singular, unless the context otherwise requires.  </p>\n" +
            "<p>12.6 <u>Modifications.</u>  AHA reserves the right, at any time and without notice, to add to, change, update, or modify the App and these Terms of Use, simply by posting such addition, change, update, or modification on the App.  Any such addition, change, update, or modification will be effective immediately upon posting on the App.</p>\n" +
            "<p>13. <u><strong>MEDICAL DISCLAIMER.</strong></u> YOU ACKNOWLEDGE THAT THE APP, INCLUDING ANY INFORMATION IN THE APP, IS PROVIDED &quot;AS IS&quot; FOR GENERAL INFORMATION ONLY AND IS NOT A MEDICAL DEVICE. NEITHER THE APP IS INTENDED TO BE OR IS TO BE CONSTRUED, AS MEDICAL ADVICE, DIAGNOSIS, TREATMENT, OR AS A SUBSTITUTE FOR CONSULTATIONS WITH QUALIFIED HEALTH PROFESSIONALS WHO ARE FAMILIAR WITH YOUR INDIVIDUAL MEDICAL NEEDS. YOU SHOULD ALWAYS CONTACT A HEALTH CARE PROVIDER TO OBTAIN A DIAGNOSIS, TO RECEIVE INFORMATION ABOUT POTENTIAL TREATMENTS, AND TO DISCUSS ANY QUESTIONS YOU MAY HAVE ABOUT YOUR HEALTH. <strong>DO NOT DELAY IN SEEKING MEDICAL ASSISTANCE IF YOU HAVE A MEDICAL EMERGENCY. THIS APP IS NOT INTENDED TO ANY MEDICAL SERVICES. IF YOU HAVE A MEDICAL EMERGENCY, CALL 911 OR YOUR HEALTH CARE PROVIDER.</strong></p>";



}
