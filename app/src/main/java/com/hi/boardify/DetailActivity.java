package com.hi.boardify;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Set;

public class DetailActivity extends AppCompatActivity {
    private static final String TAG = "TAG";
    private GalleryPagerAdapter mSectionsPagerAdapter;
    public ArrayList<ImageModel> data = new ArrayList<>();
    int pos;
    Toolbar toolbar;
    private ViewPager mViewPager;
    DataHolder dataHolder;
    DatabaseReference mRootDatabaseRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference databaseReference;
    FirebaseStorage storage;
    StorageReference storageReference;
    TextView professor;
    TextView time;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        //Necessary references to firebase and widgets
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        dataHolder = DataHolder.getInstance();
        databaseReference = mRootDatabaseRef.child(dataHolder.getUserID());
        Log.i("TAG",dataHolder.getUserID());
        professor = findViewById(R.id.professor);
        time = findViewById(R.id.time);

        //Allow the internal back button to function
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        data = getIntent().getParcelableArrayListExtra("data");
        pos = getIntent().getIntExtra("pos",0);

        setTitle(data.get(pos).getName());
        professor.setText(data.get(pos).getProf());
        time.setText(data.get(pos).getTime());
        id = data.get(pos).getId();

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new GalleryPagerAdapter(getSupportFragmentManager(), data);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setPageTransformer(true, new DepthPageTransformer());
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(pos);

        //Allow page scrolling in DetailActivity view
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }
            @Override
            public void onPageSelected(int i) {
                setTitle(data.get(i).getName());
            }
            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    //Checks the previous intent and prepare the necessary options in the toolbar menu
    //showing either "download" or "detele" depending on whether use is in "Saved Whiteboards" or viewing from "online classes"
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (getIntent().getStringExtra("Uniqid").equals("Boards")){
            menu.findItem(R.id.action_delete).setVisible(false);
            menu.findItem(R.id.action_download).setVisible(true);
        }else if (getIntent().getStringExtra("Uniqid").equals("DownloadBoards")){
            menu.findItem(R.id.action_download).setVisible(false);
            menu.findItem(R.id.action_delete).setVisible(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
            if (id == R.id.action_download){
                Log.i("LOGCAT", "THE ID IS" +data.get(pos).getId());
                databaseReference.child(data.get(pos).getId()).setValue(data.get(pos));
                Toast.makeText(this,"Whiteboard saved",Toast.LENGTH_LONG).show();
                return true;
            }
        if (id == R.id.action_delete){
            databaseReference.child(data.get(pos).getId()).removeValue();
            String storageUrl = "images/"+data.get(pos).getName(); //get the file path to be removed from Firebase Storage
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(storageUrl); //Set the filepath as the name
            storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() { //deletes the image on Firebase Storage

                @Override
                public void onSuccess(Void aVoid) {
                    // File deleted successfully
                    Log.d(TAG, "onSuccess: deleted file");
                    dataHolder.decuploadCount(); //if success we will decrement the uploadCount
                    Toast.makeText(DetailActivity.this,"Whiteboard deleted",Toast.LENGTH_LONG).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // If an error occurs we will log it for tracking purposes
                    Log.d(TAG, "onFailure: did not delete file");
                    Toast.makeText(DetailActivity.this,"Error occurred while deleting",Toast.LENGTH_LONG).show();
                }
            });
            //Returns to the RecyclerView whether or not the delete was successful
            Intent intent = new Intent(DetailActivity.this, MainActivity.class);
            intent.putExtra("delete",true);
            startActivity(intent);
            this.overridePendingTransition(0,R.anim.left_to_right);
        }
        return super.onOptionsItemSelected(item);
    }

}

