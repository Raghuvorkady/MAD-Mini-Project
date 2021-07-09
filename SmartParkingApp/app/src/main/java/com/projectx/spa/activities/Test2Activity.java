package com.projectx.spa.activities;

/*

public class Test2Activity extends AppCompatActivity {
    ParkingSlot parkingSlot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView textView = findViewById(R.id.text);

        Intent intent = getIntent();
        // for reading single instance of ParkingSlot,
        // which is passed through Intent
        parkingSlot = (ParkingSlot) intent.getParcelableExtra(SAMPLE_KEY);

        textView.setText(parkingSlot.toString());
    }

    //    trying to deserialize TimeStamp object
    public void readTime(View view) {
        Date date = parkingSlot.getLastUpdatedTime().toDate();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss aa", Locale.US);
        makeToast("Date: " + dateFormat.format(date) + "\nTime: " + timeFormat.format(date));

        FBHelper fbHelper = new FBHelper(this);
        DocumentReference doc = fbHelper.toDocumentReference(parkingSlot.getAuthorizerDocument());
        Log.d("DOC", doc.getId());
        Log.d("DOC", doc.getPath());
        Log.d("DOC", doc.getParent().getPath());
        doc.update("name", "testUpdate");
    }

    private void makeToast(String toastMessage) {
        Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show();
    }
}*/
