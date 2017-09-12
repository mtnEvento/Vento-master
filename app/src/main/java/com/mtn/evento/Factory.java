package com.mtn.evento;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mtn.evento.activities.HomeScreenActivity;
import com.mtn.evento.data.Database;
import com.mtn.evento.data.Event;
import com.mtn.evento.data.ResultSet;
import java.util.ArrayList;
import static com.mtn.evento.data.Constants.LOGMESSAGE;

/**
 * Created by Summy on 8/13/2017.
 */

public class Factory {

    private static volatile boolean internet = false;
    private static volatile boolean isIn = false;
    private HomeScreenActivity manager;
    private InternetDataListenter internetDataListenter ;
    private LoginLogoutTaskListener loginLogoutTaskListener ;
    private UserLogInOrOutListenter userLogInOrOutListenter;
    private ReservedSeatsDataAvailableListener reservedSeatsDataAvailableListener ;
    private ReservedSeatsTask reservedSeatsTask;
    private NetworkTask internetTask;

    public Factory(HomeScreenActivity homeScreenActivity) {
        if(this.manager == null){
            this.manager = homeScreenActivity ;
            this.internetDataListenter = (InternetDataListenter) this.manager ;
            this.userLogInOrOutListenter = (UserLogInOrOutListenter) this.manager ;
        }

    }

    public static synchronized boolean isNetworkAndInternetAvailableNow() {
        return internet;
    }

    public void runLoginLogoutTask() {
        this.loginLogoutTaskListener = new LoginLogoutTaskListener(this.manager, this.userLogInOrOutListenter);
        this.loginLogoutTaskListener.execute();
    }

    public void runNetworkTask() {
        this.internetTask = new NetworkTask(this.manager, this.internetDataListenter);
        this.internetTask.execute();
    }

    public void stopNetworkTask() {
        if (this.internetTask != null) {
            this.internetTask.stopNetworkTask();
        }

    }

    public void stopLoginLogoutTask() {
        if (this.loginLogoutTaskListener != null) {
            this.loginLogoutTaskListener.stopLoginLogoutTask();
        }
    }

    public void runReservedSeatsTasksOnInternetAvailable(){
        if (reservedSeatsTask == null) {

            reservedSeatsTask = new ReservedSeatsTask();
            reservedSeatsTask.execute();
        }
    }

    public void cancelReservedSeatsTasksOnInternetUnAvailable(){
        if (reservedSeatsTask != null) {
            reservedSeatsTask.cancel(true);
            reservedSeatsTask = null;
        }
    }

    public HomeScreenActivity getManager() {
        return manager;

    }

    public void setManager(HomeScreenActivity manager) {
        this.manager = manager;
    }

