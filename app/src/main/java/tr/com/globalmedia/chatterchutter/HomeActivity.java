package tr.com.globalmedia.chatterchutter;

/**
 * Created by emre on 22.12.2017.
 */

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.content.ActivityNotFoundException;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

  private ListView listView;
  private FirebaseAuth fAuth;
  private ArrayList<String> subjectLists = new ArrayList<>();
  private FirebaseDatabase db;
  private DatabaseReference dbRef;
  private ArrayAdapter<String> adapter;
  private ProgressDialog pDialog;

  @Override
  public void onBackPressed() {
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_home);

    pDialog = new ProgressDialog(HomeActivity.this);
    pDialog.setMessage("Yükleniyor...");
    pDialog.setIndeterminate(true);
    pDialog.setCancelable(false);
    pDialog.show();

    fAuth = FirebaseAuth.getInstance();

    listView = (ListView) findViewById(R.id.listViewSubjects);

    db = FirebaseDatabase.getInstance();
    dbRef = db.getReference("ChatSubjects");

    adapter = new ArrayAdapter<String>(
      HomeActivity.this,
      android.R.layout.simple_list_item_1,
      android.R.id.text1, subjectLists
    );
    listView.setAdapter(adapter);

    dbRef.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        subjectLists.clear();
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
          subjectLists.add(ds.getKey());
          createNotification("Yeni Mesaj Var");
        }
        adapter.notifyDataSetChanged();
        pDialog.dismiss();
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {
        Toast.makeText(
          getApplicationContext(),
          "" + databaseError.getMessage(),
          Toast.LENGTH_SHORT
        ).show();
      }

    });

    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
      Intent intent = new Intent(HomeActivity.this, ChatActivity.class);
      intent.putExtra("subject", subjectLists.get(position));
      startActivity(intent);

      }
    });
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.exit:
        fAuth.signOut();
        finish();
        break;
      case R.id.action_help:
        help();
        break;
      case R.id.fb_action:
        face();
        break;
      case R.id.share:
        share();
        break;
      case R.id.tw_action:
        twitter();
        break;
      case R.id.in_action:
        insta();
        break;
    }

    return super.onOptionsItemSelected(item);
  }

  public void help() {
    AlertDialog alertDialog = new AlertDialog.Builder(HomeActivity.this).create();
    alertDialog.setTitle("Chatter Chutter");
    alertDialog.setMessage(
      "Uygulama OMÜ Bilgisayar Mühendisliği Öğrencilerinin " +
      "kendi aralarında dersler hakkında haberleşmesini sağlamak" +
      " amacıyla yapılmıştır."
    );
    alertDialog.setCancelable(false);
    alertDialog.setButton(RESULT_OK, "Anladım", new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int which) {

      }
    });
    alertDialog.show();
  }

  public void face() {
    Uri uri = Uri.parse("fb://page/150211041711516");
    Intent fbInt = new Intent(Intent.ACTION_VIEW, uri);
    fbInt.setPackage("com.facebook.katana");

    try {
      startActivity(fbInt);
    } catch (ActivityNotFoundException e) {
      startActivity(
          new Intent(
              Intent.ACTION_VIEW,
              Uri.parse("https://www.facebook.com/omuoyak")
          )
      );
    }
  }

  public void insta() {
    Uri uri = Uri.parse("http://instagram.com/_u/omuoyak");
    Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);

    likeIng.setPackage("com.instagram.android");

    try {
      startActivity(likeIng);
    } catch (ActivityNotFoundException e) {
      startActivity(new Intent(Intent.ACTION_VIEW,
        Uri.parse("http://instagram.com/omuoyak")));
    }
  }

  public void twitter() {
    Uri uri = Uri.parse("twitter://user?screen_name=omuoyak");
    Intent twInt = new Intent(Intent.ACTION_VIEW, uri);
    twInt.setPackage("com.twitter.android");

    try {
      startActivity(twInt);
    } catch (ActivityNotFoundException e) {
      startActivity(new Intent(Intent.ACTION_VIEW,
        Uri.parse("http://twitter.com/omuoyak")));
    }
  }

  public void share() {
    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
    sharingIntent.setType("text/plain");
    String WebUrl = "https://play.google.com/store/apps/details?id=tr.com.globalmedia.chatterchutter";
    String WebBaslik = "OMÜ Bilgisayar Mühendisliği Öğrencileri olarak " +
      "dersler hakkında buradan chatter chutter konuşuyoruz! Sen de gel :).\n";
    String shareMesaj = WebBaslik + " " + WebUrl;
    sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Chatter Chutter");
    sharingIntent.putExtra(Intent.EXTRA_TEXT, shareMesaj);
    startActivity(
      Intent.createChooser(
        sharingIntent,
        "Uygulamayı paylaşarak arkadaşlarınızın katılmasını sağlayın."))
      ;
  }

  private void createNotification(String message) {
    String CHANNEL_ID = "my_channel_01";
    NotificationCompat.Builder mBuilder =
      new NotificationCompat.Builder(this, CHANNEL_ID)
        .setSmallIcon(R.drawable.facebook)
        .setContentTitle("Hey!")
        .setContentText(message);
    Intent resultIntent = new Intent(this, HomeActivity.class);

    TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
    stackBuilder.addParentStack(HomeActivity.class);
    stackBuilder.addNextIntent(resultIntent);
    PendingIntent resultPendingIntent =
      stackBuilder.getPendingIntent(
        0,
        PendingIntent.FLAG_UPDATE_CURRENT
      );
    mBuilder.setContentIntent(resultPendingIntent);
    NotificationManager mNotificationManager =
      (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    mNotificationManager.notify(1, mBuilder.build());

  }
}