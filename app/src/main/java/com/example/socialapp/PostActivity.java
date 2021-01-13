package com.example.socialapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class PostActivity extends AppCompatActivity {

    private ImageButton mSelectImage;
    private static final  int galleryReq = 1 ;

    private EditText mPostTitle,mPostDesc;
    private Button msubBtn;

    private Uri mImageUri;
    private StorageReference mstorage;
    private ProgressDialog mProgress;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mstorage = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Blog");


        mSelectImage = (ImageButton) findViewById(R.id.ImageButtonId);

        mPostTitle = (EditText) findViewById(R.id.EditTextTitleId);
        mPostDesc = (EditText) findViewById(R.id.EditTextDescriptionId);
        msubBtn = (Button)findViewById(R.id.submitBtnId);
        mProgress = new ProgressDialog(this);

        msubBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPosting();
            }
        });

       mSelectImage.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
               galleryIntent.setType("image/*");
               startActivityForResult(galleryIntent,galleryReq);


           }
       });
    }

    private void startPosting() {

        mProgress.setMessage("Blog is Posting...");
        mProgress.show();

        String title_val = mPostTitle.getText().toString().trim();
        String desc_val = mPostDesc.getText().toString().trim();

        if(!TextUtils.isEmpty(title_val) && !TextUtils.isEmpty(desc_val) && mImageUri!=null)
        {
            StorageReference filepath = mstorage.child("Blog_Images").child(mImageUri.getLastPathSegment());

            filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Task <Uri> downloadUrl = taskSnapshot.getMetadata().getReference().getDownloadUrl();

                    DatabaseReference newPost = mDatabase.push();

                    newPost.child("title").setValue(title_val);
                    newPost.child("desc").setValue(desc_val);
                    newPost.child("image").setValue(downloadUrl,toString());

                    mProgress.dismiss();

                    startActivity(new Intent(PostActivity.this,MainActivity.class));
                }
            });
        }

    }
    


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==galleryReq && resultCode==RESULT_OK)
        {
            mImageUri = data.getData();
            mSelectImage.setImageURI(mImageUri);

        }
    }
}