    private  boolean isNetworkOn(){
        ConnectivityManager ConnectionManager=(ConnectivityManager) manager.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=ConnectionManager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()==true )
        {
            return true;
        }
        else
        {
            return  false;
        }
    }

    private  boolean isNetworkAndInternetAvailable(){

         internet = isNetworkOn()&& isInternetOn() ;
        return   internet ;
    }

    public  boolean isInternetOn() {

        // get Connectivity Manager object to check connection
        ConnectivityManager connec =
                (ConnectivityManager) manager.getSystemService(manager.getBaseContext().CONNECTIVITY_SERVICE);

        // Check for network connections
        if (connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED) {


            return true;

        } else if (
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                        connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED)
        {

            return false;
        }
        return false;
    }

    public interface EventsDataAvailableListener {
        public void onEventsDataAvailable(int count, ArrayList<Event> reservedResultSets);
    }

    public interface ReservedSeatsDataAvailableListener {
        public void onReservedSeatsDataAvailable(int count, ArrayList<ResultSet> reservedResultSets);
    }

    public interface InternetDataListenter {
        public void onInternetConnected();

        public void onInternetDisconnected();
    }

    public interface UserLogInOrOutListenter {
        public void onUserSignedIn();

        public void onUserSignedOut();
    }

    class NetworkTask extends AsyncTask<Void,Void,Void>{
        volatile boolean hasInternet= false ;
        HomeScreenActivity manager;
        volatile boolean stop = false;
        private InternetDataListenter internetDataListenter;

        public NetworkTask(HomeScreenActivity manager, InternetDataListenter internetDataListenter) {
            this.manager = manager ;
            this.internetDataListenter = internetDataListenter;
           // Log.d(LOGMESSAGE,"NetworkTask called :   ") ;
        }

        public void stopNetworkTask() {
            stop = true;
        }

        private void operate(){

          //  Log.d(LOGMESSAGE,"NetworkTask runnable started :   ") ;
            hasInternet = isNetworkAndInternetAvailable();
            if(hasInternet){
                internetDataListenter.onInternetConnected();
            }
            else
            {
                internetDataListenter.onInternetDisconnected();
            }
        }


        @Override
        protected Void doInBackground(Void... params) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {

                    while (true) {
                        try
                        {
                             while (true)
                             {
                                operate();
                                 Thread.sleep(1000);

                                 if (stop) {
                                     break;
                                 }
                             }


                        } catch (Exception e)
                        {
                            Log.d(LOGMESSAGE, "NetworkTask Error :   " + e.fillInStackTrace(), e.getCause());
                            while (true)
                            {

                                try
                                {
                                    operate();
                                    Thread.sleep(1000);
                                    if (stop) {
                                        break;
                                    }
                                } catch (InterruptedException e1) {
                                    e1.printStackTrace();
                                }
                                finally { }
                                if (stop) {
                                    break;
                                }
                            }
                        }
                        finally { }

                        if (stop) {
                            break;
                        }

                    }
                }
            };

            Thread  internetThread = new Thread(runnable);
            internetThread.start();
            return null;
        }
    }
    class ReservedSeatsTask extends AsyncTask<Void,Void,Void> {
        volatile int prevDataCount = 0 ;
        ReservedSeatsTask reservedSeatsTask;

        public ReservedSeatsTask() {
         //   Log.d(LOGMESSAGE, "ReservedSeatsTask called :  ");
        }
        @Override
        protected Void doInBackground(Void... params) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    try
                    {
                        while (true)
                        {

                            int reservedConut = ((Evento) manager.getApplication()).getDatabaseHandler().getEventsCount();

                            if(prevDataCount <= reservedConut ){


                                if( prevDataCount == reservedConut ){

                                }
                                else  if(prevDataCount < reservedConut )
                                {

                                    ArrayList<ResultSet> resultSets = ((Evento) manager.getApplication()).getDatabaseHandler().getAllreservedEvents();
                                    Factory.this.reservedSeatsDataAvailableListener.onReservedSeatsDataAvailable(reservedConut,resultSets);
                                    prevDataCount = reservedConut ;
                                }

                            }
                            Thread.sleep(1000);
                        }

                    }catch (Exception e)
                    {
                       e.printStackTrace();
                      //  Log.d(LOGMESSAGE,"ReservedSeatsTask Error :  "+e.fillInStackTrace(),e.getCause()) ;
                    }
                }
            };

            Thread  reservedSeatsThreadCount = new Thread();
            reservedSeatsThreadCount.start();

            return null;
        }

    }
    class LoginLogoutTaskListener extends AsyncTask<Void,Void,Void>{
        volatile boolean hasInternet= false ;
        volatile boolean stop = false;
        HomeScreenActivity manager;
        UserLogInOrOutListenter userLogInOrOutListenter ;
        private InternetDataListenter internetDataListenter;

        public LoginLogoutTaskListener(HomeScreenActivity manager, UserLogInOrOutListenter userLogInOrOutListenter) {
            this.manager = manager ;
            this.userLogInOrOutListenter = userLogInOrOutListenter ;
           // Log.d(LOGMESSAGE,"LoginLogoutTaskListener called :   ") ;
        }

        public void stopLoginLogoutTask() {
            this.stop = true;
        }
        private void operateUserLoginORLogout(){

          //  Log.d(LOGMESSAGE,"userLogInOrOutListenter runnable started :   ") ;
//             FirebaseAuth.getInstance().addAuthStateListener(new FirebaseAuth.AuthStateListener() {
//                @Override
//                public void onAuthStateChanged(FirebaseAuth firebaseAuth) {
//                    firebaseAuth.
//                }
//            });
            if( FirebaseAuth.getInstance().getCurrentUser() != null){
                this.userLogInOrOutListenter.onUserSignedIn();
            }
            else if( FirebaseAuth.getInstance().getCurrentUser() ==  null)
            {
                this.userLogInOrOutListenter.onUserSignedOut();
            }
        }


        @Override
        protected Void doInBackground(Void... params) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {

                    while (true) {
                        try
                        {
                            while (true)
                            {
                                if (stop) {
                                    break;
                                }
                                operateUserLoginORLogout();
                                Thread.sleep(500);
                            }


                        } catch (Exception e)
                        {
                          //  Log.d(LOGMESSAGE, "userLogInOrOutListenter :   " + e.fillInStackTrace(), e.getCause());
                            while (true)
                            {

                                try
                                {
                                    if (stop) {
                                        break;
                                    }
                                    operateUserLoginORLogout();
                                    Thread.sleep(500);
                                } catch (InterruptedException e1) {
                                    e1.printStackTrace();
                                }
                                finally { }
                                if (stop) {
                                    break;
                                }
                            }
                        }
                        finally { }
                        if (stop) {
                            break;
                        }
                    }
                }
            };

            Thread  loggedInOrOutThread = new Thread(runnable);
            loggedInOrOutThread.start();
            return null;
        }
    }
}
