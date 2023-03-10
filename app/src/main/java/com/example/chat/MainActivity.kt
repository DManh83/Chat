package com.example.chat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.chat.databinding.ActivityMainBinding
import com.example.chat.fragment.ChatsFragment
import com.example.chat.fragment.PeopleFragment
import com.example.chat.fragment.ProfileFragment
import com.example.chat.model.User
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import de.hdodenhof.circleimageview.CircleImageView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var profileImage: CircleImageView
    private lateinit var username: TextView

    private lateinit var firebaseUser: FirebaseUser
    private lateinit var reference: DatabaseReference

    //private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.bind(findViewById(R.id.main_layout))

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = ""

        profileImage = binding.profileImage
        username = binding.username

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.uid)

        reference.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user: User? = dataSnapshot.getValue(User::class.java)
                username.text = user?.username
                if (user?.imageURI.equals("default")) {
                    profileImage.setImageResource(R.drawable.ic_account_circle_black_36dp)
                } else {

                    Glide.with(applicationContext).load(user?.imageURI).into(profileImage)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })

        val tabLayout: TabLayout = binding.tabLayout
        val viewPager: ViewPager2 = binding.tabPager

        val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager, lifecycle)

        viewPagerAdapter.applyList(
            arrayListOf(ViewPagerAdapter.FragItem({ ChatsFragment() }, "Chats"),
                ViewPagerAdapter.FragItem({ PeopleFragment() }, "People"),
                ViewPagerAdapter.FragItem({ ProfileFragment() }, "Profile")))

        viewPager.adapter = viewPagerAdapter
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = viewPagerAdapter.getTitle(position)
        }.attach()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.sign_out -> {
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this, StartActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                return true
            }
        }
        return false
    }

    class ViewPagerAdapter(fm: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fm, lifecycle) {

        data class FragItem(val newInstance : () -> Fragment, val title: String)

        private var fragments = ArrayList<FragItem>()

        override fun getItemCount() = fragments.size

        override fun createFragment(position: Int): Fragment {
            return fragments[position].newInstance.invoke()
        }

        fun applyList(newFragments : ArrayList<FragItem>) {
            fragments = newFragments
        }

        fun getTitle(position: Int): String {
            return fragments[position].title
        }
    }

    private fun status(status: String) {
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.uid)
        val hashMap = HashMap<String, Any>()
        hashMap["status"] = status
        reference.updateChildren(hashMap)
    }

    override fun onResume() {
        super.onResume()
        status("online")
    }

    override fun onPause() {
        super.onPause()
        status("offline")
    }

}