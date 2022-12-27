package com.example.chat.fragment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.TextView
import androidx.core.os.HandlerCompat.postDelayed
import com.bumptech.glide.Glide
import com.example.chat.R
import com.example.chat.databinding.VideoCallOutgoingBinding
import com.example.chat.model.User
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import de.hdodenhof.circleimageview.CircleImageView

class VideoCallOutgoing : AppCompatActivity() {
    private lateinit var username: TextView
    private lateinit var profileImage: CircleImageView

    private lateinit var declineVCOutgoing: FloatingActionButton

    private lateinit var binding: VideoCallOutgoingBinding

    private lateinit var userid: String
    private lateinit var receiverToken: String

    private lateinit var bundle: Bundle
    private lateinit var handler: Handler

    private lateinit var reference: DatabaseReference
    private lateinit var firebaseUser: FirebaseUser



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.video_call_outgoing)

        username = binding.nameVcOg
        profileImage = binding.profileImageVcOg
        declineVCOutgoing = binding.declineVcOg

        bundle = intent.extras!!
        userid = bundle.getString("uid").toString()

        reference = FirebaseDatabase.getInstance().getReference("Users").child(userid)

        reference.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                username.text = user!!.username
                if (user.imageURI.equals("default")) {
                    profileImage.setImageResource(R.drawable.ic_baseline_account_circle_white_24)
                } else {
                    Glide.with(applicationContext).load(user.imageURI).into(profileImage)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        sendCallInvitation()

    }

    private fun sendCallInvitation() {
        FirebaseDatabase.getInstance().getReference().child(userid).child("token").addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                receiverToken = snapshot.getValue(String::class.java).toString()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        handler.postDelayed(object: Runnable{
            override fun run() {
                //FcmNotificationsSender
            }

        }, 1000)
    }
}