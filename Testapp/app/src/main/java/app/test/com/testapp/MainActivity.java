package app.test.com.testapp;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;





public class MainActivity extends AppCompatActivity {

    //Defining Variables
    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private List<Person> persons;
    private TextView emptyView;
    private RecyclerView rv;
    private ProgressDialog pd;
    private SwipeRefreshLayout swipeContainer;
    String API = "https://api.github.com";
    String owner="square",repo="retrofit";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initializing Toolbar and setting it as the actionbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Retrofit Contributors");
        setSupportActionBar(toolbar);

        // init Navigation view
         initNavview();

        // init objects
        init();

        // Load repo contributors from github
        loadfeed(owner,repo);

    }

    public void init()
    {

        // FAB declarations
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getpopupmessage();
            }
        });

        // Swipe view init
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                loadfeed(owner,repo);
                swipeContainer.setRefreshing(false);
            }
        });


        // Recycle and emptyview init
        rv=(RecyclerView)findViewById(R.id.rv);
        emptyView = (TextView) findViewById(R.id.empty_view);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);

        // progress dialog init
        pd = new ProgressDialog(MainActivity.this);
        pd.setMessage("Loading...");


    }

    public void initNavview()
    {
        //Initializing NavigationView
        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {


                //Checking if the item is in checked state or not, if not make it in checked state
                if(menuItem.isChecked()) menuItem.setChecked(false);
                else menuItem.setChecked(true);

                //Closing drawer on item click
                drawerLayout.closeDrawers();

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()){


                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.retrofit:
                        owner ="square";
                        repo="retrofit";
                        toolbar.setTitle(menuItem.getTitle());
                        loadfeed(owner,repo);
                        return true;

                    case R.id.picasso:
                        owner ="square";
                        repo="picasso";
                        loadfeed(owner,repo);
                        toolbar.setTitle(menuItem.getTitle());
                        return true;
                    case R.id.okhttp:
                        owner ="square";
                        repo="okhttp";
                        loadfeed(owner, repo);
                        toolbar.setTitle(menuItem.getTitle());
                        return true;


                    default:
                        Toast.makeText(getApplicationContext(),"Somethings Wrong",Toast.LENGTH_SHORT).show();
                        return true;

                }
            }
        });

        // Initializing Drawer Layout and ActionBarToggle
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.openDrawer, R.string.closeDrawer){

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank

                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessay or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();


    }

    public void getpopupmessage()
    {
        new android.app.AlertDialog.Builder(MainActivity.this)
                .setTitle("App info")
                .setMessage("This app is Github Repo Cotributors Client app")
                .setCancelable(false)
                .setPositiveButton("Ok", new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // whatever...
                    }
                }).create().show();

    }

    public void loadfeed(String owner, String repo)
    {


        pd.show();
        persons = new ArrayList<>();
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(API).build();                                        //create an adapter for retrofit with base url

        gitapi git = restAdapter.create(gitapi.class);                            //creating a service for adapter with our GET class

        //Now ,we need to call for response
        //Retrofit using gson for JSON-POJO conversion

        git.getFeed(owner, repo,new Callback<List<Contributors>>() {



            @Override
            public void success(List<Contributors> contributorses, Response response) {
                for(int i=0;i<contributorses.size();i++) {



                    persons.add(new Person(contributorses.get(i).get_login(), contributorses.get(i).get_avatar_url(),contributorses.get(i).get_html_url()));

                }
                initializeAdapter();


            }

            @Override
            public void failure(RetrofitError error) {
                 if(error.isNetworkError())
                {
                    Toast.makeText(getApplicationContext(),"No internet Connection",
                            Toast.LENGTH_LONG).show();

                }
                Log.e("error",error.getMessage());

                emptydata();
            }
        });


    }


    private void initializeAdapter(){
        RVAdapter adapter = new RVAdapter(persons,MainActivity.this);
        rv.setAdapter(adapter);

        emptydata();


    }

    public void emptydata()
    {

        if (persons.isEmpty()) {
            rv.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }
        else {
            rv.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }

        pd.dismiss();


    }


}




